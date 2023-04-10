import java.sql.SQLException;

import javax.swing.JFrame;

import dao.AccessDB;
import view.MainFrame;


public class Belote {    
    
    public static void main(String[] args) throws SQLException {
        AccessDB access = AccessDB.getInstance();
        access.initializeDatabase();
        MainFrame f = new MainFrame(access.getStatement());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
