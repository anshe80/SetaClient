package com.seta.android.email;
import java.util.Date;    
import java.util.Properties;   

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;    
import javax.mail.BodyPart;    
import javax.mail.Message;    
import javax.mail.MessagingException;    
import javax.mail.Multipart;    
import javax.mail.Session;    
import javax.mail.Transport;    
import javax.mail.internet.InternetAddress;    
import javax.mail.internet.MimeBodyPart;    
import javax.mail.internet.MimeMessage;    
import javax.mail.internet.MimeMultipart;

import android.util.Log;
/**   
 * 简单邮件（不带附件的邮件）发送器   
 */    
public class SimpleMailSender  
{    
	public static boolean DEBUG=true;
	/**   
	 * 以文本格式发送邮件   
	 * @param mailInfo 待发送的邮件的信息   
	 */    
	public boolean sendTextMail(MailSenderInfo mailInfo) 
	{
		// 判断是否需要身份认证    
		MyAuthenticator authenticator = null;    
		Properties pro = mailInfo.getProperties();   
		if (mailInfo.isValidate()) 
		{    
			// 如果需要身份认证，则创建一个密码验证器    
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());    
		}   
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session    
		Session sendMailSession = Session.getInstance(pro,authenticator);    
		try 
		{    
			// 根据session创建一个邮件消息    
			Message mailMessage = new MimeMessage(sendMailSession);    
			// 创建邮件发送者地址    
			Address from = new InternetAddress(mailInfo.getFromAddress());    
			// 设置邮件消息的发送者    
			mailMessage.setFrom(from);    
			// 创建邮件的接收者地址，支持多方接收者，并设置到邮件消息中
			String[] toAddress=mailInfo.getToAddress().split(",");
			Address[] to=new InternetAddress[toAddress.length];			
			for (int i = 0; i < to.length; i++) 
		    { 
		        to[i] = new InternetAddress(toAddress[i]); 
		    }    
			mailMessage.setRecipients(Message.RecipientType.TO,to);    
			// 设置邮件消息的主题    
			mailMessage.setSubject(mailInfo.getSubject());    
			// 设置邮件消息发送的时间    
			mailMessage.setSentDate(new Date());    
			// 设置邮件消息的主要内容    
			String mailContent = mailInfo.getContent(); 
			Log.e("服务器邮箱", "邮箱："+mailInfo.getFromAddress()+"密码："+mailInfo.getPassword());
			mailMessage.setText(mailContent);    
			// 发送邮件    
			Transport transport = sendMailSession.getTransport(mailInfo.getMailServerHost().substring(0, 4));  
            transport.connect(mailInfo.getMailServerHost(), mailInfo.getFromAddress(), mailInfo.getPassword());  
            if(transport.isConnected()){
            	Transport.send(mailMessage); 
            	transport.close();     
            	return true;    
			}
		} 
		catch (MessagingException ex) 
		{    
			ex.printStackTrace();    
		}    
		return false;    
	}    

	/**   
	 * 以HTML格式发送邮件   
	 * @param mailInfo 待发送的邮件信息   
	 */    
	public static boolean sendHtmlMail(MailSenderInfo mailInfo)
	{    
		// 判断是否需要身份认证    
		MyAuthenticator authenticator = null;   
		Properties pro = mailInfo.getProperties();   
		//如果需要身份认证，则创建一个密码验证器     
		if (mailInfo.isValidate()) 
		{    
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());   
		}    
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session    
		Session sendMailSession = Session.getInstance(pro,authenticator);    
		try {    
			// 根据session创建一个邮件消息    
			Message mailMessage = new MimeMessage(sendMailSession);    
			// 创建邮件发送者地址    
			Address from = new InternetAddress(mailInfo.getFromAddress());    
			// 设置邮件消息的发送者    
			mailMessage.setFrom(from);   			

			// 创建邮件的接收者地址，支持多方接收者，并设置到邮件消息中
			String[] toAddress=mailInfo.getToAddress().split(",");
			Address[] to=new InternetAddress[toAddress.length];			
			for (int i = 0; i < to.length; i++) 
		    { 
		        to[i] = new InternetAddress(toAddress[i]); 
		    }   			
			
			// Message.RecipientType.TO属性表示接收者的类型为TO    
			mailMessage.setRecipients(Message.RecipientType.TO,to);    
			// 设置邮件消息的主题    
			mailMessage.setSubject(mailInfo.getSubject());    
			// 设置邮件消息发送的时间    
			mailMessage.setSentDate(new Date());    
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象    
			Multipart mainPart = new MimeMultipart();    
			// 创建一个包含HTML内容的MimeBodyPart    
			BodyPart html = new MimeBodyPart();    
			// 设置HTML内容    
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");    
			mainPart.addBodyPart(html); 
			
			// 将MiniMultipart对象设置为邮件内容    
			mailMessage.setContent(mainPart);    
			// 发送邮件    
			Transport transport = sendMailSession.getTransport(mailInfo.getMailServerHost().substring(0, 4));  
            transport.connect(mailInfo.getMailServerHost(), mailInfo.getFromAddress(), mailInfo.getPassword());  
            if(transport.isConnected()){
            	Transport.send(mailMessage); 
            	transport.close();     
            	return true;    
			}   
		} catch (MessagingException ex) {    
			ex.printStackTrace();    
		}    
		return false;    
	}    
	
	/**   
	 * 以多发接收，多个附件格式发送邮件   
	 * @param mailInfo 待发送的邮件信息   
	 */ 
	public boolean sendMutilMail(MailSenderInfo mailInfo)
	{    
		// 判断是否需要身份认证    
		MyAuthenticator authenticator = null;   
		Properties pro = mailInfo.getProperties();   
		//如果需要身份认证，则创建一个密码验证器     
		if (mailInfo.isValidate()) 
		{    
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());   
		}  		
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session    
		Session sendMailSession = Session.getInstance(pro,authenticator);    
		try {    
			// 根据session创建一个邮件消息    
			Message mailMessage = new MimeMessage(sendMailSession);    
			// 创建邮件发送者地址    
			Address from = new InternetAddress(mailInfo.getFromAddress());    
			// 设置邮件消息的发送者    
			mailMessage.setFrom(from);   			

			// 创建邮件的接收者地址，支持多方接收者，并设置到邮件消息中
			String[] toAddress=mailInfo.getToAddress().split(",");
			Address[] to=new InternetAddress[toAddress.length];			
			for (int i = 0; i < to.length; i++) 
		    { 
		        to[i] = new InternetAddress(toAddress[i]); 
		    }   			
			
			// Message.RecipientType.TO属性表示接收者的类型为TO    
			mailMessage.setRecipients(Message.RecipientType.TO,to);    
			// 设置邮件消息的主题    
			mailMessage.setSubject(mailInfo.getSubject());    
			// 设置邮件消息发送的时间    
			mailMessage.setSentDate(new Date());    
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象    
			Multipart mainPart = new MimeMultipart();    
			// 创建一个包含HTML内容的MimeBodyPart    
			BodyPart html = new MimeBodyPart();    
			// 设置HTML内容    
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");    
			mainPart.addBodyPart(html); 
			
			//添加附件
		     // 创建一新的MimeBodyPart  
		     MimeBodyPart mdp;
		     String[] fileName=mailInfo.getAttachFileNames();
		     //得到多个文件数据源
		     for(int i=0;i<fileName.length;i++){
		    	 if(DEBUG){
		    		 Log.e("邮箱发送附件：", fileName[i]);
		    	 }
			     FileDataSource fds = new FileDataSource(fileName[i]);
			     //得到附件本身并至入BodyPart
			     mdp = new MimeBodyPart();
			     mdp.setDataHandler(new DataHandler(fds));
			     //得到文件名同样至入BodyPart 
			     mdp.setFileName(fds.getName());
			     mainPart.addBodyPart(mdp);
		     }
			// 将MiniMultipart对象设置为邮件内容    
			mailMessage.setContent(mainPart);    
			// 发送邮件    
			Log.e("发送的服务器：", mailInfo.getMailServerHost());
			Transport transport = sendMailSession.getTransport(mailInfo.getMailServerHost().substring(0, 4));  
            transport.connect(mailInfo.getMailServerHost(), mailInfo.getFromAddress(), mailInfo.getPassword());  
            if(transport.isConnected()){
            	Transport.send(mailMessage); 
            	transport.close();     
            	return true;    
			}    
		} catch (MessagingException ex) {    
			ex.printStackTrace();    
		}    
		return false;    
	}    
}
