/*
 * Virus App
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
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
     * @param id
     * @param soort 
     * @param hostId
     * @param classificatie
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
     * @return virus ID
     */
    public int getId() {
        return id;
    }

    /**
     * Virus ID setter
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Soort getter
     *
     * @return virussoort
     */
    public String getSoort() {
        return soort;
    }

    /**
     * Soort setter
     *
     * @param soort
     */
    public void setSoort(String soort) {
        this.soort = soort;
    }

    /**
     * Classificatie getter
     *
     * @return virusclassificatie
     */
    public String getClassificatie() {
        return classificatie;
    }

    /**
     * Classificatie setter
     *
     * @param classificatie
     */
    public void setClassificatie(String classificatie) {
        this.classificatie = classificatie;
    }
    
    /**
     * Hostlist setter
     * 
     * @return hostlist
     */
    public ArrayList<Integer> getHostList() {
        return hostList;
    }

    /**
     * Hostlist getter
     * 
     * @param hostList
     */
    public void setHostList(ArrayList<Integer> hostList) {
        this.hostList = hostList;
    }
    
    /**
     * Voegt host ID toe
     *
     * @param hostId
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

    @Override
    public String toString() {
        return Integer.toString(id) + " " + soort;
    }
}
