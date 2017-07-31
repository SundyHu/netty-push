package com.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.util.SerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class U8JdbcUtil {
	static Logger logger = Logger.getLogger(U8JdbcUtil.class);
	private static ComboPooledDataSource ds = new ComboPooledDataSource("U8sql");
	private static final ThreadLocal<Connection> connContainer=new ThreadLocal<>();
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			 // 先从 ThreadLocal 中获取 Connection
			conn = connContainer.get();
			if (conn == null) {
			    // 若不存在，则从 DataSource 中获取 Connection
			    conn = ds.getConnection();
			    // 将 Connection 放入 ThreadLocal 中
			    if (conn != null) {
			        connContainer.set(conn);
			    }
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("获取数据库连接失败-------"+e);
		}
		return conn;
	}

	public static String excuteQuery(Connection con,String sql){
		PreparedStatement stmt=null;
		ResultSet rs =null;
		ResultSetMetaData m=null;
		List<Map<String, Object>> list=new LinkedList<Map<String,Object>>();
		try {
			stmt=con.prepareStatement(sql);
			rs = stmt.executeQuery();
			m=rs.getMetaData();
			int columns=m.getColumnCount();
            while(rs.next()){ 
            	Map<String, Object> map=new HashMap<String, Object>();
            	for (int i = 1; i <=columns; i++) {
            		map.put(m.getColumnName(i), rs.getString(i)==null?"":rs.getString(i));
				}
            	list.add(map);
            }
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(sql+"数据库查询数据失败---------"+e);
		}finally{
			if(m!=null){
				m=null;
			}
			releaseSouce(con, stmt, rs);
		}
		return SerUtil.serializeArray(list);
	}
	
	public static void releaseSouce(Connection conn, Statement stmt,ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			connContainer.remove();
		}
	}
}