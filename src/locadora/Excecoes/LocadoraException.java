package locadora.Excecoes;

public class LocadoraException extends Exception {

    public LocadoraException(String mensagem) {
        super(mensagem);
    }

    public LocadoraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
