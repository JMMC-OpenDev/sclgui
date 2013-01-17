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
 * Table element
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotTable extends MarkupComment implements IDSupport, NameSupport {

    // id attribute
    private String id = null;
    // name attribute
    private String name = null;
    // ucd attribute
    private String ucd = null;
    // utype attribute
    private String utype = null;
    // ref attribute
    private String ref = null;
    // nrows attribute
    private String nrows = null;
    // DESCRIPTION element
    private String description = null;
    // FIELD element
    private FieldSet fields = null;
    // PARAM element
    private ParamSet params = null;
    // GROUP element - since VOTable 1.1
    private GroupSet groups = null;
    // LINK element
    private LinkSet links = null;
    // DATA element
    private SavotData data = null;
    // INFO element at the end - since VOTable 1.2
    private InfoSet infosAtEnd = null;

    /**
     * Constructor
     */
    public SavotTable() {
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
     * Get ID attribute
     * 
     * @return a String
     */
    public String getId() {
        return str(id);
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
     * @return a String
     */
    public String getName() {
        return str(name);
    }

    /**
     * Set ucd attribute
     * 
     * @param ucd
     */
    public void setUcd(final String ucd) {
        this.ucd = ucd;
    }

    /**
     * Get ucd attribute
     * 
     * @return a String
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
     * @return a String
     */
    public String getUtype() {
        return str(utype);
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
     * @return a String
     */
    public String getRef() {
        return str(ref);
    }

    /**
     * Set nrows attribute
     * 
     * @param nrows
     */
    public void setNrows(final String nrows) {
        this.nrows = nrows;
    }

    /**
     * Set nrows attribute
     * 
     * @param nrows
     */
    public void setNrowsValue(final int nrows) {
        this.nrows = toStr(nrows);
    }

    /**
     * Get nrows attribute
     * 
     * @return a String
     */
    public String getNrows() {
        return str(nrows);
    }

    /**
     * Get nrows attribute
     * 
     * @return an int
     */
    public int getNrowsValue() {
        return integer(nrows);
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
     * @return a String
     */
    public String getDescription() {
        return str(description);
    }

    /**
     * Get FIELD element set reference
     * 
     * @return FieldSet
     */
    public FieldSet getFields() {
        if (fields == null) {
            fields = new FieldSet();
        }
        return fields;
    }

    /**
     * Set FIELD element set reference
     * 
     * @param fields
     */
    public void setFields(final FieldSet fields) {
        this.fields = fields;
    }

    /**
     * Get PARAM element set reference
     * 
     * @return ParamSet
     */
    public ParamSet getParams() {
        if (params == null) {
            params = new ParamSet();
        }
        return params;
    }

    /**
     * Set PARAM element set reference
     * 
     * @param params
     */
    public void setParams(final ParamSet params) {
        this.params = params;
    }

    /**
     * Get GROUP element set reference
     * 
     * @since VOTable 1.1
     * @return GroupSet
     */
    public GroupSet getGroups() {
        if (groups == null) {
            groups = new GroupSet();
        }
        return groups;
    }

    /**
     * Set GROUP element set reference
     * 
     * @since VOTable 1.1
     * @param groups
     */
    public void setGroups(final GroupSet groups) {
        this.groups = groups;
    }

    /**
     * Get LINK element set reference
     * 
     * @return LinkSet
     */
    public LinkSet getLinks() {
        if (links == null) {
            links = new LinkSet();
        }
        return links;
    }

    /**
     * Set LINK element set reference
     * 
     * @param links
     */
    public void setLinks(final LinkSet links) {
        this.links = links;
    }

    /**
     * Set DATA element
     * 
     * @param data
     */
    public void setData(final SavotData data) {
        this.data = data;
    }

    /**
     * Get DATA element
     * 
     * @return SavotData
     */
    public SavotData getData() {
        return data;
    }

    /**
     * Set the InfosAtEnd elements
     * 
     * @param infosAtEnd
     * @since VOTable 1.2
     */
    public void setInfosAtEnd(final InfoSet infosAtEnd) {
        this.infosAtEnd = infosAtEnd;
    }

    /**
     * Get the InfosAtEnd elements
     * 
     * @return an InfoSet object
     * @since VOTable 1.2
     */
    public InfoSet getInfosAtEnd() {
        if (infosAtEnd == null) {
            infosAtEnd = new InfoSet();
        }
        return infosAtEnd;
    }
}