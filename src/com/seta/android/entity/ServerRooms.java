package com.seta.android.entity;

public class ServerRooms {
	private String name;
	private String jid;
	private int    Occupants;
	private String description;
	private String subject;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public int getOccupants() {
		return Occupants;
	}
	public void setOccupants(int occupantsCount) {
		this.Occupants = occupantsCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Override
	public String toString() {
		return "FriendRooms [name=" + name + ", jid=" + jid
				+ ", occupantsCount=" + Occupants + ", description="
				+ description + ", subject=" + subject + "]";
	}
	

}
