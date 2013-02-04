package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A helper class for dealing with DataSource:s
 * <ul>
 * <li>resource
 * <li>user
 * <li>password
 * <li>select - the sql query to run
 * </ul>
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DataSourceHelper {
	private final DataSource dataSource;
	private final String dbuser;
	private final String dbpwd;
	private final String select;

	/**
	 * Create a new DataSourceHelper
	 * 
	 * @param props
	 *            the properties to read configuration from
	 * @param defaultSelect
	 *            the default sql statement to use if not configured
	 * @throws NamingException
	 *             if the DataSource can not be found
	 */
	public DataSourceHelper(SProperties props, String defaultSelect)
			throws NamingException {
		String resource = props.getProperty("resource");
		Context context = new InitialContext();
		Context envCtx = (Context) context.lookup("java:comp/env");
		dataSource = (DataSource) envCtx.lookup(resource);
		dbuser = props.getProperty("user", "");
		dbpwd = props.getProperty("password", "");
		select = props.getProperty("select", defaultSelect);
	}

	/**
	 * Get a database connection
	 * 
	 * @return a database connection
	 * @throws SQLException
	 *             if the database connection can not be established
	 */
	public Connection getConnection() throws SQLException {
		if (!(dbuser.isEmpty() || dbpwd.isEmpty()))
			return dataSource.getConnection(dbuser, dbpwd);
		return dataSource.getConnection();
	}

	/**
	 * Get the select statement for this helper.
	 * 
	 * @return the sql statement
	 */
	public String getSelect() {
		return select;
	}
}