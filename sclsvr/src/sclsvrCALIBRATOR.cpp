/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrCALIBRATOR.cpp,v 1.29 2005-02-11 10:53:17 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.28  2005/02/10 08:19:29  gzins
 * Added properties for diameter from B-V, V-K and V- R calibration
 *
 * Revision 1.27  2005/02/08 21:36:41  gzins
 * Removed diameter error conversion (% to mas). Done at catalog level.
 *
 * Revision 1.26  2005/02/08 20:54:06  gzins
 * Added 'computed' flag when setting computed star property
 * Updated Pack() and Unpack() to store and retrieve origin and confidence index of star properties
 *
 * Revision 1.25  2005/02/08 04:39:32  gzins
 * Updated for new vobsREQUEST API and used new sclsvrREQUEST class
 *
 * Revision 1.24  2005/02/07 15:00:48  gzins
 * Added CVS log as modification history
 *
 * scetre    14-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * sclsvrCALIBRATOR class definition.
 */

static char *rcsId="@(#) $Id: sclsvrCALIBRATOR.cpp,v 1.29 2005-02-11 10:53:17 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <iostream>

using namespace std;
#include "gdome.h"

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "alx.h"
#include "alxErrors.h"

/*
 * Local Headers 
 */
#include "sclsvrCALIBRATOR.h"
#include "sclsvrPrivate.h"
#include "sclsvrErrors.h"


/**
 * Class constructor.
 *
 * Build a calibrator object.
 */
sclsvrCALIBRATOR::sclsvrCALIBRATOR()
{
    // Add all star properties 
    AddProperties();
}

/*
 * Copy Constructor
 */
sclsvrCALIBRATOR::sclsvrCALIBRATOR(vobsSTAR &star)
{
    (*this).vobsSTAR::operator=(star); 

    // Add all star properties 
    AddProperties(); 
}



/*
 * Class destructor
 */
sclsvrCALIBRATOR::~sclsvrCALIBRATOR()
{
}

/*
 * Public methods
 */

/**
 * Pack the calibrator in a dynamic buffer
 *
 * \param buffer buffer to complete
 *
 * \return always mcsSUCCESS
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::Pack(miscDYN_BUF *buffer)
{
    logExtDbg("sclsvrCALIBRATOR::Pack()");

    mcsSTRING256 str;
    // Copy properties of the star in th buffer
    map<int, string>::iterator iterator;
    for (iterator = _propertyOrder.begin();
         iterator != _propertyOrder.end();
         iterator++)
    {
        // Each star property is placed in buffer in form :
        // 'value origin confidenceIndex'
        sprintf(str, "%s \t%s \t%d ", 
                (char *)_propertyList[(*iterator).second].GetValue(),
                (char *)_propertyList[(*iterator).second].GetOrigin(),
                (char *)_propertyList[(*iterator).second].GetConfidenceIndex());
        miscDynBufAppendString(buffer, str);
        miscDynBufAppendString(buffer, "\t");        
    }
    
    return mcsSUCCESS;
}

/**
 * Unpack the calibrator in a dynamic buffer
 *
 * \param calibratorString the calibrator to unpack
 *
 * \return
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::UnPack(char *calibratorString)
{ 
    logExtDbg("sclsvrCALIBRATOR::UnPack()");
    mcsSTRING256 propertyList[300];

    // Split string into list of properties 
    mcsINT32 nbProperties;
    miscSplitString(calibratorString, '\t', 
                    propertyList, 300, (mcsUINT32 *)&nbProperties);

    // Check the number of found properties 
    if (nbProperties != (NbProperties() * 3))
    {
        errAdd(sclsvrERR_WRONG_BUFFER_FORMAT, calibratorString);
    }
    else
    {
        mcsINT32 propIdx;
        for (_propertyOrderIterator = _propertyOrder.begin(), propIdx = 0;
             _propertyOrderIterator != _propertyOrder.end();
             _propertyOrderIterator++, propIdx++)
        {
            // Retrieve value, origin and confidence index
            char *value, *origin;
            vobsCONFIDENCE_INDEX confidenceIndex;
            value  = propertyList[3*propIdx];
            miscTrimString(value, " ");
            origin = propertyList[3*propIdx+1];
            miscTrimString(origin, " ");
            sscanf(propertyList[3*propIdx+2], "%d", &confidenceIndex);

            // Set the property
            vobsSTAR_PROPERTY *property;
            property = &(_propertyList[(*_propertyOrderIterator).second]);
            property->SetValue(value, origin, confidenceIndex);
        }
    } 

    return mcsSUCCESS;
}


/**
 * Say if the calibrator had a coherent diameter
 */
