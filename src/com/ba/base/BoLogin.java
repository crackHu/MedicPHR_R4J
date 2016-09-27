package com.ba.base;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class BoLogin {
	public JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Transactional(propagation=Propagation.REQUIRED)
	
	public JSONObject login(JSONObject jDataIn)
			throws Exception {
		// 方法：login
		// 入口参数
		// name: user name
		// password: user password
		// 返回参数
		// userId.userName.loginName.deptId.depName.userRole.url.userRoles
		
		// 將 sDataIn 转为 JSONObject
		//System.out.println("BoLogin:login:sDataIn="+sDataIn);
		//JSONObject joDataIn = (JSONObject) JSONSerializer.toJSON(sDataIn);
		System.out.println("BoLogin:login:DataIn="+jDataIn.toString());
		
		// 获取参数变量
		String pName= jDataIn.getString("name");
		String pPassword= jDataIn.getString("password");
		
		//Map<String, Object> dataOut = new HashMap<String, Object>();
		JSONObject jDataOut = new JSONObject();
		Connection conn = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
		try{
			String sql = "";
			sql = "select * from rs_user where loginName='"+pName+"' and password='"+pPassword+"'";
			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
			int rst=-1000;
			String rstMsg="";
			if (rs.next()){
				rst=1;  // 用户账户密码正确
				rstMsg="ok";
				
				jDataOut.put("userId", rs.getString("id"));
				jDataOut.put("userName", rs.getString("name"));
				jDataOut.put("loginName", rs.getString("loginName"));
				
				jDataOut.put("deptId", "***");
				jDataOut.put("depName", "***");
				jDataOut.put("userRole", "***");
				jDataOut.put("userRoles", "***");
				
				jDataOut.put("url", "./desk.jsp");
				
			} else {
				rst=0;  // 账户或密码不对
				rstMsg="userOrpass faile";
			}
			
			// 返回 >=0 OK
			jDataOut.put("resultCode", rst);
			jDataOut.put("resultMsg", rstMsg);
		}
		catch (Exception e)
	    {
			// 返回 <0 error
			jDataOut.put("resultCode", -99);
			jDataOut.put("resultMsg", "执行失败，err="+e.getMessage());
		    conn.rollback();
	    }
		
		return jDataOut;
	}

}
