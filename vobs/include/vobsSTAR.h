#ifndef vobsSTAR_H
#define vobsSTAR_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsSTAR.h,v 1.29 2005-02-11 10:44:00 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.28  2005/02/10 16:42:38  gluck
* Correct wrong type for confidenceIndex: mcsINT32 -> vobsCONFIDENCE_INDEX
*
* Revision 1.27  2005/02/10 08:07:38  scetre
* changed parser and hd, hip, dm number id in order to get all of them even if they have the same UCD
*
* Revision 1.26  2005/02/08 20:36:14  gzins
* Changed some parameters from char* to const char*
*
* Revision 1.25  2005/02/08 07:16:56  gzins
* Changed value parameter type: char* to const char*
*
* Revision 1.24  2005/02/07 09:10:04  gzins
* Fixed wrong type for isComputed parameter; mcsFLOAT instead of mcsLOGICAL
*
* Revision 1.23  2005/02/04 14:31:30  scetre
* add getLuminosityClass method
*
* Revision 1.22  2005/02/04 09:59:05  gzins
* Added star properties for MIDI catalog
*
* Revision 1.21  2005/01/26 14:11:42  scetre
* change assignement operator and pass list as private member of the class vobsSTAR
*
* Revision 1.20  2005/01/26 08:10:32  scetre
* change history
*
* scetre    22-Jul-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * vobsSTAR class declaration.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System headers
 */
#include <map>

/*
 * Laocal headers
 */
#include "vobsSTAR_PROPERTY.h"
#include "vobsSTAR_COMP_CRITERIA_LIST.h"
/*
 * Constants definition
 */

#define vobsSTAR_PROP_NOT_SET  "-"  /**< Default value of the empty
                                          properties */


/*
 * Type definition
 */


/*
 * Definition of the star properties
 */
#define vobsSTAR_ID_HD                          "ID_HD"
#define vobsSTAR_ID_HIP                         "ID_HIP"
#define vobsSTAR_ID_DM                          "ID_DM"
#define vobsSTAR_POS_EQ_RA_MAIN                 "POS_EQ_RA_MAIN"
#define vobsSTAR_POS_EQ_DEC_MAIN                "POS_EQ_DEC_MAIN"
#define vobsSTAR_POS_EQ_PMDEC                   "POS_EQ_PMDEC"
#define vobsSTAR_POS_EQ_PMRA                    "POS_EQ_PMRA"
#define vobsSTAR_POS_PARLX_TRIG                 "POS_PARLX_TRIG"
#define vobsSTAR_SPECT_TYPE_MK                  "SPECT_TYPE_MK"
#define vobsSTAR_CODE_VARIAB                    "CODE_VARIAB"
#define vobsSTAR_CODE_MULT_FLAG                 "CODE_MULT_FLAG"
#define vobsSTAR_POS_GAL_LAT                    "POS_GAL_LAT"
#define vobsSTAR_POS_GAL_LON                    "POS_GAL_LON"
#define vobsSTAR_VELOC_HC                       "VELOC_HC"
#define vobsSTAR_LD_DIAM                        "LD_DIAM"
#define vobsSTAR_LD_DIAM_ERROR                  "LD_DIAM_ERROR"
#define vobsSTAR_UD_DIAM                        "UD_DIAM"
#define vobsSTAR_UD_DIAM_ERROR                  "UD_DIAM_ERROR"
#define vobsSTAR_UDDK_DIAM                      "UDDK_DIAM"
#define vobsSTAR_UDDK_DIAM_ERROR                "UDDK_DIAM_ERROR"
#define vobsSTAR_OBS_METHOD                     "OBS_METHOD"
#define vobsSTAR_INST_WAVELENGTH_VALUE          "INST_WAVELENGTH_VALUE"
#define vobsSTAR_PHOT_FLUX_IR_MISC              "PHOT_FLUX_IR_MISC"
#define vobsSTAR_UNITS                          "UNITS"
#define vobsSTAR_PHOT_JHN_U                     "PHOT_JHN_U"
#define vobsSTAR_PHOT_JHN_B                     "PHOT_JHN_B"
#define vobsSTAR_PHOT_JHN_V                     "PHOT_JHN_V"
#define vobsSTAR_PHOT_JHN_R                     "PHOT_JHN_R"
#define vobsSTAR_PHOT_JHN_I                     "PHOT_JHN_I"
#define vobsSTAR_PHOT_JHN_J                     "PHOT_JHN_J"
#define vobsSTAR_PHOT_JHN_H                     "PHOT_JHN_H"
#define vobsSTAR_PHOT_JHN_K                     "PHOT_JHN_K"
#define vobsSTAR_PHOT_JHN_L                     "PHOT_JHN_L"
#define vobsSTAR_PHOT_JHN_M                     "PHOT_JHN_M"
#define vobsSTAR_PHOT_JHN_N                     "PHOT_JHN_N"
#define vobsSTAR_VELOC_ROTAT                    "VELOC_ROTAT"
#define vobsSTAR_PHOT_COLOR_EXCESS              "PHOT_COLOR_EXCESS"
#define vobsSTAR_IR_FLUX_ORIGIN                 "IR_FLUX_ORIGIN"
#define vobsSTAR_POS_PARLX_TRIG_ERROR           "POS_PARLX_TRIG_ERROR"
#define vobsSTAR_PHOT_FLUX_IR_12                "PHOT_FLUX_IR_12"
#define vobsSTAR_PHOT_FLUX_IR_12_ERROR          "PHOT_FLUX_IR_12_ERROR"
#define vobsSTAR_REF_STAR                       "REF_STAR"
#define vobsSTAR_PHYS_TEMP_EFFEC                "PHYS_TEMP_EFFEC"
#define vobsSTAR_PHYS_TEMP_EFFEC_ERROR          "PHYS_TEMP_EFFEC_ERROR"
#define vobsSTAR_PHOT_EXTINCTION_TOTAL          "PHOT_EXTINCTION_TOTAL"
#define vobsSTAR_CHI2_QUALITY                   "CHI2_QUALITY"
#define vobsSTAR_SP_TYP_PHYS_TEMP_EFFEC         "SP_TYP_PHYS_TEMP_EFFEC"


