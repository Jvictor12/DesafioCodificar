package api.desafio.codificar.Backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendApplicationTests {

	@Test
	void backendIsConfiguredAsSpringBootApplication() {
		assertTrue(Backend.class.isAnnotationPresent(SpringBootApplication.class));
	}

}
