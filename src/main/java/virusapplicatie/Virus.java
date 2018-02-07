/*
 * Virus App - © Jonathan Feenstra 2018.
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 */
package virusapplicatie;

import java.util.HashSet;

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
    private HashSet<Integer> hostSet;

    /**
     * Integer die de te gebruiken sorteringsmethode aangeeft, 0 = id, 1 =
     * classificatie alfabetisch, 2 = aantal hosts.
     */
    public static int sortMethod = 0;
    /**
     * Boolean die aangeeft of van hoog naar laag (true) of van laag naar hoog
     * (false) moet worden gesorteerd.
     */
    public static boolean sortOrder = true;

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
        this.hostSet = new HashSet<>();
        this.hostSet.add(hostId);
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
     * Hostset getter
     *
     * @return de hostlijst
     */
    public HashSet<Integer> getHostSet() {
        return hostSet;
    }

    /**
     * Hostset setter
     *
     * @param hostSet de hostlijst
     */
    public void setHostSet(HashSet<Integer> hostSet) {
        this.hostSet = hostSet;
    }

    /**
     * Voegt host ID toe
     *
     * @param hostId de host ID
     */
    public void addHost(int hostId) {
        this.hostSet.add(hostId);
    }

    @Override
    public int compareTo(Object o) {
        Virus v = (Virus) o;
        if (!sortOrder) {
            switch (sortMethod) {
                case 0:
                    return this.id - v.id;
                case 1:
                    return this.classificatie.compareTo(v.classificatie);
                case 2:
                    return this.hostSet.size() - v.hostSet.size();
                default:
                    return 0;
            }
        } else {
            switch (sortMethod) {
                case 0:
                    return v.id - this.id;
                case 1:
                    return v.classificatie.compareTo(this.classificatie);
                case 2:
                    return v.hostSet.size() - this.hostSet.size();
                default:
                    return 0;
            }
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
