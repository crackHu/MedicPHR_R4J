package com.ba.init;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class VdsResponse extends Basetuple implements Serializable{ 
	public class Dout implements Serializable{
		Status status;
		List<Object> dout;

		public Dout(){
			status=new Status();
			dout=new ArrayList<Object>();
		}




		public Status getStatus() {
			return status;
		}
		public void setStatus(Status status) {
			this.status = status;
		}
		

	}
	public class Status implements Serializable{
		int resultCode;
	    String resultMsg;
	    


		public Status(){
		}


		

		public int getResultCode() {
			return resultCode;
		}




		public String getResultMsg() {
			return resultMsg;
		}




		public void jsonStringToHeader(JSONObject json){	
			try {
				this.resultCode=json.isNull("resultCode")?-1:json.getInt("resultCode");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}




		public void setResultCode(int resultCode) {
			this.resultCode = resultCode;
		}




		public void setResultMsg(String resultMsg) {
			this.resultMsg = resultMsg;
		}
		
		
	}
	Dout dout;

	private int total ;

	public VdsResponse(){
		dout = new Dout();
	}
	
	public VdsResponse(String jsonStr){
		dout = new Dout();
		try {
			JSONObject json=new JSONObject(jsonStr);
			jsonToBasetuple(json);
			JSONObject doutJson=json.getJSONObject("dout");
			dout.getStatus().jsonStringToHeader(doutJson.getJSONObject("status"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Status _getStatus() {
		return this.getDout().getStatus();
	}
	public void _setStatus(Status Status) {
		this.getDout().setStatus(Status);
	}


	
	public Dout getDout() {
		return dout;
	}
	public void setDout(Dout dout) {
		this.dout = dout;
	}

	public String toString(VdsResponse ep){
		return net.sf.json.JSONObject.fromObject(ep).getJSONObject("dout").toString();
	}
	
	

}
