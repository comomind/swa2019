package cmu.voip.repository.call.vo;

import java.util.Date;

public class VoiceHistoryDTO {
	public long id;
	public String caller;
	public String callee;
	public int status;
	public Date created;
	public Date closed;
	public int calleraport;
	public int calleeaport;
	public int callervport;
	public int calleevport;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getCallee() {
		return callee;
	}
	public void setCallee(String callee) {
		this.callee = callee;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getClosed() {
		return closed;
	}
	public void setClosed(Date closed) {
		this.closed = closed;
	}
	public int getCalleraport() {
		return calleraport;
	}
	public void setCalleraport(int calleraport) {
		this.calleraport = calleraport;
	}
	public int getCalleeaport() {
		return calleeaport;
	}
	public void setCalleeaport(int calleeaport) {
		this.calleeaport = calleeaport;
	}
	public int getCallervport() {
		return callervport;
	}
	public void setCallervport(int callervport) {
		this.callervport = callervport;
	}
	public int getCalleevport() {
		return calleevport;
	}
	public void setCalleevport(int calleevport) {
		this.calleevport = calleevport;
	}
	@Override
	public String toString() {
		return "VoiceHistoryDTO [id=" + id + ", caller=" + caller + ", callee=" + callee + ", status=" + status
				+ ", created=" + created + ", closed=" + closed + ", calleraport=" + calleraport + ", calleeaport="
				+ calleeaport + ", callervport=" + callervport + ", calleevport=" + calleevport + "]";
	}
	
	
}
