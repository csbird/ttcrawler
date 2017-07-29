package cn.bird.ttcrawler.model;

import java.math.BigDecimal;

/**
 * 专场商品的概要信息
 * @author Administrator
 *
 */
public class ActivityItem {
	//商品id
	private long id;
	//商品标题
	private String title;
	//封面
	private String cover;
	//价格
	private BigDecimal price;
	//当前销量
	private int dealCount;
	//历史累计总销量
	private int totalQty;
	private int shopId;
	//成团件数数
	private int chengTuanCount;
	private int displayStatuId;
	private String displayStatu;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public int getDealCount() {
		return dealCount;
	}
	public void setDealCount(int dealCount) {
		this.dealCount = dealCount;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public int getShopId() {
		return shopId;
	}
	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public int getChengTuanCount() {
		return chengTuanCount;
	}
	public void setChengTuanCount(int chengTuanCount) {
		this.chengTuanCount = chengTuanCount;
	}
	public int getDisplayStatuId() {
		return displayStatuId;
	}
	public void setDisplayStatuId(int displayStatuId) {
		this.displayStatuId = displayStatuId;
	}
	public String getDisplayStatu() {
		return displayStatu;
	}
	public void setDisplayStatu(String displayStatu) {
		this.displayStatu = displayStatu;
	}
	
}