mcsLOGICAL sclsvrCALIBRATOR::IsDiameterOk()
{
    // If diameter has not been computed
    if (IsPropertySet(sclsvrCALIBRATOR_DIAM_FLAG) == mcsFALSE)
    {
        // Return false
        return mcsFALSE;
    }
    // Else
    else
    {
        // Get the flag, and test it
        const char *flag;
        flag = GetPropertyValue(sclsvrCALIBRATOR_DIAM_FLAG);
        if (strcmp(flag, "OK") == 0)
        {
            return mcsTRUE;
        }
        else
        {
            return mcsFALSE;
        }
    }
    // End if
}

/**
 * Say if the visibility is ok
 */
mcsLOGICAL sclsvrCALIBRATOR::IsVisibilityOk()
{
    // If visibility has not been computed
    if (IsPropertySet(sclsvrCALIBRATOR_VIS2_FLAG) == mcsFALSE)
    {
        // Return false
        return mcsFALSE;
    }
    // Else
    else
    {
        // Get the flag, and test it
        const char *flag;
        flag = GetPropertyValue(sclsvrCALIBRATOR_VIS2_FLAG);
        if (strcmp(flag, "OK") == 0)
        {
            return mcsTRUE;
        }
        else
        {
            return mcsFALSE;
        }
    }
    // End if
}

/**
 * Complete the property of the calibrator
 *
 * Method to complete calibrator properties by using several methods
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::Complete(sclsvrREQUEST &request)
{
    logExtDbg("sclsvrCALIBRATOR::Complete()");
    
    // Get the observed band
    const char *band;
    band = request.GetSearchBand();

    // Compute Galactic coordinates
    if (ComputeGalacticCoordinates() == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // If it is not the scenario for N band
    if (strcmp(band, "N") != 0)
    {
        // If paralax of the star if known
        if (IsPropertySet(vobsSTAR_POS_PARLX_TRIG) == mcsTRUE)
        {
            // Compute Interstellar extinction
            if (ComputeInterstellarAbsorption() == mcsFAILURE)
            {
                return mcsFAILURE;
            }

            // Compute Missing Magnitude
            if (ComputeMissingMagnitude() == mcsFAILURE)
            {
                return mcsFAILURE;
            }

            // Compute Angular Diameter
            if (ComputeAngularDiameter() == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
    }
    else
    {
         SetPropertyValue(sclsvrCALIBRATOR_DIAM_FLAG, "OK", 
                          vobsSTAR_COMPUTED_PROP);
    }

    // Compute visibility and visibility error
    if (ComputeVisibility(request) != mcsFAILURE)
    {
        // If visibility has been computed
        mcsFLOAT computedVis;
        if (IsPropertySet(sclsvrCALIBRATOR_VIS2) == mcsTRUE)
        {
            // Get computed visibility
            GetPropertyValue(sclsvrCALIBRATOR_VIS2, &computedVis);

            // Get expected visibility
            mcsFLOAT expectedVis;
            expectedVis = request.GetExpectedVisibility();
            
            // Check if the computed visibility is greater than the
            // requested one, and the flag accordingly
            if (computedVis < expectedVis)
            {
                SetPropertyValue(sclsvrCALIBRATOR_VIS2_FLAG, "NOK",
                                 vobsSTAR_COMPUTED_PROP);
            }
            else
            {
                SetPropertyValue(sclsvrCALIBRATOR_VIS2_FLAG, "OK",
                                 vobsSTAR_COMPUTED_PROP);
            }
        }
    }

    // compute multiplicity
    if (ComputeMultiplicity() == mcsFAILURE)
    {
        return mcsFAILURE;
    } // end multiplicity

    return mcsSUCCESS;
}


/*
 * Private methods
 */

