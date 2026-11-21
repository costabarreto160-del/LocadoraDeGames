
package locadora.Gestao_Alugueis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import locadora.Excecoes.*;
import locadora.Gestao_Jogos.Jogos;
import locadora.model.Customer;
import locadora.model.User;

public class GerenciadorAlugueis {

    private final List<Aluguel> alugueis;
    private final CalculadoraPenalidade calculadora;

    public static final int LIMITE_ALUGUEIS = 3;
    public static final int PRAZO_DEVOLUCAO = 7;

    public GerenciadorAlugueis() {
        alugueis = new ArrayList<>();
        calculadora = new PenalidadePorDia();
    }

    public Aluguel realizarAluguel(User usuario, Jogos jogo)
            throws LocadoraException {

        return realizarAluguel(usuario, jogo, LocalDate.now(),
                LocalDate.now().plusDays(PRAZO_DEVOLUCAO));
    }

    public Aluguel realizarAluguel(User usuario, Jogos jogo,
                                   LocalDate dataAluguel,
                                   LocalDate dataPrevistaDevolucao)
            throws LocadoraException {

        if (usuario == null || jogo == null) {
            throw new IllegalArgumentException(
                    "Usuário e jogo devem ser informados.");
        }

        if (dataAluguel == null || dataPrevistaDevolucao == null
                || !dataPrevistaDevolucao.isAfter(dataAluguel)) {
            throw new IllegalArgumentException(
                    "A data prevista deve ser posterior à data do aluguel.");
        }

        int quantidade = contarAlugueisEmAberto(usuario);

        if (quantidade >= LIMITE_ALUGUEIS) {
            throw new LimiteAlugueisException(
                    usuario.getNome(), LIMITE_ALUGUEIS);
        }

        if (jogo.getQtdEmEstoque() <= 0) {
            throw new JogoIndisponivelException(jogo.getTitulo());
        }

        Aluguel aluguel = new Aluguel(
                usuario, jogo, dataAluguel, dataPrevistaDevolucao);

        jogo.diminuirEstoque();

        alugueis.add(aluguel);

        if (usuario instanceof Customer) {
            ((Customer) usuario).adicionarAoHistorico(aluguel);
        }

        System.out.println("Aluguel realizado com sucesso!");
        System.out.println(aluguel);

        return aluguel;
    }

    public double devolverJogo(int id)
            throws LocadoraException {

        return devolverJogo(id, LocalDate.now());
    }

    public double devolverJogo(int id, LocalDate dataDevolucao)
            throws LocadoraException {

        if (dataDevolucao == null) {
            throw new IllegalArgumentException(
                    "Informe a data da devolução.");
        }

        Aluguel aluguel = buscarPorId(id);

        if (aluguel == null) {
            throw new AluguelNaoEncontradoException(id);
        }

        aluguel.registrarDevolucao(dataDevolucao, calculadora);

        aluguel.getJogo().aumentarEstoque();

        System.out.println("Devolução realizada com sucesso!");
        System.out.println("Multa: R$ "
                + String.format("%.2f", aluguel.getValorPenalidade()));

        return aluguel.getValorPenalidade();
    }

    public int contarAlugueisEmAberto(User usuario) {

        if (usuario == null) {
            return 0;
        }

        int quantidade = 0;

        for (Aluguel aluguel : alugueis) {
            if (aluguel.getUsuario().equals(usuario)
                    && !aluguel.isDevolvido()) {
                quantidade++;
            }
        }

        return quantidade;
    }

    public Aluguel buscarPorId(int id) {

        for (Aluguel aluguel : alugueis) {
            if (aluguel.getId() == id) {
                return aluguel;
            }
        }

        return null;
    }

    public List<Aluguel> listarTodos() {
        return Collections.unmodifiableList(alugueis);
    }

    public List<Aluguel> listarDoUsuario(User usuario) {

        List<Aluguel> resultado = new ArrayList<>();

        if (usuario == null) {
            return resultado;
        }

        for (Aluguel aluguel : alugueis) {
            if (aluguel.getUsuario().equals(usuario)) {
                resultado.add(aluguel);
            }
        }

        return resultado;
    }
}

