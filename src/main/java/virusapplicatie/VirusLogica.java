/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virusapplicatie;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class VirusLogica {

    /**
     *
     */
    public static HashMap<String, HashSet<Virus>> host2VirusMap;

    /**
     *
     */
    public static List<Virus> virusList1,

    /**
     *
     */
    virusList2,

    /**
     *
     */
    overlapList;

    /**
     *
     */
    public static final String[] CLASSIFICATIES = {"Any", "dsRNA", "dsDNA", "ssRNA", "ssDNA", "Retrovirus", "Satelite virus and Virophage", "Viroid", "Other"};

    /**
     * Opent FileChooser om file te selecteren.
     *
     * @return de geselecteerde File of null bij exceptions
     * @throws FileNotFoundException als de geselecteerde File niet gevonden is
     */
    public static File selectFile() throws FileNotFoundException {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Tab seperated values", "tsv"));
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                return chooser.getSelectedFile();
            } else {
                throw new FileNotFoundException();
            }
        }
        return null;
    }

    /**
     * Maakt HashMap van String host ID + (naam) naar HashSet van Virussen.
     *
     * @param file de te lezen tsv-file
     */
    public static void createHost2VirusMap(File file) {
        try {
            host2VirusMap = new HashMap<>();
            HashSet<Virus> virusSet = new HashSet<>();
            TsvParserSettings settings = new TsvParserSettings();
            settings.getFormat().setLineSeparator("\n");
            TsvParser parser = new TsvParser(settings);
            String[] row;
            parser.beginParsing(file);
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
            JOptionPane.showMessageDialog(null, ex.getMessage(), "NumberFormatException", JOptionPane.ERROR_MESSAGE);
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "IndexOutOfBoundsException", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Bepaalt virusclassificatie adhv sleutelwoorden in viruslineage
     * @param virusLineage te vinden in kolom 3 van tsv file
     * @return de bepaalde classificatie (String)
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
     * Procedure die de GUI moet ondergaan als op de openbutton in wordt geklikt
     */
    public static void loadFile() {
        try {
            File selectedFile = selectFile();
            if (selectedFile != null && selectedFile.exists()) {
                createHost2VirusMap(selectedFile);
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
            JOptionPane.showMessageDialog(null, ex.getMessage(), "FileNotFoundExeption", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sorteert en filtert viruslijsten en bepaald de overlap.
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
     * Toont virusljijst in textarea.
     * @param textArea
     * @param virusList
     */
    public static void updateTextArea(JTextArea textArea, List<Virus> virusList) {
        textArea.setText("");
        virusList.forEach((Virus virus) -> {
            textArea.append(virus.getId() + "\n");
        });
    }
}