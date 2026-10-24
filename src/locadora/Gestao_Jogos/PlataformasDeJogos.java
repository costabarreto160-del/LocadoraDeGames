package GestaoDeJogos;

import java.util.ArrayList;

public class PlataformasDeJogos{
    ArrayList<Jogos> jogo=new ArrayList<>();

    public ArrayList<Jogos> vereficar_Plataforma(String plataformaProcurada){
        ArrayList<Jogos> plataforma=new ArrayList<>();
        for (Jogos j:jogo) {
            if(j.plataforma.equals(plataformaProcurada)){
                plataforma.add(j);
            }
        }
        return plataforma;
    }
}