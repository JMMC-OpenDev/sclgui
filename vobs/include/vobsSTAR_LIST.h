#ifndef vobsSTAR_LIST_H
#define vobsSTAR_LIST_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/* 
 * System Headers 
 */
#include<list>

/*
 * MCS Headers 
 */
#include "miscoDYN_BUF.h"

/*
 * Local Headers 
 */
#include"vobsSTAR.h"

/*
 * Type declaration
 */
/**
 * typedef of UCD order list
 */
typedef std::list<std::string> vobsSTAR_PROPERTY_ID_LIST;


/*
 * Class declaration
 */
/**
 * vobsSTAR_LIST handles a list of stars.
 */
class vobsSTAR_LIST
{ 
 public:
    // Class constructors
    vobsSTAR_LIST();
    
    // Class destructor
    virtual ~vobsSTAR_LIST();
    
    virtual void          SetFreeStarPointers(const bool freeStarPtrs);
   
    virtual mcsLOGICAL    IsEmpty(void);
    virtual mcsCOMPL_STAT Clear(void);
    virtual mcsCOMPL_STAT AddAtTail(vobsSTAR &star);
    virtual mcsCOMPL_STAT AddRefAtTail(vobsSTAR* star);
    virtual mcsCOMPL_STAT Remove(vobsSTAR &star);
    virtual mcsUINT32     Size(void);
    // Copy means AddAll:
    virtual mcsCOMPL_STAT Copy(vobsSTAR_LIST& list);
    virtual mcsCOMPL_STAT CopyRefs(vobsSTAR_LIST& list);

    /**
     * Return the next star in the list.
     *
     * This method returns the pointer to the next star of the list. If @em
     * init is mcsTRUE, it returns the first star of the list.
     * 
     * This method can be used to move forward in the list, as shown below:
     * @code
     * for (unsigned int el = 0; el < starList.Size(); el++)
     * {
     *     starList.GetNextStar((mcsLOGICAL)(el==0))->View();
     * }
     * @endcode
     *
     * @return pointer to the next element of the list or NULL if the end of the
     * list is reached.
     */
    inline vobsSTAR* GetNextStar(mcsLOGICAL init = mcsFALSE) 
    {
        if ((init == mcsTRUE) || _starIterator == _starList.end())
        {
            _starIterator = _starList.begin();
        }
        else
        {
            _starIterator++;
        }

        if (_starIterator == _starList.end())
        {
            return NULL;
        }

        return (*_starIterator);
    }
    
    // note: not virtual for iteration performance
    vobsSTAR* GetStar(vobsSTAR &star);
    vobsSTAR* GetStar(vobsSTAR &star, vobsSTAR_COMP_CRITERIA_LIST *criteriaList);
    
    virtual mcsCOMPL_STAT Merge(vobsSTAR_LIST &list,
                                vobsSTAR_COMP_CRITERIA_LIST *criteriaList=NULL, 
                                mcsLOGICAL updateOnly=mcsFALSE);

    virtual mcsCOMPL_STAT Sort(const char *propertyId,
                               mcsLOGICAL reverseOrder=mcsFALSE);

    virtual void          Display(void);

    virtual mcsCOMPL_STAT GetVOTable(const char*    header,
                                     const char*    softwareVersion,
                                     const char*    request,
                                     const char*    xmlRquest,
                                     miscoDYN_BUF*  buffer);

    virtual mcsCOMPL_STAT SaveToVOTable(const char *filename,
                                        const char *header,
                                        const char *softwareVersion,
                                        const char *request,
                                        const char *xmlRequest);

    virtual mcsCOMPL_STAT Save(const char *filename,
                               mcsLOGICAL extendedFormat=mcsFALSE);

    virtual mcsCOMPL_STAT Save(const char *filename,
                               vobsSTAR_PROPERTY_ID_LIST ucdList,
                               mcsLOGICAL extendedFormat=mcsFALSE); 
    virtual mcsCOMPL_STAT Load(const char *filename,
                               mcsLOGICAL extendedFormat=mcsFALSE,
                               const char *origin=NULL); 

protected:
    // flag to indicate to free star pointers or not (shadow copy) 
    bool                            _freeStarPtrs;
    // List of stars
    std::list<vobsSTAR *>           _starList;
    std::list<vobsSTAR *>::iterator _starIterator;

private:
    // Declaration assignment operator as private
    // methods, in order to hide them from the users.
    vobsSTAR_LIST& operator=(const vobsSTAR_LIST&);
    vobsSTAR_LIST (const vobsSTAR_LIST& list);//copy constructor
};

#endif /*!vobSTAR_LIST_H*/


/*___oOo___*/
