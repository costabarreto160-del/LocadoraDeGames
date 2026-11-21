package locadora.model;

import java.util.ArrayList;
import java.util.List;

import locadora.Gestao_Alugueis.Aluguel;

public class Customer extends User {

    private List<Aluguel> historicoAlugueis;

    public Customer(String nome, String login, String senha) {
        super(nome, login, senha);
        this.historicoAlugueis = new ArrayList<>();
    }

    public List<Aluguel> getHistoricoAlugueis() {
        return historicoAlugueis;
    }

    public void setHistoricoAlugueis(List<Aluguel> historicoAlugueis) {
        this.historicoAlugueis = historicoAlugueis;
    }

    public void adicionarAoHistorico(Aluguel aluguel) {
        historicoAlugueis.add(aluguel);
    }

    @Override
    public void exibirMenuEspercifico() {
        System.out.println("\n --- MENU CLIENTE ---");
        System.out.println("1.Buscar Jogos");
        System.out.println("2.Realizar Aluguel");
        System.out.println("3.Devolver Jogo");
        System.out.println("4.Ver meu Historico");
        System.out.println("0.Sair");
    }
}
