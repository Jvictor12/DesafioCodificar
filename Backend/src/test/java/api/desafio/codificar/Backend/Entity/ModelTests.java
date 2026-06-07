package api.desafio.codificar.Backend.Entity;

import api.desafio.codificar.Backend.Records.ChamadoRecord;
import api.desafio.codificar.Backend.Records.ResponsavelRecord;
import api.desafio.codificar.Backend.enums.Prioridade;
import api.desafio.codificar.Backend.enums.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModelTests {

    @Test
    void abstractEntityAccessorsEqualityHashCodeAndToStringWork() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Chamado first = new Chamado();
        Chamado sameId = new Chamado();
        Responsavel different = new Responsavel();

        first.setId(id);
        first.setCreatedDate(now);
        first.setLastModifiedDate(now);
        first.setCreatedByClient("creator");
        first.setModifiedByClient("modifier");
        sameId.setId(id);
        different.setId(UUID.randomUUID());

        assertEquals(id, first.getId());
        assertEquals(now, first.getCreatedDate());
        assertEquals(now, first.getLastModifiedDate());
        assertEquals("creator", first.getCreatedByClient());
        assertEquals("modifier", first.getModifiedByClient());
        assertEquals(first, first);
        assertEquals(first, sameId);
        assertNotEquals(first, different);
        assertNotEquals(first, "other");
        assertEquals(first.hashCode(), sameId.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void chamadoConstructorsBuilderAndAccessorsWork() {
        Responsavel responsavel = new Responsavel();
        Chamado chamado = Chamado.builder()
                .titulo("Titulo")
                .descricao("Descricao")
                .prioridade(Prioridade.ALTA)
                .status(Status.Aberto)
                .responsavel(responsavel)
                .build();

        assertEquals("Titulo", chamado.getTitulo());
        assertEquals("Descricao", chamado.getDescricao());
        assertEquals(Prioridade.ALTA, chamado.getPrioridade());
        assertEquals(Status.Aberto, chamado.getStatus());
        assertSame(responsavel, chamado.getResponsavel());

        chamado.setTitulo("Novo");
        chamado.setDescricao("Nova");
        chamado.setPrioridade(Prioridade.BAIXA);
        chamado.setStatus(Status.Fechado);
        chamado.setResponsavel(null);
        assertEquals("Novo", chamado.getTitulo());

        Chamado complete = new Chamado("T", "D", Prioridade.MEDIA, Status.Resolvido, responsavel);
        assertEquals("T", complete.getTitulo());
    }

    @Test
    void responsavelConstructorsBuilderAndAccessorsWork() {
        List<Chamado> chamados = List.of(new Chamado());
        Responsavel responsavel = Responsavel.builder()
                .nome("Nome")
                .cpf("20431784582")
                .chamados(chamados)
                .build();

        assertEquals("Nome", responsavel.getNome());
        assertEquals("20431784582", responsavel.getCpf());
        assertSame(chamados, responsavel.getChamados());

        responsavel.setNome("Outro");
        responsavel.setCpf("16500693590");
        responsavel.setChamados(List.of());
        assertEquals("Outro", responsavel.getNome());

        Responsavel complete = new Responsavel("Completo", "93127331509", chamados);
        assertEquals("Completo", complete.getNome());
    }

    @Test
    void recordsAndEnumsExposeTheirValues() {
        Responsavel responsavel = new Responsavel();
        ChamadoRecord chamado = new ChamadoRecord(
                "Titulo", "Descricao", Prioridade.MEDIA, Status.EmAndamento, responsavel);
        UUID id = UUID.randomUUID();
        List<Chamado> chamados = List.of(new Chamado());
        ResponsavelRecord record = new ResponsavelRecord(id, "Nome", chamados);

        assertEquals("Titulo", chamado.titulo());
        assertEquals("Descricao", chamado.descricao());
        assertEquals(Prioridade.MEDIA, chamado.prioridade());
        assertEquals(Status.EmAndamento, chamado.status());
        assertSame(responsavel, chamado.responsavel());
        assertEquals(id, record.id());
        assertEquals("Nome", record.nome());
        assertSame(chamados, record.chamados());
        assertEquals(3, Prioridade.values().length);
        assertEquals(4, Status.values().length);
        assertEquals(Prioridade.ALTA, Prioridade.valueOf("ALTA"));
        assertEquals(Status.Fechado, Status.valueOf("Fechado"));
    }
}
