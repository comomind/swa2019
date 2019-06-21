package cmu.voip.repository.conference.vo;

public class ConferenceNetworkDTO {
	public String id;
	public String joiner;
	public int voiceport;
	public int videoport;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJoiner() {
		return joiner;
	}
	public void setJoiner(String joiner) {
		this.joiner = joiner;
	}
	public int getVoiceport() {
		return voiceport;
	}
	public void setVoiceport(int voiceport) {
		this.voiceport = voiceport;
	}
	public int getVideoport() {
		return videoport;
	}
	public void setVideoport(int videoport) {
		this.videoport = videoport;
	}
	@Override
	public String toString() {
		return "ConferenceNetworkVO [id=" + id + ", joiner=" + joiner + ", voiceport=" + voiceport + ", videoport="
				+ videoport + "]";
	}
	
	
}
