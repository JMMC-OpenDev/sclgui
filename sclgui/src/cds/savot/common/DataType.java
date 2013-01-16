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
package cds.savot.common;

import cds.savot.model.SavotField;
import cds.savot.model.SavotParam;

/**
 * This enumeration contains valid values for the datatype attribute (FIELD / PARAM)
 * 
 * @see Markups#DATATYPE
 * @see SavotField
 * @see SavotParam
 * 
 * @author bourgesl
 */
public enum DataType {

    /** Logical data type */
    BOOLEAN("boolean"),
    /** Bit data type */
    BIT("bit"),
    /** Byte data type */
    UNSIGNED_BYTE("unsignedByte"),
    /** Short Integer data type */
    SHORT("short"),
    /** Integer data type */
    INT("int"),
    /** Long Integer data type */
    LONG("long"),
    /** ASCII Character data type */
    CHAR("char"),
    /** Unicode Character data type */
    UNICODE_CHAR("unicodeChar"),
    /** Floating point data type */
    FLOAT("float"),
    /** Double precision Floating point data type */
    DOUBLE("double"),
    /** Float Complex data type */
    FLOAT_COMPLEX("floatComplex"),
    /** Double Complex data type */
    DOUBLE_COMPLEX("doubleComplex");

    /* members */
    /** DataType value as described in VOTable standard */
    private final String value;

    /**
     * Custom constructor
     * @param value DataType representation
     */
    private DataType(final String value) {
        this.value = value;
    }

    /**
     * Return the DataType value as described in VOTable standard
     * @return DataType value as described in VOTable standard
     */
    public String getRepresentation() {
        return value;
    }

    public static Object convert(final DataType datatype, final String content) {
        switch (datatype) {
            case DOUBLE:
                return Double.valueOf(content);
            case INT:
                return Integer.valueOf(content);
            case CHAR:
                return content;
            case UNICODE_CHAR:
                return content;
            case FLOAT:
                return Float.valueOf(content);
            case LONG:
                return Long.valueOf(content);
            case BOOLEAN:
                return Boolean.valueOf(content);
            case UNSIGNED_BYTE:
                return Byte.valueOf(content);
            case SHORT:
                return Short.valueOf(content);
            case BIT:
                return Boolean.valueOf(content);
            case FLOAT_COMPLEX:
                // TODO: use JMMC Complex type (not supported for now)
                return null;
            case DOUBLE_COMPLEX:
                // TODO: use JMMC Complex type (not supported for now)
                return null;
            default:
                return null;
        }
    }
}
