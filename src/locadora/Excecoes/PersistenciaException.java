package locadora.Excecoes;

// Erro ao ler ou gravar os arquivos .txt do sistema
public class PersistenciaException extends LocadoraException {

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
