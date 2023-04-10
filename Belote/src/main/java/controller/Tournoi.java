package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

import dao.AccessEquipe;
import dao.AccessMatch;
import dao.AccessTournoi;
import model.Equipe;
import model.Match;

public class Tournoi {
	static AccessTournoi requetesTournoi = new AccessTournoi();
	AccessMatch requetesMatch = new AccessMatch();
	AccessEquipe requestEquipe = new AccessEquipe();

	private String StatutTournoi;
	private String numeroTournoi;
	private int statut;

	

	private int idTournoi;

	
	//int    nbtours;
	private Vector<Equipe> equipes = null;
	private Vector<Match> matchs = null;
	private Vector<Integer> idEquipes = null;

	
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

			ResultSet rs = requetesTournoi.getTournoisByName(Tournoi.mysql_real_escape_string(_numeroTournoi));
			if(!rs.next()){
				return ;
			}
			this.statut = rs.getInt("statut");
			
			setIdTournoi(rs.getInt("id_tournoi"));
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur SQL: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StatutTournoi = "Inconnu";
		switch(this.statut){
		//case 0:
		//	statuttnom = "Configuration du tournoi";
		//break;
		case 0:
			StatutTournoi = "Inscription des joueurs";
		break;
		case 1:
			StatutTournoi = "Génération des matchs";
		break;
		case 2:
			StatutTournoi = "Matchs en cours";
		break;
		case 3:
			StatutTournoi = "Terminé";
		break;
			
		}
		this.numeroTournoi = _numeroTournoi;
		
	}
	
	public void majEquipes(){
		equipes = new Vector<Equipe>();
		idEquipes = new Vector<Integer>();
		try {
			ResultSet rs = requestEquipe.getEquipeByID(getIdTournoi(), "num_equipe");

			while(rs.next()){
				equipes.add(new Equipe(rs.getInt("id_equipe"),rs.getInt("num_equipe"), rs.getString("nom_j1"), rs.getString("nom_j2")));
				idEquipes.add(rs.getInt("num_equipe"));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	public void majMatch(){
		matchs = new Vector<Match>();
		try {
			ResultSet rs = requetesTournoi.getTournoisByID(getIdTournoi());
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

	public Vector<Equipe> getEquipes() {
		return this.equipes;
	}

	public void setEquipes(Vector<Equipe> dataeq) {
		this.equipes = dataeq;
	}

	public Vector<Match> getMatchs() {
		return this.matchs;
	}

	public void setMatchs(Vector<Match> datam) {
		this.matchs = datam;
	}

	public Vector<Integer> getIdEquipes() {
		return this.idEquipes;
	}

	public void setIdEquipes(Vector<Integer> ideqs) {
		this.idEquipes = ideqs;
	}
	
	public void setStatut(int statut) {
		this.statut = statut;
	}

	public int    getStatut(){
		return statut;
	}
	public String getNStatut(){
		return StatutTournoi;
	}
	public String getNom() {
		return numeroTournoi;
	}
	public int getNbTours(){
		try {
			ResultSet rs = requetesTournoi.getTournoisDernierTour(idTournoi);
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
              
          String clean_string = str;
          clean_string = clean_string.replaceAll("\\n","\\\\n");
          clean_string = clean_string.replaceAll("\\r", "\\\\r");
          clean_string = clean_string.replaceAll("\\t", "\\\\t");
          clean_string = clean_string.replaceAll("\\00", "\\\\0");
          clean_string = clean_string.replaceAll("'", "''");
          return clean_string;

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
		int     i, increment  = 1, temp;

		Vector<Vector<Match>> retour = new Vector<Vector<Match>>();
		
		Vector<Match> vm;
		
		for( int r = 1; r <= nbTours;r++){
			if(r > 1){
				temp = tabJoueurs[nbJoueurs - 2];
				for(i = (nbJoueurs - 2) ; i > 0; i--){
					tabJoueurs[i] = tabJoueurs[i-1];
				}
				tabJoueurs[0] = temp;
			}
			i       = 0;
			quitter = false;
			vm = new Vector<Match>();
			while(!quitter){
				if (tabJoueurs[i] == -1 || tabJoueurs[nbJoueurs - 1  - i] == -1){
					// Nombre impair de joueur, le joueur n'a pas d'adversaire
				}else{
					vm.add(new Match(tabJoueurs[i], tabJoueurs[nbJoueurs - 1  - i]));
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
			retour.add(vm);
		}
		return retour;
	}
}