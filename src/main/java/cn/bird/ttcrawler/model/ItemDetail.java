package cn.bird.ttcrawler.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详细信息
 * @author Administrator
 *
 */
public class ItemDetail {
	//商品id
	private int id;
	//名称
	private String name;
	//价格
	private BigDecimal price;
	//图片列表
	private List<String> images;
	//颜色、尺码等信息
	private List<Object> products;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
	public List<Object> getProducts() {
		return products;
	}
	public void setProducts(List<Object> products) {
		this.products = products;
	}
	
	
}
