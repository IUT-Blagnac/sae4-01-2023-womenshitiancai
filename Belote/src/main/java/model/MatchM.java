package model;

public class MatchM{
	public int idmatch,eq1,eq2,score1,score2,num_tour;
	public boolean termine;
	
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
	public String toString(){
		if(eq1 < eq2){
			return "  " + eq1 + " contre " + eq2;
		}else{
			return "  " + eq2 + " contre " + eq1;
		}
	}
}