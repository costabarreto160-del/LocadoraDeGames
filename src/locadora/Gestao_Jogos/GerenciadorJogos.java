package GestaoDeJogos;

import java.util.ArrayList;

public class GerenciadorJogos {
     ArrayList<Jogos> jogo=new ArrayList<>();
    
    public void adicionar(Jogos jogo){
       this.jogo.add(jogo);
    }
    public void remover(Jogos jogo){
        this.jogo.remove(jogo);
    }

}