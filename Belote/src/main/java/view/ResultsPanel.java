package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import dao.AccessDB;

public class ResultsPanel extends JPanel{
    
    MainFrame f;

    Vector< Vector<Object>> to;
    
    private JScrollPane        resultats_js;
    private JTable                     resultats_jt;
    private BoxLayout                  resultats_layout;
    private JLabel                     resultats_desc;
    private JPanel                     resultats_bas;
    private JLabel                     resultats_statut;

    public void before() {
        
		to = new Vector<Vector<Object>>();
		Vector<Object> v;		
		try {
			ResultSet rs = AccessDB.getInstance().getStatement().executeQuery("SELECT equipe,(SELECT nom_j1 FROM equipes e WHERE e.id_equipe = equipe AND e.id_tournoi = " + f.getTournoi().getIdTournoi() + ") as joueur1,(SELECT nom_j2 FROM equipes e WHERE e.id_equipe = equipe AND e.id_tournoi = " + f.getTournoi().getIdTournoi() + ") as joueur2, SUM(score) as score, (SELECT count(*) FROM matchs m WHERE (m.equipe1 = equipe AND m.score1 > m.score2  AND m.id_tournoi = id_tournoi) OR (m.equipe2 = equipe AND m.score2 > m.score1 )) as matchs_gagnes, (SELECT COUNT(*) FROM matchs m WHERE m.equipe1 = equipe OR m.equipe2=equipe) as matchs_joues FROM  (select equipe1 as equipe,score1 as score from matchs where id_tournoi=" + f.getTournoi().getIdTournoi() + " UNION select equipe2 as equipe,score2 as score from matchs where id_tournoi=" + f.getTournoi().getIdTournoi() + ") GROUP BY equipe ORDER BY matchs_gagnes DESC;");
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
		
    }

    public ResultsPanel(MainFrame f) {
		super();

        this.f = f;

		before();

        resultats_layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        
        this.setLayout(resultats_layout);
        resultats_desc = new JLabel("Résultats du tournoi");
        this.add(resultats_desc);
        this.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        f.getCenterPanel().add(this, MainFrame.RESULTATS );

        
        resultats_js = new JScrollPane(resultats_jt);
        this.add(resultats_js);
    
        resultats_bas = new JPanel();
        resultats_bas.add(resultats_statut = new JLabel("Gagnant:"));
        
        this.add(resultats_bas);
    }

    public void refresh() {
        before();
        resultats_js.setViewportView(resultats_jt);
    }
}
