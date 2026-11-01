package locadora.Excecoes;

public class LimiteAlugueisException extends LocadoraException {

    public LimiteAlugueisException(String nome, int limite) {
        super("O usuário " + nome + " já possui " + limite + " jogos alugados (limite máximo). "
                + "Devolva um jogo antes de fazer um novo aluguel.");
    }
}