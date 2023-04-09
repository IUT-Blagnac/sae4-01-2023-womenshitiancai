package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

import model.Equipe;
import model.Match;

public class Tournoi {
	private String statutTournoi;
	private String numeroTournoi;
	private int statut;
	private int idTournoi;

	
	//int    nbtours;
	private Vector<Equipe> equipes = null;
	private Vector<Match> matchs  = null;
	private Vector<Integer>idsEquipe  = null; 
	private Statement statement;
	
	public int getIdTournoi() {
		return this.idTournoi;
	}

	public void setIdTournoi(int _idTournoi) {
		this.idTournoi = _idTournoi;
	}

	public Tournoi(String _numeroTournoi, Statement _statement){
		statement = _statement;

		try {
			ResultSet resultat = _statement.executeQuery("SELECT * FROM tournois WHERE nom_tournoi = '" + Tournoi.mysql_real_escape_string(_numeroTournoi) + "';");
			if(!resultat.next()){
				return ;
			}
			this.statut = resultat.getInt("statut");
			
			setIdTournoi(resultat.getInt("id_tournoi"));
			resultat.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur SQL: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		statutTournoi = "Inconnu";
		switch(this.statut){
		//case 0:
		//	statuttnom = "Configuration du tournoi";
		//break;
		case 0:
			statutTournoi = "Inscription des joueurs";
		break;
		case 1:
			statutTournoi = "Génération des matchs";
		break;
		case 2:
			statutTournoi = "Matchs en cours";
		break;
		case 3:
			statutTournoi = "Terminé";
		break;
			
		}
		this.numeroTournoi = _numeroTournoi;
		
	}

	public void majEquipes(){
		equipes = new Vector<Equipe>();
		idsEquipe = new Vector<Integer>();
		try {
			ResultSet resultat = statement.executeQuery("SELECT * FROM equipes WHERE id_tournoi = " + getIdTournoi() + " ORDER BY num_equipe;");
			while(resultat.next()){
				equipes.add(new Equipe(resultat.getInt("id_equipe"),resultat.getInt("num_equipe"), resultat.getString("nom_j1"), resultat.getString("nom_j2")));
				idsEquipe.add(resultat.getInt("num_equipe"));
			}
			resultat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	public void majMatch(){
		matchs = new Vector<Match>();
		try {
			ResultSet rs= statement.executeQuery("SELECT * FROM matchs WHERE id_tournoi="+ getIdTournoi() + ";");
			while(rs.next()) matchs.add(new Match(rs.getInt("id_match"),rs.getInt("equipe1"),rs.getInt("equipe2"), rs.getInt("score1"),rs.getInt("score2"),rs.getInt("num_tour"),rs.getString("termine") == "oui"));
			//public MatchM(int _idmatch,int _e1,int _e2,int _score1, int _score2, int _num_tour, boolean _termine)
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	public Match getMatch(int index){
		if(matchs == null) majMatch();
		return matchs.get(index);
	}
	public int getNbMatchs(){
		if(matchs == null) majMatch();
		return matchs.size();
	}
	public Equipe getEquipe(int index){
		if(equipes == null) 
			majEquipes();
		return equipes.get(index);
		
	}
	public int getNbEquipes(){
		if(equipes == null) 
			majEquipes();
		return equipes.size();
	}
	
	public int    getStatut(){
		return statut;
	}
	public String getNStatut(){
		return statutTournoi;
	}
	public String getNom() {
		return numeroTournoi;
	}
	public int getNbTours(){
		try {
			ResultSet rs = statement.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi="+getIdTournoi()+"; ");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return -1;
		}
	}
	public void genererMatchs(){
		int nbTours = 1;

		System.out.println("Nombre d'�quipes : " + getNbEquipes());
		System.out.println("Nombre de tours  : " + nbTours);
		String request = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
		Vector<Vector<Match>> listeListeMatchs;
		listeListeMatchs = Tournoi.getMatchsToDo(getNbEquipes(), nbTours);
		int iterateur = 1;
		char separateur = ' ';
		for(Vector<Match> listeMatchs :listeListeMatchs){
			for(Match match:listeMatchs){
				request += separateur + "(NULL," + getIdTournoi() + ", " + iterateur + ", "+  match.getIdEquipe1() + ", " +  match.getIdEquipe2() + ", 'non')";
				separateur = ',';
			}
			request += "\n";
			iterateur++;
		}
		System.out.println(request);
		try{
			statement.executeUpdate(request);
			statement.executeUpdate("UPDATE tournois SET statut=2 WHERE id_tournoi=" + getIdTournoi() + ";");
			this.statut = 2;
		}catch(SQLException e){
			System.out.println("Erreur validation �quipes : " + e.getMessage());
		}
	}
	
	public boolean ajouterTour(){
		// Recherche du nombre de tours actuel
		int nbtoursav;
		if(getNbTours() >=  (getNbEquipes() -1) ) return false;
		System.out.println("Eq:" + getNbEquipes() + "  tours" + getNbTours());
		try {
			ResultSet resultat = statement.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi="+getIdTournoi()+"; ");
			resultat.next();
			nbtoursav = resultat.getInt(1);
			resultat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return false;
		}
		System.out.println("Nombre de tours avant:" + nbtoursav);
		
		
		if(nbtoursav == 0){
			Vector<Match> listeMatchs;
			
			listeMatchs = Tournoi.getMatchsToDo(getNbEquipes(), nbtoursav+1).lastElement();
			
			String request = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
			char separateur = ' ';
			for(Match match:listeMatchs){
				request += separateur + "(NULL," + getIdTournoi() + ", " + (nbtoursav + 1) + ", "+  match.getIdEquipe1() + ", " +  match.getIdEquipe2() + ", 'non')";
				separateur = ',';
			}
			request += "\n";
		
			//System.out.println(req);
			try{
				statement.executeUpdate(request);
			}catch(SQLException e){
				System.out.println("Erreur ajout tour : " + e.getMessage());
			}		
		}else{
			try {
				ResultSet rs;
				//rs = st.executeQuery("SELECT equipe, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2 AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 AND m.id_tournoi = id_tournoi )) as matchs_gagnes FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + this.id_tournoi + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + this.id_tournoi + ") GROUP BY equipe ORDER BY matchs_gagnes DESC;");
	
				rs = statement.executeQuery("SELECT equipe, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + getIdTournoi() + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + getIdTournoi() + ") GROUP BY equipe  ORDER BY matchs_gagnes DESC;");

				
				ArrayList<Integer> ordreeq= new ArrayList<Integer>();
				while(rs.next()){
					ordreeq.add(rs.getInt("equipe"));
					System.out.println(rs.getInt(1) +" _ " + rs.getString(2));
				}
				System.out.println("Taille"+ordreeq.size());
				int i;
				boolean fini;
				String req = "INSERT INTO matchs ( id_match, id_tournoi, num_tour, equipe1, equipe2, termine ) VALUES\n";
				char separateur = ' ';
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
							req += separateur + "(NULL," + getIdTournoi() + ", " + (nbtoursav + 1) + ", "+  ordreeq.get(0) + ", " +  ordreeq.get(i) + ", 'non')";
							System.out.println(ordreeq.get(0) + ", " +  ordreeq.get(i));
							ordreeq.remove(0);
							ordreeq.remove(i-1);
							separateur = ',';
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
	public void supprimerTour(){
		int nbtoursav;
		try {
			ResultSet rs = statement.executeQuery("SELECT MAX (num_tour)  FROM matchs WHERE id_tournoi="+getIdTournoi()+"; ");
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
			statement.executeUpdate("DELETE FROM matchs WHERE id_tournoi="+ getIdTournoi()+" AND num_tour=" + nbtoursav);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur del tour : " + e.getMessage());
		}
	}
		
	public static int deleteTournoi(Statement _statement, String nomtournoi){
		try {
			int idTournoi;
			ResultSet rs = _statement.executeQuery("SELECT id_tournoi FROM tournois WHERE nom_tournoi = '" + mysql_real_escape_string(nomtournoi) + "';");
			rs.next();
			idTournoi = rs.getInt(1);
			rs.close();
			System.out.println("ID du tournoi � supprimer:" + idTournoi);
			_statement.executeUpdate("DELETE FROM matchs   WHERE id_tournoi = " + idTournoi);
			_statement.executeUpdate("DELETE FROM equipes  WHERE id_tournoi = " + idTournoi);
			_statement.executeUpdate("DELETE FROM tournois WHERE id_tournoi = " + idTournoi);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur suppression" + e.getMessage());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur inconnue");
		} 
		return 0;
	}
	public static int creerTournoi(Statement _statement){
		String s = (String)JOptionPane.showInputDialog(
                null,
                "Entrez le nom du tournoi",
                "Nom du tournoi",
                JOptionPane.PLAIN_MESSAGE);
		
		
		if(s == null || s == ""){
			return 1;
		}else{
			try {
				s =  mysql_real_escape_string(s);
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
					rs = _statement.executeQuery("SELECT id_tournoi FROM tournois WHERE nom_tournoi = '" + s + "';");
					if(rs.next()){
						JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Un tournoi du m�me nom existe d�j�");
						return 2;							
					}
	
					System.out.println("INSERT INTO tournois (id_tournoi, nb_matchs, nom_tournoi, statut) VALUES (NULL, 10, '"+s+"', 0)");
				_statement.executeUpdate("INSERT INTO tournois (id_tournoi, nb_matchs, nom_tournoi, statut) VALUES (NULL, 10, '"+s+"', 0)");
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
	
	public void ajouterEquipe(){
		int idNouvelleEquipe= this.equipes.size()+1;
		for ( int i=1;i <= this.equipes.size(); i++){
			if(!idsEquipe.contains(i)){
				idNouvelleEquipe=i;
				break;
			}
		}
		try {
			statement.executeUpdate("INSERT INTO equipes (id_equipe,num_equipe,id_tournoi,nom_j1,nom_j2) VALUES (NULL,"+idNouvelleEquipe+", "+getIdTournoi() + ",'\"Joueur 1\"', '\"Joueur 2\"');");
		    majEquipes();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void majEquipe(int index){
		try {
			String req = "UPDATE equipes SET nom_j1 = '" + mysql_real_escape_string(getEquipe(index).getNomEquipe1()) + "', nom_j2 = '" + mysql_real_escape_string(getEquipe(index).getNomEquipe2()) + "' WHERE id_equipe = " + getEquipe(index).getIdEquipe() + ";";
			System.out.println(req);
			statement.executeUpdate(req);
		    majEquipes();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void majMatch(int index){
		String termine = (getMatch(index).getScoreEquipe1() > 0 || getMatch(index).getScoreEquipe2() > 0) ? "oui":"non";
		System.out.println(termine);
		String req="UPDATE matchs SET equipe1='" + getMatch(index).getIdEquipe1() + "', equipe2='" + getMatch(index).getIdEquipe2() + "',  score1='" + getMatch(index).getScoreEquipe1() + "',  score2='" +getMatch(index).getScoreEquipe2() + "', termine='" + termine + "' WHERE id_match = " + getMatch(index).getIdMatch() + ";";
		try {
			statement.executeUpdate(req);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		majMatch();
	}
	public void supprimerEquipe(int ideq){
		try {
			int numEquipe;
			ResultSet rs = statement.executeQuery("SELECT num_equipe FROM equipes WHERE id_equipe = " + ideq);
			rs.next();
			numEquipe = rs.getInt(1);
			rs.close();
			statement.executeUpdate("DELETE FROM equipes WHERE id_tournoi = " + getIdTournoi()+ " AND id_equipe = " + ideq);
			statement.executeUpdate("UPDATE equipes SET num_equipe = num_equipe - 1 WHERE id_tournoi = " + getIdTournoi() + " AND num_equipe > " + numEquipe);
		    majEquipes();
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
    public static String mysql_real_escape_string( String str) 
            throws Exception
      {
          if (str == null) {
              return null;
          }
                                      
          if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
              return str;
          }
              
          String cleanString = str;
          cleanString = cleanString.replaceAll("\\n","\\\\n");
          cleanString = cleanString.replaceAll("\\r", "\\\\r");
          cleanString = cleanString.replaceAll("\\t", "\\\\t");
          cleanString = cleanString.replaceAll("\\00", "\\\\0");
          cleanString = cleanString.replaceAll("'", "''");
          return cleanString;

      }
    
	public static Vector<Vector<Match>> getMatchsToDo(int nbJoueurs, int nbTours){
		if( nbTours  >= nbJoueurs){
			System.out.println("Erreur tours < equipes");
			return null;
		}
		
		int[]   tabJoueurs;
		if((nbJoueurs % 2) == 1){
			// Nombre impair de joueurs, on rajoute une �quipe fictive
			tabJoueurs   = new int[nbJoueurs+1];
			tabJoueurs[nbJoueurs] = -1;
			for(int z = 0; z < nbJoueurs;z++){
				tabJoueurs[z] = z+1;
			}
			nbJoueurs++;
		}else{
			tabJoueurs   = new int[nbJoueurs];
			for(int z = 0; z < nbJoueurs;z++){
				tabJoueurs[z] = z+1;
			}
		}
		
		boolean quitter;
		int i, increment = 1, temp;

		Vector<Vector<Match>> retour = new Vector<Vector<Match>>();
		
		Vector<Match> vecteurMatchs;
		
		for( int r = 1; r <= nbTours;r++){
			if(r > 1){
				temp = tabJoueurs[nbJoueurs - 2];
				for(i = (nbJoueurs - 2) ; i > 0; i--){
					tabJoueurs[i] = tabJoueurs[i-1];
				}
				tabJoueurs[0] = temp;
			}
			i = 0;
			quitter = false;
			vecteurMatchs = new Vector<Match>();
			while(!quitter){
				if (tabJoueurs[i] == -1 || tabJoueurs[nbJoueurs - 1  - i] == -1){
					// Nombre impair de joueur, le joueur n'a pas d'adversaire
				}else{
					vecteurMatchs.add(new Match(tabJoueurs[i], tabJoueurs[nbJoueurs - 1  - i]));
				}

		        i+= increment;
				if(i >= nbJoueurs / 2){
					if(increment == 1){
						quitter = true;
						break;
					}else{
						increment = -2;
						if( i > nbJoueurs / 2){
							i = ((i > nbJoueurs / 2) ? i - 3 : --i) ;
						}
						if ((i < 1) && (increment == -2)){
							quitter = true;
							break;
						}
					}
				}
			}
			retour.add(vecteurMatchs);
		}
		return retour;
	}
}