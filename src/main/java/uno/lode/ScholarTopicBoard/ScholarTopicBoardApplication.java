package uno.lode.ScholarTopicBoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(
		title = "Scholar Topic Board API", version = "v1"))

@SpringBootApplication
public class ScholarTopicBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScholarTopicBoardApplication.class, args);
	}

}
