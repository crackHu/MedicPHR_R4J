package com.ba.init;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class VdsRequest extends Basetuple implements Serializable {
	Map<String, Object> din;
	public VdsRequest() {
		din = new HashMap<String, Object>();
	}
	
	public VdsRequest(String jsonStr) {
		din = new HashMap<String, Object>();
		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			jsonToBasetuple(json);
			TransformationJSONStringToMap(din,json.getString("din"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Object> getDin() {
		return din;
	}
	public void setDin(Map<String, Object> din) {
		this.din = din;
	}
	/**
	 * 杞垚json瀛楃涓�
	 */
	public String toString(VdsRequest er) {
		return net.sf.json.JSONObject.fromObject(er).toString();
	}
}
