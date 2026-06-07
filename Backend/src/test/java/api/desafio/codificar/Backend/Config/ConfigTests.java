package api.desafio.codificar.Backend.Config;

import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Repository.ResponsavelRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ConfigTests {

    @Test
    void openApiContainsExpectedMetadata() {
        var openApi = new OpenApiConfig().backendApi();

        assertEquals("API Bootcamp Sysmap 2026", openApi.getInfo().getTitle());
        assertEquals("v0.0.1", openApi.getInfo().getVersion());
        assertNotNull(openApi.getInfo().getDescription());
    }

    @Test
    void initializerCreatesMissingResponsaveis() {
        ResponsavelRepository repository = mock(ResponsavelRepository.class);
        when(repository.findByNome(anyString())).thenReturn(Optional.empty());
        ResponsavelDataInitializer initializer = new ResponsavelDataInitializer(repository);

        initializer.run();

        ArgumentCaptor<Responsavel> captor = ArgumentCaptor.forClass(Responsavel.class);
        verify(repository, times(3)).save(captor.capture());
        assertEquals(3, captor.getAllValues().size());
        assertTrue(captor.getAllValues().stream().allMatch(r -> r.getNome() != null && r.getCpf() != null));
    }

    @Test
    void initializerUpdatesExistingResponsaveis() {
        ResponsavelRepository repository = mock(ResponsavelRepository.class);
        Responsavel existing = new Responsavel();
        when(repository.findByNome(anyString())).thenReturn(Optional.of(existing));
        ResponsavelDataInitializer initializer = new ResponsavelDataInitializer(repository);

        initializer.run("ignored");

        verify(repository, times(3)).save(existing);
        assertNotNull(existing.getCpf());
    }
}
