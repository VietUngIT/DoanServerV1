package vietung.it.dev.core.pools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by ThinhNK on 1/25/2018.
 */
public class HikariPool {
    private static final String HIRAKI_CONFIG_FILE = "commonconfig/hikari.properties";
    private static HikariDataSource dataSource;
    public static void init() throws IOException {
        InputStream input = new FileInputStream(HIRAKI_CONFIG_FILE);
        Properties prop = new Properties();
        prop.load(input);
        HikariConfig config = new HikariConfig(prop);
        config.setMaximumPoolSize(30);
        dataSource = new HikariDataSource(config);
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
