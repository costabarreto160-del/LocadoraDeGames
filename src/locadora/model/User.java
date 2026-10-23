package locadora.model;

public abstract class User { // classse abstrata para tratar não qualquer user, sim um Cliente ou ADM.
    private String nome;
    private String login;
    private String senha;

    public User(String nome, String login, String senha){
        this.nome = nome ;
        this.login = login;
        this.senha = senha;
    }

    public  void setNome(String nome ){
        this.nome = nome;
    }
    public  String getNome(){
        return nome;
    }
    public  void setLogin(String login){
        this.login = login;
    }
    public String getLogin(){
        return login;
    }
    public void setSenha(String senha ){
        this.senha = senha;
    }
    private  String getSenha(){
        return  senha;
    }
    public boolean autenticar(String loginInformado, String senhaInformada){
        return this.login.equals(loginInformado) && this.senha.equals(senhaInformada);
    }

    public abstract void exibirMenuEspercifico(); // Metodo para mostrar o menu para cada tipo de user

}
