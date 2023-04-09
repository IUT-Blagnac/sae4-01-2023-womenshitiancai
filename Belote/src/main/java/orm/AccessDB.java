package orm;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JOptionPane;


public class AccessDB {
    
    private static AccessDB instance = null;

    private Connection connection;
    private Statement statement;

    public static AccessDB getInstance() {
        if (instance == null) {
            instance = new AccessDB();
        }
        return instance;
    }

    private AccessDB() { }

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

    private void importSQL(Connection conn, File in) throws SQLException, IOException {
        Scanner s = new Scanner(in);
        s.useDelimiter("(;(\r)?\n)|(--\n)");
        Statement st = null;
        try
        {
            st = conn.createStatement();
            while (s.hasNext())
            {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/"))
                {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }
                
                if (line.trim().length() > 0)
                {
                    //System.out.println("Req:" + line);
                    st.execute(line);
                }
            }
        }
        finally
        {
            if (st != null) st.close();
        }
    }

    public Statement getStatement() {
        return statement;
    }


    // public static int chercherTournoi(String nomTournoi) throws SQLException {
    //     // Connection conn = /* obtenir la connexion à la base de données */;
    //     PreparedStatement ps = conn.prepareStatement("SELECT * FROM tournois WHERE nom_tournoi = ?");
    //     ps.setString(1, nomTournoi);
    //     ResultSet rs = ps.executeQuery();

    //     if (!rs.next()) {
    //         return -1; // le tournoi n'a pas été trouvé
    //     }

    //     int statut = rs.getInt("statut");
    //     int idTournoi = rs.getInt("id_tournoi");

    //     rs.close();
    //     ps.close();

    //     return idTournoi;
    // }
}