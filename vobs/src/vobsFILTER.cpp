/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of vobsFILTER class.
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

/*
 * Local Headers 
 */
#include "vobsFILTER.h"
#include "vobsPrivate.h"

/**
 * Class constructor
 * 
 * @param filterId a pointer on an already allocated character buffer.
 */
vobsFILTER::vobsFILTER(const char* filterId)
{
    // Store the filter Id
    strcpy(_id, filterId);

    // Disable the filter by default
    Disable();
}

/**
 * Class destructor
 */
vobsFILTER::~vobsFILTER()
{
}

/*
 * Public methods
 */
/**
 * Return whether the filter is enable or not
 *
 * @return mcsTRUE if the filter is enable, mcsFALSE otherwise.
 */
mcsLOGICAL vobsFILTER::IsEnabled(void)
{
    logTrace("vobsFILTER::IsEnabled()");

    return _isEnable;
}

/**
 * Enable the filter
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT vobsFILTER::Enable(void)
{
    logTrace("vobsFILTER::Enable()");
    
    _isEnable = mcsTRUE;
    
    return mcsSUCCESS;
}

/**
 * Disable the filter
 *
 * @return always mcsSUCCESS 
 */
mcsCOMPL_STAT vobsFILTER::Disable(void)
{
    logTrace("vobsFILTER::Disable()");

    _isEnable = mcsFALSE;
    
    return mcsSUCCESS;
}


/**
 * Get the filter Id.
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
 */
const char * vobsFILTER::GetId(void)
{
    logTrace("vobsFILTER::GetId()");

    return _id;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
