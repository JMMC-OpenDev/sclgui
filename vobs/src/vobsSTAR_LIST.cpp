/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * vobsSTAR_LIST class definition.
 */
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
#include "vobsVOTABLE.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"


/**
 * Class constructor
 */
vobsSTAR_LIST::vobsSTAR_LIST()
{
    // this list must by default free star pointers:
    _freeStarPtrs = true;
    
    _starIterator = _starList.end();
}

/**
 * Destructor
 */
vobsSTAR_LIST::~vobsSTAR_LIST()
{
    Clear();
}

/**
 * Enable the flag to indicate to free star pointers or not (shadow copy)
 */
void vobsSTAR_LIST::SetFreeStarPointers(const bool freeStarPtrs)
{
    // define the free star pointers flag:
    _freeStarPtrs = freeStarPtrs;
}

/*
 * Public methods
 */
/**
 * Copy from a list
 * i.e. Add all elements present in the given list at the end of this list
 *
 * @param list the list to copy
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_LIST::Copy(vobsSTAR_LIST& list)
{
    logTrace("vobsSTAR_LIST::Copy(vobsSTAR_LIST& list)");

    const unsigned int nbStars = list.Size();
    for (unsigned int el = 0; el < nbStars; el++)
    {
        if (AddAtTail(*(list.GetNextStar((mcsLOGICAL)(el==0)))) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }

    return mcsSUCCESS;
}

/**
 * Copy only references from the given list
 * i.e. Add all pointers present in the given list at the end of this list
 * 
 * this list must free pointers (_freeStarPtrs = true)
 * the source list must NOT free pointers (list._freeStarPtrs = false)
 *
 * @param list the list to copy
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsSTAR_LIST::CopyRefs(vobsSTAR_LIST& list)
{
    logDebug("vobsSTAR_LIST::CopyRefs(vobsSTAR_LIST& list)");

    const unsigned int nbStars = list.Size();
    for (unsigned int el = 0; el < nbStars; el++)
    {
        AddRefAtTail(list.GetNextStar((mcsLOGICAL)(el==0)));
    }
    // define _freeStarPtrs for both lists:
    _freeStarPtrs = true;
    list._freeStarPtrs = false;
    
    return mcsSUCCESS;
}

/**
 * Return whether the list is empty or not.  
 *
 * @return mcsTRUE if the number of elements is zero, mcsFALSE otherwise.
 */
mcsLOGICAL vobsSTAR_LIST::IsEmpty(void)
{
    if (_starList.empty() == false)
    {
        return mcsFALSE;
    }

    return mcsTRUE;
}

/**
 * Erase (i.e de-allocate) all elements from the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::Clear(void)
{
    logDebug("vobsSTAR_LIST::Clear: freeStarPtrs= %d", _freeStarPtrs);
    
    if (_freeStarPtrs)
    {
        // Deallocate all objects of the list 
        for (std::list<vobsSTAR*>::iterator iter = _starList.begin(); iter != _starList.end(); iter++)
        {
            delete (*iter);
        }
    }

    // Clear list
    _starList.clear();

    // this list must now (default) free star pointers:
    _freeStarPtrs = true;

    return mcsSUCCESS;
}

/**
 * Add the given element at the end of the list.
 *
 * @param star element to be added to the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::AddAtTail(vobsSTAR &star)
{
    // Put the element in the list
    vobsSTAR *newStar = new vobsSTAR(star);
    _starList.push_back(newStar);

    return mcsSUCCESS;
}

/**
 * Add the given element AS one Reference (= pointer only) at the end of the list
 * i.e. it does not copy the given star
 *
 * @param star element to be added to the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::AddRefAtTail(vobsSTAR* star)
{
    if (star != NULL)
    {
        // Put the reference in the list
        _starList.push_back(star);
    }

    return mcsSUCCESS;
}

/**
 * Remove the given element from the list
 *
 * This method looks for the specified @em star in the list. If found, it
 * remove it. Otherwise, nothing is done.
 *
 * The method vobsSTAR::IsSame() is used to compare the list elements with
 * the specified one.
 *
 * @warning if the list contains more than one instance of the given element,
 * only first occurence isremoved.
 * 
 * @note This method does not conflict with GetNextStar(); i.e. it can be used
 * to remove the star returned by GetNextStar() method, as shown below:
 * @code
 * for (unsigned int el = 0; el < starList.Size(); el++)
 * {
 *     vobsSTAR *star;
 *     star = starList.GetNextStar((mcsLOGICAL)(el==0));
 *     if ( <condition> )
 *     {
 *          // Remove star from list 
 *          starList.Remove(*star);
 *
 *          // and decrease 'el' to take into account the new list size
 *          el--;
 *     }
 * }
 * @endcode

 * @param star to be removed from the list.
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsSTAR_LIST::Remove(vobsSTAR &star)
{
    logTrace("vobsSTAR_LIST::Remove()");

    // Search star in the list
    for (std::list<vobsSTAR*>::iterator iter = _starList.begin(); iter != _starList.end(); iter++)
    {
        // If found
        if ((*iter)->IsSame(star) == mcsTRUE)
        {
            if (_freeStarPtrs)
            {
                // Delete star
                delete (*iter);
            }

            // If star to be deleted correspond to the one currently pointed
            // by GetNextStar method
            if (*_starIterator == *iter)
            {
                // If it is not the first star of the list
                if (iter != _starList.begin())
                {
                    // Then go back to the previous star
                    _starIterator--;
                }
                else
                {
                    // Else set current pointer to end() in order to restart scan
                    // from beginning of the list.
                    _starIterator = _starList.end();
                }
            }
            
            // Clear star from list
            _starList.erase(iter);

            return mcsSUCCESS;
        }
    }

    return mcsSUCCESS;
}

/**
 * Returns the number of stars currently stored in the list.
 *
 * @return The numbers of stars in the list.
 */
