package dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import controller.Tournoi;
import dao.AccessDB;
import utils.FenetreErreur;

public class AccessTournoi {
    FenetreErreur alert = new FenetreErreur();
    AccessDB connection = AccessDB.getInstance();
    
    private Statement statement = connection.getStatement();

	public ResultSet getAllTournois(){
        try {
            return this.statement.executeQuery("SELECT * FROM tournois ;");
        } catch (SQLException e) {
            alert.alertConnectionBD();
            e.printStackTrace();
        }
        return null;
    }
    
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
    
    public ResultSet getTournoisByID(int id, String order){
        try {
            return this.statement.executeQuery("SELECT * FROM matchs WHERE id_tournoi="+ id + " ORDER BY "+order+";");
        } catch (SQLException e) {
            alert.alertConnectionBD();
            e.printStackTrace();
        }
        return null;
    }
    
    public ResultSet getTournoisDernierTour(int id){
        try{
            return this.statement.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi=" + id + ";");
        } catch (SQLException e) {
            alert.alertConnectionBD();
            e.printStackTrace();
        }
        return null;
    }
    
    public int deleteTournoi(Statement s2, String nomtournoi){
		try {
			int idt;
			ResultSet rs = s2.executeQuery("SELECT id_tournoi FROM tournois WHERE nom_tournoi = '" + Tournoi.mysql_real_escape_string(nomtournoi) + "';");
			rs.next();
			idt = rs.getInt(1);
			rs.close();
			System.out.println("ID du tournoi � supprimer:" + idt);
			s2.executeUpdate("DELETE FROM matchs   WHERE id_tournoi = " + idt);
			s2.executeUpdate("DELETE FROM equipes  WHERE id_tournoi = " + idt);
			s2.executeUpdate("DELETE FROM tournois WHERE id_tournoi = " + idt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur suppression" + e.getMessage());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur inconnue");
		} 
		return 0;
	}

    public int creerTournoi(Statement s2){
		String s = (String)JOptionPane.showInputDialog(
                null,
                "Entrez le nom du tournoi",
                "Nom du tournoi",
                JOptionPane.PLAIN_MESSAGE);
		
		
		if(s == null || s == ""){
			return 1;
		}else{
			try {
				s =  Tournoi.mysql_real_escape_string(s);
				if(s.length() < 3){
					JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Nom trop court.");
					return 2;					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(s == ""){
				JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Ne pas mettre de caract�res sp�ciaux ou accents dans le nom");
				return 2;
			}else{
				
				
				ResultSet rs;
				try {
					rs = s2.executeQuery("SELECT id_tournoi FROM tournois WHERE nom_tournoi = '" + s + "';");
					if(rs.next()){
						JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Un tournoi du m�me nom existe d�j�");
						return 2;							
					}
	
					System.out.println("INSERT INTO tournois (id_tournoi, nb_matchs, nom_tournoi, statut) VALUES (NULL, 10, '"+s+"', 0)");
				s2.executeUpdate("INSERT INTO tournois (id_tournoi, nb_matchs, nom_tournoi, statut) VALUES (NULL, 10, '"+s+"', 0)");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Erreur requete insertion nouveau tournoi:" + e.getMessage());
					
					//e.printStackTrace();
					
					
				}
				//s2.executeUpdate("INSERT INTO tournois (id")
				
			}
		}
		return 0;
	}
    
}
