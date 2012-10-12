//
// Copyright 2002-2012 - Universite de Strasbourg / Centre National de la
// Recherche Scientifique
// ------
//
// SAVOT Data Model
//
// Author:  Andre Schaaff
// Address: Centre de Donnees astronomiques de Strasbourg
//          11 rue de l'Universite
//          67000 STRASBOURG
//          FRANCE
// Email:   question@simbad.u-strasbg.fr
//
// -------
//
// In accordance with the international conventions about intellectual
// property rights this software and associated documentation files
// (the "Software") is protected. The rightholder authorizes :
// the reproduction and representation as a private copy or for educational
// and research purposes outside any lucrative use,
// subject to the following conditions:
//
// The above copyright notice shall be included.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON INFRINGEMENT,
// LOSS OF DATA, LOSS OF PROFIT, LOSS OF BARGAIN OR IMPOSSIBILITY
// TO USE SUCH SOFWARE. IN NO EVENT SHALL THE RIGHTHOLDER BE LIABLE
// FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// For any other exploitation contact the rightholder.
//
//                        -----------
//
// Conformement aux conventions internationales relatives aux droits de
// propriete intellectuelle ce logiciel et sa documentation sont proteges.
// Le titulaire des droits autorise :
// la reproduction et la representation a titre de copie privee ou des fins
// d'enseignement et de recherche et en dehors de toute utilisation lucrative.
// Cette autorisation est faite sous les conditions suivantes :
//
// La mention du copyright portee ci-dessus devra etre clairement indiquee.
//
// LE LOGICIEL EST LIVRE "EN L'ETAT", SANS GARANTIE D'AUCUNE SORTE.
// LE TITULAIRE DES DROITS NE SAURAIT, EN AUCUN CAS ETRE TENU CONTRACTUELLEMENT
// OU DELICTUELLEMENT POUR RESPONSABLE DES DOMMAGES DIRECTS OU INDIRECTS
// (Y COMPRIS ET A TITRE PUREMENT ILLUSTRATIF ET NON LIMITATIF,
// LA PRIVATION DE JOUISSANCE DU LOGICIEL, LA PERTE DE DONNEES,
// LE MANQUE A GAGNER OU AUGMENTATION DE COUTS ET DEPENSES, LES PERTES
// D'EXPLOITATION,LES PERTES DE MARCHES OU TOUTES ACTIONS EN CONTREFACON)
// POUVANT RESULTER DE L'UTILISATION, DE LA MAUVAISE UTILISATION
// OU DE L'IMPOSSIBILITE D'UTILISER LE LOGICIEL, ALORS MEME
// QU'IL AURAIT ETE AVISE DE LA POSSIBILITE DE SURVENANCE DE TELS DOMMAGES.
//
// Pour toute autre utilisation contactez le titulaire des droits.
//
package cds.savot.model;

/**
 * <p>
 * Row element
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotTR extends SavotBase /* MarkupComment */ implements IDSupport {

    // ID attribute - extension since 1.2
    private String id = null;
    // TR element set
    private TDSet TDs = null;
    private int lineInXMLFile = 0;

    /**
     * Constructor
     */
    public SavotTR() {
    }

    /**
     * Create a TR element from a Separated Value String
     * 
     * @param svline
     *            String, line with separated values
     * @param sv
     *            char, separated value
     */
    public void SVtoTR(final String svline, final char sv) {
        String line = svline;
        int index = 0;
        String token;
        TDs = new TDSet();
        // cut sv following the separator

        // tabulation
        do {
            if ((index = line.indexOf(sv)) >= 0) {
                token = line.substring(0, index);
                line = line.substring(index + 1);
            } else {
                // last element
                token = line;
            }
            SavotTD td = new SavotTD();
            td.setContent(token);
            TDs.addItem(td);
        } while (index >= 0);
    }

    /**
     * Set the id attribute
     * 
     * @param id
     *            String
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get the id attribute
     * 
     * @return a String
     */
    public String getId() {
        return str(id);
    }

    /**
     * Get the TD set (same as getTDSet) TDSet
     * 
     * @return TDSet
     */
    public TDSet getTDs() {
        if (TDs == null) {
            TDs = new TDSet();
        }
        return TDs;
    }

    /**
     * Get the TD set (same as getTDs)
     * TDSet
     * @param capacity minimal capacity to provide
     * @return TDSet
     */
    public TDSet getTDSet(final int capacity) {
        if (TDs == null) {
            TDs = new TDSet();
            TDs.ensureCapacity(capacity);
        }
        return TDs;
    }

    /**
     * Get the TD set (same as getTDs) TDSet
     * @see getTDSet(int)
     * 
     * @return TDSet
     */
    public TDSet getTDSet() {
        if (TDs == null) {
            TDs = new TDSet();
        }
        return TDs;
    }

    /**
     * Set the TD set (same as setTDSet) TDSet
     * 
     * @param TDs
     */
    public void setTDs(TDSet TDs) {
        this.TDs = TDs;
    }

    /**
     * Set the TD set (same as setTDs) TDSet
     * 
     * @param TDs
     */
    public void setTDSet(TDSet TDs) {
        this.TDs = TDs;
    }

    /**
     * Get the corresponding line in the XML file or flow
     * 
     * @return lineInXMLFile
     */
    public long getLineInXMLFile() {
        return lineInXMLFile;
    }

    /**
     * Set the corresponding line in the XML file or flow during the parsing
     * 
     * @param lineInXMLFile
     */
    public void setLineInXMLFile(final int lineInXMLFile) {
        this.lineInXMLFile = lineInXMLFile;
    }

    /**
     * Clear this TR instance to recycle it
     */
    public void clear() {
        if (TDs != null) {
            TDs.removeAllItems(); // recycle TDSet (same capacity)
        }
        id = null;
        lineInXMLFile = 0;
/*
        setAbove(null);
        setBelow(null);
 */ 
    }
}