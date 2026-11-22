package locadora.Gestao_Alugueis;

import java.util.Locale;

// REGRA ADOTADA PELA EQUIPE: multa de R$ 2,00 por dia de atraso.
// Devolver até a data prevista (inclusive) não gera multa.
public class PenalidadePorDia implements CalculadoraPenalidade {

    public static final double VALOR_POR_DIA = 2.00;

    @Override
    public double calcular(long diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return diasAtraso * VALOR_POR_DIA;
    }

    @Override
    public String getDescricao() {
        return String.format(Locale.forLanguageTag("pt-BR"),
                "multa de R$ %.2f por dia de atraso na devolução", VALOR_POR_DIA);
    }
}
