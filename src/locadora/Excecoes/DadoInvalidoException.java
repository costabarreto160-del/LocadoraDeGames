package locadora.Excecoes;

// Dado informado que não passou na validação (ex.: data inválida)
public class DadoInvalidoException extends LocadoraException {

    public DadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
