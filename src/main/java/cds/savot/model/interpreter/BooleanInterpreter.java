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
 * <p>Binary interpreter for the type "logical" of VOTable.</p>
 * <ul>
 * 	<li>Null value = 0x00</li>
 * 	<li>Size = 1 byte</li>
 * 	<li>Java type = Boolean</li>
 * </ul>
 * 
 * @author Gregory Mantelet
 * @version 4.0 
 * @since 09/2011
 */
public class BooleanInterpreter extends BinaryFieldInterpreter<Boolean> {

    public BooleanInterpreter(final int[] arraysizes) throws BinaryInterpreterException {
        super(arraysizes, "logical value", 1);
    }

    @Override
    public Boolean decodePrimary(final byte[] b, final int offset) throws BinaryInterpreterException {
        char temp = (char) (b[offset] & 0xff);
        switch (temp) {
            case '1':
            case 'T':
            case 't':
                return Boolean.TRUE;
            case '0':
            case 'F':
            case 'f':
                return Boolean.FALSE;
            case '\0':
            case ' ':
            case '?':
                return null;
            default:
                throw new BinaryInterpreterException("Unknown logical representation: \"" + temp + "\" !");
        }
    }

    @Override
    protected Class<Boolean[]> getArrayClass() {
        return Boolean[].class;
    }

    @Override
    protected Boolean convertPrimary(Object value) throws BinaryInterpreterException {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("t") || str.equalsIgnoreCase("1")) {
                return Boolean.TRUE;
            } else if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("0")) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        } else {
            throw new BinaryInterpreterException("Impossible to convert a " + value.getClass().getName() + " into a Boolean !");
        }
    }

    @Override
    public byte[] encodePrimary(Boolean value) throws BinaryInterpreterException {
        if (value == null) {
            return getPadding();
        } else {
            return new byte[]{(value ? (byte) '1' : (byte) '0')};
        }
    }
}
