package GestaoDeJogos;

public class Jogos {
    public String titulo;
    public String plataforma;
    public String genero;
    public int qtdEmEstoque;

    public Jogos(String titulo,String plataforma,String genero,int qtdEmEstoque){
        this.titulo=titulo;
        this.plataforma=plataforma;
        this.genero=genero;
        this.qtdEmEstoque=qtdEmEstoque;
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

}
