/*
 * Virus App - © Jonathan Feenstra 2018.
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 */
package virusapplicatie;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Bevat methoden voor het openen, inladen en opslaan van bestanden.
 *
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class VirusFileHandler {

    private static String filePath;

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
            try (final PrintWriter out = new PrintWriter(fileLocation)) {
                out.print("[Settings]\r\nsFile=" + filePath + "\r\nsClass="
                        + VirusGUI.classComboBox.getSelectedItem().toString()
                        + "\r\nsHost1=" + VirusGUI.hostComboBox1.getSelectedItem().toString()
                        + "\r\nsHost2=" + VirusGUI.hostComboBox2.getSelectedItem().toString()
                        + "\r\n\r\n[VirusList1]\r\n");
                VirusLogica.virusList1.forEach((Virus v) -> {
                    out.print(v.getId() + "\r\n");
                });
                out.print("\r\n[VirusList2]\r\n");
                VirusLogica.virusList2.forEach((Virus v) -> {
                    out.print(v.getId() + "\r\n");
                });
                out.print("\r\n[Overlap]\r\n");
                VirusLogica.overlapList.forEach((Virus v) -> {
                    out.print(v.getId() + "\r\n");
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Er is een onbekende fout opgetreden.\n"
                        + "Neem contact op met Jonathan Feenstra.\n"
                        + ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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
     * Procedure die steeds moet worden ondergaan als een bestand wordt geopend.
     * Thijs Weenink heeft mij geholpen met het inladen van URL's.
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
                } catch (MalformedURLException ex) { // Als invoer geen URL is wordt het als filepath geïnterpreteerd.
                    selectedFileReader = new FileReader(VirusGUI.searchTextField.getText());
                }
            }
            if (selectedFileReader != null) {
                VirusLogica.saveHostAndVirusData(selectedFileReader);
                if (VirusLogica.hostToClassToVirusMap != null) {
                    String[] hostKeys = VirusLogica.hostToClassToVirusMap.keySet().toArray(new String[VirusLogica.hostToClassToVirusMap.size()]);
                    Arrays.sort(hostKeys);
                    VirusGUI.hostComboBox1.setModel(new DefaultComboBoxModel(hostKeys));
                    VirusGUI.hostComboBox2.setModel(new DefaultComboBoxModel(hostKeys));
                    VirusGUI.hostComboBox1.setEnabled(true);
                    VirusGUI.hostComboBox2.setEnabled(true);
                    VirusGUI.hostComboBox1.setToolTipText((String) VirusGUI.hostComboBox1.getSelectedItem());
                    VirusGUI.hostComboBox2.setToolTipText((String) VirusGUI.hostComboBox2.getSelectedItem());
                    VirusUpdater.updateLists();
                    VirusUpdater.updateAllEditorPanes();
                    VirusGUI.saveMenuItem.setEnabled(true);
                    VirusGUI.copyMenu.setEnabled(true);
                } else {
                    filePath = "";
                    VirusGUI.searchTextField.setText("Search");
                }
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Het geselecteerde bestand is niet gevonden.\n"
                    + ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Er is een fout opgetreden bij het openen van het bestand.\n"
                    + ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }

}
