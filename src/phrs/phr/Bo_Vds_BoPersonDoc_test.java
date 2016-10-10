package phrs.phr;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ba.init.VdsResponse;
import ba.base.BoBase;
import ba.util.Log4jUtil;
import ba.util.JsonData;

/**
 * 个人档案 基础资料 、 既往史、家族史
 * 
 * @author Nothingexp
 * 
 */

public class Bo_Vds_BoPersonDoc_test extends BoBase {

	private JdbcTemplate jdbcTemplate;
	private JsonData jsonDate = new JsonData();
	private Log4jUtil log4 = new Log4jUtil();

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 获取个人档案列表 基础资料
	 * 
	 * @param dataIn
	 * @return jDataOut
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject pdSaveData(JSONObject dataIn) {

		JSONObject jDataOut = new JSONObject();

		JSONObject obj = (JSONObject) dataIn.get("din");
		int page = (Integer) obj.get("page");
		int rows = (Integer) obj.get("rows");

		if (page <= 0) {
			page = 1;
		}

		String msg = "";
		String type = "";
		String temp = "";
		int total = 0;
		List<Object> pdSaveDataList = new ArrayList<Object>();

		// 返回前台
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("ok");
		try {
			Connection conn = DataSourceUtils.getConnection(jdbcTemplate
					.getDataSource());
			CallableStatement call = conn
					.prepareCall(" { call proGrdaLbSelect(" + page + " , "
							+ rows + ") } ");
			log4.log.info(" -=-=-=-=-=-=-=-=-=-=-=-=-= call -=-=-=-=-=-=-=-=-:    "
					+ call);
			ResultSet result = call.executeQuery();

			while (result.next()) {
				total = result.getMetaData().getColumnCount();
				JSONObject json = new JSONObject();
				for (int i = 1; i <= total; i++) {
					type = result.getMetaData().getColumnTypeName(i);
					temp = result.getMetaData().getColumnLabel(i);

					if ("DATETIME".equals(type)) {
						Date date = result.getDate(temp);
						if (null == date) {
							json.put(temp, ""); // 格式转换
						} else {
							SimpleDateFormat s = new SimpleDateFormat(
									"yyyy-MM-dd");
							json.put(temp, s.format(result.getDate(temp)));
							// String time = s.format(result.getDate(temp));
							// System.out.println("                " + time);
						}
					} else {
						json.put(temp, result.getString(temp));
					}
				}
				pdSaveDataList.add(json);
			}
			// response.getDout().setTotal(rows);
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("total", rows);
		jDataOut.put("dout", pdSaveDataList);
		// jDataOut.put("states", jsons);
		return jDataOut;
	}

	/**
	 * 根据 个人编号（id） 获取某个个人档案资料 (包括家族史 和 既往史)
	 * 
	 * @param dataIn
	 * @return jDataOut
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject getDataById(JSONObject dataIn) {
		JSONObject jDataOut = new JSONObject();

		JSONObject din = (JSONObject) dataIn.get("din");
		String grbh = (String) din.get("grbh");

		String msg = "";
		ResultSet result = null;

		JSONObject json = new JSONObject();

		// 返回前台
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("ok");

		try {

			// 开启连接
			Connection conn = DataSourceUtils.getConnection(jdbcTemplate
					.getDataSource());

			// 判断改 grbh 是否存在,不存在则不执行后面的操作
			// 访问数据库对应的函数
			CallableStatement call = conn
					.prepareCall("{ call proGrdaGrbhSelect (" + grbh + ") }");
			result = call.executeQuery();

			if (result.next()) {

				// 查询得到，证明是存在该 个人编号
				// 查询 个人档案-基本资料
				call = conn.prepareCall("{ call proGrdaJbzlSelect (" + grbh
						+ ") }");
				json.put("grdajbzl", jsonDate.queryForIf(call));
				// jbzlList = jsonDate.queryForIf(call);

				// 查询完毕 个人档案-基本资料 ，查询 个人档案-既往史
				call = conn.prepareCall("{ call proGrdaJwsSelect (" + grbh
						+ ") }");
				List<Object> jwsList = jsonDate.queryForWhile(call);
				json.put("grdajws", jwsList);
				// response.getDout().setOneTotal(jwsList.size());
				// jwsSum = (Integer) jwsList.remove(1);
				// jbzlList.add(jwsList);

				// 查询完毕 个人档案-基本资料 ，查询 个人档案-家族史
				call = conn.prepareCall("{ call proGrdaJzsSelect (" + grbh
						+ ") }");
				List<Object> jzsList = jsonDate.queryForWhile(call);
				json.put("grdajzs", jzsList);
				// response.getDout().setTwoTotal(jzsList.size());
				// jzsSum = (Integer) jzsList.remove(1);
				// jbzlList.add(jsonDate.queryForWhile(call));
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", json);

		return jDataOut;
	}

	/**
	 * 获取个人档案 根据 grbh：个人编号 grda_xm：姓名 grda_xb：性别 grda_csrq：出生日期 grda_sfzhm：身份证号码
	 * grda_hkdz_jdzmc：户口地址_街道(镇)名称 grda_hkdz_jwcmc：户口地址_居委(村)名称
	 * grda_hkdz_ljmc：户口地址_路_街_名称 grda_hklx：户口类型 grda_brdh：本人电话 grda_jtdh：家庭电话
	 * 查询（ 可选其中的一或多个查询 ）
	 * 
	 * @param dataIn
	 * @return jDataOut
	 * @throws Exception
	 */
	/*
	 * @Transactional(propagation=Propagation.REQUIRED) public JSONObject
	 * getPdList(JSONObject dataIn){ JSONObject jDataOut = new JSONObject();
	 * 
	 * JSONObject din = (JSONObject) dataIn.get("din");
	 * 
	 * //StringBuffer query = new StringBuffer(); List<Object> dealQueryList =
	 * new ArrayList<Object>(); Iterator it = din.entrySet ().iterator();
	 * while(it.hasNext()){ Map.Entry m = (Map.Entry) it.next();
	 * 
	 * String value = (String) m.getValue(); if(null == value ||
	 * "".equals(value)){ value = "%%"; }else{ value = "%"+value+"%"; }
	 * dealQueryList .add(value); //dealQueryList .add(","); }
	 * 
	 * log4.log.info(
	 * " -=-=-=----------------------- 处理过后的参数   query -=-=-=-====================------ "
	 * + dealQueryList);
	 * 
	 * String msg = ""; CallableStatement call = null; ResultSet result = null;
	 * 
	 * JSONObject json = new JSONObject(); VdsResponse response = new
	 * VdsResponse(); try { Connection conn = DataSourceUtils .getConnection
	 * (jdbcTemplate .getDataSource());
	 * 
	 * StringBuffer query = new StringBuffer( "{ call proGrdaLbSelect ( \"'" );
	 * 
	 * for (int i = 0; i < dealQueryList.size(); i++) {
	 * query.append(dealQueryList .get(i)+"'\" " );
	 * 
	 * if( i != dealQueryList.size() - 1 ){ query.append(" , \"'"); }else{
	 * query.append(" )} "); } } log4.log.info(
	 * "______________________  拼接之后的 query 是  ——+——+——+——+——+——+——+——+——" +
	 * query.toString());
	 * 
	 * call = conn.prepareCall(query .toString());
	 * 
	 * List<Object> grdaJbzlList = jsonDate.queryForWhile (call);
	 * json.put("grdaJbzl", grdaJbzlList); //response .getDout().setOneTotal
	 * (grdaJbzlList.size()); }catch (Exception e) { // TODO: handle exception
	 * msg = e.getMessage(); jDataOut. put("resultCode", -99); jDataOut
	 * .put("resultMsg", null); jDataOut.put("fail", msg); e.printStackTrace();
	 * }
	 * 
	 * //返回前台 response.getDout() .getStatus ().setResultCode(1); response
	 * .getDout().getStatus ().setResultMsg("ok");
	 * 
	 * // 拿取返回结果 String jsons = response .toString(response); jDataOut =
	 * JSONObject.fromObject (jsons); //JSONObject d = JSONObject.fromObject(
	 * jsons); jDataOut.put("dout" , json);
	 * 
	 * return jDataOut; }
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject getPdList(JSONObject dataIn) {

		JSONObject jDataOut = new JSONObject();

		JSONObject din = (JSONObject) dataIn.get("din");
		int page = (Integer) din.get("page");
		int rows = (Integer) din.get("rows");
		String condition = (String) din.get("condition");

		if (page <= 0) {
			page = 1;
		}

		String msg = "";
		String type = "";
		String temp = "";
		int total = 0;
		List<Object> pdSaveDataList = new ArrayList<Object>();

		// 返回前台
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("ok");
		try {
			Connection conn = DataSourceUtils.getConnection(jdbcTemplate
					.getDataSource());
			CallableStatement call = conn
					.prepareCall(" { call proGrdaLbByConditionSelect(\" "
							+ condition + " \" ," + page + " , " + rows
							+ ") } ");
			log4.log.info(" -=-=-=-=-=-=-=-=-=-=-=-=-= call -=-=-=-=-=-=-=-=-:    "
					+ call);
			ResultSet result = call.executeQuery();

			while (result.next()) {
				total = result.getMetaData().getColumnCount();
				JSONObject json = new JSONObject();
				for (int i = 1; i <= total; i++) {
					type = result.getMetaData().getColumnTypeName(i);
					temp = result.getMetaData().getColumnLabel(i);

					if ("DATETIME".equals(type)) {
						Date date = result.getDate(temp);
						if (null == date) {
							json.put(temp, ""); // 格式转换
						} else {
							SimpleDateFormat s = new SimpleDateFormat(
									"yyyy-MM-dd");
							json.put(temp, s.format(result.getDate(temp)));
						}
					} else {
						json.put(temp, result.getString(temp));
					}
				}
				pdSaveDataList.add(json);
			}

			response.getDout().getStatus().setResultCode(1);
			response.getDout().getStatus().setResultMsg("ok");
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", pdSaveDataList);
		// jDataOut.put("states", jsons);
		return jDataOut;
	}

	/*	*//**
	 * 个人档案保存按钮 （ 包括 更新 、 保存 ）
	 * 
	 * @param dataIn
	 * @return jDataOut
	 * @throws Exception
	 *             SQLException
	 */
	/*
	 * public JSONObject savePd(JSONObject dataIn) throws SQLException {
	 * 
	 * JSONObject jDataOut = new JSONObject();
	 * 
	 * JSONObject din = (JSONObject) dataIn.get("din"); JSONObject jsonJbzl =
	 * (JSONObject) din.get("grdaJbzl"); String id = (String)
	 * jsonJbzl.get("id");
	 * 
	 * String msg = ""; CallableStatement call = null; ResultSet result = null;
	 * String sql = ""; int i = 0; Connection conn =
	 * DataSourceUtils.getConnection(jdbcTemplate .getDataSource());
	 * 
	 * VdsResponse response = new VdsResponse();
	 * response.getDout().getStatus().setResultCode(1);
	 * response.getDout().getStatus().setResultMsg("ok"); try {
	 * 
	 * if (null != id) { // 判断这个人的 ID 是否存在 call =
	 * conn.prepareCall("{call proGrdaGrIdSelect ( \"'" + id + "'\" ) }");
	 * result = call.executeQuery(); if (result.next()) { // 有Id 并且查到此人存在
	 * UpdatePdData(dataIn); } else { // 有Id 但是查无此人
	 * 
	 * } } else { // id 为空 前台没有数据 ，就是无此人记录 savePdData(dataIn); }
	 * 
	 * } catch (Exception e) { // TODO: handle exception conn.rollback(); msg =
	 * e.getMessage(); response.getDout().getStatus().setResultCode(-99);
	 * response.getDout().getStatus().setResultMsg(msg); e.printStackTrace(); }
	 * 
	 * // 拿取返回结果 String jsons = response.toString(response); jDataOut =
	 * JSONObject.fromObject(jsons); jDataOut.put("dout", ""); return jDataOut;
	 * }
	 */

	/**
	 * 个人档案保存 （ 包括 家族史 、 基础资料 、 既往史 ） resultCode : 1: 全部插入成功 -100001 ： 改用户已经存在
	 * -100002：基础资料插入失败 -100003 :有 既往史插入失败 -100004 ： 有家族史插入失败
	 * 
	 * @param dataIn
	 * @return jDataOut
	 * @throws Exception
	 *             SQLException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject savePdData(JSONObject dataIn) throws SQLException {
		JSONObject jDataOut = new JSONObject();

		JSONObject din = (JSONObject) dataIn.get("din");

		JSONObject jsonJbzl = (JSONObject) din.get("grdaJbzl");
		JSONArray jsonJws = (JSONArray) din.get("grdaJws");
		JSONArray jsonJzs = (JSONArray) din.get("grdaJzs");

		String msg = "";
		CallableStatement call = null;
		ResultSet result = null;
		String sql = "";
		int i = 0;
		Connection conn = DataSourceUtils.getConnection(jdbcTemplate
				.getDataSource());
		boolean flag = false;

		VdsResponse response = new VdsResponse();
		// 返回前台
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("保存成功！");
		try {

			if (null != jsonJbzl) {
				String id = (String) jsonJbzl.get("id");
				System.out.println("　　id==null　" + (null == id) + "      "
						+ ("".equals(id)) + "   1" + id + "2");
				if (!("".equals(id))) {
					/*
					 * call = conn.prepareCall("{call proGrdaGrIdSelect ( \"'" +
					 * id + "'\" ) }"); result = call.executeQuery();
					 */
					// 该用户已经存在
					response.getDout().getStatus().setResultCode(-100001);
					response.getDout().getStatus().setResultMsg("改用户已经存在");
				} else {
					// 执行 个人档案 的 基础资料的 插入
					sql = jsonDate.dealListInsert("phr_grda_jbzl", jsonJbzl);
					log4.log.info(" -=-=-=-=-=- -=-=-=-= savePdData 方法里面  执行的  jsonJbzl sql 为：  -=-=-=-= - -=-=-=-=-="
							+ sql);
					i = jdbcTemplate.update(sql);
					if (i < 0) {
						// 证明插入不成功
						conn.rollback();
						response.getDout().getStatus().setResultCode(-100002);
						response.getDout().getStatus()
								.setResultMsg(" 基础资料插入失败   ");
					} else {
						// 插入成功，插入既往史
						if (null != jsonJws) {
							log4.log.info(" -=-=-=-=-=- -=-=-=-= savePdData 方法里面  执行的  jsonJws 的长度  为：  -=-=-=-= - -=-=-=-=-="
									+ jsonJws.size());
							for (int j = 0; j < jsonJws.size(); j++) {
								JSONObject jsonJwsOne = (JSONObject) jsonJws
										.get(j);
								sql = jsonDate.dealListInsert("phr_grda_jws",
										jsonJwsOne);
								log4.log.info(" -=-=-=-=-=- -=-=-=-= savePdData 方法里面  执行的  jsonJws sql 为：  -=-=-=-= - -=-=-=-=-="
										+ sql);
								i = jdbcTemplate.update(sql);
								if (i < 0) {
									// 证明插入不成功
									conn.rollback();
									response.getDout().getStatus()
											.setResultCode(-100003);
									response.getDout().getStatus()
											.setResultMsg(" 有 既往史 插入失败   ");
									flag = true;
									break;
								}
							}

							if (null != jsonJzs && !flag) {
								// 插入成功 插入家族史
								for (int k = 0; k < jsonJzs.size(); k++) {
									JSONObject jsonJzsOne = (JSONObject) jsonJzs
											.get(k);
									sql = jsonDate.dealListInsert(
											"phr_grda_jzs", jsonJzsOne);
									log4.log.info(" -=-=-=-=-=- -=-=-=-= savePdData 方法里面  执行的  jsonJws sql 为：  -=-=-=-= - -=-=-=-=-="
											+ sql);
									i = jdbcTemplate.update(sql);
									if (i < 0) {
										// 证明插入不成功
										conn.rollback();
										response.getDout().getStatus()
												.setResultCode(-100004);
										response.getDout().getStatus()
												.setResultMsg(" 有 家族史插入失败   ");
										flag = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			conn.rollback();
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", "");
		return jDataOut;
	}

	/**
	 * 个人档案 更新（ 包括 家族史 、 基础资料 、 既往史 ） resultCode :-100008 : 该用户不存在
	 * -100005：基础资料更新失败 -100006 :有 既往史更新失败 -100007 ： 有家族史更新失败 -100014 : grdaJbzl
	 * 列表为空
	 * 
	 * @param dataIn
	 * @return jDataOut
	 * @throws Exception
	 *             SQLException
	 */
	
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject UpdatePdData(JSONObject dataIn) throws SQLException {
		JSONObject jDataOut = new JSONObject();

		JSONObject din = (JSONObject) dataIn.get("din");

		JSONObject jsonJbzl = (JSONObject) din.get("grdaJbzl");
		JSONArray jsonJws = (JSONArray) din.get("grdaJws");
		JSONArray jsonJzs = (JSONArray) din.get("grdaJzs");

		String id = "";
		String msg = "";
		CallableStatement call = null;
		ResultSet result = null;
		String sql = "";
		int i = 0;
		Connection conn = DataSourceUtils.getConnection(jdbcTemplate
				.getDataSource());
		boolean flag = false;

		VdsResponse response = new VdsResponse();
		// 返回前台
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("更新成功！");
		try {
			if (null != jsonJbzl) {
				id = (String) jsonJbzl.get("id");
				call = conn
						.prepareCall("{call proGrdaByIdSelect (   'phr_grda_jbzl'   ,  \"'"
								+ id + "'\" ) }");
				result = call.executeQuery();
				if (result.next()) {
					// 执行 个人档案 的 基础资料的 更新
					String jbzlId = (String) jsonJbzl.get("id");
					sql = jsonDate.dealListUpdate("phr_grda_jbzl", jsonJbzl,
							jbzlId);
					log4.log.info(" -=-=-=-=-=- -=-=-=-= UpdatePdData 方法里面  执行的  jsonJbzl sql 为：  -=-=-=-= - -=-=-=-=-="
							+ sql);
					i = jdbcTemplate.update(sql);
					if (i < 0) {
						// 证明更新不成功
						conn.rollback();
						response.getDout().getStatus().setResultCode(-100005);
						response.getDout().getStatus()
								.setResultMsg(" 基础资料更新失败   ");
					} else {
						// 更新成功，更新既往史
						if (null != jsonJws) {
							log4.log.info(" -=-=-=-=-=- -=-=-=-= UpdatePdData 方法里面  执行的  jsonJws 的长度  为：  -=-=-=-= - -=-=-=-=-="
									+ jsonJws.size());
							for (int j = 0; j < jsonJws.size(); j++) {
								JSONObject jsonJwsOne = (JSONObject) jsonJws
										.get(j);
								String jwsId = (String) jsonJwsOne.get("id");
								sql = jsonDate.dealListUpdate("phr_grda_jws",
										jsonJwsOne, jwsId);
								log4.log.info(" -=-=-=-=-=- -=-=-=-= UpdatePdData 方法里面  执行的  jsonJws sql 为：  -=-=-=-= - -=-=-=-=-="
										+ sql);
								i = jdbcTemplate.update(sql);
								if (i < 0) {
									// 证明插入不成功
									conn.rollback();
									response.getDout().getStatus()
											.setResultCode(-100006);
									response.getDout().getStatus()
											.setResultMsg(" 有 既往史 更新 失败   ");
									flag = true;
									break;
								}
							}

							if (null != jsonJzs && !flag) {
								// 插入成功 插入家族史
								for (int k = 0; k < jsonJzs.size(); k++) {
									JSONObject jsonJzsOne = (JSONObject) jsonJzs
											.get(k);
									String jzsId = (String) jsonJzsOne
											.get("id");
									sql = jsonDate.dealListUpdate(
											"phr_grda_jzs", jsonJzsOne, jzsId);
									log4.log.info(" -=-=-=-=-=- -=-=-=-= UpdatePdData 方法里面  执行的  jsonJws sql 为：  -=-=-=-= - -=-=-=-=-="
											+ sql);
									i = jdbcTemplate.update(sql);
									if (i < 0) {
										// 证明插入不成功
										conn.rollback();
										response.getDout().getStatus()
												.setResultCode(-100007);
										response.getDout().getStatus()
												.setResultMsg(" 有 家族史插入失败   ");
										flag = true;
										break;
									}
								}
							}
						}
					}
				} else {
					// 该 用户不 存在
					response.getDout().getStatus().setResultCode(-100008);
					response.getDout().getStatus().setResultMsg("改用户不存在");
				}
			} else {
				response.getDout().getStatus().setResultCode(-100014);
				response.getDout().getStatus().setResultMsg(" grdaJbzl 列表为空 ！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			conn.rollback();
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", "");

		return jDataOut;
	}
	
	
	

	/**
	 * 删除个人档案 ResultCode： -100010 删除个人档案失败！ 1 ： 删除个人档案成功！
	 * 
	 * @throws SQLException
	 */
	private JSONObject deletePdGrzlData(String id) throws SQLException {
		JSONObject jDataOut = new JSONObject();

		String msg = "";
		CallableStatement call = null;
		ResultSet result = null;
		String sql = "";
		int i = 0;

		Connection conn = DataSourceUtils.getConnection(jdbcTemplate
				.getDataSource());
		VdsResponse response = new VdsResponse();
		// 返回前台
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("删除成功！");
		try {

			if (!"".equals(id)) {
				sql = "update phr_grda_jbzl set zfbj = 1 where id = ?";

				i = jdbcTemplate.update(sql, id);

				if (i < 0) {
					response.getDout().getStatus().setResultCode(-100010);
					response.getDout().getStatus().setResultMsg("删除个人档案失败！");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			conn.rollback();
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", "");
		return jDataOut;
	}

	/**
	 * 删除个人档案 ResultCode： -100010 删除个人档案失败！ -100011 ： 标记既往史失败！ -100012 标记家族史失败！
	 * 1 ： 删除个人档案成功！
	 * 
	 * @throws SQLException
	 */
	/*
	 * public JSONObject deletePdGrzlData(JSONObject dataIn) throws SQLException
	 * { JSONObject jDataOut = new JSONObject();
	 * 
	 * JSONObject din = (JSONObject) dataIn.get("din"); String id = (String)
	 * din.get("id");
	 * 
	 * String msg = ""; CallableStatement call = null; ResultSet result = null;
	 * String sql = ""; String grbh = ""; int i = 0;
	 * 
	 * Connection conn = DataSourceUtils.getConnection(jdbcTemplate
	 * .getDataSource()); VdsResponse response = new VdsResponse(); // 返回前台
	 * response.getDout().getStatus().setResultCode(1);
	 * response.getDout().getStatus().setResultMsg("删除成功！"); try {
	 * 
	 * if (!"".equals(id)) { call = conn
	 * .prepareCall("{call proGrdaByIdSelect (   'phr_grda_jbzl'   ,  \"'" + id
	 * + "'\" ) }"); result = call.executeQuery(); if (result.next()) { sql =
	 * "delete  from phr_grda_jbzl where id = ?";
	 * 
	 * i = jdbcTemplate.update(sql, id);
	 * 
	 * if (i < 0) { response.getDout().getStatus().setResultCode(-100010);
	 * response.getDout().getStatus() .setResultMsg("删除个人档案失败！"); } else { //
	 * 需要标记相关表数据 grbh = result.getString("grbh");
	 * 
	 * // 修改个人编号对应的家族史表 sql = "update phr_grda_jzs set Zfbj = 1 where grbh = ?";
	 * i = jdbcTemplate.update(sql, grbh);
	 * 
	 * if (i < 0) { conn.rollback(); response.getDout().getStatus()
	 * .setResultCode(-100012); response.getDout().getStatus()
	 * .setResultMsg("标记家族史失败！"); } else { // 修改个人编号对应的既往史表 sql =
	 * "update phr_grda_jws set Zfbj = 1 where grbh = ?"; i =
	 * jdbcTemplate.update(sql, grbh);
	 * 
	 * if (i < 0) { conn.rollback(); response.getDout().getStatus()
	 * .setResultCode(-100011); response.getDout().getStatus()
	 * .setResultMsg("标记既往史失败！"); } else {
	 * response.getDout().getStatus().setResultCode(1);
	 * response.getDout().getStatus() .setResultMsg("删除个人档案成功！"); } } } } } }
	 * catch (Exception e) { // TODO: handle exception conn.rollback(); msg =
	 * e.getMessage(); response.getDout().getStatus().setResultCode(-99);
	 * response.getDout().getStatus().setResultMsg(msg); e.printStackTrace(); }
	 * 
	 * // 拿取返回结果 String jsons = response.toString(response); jDataOut =
	 * JSONObject.fromObject(jsons); // JSONObject d =
	 * JSONObject.fromObject(jsons); jDataOut.put("dout", ""); return jDataOut;
	 * }
	 */

	/**
	 * 删除个人档案 (家族史) ResultCode ： -100008 删除家族史失败！
	 * 
	 * @throws SQLException
	 */
	// public JSONObject deletePdJzsData(JSONObject dataIn) throws SQLException
	// {
	// JSONObject jDataOut = new JSONObject();
	//
	// JSONObject din = (JSONObject) dataIn.get("din");
	// String id = (String) din.get("id");
	//
	// String msg = "";
	// CallableStatement call = null;
	// ResultSet result = null;
	// String sql = "";
	//
	// Connection conn = DataSourceUtils.getConnection(jdbcTemplate
	// .getDataSource());
	// VdsResponse response = new VdsResponse();
	//
	// try {
	//
	// if (!"".equals(id)) {
	//
	// call = conn
	// .prepareCall("{call proGrdaByIdSelect ( 'phr_grda_jzs' ,  \"'"
	// + id + "'\" ) }");
	// result = call.executeQuery();
	//
	// if (result.next()) {
	// sql = "delete from phr_grda_jzs where id = ?";
	//
	// int i = jdbcTemplate.update(sql, id);
	//
	// if (i < 0) {
	// response.getDout().getStatus().setResultCode(-100008);
	// response.getDout().getStatus().setResultMsg("删除失败！");
	// } else {
	// // 返回前台
	// response.getDout().getStatus().setResultCode(1);
	// response.getDout().getStatus().setResultMsg("删除成功！");
	// }
	// }
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// conn.rollback();
	// msg = e.getMessage();
	// response.getDout().getStatus().setResultCode(-99);
	// response.getDout().getStatus().setResultMsg(msg);
	// e.printStackTrace();
	// }
	//
	// // 拿取返回结果
	// String jsons = response.toString(response);
	// jDataOut = JSONObject.fromObject(jsons);
	// // JSONObject d = JSONObject.fromObject(jsons);
	// jDataOut.put("dout", "");
	// return jDataOut;
	// }

	/**
	 * 删除个人档案 (既往史) ResultCode ： -100009 删除既往史失败！
	 * 
	 * @throws SQLException
	 */
	public JSONObject deletePdJwsData(String grbh) throws SQLException {
		JSONObject jDataOut = new JSONObject();

		String msg = "";
		String sql = "";

		Connection conn = DataSourceUtils.getConnection(jdbcTemplate
				.getDataSource());
		VdsResponse response = new VdsResponse();

		try {

			if (!"".equals(grbh)) {
				sql = "update phr_grda_jws set Zfbj = 1 where grbh = ?";

				int i = jdbcTemplate.update(sql, grbh);

				if (i < 0) {
					response.getDout().getStatus().setResultCode(-100009);
					response.getDout().getStatus().setResultMsg("删除既往史失败！");
				} else {
					// 返回前台
					response.getDout().getStatus().setResultCode(1);
					response.getDout().getStatus().setResultMsg("删除成功！");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			conn.rollback();
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", "");
		return jDataOut;
	}

	// public JSONObject deletePdJwsData(JSONObject dataIn) throws SQLException
	// {
	// JSONObject jDataOut = new JSONObject();
	//
	// JSONObject din = (JSONObject) dataIn.get("din");
	// String id = (String) din.get("id");
	//
	// String msg = "";
	// CallableStatement call = null;
	// ResultSet result = null;
	// String sql = "";
	//
	// Connection conn = DataSourceUtils.getConnection(jdbcTemplate
	// .getDataSource());
	// VdsResponse response = new VdsResponse();
	//
	// try {
	//
	// if (!"".equals(id)) {
	//
	// call = conn
	// .prepareCall("{call proGrdaByIdSelect (   'phr_grda_jws'   ,  \"'"
	// + id + "'\" ) }");
	// result = call.executeQuery();
	//
	// if (result.next()) {
	// sql = "delete from phr_grda_jws where id = ?";
	//
	// int i = jdbcTemplate.update(sql, id);
	//
	// if (i < 0) {
	// response.getDout().getStatus().setResultCode(-100009);
	// response.getDout().getStatus().setResultMsg("删除既往史失败！");
	// } else {
	// // 返回前台
	// response.getDout().getStatus().setResultCode(1);
	// response.getDout().getStatus().setResultMsg("删除成功！");
	// }
	// }
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// conn.rollback();
	// msg = e.getMessage();
	// response.getDout().getStatus().setResultCode(-99);
	// response.getDout().getStatus().setResultMsg(msg);
	// e.printStackTrace();
	// }
	//
	// // 拿取返回结果
	// String jsons = response.toString(response);
	// jDataOut = JSONObject.fromObject(jsons);
	// // JSONObject d = JSONObject.fromObject(jsons);
	// jDataOut.put("dout", "");
	// return jDataOut;
	// }

	/**
	 * 个人档案 更新（ 包括 健康健康状况记录 、 健康状况记录_住院治疗情况 、 健康状况记录_主要用药情况 、 健康状况记录_非免疫规划预防接种史
	 * 、 健康状况评价表 ） resultCode : -100013 查询不到该 id -100015 ： grdaJkzk 列表为空 ！
	 * -100016 ： 跟新 phr_grda_jkzk 失败
	 */
	public JSONObject heathUpdateData(JSONObject dataIn) throws SQLException {
		JSONObject jDataOut = new JSONObject();

		JSONObject din = (JSONObject) dataIn.get("din");
		JSONObject jsonJkzk = (JSONObject) din.get("grdaJkzk");
		JSONArray jsonZyzlqk = (JSONArray) din.get("grdaZyzlqk");
		JSONArray jsonZyyyqk = (JSONArray) din.get("grdaZyyyqk");
		JSONArray jsonFmyjzs = (JSONArray) din.get("grdaFmyjzs");
		JSONArray jsonJkzkpjb = (JSONArray) din.get("grdaJkzkpjb");

		String jkzkId = "";

		String msg = "";
		CallableStatement call = null;
		ResultSet result = null;
		String sql = "";
		int i = 0;

		Connection conn = DataSourceUtils.getConnection(jdbcTemplate
				.getDataSource());
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg(" 成功更新  ");
		try {
			if (null != jsonJkzk) {

				jkzkId = (String) jsonJkzk.get("id");

				call = conn
						.prepareCall("{  call proGrdaByIdSelect( 'phr_grda_jkzk' , \"'"
								+ jkzkId + "'\" )  }");

				result = call.executeQuery();

				if (result.next()) {
					// 执行 健康健康状况记录 的跟新
					sql = jsonDate.dealListUpdate("phr_grda_jkzk", jsonJkzk,
							jkzkId);

					i = jdbcTemplate.update(sql);
					if (i > 0) {

						// 执行 健康状况记录_住院治疗情况
						if (null != jsonZyzlqk) {
							for (int j = 0; j < jsonZyzlqk.size(); j++) {
								JSONObject jsonZyzlqkOne = (JSONObject) jsonZyzlqk
										.get(i);
								String zyzlqkId = (String) jsonZyzlqkOne
										.get("id");

								if (null != zyzlqkId) {
									sql = jsonDate.dealListUpdate(
											"phr_grda_jkzk_zyzlqk",
											jsonZyzlqkOne, zyzlqkId);

								}

							}
						}
					} else {
						conn.rollback();
						response.getDout().getStatus().setResultCode(-100016);
						response.getDout().getStatus()
								.setResultMsg(" 跟新 phr_grda_jkzk 失败 ");
					}
				} else {
					response.getDout().getStatus().setResultCode(-100013);
					response.getDout().getStatus().setResultMsg(" 查询不到该 id ");
				}
			} else {
				response.getDout().getStatus().setResultCode(-100015);
				response.getDout().getStatus().setResultMsg(" grdaJkzk 列表为空 ！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			conn.rollback();
			msg = e.getMessage();
			response.getDout().getStatus().setResultCode(-99);
			response.getDout().getStatus().setResultMsg(msg);
			e.printStackTrace();
		}

		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		// JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("dout", "");
		return jDataOut;
	}

}
