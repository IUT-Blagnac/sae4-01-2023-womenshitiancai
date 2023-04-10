package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import controller.Tournoi;
import model.Match;



public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanel centerPanel;

	private Statement statement;

	private TournoisPanel tournoisPanel;
	private DetailsPanel detailsPanel;
	private EquipesPanel equipesPanel;
	private ToursPanel toursPanel;
	private MatchsPanel matchsPanel;
	private ResultsPanel resultsPanel;
	
	private JButton       btournois;
	private JButton       bequipes;
	private JButton       btours;
	private JButton       bmatchs;
	private JButton       bresultats;
	private JButton       bparams;
	
	private boolean tournois_trace  = false;
	private boolean equipes_trace   = false;
	private boolean tours_trace     = false;
	private boolean match_trace     = false;
	private boolean resultats_trace = false;
	
	private CardLayout fen;
	public final static String TOURNOIS = "Tournois";
    public final static String DETAIL   = "Paramètres du tournoi";
    public final static String EQUIPES  = "Equipes";
    public final static String TOURS    = "Tours";
    public final static String MATCHS   = "Matchs";
    public final static String RESULTATS= "Resultats";
    private Tournoi tournoi = null;
    
    private JLabel statut_slect = null;
    private final String statut_deft = "Gestion de tournois de Belote v1.0 - ";
	public MainFrame(Statement st){
		
		statement = st;
		this.setTitle("Gestion de tournoi de Belote");
		setSize(800,400);
		this.setVisible(true);
		this.setLocationRelativeTo(this.getParent());
		
		
		JPanel contenu = new JPanel();
		contenu.setLayout(new BorderLayout());
		this.setContentPane(contenu);
		
		
		JPanel phaut = new JPanel();
		contenu.add(phaut,BorderLayout.NORTH);
		
		phaut.add(statut_slect = new JLabel());
		this.setStatutSelect("Pas de tournoi sélectionné");
		
		JPanel pgauche = new JPanel();
		pgauche.setBackground(Color.RED);
		pgauche.setPreferredSize(new Dimension(130,0));
		contenu.add(pgauche,BorderLayout.WEST);
		
		
		btournois    = new JButton("Tournois");
		bparams      = new JButton("Paramètres");
		bequipes     = new JButton("Equipes");
		btours       = new JButton("Tours");
		bmatchs      = new JButton("Matchs");
		bresultats   = new JButton("Résultats");
		
		
		int taille_boutons = 100;
		int hauteur_boutons = 30;
		
		btournois.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bparams.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bequipes.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		btours.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bmatchs.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bresultats.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		
		pgauche.add(btournois);
		pgauche.add(bparams);
		pgauche.add(bequipes);
		pgauche.add(btours);
		pgauche.add(bmatchs);
		pgauche.add(bresultats);
		fen = new CardLayout();
		centerPanel = new JPanel(fen);
		
		contenu.add(centerPanel,BorderLayout.CENTER);
		
		btournois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_select_tournoi();	
			}
		});
		btours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tours_tournoi();	
			}
		});
		bparams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_details_tournoi();
			}
		});
		bequipes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_equipes();
			}
		});
		bmatchs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_matchs();
			}
		});
		bresultats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tournoi_resultats();
			}
		});
		
		tracer_select_tournoi();
	}
	
	public void setStatutSelect(String t){
		statut_slect.setText(statut_deft + "" + t);
	}
	public void majboutons(){
		if( tournoi == null){
			btournois.setEnabled(true);
			bequipes.setEnabled(false);
			bmatchs.setEnabled(false);
			btours.setEnabled(false);
			bresultats.setEnabled(false);
			bparams.setEnabled(false);			
		}else{
			switch(tournoi.getStatut()){
			case 0:
				btournois.setEnabled(true);
				bequipes.setEnabled(true);
				bmatchs.setEnabled(false);
				btours.setEnabled(false);
				bresultats.setEnabled(false);
				bparams.setEnabled(true);	
			break;
			case 2:
				btournois.setEnabled(true);
				bequipes.setEnabled(true);
				bmatchs.setEnabled(tournoi.getNbTours() > 0);
				btours.setEnabled(true);
				
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
				bresultats.setEnabled(total == termines && total > 0);			
				bparams.setEnabled(true);					
			break;
			}
		}
	}
	public void tracer_select_tournoi(){
		
		tournoi = null;
		majboutons();
		
        this.setStatutSelect("sélection d'un tournoi");
		
		if (tournois_trace) {

			tournoisPanel.refresh();

		} else {
		    tournois_trace = true;
			tournoisPanel = new TournoisPanel(statement, this);
			centerPanel.add(tournoisPanel,TOURNOIS);
			tournoisPanel.updateUI(); // IMPORTANT !
		}

		fen.show(centerPanel, TOURNOIS);
	}
    
	
	public void tracer_details_tournoi(){
		if (tournoi == null) {
			return ;
		}
		majboutons();
		
		detailsPanel = new DetailsPanel(tournoi);
		centerPanel.add(detailsPanel, DETAIL);

		fen.show(centerPanel, DETAIL);
		
	}


	public void tracer_tournoi_equipes(){
		if(tournoi == null) {
			return ;
		}
		majboutons();
		if (equipes_trace) {
			tournoi.majEquipes();
			equipesPanel.refresh();
		} else {
			equipes_trace = true;
			equipesPanel = new EquipesPanel(this);
			
			equipesPanel.refresh();
		}
		equipesPanel.updateButtons();

		fen.show(centerPanel, EQUIPES);
	}

	
	public void tracer_tours_tournoi(){
		
		
		if(tournoi == null){
			return ;
		}
		majboutons();
		
		if(tours_trace){
			toursPanel.refresh();
		}else{
			tours_trace  = true;
			toursPanel = new ToursPanel(this);

		}

		fen.show(centerPanel, TOURS);
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
		majboutons();
		if(match_trace){
			tournoi.majMatch();
			match_modele.fireTableDataChanged();
			majStatutM();
		}else{
			match_trace = true;
			match_p      = new JPanel();
			match_layout = new BoxLayout(match_p, BoxLayout.Y_AXIS);
			match_p.setLayout(match_layout);
			match_desc = new JLabel("Matchs du tournoi");
			match_p.add(match_desc);
			match_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			centerPanel.add(match_p, MATCHS );
	
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
					MainFrame.this.majStatutM();
					MainFrame.this.majboutons();
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

		fen.show(centerPanel, MATCHS);
		
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
		
		if(resultats_trace){
			resultats_js.setViewportView(resultats_jt);
		}else{
			resultats_trace = true;
			resultats_p      = new JPanel();
			resultats_layout = new BoxLayout(resultats_p, BoxLayout.Y_AXIS);
			
			resultats_p.setLayout(resultats_layout);
			resultats_desc = new JLabel("Résultats du tournoi");
			resultats_p.add(resultats_desc);
			resultats_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			centerPanel.add(resultats_p, RESULTATS );
	
			
			
			

			resultats_js = new JScrollPane(resultats_jt);
			resultats_p.add(resultats_js);
			//jt.setPreferredSize(getMaximumSize());

			
			resultats_bas = new JPanel();
			resultats_bas.add(resultats_statut = new JLabel("Gagnant:"));
			
			resultats_p.add(resultats_bas);
		}

		fen.show(centerPanel, RESULTATS);
		
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

	public Tournoi getTournoi() {
		return this.tournoi;
	}
	public void setTournoi(Tournoi tournoi) {
		this.tournoi = tournoi;
	}
	
	public JPanel getCenterPanel() {
		return centerPanel;
	}
}