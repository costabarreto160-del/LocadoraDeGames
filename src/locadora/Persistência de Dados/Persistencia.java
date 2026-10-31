package PersistenciaDeDados;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import GestaoDeJogos.Jogos;


public class Persistencia {
    private Path caminho = Paths.get("Jogos.txt");



    public void salvarJogos(ArrayList<Jogos> Lista){
        List<String> linhas=new ArrayList<>();

        for (Jogos j : Lista) {
            String linha=j.getTitulo()+ ":" + j.getPlataforma() +":" +j.getGenero()+ ":" +j.getQtdEmEstoque();
            linhas.add(linha);
        }
        try {
            Files.write(caminho, linhas);
            System.out.println("Dados salvos com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar no arquivo: "+e.getMessage());
        }
        
    }
    public ArrayList<Jogos> carregarJogos() {
    ArrayList<Jogos> listaCarregada = new ArrayList<>();

    try {
        
        List<String> linhas = Files.readAllLines(caminho);

        
        for (String linha : linhas) {
            
            String[] dados = linha.split(":");

            
            String titulo = dados[0];
            String plataforma = dados[1];
            String genero = dados[2];
            int qtdEmEstoque = Integer.parseInt(dados[3]);

            
            Jogos jogo = new Jogos(titulo, plataforma, genero, qtdEmEstoque);
            listaCarregada.add(jogo);
        }

    } catch (IOException e) {
        System.out.println("Erro ao carregar o arquivo: " + e.getMessage());
    }

    return listaCarregada;
}
}
