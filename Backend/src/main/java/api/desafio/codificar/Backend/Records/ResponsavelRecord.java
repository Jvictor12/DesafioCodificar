package api.desafio.codificar.Backend.Records;

import api.desafio.codificar.Backend.Entity.Chamado;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;
import java.util.UUID;

public record ResponsavelRecord (UUID id, @NotBlank String nome, List<Chamado> chamados) {
}
