package model;

public 	class Equipe{
	/**
	 * Constructeur d'une équipe
	 * @param _id identifiant unique de l'équipe
	 * @param _num numéro d'affichage de l'équipe
	 * @param _eq1 équipier 1
	 * @param _eq2 équipier 2
	 */
	public Equipe( int _id, int _num, String _eq1, String _eq2){
		id = _id;
		num = _num;
		eq1 = _eq1;
		eq2 = _eq2;
	}
	public int id;
	public int num;
	public String eq1;
	public String eq2;
}