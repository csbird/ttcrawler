package cn.bird.ttcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.bird.ttcrawler.model.Activity;
import cn.bird.ttcrawler.model.ActivityItem;
import cn.bird.ttcrawler.model.Category;
import cn.bird.ttcrawler.model.ItemDetail;
import cn.bird.ttcrawler.service.QueryService;
import cn.bird.ttcrawler.util.ConfigUtil;

/**
 * 基于控制台交互形式的天天数据抓取
 * @author Administrator
 *
 */
public class Console {
	public static final Logger logger = LoggerFactory.getLogger(Console.class);
	
	public static void main(String[] args) throws Exception{
		if(!ConfigUtil.init("server.properties")){
			logger.error("fail to load configuration");
			return;
		}
		Scanner scanner = new Scanner(System.in);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String day = sdf.format(new Date());
		String dataDir = ConfigUtil.config.getString("tt.data_dir");
		
		try{
			Set<Integer> targetCategorySet = new HashSet<Integer>();
			Set<Integer> targetActivitySet = new HashSet<Integer>();
			
			QueryService queryService = new QueryService(ConfigUtil.config.getString("tt.cookie"), ConfigUtil.config.getString("tt.activity_url"),
					ConfigUtil.config.getString("tt.activity_items_url"), ConfigUtil.config.getString("tt.item_url"));
			List<Category> categoryList = queryService.getCategoryList();
			if(categoryList == null){
				//try again...
				categoryList = queryService.getCategoryList();
				if(categoryList == null){
					logger.error("启动初始化失败，按回车结束然后请重新启动程序");
					scanner.nextLine();
					return;
				}
			}
			
			//市场列表
			int catIndex = 0;
			for(Category category : categoryList){
				logger.info(catIndex + ":" + category.getName());
				catIndex++;
			}
			logger.info("请选择市场类型，输入对应的数字(一次只能选择一个)");
			
			//输入选择市场
			int inputIndex = -1;
			while(true){
				String input = scanner.nextLine();
				try{
					inputIndex = Integer.parseInt(input.trim());
					if(inputIndex >= 0 && inputIndex < categoryList.size()){
						targetCategorySet.add(categoryList.get(inputIndex).getId());
						break;
					}else{
						logger.info("请输入有效的数字：");
					}
				}catch(Exception e){
					logger.info("请输入有效的数字：");
				}
			}
			logger.info("正在抓取专场列表，请稍候......");
			Category category = categoryList.get(inputIndex);
			List<Activity> activityList = queryService.getActivityList(category.getId());
			if(activityList == null){
				//try again...
				activityList = queryService.getActivityList(category.getId());
				if(activityList == null){
					logger.error("抓取专场列表失败，按回车结束然后请重新启动程序");
					scanner.nextLine();
					return;
				}
			}
			logger.info("----------------------------------------------------");
			
			int activityIndex = 0;
			for(Activity activity : activityList){
				logger.info(activityIndex + ":" + activity.getName());
				activityIndex++;
			}
			
			logger.info("请选择专场，输入对应的数字，多个专场用英文格式的逗号隔开，如：1,2,8");
			int inputActivityIndex = -1;
			while(true){
				String input = scanner.nextLine();
				try{
					boolean inputFlag = true;
					String[] tempStrs = input.split(",");
					for(String s : tempStrs){
						inputActivityIndex = Integer.parseInt(s.trim());
						if(inputActivityIndex >= 0 && inputActivityIndex < activityList.size()){
							targetActivitySet.add(activityList.get(inputActivityIndex).getId());
						}else{
							logger.info("数字超出范围:" + s + ",请重新输入**所有**专场：");
							inputFlag = false;
							break;
						}
					}
					if(!inputFlag){
						targetActivitySet.clear();
					}else{
						break;
					}
					
				}catch(Exception e){
					logger.info("非法的数字格式，请重新输入**所有**专场：");
				}
			}
			
			for(Activity activity : activityList){
				if(!targetActivitySet.contains(activity.getId())){
					continue;
				}
				BufferedWriter itemWriter = null;
				try{
					String itemFile = dataDir + File.separator + day + "_" + activity.getId() + "_item.txt";
					itemWriter = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (itemFile,false),"UTF-8"));
					logger.info("开始抓取专场：" + activity.getName());
					int displayMode = 0;
					int pageIndex = 1;
					int itemNumber = 0;
					while(true){
						if(pageIndex == 1){
							displayMode = 0;
						}else{
							displayMode = 1;
						}
						
						//获取该专场的商品列表信息
						List<ActivityItem> itemList = queryService.getActivityItemList(activity, displayMode, pageIndex, 20);
						if(itemList == null){
							//try again...
							itemList = queryService.getActivityItemList(activity, displayMode, pageIndex, 20);
							if(itemList == null){
								logger.error("fail to getActivityItemList for activity:{},page:{}", activity.getId(), pageIndex);
								break;
							}
						}
						if(itemList.size() > 0){
							//遍历商品列表，获取每个商品的图片列表
							for(ActivityItem item : itemList){
								ItemDetail itemDetail = queryService.getItemDetail(item.getId(), activity.getId());
								if(itemDetail == null){
									itemDetail = queryService.getItemDetail(item.getId(), activity.getId());
								}
								if(itemDetail != null){
									itemWriter.write(String.format("%s\t%s\t%s\t%s\t%s\n", itemDetail.getId(), activity.getName(),
											itemDetail.getName(), itemDetail.getPrice().setScale(2).toPlainString(),
											JSON.toJSONString(itemDetail.getProducts())));
								}
								String imageDir = dataDir + File.separator + "image" + File.separator + day + "_" + activity.getId() + File.separator + itemDetail.getId();
								File dir = new File(imageDir);
								if(!dir.exists()){
									dir.mkdirs();
								}
								int imageNum = 1;
								for(String imageUrl : itemDetail.getImages()){
									int i = imageUrl.lastIndexOf(".");
									int j = imageUrl.lastIndexOf(":");
									if(i >= 0 && j >= 0){
										String postfix = imageUrl.substring(i+1);
										String urlPath = imageUrl.substring(j+1);
										String[] tmpArray = imageUrl.split(":");
										String fullImageUrl = "https://" + tmpArray[1] + ".b0.upaiyun.com" + tmpArray[2];
										String imageFilePath = imageDir + File.separator + imageNum + "." + postfix;
										//下载图片
										queryService.downloadPic(fullImageUrl, imageFilePath);
										imageNum++;
									}
								}
								
							}
							itemNumber += itemList.size();
							//最后一页，直接跳出
							if(itemList.size() < 20){
								break;
							}
							
							pageIndex++;
							try {
								Thread.sleep(1000 * 2);
							} catch (InterruptedException e) {
								logger.error("exception", e);
							}
						}else{
							break;
						}
					}
					if(itemNumber == 0){
						logger.info("该专场没有数据，请检查商品是否已上架或者专场是否有积分限制");
					}
					logger.info("完成抓取专场：" + activity.getName() + ",一共" + itemNumber + "商品");
				}catch(Exception e){
					logger.error("exception", e);
				}finally{
					if(itemWriter != null){
						itemWriter.close();
					}
				}
				
			}
			
			
		}finally{
			logger.info("所有专场抓取完成，按回车结束");
			scanner.nextLine();
			scanner.close();
		}
	}
}
