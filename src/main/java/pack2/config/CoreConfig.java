package pack2.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"pack2.repository", "pack2.service"})
public class CoreConfig {
}
