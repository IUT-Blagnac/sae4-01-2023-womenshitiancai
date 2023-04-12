package dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import controller.Tournoi;
import model.Match;
import utils.FenetreErreur;

public class AccessMatch {

	private static AccessMatch instance = null;
	private AccessMatch(){}
	public static AccessMatch getInstance(){
		if (instance == null) instance = new AccessMatch();
		return instance;
	}

    FenetreErreur alert = new FenetreErreur();
    AccessDB connection = AccessDB.getInstance();

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

    public void genererMatchs(Tournoi tournoi){
		int nbt = 1;

		System.out.println("Nombre d'�quipes : " + tournoi.getNbEquipes());
		System.out.println("Nombre de tours  : " + nbt);
		String req = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
		Vector<Vector<Match>> ms;
		ms = Tournoi.getMatchsToDo(tournoi.getNbEquipes(), nbt);
		int z = 1;
		char v = ' ';
		for(Vector<Match> t :ms){
			for(Match m:t){
				req += v + "(NULL," + tournoi.getIdTournoi() + ", " + z + ", "+  m.getIdEquipe1() + ", " +  m.getIdEquipe2() + ", 'non')";
				v = ',';
			}
			req += "\n";
			z++;
		}
		System.out.println(req);
		try{
			statement.executeUpdate(req);
			statement.executeUpdate("UPDATE tournois SET statut=2 WHERE id_tournoi=" + tournoi.getIdTournoi() + ";");
			tournoi.setStatut(2);
		}catch(SQLException e){
			System.out.println("Erreur validation �quipes : " + e.getMessage());
		}
	}
    
    public boolean ajouterTour(Tournoi tournoi){
		// Recherche du nombre de tours actuel
		int nbtoursav;
		if(tournoi.getNbTours() >=  (tournoi.getNbEquipes() -1) ) return false;
		System.out.println("Eq:" + tournoi.getNbEquipes() + "  tours" + tournoi.getNbTours());
		try {
			ResultSet rs = statement.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi="+tournoi.getIdTournoi()+"; ");
			rs.next();
			nbtoursav = rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return false;
		}
		System.out.println("Nombre de tours avant:" + nbtoursav);
		
		
		if(nbtoursav == 0){
			Vector<Match> ms;
			
			ms = Tournoi.getMatchsToDo(tournoi.getNbEquipes(), nbtoursav+1).lastElement();
			
			String req = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
			char v = ' ';
			for(Match m:ms){
				req += v + "(NULL," + tournoi.getIdTournoi() + ", " + (nbtoursav + 1) + ", "+  m.getIdEquipe1() + ", " +  m.getIdEquipe2() + ", 'non')";
				v = ',';
			}
			req += "\n";
		
			//System.out.println(req);
			try{
				statement.executeUpdate(req);
			}catch(SQLException e){
				System.out.println("Erreur ajout tour : " + e.getMessage());
			}		
		}else{
			try {
				ResultSet rs;
				//rs = statement.executeQuery("SELECT equipe, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2 AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 AND m.id_tournoi = id_tournoi )) as matchs_gagnes FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + this.id_tournoi + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + this.id_tournoi + ") GROUP BY equipe ORDER BY matchs_gagnes DESC;");
	
				rs = statement.executeQuery("SELECT equipe, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + tournoi.getIdTournoi() + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + tournoi.getIdTournoi() + ") GROUP BY equipe  ORDER BY matchs_gagnes DESC;");

				
				ArrayList<Integer> ordreeq= new ArrayList<Integer>();
				while(rs.next()){
					ordreeq.add(rs.getInt("equipe"));
					System.out.println(rs.getInt(1) +" _ " + rs.getString(2));
				}
				System.out.println("Taille"+ordreeq.size());
				int i;
				boolean fini;
				String req = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
				char v = ' ';
				while(ordreeq.size() > 1){
					System.out.println("Taille " + ordreeq.size());
					int j=0;
					while(j<ordreeq.size()) {
						System.out.println(ordreeq.get(j));
						j++;
					}
					i=1;
					do{
						rs = statement.executeQuery("SELECT COUNT(*) FROM matchs m WHERE ( (m.equipe1 = " + ordreeq.get(0) + " AND m.equipe2 = " + ordreeq.get(i) + ") OR (m.equipe2 = " + ordreeq.get(0) + " AND m.equipe1 = " + ordreeq.get(i) + ")  )");  
						rs.next();
						if(rs.getInt(1) > 0){
							// Le match est d�j� jou�
							i++;
							fini = false;

						}else{ 
							fini = true;
							req += v + "(NULL," + tournoi.getIdTournoi() + ", " + (nbtoursav + 1) + ", "+  ordreeq.get(0) + ", " +  ordreeq.get(i) + ", 'non')";
							System.out.println(ordreeq.get(0) + ", " +  ordreeq.get(i));
							ordreeq.remove(0);
							ordreeq.remove(i-1);
							v = ',';
						}
					}while(!fini);
				}
				System.out.println(req);
				statement.executeUpdate(req);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		return true;
	}

    public void supprimerTour(Tournoi tournoi){
		int nbtoursav;
		try {
			ResultSet rs = statement.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi="+tournoi.getIdTournoi()+"; ");
			rs.next();
			nbtoursav = rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return ;
		}
		//if(tour != nbtoursav) return ;
		
		try {
			statement.executeUpdate("DELETE FROM matchs WHERE id_tournoi="+ tournoi.getIdTournoi()+" AND num_tour=" + nbtoursav);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur del tour : " + e.getMessage());
		}
	}

    public void majMatch(Tournoi tournoi, int index){
		String termine = (tournoi.getMatch(index).getScoreEquipe1() > 0 || tournoi.getMatch(index).getScoreEquipe2() > 0) ? "oui":"non";
		System.out.println(termine);
		String req="UPDATE matchs SET equipe1='" + tournoi.getMatch(index).getIdEquipe1() + "', equipe2='" + tournoi.getMatch(index).getIdEquipe2() + "',  score1='" + tournoi.getMatch(index).getScoreEquipe1() + "',  score2='" +tournoi.getMatch(index).getScoreEquipe2() + "', termine='" + termine + "' WHERE id_match = " + tournoi.getMatch(index).getIdMatch() + ";";
		try {
			statement.executeUpdate(req);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tournoi.majMatch();
	}
}
