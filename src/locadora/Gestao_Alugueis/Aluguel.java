package locadora.Gestao_Alugueis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import locadora.Gestao_Jogos.Jogos;
import locadora.model.User;

public class Aluguel {

    // novo id para aluguel
    private static int proximoId = 1;

    private final int id;
    private final User usuario;
    private final Jogos jogo;
    private final LocalDate dataAluguel;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;      // fica null enquanto o jogo não for devolvido
    private double valorPenalidade;

    // Construtor para um aluguel novo: o id é gerado sozinho
    public Aluguel(User usuario, Jogos jogo, LocalDate dataAluguel, LocalDate dataPrevistaDevolucao) {
        this(proximoId++, usuario, jogo, dataAluguel, dataPrevistaDevolucao, null, 0.0);
    }

    // Construtor para refazer um aluguel lido do arquivo
    public Aluguel(int id, User usuario, Jogos jogo, LocalDate dataAluguel,
                   LocalDate dataPrevistaDevolucao, LocalDate dataDevolucao, double valorPenalidade) {
        this.id = id;
        this.usuario = usuario;
        this.jogo = jogo;
        this.dataAluguel = dataAluguel;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.valorPenalidade = valorPenalidade;
        if (id >= proximoId) {
            proximoId = id + 1;   // evita ids repetidos depois de carregar do arquivo
        }
    }

    //confere se é null
    public boolean isDevolvido() {
        return dataDevolucao != null;
    }

    // quantos dias passou da entrega, nunca pode ser negativo
    public long diasDeAtraso(LocalDate referencia) {
        return Math.max(0, ChronoUnit.DAYS.between(dataPrevistaDevolucao, referencia));
    }

    // Verdadeiro se ainda não foi devolvido e o prazo já passou
    public boolean isAtrasado(LocalDate hoje) {
        return !isDevolvido() && hoje.isAfter(dataPrevistaDevolucao);
    }

    // Marca como devolvido e calcula a multa, para isso o objeto tem que cumprir o que a interface calcular penalidades diz
    public void registrarDevolucao(LocalDate data, CalculadoraPenalidade calculadora) {
        this.dataDevolucao = data;
        this.valorPenalidade = calculadora.calcular(diasDeAtraso(data));
    }

    public int getId() {
        return id; }

    public User getUsuario() {
        return usuario; }

    public Jogos getJogo() {
        return jogo; }

    public LocalDate getDataAluguel() {
        return dataAluguel; }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao; }

    public LocalDate getDataDevolucao() {
        return dataDevolucao; }

    public double getValorPenalidade() {
        return valorPenalidade; }


    @Override
    //corpo de como tudo será impresso
    public String toString() {
        String situacao;
        if (isDevolvido()) {
            situacao = "Devolvido em " + dataDevolucao + String.format(" | Multa: R$ %.2f", valorPenalidade);
        } else {
            situacao = "EM ABERTO";
        }
        return String.format("#%d | %s | %s (%s) | Alugado em %s | Prazo: %s | %s",
                id, usuario.getNome(), jogo.getTitulo(), jogo.getPlataforma(),
                dataAluguel, dataPrevistaDevolucao, situacao);
    }
}