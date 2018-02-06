/*
 * Virus App - © Jonathan Feenstra 2018.
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;

/**
 * Bevat de logica voor het opslaan van data, het sorteren en filteren van de
 * viruslijsten, het openen van (hyper)links en het kopiëren van viruslijsten.
 *
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class VirusLogica {

    static HashMap<String, HashMap<String, HashSet<Virus>>> hostToVirusMap;
    static HashMap<Integer, Virus> virusIdToVirusMap;
    static ArrayList<Virus> virusList1, virusList2, overlapList;
    static final String[] CLASSIFICATIES = {"Any", "dsRNA", "dsDNA", "ssRNA",
        "ssDNA", "Retrovirus", "Satelite virus and Virophage", "Viroid", "Other"};
    private static final String LINK_UNSUPPORTED_MSG = "Het openen van links wordt op uw computer niet ondersteund.";

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
            virusIdToVirusMap = new HashMap<>(); // Maakt het mogelijk om hostlists per Virus op te slaan zonder extra iteratie
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
                    virusPerClass.add(currVirus);
                    classToVirusMap.put(CLASSIFICATIES[0], virusPerClass);
                    if (!virusIdToVirusMap.containsKey(virusId)) {
                        virusIdToVirusMap.put(virusId, currVirus);
                    } else {
                        virusIdToVirusMap.get(virusId).addHost(hostId);
                    }
                    if (!classToVirusMap.containsKey(virusClass)) {
                        classToVirusMap.put(virusClass, (HashSet) virusPerClass.clone());
                    } else {
                        classToVirusMap.get(virusClass).add(virusIdToVirusMap.get(virusId));
                    }
                    String key = row[7] + " (" + row[8] + ")";
                    if (!hostToVirusMap.containsKey(key)) {
                        hostToVirusMap.put(key, (HashMap) classToVirusMap.clone());
                    } else if (hostToVirusMap.get(key).containsKey(virusClass)) {
                        hostToVirusMap.get(key).get(virusClass).add(virusIdToVirusMap.get(virusId));
                        hostToVirusMap.get(key).get(CLASSIFICATIES[0]).add(virusIdToVirusMap.get(virusId));
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
        } else if (virusLineage.contains("Satellite") || virusLineage.contains("virophage")) {
            return CLASSIFICATIES[6];
        } else if (virusLineage.startsWith("Viroid")) {
            return CLASSIFICATIES[7];
        }
        return CLASSIFICATIES[8];
    }

    /**
     * Sorteert de viruslijsten.
     */
    public static void sortLists() {
        if (virusList1 != null) {
            Collections.sort(virusList1);
        }
        if (virusList2 != null) {
            Collections.sort(virusList2);
        }
        if (overlapList != null) {
            Collections.sort(overlapList);
        }
    }

    /**
     * Bezoekt website bij het klikken op een hyperlink.
     *
     * @param evt de HyperlinkEvent van de aangeklikte hyperlink
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
     * Overloading: open Javadoc of Virus-Host DB website afhankelijk van welk
     * menu item is aangeklikt.
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
}
