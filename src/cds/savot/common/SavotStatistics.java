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
 * <p>
 * Statistics
 * </p>
 * * @author Andre Schaaff
 *
 * @version 4.0
 */
public final class SavotStatistics {

    private int iTablesGlobal = 0; // index of TABLE element
    private int iTablesLocal = 0; // index of TABLE element in current RESOURCE
    private int iTRGlobal = 0; // index of TR element
    private int iTRLocal = 0; // index of TR element in current TABLE
    private int iTDGlobal = 0; // index of TD element
    private int iTDLocal = 0; // index of TD element in current TABLE
    private int iResources = 0; // index of RESOURCES element
    private int iGroupsGlobal = 0; // index of GROUP element

    public void iTablesInc() {
        iTablesGlobal++;
        iTablesLocal++;
    }

    public void iTablesGlobalReset() {
        iTablesGlobal = 0;
    }

    public int getITablesGlobal() {
        return iTablesGlobal;
    }

    public void iTablesLocalReset() {
        iTablesLocal = 0;
    }

    public int getITablesLocal() {
        return iTablesLocal;
    }

    public void iTRInc() {
        iTRGlobal++;
        iTRLocal++;
    }

    public void iTRGlobalReset() {
        iTRGlobal = 0;
    }

    public int getITRGlobal() {
        return iTRGlobal;
    }

    public void iTRLocalReset() {
        iTRLocal = 0;
    }

    public int getITRLocal() {
        return iTRLocal;
    }

    public void iTDInc() {
        iTDGlobal++;
        iTDLocal++;
    }

    public void iTDGlobalReset() {
        iTDGlobal = 0;
    }

    public int getITDGlobal() {
        return iTDGlobal;
    }

    public void iTDLocalReset() {
        iTDLocal = 0;
    }

    public int getITDLocal() {
        return iTDLocal;
    }

    public void iResourcesInc() {
        iResources++;
    }

    public void iResourcesReset() {
        iResources = 0;
    }

    public void iResources(final int value) {
        iResources = value;
    }

    public int getIResources() {
        return iResources;
    }

    public void iGroupsGlobalInc() {
        iGroupsGlobal++;
    }

    public void iGroupsGlobalReset() {
        iGroupsGlobal = 0;
    }

    public void iGroupsGlobal(final int value) {
        iGroupsGlobal = value;
    }

    public int getIGroupsGlobal() {
        return iGroupsGlobal;
    }
}
