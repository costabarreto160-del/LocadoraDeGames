package locadora.Gestao_Alugueis;

import locadora.Excecoes.PersistenciaException;
import locadora.Gestao_Jogos.GerenciadorJogos;
import locadora.Gestao_Jogos.Jogos;
import locadora.model.User;
import locadora.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RepositorioAlugueis {

    private static final String SEPARADOR = "|";
    private static final String SEM_DATA = "-";

    private final Path caminho;


    private final List<String> linhasPendentes = new ArrayList<>();

    public RepositorioAlugueis() {
        this(Paths.get("Alugueis.txt"));
    }

    public RepositorioAlugueis(Path caminho) {
        this.caminho = caminho;
    }

    public List<Aluguel> carregar(UserService userService, GerenciadorJogos gerenciadorJogos)
            throws PersistenciaException {

        List<Aluguel> lista = new ArrayList<>();
        linhasPendentes.clear();

        if (!Files.exists(caminho)) {
            return lista;
        }

        List<String> linhas;
        try {
            linhas = Files.readAllLines(caminho, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o arquivo " + caminho + ".", e);
        }

        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            if (linha.trim().isEmpty()) {
                continue;
            }

            String[] c = linha.split("\\|", -1);
            if (c.length != 8) {
                throw new PersistenciaException("Arquivo " + caminho + ", linha " + (i + 1)
                        + " inválida: esperados 8 campos separados por '|', encontrados " + c.length + ".");
            }

            try {
                int id = Integer.parseInt(c[0]);
                LocalDate dataAluguel = LocalDate.parse(c[4]);
                LocalDate dataPrevista = LocalDate.parse(c[5]);
                LocalDate devolucao = c[6].equals(SEM_DATA) ? null : LocalDate.parse(c[6]);
                double multa = Double.parseDouble(c[7]);
                Aluguel.reservarId(id);

                User usuario = userService.buscarPorLogin(c[1]);
                Jogos jogo = gerenciadorJogos.buscar(c[2], c[3]);
                if (usuario == null || jogo == null) {
                    linhasPendentes.add(linha);
                    continue;
                }
                lista.add(new Aluguel(id, usuario, jogo, dataAluguel, dataPrevista, devolucao, multa));

            } catch (NumberFormatException | DateTimeParseException e) {
                throw new PersistenciaException("Arquivo " + caminho + ", linha " + (i + 1)
                        + " inválida: número ou data em formato incorreto (datas devem ser AAAA-MM-DD).");
            }
        }

        if (!linhasPendentes.isEmpty()) {
            System.out.println("Aviso: " + linhasPendentes.size() + " aluguel(is) do arquivo não foram carregados "
                    + "porque o usuário ou o jogo correspondente ainda não existe. Eles serão preservados no arquivo.");
        }
        return lista;
    }

    public void salvar(List<Aluguel> alugueis) throws PersistenciaException {

        List<String> linhas = new ArrayList<>(linhasPendentes);

        for (Aluguel a : alugueis) {
            String devolucao = a.getDataDevolucao() == null ? SEM_DATA : a.getDataDevolucao().toString();
            linhas.add(String.join(SEPARADOR,
                    String.valueOf(a.getId()),
                    campo(a.getUsuario().getLogin()),
                    campo(a.getJogo().getTitulo()),
                    campo(a.getJogo().getPlataforma()),
                    a.getDataAluguel().toString(),
                    a.getDataPrevistaDevolucao().toString(),
                    devolucao,
                    String.format(Locale.ROOT, "%.2f", a.getValorPenalidade())));
        }

        try {
            Files.write(caminho, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível gravar o arquivo " + caminho + ".", e);
        }
    }


    private String campo(String valor) throws PersistenciaException {
        if (valor.contains(SEPARADOR)) {
            throw new PersistenciaException("Não é possível gravar \"" + valor
                    + "\": o caractere '|' é reservado do arquivo de aluguéis. Remova-o do nome.");
        }
        return valor;
    }
}
