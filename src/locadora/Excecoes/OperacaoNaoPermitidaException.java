package locadora.Excecoes;

// Operação que o usuário não tem permissão de fazer (ex.: devolver aluguel de outra pessoa)
public class OperacaoNaoPermitidaException extends LocadoraException {

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
