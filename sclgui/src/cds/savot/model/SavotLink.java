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
 * Link element
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotLink extends MarkupComment implements IDSupport {

    // content
    private String content = null;
    // id attribute
    private String id = null;
    // content-role attribute
    private String contentRole = null;
    // content-type attribute
    private String contentType = null;
    // title attribute
    private String title = null;
    // value attribute
    private String value = null;
    // href attribute
    private String href = null;
    // gref attribute - removed since 1.1
    private String gref = null;
    // action attribute - extension since 1.1
    private String action = null;

    /**
     * Constructor
     */
    public SavotLink() {
    }

    /**
     * Set content
     * 
     * @param content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Get content
     * 
     * @return String
     */
    public String getContent() {
        return str(content);
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
     * @return String
     */
    public String getId() {
        return str(id);
    }

    /**
     * Set contentRole attribute
     * 
     * @param contentRole
     *            (query, hints, doc, location)
     */
    public void setContentRole(final String contentRole) {
        this.contentRole = contentRole;
    }

    /**
     * Get contentRole attribute
     * 
     * @return String
     */
    public String getContentRole() {
        return str(contentRole);
    }

    /**
     * Set contentType attribute
     * 
     * @param contentType
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get contentType attribute
     * 
     * @return String
     */
    public String getContentType() {
        return str(contentType);
    }

    /**
     * Set title attribute
     * 
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Get title attribute
     * 
     * @return String
     */
    public String getTitle() {
        return str(title);
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
     * Set href attribute
     * 
     * @param href
     */
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * Get href attribute
     * 
     * @return String
     */
    public String getHref() {
        return str(href);
    }

    /**
     * Set gref attribute removed in VOTable 1.1
     * 
     * @param gref
     */
    public void setGref(final String gref) {
        this.gref = gref;
    }

    /**
     * Get gref attribute removed in VOTable 1.1
     * 
     * @return String
     */
    public String getGref() {
        return str(gref);
    }

    /**
     * Set action attribute
     * 
     * @param action
     */
    public void setAction(final String action) {
        this.action = action;
    }

    /**
     * Get action attribute
     * 
     * @return String
     */
    public String getAction() {
        return str(action);
    }
}