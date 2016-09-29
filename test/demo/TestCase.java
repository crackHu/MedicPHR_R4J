package demo;
import javax.annotation.Resource;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.junit.Test;

import pd.Bo_Vds_BoPersonDoc;

public class TestCase extends BaseTest {

	@Resource
	private Bo_Vds_BoPersonDoc boPersonDoc;
	
	@Test
	public void testPdSaveData() {

		String json = "{\"din\":{\"page\":1,\"rows\":10}}";
		JSON json2 = JSONSerializer.toJSON(json);
		JSONObject fromObject = JSONObject.fromObject(json);
		System.out.println(json2);
		System.out.println(fromObject);
		
		System.out.println(boPersonDoc.pdSaveData(fromObject));
	}
	
	@Test
	public void testjson() {
	}
	
}
