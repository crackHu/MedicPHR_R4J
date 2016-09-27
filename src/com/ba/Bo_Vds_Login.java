package com.ba;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dingtalk.openapi.Env;
import com.alibaba.dingtalk.openapi.utils.HttpHelper;
import com.ba.init.VdsResponse;

import ba.base.BoBase;
/**
 * 登陆获取数据
 * @author Asan
 *
 */
public class Bo_Vds_Login extends BoBase{
	public JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
/*	public JdbcTemplate secondJdbcTemplate;

	public void setSecondJdbcTemplate(JdbcTemplate secondJdbcTemplate) {
		this.secondJdbcTemplate = secondJdbcTemplate;
	}
	*/

	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject login(JSONObject jDataIn) throws Exception {
		System.out.println("进入移植后的Bo_Vds_Login.java");
		// 方法：login
		// 入口参数
		// 返回参数
		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		JSONObject din = (JSONObject) jsonObj.get("din");
		String code = (String) din.get("code");
		
		String corpId = (String) din.get("corpId");
		
		long dinTimeStamp = System.currentTimeMillis();
		int drow = 1;
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS"); 
		String logNumber = sdf.format(date);
		Calendar cal = Calendar.getInstance();
		
		String accessToken = null;
		String agertId = null;
		SqlRowSet rs = null;
		JSONObject json = new JSONObject();
		String id = "";
		JSONObject jDataOut = new JSONObject();
		String msg = "";
		String resultCode = "";
		String hospitalname = null;
		String hospitalId = null;
		//获取accessToken
		try {
			String sql = "select j.accessToken as accessToken,h.agertId as agertId,h.name as hospitalname,h.id as hospitalId from badd.med_jsapi_ticket j left join badd.med_hospital h on j.hospitalId = h.id where h.corpId ='"+corpId+"'";
			rs = this.jdbcTemplate.queryForRowSet(sql);
			if (rs.next()) {
				accessToken = rs.getString("accessToken");
				agertId = rs.getString("agertId");
				hospitalname = rs.getString("hospitalname");
				hospitalId = rs.getString("hospitalId");
				
			}
			
			//用code获取用户userid
			String url = Env.OAPI_HOST + "/user/getuserinfo?" +
					"access_token=" + accessToken+"&code="+code;
			com.alibaba.fastjson.JSONObject responses = HttpHelper.httpGet(url);
		 
			System.out.println("userid ============="+responses.get("userid").toString());
			String userId = (String)responses.get("userid");
			//用获取工号userid
			/*String gurl = Env.OAPI_HOST + "/user/get?" +
					"access_token=" + accessToken+"&userid="+responses.get("userid").toString();
			com.alibaba.fastjson.JSONObject gresponses = HttpHelper.httpGet(gurl);
			System.out.println("gresponses: "+gresponses);*/
			//用userid获取用户信息
			String messageUrl = Env.OAPI_HOST + "/user/get?" +
					"access_token=" + accessToken+"&userid="+userId;
			com.alibaba.fastjson.JSONObject mresponses = HttpHelper.httpGet(messageUrl);
			
			System.out.println("name123 ============="+mresponses.get("name").toString());
			String name = (String)mresponses.get("name");
			String avatar = (String)mresponses.get("avatar");
		    String mobile = (String)mresponses.get("mobile");
		    System.out.println("当前人的电话号码="+mobile);
		    json.put("mobile", mobile);
			json.put("userId", userId);
			json.put("agertId", agertId);
			json.put("hospitalname", "123123");
			json.put("hospitalId", hospitalId);
			json.put("name", name);
			json.put("avatar", avatar);
			System.out.println("json====="+json.toString());
			resultCode = "1";
			msg = "登陆成功";
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			jDataOut.put("resultCode", -99);
			jDataOut.put("resultMsg", null);
			e.printStackTrace();
		}
		
		// 返回前台json
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		
		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		System.out.println("json====="+json.toString());
		jDataOut.put("dout", json);
		
		drow=drow+1;
		long doutTimeStamp = System.currentTimeMillis();

		cal.setTimeInMillis(doutTimeStamp-dinTimeStamp);

		String timeConsuming = "耗时: " + cal.get(Calendar.MINUTE) + "分 "
						+ cal.get(Calendar.SECOND) + "秒 "
						+ cal.get(Calendar.MILLISECOND) + " 毫秒";
		addActionLog(id, "登陆获取数据", "dout", jDataOut.toString(), logNumber, timeConsuming, msg, resultCode,"【"+drow+"】");
		
		return jDataOut;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject returnLoginData(JSONObject jDataIn) throws Exception {
		// 方法：returnLoginData
		// 入口参数
		// 返回参数
		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		JSONObject din = (JSONObject) jsonObj.get("din");
		String dd_userId = din.get("dd_userId").toString();
		String corpId = (String) din.get("corpId");
		
		
		long dinTimeStamp = System.currentTimeMillis();
		int drow = 1;
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS"); 
		String logNumber = sdf.format(date);
		Calendar cal = Calendar.getInstance();
		
		SqlRowSet rs = null;
		JSONObject json = new JSONObject();
		String id = "";
		JSONObject jDataOut = new JSONObject();
		String msg = "";
		String resultCode = "";
		addActionLog(id, "登陆加载页获取数据", "din",  jsonObj.toString(),logNumber, null, null, null,"【"+drow+"】");
		//获取accessToken
		try {
			String sql = "";
			
			String type = "";
			
			int row = 0;
			
			sql = "SELECT count(*) from badd.med_relationhospital re left join badd.med_hospital h on re.hospitalId = h.id WHERE h.corpId = '"+corpId+"' and re.dd_userId = '"+dd_userId+"'";
			drow=drow+1;
			addActionLog(id, "登陆加载页获取数据", "SQL", sql,logNumber, null, null, null,"【"+drow+"】");
			row = this.jdbcTemplate.queryForInt(sql);
			
			sql = "SELECT re.`name` as name,re.dd_userId as emplId,re.id as memberId,re.type as type,h.`name` as hospitalname,h.id as hospitalId,h.agertId as agertId,re.memberId as id from badd.med_relationhospital re left join badd.med_hospital h on re.hospitalId = h.id WHERE h.corpId = '"+corpId+"' and re.dd_userId = '"+dd_userId+"'";
			drow=drow+1;
			addActionLog(id, "登陆加载页获取数据", "SQL", sql,logNumber, null, null, null,"【"+drow+"】");
			rs = this.jdbcTemplate.queryForRowSet(sql);
			
			while(rs.next()) {
				id = rs.getString("memberId");
				//类型 0表示医生，1表示病人
				type = rs.getString("type");
				
				if (row == 1) {
					json.put("memberId", rs.getString("memberId"));
					json.put("emplId", rs.getString("emplId"));
					json.put("name", rs.getString("name"));
					json.put("hospitalname", rs.getString("hospitalname"));
					json.put("hospitalId", rs.getString("hospitalId"));
					json.put("agertId", rs.getString("agertId"));
					
					//如果是病人，查询是否有就诊人
					if (type.equals("1")) {
						sql = "select count(*) from med_userBinding where memberId = '"+rs.getString("memberId")+"' and isDelete = 0";
						drow=drow+1;
						addActionLog(id, "登陆加载页获取数据", "SQL", sql,logNumber, null, null, null,"【"+drow+"】");
						row = this.jdbcTemplate.queryForInt(sql);
						if (row>0) {
							json.put("noPeatient", 1);
						}else {
							json.put("noPeatient", 0);
						}
					}
				}else {
					if (type.equals("0")) {
						json.put("memberId", rs.getString("memberId"));
						json.put("emplId", rs.getString("emplId"));
						json.put("name", rs.getString("name"));
						json.put("hospitalname", rs.getString("hospitalname"));
						json.put("hospitalId", rs.getString("hospitalId"));
						json.put("agertId", rs.getString("agertId"));
					}
				}
					
			}
			
			json.put("type", type);
			resultCode = "1";
			msg = "登陆加载页获取数据";
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			jDataOut.put("resultCode", -99);
			jDataOut.put("resultMsg", null);
			e.printStackTrace();
		}
		
		// 返回前台json
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		jDataOut.put("dout", json);
		
		drow=drow+1;
		long doutTimeStamp = System.currentTimeMillis();

		cal.setTimeInMillis(doutTimeStamp-dinTimeStamp);

		String timeConsuming = "耗时: " + cal.get(Calendar.MINUTE) + "分 "
						+ cal.get(Calendar.SECOND) + "秒 "
						+ cal.get(Calendar.MILLISECOND) + " 毫秒";
		addActionLog(id, "登陆加载页获取数据", "dout", jDataOut.toString(), logNumber, timeConsuming, msg, resultCode,"【"+drow+"】");
		
		return jDataOut;
	}
	
	
	/**
	 * 添加日志记录类
	 * 
	 * @param memberId
	 * @param content
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addActionLog(String memberId, String title, String type,
			String content, String logNumber, String timeConsuming, String msg,
			String resultCode,String orders) {

		String msgs = null;
		if (resultCode != null) {
			if (!resultCode.equals("1")) {
				if (msg != null && !msg.equals("")) {
					msgs = msg.replaceAll("'", "''");
					resultCode = "-99";
					msg = "SQL语句出错";
				} else {
					resultCode = "-98";
					msg = "程序代码出错";
				}
				
			} else {
				msgs = content;
			}
		}else {
			if (msg != null && !msg.equals("")) {
				msgs = msg.replaceAll("'", "''");
			} else {
				msgs = content.replaceAll("'", "''");
			}
		}
		String sql = "";
		// 记录id
		String actionLogid = UUID.randomUUID().toString();
		sql = "INSERT into badd.med_actionlog (id,createDate,modifyDate,memberId,title,type,content,logNumber,timeConsuming,resultCode,resultMsg,orders) " +
				"values ('"+ actionLogid+ "',now(),now(),'"+ memberId+ "','"+ title+ "','"+ type+ "','"+ msgs+ "','"+ logNumber	+ "','"+ timeConsuming + "','" + resultCode + "','" + msg + "','"+orders+"')";
		jdbcTemplate.update(sql);
	}
}
