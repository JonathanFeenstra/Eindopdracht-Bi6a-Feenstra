/*
 * Virus App
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 */
package virusapplicatie;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Bevat de logica voor het parsen van bestanden, inladen van data, sorteren,
 * filteren en bijwerken van de viruslijsten.
 *
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class VirusLogica {

    static HashMap<String, HashSet<Virus>> host2VirusMap;
    static List<Virus> virusList1, virusList2, overlapList;
    static final String[] CLASSIFICATIES = {"Any", "dsRNA", "dsDNA", "ssRNA", "ssDNA", "Retrovirus", "Satelite virus and Virophage", "Viroid", "Other"};
    static final String FILEFORMAT_ERRORMESSAGE = "Onjuist bestandsformat. Zorg ervoor dat het bestand dezelfde structuur heeft als ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv.\n";
    static String filePath;

    /**
     * Opent FileChooser om file te selecteren.
     *
     * @return reader van de geselecteerde file of null als niets geselecteerd
     * is en bij exceptions
     * @throws FileNotFoundException als de geselecteerde file niet gevonden is
     */
    public static Reader selectFile() throws FileNotFoundException {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Tab seperated values", "tsv"));
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                filePath = chooser.getSelectedFile().getAbsolutePath();
                return new FileReader(chooser.getSelectedFile());
            } else {
                throw new FileNotFoundException();
            }
        }
        return null;
    }

    /**
     * Maakt HashMap van (host ID + naam) naar HashSet van Virussen.
     *
     * @param reader van de te lezen tsv-file
     */
    public static void createHost2VirusMap(Reader reader) {
        try {
            host2VirusMap = new HashMap<>();
            HashSet<Virus> virusSet = new HashSet<>();
            TsvParserSettings settings = new TsvParserSettings();
            settings.getFormat().setLineSeparator("\n");
            TsvParser parser = new TsvParser(settings);
            String[] row;
            parser.beginParsing(reader);
            parser.parseNext(); //Sla eerste regel met kopjes over
            while ((row = parser.parseNext()) != null) {
                if (row[7] != null) { //Virussen zonder host ID worden niet opgeslagen
                    int virusId = Integer.parseInt(row[0]), hostId = Integer.parseInt(row[7]);
                    Virus currVirus = new Virus(virusId, row[1], hostId, determineVirusClass(row[2]));
                    currVirus.addHost(hostId);
                    virusSet.add(currVirus);
                    String key = row[7] + " (" + row[8] + ")";
                    if (!host2VirusMap.containsKey(key)) {
                        host2VirusMap.put(key, (HashSet) virusSet.clone());
                    } else {
                        host2VirusMap.get(key).add(currVirus);
                        host2VirusMap.get(key).stream().filter((virus) -> (!virus.getHostList().contains(hostId))).forEachOrdered((virus) -> {
                            virus.addHost(hostId);
                        });
                    }
                    virusSet.clear();
                }
            }
            parser.stopParsing();
            reader.close();
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(null, FILEFORMAT_ERRORMESSAGE + ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Bepaalt virusclassificatie a.d.h.v. sleutelwoorden in viruslineage.
     *
     * @param virusLineage te vinden in kolom 3 van tsv file
     * @return de bepaalde classificatie
     */
    public static String determineVirusClass(String virusLineage) {
        for (int i = 1; i < 5; i++) {
            if (virusLineage.contains(CLASSIFICATIES[i])) {
                return CLASSIFICATIES[i];
            }
        }
        if (virusLineage.contains("Retro")) {
            return CLASSIFICATIES[5];
        } else if (virusLineage.contains("Satelite") || virusLineage.contains("virophage")) {
            return CLASSIFICATIES[6];
        } else if (virusLineage.startsWith("Viroid")) {
            return CLASSIFICATIES[7];
        }
        return CLASSIFICATIES[8];
    }

    /**
     * Procedure die steeds moet worden ondergaan als een bestand wordt geopend.
     * Thijs Weenink heeft mij geholpen met het inladen van URL's
     *
     * @param evt de ActionEvent van de bron
     *
     */
    public static void loadFile(ActionEvent evt) {
        Reader selectedFileReader;
        try {
            if (evt.getSource().equals(VirusGUI.openMenuItem)) {
                selectedFileReader = selectFile();
                VirusGUI.searchTextField.setText(filePath);
            } else {
                try {
                    selectedFileReader = new InputStreamReader(new URL(VirusGUI.searchTextField.getText()).openStream());
                } catch (MalformedURLException ex) {
                    selectedFileReader = new FileReader(VirusGUI.searchTextField.getText());
                }
            }
            if (selectedFileReader != null) {
                createHost2VirusMap(selectedFileReader);
                VirusGUI.hostComboBox1.setModel(new DefaultComboBoxModel(host2VirusMap.keySet().toArray()));
                VirusGUI.hostComboBox2.setModel(new DefaultComboBoxModel(host2VirusMap.keySet().toArray()));
                VirusGUI.hostComboBox1.setEnabled(true);
                VirusGUI.hostComboBox2.setEnabled(true);
                updateLists();
                updateEditorPane(VirusGUI.virusEditorPane1, virusList1);
                updateEditorPane(VirusGUI.virusEditorPane2, virusList2);
                updateEditorPane(VirusGUI.overlapEditorPane, overlapList);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sorteert en filtert viruslijsten en bepaald de overlap op basis van
     * geselecteerde items in de VirusGUI.
     */
    public static void updateLists() {
        if (host2VirusMap != null) {
            virusList1 = new ArrayList<>(host2VirusMap.get(VirusGUI.hostComboBox1.getSelectedItem().toString()));
            virusList2 = new ArrayList<>(host2VirusMap.get(VirusGUI.hostComboBox2.getSelectedItem().toString()));
            if (VirusGUI.classComboBox.getSelectedIndex() != 0) {
                virusList1.removeIf(v -> !v.getClassificatie().equals((String) VirusGUI.classComboBox.getSelectedItem()));
                virusList2.removeIf(v -> !v.getClassificatie().equals((String) VirusGUI.classComboBox.getSelectedItem()));
            }
            Collections.sort(virusList1);
            Collections.sort(virusList2);
            Set overlapSet = new HashSet(virusList1);
            overlapSet.retainAll(virusList2);
            overlapList = new ArrayList(overlapSet);
            updateBorders();
        }
    }

    /**
     * Toont Virus-host DB links van een gegeven virusljijst in een gegeven
     * JEditorPane.
     *
     * @param editorPane
     * @param virusList
     */
    public static void updateEditorPane(JEditorPane editorPane, List<Virus> virusList) {
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

    /**
     * Geeft het aantal virussen per lijst weer als het meer is dan 0.
     */
    public static void updateBorders() {
        TitledBorder virusBorder1 = (TitledBorder) VirusGUI.virusScrollPane1.getBorder(),
                virusBorder2 = (TitledBorder) VirusGUI.virusScrollPane2.getBorder(),
                overlapBorder = (TitledBorder) VirusGUI.overlapScrollPane.getBorder();
        if (virusList1.size() > 0) {
            virusBorder1.setTitle("Viruslijst (" + virusList1.size() + ")");
            VirusGUI.virusScrollPane1.repaint();
        } else {
            virusBorder1.setTitle("Viruslijst");
            VirusGUI.virusScrollPane1.repaint();
        }
        if (virusList2.size() > 0) {
            virusBorder2.setTitle("Viruslijst (" + virusList2.size() + ")");
            VirusGUI.virusScrollPane2.repaint();
        } else {
            virusBorder2.setTitle("Viruslijst");
            VirusGUI.virusScrollPane2.repaint();
        }
        if (overlapList.size() > 0) {
            overlapBorder.setTitle("Overeenkomst (" + overlapList.size() + ")");
            VirusGUI.overlapScrollPane.repaint();
        } else {
            overlapBorder.setTitle("Overeenkomst");
            VirusGUI.overlapScrollPane.repaint();
        }
    }

    /**
     * Zorgt ervoor dat de viruslijsten en de weergave van JEditorPane's worden
     * bijgewerkt in de VirusGUI.
     */
    public static void updateAll() {
        updateLists();
        updateEditorPane(VirusGUI.virusEditorPane1, virusList1);
        updateEditorPane(VirusGUI.overlapEditorPane, overlapList);
        updateEditorPane(VirusGUI.virusEditorPane2, virusList2);
        updateEditorPane(VirusGUI.overlapEditorPane, overlapList);
    }

    /**
     * Bezoekt website bij het klikken op een hyperlink.
     *
     * @param evt de HyperlinkEvent
     */
    public static void visitHyperlink(HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(evt.getURL().toURI());
                } catch (URISyntaxException | IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Het openen van hyperlinks wordt op uw computer niet ondersteund.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
