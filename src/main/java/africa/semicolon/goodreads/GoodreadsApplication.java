package africa.semicolon.goodreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.thymeleaf.templateresolver.ITemplateResolver;

@SpringBootApplication
@EnableAsync
public class GoodreadsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodreadsApplication.class, args);
	}

}
