#ifndef sclsvrCALIBRATOR_H
#define sclsvrCALIBRATOR_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: sclsvrCALIBRATOR.h,v 1.2 2004-11-25 14:54:46 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    13-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * sclsvrCALIBRATOR class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * Local header
 */
#include"vobs.h"

/* 
 * Constants definition
 */

#define sclsvrNB_CALIBRATOR_PROPERTIES 14       /**< Number of properties */
#define sclsvrCALIBRATOR_PROP_NOT_SET  "99.99"  /**< Default value of the empty
                                          properties */

/*
 * Type definition
 */

typedef mcsSTRING32 sclsvrCALIBRATOR_PROPERTY;

/*
 * Enumeration type definition
 */

/**
 * sclsvrPROPERTY_ID is an enumeration which allow correspondance between an id
 * and an PROPERTY.
 */
/* The following offset is used to have id for calibrator properties which
 * differs from the ucd id used by vobsSTAR class */
#define sclsvrPROP_ID_OFFSET 100
typedef enum
{
    UNKNOWN_PROP_ID = sclsvrPROP_ID_OFFSET-1,
    ANGULAR_DIAMETER_ID,
    ANGULAR_DIAMETER_ERROR_ID,
    MO_ID,
    LO_ID,
    KO_ID,
    HO_ID,
    JO_ID,
    IO_ID,
    RO_ID,
    VO_ID,
    BO_ID,
    MULTIPLICITY_ID,
    VISIBILITY_ID,
    VISIBILITY_ERROR_ID
} sclsvrPROPERTY_ID;

/*
 * Class declaration
 */
class sclsvrCALIBRATOR : public vobsSTAR
{
public:
    // Constructors
    sclsvrCALIBRATOR();
    sclsvrCALIBRATOR(const sclsvrCALIBRATOR &sclsvr);
    sclsvrCALIBRATOR(const vobsSTAR &star);
    
    // Destructor
    virtual ~sclsvrCALIBRATOR();
 
    // Method to set the star properties

    virtual mcsCOMPL_STAT SetProperty(char *property, char *value, 
                                      mcsLOGICAL overwrite=mcsFALSE);
    virtual mcsCOMPL_STAT SetProperty(vobsUCD_ID ucdId, char *value,
                                      mcsLOGICAL overwrite=mcsFALSE);
    virtual mcsCOMPL_STAT SetProperty(vobsUCD_ID ucdId, mcsFLOAT value,
                                      mcsLOGICAL overwrite=mcsFALSE);
    virtual mcsCOMPL_STAT SetProperty(sclsvrPROPERTY_ID id, char *value,
                                      mcsLOGICAL overwrite=mcsFALSE);
    virtual mcsCOMPL_STAT SetProperty(sclsvrPROPERTY_ID id, mcsFLOAT value,
                                      mcsLOGICAL overwrite=mcsFALSE);
    // Is property set?
    virtual mcsLOGICAL    IsPropertySet(char *ucd) const;
    virtual mcsLOGICAL    IsPropertySet(int ucdId) const;
          
    // Methods to retreive the star properties
    virtual mcsCOMPL_STAT GetProperty(char *property, char *value) const;
    virtual mcsCOMPL_STAT GetProperty(int id, char *value) const; 
    virtual mcsCOMPL_STAT GetProperty(char *property, float *value) const;
    virtual mcsCOMPL_STAT GetProperty(int id, float *value) const;
    
  
    // method to complete calibrator properties
    mcsCOMPL_STAT Complete(vobsREQUEST request);

    // methods to get galactic coordonates
    mcsCOMPL_STAT GetLongitude(float &longitude);
    mcsCOMPL_STAT GetLattitude(float &lattitude);

    virtual void Display(void);    
    
protected:
    // Is UCD id?
    virtual mcsLOGICAL    IsUcdId(int id) const;

    // Method to obtain id from the UCD
    virtual sclsvrPROPERTY_ID Property2Id(char *ucd) const;
    
private:
    // Declaration of assignment operator as private method, in order to hide
    // them from the users.
    sclsvrCALIBRATOR& operator=(const sclsvrCALIBRATOR&);

    // Method to compute specific property
    mcsCOMPL_STAT ComputeMissingMagnitude();
    mcsCOMPL_STAT ComputeGalacticCoordinates();
    mcsCOMPL_STAT ComputeInterstellarAbsorption();
    mcsCOMPL_STAT ComputeAngularDiameter();
    mcsCOMPL_STAT ComputeVisibility(vobsREQUEST request);
    mcsCOMPL_STAT ComputeMultiplicity();

    /** Table containing the calibrator computed properties */
    sclsvrCALIBRATOR_PROPERTY _compProperties[sclsvrNB_CALIBRATOR_PROPERTIES];
    
};




#endif /*!sclsvrCALIBRATOR_H*/

/*___oOo___*/
