import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;
import java.sql.Statement;  
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import view.Fenetre;


public class Belote {
    
    public static void main(String[] args) throws SQLException {
        
        Connection connection = null;  
        Statement statement = null;  		
        
        // Connection � la base de donn�es
        // et cr�ation des champs 
        
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
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de donn�e. Vérifier qu'une autre instance du logiciel n'est pas déjà ouverte.");
            System.out.println(e.getMessage());
            System.exit(0);
        }catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du logiciel. Vérifiez votre installation Java et vos droits d'acc�s sur le dossier AppData.");
            System.out.println(e.getMessage());
            System.exit(0);
        } 
        
        // Interface graphique
        
        Fenetre fenetre = new Fenetre(statement);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //statement.execute("SHUTDOWN;");
        //statement.close();  
        //connection.close();  		
    }
    
    
    public static void importSQL(Connection conn, File in) throws SQLException, FileNotFoundException
    {
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("(;(\r)?\n)|(--\n)");
        Statement statement = null;
        try
        {
            statement = conn.createStatement();
            while (scanner.hasNext())
            {
                String line = scanner.next();
                if (line.startsWith("/*!") && line.endsWith("*/"))
                {
                    int index = line.indexOf(' ');
                    line = line.substring(index + 1, line.length() - " */".length());
                }
                
                if (line.trim().length() > 0)
                {
                    //System.out.println("Req:" + line);
                    statement.execute(line);
                }
            }
        }
        finally
        {
            if (statement != null) statement.close();
        }
    }
}