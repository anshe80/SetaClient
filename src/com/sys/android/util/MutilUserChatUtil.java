package com.sys.android.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import com.seta.android.entity.ServerRooms;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class MutilUserChatUtil extends BaseAdapter{
	
	private static XMPPConnection mConnection = null;
	
	public MutilUserChatUtil(XMPPConnection connection) {
		// TODO Auto-generated constructor stub
		this.mConnection=connection;
	}
	
	public XMPPConnection getMConnection(){
		return this.mConnection;
	}

	/**
	   * 获取服务器上所有会议室
	   * @return
	   * @throws XMPPException
	   */
	  public  List<ServerRooms> getConferenceRoom() throws XMPPException {
	    List<ServerRooms> list = new ArrayList<ServerRooms>();
	    new ServiceDiscoveryManager(mConnection);
	    if (!MultiUserChat.getHostedRooms(mConnection,mConnection.getServiceName()).isEmpty()) {

	      for (HostedRoom k : MultiUserChat.getHostedRooms(mConnection,mConnection.getServiceName())) {

	        for (HostedRoom j : MultiUserChat.getHostedRooms(mConnection,
	            k.getJid())) {
	          RoomInfo info2 = MultiUserChat.getRoomInfo(mConnection,
	              j.getJid());
	          if (j.getJid().indexOf("@") > 0) {

	            ServerRooms friendrooms = new ServerRooms();
	            friendrooms.setName(j.getName());//聊天室的名称
	            friendrooms.setJid(j.getJid());//聊天室JID
	            friendrooms.setOccupants(info2.getOccupantsCount());//聊天室中占有者数量
	            friendrooms.setDescription(info2.getDescription());//聊天室的描述
	            friendrooms.setSubject(info2.getSubject());//聊天室的主题
	            list.add(friendrooms);
	          }
	        }
	      }
	    }
	    return list;
	  }
	/**
	 * 创建房间
	 * 
	 * @param roomName  房间名称
	 */
	public MultiUserChat createRoom(String user, String roomName,String password) {
		if (getMConnection() == null)
			return null;
		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(getMConnection(), roomName + "@conference."
					+ getMConnection().getServiceName());
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields
					.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
			List<String> owners = new ArrayList<String>();
			owners.add(getMConnection().getUser());// 用户JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
						true);
				// 设置进入密码
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}
	
	/**
	 * 加入会议室
	 * 
	 * @param user
	 *            昵称
	 * @param password
	 *            会议室密码
	 * @param roomsName
	 *            会议室名
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName,
			String password) {
		if (getMConnection() == null)
			return null;
		try {
			// 使用XMPPConnection创建一个MultiUserChat窗口
			MultiUserChat muc = new MultiUserChat(getMConnection(), roomsName
					+ "@conference." + getMConnection().getServiceName());
			// 聊天室服务将会决定要接受的历史记录数量
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(1000);
			Calendar calendar = Calendar.getInstance();
		    /* HOUR_OF_DAY 指示一天中的小时 */
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 30);
			history.setSince(calendar.getTime());
			// 用户加入聊天室
			muc.join(user, password, history,
					SmackConfiguration.getPacketReplyTimeout());			
			Log.i("MultiUserChat", "用户："+user+"成功加入会议室【"+roomsName+"】........");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "用户："+user+"成功加入会议室【"+roomsName+"】........");
			return null;
		}
	}
	
	/**
	   * 查询会议室成员名字
	   * @param muc
	   */
	  public static List<String> findMulitUser(MultiUserChat muc){
	    List<String> listUser = new ArrayList<String>();
	    Iterator<String> it = muc.getOccupants();
	    //遍历出聊天室人员名称
	    while (it.hasNext()) {
	      // 聊天室成员名字
	      String name = StringUtils.parseResource(it.next());
	      listUser.add(name);
	    }
	    return listUser;
	  }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
