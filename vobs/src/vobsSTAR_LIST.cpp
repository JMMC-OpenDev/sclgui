/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsSTAR_LIST.cpp,v 1.25 2005-11-16 10:47:55 scetre Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.24  2005/11/16 10:47:54  scetre
* Updated documentation
*
* Revision 1.23  2005/11/15 16:39:55  lafrasse
* Added experimental VOTable export support
*
* Revision 1.22  2005/11/11 16:39:04  gzins
* Added Sort() method
*
* Revision 1.21  2005/06/20 14:32:25  scetre
* Removed unused printf
* changed iterator initialisation to NULL
*
* Revision 1.20  2005/06/17 15:11:41  gzins
* Fixed conflict between Remove() and GetNextStar() methods when element to be deleted is the current element pointed by GetNextStar()
*
* Revision 1.19  2005/06/01 14:16:56  scetre
* Changed logExtDbg to logTrace
*
* Revision 1.18  2005/04/14 14:39:03  scetre
* Updated documentation.
* added test on method return.
*
* Revision 1.17  2005/03/30 12:49:26  scetre
* Updated documentation
*
* Revision 1.16  2005/03/04 16:28:28  scetre
* Changed Call to Save method
*
* Revision 1.15  2005/03/04 16:05:51  scetre
* Updated call to Save method
*
* Revision 1.14  2005/02/15 15:41:47  gzins
* Fixed wrong mcsmcsSUCCESS and mcsmcsFAILURE
*
* Revision 1.13  2005/02/15 15:19:30  gzins
* Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
*
* Revision 1.12  2005/02/14 08:44:43  scetre
* removed unused printf
*
* Revision 1.11  2005/02/13 15:56:55  gzins
* Added optional origin parameter to Load() method
*
* Revision 1.10  2005/02/13 15:27:53  gzins
* Added Load() method
* Updated Save() method to use new vobsCDATA class
* Set default value of criteriaList to NULL
*
* Revision 1.9  2005/02/04 14:31:50  scetre
* updated documentation
*
* Revision 1.8  2005/01/26 14:12:24  scetre
* rewrite save method in vobsSTAR_LIST
*
* Revision 1.7  2005/01/26 08:18:15  scetre
* change history
*
* scetre    06-Jul-2004  Created
* gzins     09-Dec-2004  Fixed cast problem with nez mcsLOGICAL enumerate
*
******************************************************************************/

static char *rcsId="@(#) $Id: vobsSTAR_LIST.cpp,v 1.25 2005-11-16 10:47:55 scetre Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "misc.h"
/*
 * Local Headers 
 */
#include "vobsSTAR_LIST.h"
#include "vobsCDATA.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"

//Class conctructor
vobsSTAR_LIST::vobsSTAR_LIST()
{
    _starIterator = NULL;
}

//Class destructor
vobsSTAR_LIST::~vobsSTAR_LIST()
{
    Clear();
}

/**
 * Copy from a list
 *
 * @param list the list to copy
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_LIST::Copy(vobsSTAR_LIST& list)
{
     
    logTrace("vobsSTAR_LIST::Copy(vobsSTAR_LIST& list)");
    for (unsigned int el = 0; el < list.Size(); el++)
    {
        if (AddAtTail(*(list.GetNextStar((mcsLOGICAL)(el==0)))) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    return mcsSUCCESS;
}

/**
 * Check whether the list is empty or not.  
 *
 * @return
 * True value (i.e. mcsTRUE) if the number of elements is zero, false (i.e.
 * mcsFALSE) otherwise.
 */
mcsLOGICAL vobsSTAR_LIST::IsEmpty(void)
{
    //logTrace("vobsSTAR_LIST::IsEmpty()");
    if (_starList.empty() == true)
    {
        return mcsTRUE;
    }
    else
    {
        return mcsFALSE;
    }
}

