package cmu.voip.repository.call.vo;

import java.util.Date;

public class VideoHistoryDTO {
	public String id;
	public String fromuser;
	public String touser;
	public String fromip;
	public int fromport;
	public String toip;
	public int toport;
	public Date starttime;
	public Date endtime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFromuser() {
		return fromuser;
	}
	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getFromip() {
		return fromip;
	}
	public void setFromip(String fromip) {
		this.fromip = fromip;
	}
	public int getFromport() {
		return fromport;
	}
	public void setFromport(int fromport) {
		this.fromport = fromport;
	}
	public String getToip() {
		return toip;
	}
	public void setToip(String toip) {
		this.toip = toip;
	}
	public int getToport() {
		return toport;
	}
	public void setToport(int toport) {
		this.toport = toport;
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
	@Override
	public String toString() {
		return "VoiceHistoryVo [id=" + id + ", fromuser=" + fromuser + ", touser=" + touser + ", fromip=" + fromip
				+ ", fromport=" + fromport + ", toip=" + toip + ", toport=" + toport + ", starttime=" + starttime
				+ ", endtime=" + endtime + "]";
	}
}
