package GestaoDeJogos;
import locadora.model.Usuario;

public class Jogos {
    public String titulo;
    public String plataforma;
    public String genero;
    public int qtdEmEstoque;
    private Usuario comprador;

    public Jogos(String titulo,String plataforma,String genero,int qtdEmEstoque,Usuario comprador){
        this.titulo=titulo;
        this.plataforma=plataforma;
        this.genero=genero;
        this.qtdEmEstoque=qtdEmEstoque;
        this.comprador=comprador;
    }
    public String getTitulo(){
        return titulo;
    }
    public String getPlataforma(){
        return plataforma;
    }
    public String getGenero(){
        return genero;
    }
    public int getQtdEmEstoque(){
        return qtdEmEstoque;
    }
    public void setComprador(Usuario comprador){
        this.comprador=comprador;
    }
}
