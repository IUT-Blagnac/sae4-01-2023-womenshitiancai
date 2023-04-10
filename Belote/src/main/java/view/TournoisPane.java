package view;


import java.awt.Component;
import java.awt.Dimension;
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

public class TournoisPane extends JPanel{
	AccessTournoi requestTournoi = AccessTournoi.getInstance();
	JList<String> list;
	
	JButton creerTournoi;
	JButton selectTournoi;
	JButton deleteTournoi;

	int nbdeLignes;
	Vector<String> noms_tournois;
	ResultSet rs;

	Statement s;

    public TournoisPane(Statement s, Fenetre f) {
		super();

        this.s = s;

		noms_tournois = new Vector<String>();

		loadTournois();
	
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JTextArea gt = new JTextArea("Gestion des tournois\nXXXXX XXXXXXXX, juillet 2012");
		gt.setAlignmentX(Component.CENTER_ALIGNMENT);
		gt.setEditable(false);
		this.add(gt);
		
		// Recherche de la liste des tournois
		JPanel ListeTournois = new JPanel();
		
		this.add(ListeTournois);		

		list = new JList<String>(noms_tournois); 
		list.setAlignmentX(Component.LEFT_ALIGNMENT); 
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 180));
		//listScroller.setAlignmentX(LEFT_ALIGNMENT);
		
		JLabel label = new JLabel("Liste des tournois");
		label.setLabelFor(list);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(label);
		//c.add(Box.createRigidArea(new Dimension(0,0)));
		this.add(listScroller);
		this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		

		Box bh = Box.createHorizontalBox();
		this.add(bh);
		creerTournoi = new JButton("Créer un nouveau tournoi");
		selectTournoi = new JButton("Sélectionner le tournoi");
		deleteTournoi = new JButton("Supprimer le tournoi");
		bh.add(creerTournoi);
		bh.add(selectTournoi);	
		bh.add(deleteTournoi);
		
		this.updateUI();
		if(nbdeLignes == 0){
			selectTournoi.setEnabled(false);
			deleteTournoi.setEnabled(false);
		}else{
			list.setSelectedIndex(0);
		}
		
		creerTournoi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Tournoi.creerTournoi(s);
				f.tracer_select_tournoi();
				//String nt = JOptionPane.showInputDialog("Nom du tournoi ?");
				//ResultSet rs = Fenetre.this.s.executeQuery("SELECT)
				//Fenetre.this.s.execute("INSERT INTO TOURNOI (id_tournoi)
			}
		});
		
		deleteTournoi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Tournoi.deleteTournoi(s, list.getSelectedValue());
				f.tracer_select_tournoi();

				
			}
		});
		selectTournoi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String nt = list.getSelectedValue();
				f.setT(new Tournoi(nt, s));
				//Fenetre.this.detracer_select_tournoi();
				f.tracer_details_tournoi();
				f.setStatutSelect("Tournoi \" " + nt + " \"");
				
			}
		});
	}
	

	private void loadTournois() {
		try {
			nbdeLignes = 0;
			noms_tournois.clear();

			rs = requestTournoi.getAllTournois();

			while( rs.next() ){
				nbdeLignes++;
				noms_tournois.add(rs.getString("nom_tournoi"));
			}
			
			rs.close();
		} catch (SQLException e) {
			System.out.println("Erreur lors de la requète :" + e.getMessage());
			e.printStackTrace();
		}
	}
		
	public void refresh() {
		loadTournois();
		list.setListData(noms_tournois);

		if(nbdeLignes == 0){
			selectTournoi.setEnabled(false);
			deleteTournoi.setEnabled(false);
		}else{
			selectTournoi.setEnabled(true);
			deleteTournoi.setEnabled(true);
			list.setSelectedIndex(0);
		}
	}
    
}
