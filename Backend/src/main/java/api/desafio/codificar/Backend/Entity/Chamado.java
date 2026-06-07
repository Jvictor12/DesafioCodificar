package api.desafio.codificar.Backend.Entity;

import api.desafio.codificar.Backend.enums.Prioridade;
import api.desafio.codificar.Backend.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tb_chamados")
public class Chamado extends AbstractEntity {

    @Column(length = 100, nullable = false)
    private String titulo;

    @Column(length = 500, nullable = false)
    private String descricao;

    @Column(length = 15, nullable = false)
    private Prioridade prioridade;

    @Column(length = 20, nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    @JsonIgnoreProperties("chamados")
    private Responsavel responsavel;
}
