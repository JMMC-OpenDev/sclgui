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
 * <p>Binary interpreter for the type "float" of VOTable.</p>
 * <ul>
 * 	<li>Null value = NaN</li>
 * 	<li>Size = 4 bytes</li>
 * 	<li>Java type = Float</li>
 * </ul>
 * 
 * @author Gregory Mantelet
 * @version 4.0 
 * @since 09/2011
 */
public class FloatInterpreter extends BinaryFieldInterpreter<Float> {

    private final IntegerInterpreter intDecoder;

    public FloatInterpreter() throws BinaryInterpreterException {
        this(new int[]{1});
    }

    public FloatInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        super(arraysizes, "float complex", 4);
        intDecoder = new IntegerInterpreter();
    }

    @Override
    public Float decodePrimary(final byte[] bytes, final int offset) throws BinaryInterpreterException {
        return Float.intBitsToFloat(intDecoder.decodePrimary(bytes, offset));
    }

    @Override
    protected Class<Float[]> getArrayClass() {
        return Float[].class;
    }

    @Override
    protected Float convertPrimary(Object value) throws BinaryInterpreterException {
        if (value == null) {
            return Float.NaN;
        }

        if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException nfe) {
                throw new BinaryInterpreterException("Impossible to convert \"" + value + "\" into a Float: " + nfe.getMessage() + " !");
            }
        } else {
            throw new BinaryInterpreterException("Impossible to convert a " + value.getClass().getName() + " into a Float !");
        }
    }

    @Override
    public byte[] encodePrimary(Float value) throws BinaryInterpreterException {
        if (value == null) {
            value = Float.NaN;
        }
        return intDecoder.encodePrimary(Float.floatToRawIntBits(value));
    }
}
