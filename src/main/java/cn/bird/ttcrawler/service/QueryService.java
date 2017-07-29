package cn.bird.ttcrawler.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.bird.ttcrawler.model.Activity;
import cn.bird.ttcrawler.model.ActivityItem;
import cn.bird.ttcrawler.model.Category;
import cn.bird.ttcrawler.model.ItemDetail;
import cn.bird.ttcrawler.util.HttpTool;

public class QueryService {
	public static final Logger logger = LoggerFactory.getLogger(QueryService.class);
	//登录session cookie
	private String cookie;
	//查询专场列表的http url
	private String activityListUrl;
	//查询某个专场商品列表的http url
	private String activityItemListUrl;
	//查询某个商品详细信息的http url
	private String itemUrl;
	
	public QueryService(String cookie, String activityListUrl, String activityItemListUrl, String itemUrl) {
		super();
		this.cookie = cookie;
		this.activityListUrl = activityListUrl;
		this.activityItemListUrl = activityItemListUrl;
		this.itemUrl = itemUrl;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getActivityListUrl() {
		return activityListUrl;
	}

	public void setActivityListUrl(String activityListUrl) {
		this.activityListUrl = activityListUrl;
	}

	public String getActivityItemListUrl() {
		return activityItemListUrl;
	}

	public void setActivityItemListUrl(String activityItemListUrl) {
		this.activityItemListUrl = activityItemListUrl;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	
	/**
	 * 获取市场类别列表
	 * @return
	 */
	public List<Category> getCategoryList(){
		List<Category> resultList = new ArrayList<Category>();
		String url = activityListUrl;
		try {
			String[] response = HttpTool.get(url, cookie, "UTF-8", 5000, 5000);
			if(response != null){
				if(response[0].equals("200")){
					JSONObject json = JSON.parseObject(response[1]);
					if(json != null){
						Boolean result = json.getBoolean("Result");
						if(result != null){
							if(result){
								JSONObject data = json.getJSONObject("Data");
								if(data != null){
									JSONArray array = data.getJSONArray("CategoryList");
									if(array != null){
										Iterator<Object> it = array.iterator();
										while(it.hasNext()){
											JSONObject item = (JSONObject)it.next();
											Category category = new Category();
											category.setId(item.getIntValue("Cid"));
											category.setName(item.getString("Name"));
											resultList.add(category);
										}
										return resultList;
									}
								}
							}else{
								logger.error("fail to query:{}", response[1]);
							}
						}else{
							logger.error("invalid response json, no result:{}", response[1]);
						}
					}
				}else{
					logger.error("error response code={}", response[0]);
				}
			}
		} catch (Exception e) {
			logger.error("exception while getCategoryList", e);
		}
		return null;
	}

	/**
	 * 获取某个市场类别下的所有专场列表
	 * @param cid 市场类别Category id
	 * @return
	 */
	public List<Activity> getActivityList(int cid){
		List<Activity> resultList = new ArrayList<Activity>();
		String url = activityListUrl + "?cid=" + cid;
		try {
			String[] response = HttpTool.get(url, cookie, "UTF-8", 5000, 5000);
			if(response != null){
				if(response[0].equals("200")){
					JSONObject json = JSON.parseObject(response[1]);
					if(json != null){
						Boolean result = json.getBoolean("Result");
						if(result != null){
							if(result){
								JSONObject data = json.getJSONObject("Data");
								if(data != null){
									JSONArray array = data.getJSONArray("ActivityList");
									if(array != null){
										Iterator<Object> it = array.iterator();
										while(it.hasNext()){
											JSONObject item = (JSONObject)it.next();
											Activity activity = new Activity();
											activity.setId(item.getIntValue("QsID"));
											activity.setName(item.getString("Name"));
											activity.setStartTime(item.getString("StartTime"));
											activity.setEndTime(item.getString("ToTime"));
											resultList.add(activity);
										}
										return resultList;
									}
								}
							}else{
								logger.error("fail to query:{}", response[1]);
							}
						}else{
							logger.error("invalid response json, no result:{}", response[1]);
						}
					}
				}else{
					logger.error("error response code={}", response[0]);
				}
			}
		} catch (Exception e) {
			logger.error("exception while getActivityList", e);
		}
		return null;
	}
	
	/**
	 * 获取某个专场下的某一页商品列表
	 * @param activity 专场
	 * @param displayMode 0-第一页，1-其他页
	 * @param pageIndex 页数
	 * @param pageSize 每页大小
	 * @return
	 */
	public List<ActivityItem> getActivityItemList(Activity activity, int displayMode, int pageIndex, int pageSize){
		List<ActivityItem> resultList = new ArrayList<ActivityItem>();
		String url = String.format("%s?displayMode=%s&pageIndex=%s&pageSize=%s&qsid=%s", activityItemListUrl, displayMode, pageIndex, pageSize, activity.getId());
		try {
			String[] response = HttpTool.get(url, cookie, "UTF-8", 5000, 5000);
			if(response != null){
				if(response[0].equals("200")){
					JSONObject json = JSON.parseObject(response[1]);
					if(json != null){
						Boolean result = json.getBoolean("Result");
						if(result != null){
							if(result){
								JSONObject data = json.getJSONObject("Data");
								if(data != null){
									JSONArray array = data.getJSONArray("NewItems");
									if(array != null){
										Iterator<Object> it = array.iterator();
										while(it.hasNext()){
											JSONObject item = (JSONObject)it.next();
											ActivityItem activityItem = new ActivityItem();
											activityItem.setId(item.getLongValue("ID"));
											activityItem.setTitle(item.getString("Title"));
											activityItem.setCover(item.getString("Cover"));
											activityItem.setPrice(item.getBigDecimal("Price"));
											activityItem.setDealCount(item.getIntValue("DealCount"));
											activityItem.setTotalQty(item.getIntValue("TotalQty"));
											activityItem.setShopId(item.getIntValue("ShopId"));
											activityItem.setChengTuanCount(item.getIntValue("ChengTuanCount"));
											activityItem.setDisplayStatuId(item.getIntValue("DisplayStatuId"));
											activityItem.setDisplayStatu(item.getString("DisplayStatu"));
											resultList.add(activityItem);
										}
										return resultList;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("exception while getActivityItemList", e);
		}
		return null;
	}
	
	/**
	 * 获取某个商品的详细信息
	 * @param id 商品id
	 * @param qsid 专场Activity id
	 * @return
	 */
	public ItemDetail getItemDetail(long id, int qsid){
		ItemDetail itemDetail = new ItemDetail();
		String url = String.format("%s?id=%s&qsid=%s", itemUrl, id, qsid);
		try {
			String[] response = HttpTool.get(url, cookie, "UTF-8", 5000, 5000);
			if(response != null){
				if(response[0].equals("200")){
					JSONObject json = JSON.parseObject(response[1]);
					if(json != null){
						Boolean result = json.getBoolean("Result");
						if(result != null){
							if(result){
								JSONObject data = json.getJSONObject("Data");
								if(data != null){
									itemDetail.setId(data.getIntValue("ItemID"));
									itemDetail.setName(data.getString("Name"));
									itemDetail.setPrice(data.getBigDecimal("Price"));
									itemDetail.setImages(data.getJSONArray("Images").toJavaList(String.class));
									JSONArray products = data.getJSONArray("Products");
									if(products != null){
										List<Object> productList = new ArrayList<Object>();
										for(int i = 0; i < products.size(); i++){
											JSONObject jo = products.getJSONObject(i);
											productList.add(jo);
										}
										itemDetail.setProducts(productList);
									}
									return itemDetail;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("exception while getItemDetail", e);
		}
		return null;
	}
	
	/**
	 * 下载某张商品图片
	 * @param imageUrl 图片地址
	 * @param imagePath 图片在本地保存的路径
	 */
	public void downloadPic(String imageUrl, String imagePath){
		try{
			URL url = new URL(imageUrl);
			DataInputStream dis = new DataInputStream(url.openStream());
			FileOutputStream fos = new FileOutputStream(new File(imagePath));
			byte[] buffer = new byte[1024];
			int length = 0;
			while((length = dis.read(buffer)) > 0){
				fos.write(buffer, 0, length);
			}
			dis.close();
			fos.close();
		}catch(Exception e){
			logger.error("exception while download picture");
		}
	}
}
