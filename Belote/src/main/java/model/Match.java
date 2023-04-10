package model;

public class Match{
	private int idMatch;
	private int idEquipe1;
	private int idEquipe2;
	private int scoreEquipe1;
	private int scoreEquipe2;
	private int numTour;
	private boolean mancheTerminee;

	
	/**
	* Constructeur du Match
	* @param _idMatch identifiant unique du match
	* @param _idEquipe1 équipe 1
	* @param _idEquipe2 équipe 2
	* @param _scoreEquipe1 score de l'équipe 1
	* @param _scoreEquipe2 scode de l'équipe 2
	* @param _numTour tour actuel
	* @param _mancheTerminee la partie est-elle finie
	*/
	public Match(int _idMatch,int _idEquipe1,int _idEquipe2,int _scoreEquipe1, int _scoreEquipe2, int _numTour, boolean _mancheTerminee){
		idMatch = _idMatch;
		idEquipe1     = _idEquipe1;
		idEquipe2     = _idEquipe2;
		scoreEquipe1  = _scoreEquipe1;
		scoreEquipe2  = _scoreEquipe2;
		numTour= _numTour;
		mancheTerminee = _mancheTerminee;
	}
	public Match(int idEquipe1,int idEquipe2){
		idEquipe1 = idEquipe1;
		idEquipe2 = idEquipe2;
	}
	public String toString(){
		if(idEquipe1 < idEquipe2){
			return "  " + idEquipe1 + " contre " + idEquipe2;
		}else{
			return "  " + idEquipe2 + " contre " + idEquipe1;
		}
	}

	public int getIdMatch() {return this.idMatch;}
	
	public void setIdMatch(int idmatch) {this.idMatch = idmatch;}
	
	public int getIdEquipe1() {return this.idEquipe1;}
	
	public void setIdEquipe1(int eq1) {this.idEquipe1 = eq1;}
	
	public int getIdEquipe2() {return this.idEquipe2;}
	
	public void setIdEquipe2(int eq2) {this.idEquipe2 = eq2;}
	
	public int getScoreEquipe1() {return this.scoreEquipe1;}
	
	public void setScoreEquipe1(int score1) {this.scoreEquipe1 = score1;}
	
	public int getScoreEquipe2() {return this.scoreEquipe2;}
	
	public void setScoreEquipe2(int score2) {this.scoreEquipe2 = score2;}
	
	public int getNumTour() {return this.numTour;}
	
	public void setNumTour(int num_tour) {this.numTour = num_tour;}
	
	public boolean isMancheTerminee() {return this.mancheTerminee;}
	
	public void setMancheTerminee(boolean termine) {this.mancheTerminee = termine;}
}