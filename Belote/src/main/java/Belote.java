import java.sql.SQLException;

import javax.swing.JFrame;
import orm.AccessDB;
import view.Fenetre;


public class Belote {
    
    public static void main(String[] args) throws SQLException {
        AccessDB access = AccessDB.getInstance();
        access.initializeDatabase();
        Fenetre f = new Fenetre(access.getStatement());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}