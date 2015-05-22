package com.seta.android.db;

import org.jivesoftware.smack.packet.Presence;

public class SystemMessagePacketBean {

	// ���������������еĸ���ϵͳ��Ϣ
	public static final int SystemMessageType = 100;
	public static final int IQType = 10;
	// �����յ��ĺ�����ص���Ϣ

	// .Type.u.available
	public static final int PresenceType = 20;

	// PresenceRequestType:
	// --��ʾ�����������������Ϊ���ѵ�Presence
	public static final int PresenceRequestType = 21;
	// PresenceResponseType
	// --��ʾ������Ӧ�������������Ϊ���ѵ�Presence
	public static final int PresenceResponseType = 22;

	// ����������ʾ���Է������Ƿ�����
	public static final int PresenceAvailable = 23;
	public static final int PresenceUnavailable = 24;

	public static final int RosterType = 30;

	public int MessageType;
	public Object SystemMessage;
	public String SystemMessageInfo;
	public int SystemMessageImageId;

}
