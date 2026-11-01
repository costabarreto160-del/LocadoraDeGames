package locadora.Excecoes;

public class JogoIndisponivelException extends LocadoraException {

    public JogoIndisponivelException(String titulo) {
        super("O jogo \"" + titulo + "\" não tem unidades disponíveis no momento. "
                + "Escolha outro jogo ou tente novamente mais tarde.");
    }
}