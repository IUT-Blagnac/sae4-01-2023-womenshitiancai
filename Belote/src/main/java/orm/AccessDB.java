package orm;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AccessDB {
    
    private Connection connection;
    private Statement statement;

    public void initializeDatabase() {
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            
            String dos = System.getProperty("user.dir")+ "\\src\\resources";
            System.out.println("Dossier de stockage: " + dos);
            if( !new File(dos).isDirectory() ){
                new File(dos).mkdir();
            }
            connection = DriverManager.getConnection("jdbc:hsqldb:file:" + dos + "\\bd","sa","");
            statement = connection.createStatement();

            importSQL(connection, new File(dos+"\\create.sql"));
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de donnée. Vérifier qu'une autre instance du logiciel n'est pas déjà ouverte.");
            System.out.println(e.getMessage());
            System.exit(0);
        } catch(Exception e) {  
            JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du logiciel. Vérifiez votre installation Java et vos droits d'accès sur le dossier AppData.");
            System.out.println(e.getMessage());
            System.exit(0);
        } 
    }

    private void importSQL(Connection connection, File sqlFile) throws SQLException, IOException {
        // implémentation de la méthode importSQL
    }

    public Statement getStatement() {
        return statement;
    }
}