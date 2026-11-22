package locadora.Excecoes;

// Nenhum usuário cadastrado com o login informado
public class UsuarioNaoEncontradoException extends LocadoraException {

    public UsuarioNaoEncontradoException(String login) {
        super("Usuário não encontrado: não existe usuário com o login \"" + login + "\".");
    }
}
