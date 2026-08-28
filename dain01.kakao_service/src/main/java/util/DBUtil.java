package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbcp2.BasicDataSource;


public class DBUtil {
	public static Connection getDBConnection(String db_driver, String db_url, String db_user, String db_password)
			throws Exception {
		Class.forName(db_driver);
		return DriverManager.getConnection(db_url, db_user, db_password);
	}

	private static Map<String, BasicDataSource> DS_MAP = new HashMap<String, BasicDataSource>();

	public synchronized static Connection getDBPConnection(String id, String db_driver, String db_url, String db_user,
			String db_password) throws Exception {
		BasicDataSource ds = DS_MAP.get(id);
		if (ds == null) {
			ds = new BasicDataSource();

			ds.setDriverClassName(db_driver);
			ds.setUrl(db_url);
			ds.setUsername(db_user);
			ds.setPassword(db_password);
			ds.setMinIdle(1);
			ds.setMaxIdle(5);
			ds.setMaxTotal(20);
			ds.setMaxOpenPreparedStatements(10);
			ds.setDefaultAutoCommit(false);

			DS_MAP.put(id, ds);
		}
		Connection conn = ds.getConnection();

		conn.setAutoCommit(false);
		return conn;
	}

	public static boolean execute(Connection conn, String sql) {
		boolean ret = false;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			ret = pstmt.execute();
			conn.commit();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			close(pstmt);
		}
		return ret;
	}

	public static int executeUpdate(Connection conn, String sql) {
		int ret = -1;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			ret = pstmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			close(pstmt);
		}
		return ret;
	}

	public static void commit(Connection obj) {
		if (obj != null)
			try {
				obj.commit();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
	}

	public static void rollback(Connection obj) {
		if (obj != null)
			try {
				obj.rollback();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
	}

	public static void close(Connection obj) {
		rollback(obj);
		if (obj != null)
			try {
				obj.setAutoCommit(false);
				obj.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		obj = null;
	}

	public static void close(Statement obj) {
		if (obj != null)
			try {
				obj.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		obj = null;
	}

	public static void close(ResultSet obj) {
		if (obj != null)
			try {
				obj.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		obj = null;
	}

	public static void insertToDB(int cnt, String title, String stats) {
		// 데이터 처리 건수가 있는 경우
		if (cnt > 0) {
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				conn = RvmsUtil.getDBConnection();

				String seqSql = "SELECT SEQ_DAEMON_LOG.NEXTVAL FROM dual";
				ps = conn.prepareStatement(seqSql);
				rs = ps.executeQuery();
				rs.next();
				long seq = rs.getLong(1);
				rs.close();
				ps.close();

				String sql = " INSERT INTO T_DAEMON_LOG(F_DAEMON_SEQ,F_DAEMON_DIV,F_DAEMON_STATUS,F_DAEMON_CNT,F_DAEMON_TOT) "
						+ " VALUES(?,?,?,?,?)";

				ps = conn.prepareStatement(sql);
				ps.setLong(1, seq);
				ps.setString(2, title);
				ps.setString(3, stats);
				ps.setInt(4, cnt);
				ps.setInt(5, cnt);
				ps.executeUpdate();
				conn.commit();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.rollback(conn);
				DBUtil.close(rs);
				DBUtil.close(ps);
				DBUtil.close(conn);
			}
		}
	}

}
