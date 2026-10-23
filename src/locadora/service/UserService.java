package locadora.service;
import locadora.model.Adim;
import locadora.model.Customer;
import locadora.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserService {
    private final List<User>listarUser;


    public UserService(){
        this.listarUser = new ArrayList<>();
        //Cadastrar um ADM padrão para testes iniciais
        boolean add = this.listarUser.add(new Adim("Adimin Principal", "adimin", "Pedro"));
    }

    //Cadastro de novo Customer

    public void cadastrarCustomer(String nome , String login , String senha){
        //verificação se já existe user com o mesmo login
        for(User u : listarUser){
            if (u.getLogin().equals(login)) {
                System.out.println("ERRO: Este login já está em uso!");
                return;
            }
        }
        listarUser.add(new Customer(nome,login,senha));
        System.out.println("Cliente cadastrado com sucesso!");
    }

    //Retorna o User autenticado
    public User realizarLogin(String loginInformado , String senhaInformada) {
        for (User u : listarUser) {
            if (u.autenticar(loginInformado, senhaInformada)) {
                System.out.println("Login realizado com sucesso! Bem-vindo(a)" + u.getNome());
                return u;

            }

        }
        System.out.println("ERRO: Login ou senha incorretos!");
        return null;
    }
}