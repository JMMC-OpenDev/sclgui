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
 * Stream element
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public final class SavotStream extends MarkupComment {

    // content
    private String content = null;
    // type attribute (locator, other)
    private String type = "locator"; // default
    // href attribute
    private String href = null;
    // actuate attribute
    private String actuate = null;
    // width encoding
    private String encoding = null;
    // expires attribute
    private String expires = null;
    // rights attribute
    private String rights = null;

    /**
     * Constructor
     */
    public SavotStream() {
    }

    /**
     * Set type attribute
     * 
     * @param type
     *            (locator, other)
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Get type attribute
     * 
     * @return String
     */
    public String getType() {
        return str(type);
    }

    /**
     * Set href attribute
     * 
     * @param href
     *            (URI)
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
     * Set actuate attribute
     * 
     * @param actuate
     *            (onLoad, onRequest, other, none)
     */
    public void setActuate(final String actuate) {
        this.actuate = actuate;
    }

    /**
     * Get actuate attribute
     * 
     * @return String
     */
    public String getActuate() {
        return str(actuate);
    }

    /**
     * Set encoding attribute
     * 
     * @param encoding
     *            (gzip, base64, dynamic, none)
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get encoding attribute
     * 
     * @return String
     */
    public String getEncoding() {
        return str(encoding);
    }

    /**
     * Set expires attribute
     * 
     * @param expires
     */
    public void setExpires(final String expires) {
        this.expires = expires;
    }

    /**
     * Get width attribute
     * 
     * @return String
     */
    public String getExpires() {
        return str(expires);
    }

    /**
     * Set rights attribute
     * 
     * @param rights
     */
    public void setRights(final String rights) {
        this.rights = rights;
    }

    /**
     * Get rights attribute
     * 
     * @return String
     */
    public String getRights() {
        return str(rights);
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
}