/*
 * Class declaration
 */

/**
 * vobsSTAR is a class which caracterises a star.
 *
 * vobsSTAR methods allow to
 * \li modify
 * \li read
 * \li update
 * \li compare
 * stars properties.
 *
 * \sa vobsSTAR_LIST
 *
 * \todo Finish to implement Update and Display methods.
 *
 */
class vobsSTAR
{
public:
    // Constructors
    vobsSTAR();
    vobsSTAR(vobsSTAR &star);

    // assignement operator =
    vobsSTAR& operator=(const vobsSTAR&);
    
    // Destructor
    virtual ~vobsSTAR();

    // Method to set the star properties
    virtual mcsCOMPL_STAT SetPropertyValue
               (const char *id, const char *value,
                const char *origin,
                vobsCONFIDENCE_INDEX confidenceIndex=vobsCONFIDENCE_HIGH, 
                mcsLOGICAL overwrite=mcsFALSE);
    virtual mcsCOMPL_STAT SetPropertyValue
                (const char *propertyId, 
                 mcsFLOAT value,
                 const char *origin,
                 vobsCONFIDENCE_INDEX confidenceIndex=vobsCONFIDENCE_HIGH, 
                 mcsLOGICAL overwrite=mcsFALSE);

    // Methods to retreive the star property
    virtual vobsSTAR_PROPERTY *GetProperty(char *id) ;
    virtual vobsSTAR_PROPERTY *GetNextProperty(mcsLOGICAL init = mcsFALSE);
    virtual const char *GetPropertyValue(char *id);
    virtual mcsCOMPL_STAT GetPropertyValue(char *id, mcsFLOAT *value);

    // Is property set?
    virtual mcsLOGICAL    IsPropertySet(char *propertyId);

    // Is a name a property?
    virtual mcsLOGICAL    IsProperty(char *propertyId);

    // Method to get RA and DEC (in arcsecond)
    virtual mcsCOMPL_STAT GetRa(float &ra);
    virtual mcsCOMPL_STAT GetDec(float &dec);

    // Method to get Spectral class and luminosity class of a the spectral type
    // of the star
    virtual mcsCOMPL_STAT GetSpectralClass(char *spectralClass);
    virtual mcsCOMPL_STAT GetLuminosityClass(char *luminosityClass);
    
    
    // Method to know if another star is the same than this one
    virtual mcsLOGICAL IsSame (vobsSTAR &star,
                               vobsSTAR_COMP_CRITERIA_LIST *criteriaList=NULL);

    // Method to update star properties from the given star
    virtual mcsCOMPL_STAT Update (vobsSTAR &star);

    // Method to get the number of properties
    virtual mcsINT32 NbProperties();
    
    // Method to print out all star properties
    virtual void Display(void);
    
protected:
    // Method to add a property. Should be only called by constructors.
    mcsCOMPL_STAT AddProperty(char *id, char *name,
                              vobsPROPERTY_TYPE type, char *format=NULL);
    map<string, vobsSTAR_PROPERTY> _propertyList;
    map<string, vobsSTAR_PROPERTY>::iterator _propertyListIterator;
    map<int, string> _propertyOrder;
    map<int, string>::iterator _propertyOrderIterator;

private:
    // Method to define all star properties
    mcsCOMPL_STAT AddProperties(void);

    
};


#endif /*!vobsSTAR_H*/
/*___oOo___*/
