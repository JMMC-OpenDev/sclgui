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
//Email:   question@simbad.u-strasbg.fr
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
 * <p>Binary interpreter for the type "short" of VOTable.</p>
 * <ul>
 * 	<li>Null value = 0xffff</li>
 * 	<li>Size = 2 bytes</li>
 * 	<li>Java type = Short</li>
 * </ul>
 * 
 * @author Gregory Mantelet
 * @version 4.0 
 * @since 09/2011
 */
public class ShortInterpreter extends BinaryFieldInterpreter<Short> {

    public ShortInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        super(arraysizes, "short value", 2);
    }

    @Override
    public Short decodePrimary(final byte[] b, final int offset) throws BinaryInterpreterException {
        return (short) ((b[offset] << 8) | (b[offset + 1] & 0xff));
    }

    @Override
    protected Class<Short[]> getArrayClass() {
        return Short[].class;
    }

    @Override
    protected Short convertPrimary(Object value) throws BinaryInterpreterException {
        if (value == null) {
            return null;
        }

        if (value instanceof Short) {
            return (Short) value;
        } else if (value instanceof String) {
            try {
                return Short.parseShort((String) value);
            } catch (NumberFormatException nfe) {
                throw new BinaryInterpreterException("Impossible to convert \"" + value + "\" into a Short: " + nfe.getMessage() + " !");
            }
        } else {
            throw new BinaryInterpreterException("Impossible to convert a " + value.getClass().getName() + " into a Short !");
        }
    }

    @Override
    public byte[] encodePrimary(Short value) throws BinaryInterpreterException {
        if (value == null) {
            return new byte[]{(byte) 0xff, (byte) 0xff};
        }

        byte[] encoded = new byte[2];
        encoded[0] = (byte) (value >> 8);
        encoded[1] = (byte) (value & 0xff);
        return encoded;
    }
}
