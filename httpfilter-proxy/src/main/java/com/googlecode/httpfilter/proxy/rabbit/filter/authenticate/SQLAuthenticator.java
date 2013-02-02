package com.googlecode.httpfilter.proxy.rabbit.filter.authenticate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import com.googlecode.httpfilter.proxy.rabbit.filter.DataSourceHelper;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * An authenticator that checks the username/password against an sql database.
 * 
 * Will read the following parameters from the config file:
 * <ul>
 * <li>resource
 * <li>user
 * <li>password
 * <li>select - the sql query to run
 * </ul>
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SQLAuthenticator implements Authenticator {

	private final DataSourceHelper dsh;
	private final Logger logger = Logger.getLogger(getClass().getName());
	private static final String DEFAULT_SELECT = "select password from users where username = ?";

	/**
	 * Create a new SQLAuthenticator that will be configured using the given
	 * properties.
	 * 
	 * @param props
	 *            the configuration for this authenticator
	 */
	public SQLAuthenticator(SProperties props) {
		try {
			dsh = new DataSourceHelper(props, DEFAULT_SELECT);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getToken(HttpHeader header,
			com.googlecode.httpfilter.proxy.rabbit.proxy.Connection con) {
		return con.getPassword();
	}

	public boolean authenticate(String user, String token) {
		try {
			Connection db = dsh.getConnection();
			try {
				String pwd = getDbPassword(db, user);
				if (pwd == null)
					return false;
				return pwd.equals(token);
			} finally {
				db.close();
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Exception when trying to authenticate",
					e);
		}
		return false;
	}

	private String getDbPassword(Connection db, String username)
			throws SQLException {
		PreparedStatement ps = db.prepareStatement(dsh.getSelect());
		try {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return rs.getString(1);
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
		return null;
	}
}
