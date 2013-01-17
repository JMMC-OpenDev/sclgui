//
// Copyright 2002-2012 - Universite de Strasbourg / Centre National de la
// Recherche Scientifique
//
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
 * Base class of the Savot model
 * </p>
 * 
 * @author Andre Schaaff
 * @version 4.0 (kickoff 31 May 02)
 */
public class SavotBase implements SimpleTypes {

    /**
     * Public constructor
     */
    public SavotBase() {
    }

    // TODO: type conversion
    /**
     * Helper function to return String value or "" if the given string is null
     * @param str any String value
     * @return String value or "" if the given string is null
     */
    protected final String str(final String str) {
        if (str != null) {
            return str;
        }
        return "";
    }

    /**
     * Helper function to return int value or 0 if the given string is null
     * @param str any String value
     * @return int value or 0 if the given string is null
     */
    protected final int integer(final String str) {
        if (str != null) {
            return Integer.parseInt(str);
        }
        return 0;
    }

    /**
     * Helper function to convert the integer value to String or null if the given integer is negative
     * @param val an integer value
     * @return String value or null if the given integer is negative
     */
    protected final String toStr(final int val) {
        if (val >= 0) {
            return Integer.toString(val);
        }
        return null;
    }
}
