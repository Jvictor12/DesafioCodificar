package api.desafio.codificar.Backend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tb_responsaveis")
public class Responsavel extends AbstractEntity {

    @NotBlank
    @Column(length = 100, nullable = false)
    private String nome;

    @CPF
    @Column(unique = true, nullable = false)
    private String cpf;

    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL)
    private List<Chamado> chamados;
}
