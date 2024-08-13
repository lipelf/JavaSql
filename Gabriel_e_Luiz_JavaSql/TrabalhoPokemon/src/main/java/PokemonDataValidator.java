import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonDataValidator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost/pokemon";
        String user = "root";
        String password = "Unesc";

        List<Pokemon> pokemonList = new ArrayList<>();
        Map<String, Integer> totalizadorMap = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            pokemonList = fetchPokemonList(conn);
            totalizadorMap = fetchPokemonCountByType(conn);

            validateResults(pokemonList, totalizadorMap);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Pokemon> fetchPokemonList(Connection conn) throws SQLException {
        List<Pokemon> pokemonList = new ArrayList<>();
        String query = "SELECT tabela, id, pokemon, tipo " +
                       "FROM (" +
                       "    SELECT 'tb_pokemon' AS tabela, id, pokemon, tipo FROM tb_pokemon " +
                       "    UNION " +
                       "    SELECT 'tb_pokemon_eletrico' AS tabela, id, pokemon, tipo FROM tb_pokemon_eletrico " +
                       "    UNION " +
                       "    SELECT 'tb_pokemon_fogo' AS tabela, id, pokemon, tipo FROM tb_pokemon_fogo " +
                       "    UNION " +
                       "    SELECT 'tb_pokemon_voador' AS tabela, id, pokemon, tipo FROM tb_pokemon_voador" +
                       ") sub";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String tabela = rs.getString("tabela");
                int id = rs.getInt("id");
                String nome = rs.getString("pokemon");
                String tipo = rs.getString("tipo");
                pokemonList.add(new Pokemon(tabela, id, nome, tipo));
            }
        }
        return pokemonList;
    }

    private static Map<String, Integer> fetchPokemonCountByType(Connection conn) throws SQLException {
        Map<String, Integer> totalizadorMap = new HashMap<>();
        String query = "SELECT tabela, tipo, quantidade " +
                       "FROM (" +
                       "    SELECT 'tb_pokemon' AS tabela, tipo, COUNT(*) AS quantidade FROM tb_pokemon GROUP BY tipo " +
                       "    UNION " +
                       "    SELECT 'tb_pokemon_eletrico' AS tabela, tipo, COUNT(*) AS quantidade FROM tb_pokemon_eletrico GROUP BY tipo " +
                       "    UNION " +
                       "    SELECT 'tb_pokemon_fogo' AS tabela, tipo, COUNT(*) AS quantidade FROM tb_pokemon_fogo GROUP BY tipo " +
                       "    UNION " +
                       "    SELECT 'tb_pokemon_voador' AS tabela, tipo, COUNT(*) AS quantidade FROM tb_pokemon_voador GROUP BY tipo" +
                       ") sub";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String tabela = rs.getString("tabela");
                String tipo = rs.getString("tipo");
                int quantidade = rs.getInt("quantidade");
                totalizadorMap.put(tabela + ":" + tipo, quantidade);
            }
        }
        return totalizadorMap;
    }

    private static void validateResults(List<Pokemon> pokemonList, Map<String, Integer> totalizadorMap) {
        Map<String, List<Integer>> pokemonMap = new HashMap<>();
        for (Pokemon pokemon : pokemonList) {
            String key = pokemon.getNome() + ":" + pokemon.getTipo();
            if (!pokemonMap.containsKey(key)) {
                pokemonMap.put(key, new ArrayList<>());
            }
            pokemonMap.get(key).add(pokemon.getId());
        }

        for (Map.Entry<String, List<Integer>> entry : pokemonMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println("Duplicidade encontrada para: " + entry.getKey() + " com IDs: " + entry.getValue());
            }
        }

        for (Map.Entry<String, Integer> entry : totalizadorMap.entrySet()) {
            System.out.println("Tabela: " + entry.getKey().split(":")[0] + ", Tipo: " + entry.getKey().split(":")[1] + ", Quantidade: " + entry.getValue());
        }
    }

    static class Pokemon {
        private String tabela;
        private int id;
        private String nome;
        private String tipo;

        public Pokemon(String tabela, int id, String nome, String tipo) {
            this.tabela = tabela;
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
        }

        public String getTabela() {
            return tabela;
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
