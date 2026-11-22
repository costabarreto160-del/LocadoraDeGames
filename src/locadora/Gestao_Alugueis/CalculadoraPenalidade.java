package locadora.Gestao_Alugueis;

public interface CalculadoraPenalidade {

    double calcular(long diasAtraso);

    // texto que explica a regra de multa para o usuário
    String getDescricao();
}
