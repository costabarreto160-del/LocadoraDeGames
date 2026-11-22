import locadora.Excecoes.LocadoraException;
import locadora.Gestao_Alugueis.GerenciadorAlugueis;
import locadora.Gestao_Alugueis.MenuAlugueis;
import locadora.Gestao_Alugueis.RepositorioAlugueis;
import locadora.Gestao_Jogos.GerenciadorJogos;
import locadora.Gestao_Jogos.Jogos;
import locadora.model.Adim;
import locadora.model.Customer;
import locadora.model.User;
import locadora.service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        GerenciadorJogos gerenciadorJogos = new GerenciadorJogos();
        // TEMPORÁRIO: jogos de teste até o cadastro/persistência de jogos ser integrado ao Main
        gerenciadorJogos.adicionar(new Jogos("God of War", "PlayStation", "Ação", 2));
        gerenciadorJogos.adicionar(new Jogos("Halo Infinite", "Xbox", "Ação", 1));
        gerenciadorJogos.adicionar(new Jogos("Mario Kart 8", "Nintendo", "Corrida", 3));
        gerenciadorJogos.adicionar(new Jogos("Elden Ring", "PC", "RPG", 2));

        GerenciadorAlugueis gerenciadorAlugueis;
        try {
            gerenciadorAlugueis = new GerenciadorAlugueis(new RepositorioAlugueis(), userService, gerenciadorJogos);
        } catch (LocadoraException e) {
            System.out.println("Não foi possível iniciar o sistema: " + e.getMessage());
            return;
        }
        MenuAlugueis menuAlugueis = new MenuAlugueis(scanner, gerenciadorAlugueis, gerenciadorJogos, userService);

        int opcao = -1;

        do {
            System.out.println("\n ---LOCADORA DE JOGOS--- ");
            System.out.println("1.Fazer Login");
            System.out.println("2.Cadastrar Novo Cliente");
            System.out.println("0.Sair");
            System.out.println("Escolha uma opção");

            // validação de entrada: lê a linha inteira e converte, sem quebrar se vier texto
            if (!scanner.hasNextLine()) {
                break; // entrada encerrada (Ctrl+D): sai do sistema
            }
            String entrada = scanner.nextLine().trim();
            try {
                opcao = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Digite apenas números correspondentes ao menu.");
                opcao = -1;
                continue; // volta a mostrar o menu
            }

            switch (opcao) {
                case 1:
                    System.out.println("Login:");
                    String login = scanner.nextLine();
                    System.out.println("Senha");
                    String senha = scanner.nextLine();

                    User userLogado = userService.realizarLogin(login, senha);
                    if (userLogado instanceof Customer) {
                        menuAlugueis.executarMenuCliente((Customer) userLogado);
                    } else if (userLogado instanceof Adim) {
                        menuAlugueis.executarMenuAdmin((Adim) userLogado);
                    } else if (userLogado != null) {
                        userLogado.exibirMenuEspercifico();
                    }
                    break;

                case 2:
                    System.out.println("Nome");
                    String nome = scanner.nextLine();
                    System.out.println("Novo Login");
                    String novoLogin = scanner.nextLine();
                    System.out.println("Senha");
                    String novaSenha = scanner.nextLine();

                    userService.cadastrarCustomer(nome, novoLogin, novaSenha);
                    break;

                case 0:
                    System.out.println("Sair do Sistema !");
                    break;

                default:
                    System.out.println("Opção Inválida! Digite novamente");
            }

        } while (opcao != 0);
        scanner.close();
    }
}
