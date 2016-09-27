/**
 * 
 */
package com.ba;

import java.util.Random;

import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ba.base.BoBase;
import com.ba.init.VdsResponse;
import com.ba.util.utilWtdex;
import com.ba.SHA1;

/**
 * 登陆
 * 
 * @author Asan
 * 
 */
public class Bo_Vds_Demo extends BoBase {
	public JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject demoAccessToken(JSONObject jDataIn) throws Exception {
		// 方法：sha1Encrypt
		// 入口参数
		// 返回参数
		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		// 获取参数变量
		JSONObject din = (JSONObject) jsonObj.get("din");
		String url = din.get("url").toString();
		// 返回前台json
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		// 拿取返回结果
		String jsons = response.toString(response);
		JSONObject jDataOut = JSONObject.fromObject(jsons);
		return jDataOut;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject demo(JSONObject jDataIn) throws Exception {
		// 方法：sha1Encrypt
		// 入口参数
		// 返回参数
		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		// 获取参数变量
		JSONObject din = (JSONObject) jsonObj.get("din");
		String url = din.get("url").toString();
		JSONObject json = query(url);
		// 返回前台json
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		// 拿取返回结果
		String jsons = response.toString(response);
		JSONObject jDataOut = JSONObject.fromObject(jsons);
		jDataOut.put("data", json);
		return jDataOut;
	}

	public JSONObject query(String url) {
		String sql = "";
		SqlRowSet rs = null;
		String ticket = "";
		sql = "select ticket from weixin_access_token where id = 1";
		rs = this.jdbcTemplate.queryForRowSet(sql);
		while (rs.next()) {
			ticket = rs.getString("ticket");
		}

		String noncestr = getRandomString(16);
		long timestamp = System.currentTimeMillis();
		String signature = "jsapi_ticket=" + ticket + "&noncestr=" + noncestr
				+ "&timestamp=" + timestamp + "&url=" + url + "";
		// byte[] signatures = DigestUtils.sha1(signature);
		SHA1 sha1 = new SHA1();
		String signatures = sha1.getDigestOfString(signature.getBytes());

		JSONObject json = new JSONObject();
		json.put("timestamp", timestamp);
		json.put("noncestr", noncestr);
		json.put("signature", signatures);
		return json;
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLNMOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject sendToShop(JSONObject jDataIn) throws Exception {
		// 方法：updataOrder
		// 入口参数
		// 返回参数

		System.out.println("BoDummy:dummy:DataIn=" + jDataIn.toString());
		JSONObject jsonObj = JSONObject.fromObject(jDataIn.toString());
		// 获取参数变量
		JSONObject din = (JSONObject) jsonObj.get("din");
		JSONObject jDataOut = null;
		VdsResponse response = null;

		try {
			String corpId = (String) din.get("corpId");
			String userIds = "034237096433073512|014244470032603147";// |046411213026300851|13676259962
			String accessToken = (String) din.get("accessToken");
			String AGEBTID = (String) din.get("AGEBTID");
			String msgContent = "OA消息,OA消息";
			utilWtdex utilWtdex = new utilWtdex();
			System.out.println("dd_userId=========" + userIds);
			System.out.println("msgContent=========" + msgContent);
			System.out.println("accessToken=========" + accessToken);
			System.out.println("AGEBTID=========" + AGEBTID);
			utilWtdex.postdFollowupOaMsg(userIds,
					"http://172.16.1.219:8080/vds5s1/shop/demo.html?corpId="
							+ corpId + "", "测试消息！！！", msgContent, accessToken,
					AGEBTID);
			// 返回前台json
			response = new VdsResponse();
			response.getDout().getStatus().setResultCode(1);
			response.getDout().getStatus().setResultMsg("发送成功");

			// 拿取返回结果
			String json = response.toString(response);
			jDataOut = JSONObject.fromObject(json);

		} catch (Exception e) {
			e.printStackTrace();
			response = new VdsResponse();
			response.getDout().getStatus().setResultCode(0);
			response.getDout().getStatus().setResultMsg("发送失败");
			return jDataOut;
		}

		return jDataOut;
	}
}
