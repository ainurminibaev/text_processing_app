package pack.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import pack.db.HyperSqlDbServer;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created on 10.09.2014.
 */
@Configuration
@PropertySource(value = "classpath:database.properties", ignoreResourceNotFound = true)
public class DataSourceConfig implements DisposableBean {

    @Autowired
    private Environment env;

    private HyperSqlDbServer hyperSqlDbServer;

    @Bean
    public DataSource dataSource() throws PropertyVetoException, URISyntaxException {
        String username, password, dbUrl, driverClassName;
        String mode = env.getProperty("jdbc.mode");
        if (mode.contains("hsql")) {
            createHsqlDataSource();
        }
        username = env.getProperty("jdbc.user");
        password = env.getProperty("jdbc.password");
        dbUrl = env.getProperty("jdbc.url");
        driverClassName = env.getProperty("jdbc.driver");
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClassName);
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    private DataSource createHsqlDataSource() {
        hyperSqlDbServer = new HyperSqlDbServer();
        hyperSqlDbServer.start();
        return null;
    }


    @Override
    public void destroy() throws Exception {
        if (hyperSqlDbServer != null) {
            hyperSqlDbServer.stop();
        }
    }
}
