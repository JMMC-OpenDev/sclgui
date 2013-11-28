package cds.savot.common;

//
//Copyright 2002-2013 - Universite de Strasbourg / Centre National de la
//Recherche Scientifique
//------
//
//SAVOT - Simple Access to VOTable - Parser
//
//Author:  Andre Schaaff
//Address: Centre de Donnees astronomiques de Strasbourg
//         11 rue de l'Universite
//         67000 STRASBOURG
//         FRANCE
//Contributors: Laurent Bourges (code improvement, ideas, tests, etc.), Gregory Mantelet (binary capabilities)
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
//                   -----------
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
/**
 * This enum contains all tags present in VOTable specification
 * @author Laurent Bourges
 * @version 4.0
 */
public enum VOTableTag {

    UNDEFINED,
    BINARY,
    COOSYS,
    DATA,
    DEFINITIONS,
    DESCRIPTION,
    FIELD,
    FIELDREF,
    FITS,
    GROUP,
    INFO,
    LINK,
    MIN,
    MAX,
    OPTION,
    PARAM,
    PARAMREF,
    RESOURCE,
    STREAM,
    SYSTEM,
    TABLE,
    TABLEDATA,
    TD,
    TR,
    VALUES,
    VOTABLE;

    /**
     * Return the VoTableTag corresponding to the given string (ignore case)
     *
     * LBO: Note: equalsIgnoreCase() vs() equals as XML is case sensitive and VOTable specification says that clearly
     *
     * @param name tag name to look up
     * @return VoTableTag or VoTableTag.UNDEFINED if no match
     */
    public static VOTableTag parseTag(final String name) {
        switch (name.charAt(0)) {
            case 'T':
            case 't':
                // note: String comparison using == because our kXml parser or Java Stax Parser provides interned strings using SymbolTable:
                if (Markups.TD == name) {
                    return TD;
                }
                if (Markups.TR == name) {
                    return TR;
                }
                // standard comparison fallback:
                if (Markups.TD.equalsIgnoreCase(name)) {
                    return TD;
                }
                if (Markups.TR.equalsIgnoreCase(name)) {
                    return TR;
                }
                if (Markups.TABLE.equalsIgnoreCase(name)) {
                    return TABLE;
                }
                if (Markups.TABLEDATA.equalsIgnoreCase(name)) {
                    return TABLEDATA;
                }
                break;

            case 'F':
            case 'f':
                if (Markups.FIELD.equalsIgnoreCase(name)) {
                    return FIELD;
                }
                if (Markups.FIELDREF.equalsIgnoreCase(name)) {
                    return FIELDREF;
                }
                if (Markups.FITS.equalsIgnoreCase(name)) {
                    return FITS;
                }
                break;

            case 'G':
            case 'g':
                if (Markups.GROUP.equalsIgnoreCase(name)) {
                    return GROUP;
                }
                break;

            case 'P':
            case 'p':
                if (Markups.PARAM.equalsIgnoreCase(name)) {
                    return PARAM;
                }
                if (Markups.PARAMREF.equalsIgnoreCase(name)) {
                    return PARAMREF;
                }
                break;

            case 'I':
            case 'i':
                if (Markups.INFO.equalsIgnoreCase(name)) {
                    return INFO;
                }
                break;

            case 'D':
            case 'd':
                if (Markups.DESCRIPTION.equalsIgnoreCase(name)) {
                    return DESCRIPTION;
                }
                if (Markups.DATA.equalsIgnoreCase(name)) {
                    return DATA;
                }
                if (Markups.DEFINITIONS.equalsIgnoreCase(name)) {
                    return DEFINITIONS;
                }
                break;

            case 'B':
            case 'b':
                if (Markups.BINARY.equalsIgnoreCase(name)) {
                    return BINARY;
                }
                break;

            case 'C':
            case 'c':
                if (Markups.COOSYS.equalsIgnoreCase(name)) {
                    return COOSYS;
                }
                break;

            case 'L':
            case 'l':
                if (Markups.LINK.equalsIgnoreCase(name)) {
                    return LINK;
                }
                break;

            case 'M':
            case 'm':
                if (Markups.MIN.equalsIgnoreCase(name)) {
                    return MIN;
                }
                if (Markups.MAX.equalsIgnoreCase(name)) {
                    return MAX;
                }
                break;

            case 'O':
            case 'o':
                if (Markups.OPTION.equalsIgnoreCase(name)) {
                    return OPTION;
                }
                break;

            case 'S':
            case 's':
                if (Markups.STREAM.equalsIgnoreCase(name)) {
                    return STREAM;
                }
                if (Markups.SYSTEM.equalsIgnoreCase(name)) {
                    return SYSTEM;
                }
                break;

            case 'R':
            case 'r':
                if (Markups.RESOURCE.equalsIgnoreCase(name)) {
                    return RESOURCE;
                }
                break;

            case 'V':
            case 'v':
                if (Markups.VALUES.equalsIgnoreCase(name)) {
                    return VALUES;
                }
                if (Markups.VOTABLE.equalsIgnoreCase(name)) {
                    return VOTABLE;
                }
                break;

            default:
        }
        return UNDEFINED;
    }
}
