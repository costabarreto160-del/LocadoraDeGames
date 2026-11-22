package locadora.Gestao_Alugueis;

import locadora.Excecoes.LocadoraException;
import locadora.Excecoes.OperacaoNaoPermitidaException;
import locadora.Excecoes.UsuarioNaoEncontradoException;
import locadora.Gestao_Jogos.GerenciadorJogos;
import locadora.Gestao_Jogos.Jogos;
import locadora.model.Adim;
import locadora.model.Customer;
import locadora.model.User;
import locadora.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// Telas de aluguel, devolução, histórico e perfil, para o cliente e para o administrador,
// com validação das entradas
public class MenuAlugueis {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;
    private final GerenciadorAlugueis gerenciadorAlugueis;
    private final GerenciadorJogos gerenciadorJogos;
    private final UserService userService;

    public MenuAlugueis(Scanner scanner, GerenciadorAlugueis gerenciadorAlugueis,
                        GerenciadorJogos gerenciadorJogos, UserService userService) {
        this.scanner = scanner;
        this.gerenciadorAlugueis = gerenciadorAlugueis;
        this.gerenciadorJogos = gerenciadorJogos;
        this.userService = userService;
    }

    // ===================== MENU DO CLIENTE =====================

    // Laço do menu do cliente logado. O texto do menu continua vindo de Customer.exibirMenuEspercifico()
    public void executarMenuCliente(Customer cliente) {
        avisarAtrasos(cliente);
        int opcao;
        do {
            cliente.exibirMenuEspercifico();
            opcao = lerInteiro("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1:
                        // TODO (Gestão de Jogos): trocar pela busca com filtros quando o SistemaDeBusca estiver integrado
                        listarJogos();
                        break;
                    case 2:
                        realizarAluguel(cliente);
                        break;
                    case 3:
                        devolverJogo(cliente);
                        break;
                    case 4:
                        mostrarPerfil(cliente, "MEU PERFIL E HISTÓRICO DE ALUGUÉIS");
                        break;
                    case 0:
                        System.out.println("Logout realizado.");
                        break;
                    default:
                        System.out.println("  [ATENÇÃO] Opção inválida. Escolha uma opção do menu (0 a 4).");
                }
            } catch (LocadoraException e) {
                System.out.println("\n  [ERRO] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void realizarAluguel(Customer cliente) throws LocadoraException {
        System.out.println("\n=== REALIZAR ALUGUEL ===");
        System.out.println("Você tem " + gerenciadorAlugueis.contarAlugueisEmAberto(cliente) + " de "
                + GerenciadorAlugueis.LIMITE_ALUGUEIS + " aluguéis ativos.");

        Jogos jogo = escolherJogo();
        if (jogo == null) {
            return;
        }
        mostrarResultadoAluguel(gerenciadorAlugueis.realizarAluguel(cliente, jogo));
    }

    private void devolverJogo(Customer cliente) throws LocadoraException {
        System.out.println("\n=== DEVOLVER JOGO ===");
        List<Aluguel> ativos = gerenciadorAlugueis.listarAtivosDoUsuario(cliente);
        if (ativos.isEmpty()) {
            System.out.println("Você não possui jogos alugados para devolver.");
            return;
        }
        for (Aluguel a : ativos) {
            System.out.println(descrever(a, false));
        }

        int id = lerInteiro("\nNúmero (#) do aluguel que deseja devolver (0 para cancelar): ");
        if (id == 0) {
            System.out.println("Devolução cancelada.");
            return;
        }
        mostrarResultadoDevolucao(gerenciadorAlugueis.devolverJogo(cliente, id));
    }

    // ===================== MENU DO ADMINISTRADOR =====================

    // Laço do menu do administrador logado. As opções 1 a 4 (jogos e usuários) são de outros integrantes
    // e ainda não foram integradas; a opção 5 abre o submenu de aluguéis.
    public void executarMenuAdmin(Adim admin) {
        avisarDataSimulada();
        int opcao;
        do {
            admin.exibirMenuEspercifico();
            System.out.println("5.Gerenciar Alugueis (consultas, registrar aluguel/devolução, simular dias)");
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                case 2:
                case 3:
                case 4:
                    // TODO (Gestão de Jogos e Usuários): ligar estas opções quando forem implementadas
                    System.out.println("  [EM DESENVOLVIMENTO] Esta opção ainda não foi integrada ao sistema.");
                    break;
                case 5:
                    menuGerenciarAlugueis(admin);
                    break;
                case 0:
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("  [ATENÇÃO] Opção inválida. Escolha uma opção do menu (0 a 5).");
            }
        } while (opcao != 0);
    }

    private void menuGerenciarAlugueis(Adim admin) {
        int opcao;
        do {
            System.out.println("\n --- GERENCIAR ALUGUEIS ---");
            System.out.println("1.Consultar todos os alugueis (historico geral)");
            System.out.println("2.Consultar alugueis em aberto");
            System.out.println("3.Consultar alugueis atrasados");
            System.out.println("4.Ver perfil e historico de um cliente");
            System.out.println("5.Registrar aluguel para um cliente");
            System.out.println("6.Registrar devolucao");
            System.out.println("7.Simular passagem de dias (para testar a multa)");
            System.out.println("0.Voltar");
            opcao = lerInteiro("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1:
                        consultar("TODOS OS ALUGUÉIS", gerenciadorAlugueis.listarTodos(),
                                "Nenhum aluguel foi registrado ainda.");
                        break;
                    case 2:
                        consultar("ALUGUÉIS EM ABERTO", gerenciadorAlugueis.listarEmAberto(),
                                "Não há aluguéis em aberto.");
                        break;
                    case 3:
                        consultar("ALUGUÉIS ATRASADOS", gerenciadorAlugueis.listarAtrasados(),
                                "Não há aluguéis atrasados.");
                        break;
                    case 4:
                        verPerfilDeCliente();
                        break;
                    case 5:
                        registrarAluguelParaCliente(admin);
                        break;
                    case 6:
                        registrarDevolucao(admin);
                        break;
                    case 7:
                        simularDias();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("  [ATENÇÃO] Opção inválida. Escolha uma opção do menu (0 a 7).");
                }
            } catch (LocadoraException e) {
                System.out.println("\n  [ERRO] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void consultar(String titulo, List<Aluguel> lista, String mensagemVazia) {
        System.out.println("\n=== " + titulo + " ===");
        if (lista.isEmpty()) {
            System.out.println(mensagemVazia);
            return;
        }
        for (Aluguel a : lista) {
            System.out.println(descrever(a, true));
        }
        System.out.println("\nTotal: " + lista.size() + " aluguel(is).");
    }

    private void verPerfilDeCliente() throws LocadoraException {
        System.out.println("\n=== PERFIL DE CLIENTE ===");
        String login = lerTexto("Login do cliente: ");
        mostrarPerfil(buscarCliente(login), "PERFIL E HISTÓRICO DE ALUGUÉIS");
    }

    private void registrarAluguelParaCliente(Adim admin) throws LocadoraException {
        System.out.println("\n=== REGISTRAR ALUGUEL PARA UM CLIENTE ===");
        String login = lerTexto("Login do cliente: ");
        Customer cliente = buscarCliente(login);
        System.out.println("Cliente: " + cliente.getNome() + " | aluguéis ativos: "
                + gerenciadorAlugueis.contarAlugueisEmAberto(cliente) + " de " + GerenciadorAlugueis.LIMITE_ALUGUEIS);

        Jogos jogo = escolherJogo();
        if (jogo == null) {
            return;
        }
        mostrarResultadoAluguel(gerenciadorAlugueis.realizarAluguel(cliente, jogo));
    }

    private void registrarDevolucao(Adim admin) throws LocadoraException {
        System.out.println("\n=== REGISTRAR DEVOLUÇÃO ===");
        List<Aluguel> emAberto = gerenciadorAlugueis.listarEmAberto();
        if (emAberto.isEmpty()) {
            System.out.println("Não há aluguéis em aberto.");
            return;
        }
        for (Aluguel a : emAberto) {
            System.out.println(descrever(a, true));
        }

        int id = lerInteiro("\nNúmero (#) do aluguel a devolver (0 para cancelar): ");
        if (id == 0) {
            System.out.println("Devolução cancelada.");
            return;
        }
        mostrarResultadoDevolucao(gerenciadorAlugueis.devolverJogo(admin, id));
    }

    // Opção de teste: avança a data do sistema para o prazo vencer e a multa poder ser demonstrada
    private void simularDias() {
        System.out.println("\n=== SIMULAR PASSAGEM DE DIAS ===");
        System.out.println("Data atual do sistema: " + data(GerenciadorAlugueis.hoje()));
        System.out.println("Regra de multa: " + gerenciadorAlugueis.getCalculadora().getDescricao() + ".");
        int dias = lerInteiro("Quantos dias deseja avançar? ");
        if (dias <= 0) {
            System.out.println("  [ATENÇÃO] Informe uma quantidade de dias maior que zero.");
            return;
        }
        GerenciadorAlugueis.avancarDias(dias);
        System.out.println("Nova data do sistema: " + data(GerenciadorAlugueis.hoje()));
    }

    private void avisarDataSimulada() {
        if (GerenciadorAlugueis.getDiasSimulados() != 0) {
            System.out.println("\n  *** Data do sistema simulada: " + data(GerenciadorAlugueis.hoje())
                    + " (" + GerenciadorAlugueis.getDiasSimulados() + " dia(s) à frente da data real) ***");
        }
    }

    // ===================== PARTES COMUNS =====================

    // Mostra o acervo numerado e devolve o jogo escolhido (null se o acervo está vazio ou o usuário cancelou)
    private Jogos escolherJogo() {
        List<Jogos> jogos = gerenciadorJogos.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Não há jogos cadastrados no acervo.");
            return null;
        }
        listarJogos();

        int numero;
        while (true) {
            numero = lerInteiro("\nNúmero do jogo (0 para cancelar): ");
            if (numero >= 0 && numero <= jogos.size()) {
                break;
            }
            System.out.println("  [ATENÇÃO] Número inválido. Digite um valor entre 0 e " + jogos.size() + ".");
        }
        if (numero == 0) {
            System.out.println("Operação cancelada.");
            return null;
        }
        return jogos.get(numero - 1);
    }

    private Customer buscarCliente(String login) throws LocadoraException {
        User usuario = userService.buscarPorLogin(login);
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException(login);
        }
        if (!(usuario instanceof Customer)) {
            throw new OperacaoNaoPermitidaException("\"" + login
                    + "\" é administrador: aluguéis e histórico existem apenas para clientes.");
        }
        return (Customer) usuario;
    }

    private void mostrarResultadoAluguel(Aluguel aluguel) {
        System.out.println("\nAluguel realizado com sucesso!");
        System.out.println("Jogo: " + aluguel.getJogo().getTitulo() + " (" + aluguel.getJogo().getPlataforma() + ")");
        System.out.println("Devolução prevista para: " + data(aluguel.getDataPrevistaDevolucao())
                + " (prazo de " + GerenciadorAlugueis.PRAZO_DEVOLUCAO + " dias)");
        System.out.println("Atenção: " + gerenciadorAlugueis.getCalculadora().getDescricao() + ".");
    }

    private void mostrarResultadoDevolucao(Aluguel aluguel) {
        System.out.println("\nDevolução registrada: " + aluguel.getJogo().getTitulo()
                + " (cliente: " + aluguel.getUsuario().getLogin() + ")");
        if (aluguel.getValorPenalidade() > 0) {
            long dias = aluguel.diasDeAtraso(aluguel.getDataDevolucao());
            System.out.println("Devolução com " + dias + " dia(s) de atraso. Multa a pagar: "
                    + moeda(aluguel.getValorPenalidade()));
        } else {
            System.out.println("Devolvido dentro do prazo. Nenhuma multa.");
        }
    }

    // Perfil do cliente (dados + resumo) seguido do histórico de aluguéis
    private void mostrarPerfil(Customer cliente, String titulo) {
        System.out.println("\n=== " + titulo + " ===");
        System.out.println("Nome:  " + cliente.getNome());
        System.out.println("Login: " + cliente.getLogin());
        System.out.println("Tipo:  Cliente");

        double totalMultas = 0;
        for (Aluguel a : cliente.getHistoricoAlugueis()) {
            totalMultas += a.getValorPenalidade();
        }
        System.out.println("Aluguéis ativos: " + gerenciadorAlugueis.contarAlugueisEmAberto(cliente)
                + " de " + GerenciadorAlugueis.LIMITE_ALUGUEIS);
        System.out.println("Total de multas: " + moeda(totalMultas));
        System.out.println("Regra de multa:  " + gerenciadorAlugueis.getCalculadora().getDescricao() + ".");

        System.out.println("\nHistórico de aluguéis:");
        if (cliente.getHistoricoAlugueis().isEmpty()) {
            System.out.println("Nenhum aluguel realizado ainda.");
            return;
        }
        for (Aluguel a : cliente.getHistoricoAlugueis()) {
            System.out.println(descrever(a, false));
        }
    }

    private void listarJogos() {
        System.out.println("\n=== ACERVO ===");
        List<Jogos> jogos = gerenciadorJogos.getJogos();
        if (jogos.isEmpty()) {
            System.out.println("Não há jogos cadastrados no acervo.");
            return;
        }
        for (int i = 0; i < jogos.size(); i++) {
            Jogos j = jogos.get(i);
            System.out.println(String.format("%2d. %-35s %-12s %-12s disponíveis: %d",
                    i + 1, j.getTitulo(), j.getPlataforma(), j.getGenero(), j.getQtdEmEstoque()));
        }
    }

    private void avisarAtrasos(Customer cliente) {
        int atrasados = 0;
        for (Aluguel a : gerenciadorAlugueis.listarAtivosDoUsuario(cliente)) {
            if (a.isAtrasado(GerenciadorAlugueis.hoje())) {
                atrasados++;
            }
        }
        if (atrasados > 0) {
            System.out.println("\n  *** AVISO: você tem " + atrasados + " aluguel(is) em atraso! ("
                    + gerenciadorAlugueis.getCalculadora().getDescricao() + ") ***");
        }
    }

    // Leitura segura: repete a pergunta até o usuário digitar um número inteiro
    private int lerInteiro(String mensagem) {
        while (true) {
            String texto = lerLinha(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                if (texto.isEmpty()) {
                    System.out.println("  [ATENÇÃO] Você não digitou nada. Digite um número inteiro (ex.: 1).");
                } else {
                    System.out.println("  [ATENÇÃO] \"" + texto + "\" não é um número válido. Digite apenas números inteiros (ex.: 1).");
                }
            }
        }
    }

    // Leitura de texto obrigatório: repete até o usuário digitar alguma coisa
    private String lerTexto(String mensagem) {
        while (true) {
            String texto = lerLinha(mensagem);
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  [ATENÇÃO] Este campo não pode ficar vazio. Digite um valor.");
        }
    }

    private String lerLinha(String mensagem) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) {
            System.out.println("\nEntrada encerrada.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    // Descreve um aluguel em uma linha. Se estiver atrasado e em aberto, mostra a multa acumulada até hoje.
    private String descrever(Aluguel a, boolean mostrarCliente) {
        LocalDate hoje = GerenciadorAlugueis.hoje();
        String situacao;
        if (a.isDevolvido()) {
            situacao = "Devolvido em " + data(a.getDataDevolucao()) + " | Multa: " + moeda(a.getValorPenalidade());
        } else if (a.isAtrasado(hoje)) {
            long dias = a.diasDeAtraso(hoje);
            situacao = "ATRASADO (" + dias + " dia(s)) | Multa se devolver hoje: "
                    + moeda(gerenciadorAlugueis.getCalculadora().calcular(dias));
        } else {
            situacao = "Em andamento";
        }
        String cliente = mostrarCliente ? " | Cliente: " + a.getUsuario().getLogin() : "";
        return String.format("#%d | %s (%s)%s | Alugado em %s | Prazo: %s | %s",
                a.getId(), a.getJogo().getTitulo(), a.getJogo().getPlataforma(), cliente,
                data(a.getDataAluguel()), data(a.getDataPrevistaDevolucao()), situacao);
    }

    private String data(LocalDate data) {
        return data.format(FORMATO_DATA);
    }

    private String moeda(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", valor);
    }
}
