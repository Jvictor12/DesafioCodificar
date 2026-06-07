package api.desafio.codificar.Backend.Records;

import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.enums.Prioridade;
import api.desafio.codificar.Backend.enums.Status;
import jakarta.validation.constraints.NotBlank;

//Verificar se da pra usar o Enum no Record
public record ChamadoRecord (@NotBlank String titulo, @NotBlank String descricao, @NotBlank Prioridade prioridade, @NotBlank Status status, Responsavel responsavel) {
}
