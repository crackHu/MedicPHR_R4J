/**
 * 
 */
package com.ba;


import java.util.Random;

import com.alibaba.dingtalk.openapi.OApiException;
import com.alibaba.dingtalk.openapi.OApiResultException;
import com.alibaba.dingtalk.openapi.auth.AuthHelper;
import com.alibaba.dingtalk.openapi.utils.ConstConfig;
import com.alibaba.dingtalk.openapi.utils.HttpHelper;
import com.ba.init.VdsResponse;
//import badd.util.TokenUtil;
import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//import badd.util.SHA1;


import ba.base.BoBase;


/**
 * 钉钉 加密
 * 
 * @author Asan
 * 
 */
public class Bo_Vds_Sha1Encrypt extends BoBase {
	public JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject sha1Encrypt(JSONObject jDataIn) throws Exception {
		System.out.println("进入移植后的Bo_Vds_Sha1Encrypt.java");
		// 方法：sha1Encrypt
		// 入口参数
		// 返回参数
		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		// 获取参数变量
		JSONObject din = (JSONObject) jsonObj.get("din");
		String url = (String) din.get("url");
		String corpId = (String) din.get("corpId");
		JSONObject json = query(url,corpId);
		// 返回前台json
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		// 拿取返回结果
		String jsons = response.toString(response);
		JSONObject jDataOut = JSONObject.fromObject(jsons);
		jDataOut.put("data", json);
		return jDataOut;
	}
	
	
	/**
	 * 
	 * @param url
	 * @param corpId
	 * @return
	 */
	public JSONObject query(String url,String corpId){
		String sql = "";
		SqlRowSet rs = null;
		String ticket = null;
		String agertId = null;
		String hospitalId = null;
		String endTime = null;
		sql = "select j.ticket as ticket,h.agertId as agertId,j.hospitalId as hospitalId,j.endTime as endTime from badd.med_jsapi_ticket j " +
				" left join badd.med_hospital h on j.hospitalId = h.id where h.corpId = '"+corpId+"'";
		rs = this.jdbcTemplate.queryForRowSet(sql);
		while (rs.next()) {
			ticket = rs.getString("ticket");
			agertId = rs.getString("agertId");
			hospitalId = rs.getString("hospitalId");
			endTime = rs.getString("endTime");
		}
		
		
		System.out.println("ticket=====:"+ticket);
		System.out.println("agertId=====:"+agertId);
		
		String noncestr = Bo_Vds_Sha1Encrypt.getRandomString(16);
		long timestamp = System.currentTimeMillis();
		
		// 获取签名
		
		String signature = "";
		try {
			signature = AuthHelper.sign(ticket, noncestr, timestamp, url);
		} catch (OApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		JSONObject json = new JSONObject();
		json.put("timestamp", timestamp);
		json.put("noncestr", noncestr);
		json.put("signature", signature);
		json.put("agertId", agertId);
		json.put("hospitalId", hospitalId);
		json.put("endTime", endTime);
		return json;
	}
	
	/**
	 * 获取接口验证需要的sign
	 * @param jDataIn
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject getSign(JSONObject jDataIn) throws Exception {
		// 方法：sha1Encrypt
		// 入口参数
		// 返回参数
		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		// 获取参数变量
		JSONObject din = (JSONObject) jsonObj.get("din");
		String token = (String) din.get("token");
		String timestamp = (String) din.get("timestamp");
		String noncestr = (String) din.get("noncestr");
		
		String signature = "token="+token+"&noncestr="+noncestr+"&timestamp="+timestamp+"";
		
		SHA1 sha1 = new SHA1();
		String signatures = sha1.getDigestOfString(signature.getBytes());
		
		JSONObject json = new JSONObject();
		json.put("sign", signatures);
		// 返回前台json
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		// 拿取返回结果
		String jsons = response.toString(response);
		JSONObject jDataOut = JSONObject.fromObject(jsons);
		jDataOut.put("dout", json);
		return jDataOut;
	}
	
	/**
	 * 生成随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLNMOPQRSTUVWXYZ";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }
	
	
	//补充：

	/**
	 *   获得 token
	 * @param url
	 * @param corpId
	 * @return
	 */
	public String getAccessToken(String corpid , String corpsecret) throws OApiException{
		String url = ConstConfig.OAPI_HOST + "/gettoken?" + 
				"corpid=" + corpid + "&corpsecret=" + corpsecret;
		com.alibaba.fastjson.JSONObject response = HttpHelper.httpGet(url);
		if (response.containsKey("access_token")) {
			return response.getString("access_token");
		}
		else {
			throw new OApiResultException("access_token");
		}
	}
	
	/**
	 *  获取JsapiTicket
	 * @param accessToken
	 * @return
	 * @throws OApiException
	 */
	public String getJsapiTicket(String accessToken) throws OApiException{
		String url = ConstConfig.OAPI_HOST + "/get_jsapi_ticket?" + 
				"type=jsapi" + "&access_token=" + accessToken;
		com.alibaba.fastjson.JSONObject response = HttpHelper.httpGet(url);
		if (response.containsKey("ticket")) {
			return response.getString("ticket");
		}
		else {
			throw new OApiResultException("ticket");
		}
	}
	
	/**
	 * 更新 Ticket accessToken
	 *
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject oldupdateTicket(JSONObject jDataIn) throws Exception {

		// 方法：CustomsServlet
		// 入口参数
		// 返回参数

		// 將 sDataIn 转为 JSONObject
		updateTicket(jdbcTemplate);
		return jDataIn;
	}

	private void updateTicket(JdbcTemplate jdbcTemplate2) {
		// TODO Auto-generated method stub
		
	}
	
}
