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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import controller.Tournoi;



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
			toursPanel.before();
			toursPanel.refresh();
		}else{
			tours_trace = true;
			toursPanel = new ToursPanel(this);

		}
		toursPanel.after();

		fen.show(centerPanel, TOURS);
	}

	public void tracer_tournoi_matchs(){
		if(tournoi == null){
			return ;
		}
		majboutons();
		if(match_trace){
			tournoi.majMatch();
			matchsPanel.refresh();
		}else{
			match_trace = true;
			matchsPanel = new MatchsPanel(this);
		}

		fen.show(centerPanel, MATCHS);
		
	}
	

	public void tracer_tournoi_resultats(){
		if(tournoi == null){
			return ;
		}
		if(resultats_trace){
			resultsPanel.refresh();
		}else{
			resultats_trace = true;
			resultsPanel = new ResultsPanel(this);
			
		}

		fen.show(centerPanel, RESULTATS);
		
	}
	
	void majStatutM(JLabel match_statut, JButton match_valider){
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