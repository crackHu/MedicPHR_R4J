package com.ba.util;
 
import com.alibaba.dingtalk.openapi.OApiException;
import com.alibaba.dingtalk.openapi.message.LightAppMessageDelivery;
import com.alibaba.dingtalk.openapi.message.MessageHelper;
import com.alibaba.dingtalk.openapi.message.OAMessage;




public class utilWtdex {
 
	
	/**
	 * 向企业群发送一条OA消息
	 * @param bs
	 * @return
	 * @throws OApiException 
	 */
	
	public void postdFollowupOaMsg(String sender,String urls,String title,String content,String accessToken,String AGEBTID) throws OApiException{
	    // 设置消息通知内容
		//TextMessage textMessage = new TextMessage(msg);
		
		//创建oa消息
		//String url = "http://www.basoft.cn:8082/medical/medic/dr_visit.html?rand=123124566645654";
		OAMessage oaMessage = new OAMessage();
		oaMessage.message_url = urls;
		OAMessage.Head head = new OAMessage.Head();
	    head.bgcolor = "ff40affc";
		oaMessage.head = head;
		
		OAMessage.Body body = new OAMessage.Body();
		body.title = content;
		body.image = "http://cdimg.cdsns.com/thumbs/201511/16/bbc0d5baf81c00e248c760dd77c4cb4d.gif";
		body.author = title;
		oaMessage.body = body;
	    // 发送消息通知客户
		LightAppMessageDelivery lightAppMessageDelivery = new LightAppMessageDelivery(sender, AGEBTID);
		lightAppMessageDelivery.withMessage(oaMessage);
		try {
			MessageHelper.send(accessToken, lightAppMessageDelivery);
		} catch (OApiException e) {
		 
			e.printStackTrace();
		}
		System.out.println("发送消息通知用户成功");
		
		
	}
	
	

}