/**
 * Compute Missing magnitude
 *
 * \return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::ComputeMissingMagnitude()
{
    logExtDbg("sclsvrCALIBRATOR::ComputeMissingMagnitude()");
    alxCONFIDENCE_INDEX confidenceIndex;

    mcsFLOAT mgB, mgV, mgR, mgI, mgJ, mgH, mgK, mgL, mgM;
    mcsSTRING32 spType;
    //Get the value of the Spectral Type
    strcpy(spType, GetPropertyValue(vobsSTAR_SPECT_TYPE_MK));
    // Get the value of the magnitude in the different band
    // if B or V is not present, return mcsFAILURE
    if (IsPropertySet(sclsvrCALIBRATOR_BO) == mcsTRUE)
    {
        if (GetPropertyValue(sclsvrCALIBRATOR_BO, &mgB) == mcsFAILURE)
        {       
            return mcsFAILURE;
        }
    }
    if (IsPropertySet(sclsvrCALIBRATOR_VO) == mcsTRUE)
    {
        if (GetPropertyValue(sclsvrCALIBRATOR_VO, &mgV) == mcsFAILURE)
        {
            return mcsFAILURE;
        }
    }
    // For the other Magnitude, don't return mcsFAILURE
    if (IsPropertySet(sclsvrCALIBRATOR_RO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_RO, &mgR);
    }
    if (IsPropertySet(sclsvrCALIBRATOR_IO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_IO, &mgI);
    }
    if (IsPropertySet(sclsvrCALIBRATOR_JO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_JO, &mgJ);
    }
    if (IsPropertySet(sclsvrCALIBRATOR_HO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_HO, &mgH);
    }
    if (IsPropertySet(sclsvrCALIBRATOR_KO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_KO, &mgK);
    }
    if (IsPropertySet(sclsvrCALIBRATOR_LO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_LO, &mgL);
    }
    if (IsPropertySet(sclsvrCALIBRATOR_MO) == mcsTRUE)
    {
        GetPropertyValue(sclsvrCALIBRATOR_MO, &mgM);
    }
    
    // run alx function to compute magnitude
    if (alxComputeMagnitudesForBrightStar(spType, mgB, mgV, &mgR, &mgI, &mgJ,
                                          &mgH, &mgK, &mgL, &mgM,
                                          &confidenceIndex) == mcsFAILURE)
    {
        if ((errIsInStack("alx", alxERR_SPECTRAL_TYPE_NOT_FOUND) == mcsTRUE) ||
            (errIsInStack("alx", alxERR_WRONG_SPECTRAL_TYPE_FORMAT) == mcsTRUE))
        {
            errResetStack();
            return mcsSUCCESS;
        }
        else
        {
            return mcsFAILURE;
        }
    }
  
    // If property is ever set, do nothing, else write the computed value
    SetPropertyValue(sclsvrCALIBRATOR_RO, mgR, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_IO, mgI, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_JO, mgJ, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_HO, mgH, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_KO, mgK, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_LO, mgL, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_MO, mgM, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    
    return mcsSUCCESS;
}

/**
 * Compute Galactic Coordonate
 *
 * \return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::ComputeGalacticCoordinates()
{
    logExtDbg("sclsvrCALIBRATOR::ComputeGalacticCoordinates()");
    mcsFLOAT gLat, gLon;
    mcsFLOAT ra; 
    mcsFLOAT dec;

    // Get right ascension position in degree
    if (GetRa(ra)==mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Get declinaison in degree
    if (GetDec(dec)==mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Run alxComputeGalacticCoordinates() from alx library
    if (alxComputeGalacticCoordinates(ra, dec, &gLat, &gLon) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Set the galactic lattitude (if not yet set)
    SetPropertyValue(vobsSTAR_POS_GAL_LAT, gLat, vobsSTAR_COMPUTED_PROP);

    // Set the galactic longitude (if not yet set)
    SetPropertyValue(vobsSTAR_POS_GAL_LON, gLon, vobsSTAR_COMPUTED_PROP);

    return mcsSUCCESS;
}
/**
 * Compute Interstellar Absorption
 *
 * \return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::ComputeInterstellarAbsorption()
{
    logExtDbg("sclsvrCALIBRATOR::ComputeInterstellarAbsorption()");

    mcsFLOAT mgB, mgV, mgR, mgI, mgJ, mgH, mgK, mgL, mgM;
    mcsFLOAT paralax;
    mcsFLOAT gLat, gLon;


    mcsFLOAT starProperty[9];
    mcsLOGICAL isPropertySet[9];
    mcsSTRING64 starPropertyId[9] = 
    {
        vobsSTAR_PHOT_JHN_B,
        vobsSTAR_PHOT_JHN_V,
        vobsSTAR_PHOT_JHN_R,
        vobsSTAR_PHOT_JHN_I,
        vobsSTAR_PHOT_JHN_J,
        vobsSTAR_PHOT_JHN_H,
        vobsSTAR_PHOT_JHN_K,
        vobsSTAR_PHOT_JHN_L,
        vobsSTAR_PHOT_JHN_M
    };

    // for each property
    for (int i=0; i<9; i++)
    { 
        if (IsPropertySet(starPropertyId[i]) == mcsTRUE)
        {
            GetPropertyValue(starPropertyId[i], &starProperty[i]);
            isPropertySet[i] = mcsTRUE;
        }
        else
        {
            starProperty[i] = 0;
            isPropertySet[i] = mcsFALSE;
        }
    }
    mgB=starProperty[0];
    mgV=starProperty[1];
    mgR=starProperty[2];
    mgI=starProperty[3];
    mgJ=starProperty[4];
    mgH=starProperty[5];
    mgK=starProperty[6];
    mgL=starProperty[7];
    mgM=starProperty[8];

    // Get the value of the paralax, the galactic coordinates if it exists.
    if (IsPropertySet(vobsSTAR_POS_PARLX_TRIG) == mcsTRUE)
    {
        GetPropertyValue(vobsSTAR_POS_PARLX_TRIG, &paralax);
    }
    else 
    {
        errAdd(sclsvrERR_MISSING_PROPERTY, vobsSTAR_POS_PARLX_TRIG,
               "interstellar absorption");
        return mcsFAILURE;
    }
    if (IsPropertySet(vobsSTAR_POS_GAL_LAT) == mcsTRUE)
    {
        GetPropertyValue(vobsSTAR_POS_GAL_LAT, &gLat);
    }
    else
    {
        errAdd(sclsvrERR_MISSING_PROPERTY, vobsSTAR_POS_GAL_LAT,
               "interstellar absorption");
        return mcsFAILURE;
    }
    if (IsPropertySet(vobsSTAR_POS_GAL_LON) == mcsTRUE)
    {
        GetPropertyValue(vobsSTAR_POS_GAL_LON, &gLon);
    }
    else 
    {
        errAdd(sclsvrERR_MISSING_PROPERTY, vobsSTAR_POS_GAL_LON,
               "interstellar absorption");
        return mcsFAILURE;
    }

    // Run alx function to compute corrected magnitude
    if (alxComputeRealMagnitudes(paralax, gLat, gLon, &mgM, &mgL, &mgK, &mgH,
                                 &mgJ, &mgI, &mgR, &mgV, &mgB) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Print in the calibrators properties the magnitude if they had been
    // computed, else do nothing
    if (isPropertySet[8] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_MO, mgM, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[7] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_LO, mgL, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[6] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_KO, mgK, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[5] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_HO, mgH, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[4] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_JO, mgJ, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[3] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_IO, mgI, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[2] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_RO, mgR, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[1] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_VO, mgV, vobsSTAR_COMPUTED_PROP);
    }
    if (isPropertySet[0] == mcsTRUE)
    {
        SetPropertyValue(sclsvrCALIBRATOR_BO, mgB, vobsSTAR_COMPUTED_PROP);
    }

    return mcsSUCCESS;
}
/**
 * Compute Angular diameter
 *
 * \return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::ComputeAngularDiameter()
{
    logExtDbg("sclsvrCALIBRATOR::ComputeAngularDiameter()");
    mcsFLOAT diamBv, diamVr, diamVk, diamError;
    mcsFLOAT mgB, mgV, mgR, mgK;
    mcsFLOAT starProperty[4];
    alxCONFIDENCE_INDEX confidenceIndex;
    
    char *starPropertyId[4] = 
    {
        sclsvrCALIBRATOR_BO,
        sclsvrCALIBRATOR_VO,
        sclsvrCALIBRATOR_RO,
        sclsvrCALIBRATOR_KO
    };

    // For each property needed to compute angular diameter, check if they are
    // affected
    for (int i=0; i<4; i++)
    { 
        if (IsPropertySet(starPropertyId[i]) == mcsTRUE)
        {
            GetPropertyValue(starPropertyId[i], &starProperty[i]);
        }
        else
        {
            // Do nothing
            return mcsSUCCESS;
        }
    }
    mgB=starProperty[0];
    mgV=starProperty[1];
    mgR=starProperty[2];
    mgK=starProperty[3];
     
    if (alxComputeAngularDiameter(mgB, mgV, mgR, mgK,
                                  &diamBv, &diamVr, &diamVk, &diamError,
                                  &confidenceIndex) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Set compute value of the angular diameter
    SetPropertyValue(sclsvrCALIBRATOR_DIAM_BV, diamBv, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_DIAM_VR, diamVr, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_DIAM_VK, diamVk, vobsSTAR_COMPUTED_PROP,
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
    SetPropertyValue(sclsvrCALIBRATOR_DIAM_ERROR, diamError,
                     vobsSTAR_COMPUTED_PROP, 
                     (vobsCONFIDENCE_INDEX)confidenceIndex);
   
    // Set flag according to the confidence index 
    if (confidenceIndex == alxCONFIDENCE_LOW)
    {
         SetPropertyValue(sclsvrCALIBRATOR_DIAM_FLAG, 
                          "NOK", vobsSTAR_COMPUTED_PROP);
    }
    else
    {
         SetPropertyValue(sclsvrCALIBRATOR_DIAM_FLAG,
                          "OK", vobsSTAR_COMPUTED_PROP);
    }
   
    return mcsSUCCESS;
}
/**
 * Compute Visibility
 *
 * \return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::ComputeVisibility(sclsvrREQUEST &request)
{
    logExtDbg("sclsvrCALIBRATOR::ComputeVisibility()");
    mcsFLOAT diam, diamError;
    mcsFLOAT baseMax, wavelength;
    mcsFLOAT vis, vis2, visErr, vis2Err;

    // Get object diameter. First look at the diameter coming from catalog, and
    // if not found used the computed diameter.
    if (IsPropertySet(vobsSTAR_UD_DIAM) == mcsTRUE)
    {
        GetPropertyValue(vobsSTAR_UD_DIAM, &diam);
        // If diameter error was not found in catalog, it assume to 0  
        if (IsPropertySet(vobsSTAR_UD_DIAM_ERROR) == mcsTRUE)
        {
            GetPropertyValue(vobsSTAR_UD_DIAM_ERROR, &diamError);
        }
        else
        {
            diamError = 0.0;
        }
    }
    else
    {
        // If diameter has been computed 
        if (IsPropertySet(sclsvrCALIBRATOR_DIAM_BV) == mcsTRUE)
        {
            // And it is coherent
            if (IsDiameterOk() == mcsTRUE)
            {
                // Get diameters and associated error values
                mcsFLOAT diamBv, diamVr, diamVk;
                GetPropertyValue(sclsvrCALIBRATOR_DIAM_BV, &diamBv);
                GetPropertyValue(sclsvrCALIBRATOR_DIAM_VR, &diamVr);
                GetPropertyValue(sclsvrCALIBRATOR_DIAM_VK, &diamVk);
                GetPropertyValue(sclsvrCALIBRATOR_DIAM_ERROR, &diamError);

                // Compute mean diameter
                diam = (diamBv + diamVr + diamVk) / 3.0;
            }
            // Else do not compute visibility
            else
            {
                return mcsSUCCESS;
            }
        }
        // Else do not compute visibility
        else
        {
            return mcsSUCCESS;
        }
    }

    // Get value in request of the wavelength
    wavelength = request.GetObservingWlen();

    // Get value in request of the base max
    baseMax = request.GetMaxBaselineLength();
    if (alxComputeVisibility(diam, diamError, baseMax, wavelength,
                             &vis, &vis2, &visErr, &vis2Err)==mcsFAILURE)
    {
        return mcsFAILURE;
    }
    printf("diam = %f (%f) -> vis = %f (%f) vis2 = %f (%f)\n", diam, diamError, vis, visErr, vis2, vis2Err); 
    
    // Affect visibility property
    SetPropertyValue(sclsvrCALIBRATOR_VIS2, vis2, vobsSTAR_COMPUTED_PROP);
    SetPropertyValue(sclsvrCALIBRATOR_VIS2_ERROR, vis2Err, vobsSTAR_COMPUTED_PROP);
    
    return mcsSUCCESS;
}

/**
 * Compute Multiplicity
 *
 * \return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::ComputeMultiplicity()
{
    logExtDbg("sclsvrCALIBRATOR::ComputeMultiplicity()");

    //SetPropertyValue(sclsvrCALIBRATOR_MULTIPLICITY, "1.00");
    
    return mcsSUCCESS;
}

/**
 * Add all star properties 
 *
 * \return mcsSUCCESS 
 */
