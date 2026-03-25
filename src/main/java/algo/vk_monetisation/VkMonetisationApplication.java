package algo.vk_monetisation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VkMonetisationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkMonetisationApplication.class, args);
    }

}
