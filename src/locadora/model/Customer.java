package locadora.model;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<String> historicoAlugueis; // integra com a classe alugueis.

    public Customer(String nome, String login, String senha){
        super(nome, login, senha);
        this.historicoAlugueis = new ArrayList<>();
    }

    public List<String> getHistoricoAlugueis() {
        return historicoAlugueis;
    }

    public void setHistoricoAlugueis(List<String> historicoAlugueis) {
        this.historicoAlugueis = historicoAlugueis;
    }

    @Override
    public void exibirMenuEspercifico(){
        System.out.println("\n --- MENU CLIENTE ---");
        System.out.println("1.Buscar Jogos");
        System.out.println("2.Realizar Aluguel");
        System.out.println("3.Devolver Jogo");
        System.out.println("4.Ver meu Historico");
        System.out.println("0.Sair");

    }
}