mcsCOMPL_STAT sclsvrCALIBRATOR::AddProperties(void)
{
    //logExtDbg("vobsSTAR::AddProperties()");
    AddProperty(sclsvrCALIBRATOR_DIAM_BV, "diam_bv", vobsFLOAT_PROPERTY, 
                "%.3f");
    AddProperty(sclsvrCALIBRATOR_DIAM_VR, "diam_vr", vobsFLOAT_PROPERTY, 
                "%.3f");
    AddProperty(sclsvrCALIBRATOR_DIAM_VK, "diam_vk", vobsFLOAT_PROPERTY, 
                "%.3f");
    AddProperty(sclsvrCALIBRATOR_DIAM_ERROR, "diamErr", 
                vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_DIAM_FLAG, "diamFlag", 
                vobsSTRING_PROPERTY);
    AddProperty(sclsvrCALIBRATOR_MO, "Mo", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_LO, "Lo", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_KO, "Ko", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_HO, "Ho", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_JO, "Jo", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_IO, "Io", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_RO, "Ro", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_VO, "Vo", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_BO, "Bo", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_VIS2, "vis2", vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_VIS2_ERROR, "vis2Err", 
                vobsFLOAT_PROPERTY, "%.3f");
    AddProperty(sclsvrCALIBRATOR_VIS2_FLAG, "vis2Flag", vobsSTRING_PROPERTY);

    return mcsSUCCESS;
}

/*___oOo___*/
