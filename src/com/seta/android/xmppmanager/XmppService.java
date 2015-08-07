package com.seta.android.xmppmanager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.IBinder;

/**
 * xmpp方法
 * @author anshe
 * @date 2015-04-27
 */
public class XmppService  extends Service{

	    private OfflineMessageManager offlineManager;
	    public static XMPPConnection con = null;

	    static{   
	        try{  
	           Class.forName("org.jivesoftware.smack.ReconnectionManager");  
	        }catch(Exception e){  
	            e.printStackTrace();  
	        }  
	    } 
	/** 
     * 删除当前用户 
     * @param connection 
     * @return 
     */  
    public static boolean deleteAccount(XMPPConnection connection)  
    {  
        try {  
            /*connection.getAccountManager().deleteAccount();  */
        	connection.disconnect();
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    }  
	/**
	 * 返回所有组信息 <RosterGroup>
	 * @return List(RosterGroup)
	 */
	public static List<RosterGroup> getGroups(Roster roster) {
		List<RosterGroup> groupsList = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = roster.getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext())
			groupsList.add(i.next());
		return groupsList;
	}

	/**
	 * 返回相应(groupName)组里的所有用户<RosterEntry>
	 * @return List(RosterEntry)
	 */
	public static List<RosterEntry> getEntriesByGroup(Roster roster,
			String groupName) {
		List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = roster.getGroup(groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext())
			EntriesList.add(i.next());
		return EntriesList;
	}

	/**
	 * 返回所有用户信息 <RosterEntry>
	 * @return List(RosterEntry)
	 */
	public static List<RosterEntry> getAllEntries(Roster roster) {
		List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = roster.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext())
			EntriesList.add(i.next());
		return EntriesList;
	}
	
	
	/** 
     * 创建一个组 
     */ 
	public static boolean addGroup(Roster roster,String groupName)  
    {  
        try {  
            roster.createGroup(groupName);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 删除一个组 
     */  
    public static boolean removeGroup(Roster roster,String groupName)  
    {  
        return false;  
    }
    
    /**
	 * 添加一个好友  无分组
	 */
	public static boolean addUser(Roster roster,String userName,String name)
	{
		try {
			roster.createEntry(userName, name, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	/**
	 * 添加一个好友到分组
	 * @param roster
	 * @param userName
	 * @param name
	 * @return
	 */
	public static boolean addUsers(Roster roster,String userName,String name,String groupName)
	{
		try {
			roster.createEntry(userName, name,new String[]{ groupName});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * 删除一个好友
	 * @param roster
	 * @param userJid
	 * @return
	 */
	public static boolean removeUser(Roster roster,String userJid)
	{
		try {
			RosterEntry entry = roster.getEntry(userJid);
			roster.removeEntry(entry);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
     * 把一个好友添加到一个组中
     * @param userJid
     * @param groupName
     */
    public static void addUserToGroup(final String userJid, final String groupName,
            final XMPPConnection connection) {
            	RosterGroup group = connection.getRoster().getGroup(groupName);
                // 这个组已经存在就添加到这个组，不存在创建一个组
                RosterEntry entry = connection.getRoster().getEntry(userJid);
                try {
                    if (group != null) {
                        if (entry != null)
                            group.addEntry(entry);
                    } else {
                        RosterGroup newGroup = connection.getRoster().createGroup("我的好友");
                        if (entry != null)
                            newGroup.addEntry(entry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    /**
     * 把一个好友从组中删除
     * @param userJid
     * @param groupName
     */
    public static void removeUserFromGroup(final String userJid,final String groupName, final XMPPConnection connection) {
            RosterGroup group = connection.getRoster().getGroup(groupName);
            if (group != null) {
                try {
                	RosterEntry entry = connection.getRoster().getEntry(userJid);
                    if (entry != null)
                        group.removeEntry(entry);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
     }
    
    /** 
     * 修改心情 
     * @param connection 
     * @param status 
     */  
    public static void changeStateMessage(final XMPPConnection connection,final String status)  
    {  
        Presence presence = new Presence(Presence.Type.available);  
        presence.setStatus(status);  
        connection.sendPacket(presence);      
    }  
    
    /** 
     * 获取用户头像信息 
     *  
     * @param connection 
     * @param user 
     * @return 
     */  
    public Drawable getUserImage(final XMPPConnection connection,String user) {  
        
        ByteArrayInputStream bais = null;  
        try {  
            VCard vcard = new VCard();  
            // 加入这句代码，解决No VCard for  
            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",  
                    new org.jivesoftware.smackx.provider.VCardProvider());  
            if (user == "" || user == null || user.trim().length() <= 0) {  
                return null;  
            }  
            vcard.load(connection, user + "@"  
                    + connection.getServiceName());  
  
            if (vcard == null || vcard.getAvatar() == null)  
                return null;  
            bais = new ByteArrayInputStream(vcard.getAvatar());  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
        return null;//FormatTools.getInstance().InputStream2Drawable(bais);  
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}    
	
    
}
