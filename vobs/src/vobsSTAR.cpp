/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsSTAR.cpp,v 1.13 2004-12-22 10:08:27 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    22-Jul-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * vobsSTAR class definition.
 */


static char *rcsId="@(#) $Id: vobsSTAR.cpp,v 1.13 2004-12-22 10:08:27 scetre Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <math.h>
#include <iostream>
using namespace std;  
/**
 * Export standard iostream objects (cin, cout,...).
 */


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "vobsSTAR.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"

/*
 * Class constructor
 */

/**
 * Build a star object.
 */
vobsSTAR::vobsSTAR()
{
    // Add all star properties 
    AddProperties();
    _propertyListIterator=_propertyList.begin();
    _propertyOrderIterator=_propertyOrder.begin();
}

/**
 * Build a star object from another one (copy constructor).
 */
vobsSTAR::vobsSTAR(vobsSTAR &star)
{
    _propertyList = star._propertyList;
}

/*
 * Class destructor
 */

/** Delete a star object. */
vobsSTAR::~vobsSTAR()
{
}

/*
 * Public methods
 */

/**
 * Set a star property value.
 *
 * Set value corresponding to the given property id
 * 
 * \param id property id. 
 * \param value property value to set
 * \param overwrite booleen to know if it is an overwrite property 
 *
 * \return SUCCESS on successful completion. Otherwise FAILURE is returned.
 *
 * \b Error codes:\n
 * The possible errors are :
 * \li vobsERR_INVALID_PROPERTY_ID
 */
mcsCOMPL_STAT vobsSTAR::SetPropertyValue(char *id, char *value, 
                                         mcsLOGICAL overwrite)
{
    logExtDbg("vobsSTAR::SetPropertyValue()");

    // Look for property
    map<string, vobsSTAR_PROPERTY> ::iterator propertyIter;
    propertyIter = _propertyList.find(id);
    // If found
    if (propertyIter != _propertyList.end())
    {
        // Set property
        propertyIter->second.SetValue(value, overwrite);
        return SUCCESS;
    }
    // Else
    else
    {
        // Return error
        errAdd(vobsERR_INVALID_PROPERTY_ID, id);
        return FAILURE;
    }
    // End if
}

/**
 * Set a star property.
 *
 * Set value corresponding to the given property id
 * 
 * \param id property id. 
 * \param value property value to set
 * \param overwrite booleen to know if it is an overwrite property 
 *
 * \return SUCCESS on successful completion. Otherwise FAILURE is returned.
 *
 * \b Error codes:\n
 * The possible errors are :
 * \li vobsERR_INVALID_PROPERTY_ID
 */
mcsCOMPL_STAT vobsSTAR::SetPropertyValue(char *id, float value, 
                                         mcsLOGICAL overwrite)
{
    logExtDbg("vobsSTAR::SetPropertyValue()");

    // Look for property
    map<string, vobsSTAR_PROPERTY> ::iterator propertyIter;
    propertyIter = _propertyList.find(id);
    // If found
    if (propertyIter != _propertyList.end())
    {
        // Set property
        propertyIter->second.SetValue(value, overwrite);
        return SUCCESS;
    }
    // Else
    else
    {
        // Return error
        errAdd(vobsERR_INVALID_PROPERTY_ID, id);
        return FAILURE;
    }
    // End if
}

/**
 * Get a star property.
 *
 * Set property value corresponding to the UCD
 * 
 * \param id property id. 
 * \param value property value to set
 * \param overwrite booleen to know if it is an overwrite property 
 *
 * \return pointer to the found star property object on successful completion.
 * Otherwise NULL is returned.
 *
 * \b Error codes:\n
 * The possible errors are :
 * \li vobsERR_INVALID_PROPERTY_ID
 */
vobsSTAR_PROPERTY *vobsSTAR::GetProperty(char *id)
{
    logExtDbg("vobsSTAR::GetProperty()");

    // Look for property
    map<string, vobsSTAR_PROPERTY> ::iterator propertyIter;
    propertyIter = _propertyList.find(id);
    // If found
    if (propertyIter != _propertyList.end())
    {
        // Return property
        return &propertyIter->second;
    }
    // Else
    else
    {
        // Return error
        errAdd(vobsERR_INVALID_PROPERTY_ID, id);
        return NULL;
    }
    // End if
}

