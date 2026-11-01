package locadora.Gestao_Alugueis;

public class PenalidadePorDia implements CalculadoraPenalidade {

    public static final double VALOR_POR_DIA = 2.00;

    @Override
    public double calcular(long diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return diasAtraso * VALOR_POR_DIA;
    }
}