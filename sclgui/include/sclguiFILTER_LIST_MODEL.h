#ifndef sclguiFILTER_LIST_MODEL_H
#define sclguiFILTER_LIST_MODEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiFILTER_LIST_MODEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "fnd.h"


/*
 * SCALIB header
 */
#include "vobs.h"
#include "sclsvr.h"


/*
 * Class declaration
 */
/**
 * Model class of the filter list.
 * 
 * This class derive from the fndMVC_MODEL and from the vobsFILTER_LIST
 */
class sclguiFILTER_LIST_MODEL : public fndMVC_MODEL, public vobsFILTER_LIST
{
public:
    // Class constructor
    sclguiFILTER_LIST_MODEL(const char* filterId);

    // Class destructor
    virtual ~sclguiFILTER_LIST_MODEL();

    virtual mcsCOMPL_STAT Init();
    
    mcsCOMPL_STAT ResetFilters(void);

    mcsCOMPL_STAT SetFilterMagnitude        (mcsSTRING32       band="",
                                             mcsFLOAT          value=0,
                                             mcsFLOAT          range=0,
                                             mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT SetFilterDistance         (mcsSTRING32       raValue="",
                                             mcsSTRING32       decValue="",
                                             mcsFLOAT          raRange=0,
                                             mcsFLOAT          decRange=0,
                                             mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT SetFilterVisibility       (mcsFLOAT          visMax=1,
                                             mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT SetFilterTemperatureClass (std::list<char *> tempClassList,
                                             mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT SetFilterLuminosityClass  (std::list<char *> lumClassList,
                                             mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT SetFilterVariability      (mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT SetFilterMultiplicity     (mcsLOGICAL        enable=mcsFALSE);

    mcsCOMPL_STAT DisableFilter             (mcsSTRING32       name);

    // Apply filters
    inline mcsCOMPL_STAT Apply(vobsSTAR_LIST &list)
    {
        return vobsFILTER_LIST::Apply(&list);
    }

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiFILTER_LIST_MODEL(const sclguiFILTER_LIST_MODEL&);
    sclguiFILTER_LIST_MODEL& operator=(const sclguiFILTER_LIST_MODEL&);

    vobsVARIABILITY_FILTER    _variabilityFilter;
    vobsMULTIPLICITY_FILTER   _multiplicityFilter;
    vobsMAGNITUDE_FILTER      _magnitudeFilter;
    sclsvrVISIBILITY_FILTER   _visibilityFilter;
    vobsDISTANCE_FILTER       _distanceFilter;
    vobsSPECTRAL_TYPE_FILTER  _spectralTypeFilter;
    vobsLUMINOSITY_FILTER     _luminosityFilter;
};

#endif /*!sclguiFILTER_LIST_MODEL_H*/

/*___oOo___*/
