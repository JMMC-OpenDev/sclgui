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
 * <p>Binary interpreter for the type "doubleComplex" of VOTable.</p>
 * <ul>
 * 	<li>Null value = NaN NaN</li>
 * 	<li>Size = 2*8 bytes</li>
 * 	<li>Java type = Double[2]</li>
 * </ul>
 * 
 * @author Gregory Mantelet
 * @version 4.0 
 * @since 09/2011
 */
public class DoubleComplexInterpreter extends BinaryFieldInterpreter<Double[]> {

    private final DoubleInterpreter doubleDecoder;

    public DoubleComplexInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        super(arraysizes, "double complex", 16);
        doubleDecoder = new DoubleInterpreter();
    }

    @Override
    public Double[] decodePrimary(final byte[] bytes, final int offset) throws BinaryInterpreterException {
        Double[] complex = new Double[2];
        complex[0] = doubleDecoder.decodePrimary(bytes, offset);
        complex[1] = doubleDecoder.decodePrimary(bytes, offset + (NB_BYTES / 2));

        if (complex[0] == null || complex[0] == Double.NaN
                || complex[1] == null || complex[1] == Double.NaN) {
            complex = new Double[]{null, null};
        }

        return complex;
    }

    @Override
    protected Class<Double[][]> getArrayClass() {
        return Double[][].class;
    }

    @Override
    protected ArrayList<Double[]> convertIntoArray(Object value) throws BinaryInterpreterException {
        ArrayList<Double> doubles = doubleDecoder.convertIntoArray(value);

        if (doubles.size() % 2 > 0) {
            throw new BinaryInterpreterException("Bad number of double values (" + doubles.size() + "): not a factor of 2 ! Note: a Double Complex is composed of 2 double values.");
        }

        ArrayList<Double[]> list = new ArrayList<Double[]>();
        for (int i = 0; i + 1 < doubles.size(); i += 2) {
            list.add(new Double[]{doubles.get(i), doubles.get(i + 1)});
        }
        return list;
    }

    @Override
    protected Double[] convertPrimary(Object value) throws BinaryInterpreterException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] encodePrimary(Double[] value) throws BinaryInterpreterException {
        if (value.length % 2 > 0) {
            throw new BinaryInterpreterException("Bad number of double values: a Double Complex is composed of 2 double values !");
        }

        byte[] encoded = new byte[2 * doubleDecoder.NB_BYTES];
        for (int i = 0; i < 2; i++) {
            byte[] bytes = doubleDecoder.encodePrimary(value[i]);
            for (int b = 0; b < doubleDecoder.NB_BYTES; b++) {
                encoded[i * 8 + b] = bytes[b];
            }
        }
        return encoded;
    }
}
