package locadora.Excecoes;

public class AluguelNaoEncontradoException extends LocadoraException {

    public AluguelNaoEncontradoException(int id) {
        super("Aluguel de número " + id + " não encontrado. Confira o número na lista exibida.");
    }
}