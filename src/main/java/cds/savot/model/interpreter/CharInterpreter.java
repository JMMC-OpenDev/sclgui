package cds.savot.model.interpreter;

//
//Copyright 2002-2013 - Universite de Strasbourg / Centre National de la
//Recherche Scientifique
//------
//
//SAVOT Model Interpreter
//
//Author:  Gregory Mantelet
//Address: Centre de Donnees astronomiques de Strasbourg
//   11 rue de l'Universite
//   67000 STRASBOURG
//   FRANCE
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
//                 -----------
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
//
/**
 * <p>Binary interpreter for the type "character" of VOTable.</p>
 * <ul>
 * 	<li>Null value = \0</li>
 * 	<li>Size = 1 byte</li>
 * 	<li>Java type = Character</li>
 * </ul>
 * 
 * @author Gregory Mantelet
 * @version 4.0 
 * @since 09/2011
 */
public class CharInterpreter extends BinaryFieldInterpreter<Character> {

    public CharInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        this(arraysizes, "character", 1);
    }

    public CharInterpreter(final int[] arraysizes, final String typeLabel, final int nbBytes) throws BinaryInterpreterException {
        super(arraysizes, "character", 1);
        arraySeparator = "";
    }

    @Override
    public Character decodePrimary(final byte[] b, final int offset) throws BinaryInterpreterException {
        return (char) (b[offset] & 0xff);
    }

    @Override
    protected Class<Character[]> getArrayClass() {
        return Character[].class;
    }

    @Override
    protected Character convertPrimary(Object value) throws BinaryInterpreterException {
        if (value == null) {
            return '\0';
        }

        if (value instanceof Character) {
            return (Character) value;
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.length() > 1) {
                throw new BinaryInterpreterException("Impossible to convert into a single " + TYPE_LABEL + " a String which contains " + str.length() + " characters !");
            } else if (str.isEmpty()) {
                return '\0';
            } else {
                return str.charAt(0);
            }
        } else {
            throw new BinaryInterpreterException("Impossible to convert a " + value.getClass().getName() + " into a " + TYPE_LABEL + " !");
        }
    }

    @Override
    public byte[] encodePrimary(Character value) throws BinaryInterpreterException {
        if (value == null) {
            return getPadding();
        } else {
            return new byte[]{(byte) value.charValue()};
        }
    }
}
