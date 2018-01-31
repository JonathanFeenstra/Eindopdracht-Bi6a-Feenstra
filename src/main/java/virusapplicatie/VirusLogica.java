/*
 * Virus App
 * Datum laatste versie: 31 januari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 */
package virusapplicatie;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
                return new FileReader(chooser.getSelectedFile());
            } else {
                throw new FileNotFoundException();
            }
        }
        return null;
    }

    /**
     * Maakt HashMap van host ID + (naam) naar HashSet van Virussen.
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
                    Virus currVirus = new Virus(Integer.parseInt(row[0]), row[1], Integer.parseInt(row[7]), determineVirusClass(row[2]));
                    virusSet.add(currVirus);
                    String key = row[7] + " (" + row[8] + ")";
                    if (!host2VirusMap.containsKey(key)) {
                        host2VirusMap.put(key, (HashSet) virusSet.clone());
                    } else {
                        host2VirusMap.get(key).add(currVirus);
                        for (Virus virus : host2VirusMap.get(key)) {
                            virus.addHost(Integer.parseInt(row[7]));
                        }
                    }
                    virusSet.clear();
                }
            }
            parser.stopParsing();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, FILEFORMAT_ERRORMESSAGE + ex.toString(), "NumberFormatException", JOptionPane.ERROR_MESSAGE);
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(null, FILEFORMAT_ERRORMESSAGE + ex.toString(), "IndexOutOfBoundsException", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Bepaalt virusclassificatie adhv sleutelwoorden in viruslineage
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
     * Procedure die steed moet worden ondergaan als een bestand wordt geopend.
     * Thijs Weenink heeft mij geholpen met het inladen van URL's
     *
     * @param evt
     *
     */
    public static void loadFile(ActionEvent evt) {
        Reader selectedFileReader;
        try {
            if (evt.getSource().equals(VirusGUI.openMenuItem)) {
                selectedFileReader = selectFile();
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
                updateTextArea(VirusGUI.virusTextArea1, virusList1);
                updateTextArea(VirusGUI.virusTextArea2, virusList2);
                updateTextArea(VirusGUI.overlapTextArea, overlapList);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "FileNotFoundException", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "IOException", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sorteert en filtert viruslijsten en bepaald de overlap op basis van
     * geselecteerde items in de VirusGUI.
     */
    public static void updateLists() {
        virusList1 = new ArrayList<>(host2VirusMap.get(VirusGUI.hostComboBox1.getSelectedItem().toString()));
        virusList2 = new ArrayList<>(host2VirusMap.get(VirusGUI.hostComboBox2.getSelectedItem().toString()));
        if (VirusGUI.classComboBox.getSelectedIndex() != 0 && VirusGUI.classComboBox.getSelectedIndex() != 8) {
            virusList1.removeIf(v -> !v.getClassificatie().equals((String) VirusGUI.classComboBox.getSelectedItem()));
            virusList2.removeIf(v -> !v.getClassificatie().equals((String) VirusGUI.classComboBox.getSelectedItem()));
        } else if (VirusGUI.classComboBox.getSelectedIndex() == 8) {
            virusList1.removeIf(v -> Arrays.asList(CLASSIFICATIES).contains(v.getClassificatie()));
            virusList2.removeIf(v -> Arrays.asList(CLASSIFICATIES).contains(v.getClassificatie()));
        }
        Collections.sort(virusList1);
        Collections.sort(virusList2);
        Set overlapSet = new HashSet(virusList1);
        overlapSet.retainAll(virusList2);
        overlapList = new ArrayList(overlapSet);
    }

    /**
     * Toont een gegeven virusljijst in een gegeven JTextArea.
     *
     * @param textArea
     * @param virusList
     */
    public static void updateTextArea(JTextArea textArea, List<Virus> virusList) {
        textArea.setText("");
        virusList.forEach((Virus virus) -> {
            textArea.append(virus.getId() + "\n");
        });
    }

    /**
     * Zorgt ervoor dat de viruslijsten en de weergave van JTextArea's worden
     * bijgewerkt in de VirusGUI.
     */
    public static void updateAll() {
        updateLists();
        updateTextArea(VirusGUI.virusTextArea1, virusList1);
        updateTextArea(VirusGUI.overlapTextArea, overlapList);
        updateTextArea(VirusGUI.virusTextArea2, virusList2);
        updateTextArea(VirusGUI.overlapTextArea, overlapList);
    }

}