/**
 * Erase all elements from the list.
 *
 * @return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::Clear(void)
{
    // Delete all objects in list 
    std::list<vobsSTAR *>::iterator iter;
    for (iter=_starList.begin(); iter != _starList.end(); iter++)
    {
        delete (*iter);
    }

    // Clear list
    _starList.clear();

    return mcsSUCCESS;
}

/**
 * Adds the element at the end of the list
 *
 * @param star element to be added to the list.
 *
 * @return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::AddAtTail(vobsSTAR &star)
{
    logTrace("vobsSTAR_LIST::AddAtTail()");
    // Put element in the list
    vobsSTAR *newStar = new vobsSTAR(star);
    _starList.push_back(newStar);

    return mcsSUCCESS;
}

/**
 * Remove the element from the list
 *
 * This method looks for the specified \em star in the list. If found, it
 * remove it, and do nothing otherwise.
 *
 * The method vobsSTAR::IsSame() is used to compare element of the list with
 * the specified one.
 *
 * \warning if list contains more than one instance, only first occurence is
 * removed. 
 * 
 * @note This method does not conflict with GetNextStar(); i.e. it can be used
 * to remove the star returned by GetNextStar() method, as shown below:
 * \code
 *     for (unsigned int el = 0; el < starList.Size(); el++)
 *     {
 *         vobsSTAR *star;
 *         star = starList.GetNextStar((mcsLOGICAL)(el==0));
 *         if ( <condition> )
 *         {
 *              // Remove star from list 
 *              starList.Remove(*star);
 *
 *              // and decrease 'el' to take into account the new list size
 *              el--;
 *         }
 *     }
 * \endcode

 * @param star element to be removed from the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::Remove(vobsSTAR &star)
{
    logTrace("vobsSTAR_LIST::Remove()");

    // Search star in the list
    std::list<vobsSTAR *>::iterator iter;
    for (iter=_starList.begin(); iter != _starList.end(); iter++)
    {
        // If found
        if ((*iter)->IsSame(star) == mcsTRUE)
        {
            // Delete element
            delete (*iter);

            // If element to be deleted correspond to the one currently pointed
            // by GetNextStar method
            if (*_starIterator == *iter)
            {
                // If it is not the first element of the list
                if (iter != _starList.begin())
                {
                    // Then go back to the previous star
                    _starIterator--;
                }
                else
                {
                    // Else set current pointer to NULL in order to restart scan
                    // from beginning of the list.
                    _starIterator = NULL;
                }
            }
            
            // Clear element from list
            _starList.erase(iter);

            return mcsSUCCESS;
        }
    }

    return mcsSUCCESS;
}

/**
 * Returns the number of elements (stars) currently stored in the list.
 * @return 
 * The numbers of stars in the list.
 */
mcsUINT32 vobsSTAR_LIST::Size(void) 
{
    return _starList.size();
}

/**
 * Returns the next element (star) in the list.
 *
 * This method returns the pointer to the next element of the list. If \em
 * init is mcsTRUE, it returns the first element of the list.
 * 
 * This method can be used to move forward in the list, as shown below:
 * \code
 *     for (unsigned int el = 0; el < starList.Size(); el++)
 *     {
 *         starList.GetNextStar((mcsLOGICAL)(el==0))->View();
 *     }
 * \endcode
 * @return pointer to the next element of the list or NULL if the end of the
 * list is reached.
 */
vobsSTAR *vobsSTAR_LIST::GetNextStar(mcsLOGICAL init) 
{
    logTrace("vobsSTAR_LIST::GetNextStar()");

    if ((init == mcsTRUE) || _starIterator == NULL)
    {
        _starIterator = _starList.begin();
    }
    else
    {
        _starIterator++;
        if (_starIterator == _starList.end())
        {
            return NULL;
        }
    }
    return (*_starIterator);
}

/**
 * Returns the element (star) of the list corresponding to the star.
 *
 * This method looks for the specified \em star in the list. If found, it
 * returns the pointer to this element, and NULL otherwise.
 * The method vobsSTAR::IsSame() is used to compare element of the list with
 * the specified one.
 * 
 * This method can be used to know whether a star is in list or not, as shown
 * below:
 * \code
 *     if (starList.GetStar(star)->View() == NULL)
 *     {
 *         printf ("Star not found in list !!");
 *     }
 * \endcode
 * @return pointer to the found element of the list or NULL if element is not
 * found in list.
 */
vobsSTAR *vobsSTAR_LIST::GetStar(vobsSTAR &star,
                                 vobsSTAR_COMP_CRITERIA_LIST *criteriaList)
{
    //logTrace("vobsSTAR_LIST::GetStar()");

    // Search star in the list
    std::list<vobsSTAR *>::iterator iter;
    for (iter=_starList.begin(); iter != _starList.end(); iter++)
    {
        if ((*iter)->IsSame(star, criteriaList) == mcsTRUE)
        {
            return (*iter);
        }
    }

    // If not found return NULL pointer
    return NULL;
}

/**
 * Merge the specified list.
 *
 * This method merges all elements (stars) of the given list with the current
 * one. If star is already stored in the list, it is just updated using
 * vobsSTAR::Update method, otherwise it is added to the list.
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned if updating or adding star fails.
 */