/**
 * Return the next property in the list
 *
 * This method returns the pointer to the next element of the list. If \em
 * init is mcsTRUE, it returns the first element of the list.
 *
 * This method can be used to move forward in the list, as shown below:
 * \code
 *     for (unsigned int el = 0; el < star.NbProperties(); el++)
 *     {
 *         printf("%s",star.GetNextProperty((mcsLOGICAL)(el==0))->GetName());
 *     }
 * \endcode
 * 
 * \return pointer to the next element of the list or NULL if the end of the
 * list is reached.
 */
vobsSTAR_PROPERTY *vobsSTAR::GetNextProperty(mcsLOGICAL init)
{
    logExtDbg("vobsSTAR::GetNextProperty()");
    if (init == mcsTRUE)
    {
        _propertyOrderIterator=_propertyOrder.begin();
    }
    else
    {
        _propertyOrderIterator++;
        if ( _propertyOrderIterator == _propertyOrder.end())
        {
            return NULL;
        }
    }
    _propertyListIterator = _propertyList.find(_propertyOrderIterator->second);
    
    return (&(_propertyListIterator->second));
}


/**
 * Get a star property.
 *
 * Get value as string corresponding to the given property id.
 * 
 * \param id property id. 
 *
 * \return pointer to the found star property value on successful completion.
 * Otherwise NULL is returned.
 */
const char *vobsSTAR::GetPropertyValue(char *id)
{
    logExtDbg("vobsSTAR::GetProperty()");

    // Look for property
    vobsSTAR_PROPERTY *property;
    property = GetProperty(id);
    // If found
    if (property != NULL)
    {
        // Return property
        return (property->GetValue());
    }
    // Else
    else
    {
        // Return error
        return NULL;
    }
    // End if
}

/**
 * Get a star property.
 *
 * Get value as string corresponding to the given property id.
 * 
 * \param id property id. 
 * \param value pointer to store value.
 *
 * \return pointer to the found star property value on successful completion.
 * Otherwise NULL is returned.
 */
mcsCOMPL_STAT vobsSTAR::GetPropertyValue(char *id, mcsFLOAT *value)
{
    logExtDbg("vobsSTAR::GetProperty()");

    // Look for property
    vobsSTAR_PROPERTY *property;
    property = GetProperty(id);
    // If found
    if (property != NULL)
    {
        // Return property
        return (property->GetValue(value));
    }
    // Else
    else
    {
        // Return error
        return FAILURE;
    }
    // End if
}

/**
 * Check whether the property is set or not.  
 * 
 * \param id property id. 
 * 
 * \warning
 * If the given property id is unknown, this method returns false (i.e.
 * mcsFALSE)
 *
 * \return
 * True value (i.e. mcsTRUE) if the the property has been set, false (i.e.
 * mcsFALSE) otherwise.
 */
mcsLOGICAL vobsSTAR::IsPropertySet(char *id)
{
    logExtDbg("vobsSTAR::IsPropertySet()");

    // Look for property
    vobsSTAR_PROPERTY *property;
    property = GetProperty(id);
    // If found
    if (property != NULL)
    {
        // Return property
        return (property->IsSet());
    }
    // Else
    else
    {
        // Return error
        return mcsFALSE;
    }
    // End if
}

/**
 * Get right ascension (ra) in arcseconds.
 *
 * \param ra pointer right ascension.
 *
 * \return SUCCESS on successful completion. Otherwise FAILURE is returned.
 *
 */
mcsCOMPL_STAT vobsSTAR::GetRa(float &ra)
{
    logExtDbg("vobsSTAR::GetRa()");
    
    mcsSTRING64 raHms;
    float hh,hm,hs; 

    if (IsPropertySet(vobsSTAR_POS_EQ_RA_MAIN) == mcsFALSE)
    {
        // Return error
        errAdd(vobsERR_RA_NOT_SET);
        return FAILURE;
    }
    strcpy(raHms, GetPropertyValue(vobsSTAR_POS_EQ_RA_MAIN));
    
    if (sscanf(raHms, "%f %f %f", &hh, &hm, &hs) != 3)
    {
        errAdd(vobsERR_INVALID_RA_FORMAT, raHms);
        return FAILURE;
    }

    // Convert to degrees
    ra  = (hh + hm/60.0 + hs/3600.0)*15.0;

    // Set angle range [-180 - 180]
    if (ra > 180)
    {
        ra=-(360-ra);
    }
    
    return SUCCESS;
}

