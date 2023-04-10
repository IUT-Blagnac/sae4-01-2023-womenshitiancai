package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import dao.AccessDB;

public class ToursPanel extends JPanel{
    
    MainFrame f;

    Vector< Vector<Object>> to;
    boolean peutajouter;

    private JTable                     tours_t;
    private JScrollPane                tours_js;
    private JPanel                     tours_p;
    private BoxLayout                  tours_layout;
    private JLabel                     tours_desc;
    
    private JButton                    tours_ajouter;
    private JButton                    tours_supprimer;
    private JButton                    tours_rentrer;

    public void before() {
      to = new Vector<Vector<Object>>();
      Vector<Object> v;
      peutajouter = true;
      try {
        
        ResultSet rs = AccessDB.getInstance().getStatement().executeQuery("Select num_tour,count(*) as tmatchs, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.num_tour=m.num_tour  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + f.getTournoi().getIdTournoi() + " GROUP BY m.num_tour,m.id_tournoi;");
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
    }

    public ToursPanel(MainFrame f) {
		  super();

      this.f = f;

      before();
      
			this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS));
			this.add(new JLabel("Tours"));
			this.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			f.getCenterPanel().add(this, MainFrame.TOURS);

			tours_js = new JScrollPane();
			tours_js.setViewportView(tours_t);
			this.add(tours_js);
			
			JPanel bt    = new JPanel();
			tours_ajouter   = new JButton("Ajouter un tour");
			tours_supprimer = new JButton("Supprimer le dernier tour");
			//tours_rentrer   = new JButton("Rentrer les scores du tour s�lectionn�");
			bt.add(tours_ajouter);
			bt.add(tours_supprimer);
			//bt.add(tours_rentrer);
			this.add(bt);	
			this.add(new JLabel("Pour pouvoir ajouter un tour, terminez tous les matchs du précédent."));
			this.add(new JLabel("Le nombre maximum de tours est \"le nombre total d'équipes - 1\""));
			tours_ajouter.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					f.getTournoi().ajouterTour();
					f.tracer_tours_tournoi();								
				}
			});
			tours_supprimer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					f.getTournoi().supprimerTour();
					f.tracer_tours_tournoi();				
				}
			});
      after();
    }

    public void refresh() {
      before();
			tours_js.setViewportView(tours_t);
      after();
    }

    public void after() {
      
      if(to.size() == 0){
        tours_supprimer.setEnabled(false);
        tours_ajouter.setEnabled(true);
      }else{
        
        tours_supprimer.setEnabled( f.getTournoi().getNbTours() > 1);
        
        if(!peutajouter || f.getTournoi().getNbTours()  >= f.getTournoi().getNbEquipes()-1 ){
          tours_ajouter.setEnabled(false);
        }else
          tours_ajouter.setEnabled(true);
      }
    }
}