mcsCOMPL_STAT vobsSTAR_LIST::Merge(vobsSTAR_LIST &list,
                                   vobsSTAR_COMP_CRITERIA_LIST *criteriaList,
                                   mcsLOGICAL updateOnly)
{
    logTrace("vobsSTAR_LIST::Merge()");

    // For each star of the given list
    unsigned int nbStars;
    nbStars = list.Size();
    for (unsigned int el = 0; el < nbStars; el++)
    {
        vobsSTAR *starPtr;
        starPtr = list.GetNextStar((mcsLOGICAL)(el==0));
        // If star is in the list
        vobsSTAR *starToUpdatePtr;
        starToUpdatePtr = GetStar(*starPtr, criteriaList);
        if (starToUpdatePtr != NULL)
        {
            // Update the star
            if (starToUpdatePtr->Update(*starPtr)== mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
        else if (updateOnly == mcsFALSE)
        {
            // Else add it to the list
            if (AddAtTail(*starPtr) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
    }
 
    return mcsSUCCESS;
}

/**
 * Sort the list.
 *
 * This method sorts the given list according to the given property Id. 
 * 
 * @param propertyId property id 
 * @param reverseOrder indicates sorting order 
 *
 * @return mcsSUCCESS on successful completion, and mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT vobsSTAR_LIST::Sort(char *propertyId, mcsLOGICAL reverseOrder)
{
    logTrace("vobsSTAR_LIST::Sort()");
 
    // If list is empty or contains only one element, return
    if ((IsEmpty() == mcsTRUE) || (Size() == 1))
    {
        return mcsSUCCESS;
    }

    // Check that the given property Id exists
    if (GetNextStar(mcsTRUE)->IsProperty(propertyId) == mcsFALSE)
    {
        errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId); 
        return mcsFAILURE;
    }

    // Get property type 
    vobsPROPERTY_TYPE propertyType;
    propertyType = GetNextStar(mcsTRUE)->GetProperty(propertyId)->GetType();
    printf("propertyType = %d\n", propertyType); 
    
    // While the sorting is not achieved
    mcsLOGICAL sortingDone;
    do 
    {
        // Assumes the list is sorted
        sortingDone = mcsTRUE;

        // For all elements of the list
        std::list<vobsSTAR *>::iterator iter;
        std::list<vobsSTAR *>::iterator prevIter=_starList.begin();
        for (iter=_starList.begin(); iter != _starList.end(); iter++)
        {
            // If iterator is not on the first element
            if (iter != _starList.begin()) 
            {
                // Indicates if current elements are to swapped or not
                mcsLOGICAL swap = mcsFALSE;

                // Check properties are set
                mcsLOGICAL isValue1Set;
                mcsLOGICAL isValue2Set;
                isValue1Set = (*prevIter)->IsPropertySet(propertyId);
                isValue2Set = (*iter)->IsPropertySet(propertyId);

                // If one of the properties is not set, move it at the begining
                // or at the end, according to the sorting order
                if ((isValue1Set == mcsFALSE) || (isValue2Set == mcsFALSE))
                {
                    // If it is normal sorting order
                    if (reverseOrder == mcsFALSE) 
                    {
                        // If value of previous element is not set while next
                        // one is, swap them
                        if ((isValue1Set == mcsFALSE) && 
                            (isValue2Set == mcsTRUE))
                        {
                            swap = mcsTRUE;
                        }
                    }
                    // Else (reverse sorting order)
                    else
                    {
                        // If value of next element is not set while previous
                        // one is, swap them
                        if ((isValue2Set == mcsFALSE) && 
                            (isValue1Set == mcsTRUE))
                        {
                            swap = mcsTRUE;
                        }
                    }
                }
                // Else (properties are set)
                else
                {
                    // Compare element values according to property  or property
                    // type, and check if elements have to be swapped according
                    // to the sorting order
                    if ((strcmp (propertyId, vobsSTAR_POS_EQ_RA_MAIN)  == 0) ||
                        (strcmp (propertyId, vobsSTAR_POS_EQ_DEC_MAIN) == 0) ||
                        (propertyType == vobsFLOAT_PROPERTY))
                    {
                        mcsFLOAT value1;
                        mcsFLOAT value2;
                        if (strcmp (propertyId, vobsSTAR_POS_EQ_RA_MAIN)  == 0)
                        {
                            (*prevIter)->GetRa(value1);
                            (*iter)->GetRa(value2);
                        }
                        else if (strcmp (propertyId, 
                                         vobsSTAR_POS_EQ_DEC_MAIN) == 0)
                        {
                            (*prevIter)->GetDec(value1);
                            (*iter)->GetDec(value2);
                        }
                        else
                        {
                            (*prevIter)->GetPropertyValue(propertyId, &value1);
                            (*iter)->GetPropertyValue(propertyId, &value2);
                        }

                        if (reverseOrder == mcsFALSE) 
                        {
                            if (value1 > value2)
                            {
                                swap = mcsTRUE;
                            }
                        }
                        else
                        {
                            if (value1 < value2)
                            {
                                swap = mcsTRUE;
                            }
                        }
                    }
                    else
                    {
                        const char *value1;
                        const char *value2;
                        value1 = (*prevIter)->GetPropertyValue(propertyId);
                        value2 = (*iter)->GetPropertyValue(propertyId);

                        if (reverseOrder == mcsFALSE) 
                        {
                            if (strcmp (value1, value2) > 0)
                            {
                                swap = mcsTRUE;
                            }
                        }
                        else
                        {
                            if (strcmp (value1, value2) < 0)
                            {
                                swap = mcsTRUE;
                            }
                        }
                    }
                }

                // Swap elements if needed 
                if (swap == mcsTRUE)
                {
                    iter++;
                    iter = _starList.insert(iter, (*prevIter));
                    _starList.erase(prevIter);
                    sortingDone = mcsFALSE;
                }
                
                // Update previous element iterator 
                prevIter = iter;
            }
            else
            {
                prevIter = iter;
            }
        }
    } while (sortingDone == mcsFALSE);

    
    return mcsSUCCESS;
}

/**
 * Method to print the list on the console
 */
/**
 * Display the elements (stars) of the list.
 *
 * This method display all elements of the list on the console, using the
 * vobsSTAR::Display method.
 */
void vobsSTAR_LIST::Display(void)
{
    logTrace("vobsSTAR_LIST::Display()");

    // Display all element of the list 
    std::list<vobsSTAR *>::iterator iter;
    for (iter=_starList.begin(); iter != _starList.end(); iter++)
    {
        (*iter)->Display();
    }
    printf("\n");

}

/**
 * Save each star in a VOTable v1.0.
 *
 * @param filename the path to the file in which the VOTable should be saved
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT vobsSTAR_LIST::SaveToVOTable(const char *filename)
{
    logTrace("vobsSTAR_LIST::SaveToVOTable()");

    miscoDYN_BUF buffer;

    // Add VOTable standard header
    buffer.AppendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    buffer.AppendLine(" <VOTABLE version=\"1.1\"");
    buffer.AppendLine("          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    buffer.AppendLine("          xsi:noNamespaceSchemaLocation=\"http://www.ivoa.net/xml/VOTable/VOTable/v1.1\">");

    // Add SCALIB specific informations
    buffer.AppendLine(" <DESCRIPTION>");
    buffer.AppendLine("  SCALIB output");
    buffer.AppendLine(" </DESCRIPTION>");

    // Add context specific informations
    buffer.AppendLine(" <COOSYS ID=\"J2000\" equinox=\"J2000.\" epoch=\"J2000.\" system=\"eq_FK5\"/>");

    // Add SCALIB data header
    buffer.AppendLine(" <RESOURCE name=\"SCALIB calibrators\">");
    buffer.AppendLine("  <TABLE name=\"results\">");
    buffer.AppendLine("   <DESCRIPTION>");
    buffer.AppendLine("    Calibrators for the ");
    //buffer.AppendString(science object name);
    buffer.AppendString(" science objetc");
    buffer.AppendLine("   </DESCRIPTION>");

    // Add all stars list column informations
    mcsSTRING16        tmp;
    mcsUINT32          i         = 0;
    vobsSTAR*          firstStar = GetNextStar(mcsTRUE);
    vobsSTAR_PROPERTY* property  = firstStar->GetNextProperty(mcsTRUE);
    while (property != NULL)
    {
        // Add standard field header
        buffer.AppendLine("   <FIELD");

        // Add field name
        buffer.AppendString(" name=\"");
        buffer.AppendString(property->GetName());
        buffer.AppendString("\"");

        // Add field ID
        buffer.AppendString(" ID=\"");
        sprintf(tmp, "col%d", i);
        buffer.AppendString(tmp);
        buffer.AppendString("\"");

        // Add field ucd
        buffer.AppendString(" ucd=\"");
        buffer.AppendString(property->GetId());
        buffer.AppendString("\"");
/*
        // Add field ref
        buffer.AppendString(" ref=\"");
        //buffer.AppendString(property->XXX);
        buffer.AppendString("\"");
*/
        // Add field datatype
        buffer.AppendString(" datatype=\"");
        switch (property->GetType())
        {
            case vobsSTRING_PROPERTY:
                buffer.AppendString("char\" arraysize=\"8*");
                break;

            case vobsFLOAT_PROPERTY:
                buffer.AppendString("float");
                break;

            default:
                // Assertion - unknow type
                break;
        }
        buffer.AppendString("\"");
/*
        // Add field width
        buffer.AppendString(" width=\"");
        //buffer.AppendString(property->XXX);
        buffer.AppendString("\"");

        // Add field precision
        buffer.AppendString(" precision=\"");
        //buffer.AppendString(property->XXX);
        buffer.AppendString("\"");

        // Add field unit
        buffer.AppendString(" unit=\"");
        //buffer.AppendString(property->XXX);
        buffer.AppendString("\"");
*/
        // Add standard row footer
        buffer.AppendString("/>");

        // Retrieve the next property
        property = firstStar->GetNextProperty(mcsFALSE);
        i++;
    }

    buffer.AppendLine("   <DATA>");
    buffer.AppendLine("    <TABLEDATA>");

    // Save each property value of all the stars of the list
    std::list<vobsSTAR *>::iterator star;
    for (star=_starList.begin(); star != _starList.end(); star++)
    {
        // Add standard row header
        buffer.AppendLine("     <TR>");

        mcsLOGICAL init = mcsTRUE;
        vobsSTAR_PROPERTY* property = NULL;
        while((property = (*star)->GetNextProperty(init)) != NULL)
        {
            init = mcsFALSE;

            // Add standard column header
            buffer.AppendLine("      <TD>");

            buffer.AppendString(property->GetValue());

            // Add standard column footer
            buffer.AppendLine("      </TD>");
        }

        // Add standard row footer
        buffer.AppendLine("     </TR>");
    }

    // Add SCALIB data footer
    buffer.AppendLine("    </TABLEDATA>");
    buffer.AppendLine("   </DATA>");
    buffer.AppendLine("  </TABLE>");
    buffer.AppendLine(" </RESOURCE>");

    // Add VOTable standard footer
    buffer.AppendLine("</VOTABLE>");

    // Try to save the generated VOTable in the specified file
    return(buffer.SaveInFile(filename));
}

/**
 * Save the elements (stars) of the list in a file.
 *
 * @param filename the file where to save
 * @param extendedFormat if true, each property is saved with its attributes
 * (origin and confidence index), otherwise only only property is saved.
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_LIST::Save(const char *filename,
                                  mcsLOGICAL extendedFormat)
{
    logTrace("vobsSTAR_LIST::Save()");

    vobsSTAR_PROPERTY_ID_LIST ucdList;
    
    return Save(filename, ucdList, extendedFormat);
}


/**
 * Save the elements (stars) of the list in a file.
 *
 * @param filename the file where to save
 * @param ucdList list of ucd to save
 * @param extendedFormat if true, each property is saved with its attributes
 * (origin and confidence index), otherwise only only property is saved.
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_LIST::Save(const char *filename,
                                  vobsSTAR_PROPERTY_ID_LIST ucdList,
                                  mcsLOGICAL extendedFormat)
{
    logTrace("vobsSTAR_LIST::Save()");

    // Store list into the CDATA
    vobsCDATA cData;
    vobsSTAR  star;
    if (cData.Store(star, *this, ucdList, extendedFormat) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Save into file
    if (cData.SaveInFile(filename) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Load elements (stars) from a file.
 *
 * @param filename name of file containing star list
 * @param extendedFormat if true, each property is has been saved with its
 * attributes (origin and confidence index), otherwise only only property has
 * been saved.
 * @param origin used if origin is not given in file (see above). If NULL, the
 * name of file is used as origin.
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_LIST::Load(const char *filename,
                                  mcsLOGICAL extendedFormat,
                                  const char *origin)
{
    logTrace("vobsSTAR_LIST::Load()");

    // Load file
    vobsCDATA cData;
    if (cData.LoadFile(filename) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set origin (if needed)
    if (extendedFormat == mcsFALSE)
    {
        // if origin is unknown, set catalog name as the file in which the data
        // had been got
        if (origin == NULL)
        {
            if (cData.SetCatalogName(filename) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
        // if origin is kniwn, set catalog name as the catalog in which the data
        // had been got
        else
        {
            if (cData.SetCatalogName(origin) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
    }
        
    // Extract list from the CDATA
    vobsSTAR  star;
    if (cData.Extract(star, *this, extendedFormat) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}
/*___oOo___*/
