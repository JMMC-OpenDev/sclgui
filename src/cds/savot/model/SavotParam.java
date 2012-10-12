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
 * Param element
 * </p>
 * 
 * @author Andre Schaaff
 * 
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotParam extends MarkupComment implements IDSupport, NameSupport {

    // ID attribute
    private String id = null;
    // unit attribute
    private String unit = null;
    // datatype attribute
    private String datatype = null;
    // precision attribute
    private String precision = null;
    // width attribute
    private String width = null;
    // xtype attribute @since 1.2
    private String xtype = null;
    // ref attribute
    private String ref = null;
    // name attribute
    private String name = null;
    // ucd attribute
    private String ucd = null;
    // utype attribute
    private String utype = null;
    // arraysize attribute
    private String arraysize = null;
    // value attribute
    private String value = null;
    // DESCRIPTION element
    private String description = null;
    // VALUES element
    private SavotValues values = null;
    // LINK element
    private LinkSet links = null;

    /**
     * Constructor
     */
    public SavotParam() {
    }

    /**
     * Set ID attribute
     * 
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get id attribute
     * 
     * @return String
     */
    public String getId() {
        return str(id);
    }

    /**
     * Set unit attribute
     * 
     * @param unit
     */
    public void setUnit(final String unit) {
        this.unit = unit;
    }

    /**
     * Get unit attribute
     * 
     * @return String
     */
    public String getUnit() {
        return str(unit);
    }

    /**
     * Set datatype attribute
     * 
     * @param datatype
     *            (boolean, bit, unsignedByte, short, int, long, char,
     *            unicodeChar, float, double, floatComplex, doubleComplex)
     */
    public void setDataType(final String datatype) {
        this.datatype = datatype;
    }

    /**
     * Get datatype attribute
     * 
     * @return String
     */
    public String getDataType() {
        return str(datatype);
    }

    /**
     * Set precision attribute
     * 
     * @param precision
     *            ([EF]?[1-0][0-9]*)
     */
    public void setPrecision(final String precision) {
        this.precision = precision;
    }

    /**
     * Get precision attribute
     * 
     * @return String
     */
    public String getPrecision() {
        return str(precision);
    }

    /**
     * Set width attribute
     * 
     * @param width
     */
    public void setWidth(final String width) {
        this.width = width;
    }

    /**
     * Set width attribute
     * 
     * @param width
     */
    public void setWidthValue(final int width) {
        this.width = toStr(width);
    }

    /**
     * Get width attribute
     * 
     * @return String
     */
    public String getWidth() {
        return str(width);
    }

    /**
     * Get width attribute
     * 
     * @return String
     */
    public int getWidthValue() {
        return integer(width);
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
     * @param ref
     */
    public void setRef(final String ref) {
        this.ref = ref;
    }

    /**
     * Get ref attribute
     * 
     * @return String
     */
    public String getRef() {
        return str(ref);
    }

    /**
     * Set name attribute
     * 
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get name attribute
     * 
     * @return String
     */
    public String getName() {
        return str(name);
    }

    /**
     * Set ucd attribute
     * 
     * @param ucd
     *            ([A-Za-z0-9_.,-]*)
     */
    public void setUcd(final String ucd) {
        this.ucd = ucd;
    }

    /**
     * Get ucd attribute
     * 
     * @return String
     */
    public String getUcd() {
        return str(ucd);
    }

    /**
     * Set utype attribute
     * 
     * @param utype
     */
    public void setUtype(final String utype) {
        this.utype = utype;
    }

    /**
     * Get utype attribute
     * 
     * @return String
     */
    public String getUtype() {
        return str(utype);
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
     * Set arraysize attribute
     * 
     * @param arraysize
     *            (([0-9]+x)*[0-9]*[*]?(s\W)?)
     */
    public void setArraySize(final String arraysize) {
        this.arraysize = arraysize;
    }

    /**
     * Get arraysize attribute
     * 
     * @return String
     */
    public String getArraySize() {
        return str(arraysize);
    }

    /**
     * Set DESCRIPTION content
     * 
     * @param description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get DESCRIPTION content
     * 
     * @return String
     */
    public String getDescription() {
        return str(description);
    }

    /**
     * Set VALUES element
     * 
     * @param values
     */
    public void setValues(final SavotValues values) {
        this.values = values;
    }

    /**
     * Get VALUES element
     * 
     * @return a SavotValues object
     */
    public SavotValues getValues() {
        return values;
    }

    /**
     * Get Link set reference
     * 
     * @return a set of LINK elements
     */
    public LinkSet getLinks() {
        if (links == null) {
            links = new LinkSet();
        }
        return links;
    }

    /**
     * Set Link set reference
     * 
     * @param links
     */
    public void setLinks(final LinkSet links) {
        this.links = links;
    }
}