/**
 * Get declinaison (dec) in arcseconds.
 *
 * \param dec declinaison.
 *
 * \return SUCCESS on successful completion. Otherwise FAILURE is returned.
 *
 */
mcsCOMPL_STAT vobsSTAR::GetDec(float &dec)
{
    logExtDbg("vobsSTAR::GetDec()");
    
    const char *decDms;
    float dd,dm,ds; 

    // Get DEC  property
    vobsSTAR_PROPERTY *property;
    property = GetProperty(vobsSTAR_POS_EQ_DEC_MAIN);
    if (property == NULL)
    {
        return FAILURE;
    }

    // If DEC has been set
    if (property->IsSet() == mcsFALSE)
    {
        // Return error
        errAdd(vobsERR_RA_NOT_SET);
        return FAILURE;
    }
    // End if

    // Get hours, min and sec 
    decDms = property->GetValue();
    if (sscanf(decDms, "%f %f %f", &dd, &dm, &ds) != 3)
    {
        errAdd(vobsERR_INVALID_DEC_FORMAT, decDms);
        return FAILURE;
    }

    // Convert to degrees
    dec = dd + ((dm + ds / 60) / 60);
    if (dec>180)
    {
        dec=-(360-dec);
    }
   
    return SUCCESS;
}

/**
 * Test if this star is the same than another one.
 *
 * Check that the star coordinates are the same.
 *
 * \param star the other star.
 * \param criteriaList the list of comparaison criteria
 *
 * \return TRUE if star positions are the same. Otherwise FALSE is returned.
 */
mcsLOGICAL vobsSTAR::IsSame(vobsSTAR &star,
                            vobsSTAR_COMP_CRITERIA_LIST *criteriaList)
{
    logExtDbg("vobsSTAR::IsSame(0x%x)", criteriaList);

    if (criteriaList == NULL)
    {
        mcsFLOAT ra1, ra2, dec1, dec2;
        // Get right ascension of the star. If not set return FALSE
        if (GetRa(ra1) == FAILURE)
        {
            errResetStack();
            return mcsFALSE;                
        }
        if (star.GetRa(ra2) == FAILURE)
        {
            errResetStack();
            return mcsFALSE;                
        }
        // Get declinaison of the star. If not set return FALSE
        if (GetDec(dec1) == FAILURE)
        {
            errResetStack();
            return mcsFALSE;                
        }
        if (star.GetDec(dec2) == FAILURE)
        {
            errResetStack();
            return mcsFALSE;                
        }
        // Compare coordinates
        if ((ra1 == ra2) && (dec1==dec2))
        {
            return mcsTRUE;
        }
        else
        {
            return mcsFALSE;
        }
    }
    // if criteria list is not empty
    else 
    {
        mcsSTRING64 propertyId;
        mcsFLOAT range;
        int listSize=criteriaList->Size();
        logTest("criteriaSize = %d", listSize);
        mcsFLOAT val1, val2;    
        for (int el = 0; el < listSize; el++)
        {
            criteriaList->GetNextCriteria(propertyId,
                                          &range,
                                          (mcsLOGICAL)(el==0));
            if (strcmp(propertyId, vobsSTAR_POS_EQ_RA_MAIN) == 0)
            {
                // Get right ascension of the star. If not set return FALSE
                if (GetRa(val1) == FAILURE)
                {
                    errResetStack();
                    return mcsFALSE;                
                }
                if (star.GetRa(val2) == FAILURE)
                {
                    errResetStack();
                    return mcsFALSE;                
                }
            }
            if(strcmp(propertyId, vobsSTAR_POS_EQ_DEC_MAIN) == 0)
            {
                // Get declinaison of the star. If not set return FALSE
                if (GetDec(val1) == FAILURE)
                {
                    errResetStack();
                    return mcsFALSE;                
                }
                if (star.GetDec(val2) == FAILURE)
                {
                    errResetStack();
                    return mcsFALSE;                
                }
            }
            else
            {
                // Get value of the property id
                if (GetPropertyValue(propertyId, &val1) == FAILURE)
                {
                    errResetStack();
                    return mcsFALSE;
                }
                if (star.GetPropertyValue(propertyId, &val2) == FAILURE)
                {
                    errResetStack();
                    return mcsFALSE;
                }     
            }
            if (fabs(val1-val2)>range)
            {
                return mcsFALSE;
            }
        }
        return mcsTRUE;
    }
}

