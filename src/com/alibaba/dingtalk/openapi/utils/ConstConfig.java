package com.alibaba.dingtalk.openapi.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConstConfig {

	private transient static final Log log = LogFactory
			.getLog(ConstConfig.class);
	static {
		try {
			init();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	
	public static String myPostUrl;
	public static String CORP_ID;
	public static String SECRET;
	public static String AGEBTID;
	public static String OAPI_HOST;
	public static String departmentId;
	public static String notifyUrl;
	public static String msgPostUrl;
	public static String testDepartmentId;
	public static String appid;
	public static String wxsecret;
	static void init() {
		final Properties pro = new Properties();
		try {
			pro.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("config.properties"));
		} catch (FileNotFoundException e) {
			log.error("File not found" + e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		
		ConstConfig.myPostUrl = pro.getProperty("myPostUrl");
		ConstConfig.CORP_ID = pro.getProperty("CORP_ID");
		ConstConfig.SECRET = pro.getProperty("SECRET");
		ConstConfig.AGEBTID = pro.getProperty("AGEBTID");
		ConstConfig.OAPI_HOST = pro.getProperty("OAPI_HOST");
		ConstConfig.departmentId = pro.getProperty("departmentId");
		ConstConfig.notifyUrl = pro.getProperty("notifyUrl");
		ConstConfig.msgPostUrl = pro.getProperty("msgPostUrl");
		ConstConfig.testDepartmentId = pro.getProperty("testDepartmentId");
		ConstConfig.appid = pro.getProperty("appid");
		ConstConfig.wxsecret = pro.getProperty("wxsecret");
		//ConstConfig.isToken = pro.getProperty("isToken");
	}
}
