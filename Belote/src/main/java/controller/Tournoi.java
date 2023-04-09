package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

import model.Equipe;
import model.Match;

import orm.AccessTournoi;
import orm.AccessEquipe;
import orm.AccessMatch;

public class Tournoi {
	static AccessTournoi requetesTournoi = new AccessTournoi();
	AccessMatch requetesMatch = new AccessMatch();
	AccessEquipe requestEquipe = new AccessEquipe();

	private String statuttnom;
	private String nt;
	private int statut;

	

	private int id_tournoi;

	
	//int    nbtours;
	private Vector<Equipe> dataeq = null;
	private Vector<Match> datam = null;
	private Vector<Integer> ideqs = null;

	
	private Statement st;

	
	public int getIdTournoi() {
		return this.idTournoi;
	}

	public void setIdTournoi(int _idTournoi) {
		this.idTournoi = _idTournoi;
	}

	public Tournoi(String _numeroTournoi, Statement _statement){
		statement = _statement;

		try {
			ResultSet rs = requetesTournoi.getTournoisByName(Tournoi.mysql_real_escape_string(nt));
			if(!rs.next()){
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
			ResultSet rs = requestEquipe.getEquipeByID(getId_tournoi(), "num_equipe");

			while(rs.next()){
				dataeq.add(new Equipe(rs.getInt("id_equipe"),rs.getInt("num_equipe"), rs.getString("nom_j1"), rs.getString("nom_j2")));
				ideqs.add(rs.getInt("num_equipe"));
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
			ResultSet rs = requetesTournoi.getTournoisByID(getId_tournoi());
			while(rs.next()) datam.add(new Match(rs.getInt("id_match"),rs.getInt("equipe1"),rs.getInt("equipe2"), rs.getInt("score1"),rs.getInt("score2"),rs.getInt("num_tour"),rs.getString("termine") == "oui"));
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
		if(dataeq == null) 
		majEquipes();
		return dataeq.size();
	}

	public Vector<Equipe> getDataeq() {
		return this.dataeq;
	}

	public void setDataeq(Vector<Equipe> dataeq) {
		this.dataeq = dataeq;
	}

	public Vector<Match> getDatam() {
		return this.datam;
	}

	public void setDatam(Vector<Match> datam) {
		this.datam = datam;
	}

	public Vector<Integer> getIdeqs() {
		return this.ideqs;
	}

	public void setIdeqs(Vector<Integer> ideqs) {
		this.ideqs = ideqs;
	}
	
	public void setStatut(int statut) {
		this.statut = statut;
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
			ResultSet rs = requetesTournoi.getTournoisDernierTour(id_tournoi);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public void genererMatchs(){
		requetesMatch.genererMatchs(this);
	}
	
	public boolean ajouterTour(){
		return requetesMatch.ajouterTour(this);
	}

	public void supprimerTour(){
		requetesMatch.supprimerTour(this);
	}
		
	public static int deleteTournoi(Statement s2, String nomtournoi){
		return requetesTournoi.deleteTournoi(s2, nomtournoi);
	}

	public static int creerTournoi(Statement s2){
		return requetesTournoi.creerTournoi(s2); //很好🥚
	}
	
	public void ajouterEquipe(){
		requestEquipe.ajouterEquipe(this);
	}

	public void majEquipe(int index){
		requestEquipe.majEquipe(this, index);
	}

	public void majMatch(int index){
		requetesMatch.majMatch(this, index);
	}

	public void supprimerEquipe(int ideq){
		requestEquipe.supprimerEquipe(this, ideq);
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