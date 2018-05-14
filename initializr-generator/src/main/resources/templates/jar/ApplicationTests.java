package {{packageName}};

import org.junit.Test;
import org.junit.runner.RunWith;
import com.fluig.starter.test.configuration.IntegrationTestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@ContextConfiguration(classes = {IntegrationTestConfiguration.class})
public class {{applicationName}}IT {

	@Test
	public void contextLoads() {
	}

}
