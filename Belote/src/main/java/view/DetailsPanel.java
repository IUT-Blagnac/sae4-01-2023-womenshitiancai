package view;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import controller.Tournoi;

import dao.AccessTournoi;

public class DetailsPanel extends JPanel{

	private JLabel detailt_nom;
	private JLabel detailt_statut;
	private JLabel detailt_nbtours;

    public DetailsPanel(Tournoi t) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel("Détail du tournoi"));
		
		JPanel tab = new JPanel( new GridLayout(4,2));
		detailt_nom = new JLabel(t.getNom());
		tab.add(new JLabel("Nom du tournoi"));
		tab.add(detailt_nom);

		detailt_statut = new JLabel(t.getNStatut());
		tab.add(new JLabel("Statut"));
		tab.add(detailt_statut);
		
		detailt_nbtours = new JLabel(Integer.toString(t.getNbTours()));
		tab.add(new JLabel("Nombre de tours:"));
		tab.add(detailt_nbtours);

		this.add(tab);
		
		this.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		

	}
    
}
