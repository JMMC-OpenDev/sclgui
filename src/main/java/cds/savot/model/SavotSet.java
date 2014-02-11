package cds.savot.model;

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
//                     -----------
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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Generic class for other set classes
 * </p>
 *
 * @param <E> element type
 * @author Andre Schaaff
 * @version 4.0
 * @see SavotSet
 */
public class SavotSet<E> {

    /** storage of the set elements */
    private ArrayList<E> set = null;

    /**
     * Constructor
     */
    public SavotSet() {
    }

    /**
     * Add an item to the set
     * 
     * @param item
     */
    public final void addItem(final E item) {
        if (set == null) {
            set = new ArrayList<E>();
        }
        set.add(item);
    }

    /**
     * Get an item at a given position (index)
     * 
     * @param index
     * @return Object
     */
    public final E getItemAt(final int index) {
        if (set == null) {
            return null;
        }
        if (index >= 0 && index < set.size()) {
            return set.get(index);
        }
        return null;
    }

    /**
     * Remove an item at a given position (index)
     * 
     * @param index
     */
    public final void removeItemAt(final int index) {
        if (set != null && index >= 0 && index < set.size()) {
            set.remove(index);
        }
    }

    /**
     * Remove all items
     */
    public final void removeAllItems() {
        if (set != null && !set.isEmpty()) {
            set.clear();
        }
    }

    /**
     * Set the whole set to a given set
     * 
     * @param set
     */
    public final void setItems(final ArrayList<E> set) {
        this.set = set;
    }

    /**
     * Get the whole set
     * 
     * @return a ArrayList
     */
    public final List<E> getItems() {
        return set;
    }

    /**
     * Get the number of items
     * 
     * @return int
     */
    public final int getItemCount() {
        if (set != null) {
            return set.size();
        }
        return 0;
    }

    /**
     * Increases the capacity of this <tt>SavotSet</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public final void ensureCapacity(final int minCapacity) {
        if (set == null) {
            set = new ArrayList<E>(minCapacity);
        } else {
            set.ensureCapacity(minCapacity);
        }
    }

    /**
     * Trims the capacity of this <tt>SavotSet</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>SavotSet</tt> instance.
     */
    public final void trim() {
        if (set != null) {
            set.trimToSize();
        }
    }
}
