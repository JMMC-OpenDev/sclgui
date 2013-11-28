package cds.savot.model;

//
//Copyright 2002-2013 - Universite de Strasbourg / Centre National de la
//Recherche Scientifique
//------
//
//SAVOT - Simple Access to VOTable - Parser
//
//Author:  Andre Schaaff
//Address: Centre de Donnees astronomiques de Strasbourg
//         11 rue de l'Universite
//         67000 STRASBOURG
//         FRANCE
//Contributors: Laurent Bourges (code improvement, ideas, tests, etc.), Gregory Mantelet (binary capabilities)
//Email:   cds-question@astro.unistra.fr
//
//-------
//
//In accordance with the international conventions about intellectual
//property rights this software and associated documentation files
//(the "Software") is protected. The rightholder authorizes :
//the reproduction and representation as a private copy or for educational
//and research purposes outside any lucrative use,
//subject to the following conditions:
//
//The above copyright notice shall be included.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON INFRINGEMENT,
//LOSS OF DATA, LOSS OF PROFIT, LOSS OF BARGAIN OR IMPOSSIBILITY
//TO USE SUCH SOFWARE. IN NO EVENT SHALL THE RIGHTHOLDER BE LIABLE
//FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
//TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
//THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
//For any other exploitation contact the rightholder.
//
//                     -----------
//
//Conformement aux conventions internationales relatives aux droits de
//propriete intellectuelle ce logiciel et sa documentation sont proteges.
//Le titulaire des droits autorise :
//la reproduction et la representation a titre de copie privee ou des fins
//d'enseignement et de recherche et en dehors de toute utilisation lucrative.
//Cette autorisation est faite sous les conditions suivantes :
//
//La mention du copyright portee ci-dessus devra etre clairement indiquee.
//
//LE LOGICIEL EST LIVRE "EN L'ETAT", SANS GARANTIE D'AUCUNE SORTE.
//LE TITULAIRE DES DROITS NE SAURAIT, EN AUCUN CAS ETRE TENU CONTRACTUELLEMENT
//OU DELICTUELLEMENT POUR RESPONSABLE DES DOMMAGES DIRECTS OU INDIRECTS
//(Y COMPRIS ET A TITRE PUREMENT ILLUSTRATIF ET NON LIMITATIF,
//LA PRIVATION DE JOUISSANCE DU LOGICIEL, LA PERTE DE DONNEES,
//LE MANQUE A GAGNER OU AUGMENTATION DE COUTS ET DEPENSES, LES PERTES
//D'EXPLOITATION,LES PERTES DE MARCHES OU TOUTES ACTIONS EN CONTREFACON)
//POUVANT RESULTER DE L'UTILISATION, DE LA MAUVAISE UTILISATION
//OU DE L'IMPOSSIBILITE D'UTILISER LE LOGICIEL, ALORS MEME
//QU'IL AURAIT ETE AVISE DE LA POSSIBILITE DE SURVENANCE DE TELS DOMMAGES.
//
//Pour toute autre utilisation contactez le titulaire des droits.
/**
 * <p>
 * Info element
 * </p>
 *
 * @author Andre Schaaff
 * @version 4.0
 */
public final class SavotInfo extends MarkupComment implements IDSupport, NameSupport {

    // id attribute
    private String id = null;

    // name attribute
    private String name = null;

    // value attribute
    private String value = null;

    // INFO element content
    private String content = null;

    // xtype attribute @since 1.2
    private String xtype = null;

    // ref attribute @since 1.2
    private String ref = null;

    // unit attribute @since 1.2
    private String unit = null;

    // ucd attribute @since 1.2
    private String ucd = null;

    // utype attribute @since 1.2
    private String utype = null;

    // DESCRIPTION element - since VOTable 1.2 (not in the standard)
    private String description = null;

    // VALUES element - since VOTable 1.2 (not in the standard)
    private SavotValues values = null;

    // LINK elements - since VOTable 1.2 (not in the standard)
    private LinkSet links = null;

    /**
     * Constructor
     */
    public SavotInfo() {
    }

    /**
     * Set ID attribute
     *
     * @param id
     */
    @Override
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get ID attribute
     *
     * @return String
     */
    @Override
    public String getId() {
        return str(id);
    }

    /**
     * Set name attribute
     *
     * @param name
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get name attribute
     *
     * @return String
     */
    @Override
    public String getName() {
        return str(name);
    }

    /**
     * Set value attribute
     *
     * @param value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Get value attribute
     *
     * @return String
     */
    public String getValue() {
        return str(value);
    }

    /**
     * Set xtype attribute
     *
     * @since VOTable 1.2
     * @param xtype
     */
    public void setXtype(final String xtype) {
        this.xtype = xtype;
    }

    /**
     * Get xtype attribute
     *
     * @since VOTable 1.2
     * @return String
     */
    public String getXtype() {
        return str(xtype);
    }

    /**
     * Set ref attribute
     *
     * @since VOTable 1.2
     * @param ref
     */
    public void setRef(final String ref) {
        this.ref = ref;
    }

    /**
     * Get ref attribute
     *
     * @since VOTable 1.2
     * @return String
     */
    public String getRef() {
        return str(ref);
    }

    /**
     * Set unit attribute
     *
     * @since VOTable 1.2
     * @param unit
     */
    public void setUnit(final String unit) {
        this.unit = unit;
    }

    /**
     * Get unit attribute
     *
     * @since VOTable 1.2
     * @return String
     */
    public String getUnit() {
        return str(unit);
    }

    /**
     * Set ucd attribute
     *
     * @since VOTable 1.2
     * @param ucd
     */
    public void setUcd(final String ucd) {
        this.ucd = ucd;
    }

    /**
     * Get ucd attribute
     *
     * @since VOTable 1.2
     * @return String
     */
    public String getUcd() {
        return str(ucd);
    }

    /**
     * Set utype attribute
     *
     * @since VOTable 1.2
     * @param utype
     */
    public void setUtype(final String utype) {
        this.utype = utype;
    }

    /**
     * Get utype attribute
     *
     * @since VOTable 1.2
     * @return String
     */
    public String getUtype() {
        return str(utype);
    }

    /**
     * Set element content
     *
     * @param content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Get element content
     *
     * @return a String
     */
    public String getContent() {
        return str(content);
    }

    /**
     * Set DESCRIPTION content
     *
     * @since VOTable 1.2 (not in the standard)
     * @param description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get DESCRIPTION content
     *
     * @since VOTable 1.2 (not in the standard)
     * @return String
     */
    public String getDescription() {
        return str(description);
    }

    /**
     * Set the VALUES element
     *
     * @since VOTable 1.2 (not in the standard)
     * @param values
     */
    public void setValues(final SavotValues values) {
        this.values = values;
    }

    /**
     * Get the VALUES element
     *
     * @since VOTable 1.2 (not in the standard)
     * @return SavotValues
     */
    public SavotValues getValues() {
        return values;
    }

    /**
     * Get LINK elements set reference
     *
     * @since VOTable 1.2 (not in the standard)
     * @return LinkSet
     */
    public LinkSet getLinks() {
        if (links == null) {
            links = new LinkSet();
        }
        return links;
    }

    /**
     * Set LINK elements set reference
     *
     * @since VOTable 1.2 (not in the standard)
     * @param links
     */
    public void setLinks(final LinkSet links) {
        this.links = links;
    }
}
