package controller;

public class Equipe {
    private int idEquipe;
    private int numEquipe;
    private String nomJoueur1;
    private String nomJoueur2;

    
    public Equipe(int idEquipe, int numEquipe, String nomJoueur1, String nomJoueur2) {
        this.idEquipe = idEquipe;
        this.numEquipe = numEquipe;
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;
    }
    
    // getters and setters
    public int getIdEquipe() {
        return this.idEquipe;
    }

    public void setIdEquipe(int idEquipe) {
        this.idEquipe = idEquipe;
    }

    public int getNumEquipe() {
        return this.numEquipe;
    }

    public void setNumEquipe(int numEquipe) {
        this.numEquipe = numEquipe;
    }

    public String getNomJoueur1() {
        return this.nomJoueur1;
    }

    public void setNomJoueur1(String nomJoueur1) {
        this.nomJoueur1 = nomJoueur1;
    }

    public String getNomJoueur2() {
        return this.nomJoueur2;
    }

    public void setNomJoueur2(String nomJoueur2) {
        this.nomJoueur2 = nomJoueur2;
    }
}