/**
 * Update a star from another one.
 *
 * \param star the other star.
 *
 * \return TRUE on successful completion. Otherwise FALSE is returned.
 *
 */
mcsCOMPL_STAT vobsSTAR::Update (vobsSTAR &star)
{
    logExtDbg("vobsSTAR::Update()");
    
    // For each star property
    map<string, vobsSTAR_PROPERTY > ::iterator propertyIter;
    for (propertyIter  = _propertyList.begin();
         propertyIter != _propertyList.end() ; propertyIter++)
    {
        if (IsPropertySet((char *)(*propertyIter).first.c_str()) == mcsFALSE)
        {
            // Copy property form the given star
            _propertyList[(*propertyIter).first] = 
                star._propertyList[(*propertyIter).first];
        }
    }

    return SUCCESS;
}

/**
 * method to get the number of properties
 *
 * \return the number of properties of the star
 */
mcsINT32 vobsSTAR::NbProperties()
{
    return _propertyList.size();
}

/**
 * Display all star properties on the console.
 * 
 */
void vobsSTAR::Display(void)
{
    logExtDbg("vobsSTAR::Display()");
    map<string, vobsSTAR_PROPERTY > ::iterator propertyIter;
    
    for (propertyIter  = _propertyList.begin();
         propertyIter != _propertyList.end();
         propertyIter++)
    {
         printf("%12s", (*propertyIter).second.GetValue());
    }
    printf("\n");

}

/*
 * Protected methods
 */

/**
 * Add a star property  
 *
 * \param id     property identifier
 * \param name   property name 
 * \param type   property type
 * \param format format used to set property
 *
 * \return SUCCESS on successful completion. Otherwise FAILURE is returned.
 *
 * \b Error codes:\n
 * The possible error is :
 * \li vobsERR_DUPLICATED_PROPERTY
 */
mcsCOMPL_STAT vobsSTAR::AddProperty(char *id, char *name,
                                    vobsPROPERTY_TYPE type, char *format)
{
    //logExtDbg("vobsSTAR::AddProperty()");

    // Check for duplicated property
    if (_propertyList.find(id) != _propertyList.end())
    {
        errAdd(vobsERR_DUPLICATED_PROPERTY, id);
        return FAILURE;
    }

    //
    vobsSTAR_PROPERTY property(id, name, type, format);
    _propertyList[id] = property;
    return SUCCESS;
}

/**
 * Add all star properties 
 *
 * \return SUCCESS 
 */
