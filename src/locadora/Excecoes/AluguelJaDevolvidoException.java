package locadora.Excecoes;

public class AluguelJaDevolvidoException extends LocadoraException {

    public AluguelJaDevolvidoException(int id) {
        super("O aluguel " + id + " já foi devolvido anteriormente e não pode ser devolvido outra vez.");
    }
}