/*
 * Virus App - © Jonathan Feenstra 2018.
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 */

package virusapplicatie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Bevat methoden die variabelen en de weergave van de GUI bijwerken na bepaalde
 * acties.
 * 
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class VirusUpdater {

    /**
     * Werkt viruslijsten bij door te sorteren en te filteren en bepaald de
     * overlap op basis van geselecteerde items in de VirusGUI.
     */
    public static void updateLists() {
        if (VirusLogica.hostToClassToVirusMap != null) {
            HashSet<Virus> virusSet1 = VirusLogica.hostToClassToVirusMap.get(VirusGUI.hostComboBox1.getSelectedItem().toString()).get(VirusGUI.classComboBox.getSelectedItem().toString());
            HashSet<Virus> virusSet2 = VirusLogica.hostToClassToVirusMap.get(VirusGUI.hostComboBox2.getSelectedItem().toString()).get(VirusGUI.classComboBox.getSelectedItem().toString());
            if (virusSet1 != null) {
                VirusLogica.virusList1 = new ArrayList<>(virusSet1);
            } else {
                virusSet1 = new HashSet<>();
                VirusLogica.virusList1 = new ArrayList<>();
            }
            if (virusSet2 != null) {
                VirusLogica.virusList2 = new ArrayList<>(virusSet2);
            } else {
                virusSet2 = new HashSet<>();
                VirusLogica.virusList2 = new ArrayList<>();
            }
            Set overlapSet = (HashSet) virusSet1.clone();
            overlapSet.retainAll(virusSet2);
            VirusLogica.overlapList = new ArrayList(overlapSet);
            VirusLogica.sortLists();
            updateBorders();
        }
    }

    /**
     * Zorgt ervoor dat de viruslijsten en de weergave van JEditorPane's worden
     * bijgewerkt in de VirusGUI.
     */
    public static void updateAllEditorPanes() {
        updateEditorPane(VirusGUI.virusEditorPane1, VirusLogica.virusList1);
        updateEditorPane(VirusGUI.virusEditorPane2, VirusLogica.virusList2);
        updateEditorPane(VirusGUI.overlapEditorPane, VirusLogica.overlapList);
    }

    /**
     * Toont Virus-Host DB links van een gegeven virusljijst in een gegeven
     * JEditorPane.
     *
     * @param editorPane de te vullen JEditorPane
     * @param virusList de te weergeven viruslijst
     */
    public static void updateEditorPane(JEditorPane editorPane, List<Virus> virusList) {
        if (editorPane != null && virusList != null) {
            editorPane.setText("");
            HTMLDocument htmlDoc = (HTMLDocument) editorPane.getDocument();
            HTMLEditorKit htmlKit = (HTMLEditorKit) editorPane.getEditorKit();
            virusList.forEach((Virus virus) -> {
                try {
                    htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<a href=\"http://www.genome.jp/virushostdb/" + virus.getId() + "\">" + virus.getId() + "</a>", 0, 0, null);
                } catch (BadLocationException | IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    /**
     * Geeft het aantal virussen per lijst weer als het meer is dan 0.
     */
    public static void updateBorders() {
        TitledBorder virusBorder1 = (TitledBorder) VirusGUI.virusScrollPane1.getBorder();
        TitledBorder virusBorder2 = (TitledBorder) VirusGUI.virusScrollPane2.getBorder();
        TitledBorder overlapBorder = (TitledBorder) VirusGUI.overlapScrollPane.getBorder();
        if (VirusLogica.virusList1.size() > 0) {
            virusBorder1.setTitle("Viruslijst 1 (" + VirusLogica.virusList1.size() + ")");
            VirusGUI.virusScrollPane1.repaint();
        } else {
            virusBorder1.setTitle("Viruslijst 1");
            VirusGUI.virusScrollPane1.repaint();
        }
        if (VirusLogica.virusList2.size() > 0) {
            virusBorder2.setTitle("Viruslijst 2 (" + VirusLogica.virusList2.size() + ")");
            VirusGUI.virusScrollPane2.repaint();
        } else {
            virusBorder2.setTitle("Viruslijst 2");
            VirusGUI.virusScrollPane2.repaint();
        }
        if (VirusLogica.overlapList.size() > 0) {
            overlapBorder.setTitle("Overeenkomst (" + VirusLogica.overlapList.size() + ")");
            VirusGUI.overlapScrollPane.repaint();
        } else {
            overlapBorder.setTitle("Overeenkomst");
            VirusGUI.overlapScrollPane.repaint();
        }
    }

}
