package locadora.Gestao_Alugueis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import locadora.Excecoes.*;
import locadora.Gestao_Jogos.GerenciadorJogos;
import locadora.Gestao_Jogos.Jogos;
import locadora.model.Adim;
import locadora.model.Customer;
import locadora.model.User;
import locadora.service.UserService;

public class GerenciadorAlugueis {

    private final List<Aluguel> alugueis;
    private final CalculadoraPenalidade calculadora;
    private final RepositorioAlugueis repositorio; // null = não grava em arquivo

    public static final int LIMITE_ALUGUEIS = 3;
    public static final int PRAZO_DEVOLUCAO = 7;

    // Só para demonstrar a multa sem esperar dias de verdade (ver opção [TESTE] do menu)
    private static int diasSimulados = 0;

    // Sem persistência (útil para testes rápidos)
    public GerenciadorAlugueis() {
        alugueis = new ArrayList<>();
        calculadora = new PenalidadePorDia();
        repositorio = null;
    }

    // Com persistência: recarrega os aluguéis salvos e devolve cada um ao histórico do seu cliente
    public GerenciadorAlugueis(RepositorioAlugueis repositorio, UserService userService,
                               GerenciadorJogos gerenciadorJogos) throws LocadoraException {
        alugueis = new ArrayList<>();
        calculadora = new PenalidadePorDia();
        this.repositorio = repositorio;

        for (Aluguel aluguel : repositorio.carregar(userService, gerenciadorJogos)) {
            alugueis.add(aluguel);
            if (aluguel.getUsuario() instanceof Customer) {
                ((Customer) aluguel.getUsuario()).adicionarAoHistorico(aluguel);
            }
        }
    }

    // Data usada pelo sistema inteiro (hoje + dias simulados, que normalmente é 0)
    public static LocalDate hoje() {
        return LocalDate.now().plusDays(diasSimulados);
    }

    public static void avancarDias(int dias) {
        diasSimulados += dias;
    }

    public static int getDiasSimulados() {
        return diasSimulados;
    }

    public CalculadoraPenalidade getCalculadora() {
        return calculadora;
    }

    public Aluguel realizarAluguel(User usuario, Jogos jogo)
            throws LocadoraException {

        LocalDate hoje = hoje();
        return realizarAluguel(usuario, jogo, hoje, hoje.plusDays(PRAZO_DEVOLUCAO));
    }

    public Aluguel realizarAluguel(User usuario, Jogos jogo,
                                   LocalDate dataAluguel,
                                   LocalDate dataPrevistaDevolucao)
            throws LocadoraException {

        if (usuario == null || jogo == null) {
            throw new DadoInvalidoException("Usuário e jogo devem ser informados.");
        }

        // o enunciado fala em aluguéis "para os clientes"
        if (!(usuario instanceof Customer)) {
            throw new OperacaoNaoPermitidaException("Aluguéis só podem ser feitos para clientes.");
        }

        if (dataAluguel == null || dataPrevistaDevolucao == null
                || !dataPrevistaDevolucao.isAfter(dataAluguel)) {
            throw new DadoInvalidoException("A data prevista deve ser posterior à data do aluguel.");
        }

        // regra 4: máximo de 3 jogos ao mesmo tempo
        if (contarAlugueisEmAberto(usuario) >= LIMITE_ALUGUEIS) {
            throw new LimiteAlugueisException(usuario.getNome(), LIMITE_ALUGUEIS);
        }

        // regra 1: só aluga se houver unidade disponível
        if (jogo.getQtdEmEstoque() <= 0) {
            throw new JogoIndisponivelException(jogo.getTitulo());
        }

        Aluguel aluguel = new Aluguel(usuario, jogo, dataAluguel, dataPrevistaDevolucao);
        Customer cliente = (Customer) usuario;

        jogo.diminuirEstoque();                                   // regra 2
        alugueis.add(aluguel);
        cliente.adicionarAoHistorico(aluguel);

        try {
            salvar();
        } catch (PersistenciaException e) {
            // não conseguiu gravar no arquivo: desfaz tudo para a memória não ficar diferente do arquivo
            alugueis.remove(aluguel);
            cliente.getHistoricoAlugueis().remove(aluguel);
            jogo.aumentarEstoque();
            throw e;
        }
        return aluguel;
    }

    public Aluguel devolverJogo(User solicitante, int id)
            throws LocadoraException {

        return devolverJogo(solicitante, id, hoje());
    }

    public Aluguel devolverJogo(User solicitante, int id, LocalDate dataDevolucao)
            throws LocadoraException {

        if (solicitante == null || dataDevolucao == null) {
            throw new DadoInvalidoException("Informe o usuário e a data da devolução.");
        }

        Aluguel aluguel = buscarPorId(id);

        if (aluguel == null) {
            throw new AluguelNaoEncontradoException(id);
        }

        // cliente só devolve o que é dele; administrador pode registrar qualquer devolução
        boolean dono = aluguel.getUsuario().getLogin().equals(solicitante.getLogin());
        if (!dono && !(solicitante instanceof Adim)) {
            throw new OperacaoNaoPermitidaException("O aluguel " + id + " pertence a outro usuário.");
        }

        if (dataDevolucao.isBefore(aluguel.getDataAluguel())) {
            throw new DadoInvalidoException("A data de devolução não pode ser anterior à data do aluguel.");
        }

        aluguel.registrarDevolucao(dataDevolucao, calculadora);   // regras 6 e 7
        aluguel.getJogo().aumentarEstoque();                      // regra 3

        try {
            salvar();
        } catch (PersistenciaException e) {
            // não conseguiu gravar no arquivo: desfaz a devolução
            aluguel.desfazerDevolucao();
            aluguel.getJogo().diminuirEstoque();
            throw e;
        }
        return aluguel;
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

    // aluguéis do usuário que ainda não foram devolvidos
    public List<Aluguel> listarAtivosDoUsuario(User usuario) {

        List<Aluguel> resultado = new ArrayList<>();

        for (Aluguel aluguel : listarDoUsuario(usuario)) {
            if (!aluguel.isDevolvido()) {
                resultado.add(aluguel);
            }
        }

        return resultado;
    }

    // todos os aluguéis que ainda não foram devolvidos (para consulta do administrador)
    public List<Aluguel> listarEmAberto() {

        List<Aluguel> resultado = new ArrayList<>();

        for (Aluguel aluguel : alugueis) {
            if (!aluguel.isDevolvido()) {
                resultado.add(aluguel);
            }
        }

        return resultado;
    }

    // aluguéis em aberto cujo prazo já venceu (para consulta do administrador)
    public List<Aluguel> listarAtrasados() {

        List<Aluguel> resultado = new ArrayList<>();

        for (Aluguel aluguel : alugueis) {
            if (aluguel.isAtrasado(hoje())) {
                resultado.add(aluguel);
            }
        }

        return resultado;
    }

    public void salvar() throws PersistenciaException {
        if (repositorio != null) {
            repositorio.salvar(alugueis);
        }
    }
}
