package com.ba.init;
 
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Basetuple implements Serializable{
	String sid;
	String pid;
	String fid;
	String jid_c="";
	String jid_s="";

	public Basetuple(){
		
	}



	public String getJid_c() {
		return jid_c;
	}

	public void setJid_c(String jid_c) {
		this.jid_c = jid_c;
	}

	public String getJid_s() {
		return jid_s;
	}

	public void setJid_s(String jid_s) {
		this.jid_s = jid_s;
	}

	public void jsonToBasetuple(JSONObject  json){
		try {
			this.sid=json.isNull("sid")?"-1":json.getString("sid");
			this.pid=json.isNull("pid")?"-1":json.getString("pid");
			this.fid=json.isNull("fid")?"-1":json.getString("fid");
			this.jid_c=json.isNull("jid_c")?"":json.getString("jid_c");
			this.jid_s=json.isNull("jid_s")?"":json.getString("jid_s");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
	public String getSid() {
		return sid;
	}



	public void setSid(String sid) {
		this.sid = sid;
	}



	public String getPid() {
		return pid;
	}



	public void setPid(String pid) {
		this.pid = pid;
	}



	public String getFid() {
		return fid;
	}
	
	
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	
	
	public Map<String, Object> TransformationJSONStringToMap(Map<String, Object> map,
			String jsonStr) {
		JSONObject obj;
			try {
			obj = new JSONObject(jsonStr);
			Iterator<?> objkey = obj.keys();
			while (objkey.hasNext()) {// 遍历JSONObject
				String key = (String) objkey.next().toString();
				String value = obj.getString(key);
				map.put(key, value);
				System.out.println(key+","+value);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return map;
	}
}
