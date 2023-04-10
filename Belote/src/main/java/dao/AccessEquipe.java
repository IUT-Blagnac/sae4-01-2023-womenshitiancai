package dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import controller.Tournoi;
import dao.AccessDB;
import utils.FenetreErreur;

public class AccessEquipe {
    FenetreErreur alert = new FenetreErreur();
    AccessDB connection = AccessDB.getInstance();
    
    private Statement statement = connection.getStatement();
    
    public ResultSet getEquipeByID(int id, String order){
        try {
            return this.statement.executeQuery("SELECT * FROM equipes WHERE id_tournoi="+ id + " ORDER BY "+order+";");
        } catch (SQLException e) {
            alert.alertConnectionBD();
            e.printStackTrace();
        }
        return null;
    }  
    
    public void ajouterEquipe(Tournoi tournoi){
		int a_aj= tournoi.getEquipes().size()+1;
		for ( int i=1;i <= tournoi.getEquipes().size(); i++){
			if(!tournoi.getIdEquipes().contains(i)){
				a_aj=i;
				break;
			}
		}
		try {
			statement.executeUpdate("INSERT INTO equipes (id_equipe,num_equipe,id_tournoi,nom_j1,nom_j2) VALUES (NULL,"+a_aj+", "+tournoi.getIdTournoi() + ",'\"Joueur 1\"', '\"Joueur 2\"');");
		    tournoi.majEquipes();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void majEquipe(Tournoi tournoi, int index){
		try {
			String req = "UPDATE equipes SET nom_j1 = '" + Tournoi.mysql_real_escape_string(tournoi.getEquipe(index).getNomEquipe1()) + "', nom_j2 = '" + Tournoi.mysql_real_escape_string(tournoi.getEquipe(index).getNomEquipe2()) + "' WHERE id_equipe = " + tournoi.getEquipe(index).getIdEquipe() + ";";
			System.out.println(req);
			statement.executeUpdate(req);
		    tournoi.majEquipes();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

    public void supprimerEquipe(Tournoi tournoi, int ideq){
		try {
			int numeq;
			ResultSet rs = statement.executeQuery("SELECT num_equipe FROM equipes WHERE id_equipe = " + ideq);
			rs.next();
			numeq = rs.getInt(1);
			rs.close();
			statement.executeUpdate("DELETE FROM equipes WHERE id_tournoi = " + tournoi.getIdTournoi()+ " AND id_equipe = " + ideq);
			statement.executeUpdate("UPDATE equipes SET num_equipe = num_equipe - 1 WHERE id_tournoi = " + tournoi.getIdTournoi() + " AND num_equipe > " + numeq);
		    tournoi.majEquipes();
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
    
}
