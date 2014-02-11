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
import java.util.ArrayList;

/**
 * <p>Binary interpreter for the type "floatComplex" of VOTable.</p>
 * <ul>
 * 	<li>Null value = NaN NaN</li>
 * 	<li>Size = 2*4 bytes</li>
 * 	<li>Java type = Float[2]</li>
 * </ul>
 * 
 * @author Gregory Mantelet
 * @version 4.0 
 * @since 09/2011
 */
public class FloatComplexInterpreter extends BinaryFieldInterpreter<Float[]> {

    private final FloatInterpreter floatDecoder;

    public FloatComplexInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        super(arraysizes, "float complex", 8);
        floatDecoder = new FloatInterpreter();
    }

    @Override
    public Float[] decodePrimary(final byte[] bytes, final int offset) throws BinaryInterpreterException {
        Float[] complex = new Float[2];
        complex[0] = floatDecoder.decodePrimary(bytes, offset);
        complex[1] = floatDecoder.decodePrimary(bytes, offset + (NB_BYTES / 2));

        if (complex[0] == null || complex[0] == Float.NaN
                || complex[1] == null || complex[1] == Float.NaN) {
            complex = new Float[]{Float.NaN, Float.NaN};
        }

        return complex;
    }

    @Override
    protected Class<Float[][]> getArrayClass() {
        return Float[][].class;
    }

    @Override
    protected ArrayList<Float[]> convertIntoArray(Object value) throws BinaryInterpreterException {
        ArrayList<Float> floats = floatDecoder.convertIntoArray(value);

        if (floats.size() % 2 > 0) {
            throw new BinaryInterpreterException("Bad number of float values (" + floats.size() + "): not a factor of 2 ! Note: a Float Complex is composed of 2 float values.");
        }

        ArrayList<Float[]> list = new ArrayList<Float[]>();
        for (int i = 0; i + 1 < floats.size(); i += 2) {
            list.add(new Float[]{floats.get(i), floats.get(i + 1)});
        }
        return list;
    }

    @Override
    protected Float[] convertPrimary(Object value) throws BinaryInterpreterException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] encodePrimary(Float[] value) throws BinaryInterpreterException {
        if (value.length % 2 > 0) {
            throw new BinaryInterpreterException("Bad number of float values: a Float Complex is composed of 2 float values !");
        }

        byte[] encoded = new byte[2 * floatDecoder.NB_BYTES];
        for (int i = 0; i < 2; i++) {
            byte[] bytes = floatDecoder.encodePrimary(value[i]);
            for (int b = 0; b < floatDecoder.NB_BYTES; b++) {
                encoded[i * 8 + b] = bytes[b];
            }
        }
        return encoded;
    }
}
