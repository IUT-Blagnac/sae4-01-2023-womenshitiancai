package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.Equipe;

public class EquipesPanel extends JPanel{
    
    private MainFrame f;

	private AbstractTableModel eq_modele;
    private JButton            eq_ajouter;
    private JButton            eq_supprimer;
    private JButton            eq_valider;
    private JScrollPane        eq_js;
    private JTable             eq_jt;

    public EquipesPanel(MainFrame f) {
		super();

        this.f = f;
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel("Equipes du tournoi"));
        this.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        f.getCenterPanel().add(this, MainFrame.EQUIPES);

        eq_modele = new AbstractTableModel() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public Object getValueAt(int arg0, int arg1) {
                Object r=null;
                switch(arg1){
                case 0:
                    r= f.getTournoi().getEquipe(arg0).getNumEquipe();
                break;
                case 1:
                    r= f.getTournoi().getEquipe(arg0).getNomEquipe1();
                break;
                case 2:
                    r= f.getTournoi().getEquipe(arg0).getNomEquipe2();
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
                if(f.getTournoi() == null)return 0;
                return f.getTournoi().getNbEquipes();
            }
            
            @Override
            public int getColumnCount() {
                return 3;
            }
            public boolean isCellEditable(int x, int y){
                if(f.getTournoi().getStatut() != 0) return false;
                return y > 0;
            }
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                Equipe e = f.getTournoi().getEquipe(rowIndex);
                if( columnIndex == 0){
                    
                }else if( columnIndex == 1){
                    e.setNomEquipe1((String)aValue);
                }else if( columnIndex == 2){
                    e.setNomEquipe2((String)aValue);
                }
                f.getTournoi().majEquipe(rowIndex);
                fireTableDataChanged();
            }
        };
        eq_jt = new JTable(eq_modele);
        eq_js = new JScrollPane(eq_jt);
        this.add(eq_js);

        JPanel bt    = new JPanel();
        eq_ajouter   = new JButton("Ajouter une équipe");
        eq_supprimer = new JButton("Supprimer une équipe");
        eq_valider   = new JButton("Valider les équipes");
        
        eq_ajouter.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                f.getTournoi().ajouterEquipe();
                eq_valider.setEnabled(f.getTournoi().getNbEquipes() > 0 && f.getTournoi().getNbEquipes() % 2 == 0) ;
                eq_modele.fireTableDataChanged();
                if(f.getTournoi().getNbEquipes() > 0){
                    eq_jt.getSelectionModel().setSelectionInterval(0, 0);
                
                }
            }
        });
        eq_supprimer.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if(eq_jt.getSelectedRow() != -1){
                    f.getTournoi().supprimerEquipe(f.getTournoi().getEquipe(eq_jt.getSelectedRow()).getIdEquipe());
                }
                eq_valider.setEnabled(f.getTournoi().getNbEquipes() > 0 && f.getTournoi().getNbEquipes() % 2 == 0) ;
                eq_modele.fireTableDataChanged();
                if(f.getTournoi().getNbEquipes() > 0){
                    eq_jt.getSelectionModel().setSelectionInterval(0, 0);
                }					
            }
        });
        eq_valider.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                f.getTournoi().genererMatchs();
                f.majboutons();
                f.tracer_tournoi_matchs();
            }
        });
        if(f.getTournoi().getNbEquipes() > 0){
            eq_jt.getSelectionModel().setSelectionInterval(0, 0);
        }
        bt.add(eq_ajouter);
        bt.add(eq_supprimer);
        bt.add(eq_valider);
        this.add(bt);
        this.add(new JLabel("Dans le cas de nombre d'équipes impair, créer une équipe virtuelle"));
		
    }
    public void refresh() {
        eq_modele.fireTableDataChanged();
    }

    public void updateButtons() {
        if(f.getTournoi().getStatut() != 0){
			eq_ajouter.setEnabled(false);
			eq_supprimer.setEnabled(false);
			eq_valider.setEnabled(f.getTournoi().getStatut() == 1);
		}else{
			eq_ajouter.setEnabled(true);
			eq_supprimer.setEnabled(true);	
			eq_valider.setEnabled(f.getTournoi().getNbEquipes() > 0) ;
		}
    }
}
