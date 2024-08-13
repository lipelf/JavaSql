import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao {

    public static void main(String[] args) throws SQLException {
        Connection conexao = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexao = DriverManager.getConnection("jdbc:mysql://localhost/pokemon", "root", "Unesc");
            ResultSet reCliente = conexao.createStatement().executeQuery("SELECT * FROM CLIENTE");
            while (reCliente.next()){
                System.out.println("Nome: " + reCliente.getString("pokemon"));
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver do banco de dados não localizado");
        } catch (SQLException ex) {
            System.out.println("Ocorreu um erro ao acessar o banco: " + ex.getMessage());
        } finally {
            if(conexao != null){
            conexao.close();
            }
        }

    }

}