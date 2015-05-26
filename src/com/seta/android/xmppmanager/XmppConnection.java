package com.seta.android.xmppmanager;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import com.sys.android.util.OpenfileFunction;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
/**
 * xmpp配置页面
 * @author anshe
 * @date 2013-04-27
 */
public class XmppConnection {
	
	//public static String SERVER_HOST = "192.168.1.99";//你openfire服务器所在的ip
	//public static  String SERVER_NAME = "aa";//设置openfire时的服务器名
	public static int    SERVER_PORT = 5222;//服务端口 可以在openfire上设置
	public static  String SERVER_HOST = "172.18.19.7";//你openfire服务器所在的ip
	public static  String SERVER_NAME = "seta";//设置openfire时的服务器名
    private static XMPPConnection connection = null;
	private static SharedPreferences rememberPassword;
    
	public static XMPPConnection openConnection() {
		try {
			if (null == connection || !connection.isAuthenticated()) {
				XMPPConnection.DEBUG_ENABLED = true;//开启DEBUG模式
				InetAddress addr = InetAddress.getLocalHost();
				//SERVER_HOST=addr.getHostAddress().toString();//获得本机IP
				//配置连接
				ConnectionConfiguration config = new ConnectionConfiguration(
						SERVER_HOST, SERVER_PORT,
						SERVER_NAME);
				config.setReconnectionAllowed(true);
				config.setSendPresence(true);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);       
				config.setSASLAuthenticationEnabled(false);
				OpenfileFunction.makeFilePath(Environment.getExternalStorageDirectory().getPath()+"/seta/security/", "cacerts.bks");
				//delete by anshe 2015.5.26
				/*File file =new File(Environment.getExternalStorageDirectory().getPath()+"/seta/security/");
				if(!file.exists()){
					file.mkdirs();
				}*/
				//delete by anshe 2015.5.26
				config.setTruststorePath(Environment.getExternalStorageDirectory().getPath()+"/seta/security/cacerts.bks");       
				//config.setTruststorePassword("123456");       
				config.setTruststoreType("bks"); 
				config.setSASLAuthenticationEnabled(true);
				 
				connection = new XMPPConnection(config);
				connection.connect();//连接到服务器
				//配置各种Provider，如果不配置，则会无法解析数据
				configureConnection(ProviderManager.getInstance());
			}
		} catch (XMPPException xe) {
			xe.printStackTrace();
			return null;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return connection;
	}

	/**
	 * 创建连接
	 * @param activity TODO
	 */	
	public static XMPPConnection getConnection(Activity activity) {
		if (connection == null&&activity!=null) {
			connection=openConnection();
			rememberPassword = activity.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
			String accounts=rememberPassword.getString("USER_NAME", "admin");
			String password=rememberPassword.getString("PASSWORD", "admin");
			if (accounts != null && password != null){
				try {
					connection.login(accounts, password);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				// 连接服务器成功，更改在线状态
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection(activity).sendPacket(presence);
			}
		}else{
			if(connection == null&&activity==null){
				connection=openConnection();
			}
		}
		return connection;
	}
	/** 
     * 更改用户状态 
     */  
    public void setPresence(Activity activity,int code) {  
        XMPPConnection con = getConnection(activity);  
        if (con == null)  
            return;  
        Presence presence;  
        switch (code) {  
        case 0:  
            presence = new Presence(Presence.Type.available);  
            con.sendPacket(presence);  
            Log.v("state", "设置在线");  
            break;  
        case 1:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.chat);  
            con.sendPacket(presence);  
            Log.v("state", "设置Q我吧");  
            break;  
        case 2:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.dnd);  
            con.sendPacket(presence);  
            Log.v("state", "设置忙碌");  
            break;  
        case 3:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.away);  
            con.sendPacket(presence);  
            Log.v("state", "设置离开");  
            break;  
        case 4:  
            Roster roster = con.getRoster();  
            Collection<RosterEntry> entries = roster.getEntries();  
            for (RosterEntry entry : entries) {  
                presence = new Presence(Presence.Type.unavailable);  
                presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
                presence.setFrom(con.getUser());  
                presence.setTo(entry.getUser());  
                con.sendPacket(presence);  
                Log.v("state", presence.toXML());  
            }  
            // 向同一用户的其他客户端发送隐身状态  
            presence = new Presence(Presence.Type.unavailable);  
            presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
            presence.setFrom(con.getUser());  
            presence.setTo(StringUtils.parseBareAddress(con.getUser()));  
            con.sendPacket(presence);  
            Log.v("state", "设置隐身");  
            break;  
        case 5:  
            presence = new Presence(Presence.Type.unavailable);  
            con.sendPacket(presence);  
            Log.v("state", "设置离线");  
            break;  
        default:  
            break;  
        }  
    }  
  
	/**
	 * 关闭连接
	 */	
	public static void closeConnection() {
		if(connection!=null){
			connection.disconnect();
		}
		connection = null;
	}
	
	/**
	 * xmpp配置
	 */
	private static void configureConnection(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",new XHTMLExtensionProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",new GroupChatInvitation.Provider());
		// Service Discovery # Items //解析房间列表
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",new DiscoverItemsProvider());
		// Service Discovery # Info //某一个房间的信息
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",Class.forName("org.jivesoftware.smackx.packet.Version"));
			
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline","http://jabber.org/protocol/offline",new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup",new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses","http://jabber.org/protocol/address",new MultipleAddressesProvider());
		pm.addIQProvider("si", "http://jabber.org/protocol/si",new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",new BytestreamsProvider());
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.SessionExpiredError());
	}

    //start add by anshe 2015.5.23
    /*
     * 用于监控断线
     * 15秒检测一次
     * */
    public static class  reConnnectionListener extends Thread {
		
            public void run() {
                while(XmppConnection.getConnection(null).isConnected()){
                    try {
                        sleep(15*1000);
                 
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };	        
    }
    //end add by anshe 2015.5.23

	public static ConnectionListener connectionListener = new ConnectionListener() {  
		  
        @Override  
        public void reconnectionSuccessful() {  
            Log.i("connection", "来自连接监听,conn重连成功");  
        }  
  
        @Override  
        public void reconnectionFailed(Exception arg0) {  
            Log.i("connection", "来自连接监听,conn失败："+arg0.getMessage());  
        }  
  
        @Override  
        public void reconnectingIn(int arg0) {  
            Log.i("connection", "来自连接监听,conn重连中..."+arg0);  
        }  
  
        @Override  
        public void connectionClosedOnError(Exception arg0) { 
        	 //这里就是网络不正常或者被挤掉断线激发的事件
            if(arg0.getMessage().contains("conflict")){ //被挤掉线
/*              log.e("来自连接监听,conn非正常关闭");
                log.e("非正常关闭异常:"+arg0.getMessage());
                log.e(con.isConnected());*/
            //关闭连接，由于是被人挤下线，可能是用户自己，所以关闭连接，让用户重新登录是一个比较好的选择                           
            XmppConnection.closeConnection();
            //接下来你可以通过发送一个广播，提示用户被挤下线，重连很简单，就是重新登录
            }else if(arg0.getMessage().contains("Connection timed out")){//连接超时
                // 不做任何操作，会实现自动重连
            }
            Log.i("connection", "connectionClosedOnError");  
              
        }
        
		@Override
		public void connectionClosed() {
			// TODO Auto-generated method stub
            Log.e("connection","来自连接监听,conn正常关闭");
			
		} 
	};
	}
