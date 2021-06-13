package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnect 
{
	private static final String jdbcURL = "jdbc:mariadb://localhost/PremierLeague";
	private static final HikariDataSource dataSource;
	private static final String username = "root";
	private static final String password = "root";

	static 
	{
		HikariConfig configuration = new HikariConfig();
		configuration.setJdbcUrl(jdbcURL);
		configuration.setUsername(username);
		configuration.setPassword(password);
		
		// MySQL configuration
		configuration.addDataSourceProperty("cachePrepStmts", "true");
		configuration.addDataSourceProperty("prepStmtCacheSize", "250");
		configuration.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		
		dataSource = new HikariDataSource(configuration);
	}
	
	public static Connection getConnection() 
	{
		try 
		{	
			return dataSource.getConnection();
		} 
		catch (SQLException e) 
		{
			System.err.println("Error DB connection at: " + jdbcURL);
			throw new RuntimeException("Error DB connection at: " + jdbcURL, e);
		}
	}
}
