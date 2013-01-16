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
 * Values element
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotValues extends MarkupComment implements IDSupport {

    // ID attribute
    private String id = null;
    // type attribute
    private String type = "legal";
    // null content
    private String nul = null;
    // ref content
    private String ref = null;
    // invalid content - deprecated since VOTable 1.1
    private String invalid = null;
    // MIN element
    private SavotMin min = null;
    // MAX element
    private SavotMax max = null;
    // OPTION element
    private OptionSet options = null;

    /**
     * Constructor
     */
    public SavotValues() {
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
     * Set the type attribute
     * 
     * @param type
     *            String (legal, actual)
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Get the type attribute
     * 
     * @return a String
     */
    public String getType() {
        return str(type);
    }

    /**
     * Set the null attribute
     * 
     * @param nul
     *            String
     */
    public void setNull(final String nul) {
        this.nul = nul;
    }

    /**
     * Get the null attribute
     * 
     * @return a String
     */
    public String getNull() {
        return str(nul);
    }

    /**
     * Set the ref attribute
     * 
     * @param ref
     *            ref
     */
    public void setRef(final String ref) {
        this.ref = ref;
    }

    /**
     * Get the ref attribute
     * 
     * @return a String
     */
    public String getRef() {
        return str(ref);
    }

    /**
     * Set the invalid attribute deprecated since VOTable 1.1
     * 
     * @param invalid
     *            String
     */
    public void setInvalid(final String invalid) {
        this.invalid = invalid;
    }

    /**
     * Get the invalid attribute deprecated since VOTable 1.1
     * 
     * @return a String
     */
    public String getInvalid() {
        return str(invalid);
    }

    /**
     * Set MIN element
     * 
     * @param min
     */
    public void setMin(final SavotMin min) {
        this.min = min;
    }

    /**
     * Get MIN element
     * 
     * @return a SavotMin object
     */
    public SavotMin getMin() {
        return min;
    }

    /**
     * Set MAX element
     * 
     * @param max
     */
    public void setMax(final SavotMax max) {
        this.max = max;
    }

    /**
     * Get MAX element
     * 
     * @return a SavotMax object
     */
    public SavotMax getMax() {
        return max;
    }

    /**
     * Get OPTION element set reference
     * 
     * @return OptionSet object
     */
    public OptionSet getOptions() {
        if (options == null) {
            options = new OptionSet();
        }
        return options;
    }

    /**
     * Set OPTION element set reference
     * 
     * @param options
     */
    public void setOptions(final OptionSet options) {
        this.options = options;
    }
}