package utils;

import javax.swing.JOptionPane;

public class fenetreErreur {
    public void alert(String s) {
        JOptionPane.showMessageDialog(null, s);
    }

    public void alertConnectionBD(){
        JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de données. Vérifier qu'une autre instance du logiciel n'est pas déjà ouverte.");
    }
}
