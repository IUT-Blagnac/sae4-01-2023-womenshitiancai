package orm;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import orm.AccessDB;

import utils.fenetreErreur;

public class AccesTournoi {
    fenetreErreur alert = new fenetreErreur();
    AccessDB connection = new AccessDB();
    
    private Statement statement = connection.getStatement();
    
    public ResultSet getTournoisByName(String name){
        try {
            return this.statement.executeQuery("SELECT * FROM tournois WHERE nom_tournoi = '" + name + "';");
        } catch (SQLException e) {
            alert.alertConnectionBD();
            e.printStackTrace();
        }
        return null;
    }
    
    public ResultSet getTournoisByID(int id){
        try {
            return this.statement.executeQuery("SELECT * FROM matchs WHERE id_tournoi="+ id + ";");
        } catch (SQLException e) {
            alert.alertConnectionBD();
            e.printStackTrace();
        }
        return null;
    }
    
    public ResultSet getTournoisByID(int id){
        return this.statement.executeQuery("SELECT * FROM matchs WHERE id_tournoi="+ id + ";");
    }
}
