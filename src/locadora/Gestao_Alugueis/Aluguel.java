
package locadora.Gestao_Alugueis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import locadora.Excecoes.AluguelJaDevolvidoException;
import locadora.Gestao_Jogos.Jogos;
import locadora.model.User;

public class Aluguel {

    private static int proximoId = 1;

    private final int id;
    private final User usuario;
    private final Jogos jogo;
    private final LocalDate dataAluguel;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private double valorPenalidade;

    public Aluguel(User usuario, Jogos jogo, LocalDate dataAluguel,
                   LocalDate dataPrevistaDevolucao) {

        if (usuario == null || jogo == null) {
            throw new IllegalArgumentException(
                    "Usuário e jogo devem ser informados.");
        }

        if (dataAluguel == null || dataPrevistaDevolucao == null
                || !dataPrevistaDevolucao.isAfter(dataAluguel)) {
            throw new IllegalArgumentException(
                    "A data prevista deve ser posterior à data do aluguel.");
        }

        this.id = proximoId++;
        this.usuario = usuario;
        this.jogo = jogo;
        this.dataAluguel = dataAluguel;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = null;
        this.valorPenalidade = 0.0;
    }

    public boolean isDevolvido() {
        return dataDevolucao != null;
    }

    public long diasDeAtraso(LocalDate referencia) {
        return Math.max(0,
                ChronoUnit.DAYS.between(dataPrevistaDevolucao, referencia));
    }

    public boolean isAtrasado(LocalDate hoje) {
        return !isDevolvido() && hoje.isAfter(dataPrevistaDevolucao);
    }

    public void registrarDevolucao(LocalDate dataDevolucao,
                                   CalculadoraPenalidade calculadora)
            throws AluguelJaDevolvidoException {

        if (this.dataDevolucao != null) {
            throw new AluguelJaDevolvidoException(id);
        }

        if (dataDevolucao == null || calculadora == null) {
            throw new IllegalArgumentException(
                    "Data de devolução e calculadora devem ser informadas.");
        }

        if (dataDevolucao.isBefore(dataAluguel)) {
            throw new IllegalArgumentException(
                    "A data de devolução não pode ser anterior ao aluguel.");
        }

        long diasAtraso = diasDeAtraso(dataDevolucao);

        this.valorPenalidade = calculadora.calcular(diasAtraso);
        this.dataDevolucao = dataDevolucao;
    }

    public int getId() {
        return id;
    }

    public User getUsuario() {
        return usuario;
    }

    public Jogos getJogo() {
        return jogo;
    }

    public LocalDate getDataAluguel() {
        return dataAluguel;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public double getValorPenalidade() {
        return valorPenalidade;
    }

    @Override
    public String toString() {

        String situacao;

        if (isDevolvido()) {
            situacao = "Devolvido em " + dataDevolucao
                    + String.format(" | Multa: R$ %.2f", valorPenalidade);
        } else {
            situacao = "EM ABERTO";
        }

        return String.format(
                "#%d | %s | %s (%s) | Alugado em %s | Prazo: %s | %s",
                id,
                usuario.getNome(),
                jogo.getTitulo(),
                jogo.getPlataforma(),
                dataAluguel,
                dataPrevistaDevolucao,
                situacao
        );
    }
}
