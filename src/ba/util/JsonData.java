package ba.util;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ba.util.Bo_vds_log4jUtil;

import net.sf.json.JSONObject;

public class JsonData {
	private Bo_vds_log4jUtil log4 = new Bo_vds_log4jUtil();

	/**
	 * 查询列表数据 ， 返回 一个装有 json格式 的集合
	 * 
	 * @throws SQLException
	 */
	public List<Object> queryForIf(CallableStatement call) throws SQLException {
		List<Object> list = new ArrayList<Object>();

		String msg = "";
		int total = 0;
		String type = "";
		String temp = "";
		ResultSet result = null;

		if (null == call) {
			log4.log.info(" call 为空：   " + call);
		} else {
			result = call.executeQuery();

			if (result.next()) {
				total = result.getMetaData().getColumnCount();
				JSONObject json = new JSONObject();
				for (int i = 1; i <= total; i++) {
					type = result.getMetaData().getColumnTypeName(i);
					temp = result.getMetaData().getColumnLabel(i);

					if ("DATETIME".equals(type)) {
						Date date = result.getDate(temp);
						if (null == date) {
							json.put(temp, "");
						} else {
							SimpleDateFormat s = new SimpleDateFormat(
									"yyyy-MM-dd");
							json.put(temp, s.format(result.getDate(temp)));
						}
					}
				}
				list.add(json);
				log4.log.info(" list-=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + list);
			} else {
				// 如果查询不到
				log4.log.info(" 查询不到：-=-=-=-=-=-=-=-=-=-=-=-=-=-= list  "
						+ list);
			}
		}
		log4.log.info(" list-=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + list);
		return list;
	}

	/**
	 * 查询列表数据 ， 返回 一个装有 json格式 的集合
	 * 
	 * @throws SQLException
	 */
	public List<Object> queryForWhile(CallableStatement call)
			throws SQLException {
		List<Object> list = new ArrayList<Object>();

		String msg = "";
		int total = 0;
		String type = "";
		String temp = "";
		ResultSet result = null;
		// int sum = 0;
		if (null == call) {
			log4.log.info(" call 为空：   " + call);
		} else {
			result = call.executeQuery();

			while (result.next()) {
				// sum++;
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
					}
				}
				list.add(json);
				log4.log.info(" list-=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + list);
			}
			// list.add(sum);
		}
		log4.log.info(" list-=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + list);
		return list;
	}

}
