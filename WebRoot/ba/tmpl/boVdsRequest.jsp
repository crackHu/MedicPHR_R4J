//
// 前端 dummy 桥接bo
//
// js 端调用规范
/*
	var dataIn={};  // dataIn=<YourJSONObject>;
	$.ajax({
		url:_ba.app+"ba/tmpl/boDummy.jsp",
		cache:false,
		type:"post",
		data:{data:JSON.stringify(dataIn)},
		dataType:"json",
		success:function(dataOut){
			// dataOut.resultCode >=0 是成功执行（语义由类自定义）;  <0 执行失败
			alert("code="+dataOut._c+"\nmsg="+dataOut._m);
		},
		error: function(dataOut){
			alert("失败");
		}
	});
*/

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="org.apache.log4j.*"/>
<jsp:directive.page import="org.springframework.web.context.WebApplicationContext"/>
<jsp:directive.page import="org.springframework.web.context.support.WebApplicationContextUtils"/>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.util.HashMap"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="net.sf.json.JSONObject"/>
<jsp:directive.page import="net.sf.json.JSONSerializer"/>

// [Mod-A1]:本jsp调用的类
<jsp:directive.page import="ba.base.BoBase"/>
<%
// [Mod-A1]

//访问网址  http://localhost:8080/vds5s1/prjX/login.html
org.apache.log4j.Logger log = Logger.getLogger("boVdsRequest");

Date dateBg = new Date();
String sSession = session.getId();// .toString();
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println("path="+basePath);
request.setCharacterEncoding("utf-8");

// 获取ajax参数，ajax格式是  data:<dataIn json to string>
// 前端js语句: $.ajax({ data:{data:JSON.stringify(dataIn) })
// 以下语句使得  sDataIn = <dataIn json to string>，并转化为 JSONObject jDataIn
// 在 bean中 jDataIn 恢复为 jason 结构
// bean语句: jDataIn.getString("<name>"); 获得参数
Map map = request.getParameterMap();
for(Iterator iter = map.entrySet().iterator();iter.hasNext();){    
    Map.Entry element = (Map.Entry)iter.next();    
    String key = element.getKey().toString();   
    String[] value=(String[])element.getValue();  
   System.out.println(key+"="+value[0]);  
}

//通过  get 的方法来加载
String sDataIn=request.getParameter("data");
System.out.println("sDataIn="+sDataIn);
JSONObject jDataIn = (JSONObject) JSONSerializer.toJSON(sDataIn);
JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());

System.out.println("jsonObj="+jsonObj);
//获取要执行的function
String sBoName = (String)jsonObj.get("pid");
String sFuncName= (String)jsonObj.get("fid"); 
// [Dbg-1]
System.out.println("sBoName="+sBoName);
System.out.println("sFuncName="+sFuncName);
System.out.format("[%s][%s][%s]in=[%s]%n",sSession,sBoName,dateBg.toGMTString(),sDataIn);

log.fatal(String.format("[%s][%s][%s]in=[%s]%n",sSession,sBoName,dateBg.toGMTString(),sDataIn));

WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
// 获取bean，该bean必须在 applicationContext.xml(或之下)中定义，并且在本jsp上面 import 该类(如 com.ba.base.Bo<name>)
// 注意类的大小写。规范：beanClassName= Bo<name>;  xml配置bean= bo<name>;  本jsp变量名= bo<name>
BoBase boNameMgr = (BoBase)wac.getBean(sBoName);
JSONObject jDataOut = null;

// 调用该类的某个属性名
jDataOut = boNameMgr.callMethod(sFuncName, jDataIn);
// 一般情况下，除非有必要，不需要以下代码（对 jDataOut.resultCode 处理）
// ------------------------------
// ------------------------------

// 将 jDataOut 转化为 string，返回前端
out.clear();
out.print(jDataOut.toString());

// 调试信息
// [Dbg-2]
Date dateEnd = new Date();
Long dateMs = dateEnd.getTime() - dateBg.getTime();
System.out.format("[%s][%s][%d ms]out=[%s]%n",sSession,sBoName,dateMs,jDataOut.toString());

%>