mcsUINT32 vobsSTAR_LIST::Size(void) 
{
    return _starList.size();
}

/**
 * Return the star of the list corresponding to the given star.
 *
 * This method looks for the specified @em star in the list. If found, it
 * returns the pointer to this element. Otherwise, NULL is returned.
 *
 * The method vobsSTAR::IsSame() is used to compare element of the list with
 * the specified one.
 * 
 * This method can be used to discover whether a star is in list or not, as
 * shown below:
 * @code
 * if (starList.GetStar(star)->View() == NULL)
 * {
 *     printf ("Star not found in list !!");
 * }
 * @endcode
 *
 * @return pointer to the found element of the list or NULL if element is not
 * found in list.
 */
vobsSTAR* vobsSTAR_LIST::GetStar(vobsSTAR &star)
{
    // Search star in the list
    for (std::list<vobsSTAR*>::iterator iter = _starList.begin(); iter != _starList.end(); iter++)
    {
        if ((*iter)->IsSame(star) == mcsTRUE)
        {
            return (*iter);
        }
    }

    // If nothing found, return NULL pointer
    return NULL;
}

/**
 * Return the star of the list corresponding to the given star.
 *
 * This method looks for the specified @em star in the list. If found, it
 * returns the pointer to this element. Otherwise, NULL is returned.
 *
 * The method vobsSTAR::IsSame() is used to compare element of the list with
 * the specified one.
 * 
 * This method can be used to discover whether a star is in list or not, as
 * shown below:
 * @code
 * if (starList.GetStar(star)->View() == NULL)
 * {
 *     printf ("Star not found in list !!");
 * }
 * @endcode
 *
 * @return pointer to the found element of the list or NULL if element is not
 * found in list.
 */
vobsSTAR* vobsSTAR_LIST::GetStar(vobsSTAR &star, vobsSTAR_COMP_CRITERIA_LIST *criteriaList)
{
    // Search star in the list
    for (std::list<vobsSTAR*>::iterator iter = _starList.begin(); iter != _starList.end(); iter++)
    {
        if ((*iter)->IsSame(star, criteriaList) == mcsTRUE)
        {
            return (*iter);
        }
    }

    // If nothing found, return NULL pointer
    return NULL;
}

/**
 * Merge the specified list.
 *
 * This method merges all stars of the given list with the current one. If a
 * star is already stored in the list, it is just updated using
 * vobsSTAR::Update method, otherwise it is added to the list.
 * 
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned if updating or adding star failed.
 */
