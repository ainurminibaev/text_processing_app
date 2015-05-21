package pack2.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.net.URISyntaxException;

/**
 * Created on 10.09.2014.
 */
//@Configuration
//@PropertySource(value = "classpath:database.properties", ignoreResourceNotFound = true)
//public class DataSourceConfig {
//
//    @Autowired
//    private Environment env;
//
//    @Bean
//    public DataSource dataSource() throws PropertyVetoException, URISyntaxException {
//        String username, password, dbUrl, driverClassName;
//        username = env.getProperty("jdbc.user");
//        password = env.getProperty("jdbc.password");
//        dbUrl = env.getProperty("jdbc.url");
//        driverClassName = env.getProperty("jdbc.dialect");
//        ComboPooledDataSource dataSource = new ComboPooledDataSource();
//        dataSource.setDriverClass(driverClassName);
//        dataSource.setJdbcUrl(dbUrl);
//        dataSource.setUser(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//
//}
