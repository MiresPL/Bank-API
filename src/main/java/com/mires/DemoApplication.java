package com.mires;

import com.mires.database.MysqlManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class DemoApplication {
	private static MysqlManager mysqlManager;

	public static void main(String[] args) throws SQLException {
		mysqlManager = new MysqlManager();
		SpringApplication.run(DemoApplication.class, args);
	}
	public static MysqlManager getMysqlManager() {
		return mysqlManager;
	}
}
