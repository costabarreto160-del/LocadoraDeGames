package GestaoDeJogos;

import java.util.ArrayList;

public class ClassficacaoDosJogos{
    ArrayList<Jogos> jogo=new ArrayList<>();
     

    public ArrayList<Jogos> verificar_Genero(String generoProcurado){
        ArrayList<Jogos> genero=new ArrayList<>();
        for (Jogos j : jogo) {
            if(j.genero.equals(generoProcurado)){
                genero.add(j);
            }
        }
        return genero;
    }
}