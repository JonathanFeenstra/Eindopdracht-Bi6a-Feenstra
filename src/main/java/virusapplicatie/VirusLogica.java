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
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
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

    private static HashMap<String, HashMap<String, HashSet<Virus>>> hostToVirusMap;
    static HashMap<Integer, Virus> virusIdToVirusMap;
    static ArrayList<Virus> virusList1, virusList2, overlapList;
    static final String[] CLASSIFICATIES = {"Any", "dsRNA", "dsDNA", "ssRNA",
        "ssDNA", "Retrovirus", "Satelite virus and Virophage", "Viroid", "Other"};
    private static final String LINK_UNSUPPORTED_MSG = "Het openen van links wordt op uw computer niet ondersteund.";
    private static String filePath;

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
     * Leest bestand in en maakt HashMaps van (host ID + naam) naar HashMaps van
     * classificatie naar HashSet van Virus objecten en van virus ID naar Virus
     * object.
     *
     * @param reader van de te lezen tsv-file
     */
    public static void saveHostToVirusData(Reader reader) {
        try {
            hostToVirusMap = new HashMap<>();
            virusIdToVirusMap = new HashMap<>();
            TsvParserSettings settings = new TsvParserSettings();
            settings.getFormat().setLineSeparator("\n");
            TsvParser parser = new TsvParser(settings);
            String[] row;
            parser.beginParsing(reader);
            parser.parseNext(); // Sla eerste regel met kopjes over
            while ((row = parser.parseNext()) != null) {
                if (row[7] != null) { // Virussen zonder host ID worden niet opgeslagen
                    int virusId = Integer.parseInt(row[0]), hostId = Integer.parseInt(row[7]);
                    String virusNaam = row[1], virusClass = determineVirusClass(row[2]);
                    HashSet<Virus> virusPerClass = new HashSet();
                    HashMap<String, HashSet<Virus>> classToVirusMap = new HashMap<>();
                    Virus currVirus = new Virus(virusId, virusNaam, hostId, virusClass);
                    currVirus.addHost(hostId);
                    virusPerClass.add(currVirus);
                    classToVirusMap.put("Any", virusPerClass);
                    classToVirusMap.put(virusClass, virusPerClass);
                    if (!virusIdToVirusMap.containsKey(virusId)) {
                        virusIdToVirusMap.put(virusId, currVirus);
                    } else if (!virusIdToVirusMap.get(virusId).getHostList().contains(hostId)) {
                        virusIdToVirusMap.get(virusId).addHost(hostId);
                    }
                    String key = row[7] + " (" + row[8] + ")";
                    if (!hostToVirusMap.containsKey(key)) {
                        hostToVirusMap.put(key, (HashMap) classToVirusMap.clone());
                    } else if (hostToVirusMap.get(key).containsKey(virusClass)) {
                        hostToVirusMap.get(key).get(virusClass).add(currVirus);
                        hostToVirusMap.get(key).get("Any").add(currVirus);
                    } else {
                        hostToVirusMap.get(key).put(virusClass, (HashSet) virusPerClass.clone());
                    }
                }
            }
            parser.stopParsing();

        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(null, "Onjuist bestandsformat.\n"
                    + "Zorg ervoor dat het bestand dezelfde structuur heeft "
                    + "als ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv.\n"
                    + ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            hostToVirusMap = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            hostToVirusMap = null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
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
                    filePath = VirusGUI.searchTextField.getText();
                    selectedFileReader = new InputStreamReader(new URL(filePath).openStream());
                } catch (MalformedURLException ex) {
                    selectedFileReader = new FileReader(VirusGUI.searchTextField.getText());
                }
            }
            if (selectedFileReader != null) {
                saveHostToVirusData(selectedFileReader);
                if (hostToVirusMap != null) {
                    String[] hostKeys = hostToVirusMap.keySet().toArray(new String[hostToVirusMap.size()]);
                    Arrays.sort(hostKeys);
                    VirusGUI.hostComboBox1.setModel(new DefaultComboBoxModel(hostKeys));
                    VirusGUI.hostComboBox2.setModel(new DefaultComboBoxModel(hostKeys));
                    VirusGUI.hostComboBox1.setEnabled(true);
                    VirusGUI.hostComboBox2.setEnabled(true);
                    VirusGUI.hostComboBox1.setToolTipText((String) VirusGUI.hostComboBox1.getSelectedItem());
                    VirusGUI.hostComboBox2.setToolTipText((String) VirusGUI.hostComboBox2.getSelectedItem());
                    updateLists();
                    updateEditorPane(VirusGUI.virusEditorPane1, virusList1);
                    updateEditorPane(VirusGUI.virusEditorPane2, virusList2);
                    updateEditorPane(VirusGUI.overlapEditorPane, overlapList);
                    VirusGUI.saveMenuItem.setEnabled(true);
                    VirusGUI.copyMenu.setEnabled(true);
                } else {
                    filePath = "";
                    VirusGUI.searchTextField.setText("Search");
                }
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sorteert en filtert viruslijsten en bepaald de overlap op basis van
     * geselecteerde items in de VirusGUI.
     */
    public static void updateLists() {
        if (hostToVirusMap != null) {
            HashSet<Virus> virusSet1 = hostToVirusMap.get(VirusGUI.hostComboBox1.getSelectedItem().toString()).get(VirusGUI.classComboBox.getSelectedItem().toString());
            HashSet<Virus> virusSet2 = hostToVirusMap.get(VirusGUI.hostComboBox2.getSelectedItem().toString()).get(VirusGUI.classComboBox.getSelectedItem().toString());
            virusList1 = new ArrayList<>(virusSet1);
            virusList2 = new ArrayList<>(virusSet2);
            Set overlapSet = (HashSet) virusSet1.clone();
            overlapSet.retainAll(virusList2);
            overlapList = new ArrayList(overlapSet);
            Collections.sort(virusList1);
            Collections.sort(virusList2);
            Collections.sort(overlapList);
            updateBorders();
        }
    }

    /**
     * Toont Virus-Host DB links van een gegeven virusljijst in een gegeven
     * JEditorPane.
     *
     * @param editorPane de te vullen JEditorPane
     * @param virusList de te weergeven viruslijst
     */
    public static void updateEditorPane(JEditorPane editorPane, List<Virus> virusList) {
        editorPane.setText("");
        HTMLDocument htmlDoc = (HTMLDocument) editorPane.getDocument();
        HTMLEditorKit htmlKit = (HTMLEditorKit) editorPane.getEditorKit();
        virusList.forEach((Virus virus) -> {
            try {
                htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(),
                        "<a href=\"http://www.genome.jp/virushostdb/"
                        + virus.getId() + "\">" + virus.getId() + "</a>", 0, 0, null);
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
            virusBorder1.setTitle("Viruslijst 1 (" + virusList1.size() + ")");
            VirusGUI.virusScrollPane1.repaint();
        } else {
            virusBorder1.setTitle("Viruslijst 1");
            VirusGUI.virusScrollPane1.repaint();
        }
        if (virusList2.size() > 0) {
            virusBorder2.setTitle("Viruslijst 2 (" + virusList2.size() + ")");
            VirusGUI.virusScrollPane2.repaint();
        } else {
            virusBorder2.setTitle("Viruslijst 2");
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
                JOptionPane.showMessageDialog(null, LINK_UNSUPPORTED_MSG, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Overloading: open Javadoc of Virus-Host DB website afhankelijk van wat er
     * is aangeklikt.
     *
     * @param evt de ActionEvent van het aangeklikte JMenuItem
     */
    public static void visitHyperlink(ActionEvent evt) {
        if (Desktop.isDesktopSupported()) {
            try {
                if (evt.getSource() == VirusGUI.dbMenuItem) {
                    Desktop.getDesktop().browse(new URI("http://www.genome.jp/virushostdb/"));
                } else if (evt.getSource() == VirusGUI.jdocMenuItem) {
                    File javaDoc = new File(System.getProperty("user.dir") + "\\target\\site\\apidocs\\index.html");
                    Desktop.getDesktop().browse(javaDoc.toURI());
                }
            } catch (URISyntaxException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, LINK_UNSUPPORTED_MSG, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Kopieert geselecteerde viruslijst naar het klembord.
     *
     * @param evt de ActionEvent van het aangeklikte JMenuItem
     */
    public static void copyList(ActionEvent evt) {
        try {
            if (evt.getSource() == VirusGUI.copyList1Item) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(VirusGUI.virusEditorPane1.getDocument().getText(0, VirusGUI.virusEditorPane1.getDocument().getLength()).trim()), null);
            } else if (evt.getSource() == VirusGUI.copyList2Item) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(VirusGUI.virusEditorPane2.getDocument().getText(0, VirusGUI.virusEditorPane2.getDocument().getLength()).trim()), null);
            } else if (evt.getSource() == VirusGUI.copyOverlapItem) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(VirusGUI.overlapEditorPane.getDocument().getText(0, VirusGUI.overlapEditorPane.getDocument().getLength()).trim()), null);
            }
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Slaat viruslijsten op in geselecteerd bestand.
     */
    public static void saveFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileLocation = chooser.getSelectedFile().getAbsolutePath();
            if (!fileLocation.endsWith(".txt")) {
                fileLocation += ".txt";
            }
            try (PrintWriter out = new PrintWriter(fileLocation)) { // Zorgt dat flush() en close() vanzelf worden aangeroepen
                out.print("[Settings]\r\nsFile=" + filePath + "\r\nsClass="
                        + VirusGUI.classComboBox.getSelectedItem().toString()
                        + "\r\nsHost1=" + VirusGUI.hostComboBox1.getSelectedItem().toString()
                        + "\r\nsHost2=" + VirusGUI.hostComboBox2.getSelectedItem().toString()
                        + "\r\n\r\n[VirusList1]\r\n");
                virusList1.forEach((v) -> {
                    out.print(v.getId() + "\r\n");
                });
                out.print("\r\n[VirusList2]\r\n");
                virusList2.forEach((v) -> {
                    out.print(v.getId() + "\r\n");
                });
                out.print("\r\n[Overlap]\r\n");
                overlapList.forEach((v) -> {
                    out.print(v.getId() + "\r\n");
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
