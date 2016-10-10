package demo;
import javax.inject.Inject;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.junit.Test;

import phrs.phr.Bo_Vds_Login;

public class TestCase extends BaseTest {

	@Inject
	private Bo_Vds_Login boVdsLogin;
	
	@Test
	public void testPdSaveData() {

		String json = "{\"din\":{\"page\":1,\"rows\":10}}";
		JSON json2 = JSONSerializer.toJSON(json);
		JSONObject fromObject = JSONObject.fromObject(json);
		System.out.println(json2);
		System.out.println(fromObject);
		
	}
	
	@Test
	public void testLogin() {
		
		String json = "{\"din\":{\"loginName\":\"lweihua\",\"loginPwd\":\"123456\"}}";
		JSONObject dataIn = JSONObject.fromObject(json);
		JSONObject proLogin = boVdsLogin.proLogin(dataIn);
		System.out.println(proLogin);
	}
	
}
