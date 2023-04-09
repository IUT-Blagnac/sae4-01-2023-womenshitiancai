package view;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

public class TournoisPane extends JPanel{

    public TournoisPane(Statement s) {
        
        JList<String> list;
        boolean tournois_trace = false;

        
		JButton selectTournoi;
		JButton deleteTournoi;

		int nbdeLignes = 0;
		Vector<String> noms_tournois = new Vector<String>();
		ResultSet rs;
		try {
			rs = s.executeQuery("SELECT * FROM tournois;");

			while( rs.next() ){
				nbdeLignes++;
				noms_tournois.add(rs.getString("nom_tournoi"));
			}
			
			if( nbdeLignes == 0){
				//System.out.println("Pas de résultats");
				//t.add(new JLabel("Aucun tournoi n'a �t� cr��"));
			}else{
				//System.out.println(nbdeLignes + " r�sultats");
				
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Erreur lors de la requète :" + e.getMessage());
			e.printStackTrace();
		}
		
        if(tournois_trace){
			list.setListData(noms_tournois);

	        if(nbdeLignes == 0){
	        	selectTournoi.setEnabled(false);
	        	deleteTournoi.setEnabled(false);
	        }else{
	        	selectTournoi.setEnabled(true);
	        	deleteTournoi.setEnabled(true);
	        	list.setSelectedIndex(0);
	        }
			fen.show(c, TOURNOIS);


		}else{
		    tournois_trace = true;
			JPanel t = new JPanel();
			
			t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
			c.add(t,TOURNOIS);
			gt = new JTextArea("Gestion des tournois\nXXXXX XXXXXXXX, juillet 2012");
			gt.setAlignmentX(Component.CENTER_ALIGNMENT);
			gt.setEditable(false);
			t.add(gt);
			
			// Recherche de la liste des tournois
			ListeTournois = new JPanel();
			
			t.add(ListeTournois);		
	
			list = new JList<String>(noms_tournois); 
			list.setAlignmentX(Component.LEFT_ALIGNMENT); 
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		    list.setVisibleRowCount(-1);
		    JScrollPane listScroller = new JScrollPane(list);
	        listScroller.setPreferredSize(new Dimension(250, 180));
	        //listScroller.setAlignmentX(LEFT_ALIGNMENT);
	        
	        label = new JLabel("Liste des tournois");
	        label.setLabelFor(list);
	        label.setAlignmentX(Component.LEFT_ALIGNMENT);
	        t.add(label);
	        //c.add(Box.createRigidArea(new Dimension(0,0)));
	        t.add(listScroller);
	        t.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	        
	        
	
	        Box bh = Box.createHorizontalBox();
	        t.add(bh);
			creerTournoi = new JButton("Créer un nouveau tournoi");
			selectTournoi = new JButton("Sélectionner le tournoi");
			deleteTournoi = new JButton("Supprimer le tournoi");
			bh.add(creerTournoi);
			bh.add(selectTournoi);	
			bh.add(deleteTournoi);
			
			t.updateUI();
	        if(nbdeLignes == 0){
	        	selectTournoi.setEnabled(false);
	        	deleteTournoi.setEnabled(false);
	        }else{
	        	list.setSelectedIndex(0);
	        }
	        
	        creerTournoi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Tournoi.creerTournoi(Fenetre.this.s);
					Fenetre.this.tracer_select_tournoi();
					//String nt = JOptionPane.showInputDialog("Nom du tournoi ?");
					//ResultSet rs = Fenetre.this.s.executeQuery("SELECT)
					//Fenetre.this.s.execute("INSERT INTO TOURNOI (id_tournoi)
				}
			});
	        
	        deleteTournoi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Tournoi.deleteTournoi(Fenetre.this.s, Fenetre.this.list.getSelectedValue());
					Fenetre.this.tracer_select_tournoi();
	
					
				}
			});
	        selectTournoi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String nt = Fenetre.this.list.getSelectedValue();
					Fenetre.this.t = new Tournoi(nt, Fenetre.this.s);
					//Fenetre.this.detracer_select_tournoi();
					Fenetre.this.tracer_details_tournoi();
					Fenetre.this.setStatutSelect("Tournoi \" " + nt + " \"");
					
				}
			});
	        fen.show(c, TOURNOIS);
		}
        
		
		
	}
    
}
