package Teste0112;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemDoacao {
    private int id;
    private String nomeAlimento;
    private int unidadesDisponiveis;

    public ItemDoacao(String nomeAlimento, int unidadesDisponiveis) {
        this.nomeAlimento = nomeAlimento;
        this.unidadesDisponiveis = unidadesDisponiveis;
    }

    public void cadastrarItemDoacao(Connection connection) throws SQLException {
        String sql = "INSERT INTO ItemDoacao (nomeAlimento, unidadesDisponiveis) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, nomeAlimento);
            preparedStatement.setInt(2, unidadesDisponiveis);

            preparedStatement.executeUpdate();

            // Obtém o ID gerado
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
        }
    }
    //Método para atualizar o item de doação no banco de dados
    public void atualizarItemDoacao(Connection connection) throws SQLException {
        if (id == 0) {
            cadastrarItemDoacao(connection);
        } else {
            //Atualize as informações do item de doação no banco de dados
            String sql = "UPDATE ItemDoacao SET nomeAlimento = ?, unidadesDisponiveis = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, nomeAlimento);
                preparedStatement.setInt(2, unidadesDisponiveis);
                preparedStatement.setInt(3, id);
                preparedStatement.executeUpdate();
            }
        }
    }


    public int getId() {
        return id;
    }

    public String getNomeAlimento() {
        return nomeAlimento;
    }

    public int getUnidadesDisponiveis() {
        return unidadesDisponiveis;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNomeAlimento(String nomeAlimento) {
        this.nomeAlimento = nomeAlimento;
    }

    public void setUnidadesDisponiveis(int unidadesDisponiveis) {
        this.unidadesDisponiveis = unidadesDisponiveis;
    }
}