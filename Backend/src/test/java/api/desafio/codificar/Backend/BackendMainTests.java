package api.desafio.codificar.Backend;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BackendMainTests {

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--server.port=0"};
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            Backend.main(args);
            springApplication.verify(() -> SpringApplication.run(Backend.class, args));
        }
    }

    @Test
    void backendCanBeConstructed() {
        assertNotNull(new Backend());
    }
}
