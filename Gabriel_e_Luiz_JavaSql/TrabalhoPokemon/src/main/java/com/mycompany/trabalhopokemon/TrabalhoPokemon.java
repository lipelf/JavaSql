
package com.mycompany.trabalhopokemon;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrabalhoPokemon {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost/pokemon";
        String user = "root";
        String password = "Unesc";

        List<Pokemon> pokemonList = new ArrayList<>();
        Map<String, Integer> totalizadorMap = new HashMap<>();

        // Conexão ao banco de dados
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Criar tabelas necessárias
            createTables(conn);

            // Buscar dados da tabela principal
            pokemonList = fetchPokemonList(conn);

            // Deletar duplicados da tabela principal e inserir na tabela de deletados
            for (Pokemon pokemon : pokemonList) {
                if (isPokemonDuplicated(conn, pokemon)) {
                    deletePokemon(conn, pokemon);
                    insertPokemon(conn, "tb_pokemon_deletados", pokemon);
                }
            }

            // Inserir dados nas tabelas específicas sem duplicatas e contar por tipo
            for (Pokemon pokemon : pokemonList) {
                if (pokemon.getTipo().equalsIgnoreCase("eletrico") || pokemon.getTipo().equalsIgnoreCase("elétrico")) {
                    if (!isPokemonExists(conn, "tb_pokemon_eletrico", pokemon)) {
                        insertPokemon(conn, "tb_pokemon_eletrico", pokemon);
                    }
                    totalizadorMap.put("eletrico", totalizadorMap.getOrDefault("eletrico", 0) + 1);
                } else if (pokemon.getTipo().equalsIgnoreCase("fogo")) {
                    if (!isPokemonExists(conn, "tb_pokemon_fogo", pokemon)) {
                        insertPokemon(conn, "tb_pokemon_fogo", pokemon);
                    }
                    totalizadorMap.put("fogo", totalizadorMap.getOrDefault("fogo", 0) + 1);
                } else if (pokemon.getTipo().equalsIgnoreCase("voador")) {
                    if (!isPokemonExists(conn, "tb_pokemon_voador", pokemon)) {
                        insertPokemon(conn, "tb_pokemon_voador", pokemon);
                    }
                    totalizadorMap.put("voador", totalizadorMap.getOrDefault("voador", 0) + 1);
                }
            }

            // Inserir dados na tabela tb_pokemon_totalizador
            for (Map.Entry<String, Integer> entry : totalizadorMap.entrySet()) {
                insertTotalizador(conn, entry.getKey(), entry.getValue());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String createTotalizadorTableQuery = "CREATE TABLE IF NOT EXISTS tb_pokemon_totalizador (" +
                                             "tipo VARCHAR(50) PRIMARY KEY, " +
                                             "quantidade INT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTotalizadorTableQuery);
        }

        String createDeletadosTableQuery = "CREATE TABLE IF NOT EXISTS tb_pokemon_deletados (" +
                                           "id INT PRIMARY KEY, " +
                                           "pokemon VARCHAR(50), " +
                                           "tipo VARCHAR(50))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createDeletadosTableQuery);
        }
    }

    private static List<Pokemon> fetchPokemonList(Connection conn) throws SQLException {
        List<Pokemon> pokemonList = new ArrayList<>();
        String selectQuery = "SELECT id, pokemon, tipo FROM tb_pokemon";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("pokemon");
                String tipo = rs.getString("tipo");
                pokemonList.add(new Pokemon(id, nome, tipo));
            }
        }
        return pokemonList;
    }

    private static boolean isPokemonDuplicated(Connection conn, Pokemon pokemon) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM tb_pokemon WHERE pokemon = ? AND tipo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setString(1, pokemon.getNome());
            pstmt.setString(2, pokemon.getTipo());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 1;
                }
            }
        }
        return false;
    }

    private static void deletePokemon(Connection conn, Pokemon pokemon) throws SQLException {
        String deleteQuery = "DELETE FROM tb_pokemon WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, pokemon.getId());
            pstmt.executeUpdate();
        }
    }

    private static boolean isPokemonExists(Connection conn, String tableName, Pokemon pokemon) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ? AND pokemon = ? AND tipo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setInt(1, pokemon.getId());
            pstmt.setString(2, pokemon.getNome());
            pstmt.setString(3, pokemon.getTipo());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static void insertPokemon(Connection conn, String tableName, Pokemon pokemon) throws SQLException {
        String insertQuery = "INSERT INTO " + tableName + " (id, pokemon, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, pokemon.getId());
            pstmt.setString(2, pokemon.getNome());
            pstmt.setString(3, pokemon.getTipo());
            pstmt.executeUpdate();
        }
    }

    private static void insertTotalizador(Connection conn, String tipo, int quantidade) throws SQLException {
        String insertQuery = "INSERT INTO tb_pokemon_totalizador (tipo, quantidade) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, tipo);
            pstmt.setInt(2, quantidade);
            pstmt.executeUpdate();
        }
    }

    static class Pokemon {
        private int id;
        private String nome;
        private String tipo;

        public Pokemon(int id, String nome, String tipo) {
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getTipo() {
            return tipo;
        }
    }
}

