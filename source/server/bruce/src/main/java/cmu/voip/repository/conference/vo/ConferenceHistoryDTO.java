package cmu.voip.repository.conference.vo;

import java.util.Date;

public class ConferenceHistoryDTO {
	public String id;
	public String creater;
	public Date starttime;
	public Date endtime;
	public int attdcount;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public int getAttdcount() {
		return attdcount;
	}
	public void setAttdcount(int attdcount) {
		this.attdcount = attdcount;
	}
	@Override
	public String toString() {
		return "ConferenceHistoryVO [id=" + id + ", creater=" + creater + ", starttime=" + starttime + ", endtime="
				+ endtime + ", attdcount=" + attdcount + "]";
	}
}
