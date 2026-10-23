import locadora.model.User;
import locadora.service.UserService;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        int opcao = -1;

        do {
            System.out.println("\n ---LOCADORA DE JOGOS--- ");
            System.out.println("1.Fazer Login");
            System.out.println("2.Cadastrar Novo Cliente");
            System.out.println("0.Sair");
            System.out.println("Escolha uma opção");

            // validação de entrada
            if (scanner.hasNext()) {
                opcao = scanner.nextInt();
                scanner.nextLine(); // limpar o buffer

                switch (opcao) {
                    case 1:
                        System.out.println("Login:");
                        String login = scanner.nextLine();
                        System.out.println("Senha");
                        String senha = scanner.nextLine();

                        User userLogado = userService.realizarLogin(login, senha);
                        if (userLogado != null) {
                            userLogado.exibirMenuEspercifico();
                        }
                        break;

                    case 2:
                        System.out.println("Nome");
                        String nome = scanner.nextLine();
                        System.out.println("Novo Login");
                        String novoLogin = scanner.nextLine();
                        String novaSenha = scanner.nextLine();

                        userService.cadastrarCustomer(nome, novoLogin, novaSenha);
                        break;

                    case 0:
                        System.out.println("Sair do Sistema !");
                        break;

                    default:
                        System.out.println("Opção Inválida! Digite novamente");


                }
            }else{
                System.out.println("ERRO: Digite apenas números correspondentes ao menu.");
                scanner.next(); //Descarta entrada invalida
            }

        }while (opcao !=0);
            scanner.close();

    }
}