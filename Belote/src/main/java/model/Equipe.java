package model;

public 	class Equipe{
	private int idEquipe;
	private int numEquipe;
	private String nomEquipe1;
	private String nomEquipe2;
	
	/**
	 * Constructeur d'une équipe
	 * @param _idEquipe identifiant unique de l'équipe
	 * @param _numEquipe numéro d'affichage de l'équipe
	 * @param _nomEquipe1 équipier 1
	 * @param _nomEquipe2 équipier 2
	 */
	public Equipe( int _idEquipe, int _numEquipe, String _nomEquipe1, String _nomEquipe2){
		idEquipe = _idEquipe;
		numEquipe = _numEquipe;
		nomEquipe1 = _nomEquipe1;
		nomEquipe2 = _nomEquipe2;
	}
	
		public int getIdEquipe() {return this.idEquipe;}
	
		public void setIdEquipe(int idEquipe) {this.idEquipe = idEquipe;}
	
		public int getNumEquipe() {return this.numEquipe;}
	
		public void setNumEquipe(int numEquipe) {this.numEquipe = numEquipe;}
	
		public String getNomEquipe1() {return this.nomEquipe1;}
	
		public void setNomEquipe1(String nomEquipe1) {this.nomEquipe1 = nomEquipe1;}
	
		public String getNomEquipe2() {return this.nomEquipe2;}
	
		public void setNomEquipe2(String nomEquipe2) {this.nomEquipe2 = nomEquipe2;}
}