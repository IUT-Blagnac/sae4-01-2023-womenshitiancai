package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import controller.Tournoi;
import model.Equipe;
import model.Match;



public class Fenetre extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel panelCentre;
	private Statement statement;
	
	private JTextArea gt;
	private JPanel panelListeTournois;
	private Vector<String> nomsTournois;
	private JList<String> listeTournois;
	private JLabel        labelTournois;
	private JButton       boutonCreerTournoi;
	private JButton       boutonSelectTournoi;	
	private JButton       boutonDeleteTournoi;
	private JButton       boutonTournois;
	private JButton       boutonEquipes;
	private JButton       boutonTours;
	private JButton       boutonMatchs;
	private JButton       boutonResultats;
	private JButton       boutonParams;
	
	private boolean tournoisTrace  = false;
	private boolean detailsTrace   = false;
	private boolean equipesTrace   = false;
	private boolean toursTrace     = false;
	private boolean matchTrace     = false;
	private boolean resultatsTrace = false;
	
	private CardLayout fenetreLayout;
	private final static String TOURNOIS = "Tournois";
    private final static String DETAIL   = "Paramètres du tournoi";
    private final static String EQUIPES  = "Equipes";
    private final static String TOURS    = "Tours";
    private final static String MATCHS   = "Matchs";
    private final static String RESULTATS= "Resultats";
    private Tournoi tournoi = null;
    
    private JLabel statutSelect = null;
    private final String statutDeft = "Gestion de tournois de Belote v1.0 - ";

	public Fenetre(Statement _statement){
		
		statement = _statement;
		this.setTitle("Gestion de tournoi de Belote");
		setSize(800,400);
		this.setVisible(true);
		this.setLocationRelativeTo(this.getParent());
		
		
		JPanel contenu = new JPanel();
		contenu.setLayout(new BorderLayout());
		this.setContentPane(contenu);
		
		
		JPanel phaut = new JPanel();
		contenu.add(phaut,BorderLayout.NORTH);
		
		phaut.add(statutSelect = new JLabel());
		this.setStatutSelect("Pas de tournoi sélectionné");
		
		JPanel pgauche = new JPanel();
		pgauche.setBackground(Color.RED);
		pgauche.setPreferredSize(new Dimension(130,0));
		contenu.add(pgauche,BorderLayout.WEST);
		
		
		boutonTournois    = new JButton("Tournois");
		boutonParams      = new JButton("Paramètres");
		boutonEquipes     = new JButton("Equipes");
		boutonTours       = new JButton("Tours");
		boutonMatchs      = new JButton("Matchs");
		boutonResultats   = new JButton("Résultats");
		
		
		int tailleBoutons = 100;
		int hauteurBoutons = 30;
		
		boutonTournois.setPreferredSize(new Dimension(tailleBoutons,hauteurBoutons));
		boutonParams.setPreferredSize(new Dimension(tailleBoutons,hauteurBoutons));
		boutonEquipes.setPreferredSize(new Dimension(tailleBoutons,hauteurBoutons));
		boutonTours.setPreferredSize(new Dimension(tailleBoutons,hauteurBoutons));
		boutonMatchs.setPreferredSize(new Dimension(tailleBoutons,hauteurBoutons));
		boutonResultats.setPreferredSize(new Dimension(tailleBoutons,hauteurBoutons));
		
		pgauche.add(boutonTournois);
		pgauche.add(boutonParams);
		pgauche.add(boutonEquipes);
		pgauche.add(boutonTours);
		pgauche.add(boutonMatchs);
		pgauche.add(boutonResultats);
		fenetreLayout = new CardLayout();
		panelCentre = new JPanel(fenetreLayout);
		
		contenu.add(panelCentre,BorderLayout.CENTER);
		
		boutonTournois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_select_tournoi();	
			}
		});
		boutonTours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tours_tournoi();	
			}
		});
		boutonParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_details_tournoi();
			}
		});
		boutonEquipes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_equipes();
			}
		});
		boutonMatchs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_matchs();
			}
		});
		boutonResultats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tournoi_resultats();
			}
		});
		
		tracer_select_tournoi();
	}
	
	public void setStatutSelect(String t){
		statutSelect.setText(statutDeft + "" + t);
	}
	public void majBoutons(){
		if( tournoi == null){
			boutonTournois.setEnabled(true);
			boutonEquipes.setEnabled(false);
			boutonMatchs.setEnabled(false);
			boutonTours.setEnabled(false);
			boutonResultats.setEnabled(false);
			boutonParams.setEnabled(false);			
		}else{
			switch(tournoi.getStatut()){
			case 0:
				boutonTournois.setEnabled(true);
				boutonEquipes.setEnabled(true);
				boutonMatchs.setEnabled(false);
				boutonTours.setEnabled(false);
				boutonResultats.setEnabled(false);
				boutonParams.setEnabled(true);	
			break;
			case 2:
				boutonTournois.setEnabled(true);
				boutonEquipes.setEnabled(true);
				boutonMatchs.setEnabled(tournoi.getNbTours() > 0);
				boutonTours.setEnabled(true);
				
				int total=-1, termines=-1;
				try {
					ResultSet rs = statement.executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + this.tournoi.getIdTournoi() +" GROUP by id_tournoi ;");
					rs.next();
					total    = rs.getInt(1);
					termines = rs.getInt(2);
				} catch (SQLException e) {
					e.printStackTrace();
					return ;
				}
				boutonResultats.setEnabled(total == termines && total > 0);			
				boutonParams.setEnabled(true);					
			break;
			}
		}
	}
	public void tracer_select_tournoi(){
		
		tournoi = null;
		majBoutons();

		int nbLignes = 0;
		nomsTournois = new Vector<String>();
       this.setStatutSelect("sélection d'un tournoi");
		ResultSet resultatStatement;
		try {
			resultatStatement = statement.executeQuery("SELECT * FROM tournois;");

			while( resultatStatement.next() ){
				nbLignes++;
				nomsTournois.add(resultatStatement.getString("nom_tournoi"));
			}
			
			if( nbLignes == 0){
				//System.out.println("Pas de résultats");
				//t.add(new JLabel("Aucun tournoi n'a �t� cr��"));
			}else{
				//System.out.println(nbdeLignes + " r�sultats");
				
			}
			resultatStatement.close();
		} catch (SQLException e) {
			System.out.println("Erreur lors de la requète :" + e.getMessage());
			e.printStackTrace();
		}
		
		if(tournoisTrace){
			listeTournois.setListData(nomsTournois);

	        if(nbLignes == 0){
	        	boutonSelectTournoi.setEnabled(false);
	        	boutonDeleteTournoi.setEnabled(false);
	        }else{
	        	boutonSelectTournoi.setEnabled(true);
	        	boutonDeleteTournoi.setEnabled(true);
	        	listeTournois.setSelectedIndex(0);
	        }
			fenetreLayout.show(panelCentre, TOURNOIS);


		}else{
		    tournoisTrace = true;
			JPanel panelTournois = new JPanel();
			
			panelTournois.setLayout(new BoxLayout(panelTournois, BoxLayout.Y_AXIS));
			panelCentre.add(panelTournois,TOURNOIS);
			gt = new JTextArea("Gestion des tournois\nXXXXX XXXXXXXX, juillet 2012");
			gt.setAlignmentX(Component.CENTER_ALIGNMENT);
			gt.setEditable(false);
			panelTournois.add(gt);
			
			// Recherche de la liste des tournois
			panelListeTournois = new JPanel();
			
			panelTournois.add(panelListeTournois);		
	
			listeTournois = new JList<String>(nomsTournois); 
			listeTournois.setAlignmentX(Component.LEFT_ALIGNMENT); 
			listeTournois.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		    listeTournois.setVisibleRowCount(-1);
		    JScrollPane listScroller = new JScrollPane(listeTournois);
	        listScroller.setPreferredSize(new Dimension(250, 180));
	        //listScroller.setAlignmentX(LEFT_ALIGNMENT);
	        
	        labelTournois = new JLabel("Liste des tournois");
	        labelTournois.setLabelFor(listeTournois);
	        labelTournois.setAlignmentX(Component.LEFT_ALIGNMENT);
	        panelTournois.add(labelTournois);
	        //c.add(Box.createRigidArea(new Dimension(0,0)));
	        panelTournois.add(listScroller);
	        panelTournois.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	        
	        
	
	        Box bh = Box.createHorizontalBox();
	        panelTournois.add(bh);
			boutonCreerTournoi = new JButton("Créer un nouveau tournoi");
			boutonSelectTournoi = new JButton("Sélectionner le tournoi");
			boutonDeleteTournoi = new JButton("Supprimer le tournoi");
			bh.add(boutonCreerTournoi);
			bh.add(boutonSelectTournoi);	
			bh.add(boutonDeleteTournoi);
			
			panelTournois.updateUI();
	        if(nbLignes == 0){
	        	boutonSelectTournoi.setEnabled(false);
	        	boutonDeleteTournoi.setEnabled(false);
	        }else{
	        	listeTournois.setSelectedIndex(0);
	        }
	        
	        boutonCreerTournoi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Tournoi.creerTournoi(Fenetre.this.statement);
					Fenetre.this.tracer_select_tournoi();
					//String nt = JOptionPane.showInputDialog("Nom du tournoi ?");
					//ResultSet rs = Fenetre.this.s.executeQuery("SELECT)
					//Fenetre.this.s.execute("INSERT INTO TOURNOI (id_tournoi)
				}
			});
	        
	        boutonDeleteTournoi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Tournoi.deleteTournoi(Fenetre.this.statement, Fenetre.this.listeTournois.getSelectedValue());
					Fenetre.this.tracer_select_tournoi();
	
					
				}
			});
	        boutonSelectTournoi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String nt = Fenetre.this.listeTournois.getSelectedValue();
					Fenetre.this.tournoi = new Tournoi(nt, Fenetre.this.statement);
					//Fenetre.this.detracer_select_tournoi();
					Fenetre.this.tracer_details_tournoi();
					Fenetre.this.setStatutSelect("Tournoi \" " + nt + " \"");
					
				}
			});
	        fenetreLayout.show(panelCentre, TOURNOIS);
		}
        
		
		
	}
    
	private JLabel                     detailt_nom;
	private JLabel                     detailt_statut;
	private JLabel                     detailt_nbtours;
	//JButton                    detailt_enregistrer;
	
	public void tracer_details_tournoi(){
		if(tournoi == null){
			return ;
		}
		majBoutons();
		
		if(detailsTrace){
			detailt_nom.setText(tournoi.getNom());
			detailt_statut.setText(tournoi.getNStatut());
			detailt_nbtours.setText(Integer.toString(tournoi.getNbTours()));
		}else{
			detailsTrace = false;
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.add(new JLabel("Détail du tournoi"));
			panelCentre.add(p, DETAIL);
			
			JPanel tab = new JPanel( new GridLayout(4,2));
			detailt_nom = new JLabel(tournoi.getNom());
			tab.add(new JLabel("Nom du tournoi"));
			tab.add(detailt_nom);

			detailt_statut = new JLabel(tournoi.getNStatut());
			tab.add(new JLabel("Statut"));
			tab.add(detailt_statut);
			
			detailt_nbtours = new JLabel(Integer.toString(tournoi.getNbTours()));
			tab.add(new JLabel("Nombre de tours:"));
			tab.add(detailt_nbtours);
			//detailt_nbtours.setPreferredSize(new Dimension(40,40));

			p.add(tab);
			
			//detailt_enregistrer = new JButton("Enregistrer");
			//p.add(Box.createHorizontalGlue());
			//p.add(detailt_enregistrer);
			p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			//p.add(new JLabel("  e"));
			//tab.setPreferredSize(new Dimension(-1, 200));
			

		}
		fenetreLayout.show(panelCentre, DETAIL);
		
	}

	private AbstractTableModel eq_modele;
    private JButton            eq_ajouter;
    private JButton            eq_supprimer;
    private JButton            eq_valider;
    private JScrollPane        eq_js;
    private JTable                     eq_jt;
    private JPanel                     eq_p;
    private BoxLayout                  eq_layout;
    private JLabel                     eq_desc;

	public void tracer_tournoi_equipes(){
		if(tournoi == null){
			return ;
		}
		majBoutons();
		if(equipesTrace){
			tournoi.majEquipes();
			eq_modele.fireTableDataChanged();
		}else{
			equipesTrace = true;
			eq_p      = new JPanel();
			eq_layout = new BoxLayout(eq_p, BoxLayout.Y_AXIS);
			eq_p.setLayout(eq_layout);
			eq_desc = new JLabel("Equipes du tournoi");
			eq_p.add(eq_desc);
			eq_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panelCentre.add(eq_p, EQUIPES);
	
			eq_modele = new AbstractTableModel() {
				

				private static final long serialVersionUID = 1L;

				@Override
				public Object getValueAt(int arg0, int arg1) {
					Object r=null;
					switch(arg1){
					case 0:
						r= tournoi.getEquipe(arg0).getNumEquipe();
					break;
					case 1:
						r= tournoi.getEquipe(arg0).getNomEquipe1();
					break;
					case 2:
						r= tournoi.getEquipe(arg0).getNomEquipe2();
					break;
					}
					return r;
		
				}
				public String getColumnName(int col) {
				        if(col == 0){
				        	return "Numéro d'équipe";
				        }else if(col == 1){
				        	return "Joueur 1";
				        }else if(col == 2){
				        	return "Joueur 2";
				        }else{
				        	return "??";
				        }
				 }
				@Override
				public int getRowCount() {
					if(tournoi == null)return 0;
					return tournoi.getNbEquipes();
				}
				
				@Override
				public int getColumnCount() {
					return 3;
				}
				public boolean isCellEditable(int x, int y){
					if(tournoi.getStatut() != 0) return false;
					return y > 0;
				}
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					Equipe e = tournoi.getEquipe(rowIndex);
					if( columnIndex == 0){
						
					}else if( columnIndex == 1){
						e.setNomEquipe1((String)aValue);
					}else if( columnIndex == 2){
						e.setNomEquipe2((String)aValue);
					}
					tournoi.majEquipe(rowIndex);
					fireTableDataChanged();
				}
			};
			eq_jt = new JTable(eq_modele);
			eq_js = new JScrollPane(eq_jt);
			eq_p.add(eq_js);
			//jt.setPreferredSize(getMaximumSize());

			JPanel bt    = new JPanel();
			eq_ajouter   = new JButton("Ajouter une équipe");
			eq_supprimer = new JButton("Supprimer une équipe");
			eq_valider   = new JButton("Valider les équipes");
			
			eq_ajouter.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tournoi.ajouterEquipe();
					eq_valider.setEnabled(tournoi.getNbEquipes() > 0 && tournoi.getNbEquipes() % 2 == 0) ;
					eq_modele.fireTableDataChanged();
					if(tournoi.getNbEquipes() > 0){
						eq_jt.getSelectionModel().setSelectionInterval(0, 0);
					
					}	
					
					
				}
			});
			eq_supprimer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(Fenetre.this.eq_jt.getSelectedRow() != -1){
						tournoi.supprimerEquipe(tournoi.getEquipe(Fenetre.this.eq_jt.getSelectedRow()).getIdEquipe());
					}
					eq_valider.setEnabled(tournoi.getNbEquipes() > 0 && tournoi.getNbEquipes() % 2 == 0) ;
					eq_modele.fireTableDataChanged();
					if(tournoi.getNbEquipes() > 0){
						eq_jt.getSelectionModel().setSelectionInterval(0, 0);
					}					
				}
			});
			eq_valider.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					tournoi.genererMatchs();
					Fenetre.this.majBoutons();
					Fenetre.this.tracer_tournoi_matchs();
					
				}
			});
			if(tournoi.getNbEquipes() > 0){
				eq_jt.getSelectionModel().setSelectionInterval(0, 0);
			}
			bt.add(eq_ajouter);
			bt.add(eq_supprimer);
			bt.add(eq_valider);
			eq_p.add(bt);
			eq_p.add(new JLabel("Dans le cas de nombre d'équipes impair, créer une équipe virtuelle"));
		}
		if(tournoi.getStatut() != 0){
			eq_ajouter.setEnabled(false);
			eq_supprimer.setEnabled(false);
			eq_valider.setEnabled(tournoi.getStatut() == 1);
		}else{
			eq_ajouter.setEnabled(true);
			eq_supprimer.setEnabled(true);	
			eq_valider.setEnabled(tournoi.getNbEquipes() > 0) ;
		}
		fenetreLayout.show(panelCentre, EQUIPES);
		
	}

	private JTable                     tours_t;
	private JScrollPane                tours_js;
	private JPanel                     tours_p;
	private BoxLayout                  tours_layout;
	private JLabel                     tours_desc;
	
	private JButton                    tours_ajouter;
	private JButton                    tours_supprimer;
	private JButton                    tours_rentrer;
	
	public void tracer_tours_tournoi(){
		
		
		if(tournoi == null){
			return ;
		}
		majBoutons();
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;
		boolean peutajouter = true;
		try {
			ResultSet rs = statement.executeQuery("Select num_tour,count(*) as tmatchs, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.num_tour=m.num_tour  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + this.tournoi.getIdTournoi() + " GROUP BY m.num_tour,m.id_tournoi;");
			while(rs.next()){
				v = new Vector<Object>();
				v.add(rs.getInt("num_tour"));
				v.add(rs.getInt("tmatchs"));
				v.add(rs.getString("termines"));
				to.add(v);
				peutajouter = peutajouter && rs.getInt("tmatchs") == rs.getInt("termines");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro du tour");
		columnNames.add("Nombre de matchs");
		columnNames.add("Matchs joués");
		tours_t = new JTable(to,columnNames );
		if(toursTrace){
			tours_js.setViewportView(tours_t);
		}else{
			toursTrace  = true;
			tours_p      = new JPanel();
			tours_layout = new BoxLayout( tours_p, BoxLayout.Y_AXIS);
			tours_p.setLayout( tours_layout);
			tours_desc = new JLabel("Tours");
			tours_p.add(tours_desc);
			tours_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panelCentre.add(tours_p, TOURS);


			
			tours_js = new JScrollPane();
			tours_js.setViewportView(tours_t);
			tours_p.add(tours_js);
			
			JPanel bt    = new JPanel();
			tours_ajouter   = new JButton("Ajouter un tour");
			tours_supprimer = new JButton("Supprimer le dernier tour");
			//tours_rentrer   = new JButton("Rentrer les scores du tour s�lectionn�");
			bt.add(tours_ajouter);
			bt.add(tours_supprimer);
			//bt.add(tours_rentrer);
			tours_p.add(bt);	
			tours_p.add(new JLabel("Pour pouvoir ajouter un tour, terminez tous les matchs du précédent."));
			tours_p.add(new JLabel("Le nombre maximum de tours est \"le nombre total d'équipes - 1\""));
			tours_ajouter.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					tournoi.ajouterTour();
					Fenetre.this.tracer_tours_tournoi();								
				}
			});
			tours_supprimer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					tournoi.supprimerTour();
					Fenetre.this.tracer_tours_tournoi();				
				}
			});
		}
		if(to.size() == 0){
			tours_supprimer.setEnabled(false);
			tours_ajouter.setEnabled(true);
		}else{
			
			tours_supprimer.setEnabled( tournoi.getNbTours() > 1);
			
			if(!peutajouter || tournoi.getNbTours()  >= tournoi.getNbEquipes()-1 ){
				tours_ajouter.setEnabled(false);
			}else
				tours_ajouter.setEnabled(true);
		}

		fenetreLayout.show(panelCentre, TOURS);
		//tours_ajouter.setEnabled(peutajouter);
	}

	private AbstractTableModel match_modele;
    private JScrollPane        match_js;
    private JTable                     match_jt;
    private JPanel                     match_p;
    private BoxLayout                  match_layout;
    private JLabel                     match_desc;
    private JPanel                     match_bas;
    private JLabel                     match_statut;
    private JButton                    match_valider;

	public void tracer_tournoi_matchs(){
		if(tournoi == null){
			return ;
		}
		majBoutons();
		if(matchTrace){
			tournoi.majMatch();
			match_modele.fireTableDataChanged();
			majStatutM();
		}else{
			matchTrace = true;
			match_p      = new JPanel();
			match_layout = new BoxLayout(match_p, BoxLayout.Y_AXIS);
			match_p.setLayout(match_layout);
			match_desc = new JLabel("Matchs du tournoi");
			match_p.add(match_desc);
			match_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panelCentre.add(match_p, MATCHS );
	
			match_modele = new AbstractTableModel() {
				private static final long serialVersionUID = 1L;
				@Override
				public Object getValueAt(int arg0, int arg1) {
					Object r=null;
					switch(arg1){
					case 0:
						r= tournoi.getMatch(arg0).getNumTour();
					break;
					case 1:
						r= tournoi.getMatch(arg0).getIdEquipe1();
					break;
					case 2:
						r= tournoi.getMatch(arg0).getIdEquipe2();
					break;
					case 3:
						r= tournoi.getMatch(arg0).getScoreEquipe1();
					break;
					case 4:
						r= tournoi.getMatch(arg0).getScoreEquipe2();
					break;
					}
					return r;
		
				}
				public String getColumnName(int col) {
				        if(col == 0){
				        	return "Tour";
				        }else if(col == 1){
				        	return "Équipe 1";
				        }else if(col == 2){
				        	return "Équipe 2";
				        }else if(col == 3){
				        	return "Score équipe 1";
				        }else if(col == 4){
				        	return "Score équipe 2";
				        }else{
				        	return "??";
				        }
				 }
				@Override
				public int getRowCount() {
					// TODO Auto-generated method stub
					if(tournoi == null)return 0;
					return tournoi.getNbMatchs();
				}
				
				@Override
				public int getColumnCount() {
					// TODO Auto-generated method stub
					return 5;
				}
				public boolean isCellEditable(int x, int y){
					return y > 2 && tournoi.getMatch(x).getNumTour() == tournoi.getNbTours();
				}
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					Match m = tournoi.getMatch(rowIndex);
					if( columnIndex == 0){
						
					}else if( columnIndex == 3){
						try{
							int sco = Integer.parseInt((String)aValue);
							m.setScoreEquipe1(sco);
							tournoi.majMatch(rowIndex);
							
						}catch(Exception e){
							return ;
						}
						
					}else if( columnIndex == 4){
						try{
							int sco = Integer.parseInt((String)aValue);
							m.setScoreEquipe2(sco);
							tournoi.majMatch(rowIndex);
							
						}catch(Exception e){
							return ;
						}
					}

					fireTableDataChanged();
					Fenetre.this.majStatutM();
					Fenetre.this.majBoutons();
				}
			};
			match_jt = new JTable(match_modele);

			match_js = new JScrollPane(match_jt);
			match_p.add(match_js);
			//jt.setPreferredSize(getMaximumSize());

			System.out.println("truc2");
			
			match_bas = new JPanel();
			match_bas.add(match_statut = new JLabel("?? Matchs joués"));
			match_bas.add(match_valider = new JButton("Afficher les résultats"));
			match_valider.setEnabled(false);
			
			match_p.add(match_bas);
			majStatutM();

			
		}

		fenetreLayout.show(panelCentre, MATCHS);
		
	}
	
    private JScrollPane        resultats_js;
    private JTable                     resultats_jt;
    private JPanel                     resultats_p;
    private BoxLayout                  resultats_layout;
    private JLabel                     resultats_desc;
    private JPanel                     resultats_bas;
    private JLabel                     resultats_statut;


	public void tracer_tournoi_resultats(){
		if(tournoi == null){
			return ;
		}
		
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;		
		try {
			ResultSet rs = statement.executeQuery("SELECT equipe,(SELECT nom_j1 FROM equipes e WHERE e.id_equipe = equipe AND e.id_tournoi = " + this.tournoi.getIdTournoi() + ") as joueur1,(SELECT nom_j2 FROM equipes e WHERE e.id_equipe = equipe AND e.id_tournoi = " + this.tournoi.getIdTournoi() + ") as joueur2, SUM(score) as score, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes, (SELECT COUNT(*) FROM matchs m WHERE m.equipe1 = equipe OR m.equipe2=equipe) as matchs_joues FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + this.tournoi.getIdTournoi() + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + this.tournoi.getIdTournoi() + ") GROUP BY equipe ORDER BY matchs_gagnes DESC;");
			while(rs.next()){
				v = new Vector<Object>();
				v.add(rs.getInt("equipe"));
				v.add(rs.getString("joueur1"));
				v.add(rs.getString("joueur2"));
				v.add(rs.getInt("score"));
				v.add(rs.getInt("matchs_gagnes"));
				v.add(rs.getInt("matchs_joues"));
				to.add(v);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro d'équipe");
		columnNames.add("Nom joueur 1");
		columnNames.add("Nom joueur 2");
		columnNames.add("Score");
		columnNames.add("Matchs gagnés");
		columnNames.add("Matchs joués");
		resultats_jt = new JTable(to,columnNames );		
		resultats_jt.setAutoCreateRowSorter(true);
		
		if(resultatsTrace){
			resultats_js.setViewportView(resultats_jt);
		}else{
			resultatsTrace = true;
			resultats_p      = new JPanel();
			resultats_layout = new BoxLayout(resultats_p, BoxLayout.Y_AXIS);
			
			resultats_p.setLayout(resultats_layout);
			resultats_desc = new JLabel("Résultats du tournoi");
			resultats_p.add(resultats_desc);
			resultats_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panelCentre.add(resultats_p, RESULTATS );
	
			
			
			

			resultats_js = new JScrollPane(resultats_jt);
			resultats_p.add(resultats_js);
			//jt.setPreferredSize(getMaximumSize());

			
			resultats_bas = new JPanel();
			resultats_bas.add(resultats_statut = new JLabel("Gagnant:"));
			
			resultats_p.add(resultats_bas);
		}

		fenetreLayout.show(panelCentre, RESULTATS);
		
	}
	
	private void majStatutM(){
		int total=-1, termines=-1;
		try {
			ResultSet rs = statement.executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + this.tournoi.getIdTournoi() +" GROUP by id_tournoi ;");
			rs.next();
			total    = rs.getInt(1);
			termines = rs.getInt(2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		match_statut.setText(termines + "/" + total + " matchs terminés");
		match_valider.setEnabled(total == termines);
	}
}