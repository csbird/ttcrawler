package cn.bird.ttcrawler.model;

/**
 * 专场
 * @author Administrator
 *
 */
public class Activity {
	//专场id
	private int id;
	//名称
	private String name;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
