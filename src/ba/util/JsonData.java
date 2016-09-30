package ba.util;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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
					}else{				
						json.put(temp, result.getString(temp));
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
					}else{				
						json.put(temp, result.getString(temp));
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
	
	/**
	 *   对 json 数据的处理 并且拼接成输入插入语句
	 */
	public String  dealListInsert(String tableName , JSONObject dataIn ){
		
		StringBuffer insert = new StringBuffer("insert into "+tableName+" ( ");
		StringBuffer values = new StringBuffer("'");
		Iterator it = dataIn.entrySet().iterator();
		if( null == it ){
			log4.log.info(" -=-=-=-=-=-=-=-=-=-=-=-dealListInsert dataIn 是空的 -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + insert + "     " + dataIn);
			return null;
		}else{
			while(it.hasNext()){
				Map.Entry m = (Entry) it.next();
				String key = (String) m.getKey();
				String value = (String) m.getValue();
				if(!key.equals("id")){		
					insert.append(key + ",");
					values.append(value + "','");
				}
			}
			//insert.deleteCharAt(insert.length() - 1);
			insert.append(" id ) values(");
			log4.log.info("-=-=-=-=-=-=-=-=-=-=-=-dealListInsert  不是空的，值是： -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + values);
			//String val = values.substring(0, values.length() - 2);
			String id = UUID.randomUUID().toString();
			values.append(id + "' " );
			insert.append(values);
			insert.append(")");
			log4.log.info("-=-=-=-=-=-=-=-=-=-=-=-dealListInsert  不是空的，值是： -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + insert);
		}	
		log4.log.info("-=-=-=-=-=-=-=-=-=-=-=-dealListInsert  不是空的，值是： -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + insert);		
		return insert.toString();
	}
	
	
	
	/**
	 *   对 json 数据的处理 并且拼接成输入更新 语句
	 */
	public String dealListUpdate(String tableName , JSONObject dataIn , String id){
		log4.log.info(" -=-=-=-=-=-=-=-=-=-=-=-dealListUpdate id  -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + id );
		StringBuffer update = new StringBuffer("update " + tableName + " set ");
		if(!"".equals(id)){
			//证明记录存在，更新
			Iterator it = dataIn.entrySet().iterator();
			if( null == it ){
				log4.log.info(" -=-=-=-=-=-=-=-=-=-=-=-dealListUpdate dataIn 是空的 -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + update + "     " + dataIn);
				return null;
			}else{
				while(it.hasNext()){
					Map.Entry m = (Entry) it.next();
					String key = (String) m.getKey();
					String value = (String) m.getValue();
					if(!key.equals("id")){
						update.append(key + " = '" + value + "' , ");
					}		
				}
				update.deleteCharAt(update.length()-2);
				update.append("where id = '" + id + "'");
				log4.log.info("-=-=-=-=-=-=-=-=-=-=-=-dealListUpdate  不是空的，值是： -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + update);
			}	
		}else{
			//记录不存在， 刚刚加进来的，需要插入。
			return dealListInsert(tableName , dataIn);
		}
		log4.log.info("-=-=-=-=-=-=-=-=-=-=-=-dealListUpdate  不是空的，值是： -=-=-=-=-=-=-=-=-=-=-=-=-=-=   " + update);
		return update.toString();
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
