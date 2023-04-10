package view;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Match;

public class MatchsPanel extends JPanel{
    
    MainFrame f;

	private AbstractTableModel match_modele;
    private JScrollPane        match_js;
    private JTable             match_jt;
    private JPanel             match_bas;
    private JLabel             match_statut;
    private JButton            match_valider;

    public MatchsPanel(MainFrame f) {
		super();

        this.f = f;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel("Matchs du tournoi"));
        this.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        f.getCenterPanel().add(this, MainFrame.MATCHS );

        match_modele = new AbstractTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public Object getValueAt(int arg0, int arg1) {
                Object r=null;
                switch(arg1){
                case 0:
                    r= f.getTournoi().getMatch(arg0).getNumTour();
                break;
                case 1:
                    r= f.getTournoi().getMatch(arg0).getIdEquipe1();
                break;
                case 2:
                    r= f.getTournoi().getMatch(arg0).getIdEquipe2();
                break;
                case 3:
                    r= f.getTournoi().getMatch(arg0).getScoreEquipe1();
                break;
                case 4:
                    r= f.getTournoi().getMatch(arg0).getScoreEquipe2();
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
                if(f.getTournoi() == null)return 0;
                return f.getTournoi().getNbMatchs();
            }
            
            @Override
            public int getColumnCount() {
                // TODO Auto-generated method stub
                return 5;
            }
            public boolean isCellEditable(int x, int y){
                return y > 2 && f.getTournoi().getMatch(x).getNumTour() == f.getTournoi().getNbTours();
            }
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                Match m = f.getTournoi().getMatch(rowIndex);
                if( columnIndex == 0){
                    
                }else if( columnIndex == 3){
                    try{
                        int sco = Integer.parseInt((String)aValue);
                        m.setScoreEquipe1(sco);
                        f.getTournoi().majMatch(rowIndex);
                        
                    }catch(Exception e){
                        return ;
                    }
                    
                }else if( columnIndex == 4){
                    try{
                        int sco = Integer.parseInt((String)aValue);
                        m.setScoreEquipe2(sco);
                        f.getTournoi().majMatch(rowIndex);
                        
                    }catch(Exception e){
                        return ;
                    }
                }

                fireTableDataChanged();
                f.majStatutM(match_statut, match_valider);
                f.majboutons();
            }
        };
        match_jt = new JTable(match_modele);

        match_js = new JScrollPane(match_jt);
        this.add(match_js);
        //jt.setPreferredSize(getMaximumSize());

        System.out.println("truc2");
        
        match_bas = new JPanel();
        match_bas.add(match_statut = new JLabel("?? Matchs joués"));
        match_bas.add(match_valider = new JButton("Afficher les résultats"));
        match_valider.setEnabled(false);
        
        this.add(match_bas);
        f.majStatutM(match_statut, match_valider);

        
    }

    public void refresh() {
        match_modele.fireTableDataChanged();
        f.majStatutM(match_statut, match_valider);
    }
}
