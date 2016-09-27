package ba.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.Priority;

public class Bo_vds_log4jUtil extends DailyRollingFileAppender{

	// 存储编码格式信息
	private String encode = null;
	/**
	 * 读取配置文件
	 */

	// transient : 这个字段的生命周期仅存于调用者的内存中而不会写到磁盘里持久化
	public transient static final Log log = LogFactory
			.getLog(Bo_vds_log4jUtil.class);
	static {
		try {
			init();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	static void init() {
		final Properties pro = new Properties();
		try {
			//读取配置文件
			pro.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("log4j.properties"));
		} catch (FileNotFoundException e) {
			log.error("File not found" + e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		// Priority 就是从配置文件中获得的 ，该方法我们不必去调用就能够使用
		// 只判断是否相等，而不判断优先级
		return this.getThreshold().equals(priority);
	}
}
