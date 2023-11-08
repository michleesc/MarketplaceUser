package comp.finalproject.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = {"comp.finalproject.user"})
public class MarketplaceUser {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceUser.class, args);
    }

}