mcsCOMPL_STAT vobsSTAR_LIST::Merge(vobsSTAR_LIST &list,
                                   vobsSTAR_COMP_CRITERIA_LIST *criteriaList,
                                   mcsLOGICAL updateOnly)
{
    const unsigned int nbStars = list.Size();
    
    if (nbStars == 0) {
        // nothing to do
        return mcsSUCCESS;
    }
    
    const bool hasCriteria = (criteriaList != NULL);
    
    if (hasCriteria) {
        logTest("vobsSTAR_LIST::Merge() with criteria - list size = %d", Size());

        // Initialize criteria informations:
        if (criteriaList->InitializeCriterias() == mcsFAILURE)
        {
            return mcsFAILURE;
        }
        
        // log criterias:
        criteriaList->log(logTEST);

    } else {
        logTest("vobsSTAR_LIST::Merge() without criteria - list size = %d", Size());
    }
    
    vobsSTAR *starPtr;
    vobsSTAR *starToUpdatePtr;
    
    // For each star of the given list
    const unsigned int step = nbStars / 10;
    const bool logProgress = nbStars > 2000;
    
    for (unsigned int el = 0; el < nbStars; el++)
    {
        if (logProgress && el % step == 0)
        {
            logTest("vobsSTAR_LIST::Merge() - merged stars = %d", el);
        }
        
        starPtr = list.GetNextStar((mcsLOGICAL)(el==0));
        
        // If star is in the list ?
        
        if (hasCriteria)
        {
            starToUpdatePtr = GetStar(*starPtr, criteriaList);
        }
        else
        {
            starToUpdatePtr = GetStar(*starPtr);
        }
        
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
 * vobsSTAR comparison functor
 */
class StarPropertyCompare {
    
    private:
        int                           _propertyIndex;
        const vobsSTAR_PROPERTY_META* _meta;
        bool                          _reverseOrder;
        
        // members:
        const char*                   _propertyId;
        vobsPROPERTY_TYPE             _propertyType;
        bool                          _isRA;
        bool                          _isDEC;
    
    public:
        // Constructor
        StarPropertyCompare(const int propertyIndex, const vobsSTAR_PROPERTY_META* meta, const bool reverseOrder) {

            _propertyIndex = propertyIndex;
            _meta          = meta;
            _reverseOrder  = reverseOrder;
            
            _propertyId    = meta->GetId();
            _propertyType = _meta->GetType();

            // is RA or DEC:
            _isRA  = strcmp(_propertyId, vobsSTAR_POS_EQ_RA_MAIN)  == 0;
            _isDEC = strcmp(_propertyId, vobsSTAR_POS_EQ_DEC_MAIN) == 0;

            logDebug("vobsSTAR_LIST::StarPropertyCompare() - property [%d - %s]", _propertyIndex, _propertyId);
            logDebug("vobsSTAR_LIST::StarPropertyCompare() - property type: %d", _propertyType);
            logDebug("vobsSTAR_LIST::StarPropertyCompare() - isRA  = %d", _isRA);
            logDebug("vobsSTAR_LIST::StarPropertyCompare() - isDEC = %d", _isDEC);
        }

        /**
         * Check if leftStar < rightStar
         */
        bool operator()(vobsSTAR* leftStar, vobsSTAR* rightStar) const
        {
            // Get star properties:
            vobsSTAR_PROPERTY* leftProperty  = leftStar ->GetProperty(_propertyIndex);
            vobsSTAR_PROPERTY* rightProperty = rightStar->GetProperty(_propertyIndex);

            // Check properties are set
            const mcsLOGICAL isValue1Set = leftStar ->IsPropertySet(leftProperty);
            const mcsLOGICAL isValue2Set = rightStar->IsPropertySet(rightProperty);

            // If one of the properties is not set, move it at the begining
            // or at the end, according to the sorting order
            if ((isValue1Set == mcsFALSE) || (isValue2Set == mcsFALSE))
            {
                // If it is normal sorting order
                if (!_reverseOrder) 
                {
                    // blank values are at the end:
                    // If value of next element is not set while previous
                    // one is, swap them
                    return ((isValue2Set == mcsFALSE) && (isValue1Set == mcsTRUE));
                }
                // Else (reverse sorting order)
                else
                {
                    // blanks values are at the beginning:
                    // If value of previous element is not set while next
                    // one is, swap them
                    return ((isValue1Set == mcsFALSE) && (isValue2Set == mcsTRUE));
                }
            }
            // Else (properties are set)
            else
            {
                // Compare element values according to property or property
                // type, and check if elements have to be swapped according
                // to the sorting order

                if ((_propertyType == vobsFLOAT_PROPERTY) || (_isRA) || (_isDEC))
                {
                    mcsDOUBLE value1;
                    mcsDOUBLE value2;
                    
                    if (_isRA)
                    {
                        leftStar ->GetRa(value1);
                        rightStar->GetRa(value2);
                    }
                    else if (_isDEC)
                    {
                        leftStar ->GetDec(value1);
                        rightStar->GetDec(value2);
                    }
                    else
                    {
                        leftStar ->GetPropertyValue(leftProperty,  &value1);
                        rightStar->GetPropertyValue(rightProperty, &value2);
                    }

                    if (!_reverseOrder) 
                    {
                        return value1 < value2;
                    }
                    else
                    {
                        return value2 < value1;
                    }
                }
                else
                {
                    const char* value1 = leftStar ->GetPropertyValue(leftProperty);
                    const char* value2 = rightStar->GetPropertyValue(rightProperty);

                    if (!_reverseOrder) 
                    {
                        return strcmp(value1, value2) < 0;
                    }
                    else
                    {
                        return strcmp(value1, value2) > 0;
                    }
                }
            }
    }
};

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
mcsCOMPL_STAT vobsSTAR_LIST::Sort(const char *propertyId, mcsLOGICAL reverseOrder)
{
    logInfo("vobsSTAR_LIST::Sort()");

    // If list is empty or contains only one element, return
    if (Size() <= 1)
    {
        return mcsSUCCESS;
    }

    // Get property index:
    const int propertyIndex = vobsSTAR::GetPropertyIndex(propertyId);
    if (propertyIndex == -1)
    {
        errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId); 
        return mcsFAILURE;
    }
    
    // Get property meta:
    const vobsSTAR_PROPERTY_META* meta = vobsSTAR::GetPropertyMeta(propertyIndex);
    if (meta == NULL)
    {
        errAdd(vobsERR_INVALID_PROPERTY_ID, propertyId); 
        return mcsFAILURE;
    }

    StarPropertyCompare comp(propertyIndex, meta, reverseOrder == mcsTRUE);
    
    _starList.sort(comp);
    
    logInfo("vobsSTAR_LIST::Sort() - done.");
    
    return mcsSUCCESS;
}

/**
 * Display the list contnent on the console.
 */
void vobsSTAR_LIST::Display(void)
{
    logTrace("vobsSTAR_LIST::Display()");

    // Display all element of the list 
    for (std::list<vobsSTAR*>::iterator iter = _starList.begin(); iter != _starList.end(); iter++)
    {
        (*iter)->Display();
    }

    printf("\n");
}

/**
 * Save each star in a VOTable v1.1.
 *
 * @param header header of the VO Table
 * @param softwareVersion software version
 * @param request user request
 * @param xmlRequest user request as XML
 * @param buffer the buffer in which the VOTable should be written
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT vobsSTAR_LIST::GetVOTable(const char*    header,
                                        const char*    softwareVersion,
                                        const char*    request,
                                        const char*    xmlRequest,
                                        miscoDYN_BUF*  buffer)
{
    logTrace("vobsSTAR_LIST::GetVOTable()");

    vobsVOTABLE serializer;
    return(serializer.GetVotable(*this, NULL, header, softwareVersion, request, xmlRequest, buffer));
}

/**
 * Save each star in a VOTable v1.1.
 *
 * @param filename the path to the file in which the VOTable should be saved
 * @param header header of the VO Table
 * @param softwareVersion software version
 * @param request user request
 * @param xmlRequest user request as XML
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT vobsSTAR_LIST::SaveToVOTable(const char *filename,
                                           const char *header,
                                           const char *softwareVersion,
                                           const char *request,
                                           const char *xmlRequest)
{
    logTrace("vobsSTAR_LIST::SaveToVOTable()");

    vobsVOTABLE serializer;
    return(serializer.Save(*this, filename, header, softwareVersion, request, xmlRequest));
}

/**
 * Save the stars of the list in a file.
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
 * Save the stars of the list in a file.
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

    // Store list into the begining
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
 * Load stars from a file.
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
    vobsSTAR star;
    if (cData.Extract(star, *this, extendedFormat) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    return mcsSUCCESS;
}
/*___oOo___*/
