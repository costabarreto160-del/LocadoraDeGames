package locadora.Gestao_Jogos;

import java.util.ArrayList;

public class GerenciadorJogos {
     ArrayList<Jogos> jogo=new ArrayList<>();
    
    public void adicionar(Jogos jogo){
       this.jogo.add(jogo);
    }
    public void remover(Jogos jogo){
        this.jogo.remove(jogo);
    }
    public ArrayList<Jogos> getJogos(){
        return jogo;
    }

    public Jogos buscar(String titulo, String plataforma){
        for (Jogos j : jogo) {
            if (j.getTitulo().equalsIgnoreCase(titulo) && j.getPlataforma().equalsIgnoreCase(plataforma)) {
                return j;
            }
        }
        return null;
    }
}