mcsCOMPL_STAT vobsSTAR::AddProperties(void)
{
    //logExtDbg("vobsSTAR::AddProperties()");
    AddProperty(vobsSTAR_ID_MAIN, "hd", vobsSTRING_PROPERTY);
    _propertyOrder[0]=vobsSTAR_ID_MAIN;
    AddProperty(vobsSTAR_ID_ALTERNATIVE, "hip", vobsSTRING_PROPERTY);
    _propertyOrder[1]=vobsSTAR_ID_ALTERNATIVE;
    AddProperty(vobsSTAR_POS_EQ_RA_MAIN, "ra", vobsSTRING_PROPERTY);
    _propertyOrder[2]=vobsSTAR_POS_EQ_RA_MAIN;
    AddProperty(vobsSTAR_POS_EQ_DEC_MAIN, "dec", vobsSTRING_PROPERTY);
    _propertyOrder[3]=vobsSTAR_POS_EQ_DEC_MAIN;
    AddProperty(vobsSTAR_POS_EQ_PMDEC, "pmdec", vobsSTRING_PROPERTY);
    _propertyOrder[4]=vobsSTAR_POS_EQ_PMDEC;
    AddProperty(vobsSTAR_POS_EQ_PMRA, "pmra", vobsSTRING_PROPERTY);
    _propertyOrder[5]=vobsSTAR_POS_EQ_PMRA;
    AddProperty(vobsSTAR_POS_PARLX_TRIG, "plx", vobsFLOAT_PROPERTY);
    _propertyOrder[6]=vobsSTAR_POS_PARLX_TRIG;
    AddProperty(vobsSTAR_SPECT_TYPE_MK, "tsp", vobsSTRING_PROPERTY);
    _propertyOrder[7]=vobsSTAR_SPECT_TYPE_MK;
    AddProperty(vobsSTAR_CODE_VARIAB, "varflag", vobsSTRING_PROPERTY);
    _propertyOrder[8]=vobsSTAR_CODE_VARIAB;
    AddProperty(vobsSTAR_CODE_MULT_FLAG, "multflag", vobsSTRING_PROPERTY);
    _propertyOrder[9]=vobsSTAR_CODE_MULT_FLAG;
    AddProperty(vobsSTAR_POS_GAL_LAT, "glat", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[10]=vobsSTAR_POS_GAL_LAT;
    AddProperty(vobsSTAR_POS_GAL_LON, "glon", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[11]=vobsSTAR_POS_GAL_LON;
    AddProperty(vobsSTAR_VELOC_HC, "radvel", vobsSTRING_PROPERTY);
    _propertyOrder[12]=vobsSTAR_VELOC_HC;
    AddProperty(vobsSTAR_EXTENSION_DIAM, "diam", vobsSTRING_PROPERTY);
    _propertyOrder[13]=vobsSTAR_EXTENSION_DIAM;
    AddProperty(vobsSTAR_OBS_METHOD, "meth", vobsSTRING_PROPERTY);
    _propertyOrder[14]=vobsSTAR_OBS_METHOD;
    AddProperty(vobsSTAR_INST_WAVELENGTH_VALUE, "wlen", vobsFLOAT_PROPERTY);
    _propertyOrder[15]=vobsSTAR_INST_WAVELENGTH_VALUE;
    AddProperty(vobsSTAR_PHOT_FLUX_IR_MISC, "photflux", vobsSTRING_PROPERTY);
    _propertyOrder[16]=vobsSTAR_PHOT_FLUX_IR_MISC;
    AddProperty(vobsSTAR_UNITS, "units", vobsSTRING_PROPERTY);
    _propertyOrder[17]=vobsSTAR_UNITS;
    AddProperty(vobsSTAR_PHOT_JHN_U, "U", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[18]=vobsSTAR_PHOT_JHN_U;
    AddProperty(vobsSTAR_PHOT_JHN_B, "B", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[19]=vobsSTAR_PHOT_JHN_B;
    AddProperty(vobsSTAR_PHOT_JHN_V, "V", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[20]=vobsSTAR_PHOT_JHN_V;
    AddProperty(vobsSTAR_PHOT_JHN_R, "R", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[21]=vobsSTAR_PHOT_JHN_R;
    AddProperty(vobsSTAR_PHOT_JHN_I, "I", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[22]=vobsSTAR_PHOT_JHN_I;
    AddProperty(vobsSTAR_PHOT_JHN_J, "J", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[23]=vobsSTAR_PHOT_JHN_J;
    AddProperty(vobsSTAR_PHOT_JHN_H, "H", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[24]=vobsSTAR_PHOT_JHN_H;
    AddProperty(vobsSTAR_PHOT_JHN_K, "K", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[25]=vobsSTAR_PHOT_JHN_K;
    AddProperty(vobsSTAR_PHOT_JHN_L, "L", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[26]=vobsSTAR_PHOT_JHN_L;
    AddProperty(vobsSTAR_PHOT_JHN_M, "M", vobsFLOAT_PROPERTY, "%.3f");
    _propertyOrder[27]=vobsSTAR_PHOT_JHN_M;
    AddProperty(vobsSTAR_PHOT_IR_N_10_4, "N", vobsSTRING_PROPERTY);
    _propertyOrder[28]=vobsSTAR_PHOT_IR_N_10_4;
    AddProperty(vobsSTAR_VELOC_ROTAT, "velocrotat", vobsSTRING_PROPERTY);
    _propertyOrder[29]=vobsSTAR_VELOC_ROTAT;
    AddProperty(vobsSTAR_PHOT_COLOR_EXCESS, "color", vobsSTRING_PROPERTY);
    _propertyOrder[30]=vobsSTAR_PHOT_COLOR_EXCESS;

    return SUCCESS;
}


/*___oOo___*/
