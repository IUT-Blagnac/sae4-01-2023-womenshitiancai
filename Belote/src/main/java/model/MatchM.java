package model;

public class MatchM{
	private int idmatch;
	private int eq1;
	private int eq2;
	private int score1;
	private int score2;
	private int num_tour;
	private boolean termine;

	
	/**
	* Constructeur du Match
	* @param _idmatch identifiant unique du match
	* @param _e1 équipe 1
	* @param _e2 équipe 2
	* @param _score1 score de l'équipe 1
	* @param _score2 scode de l'équipe 2
	* @param _num_tour tour actuel
	* @param _termine la partie est-elle finie
	*/
	public MatchM(int _idmatch,int _e1,int _e2,int _score1, int _score2, int _num_tour, boolean _termine){
		idmatch = _idmatch;
		eq1     = _e1;
		eq2     = _e2;
		score1  = _score1;
		score2  = _score2;
		num_tour= _num_tour;
		termine = _termine;
	}
	public MatchM(int e1,int e2){
		eq1 = e1;
		eq2 = e2;
	}
	public String toString(){
		if(eq1 < eq2){
			return "  " + eq1 + " contre " + eq2;
		}else{
			return "  " + eq2 + " contre " + eq1;
		}
	}

	public int getIdmatch() {return this.idmatch;}
	
	public void setIdmatch(int idmatch) {this.idmatch = idmatch;}
	
	public int getEq1() {return this.eq1;}
	
	public void setEq1(int eq1) {this.eq1 = eq1;}
	
	public int getEq2() {return this.eq2;}
	
	public void setEq2(int eq2) {this.eq2 = eq2;}
	
	public int getScore1() {return this.score1;}
	
	public void setScore1(int score1) {this.score1 = score1;}
	
	public int getScore2() {return this.score2;}
	
	public void setScore2(int score2) {this.score2 = score2;}
	
	public int getNum_tour() {return this.num_tour;}
	
	public void setNum_tour(int num_tour) {this.num_tour = num_tour;}
	
	public boolean isTermine() {return this.termine;}
	
	public void setTermine(boolean termine) {this.termine = termine;}
}