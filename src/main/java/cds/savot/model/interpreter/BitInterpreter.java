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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * 
 * @author Gregory Mantelet
 * @version 4.0
 *
 */
public class BitInterpreter extends CharInterpreter {

    public BitInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        super(arraysizes, "bit array", 1);
    }

    @Override
    public Character[] decode(final InputStream input) throws IOException, BinaryInterpreterException {
        int arraysize = getArraySize(input);

        int nbBytes = arraysize / 8;
        if (arraysize % 8 > 0) {
            nbBytes++;
        }

        byte[] bytes = readBytes(input, nbBytes);

        Character[] decoded = createEmptyArray(arraysize);
        int indBit = 0;
        for (int i = 0; i < nbBytes && indBit < arraysize; i++) {
            byte b = bytes[i];
            for (int j = 0; j < 8 && indBit < arraysize; j++, indBit++) {
                char c = (char) (((0x80 & (b << j)) >>> 7) | '0');
                decoded[indBit] = c;
            }
        }

        return decoded;
    }

    @Override
    public Character decodePrimary(final byte[] bytes, final int offset) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class<Character[]> getArrayClass() {
        return Character[].class;
    }

    @Override
    protected Character convertPrimary(Object value) throws BinaryInterpreterException {
        if (value != null && value instanceof Boolean) {
            return ((Boolean) value) ? '1' : '0';
        } else {
            char c = super.convertPrimary(value);
            if (c != '0' && c != '1') {
                throw new BinaryInterpreterException("Incorrect bit value: \"" + c + "\" !");
            }
            return c;
        }
    }

    @Override
    public void encode(OutputStream output, Object value) throws IOException, BinaryInterpreterException {
        boolean variableArray = (nbItems < 0);
        boolean encodeInArray = (variableArray || (nbItems > 1));

        // get the corresponding array, but of only 1 dimension:
        ArrayList<Character> values = new ArrayList<Character>();
        if (value.getClass().isArray()) {
            if (!encodeInArray) {
                throw new BinaryInterpreterException("Impossible to encode an array into a single " + TYPE_LABEL + " !");
            } else {
                values.addAll(convertIntoArray((Object[]) value));
            }
        } else {
            values.add(convertPrimary(value));
        }

        // write the number of bytes if needed:
        if (variableArray) {
            arraySizeInterpreter.encode(output, values.size());
        } else if (values.size() > nbItems) {
            throw new BinaryInterpreterException("The given array is bigger than the arraysize set by the savot field: " + values.size() + " > " + nbItems + " !");
        }

        // count needed bytes (padding included):
        int nbBytes = 0;
        if (nbItems > 0) {
            nbBytes = (nbItems / 8);
            if (nbItems % 8 > 0) {
                nbBytes++;
            }
        } else {
            nbBytes = (values.size() / 8);
            if (values.size() % 8 > 0) {
                nbBytes++;
            }
        }

        // write all the given items (and the padding bytes):
        byte[] bytes = new byte[nbBytes];
        for (int b = 0; b < nbBytes; b++) {
            bytes[b] = 0;
            for (int i = 0, v = b * 8; i < 8 && v < values.size(); i++, v++) {
                char c = values.get(v).charValue();
                switch (c) {
                    case '1':
                        bytes[b] |= (byte) (1 << (7 - i));
                        break;
                    case '0':
                        break;
                    default:
                        throw new BinaryInterpreterException("Incorrect bit value: " + c);
                }
            }
        }
        output.write(bytes);
    }

    @Override
    public byte[] encodePrimary(Character value) throws BinaryInterpreterException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getPadding(final int length) {
        throw new UnsupportedOperationException();
    }
}
