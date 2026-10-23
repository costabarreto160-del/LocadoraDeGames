package locadora.model;

public class Adim extends User{

    public Adim(String nome , String login , String senha){
        super(nome, login, senha);
    }
    @Override
    public void exibirMenuEspercifico(){
        System.out.println("\n ---MENU ADMINISTRADOR");
        System.out.println("1.Cadastrar Jogo");
        System.out.println("2.Remover Novo Jogo");
        System.out.println("3.Editar Informações do Jogo");
        System.out.println("4.Gerenciar Usuarios");
        System.out.println("0.Sair");

    }
}
