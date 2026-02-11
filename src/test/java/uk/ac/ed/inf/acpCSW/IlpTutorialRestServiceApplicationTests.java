package uk.ac.ed.inf.acpCSW;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		classes = AcpApplication.class,
		properties = {
				"ACP_POSTGRES=jdbc:postgresql://dummy:5432/acp",
				"ACP_S3=http://dummy:4566",
				"ACP_DYNAMODB=http://dummy:4566",
				"ACP_ILP_ENDPOINT=https://example.com/"
		}
)
class acpApplicationTests {

	@Test
	void contextLoads() {
	}
}
