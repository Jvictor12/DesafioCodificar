package api.desafio.codificar.Backend.Controller;

import api.desafio.codificar.Backend.Entity.Chamado;
import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Records.ResponsavelRecord;
import api.desafio.codificar.Backend.Service.ChamadoService;
import api.desafio.codificar.Backend.Service.ResponsavelService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTests {

    @Test
    void chamadoControllerDelegatesEveryOperation() {
        ChamadoService service = mock(ChamadoService.class);
        ChamadoController controller = new ChamadoController(service);
        UUID id = UUID.randomUUID();
        Chamado chamado = new Chamado();
        var page = new PageImpl<>(List.of(chamado));
        when(service.findAll(0, 10, "titulo", "asc")).thenReturn(page);
        when(service.findById(id)).thenReturn(chamado);
        when(service.save(chamado)).thenReturn(chamado);
        when(service.update(id, chamado)).thenReturn(chamado);

        assertSame(page, controller.findAll(0, 10, "titulo", "asc").getBody());
        assertSame(chamado, controller.findById(id).getBody());
        assertEquals(HttpStatus.CREATED, controller.create(chamado).getStatusCode());
        assertSame(chamado, controller.update(id, chamado).getBody());
        assertEquals("Deletado com Sucesso!", controller.delete(id).getBody());
        verify(service).delete(id);
    }

    @Test
    void responsavelControllerDelegatesEveryOperation() {
        ResponsavelService service = mock(ResponsavelService.class);
        ResponsavelController controller = new ResponsavelController(service);
        UUID id = UUID.randomUUID();
        Responsavel responsavel = new Responsavel();
        ResponsavelRecord record = new ResponsavelRecord(id, "Nome", List.of());
        var page = new PageImpl<>(List.of(record));
        when(service.findAll(0, 10, "nome", "asc")).thenReturn(page);
        when(service.findById(id)).thenReturn(record);
        when(service.atribuirResponsavel()).thenReturn(id);
        when(service.save(responsavel)).thenReturn(record);
        when(service.update(id, responsavel)).thenReturn(record);

        assertSame(page, controller.findAll(0, 10, "nome", "asc").getBody());
        assertSame(record, controller.findById(id).getBody());
        assertEquals(id, controller.atribuirResponsavel().getBody());
        assertEquals(HttpStatus.CREATED, controller.create(responsavel).getStatusCode());
        assertSame(record, controller.update(id, responsavel).getBody());
        assertEquals("Chamado deletado com Sucesso!", controller.delete(id).getBody());
    }
}
