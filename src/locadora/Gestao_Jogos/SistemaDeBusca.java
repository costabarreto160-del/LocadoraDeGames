package GestaoDeJogos;

import java.util.ArrayList;

public class SistemaDeBusca {
    ArrayList<Jogos> jogo=new ArrayList<>();

    public ArrayList<Jogos> sistema_Busca(String titulo,String plataforma,String genero){
        ArrayList<Jogos> busca=new ArrayList<>();
        for(Jogos j : jogo){
            if(j.titulo.contains(titulo) && j.plataforma.equals(plataforma) && j.genero.equals(genero)){
                busca.add(j);
            }
        }
        return busca;
    }
}
