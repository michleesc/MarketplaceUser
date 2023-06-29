package comp.finalproject.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"comp.finalproject.user"})
public class MarketplaceUser {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceUser.class, args);
    }

}
