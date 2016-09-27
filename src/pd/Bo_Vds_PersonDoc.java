package pd;

import net.sf.json.JSONObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.ba.init.VdsResponse;



import ba.base.BoBase;
import ba.util.JsonData;


/**
 *  个人档案
 * @author Nothingexp
 *
 */
public class Bo_Vds_PersonDoc extends BoBase{
	
	private JdbcTemplate jdbcTemplate;
	private JsonData jsonDate = new JsonData();
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	/**
	 *    获取个人档案列表
	 */
	public JSONObject pdSaveData(JSONObject dataIn){
		
		JSONObject jDataOut = new JSONObject();
		
		JSONObject obj = (JSONObject) dataIn.get("din");
		int page = (Integer) obj.get("page");
		int rows = (Integer) obj.get("rows");
		
		if( page <= 0 ){
			page = 1;	
		}
		
	    String msg = "";
	    String type = "";
	    String temp = "";
		int total = 0;
		List<Object> pdSaveDataList = new ArrayList<Object>();
		
		try {
			Connection conn = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
			CallableStatement call = conn.prepareCall(" { call proGrdaLbSelect("+ page +" , "+rows +") } ");
			
			ResultSet result = call.executeQuery();
			
			while(result.next()){
				total = result.getMetaData().getColumnCount();
				JSONObject json = new JSONObject();
				for (int i = 1; i <= total; i++) {
					type = result.getMetaData().getColumnTypeName(i);
					temp = result.getMetaData().getColumnLabel(i);
					
					if("DATETIME".equals(type)){
						Date date = result.getDate(temp);
						if(null == date){
							json.put(temp, ""); //格式转换
						}else{
							SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
							json.put(temp, s.format(result.getDate(temp)));
						}
					}
				}
				pdSaveDataList.add(json);
			}	
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			jDataOut.put("resultCode", -99);
			jDataOut.put("resultMsg", null);
			jDataOut.put("fail", msg);
			e.printStackTrace();
		}	
		
		//返回前台
		VdsResponse response = new VdsResponse();
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("ok");
		
		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		//JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("total", rows);
		jDataOut.put("dout" , pdSaveDataList);
		//jDataOut.put("states", jsons);		
		return jDataOut;
	}
	
	/**
	 *    根据id获取某个个人档案资料
	 */
	public JSONObject GetDataById(JSONObject dataIn){
		VdsResponse response = new VdsResponse();
		JSONObject jDataOut = new JSONObject();
		
		JSONObject din = (JSONObject) dataIn.get("din");
		String grbh = (String) din.get("grbh");
		
		String msg = "";
		ResultSet result = null;
		//定义  既往史 的集合总数
		int jwsSum = 0;
		//定义  家族史 的集合总数
		int jzsSum = 0;
		//定义 个人资料的 存储集合
		List<Object> jbzlList = new ArrayList<Object>();
		
		JSONObject json = new JSONObject();
		try {
			
			//开启连接
			Connection conn = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
			
			//判断改  grbh 是否存在,不存在则不执行后面的操作
			//访问数据库对应的函数
			CallableStatement call = conn.prepareCall("{ call proGrdaGrbhSelect ("+grbh+")  }");
			result = call.executeQuery();
			
			if(result.next()){
				
				//查询得到，证明是存在该 个人编号 
				//查询 个人档案-基本资料
				call = conn.prepareCall("{ call proGrdaJbzlSelect ("+grbh+") }");
				json.put("grdajbzl", jsonDate.queryForIf(call));
				//jbzlList = jsonDate.queryForIf(call);
					
				//查询完毕  个人档案-基本资料 ，查询  个人档案-既往史
				call = conn.prepareCall("{ call proGrdaJwsSelect ("+grbh+") }");
				List<Object> jwsList = jsonDate.queryForWhile(call);
				json.put("grdajws", jwsList);
				response.getDout().setOneTotal(jwsList.size());
				//jwsSum =  (Integer) jwsList.remove(1);
				//jbzlList.add(jwsList);
				
				//查询完毕  个人档案-基本资料 ，查询  个人档案-家族史
				call = conn.prepareCall("{ call proGrdaJzsSelect ("+grbh+") }");
				List<Object> jzsList = jsonDate.queryForWhile(call);
				json.put("grdajzs", jzsList);
				response.getDout().setTwoTotal(jzsList.size());
				//jzsSum =  (Integer) jzsList.remove(1);
				//jbzlList.add(jsonDate.queryForWhile(call));
			}	
		} catch (Exception e) {
			// TODO: handle exception
			msg = e.getMessage();
			jDataOut.put("resultCode", -99);
			jDataOut.put("resultMsg", null);
			jDataOut.put("fail", msg);
			e.printStackTrace();
		}
		
		//返回前台
		
		response.getDout().getStatus().setResultCode(1);
		response.getDout().getStatus().setResultMsg("ok");
				
		// 拿取返回结果
		String jsons = response.toString(response);
		jDataOut = JSONObject.fromObject(jsons);
		//JSONObject d = JSONObject.fromObject(jsons);
		jDataOut.put("jwsSum", jwsSum);
		jDataOut.put("jzsSum", jzsSum);
		jDataOut.put("dout" , json);
		
		return jDataOut;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
