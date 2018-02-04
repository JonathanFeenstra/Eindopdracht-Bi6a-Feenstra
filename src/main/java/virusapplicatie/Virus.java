/*
 * Virus App
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 * Bekende bugs: Bij het selecteren van 1 (root) als host en "Other" als class,
 * worden vaak alle virussen van 1 (root) getoond terwijl sommige van een andere 
 * class zijn.
 */
package virusapplicatie;

import java.util.ArrayList;

/**
 * Class voor het opslaan van informatie per virus.
 *
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class Virus implements Comparable {

    private int id;
    private String soort, classificatie;
    private ArrayList<Integer> hostList;

    /**
     * Integer die de te gebruiken sorteringsmethode aangeeft, 0 = id van laag
     * naar hoog, 1 = classificatie alfabetisch, 2 = aantal hosts
     */
    public static int sortMethod = 0;

    /**
     * Constructor
     *
     * @param id de virus ID
     * @param soort de virussoortnaam
     * @param hostId de host ID
     * @param classificatie de virusclassifiecatie zoals op Virus-Host DB
     */
    public Virus(int id, String soort, int hostId, String classificatie) {
        this.id = id;
        this.soort = soort;
        this.hostList = new ArrayList<>();
        this.hostList.add(hostId);
        this.classificatie = classificatie;
    }

    /**
     * Virus ID getter
     *
     * @return de virus ID
     */
    public int getId() {
        return id;
    }

    /**
     * Virus ID setter
     *
     * @param id de virus ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Soort getter
     *
     * @return de virussoort
     */
    public String getSoort() {
        return soort;
    }

    /**
     * Soort setter
     *
     * @param soort de virussoort
     */
    public void setSoort(String soort) {
        this.soort = soort;
    }

    /**
     * Classificatie getter
     *
     * @return de virusclassificatie
     */
    public String getClassificatie() {
        return classificatie;
    }

    /**
     * Classificatie setter
     *
     * @param classificatie de virusclassificatie
     */
    public void setClassificatie(String classificatie) {
        this.classificatie = classificatie;
    }

    /**
     * Hostlist getter
     *
     * @return de hostlijst
     */
    public ArrayList<Integer> getHostList() {
        return hostList;
    }

    /**
     * Hostlist setter
     *
     * @param hostList de hostlijst
     */
    public void setHostList(ArrayList<Integer> hostList) {
        this.hostList = hostList;
    }

    /**
     * Voegt host ID toe
     *
     * @param hostId de host ID
     */
    public void addHost(int hostId) {
        this.hostList.add(hostId);
    }

    @Override
    public int compareTo(Object o) {
        Virus v = (Virus) o;
        switch (sortMethod) {
            case 0:
                return this.id - v.id;
            case 1:
                return this.classificatie.compareTo(v.classificatie);
            case 2:
                return this.hostList.size() - v.hostList.size();
            default:
                return 0;
        }
    }

    // Deze overrides zorgen dat retainAll() alle virussen met dezelfde id's behoudt.
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Virus) {
            Virus v = (Virus) obj;
            return v.id == this.id;
        }
        return false;
    }
}
