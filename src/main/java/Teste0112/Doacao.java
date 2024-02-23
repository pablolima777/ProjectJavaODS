package Teste0112;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Doacao {
    private int id;
    private Pessoa pessoa;
    private ItemDoacao itemDoacao;

    public Doacao(int id, Pessoa pessoa, ItemDoacao itemDoacao) {
        this.id = id;
        this.pessoa = pessoa;
        this.itemDoacao = itemDoacao;
    }

    public int getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public ItemDoacao getItemDoacao() {
        return itemDoacao;
    }

    public void cadastrarDoacao(Connection connection) throws SQLException {
        //primeiro, cadastre a Pessoa e o ItemDoacao se ainda não estiverem no banco
        pessoa.cadastrarPessoa(connection);
        itemDoacao.cadastrarItemDoacao(connection);

        //Em seguida, cria a Doacao associando a pessoa e o itemDoacao
        String sql = "INSERT INTO Doacao (idPessoa, idItemDoacao) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, pessoa.getId());
            preparedStatement.setInt(2, itemDoacao.getId());

            preparedStatement.executeUpdate();

            //recebe o id gerado
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
        }
    }

    public static List<Doacao> listarDoacoesDisponiveis(Connection connection) throws SQLException {
        List<Doacao> doacoes = new ArrayList<>();
        String sql = "SELECT d.id, p.nome, p.endereco, i.id as idItemDoacao, i.nomeAlimento, i.unidadesDisponiveis FROM Doacao d " +
                "JOIN Pessoa p ON d.idPessoa = p.id " +
                "JOIN ItemDoacao i ON d.idItemDoacao = i.id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int idDoacao = resultSet.getInt("id");
                    String nomeDoador = resultSet.getString("nome");
                    String enderecoDoador = resultSet.getString("endereco");
                    int idItemDoacao = resultSet.getInt("idItemDoacao");
                    String nomeAlimento = resultSet.getString("nomeAlimento");
                    int unidadesDisponiveis = resultSet.getInt("unidadesDisponiveis");

                    Pessoa pessoa = new Pessoa(0, nomeDoador, enderecoDoador);
                    ItemDoacao itemDoacao = new ItemDoacao(nomeAlimento, unidadesDisponiveis);

                    Doacao doacao = new Doacao(idDoacao, pessoa, itemDoacao);
                    doacoes.add(doacao);
                }
            }
        }
        return doacoes;
    }

    public static void retirarDoacao(Connection connection, int idDoacao) throws SQLException {
        String sql = "DELETE FROM Doacao WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idDoacao);
            preparedStatement.executeUpdate();
        }
    }

    public static Doacao obterDoacaoPorId(Connection connection, int idDoacao) throws SQLException {
        String sql = "SELECT d.id, p.id as idPessoa, p.nome, p.endereco, i.id as idItemDoacao, i.nomeAlimento, i.unidadesDisponiveis " +
                "FROM Doacao d " +
                "JOIN Pessoa p ON d.idPessoa = p.id " +
                "JOIN ItemDoacao i ON d.idItemDoacao = i.id " +
                "WHERE d.id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idDoacao);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int idPessoa = resultSet.getInt("idPessoa");
                    String nomeDoador = resultSet.getString("nome");
                    String enderecoDoador = resultSet.getString("endereco");

                    int idItemDoacao = resultSet.getInt("idItemDoacao");
                    String nomeAlimento = resultSet.getString("nomeAlimento");
                    int unidadesDisponiveis = resultSet.getInt("unidadesDisponiveis");

                    Pessoa pessoa = new Pessoa(idPessoa, nomeDoador, enderecoDoador);
                    ItemDoacao itemDoacao = new ItemDoacao(nomeAlimento, unidadesDisponiveis);

                    return new Doacao(idDoacao, pessoa, itemDoacao);
                }
            }
        }
        return null; //Retorna null se a doação não for encontrada
    }

    public void atualizarDoacao(Connection connection) throws SQLException {

        //Verifica se o ItemDoacao existe; se não, cadastrar
        if (itemDoacao.getId() == 0) {
            itemDoacao.cadastrarItemDoacao(connection);
        } else {
            // Se o ItemDoacao já existe, atualizar suas informações
            itemDoacao.atualizarItemDoacao(connection);
        }

        //Após atualizar as informações necessárias, atualize a doacao
        String sql = "UPDATE Doacao SET idPessoa = ?, idItemDoacao = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, pessoa.getId());
            preparedStatement.setInt(2, itemDoacao.getId());
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
        }
    }
}