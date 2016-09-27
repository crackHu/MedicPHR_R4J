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

public class BoDummy {
	public JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Transactional(propagation=Propagation.REQUIRED)
	
	public JSONObject dummy(JSONObject jDataIn)
			throws Exception {
		// 方法：dummy
		// 入口参数
		// 返回参数
		
		// 將 sDataIn 转为 JSONObject
		//System.out.println("BoLogin:login:sDataIn="+sDataIn);
		//JSONObject joDataIn = (JSONObject) JSONSerializer.toJSON(sDataIn);
		System.out.println("BoDummy:dummy:DataIn="+jDataIn.toString());
		
		// 获取参数变量
		
		//Map<String, Object> dataOut = new HashMap<String, Object>();
		JSONObject jDataOut = new JSONObject();
		// 返回 >=0 OK
		jDataOut.put("_c", 1);
		jDataOut.put("_m", "[ok]前端到后台通路正确");
		
		return jDataOut;
	}

}
