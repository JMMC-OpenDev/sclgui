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
 * Resource element
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotResource extends MarkupComment implements IDSupport, NameSupport {

    // name attribute
    private String name = null;
    // id attribute
    private String id = null;
    // type attribute (results, meta)
    private String type = "results"; // default
    // utype attribute
    private String utype = null;
    // DESCRIPTION element
    private String description = null;
    // COOSYS element set - deprecated since 1.2
    private CoosysSet coosys = null;
    // GROUP element set - since VOTable 1.2
    private GroupSet groups = null;
    // PARAM element set
    private ParamSet params = null;
    // INFO element set
    private InfoSet infos = null;
    // LINK element set
    private LinkSet links = null;
    // TABLE element set
    private TableSet tables = null;
    // RESOURCE element set (recursive usage)
    private ResourceSet resources = null;
    // INFO (at End) element set - since VOTable 1.2
    private InfoSet infosAtEnd = null;

    /**
     * Constructor
     */
    public SavotResource() {
    }

    /**
     * init a SavotResource object
     */
    public void init() {
        name = null;
        id = null;
        type = null;
        utype = null;
        description = null;
        coosys = null;
        groups = null;
        params = null;
        infos = null;
        links = null;
        tables = null;
        resources = null;
        infosAtEnd = null;
    }

    /**
     * Set the description
     * 
     * @param description
     *            String
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get the description
     * 
     * @return a String
     */
    public String getDescription() {
        return str(description);
    }

    /**
     * Set the Coosys elements
     * 
     * @deprecated since VOTable 1.2
     * @param coosys
     */
    public void setCoosys(final CoosysSet coosys) {
        this.coosys = coosys;
    }

    /**
     * Get the Coosys elements
     * 
     * @deprecated since VOTable 1.2
     * @return a CoosysSet object
     */
    public CoosysSet getCoosys() {
        if (coosys == null) {
            coosys = new CoosysSet();
        }
        return coosys;
    }

    /**
     * Set GROUP element set reference
     * 
     * @since VOTable 1.2
     * @param groups
     */
    public void setGroups(final GroupSet groups) {
        this.groups = groups;
    }

    /**
     * Get GROUP element set reference
     * 
     * @since VOTable 1.2
     * @return GroupSet
     */
    public GroupSet getGroups() {
        if (groups == null) {
            groups = new GroupSet();
        }
        return groups;
    }

    /**
     * Set the Infos elements
     * 
     * @param infos
     */
    public void setInfos(final InfoSet infos) {
        this.infos = infos;
    }

    /**
     * Get the Infos elements
     * 
     * @return a InfoSet object
     */
    public InfoSet getInfos() {
        if (infos == null) {
            infos = new InfoSet();
        }
        return infos;
    }

    /**
     * Set the Param elements
     * 
     * @param params
     */
    public void setParams(final ParamSet params) {
        this.params = params;
    }

    /**
     * Get the Param elements
     * 
     * @return a ParamSet object
     */
    public ParamSet getParams() {
        if (params == null) {
            params = new ParamSet();
        }
        return params;
    }

    /**
     * Set the Link elements
     * 
     * @param links
     */
    public void setLinks(final LinkSet links) {
        this.links = links;
    }

    /**
     * Get the Link elements
     * 
     * @return a LinkSet object
     */
    public LinkSet getLinks() {
        if (links == null) {
            links = new LinkSet();
        }
        return links;
    }

    /**
     * Set the Table elements
     * 
     * @param tables
     */
    public void setTables(final TableSet tables) {
        this.tables = tables;
    }

    /**
     * Get the Table elements
     * 
     * @return a TableSet object
     */
    public TableSet getTables() {
        if (tables == null) {
            tables = new TableSet();
        }
        return tables;
    }

    /**
     * Set the Resource elements
     * 
     * @param resources
     */
    public void setResources(final ResourceSet resources) {
        this.resources = resources;
    }

    /**
     * Get the Resource elements
     * 
     * @return a ResourceSet object
     */
    public ResourceSet getResources() {
        if (resources == null) {
            resources = new ResourceSet();
        }
        return resources;
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
     * @return a InfoSet object
     * @since VOTable 1.2
     */
    public InfoSet getInfosAtEnd() {
        if (infosAtEnd == null) {
            infosAtEnd = new InfoSet();
        }
        return infosAtEnd;
    }

    /**
     * Set the name attribute
     * 
     * @param name
     *            String
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the name attribute
     * 
     * @return a String
     */
    public String getName() {
        return str(name);
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
     * @return String
     */
    public String getId() {
        return str(id);
    }

    /**
     * Set the type attribute
     * 
     * @param type
     *            String (results, meta)
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
     * Set the utype attribute
     * 
     * @param utype
     *            String
     */
    public void setUtype(final String utype) {
        this.utype = utype;
    }

    /**
     * Get the utype attribute
     * 
     * @return a String
     */
    public String getUtype() {
        return str(utype);
    }

    /**
     * Get the number of TR object for table index tableIndex (shortcut)
     * 
     * @param tableIndex
     * @return int
     */
    public int getTRCount(final int tableIndex) {
        return getTables().getItemAt(tableIndex).getData().getTableData().getTRs().getItemCount();
    }

    /**
     * Get a TRSet object for table index tableIndex (shortcut)
     * 
     * @param tableIndex
     * @return TRSet
     */
    public TRSet getTRSet(final int tableIndex) {
        return getTables().getItemAt(tableIndex).getData().getTableData().getTRs();
    }

    /**
     * Get a TR object for table index tableIndex and the corresponding row
     * index rowIndex of this table (shortcut)
     * 
     * @param tableIndex
     * @param rowIndex
     * @return SavotTR
     */
    public SavotTR getTR(final int tableIndex, final int rowIndex) {
        return getTables().getItemAt(tableIndex).getData().getTableData().getTRs().getItemAt(rowIndex);
    }

    /**
     * Return the number of tables contained in the resource this value doesn't
     * contain the tables of included resources (shortcut)
     * 
     * @return int
     */
    public int getTableCount() {
        return getTables().getItemCount();
    }

    /**
     * Get a FieldSet object for table index tableIndex (shortcut)
     * 
     * @param tableIndex
     * @return FieldSet
     */
    public FieldSet getFieldSet(final int tableIndex) {
        return getTables().getItemAt(tableIndex).getFields();
    }

    /**
     * Get a LinkSet object for table index tableIndex (shortcut)
     * 
     * @param tableIndex
     * @return LinkSet
     */
    public LinkSet getLinkSet(final int tableIndex) {
        return getTables().getItemAt(tableIndex).getLinks();
    }

    /**
     * Get a Description object (final String) for table index tableIndex (shortcut)
     * 
     * @param tableIndex
     * @return String
     */
    public String getDescription(final int tableIndex) {
        return getTables().getItemAt(tableIndex).getDescription();
    }

    /**
     * Get a SavotData object for table index tableIndex (shortcut)
     * 
     * @param tableIndex
     * @return SavotData
     */
    public SavotData getData(final int tableIndex) {
        return getTables().getItemAt(tableIndex).getData();
    }
}