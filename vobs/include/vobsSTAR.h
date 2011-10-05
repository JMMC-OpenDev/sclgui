#ifndef vobsSTAR_H
#define vobsSTAR_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * vobsSTAR class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * System headers
 */
#include "string.h"
#include "math.h"
#include <map>
#include <vector>


/*
 * Local headers
 */
#include "vobsSTAR_PROPERTY.h"
#include "vobsSTAR_COMP_CRITERIA_LIST.h"

// TODO: use enumValues (singleton to have both enum and string literal value
/*
 * Definition of the star properties
 */
#define vobsSTAR_ID_HD                          "ID_HD"
#define vobsSTAR_ID_HIP                         "ID_HIP"
#define vobsSTAR_ID_DM                          "ID_DM"
#define vobsSTAR_ID_TYC1                        "ID_TYC1"
#define vobsSTAR_ID_TYC2                        "ID_TYC2"
#define vobsSTAR_ID_TYC3                        "ID_TYC3"
#define vobsSTAR_ID_CATALOG                     "ID_CATALOG"
#define vobsSTAR_ID_2MASS                       "ID_2MASS"
#define vobsSTAR_ID_DENIS                       "ID_DENIS"
#define vobsSTAR_ID_SB9                         "ID_SB9"
#define vobsSTAR_ID_WDS                         "ID_WDS"
#define vobsSTAR_POS_EQ_RA_MAIN                 "POS_EQ_RA_MAIN"
#define vobsSTAR_POS_EQ_RA_OTHER                "POS_EQ_RA_OTHER"
#define vobsSTAR_POS_EQ_DEC_MAIN                "POS_EQ_DEC_MAIN"
#define vobsSTAR_POS_EQ_DEC_OTHER               "POS_EQ_DEC_OTHER"
#define vobsSTAR_POS_EQ_PMDEC                   "POS_EQ_PMDEC"
#define vobsSTAR_POS_EQ_PMRA                    "POS_EQ_PMRA"
#define vobsSTAR_POS_PARLX_TRIG                 "POS_PARLX_TRIG"
#define vobsSTAR_POS_PARLX_TRIG_ERROR           "POS_PARLX_TRIG_ERROR"
#define vobsSTAR_SPECT_TYPE_MK                  "SPECT_TYPE_MK"
#define vobsSTAR_CODE_VARIAB_V1                 "CODE_VARIAB_V1"
#define vobsSTAR_CODE_VARIAB_V2                 "CODE_VARIAB_V2"
#define vobsSTAR_CODE_VARIAB_V3                 "VAR_CLASS"
#define vobsSTAR_CODE_BIN_FLAG                  "CODE_BIN_FLAG"
#define vobsSTAR_CODE_MULT_FLAG                 "CODE_MULT_FLAG"
#define vobsSTAR_ORBIT_SEPARATION_SEP1          "ORBIT_SEPARATION_SEP1"
#define vobsSTAR_ORBIT_SEPARATION_SEP2          "ORBIT_SEPARATION_SEP2"
#define vobsSTAR_CODE_MISC_I                    "CODE_MISC_I"
#define vobsSTAR_CODE_MISC_J                    "CODE_MISC_J"
#define vobsSTAR_CODE_MISC_K                    "CODE_MISC_K"
#define vobsSTAR_CODE_QUALITY                   "CODE_QUALITY"
#define vobsSTAR_POS_GAL_LAT                    "POS_GAL_LAT"
#define vobsSTAR_POS_GAL_LON                    "POS_GAL_LON"
#define vobsSTAR_VELOC_HC                       "VELOC_HC"
#define vobsSTAR_LD_DIAM                        "LD_DIAM"
#define vobsSTAR_LD_DIAM_ERROR                  "LD_DIAM_ERROR"
#define vobsSTAR_UD_DIAM                        "UD_DIAM"
#define vobsSTAR_UD_DIAM_ERROR                  "UD_DIAM_ERROR"
#define vobsSTAR_UDDK_DIAM                      "UDDK_DIAM"
#define vobsSTAR_UDDK_DIAM_ERROR                "UDDK_DIAM_ERROR"
#define vobsSTAR_DIAM12                         "DIAM12"
#define vobsSTAR_DIAM12_ERROR                   "DIAM12_ERROR"
#define vobsSTAR_OBS_METHOD                     "REDUCT_METHOD"
#define vobsSTAR_INST_WAVELENGTH_VALUE          "INST_WAVELENGTH_VALUE"
#define vobsSTAR_SPECT_WAVELENGTH_MISC          "SPECT_WAVELENGTH_MISC"
#define vobsSTAR_INST_FILTER_CODE               "INST_FILTER_CODE"
#define vobsSTAR_PHOT_FLUX_IR_MISC              "PHOT_FLUX_IR_MISC"
#define vobsSTAR_UNITS                          "UNITS"
#define vobsSTAR_PHOT_JHN_B                     "PHOT_JHN_B"
#define vobsSTAR_PHOT_PHG_B                     "PHOT_PHG_B"
#define vobsSTAR_PHOT_JHN_V                     "PHOT_JHN_V"
#define vobsSTAR_PHOT_PHG_V                     "PHOT_PHG_V"
#define vobsSTAR_PHOT_JHN_R                     "PHOT_JHN_R"
#define vobsSTAR_PHOT_PHG_R                     "PHOT_PHG_R"
#define vobsSTAR_PHOT_JHN_I                     "PHOT_JHN_I"
#define vobsSTAR_PHOT_COUS_I                    "PHOT_COUS_I"
#define vobsSTAR_PHOT_PHG_I                     "PHOT_PHG_I"
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
#define vobsSTAR_PHOT_FLUX_IR_09                "PHOT_FLUX_IR_09"
#define vobsSTAR_PHOT_FLUX_IR_09_ERROR          "PHOT_FLUX_IR_09_ERROR"
#define vobsSTAR_PHOT_FLUX_IR_12                "PHOT_FLUX_IR_12"
#define vobsSTAR_PHOT_FLUX_IR_12_ERROR          "PHOT_FLUX_IR_12_ERROR"
#define vobsSTAR_PHOT_FLUX_IR_18                "PHOT_FLUX_IR_18"
#define vobsSTAR_PHOT_FLUX_IR_18_ERROR          "PHOT_FLUX_IR_18_ERROR"
#define vobsSTAR_REF_STAR                       "REF_STAR"
#define vobsSTAR_PHYS_TEMP_EFFEC                "PHYS_TEMP_EFFEC"
#define vobsSTAR_PHYS_TEMP_EFFEC_ERROR          "PHYS_TEMP_EFFEC_ERROR"
#define vobsSTAR_PHOT_EXTINCTION_TOTAL          "PHOT_EXTINCTION_TOTAL"
#define vobsSTAR_CHI2_QUALITY                   "CHI2_QUALITY"
#define vobsSTAR_SP_TYP_PHYS_TEMP_EFFEC         "SP_TYP_PHYS_TEMP_EFFEC"

/* Blanking value used for parsed RA/DEC coordinates */
#define EMPTY_COORD_DEG 1000.

/*
 * const char* comparator used by map<const char*, ...>
 */
struct constStringComparator {
    bool operator()(const char* s1, const char* s2) const {
        return (s1 == s2) ? false : strcmp(s1, s2) < 0;
    }
};

/* property index map type using char* keys and custom comparator functor */
typedef std::map<const char*, unsigned int, constStringComparator> PropertyIndexMap;

/* property meta list type using property meta pointers */
typedef std::vector<vobsSTAR_PROPERTY_META*> PropertyMetaList;

/* property list type using property pointers */
typedef std::vector<vobsSTAR_PROPERTY*> PropertyList;

/*
 * Class declaration
 */
/**
 * Store all the propreties caracterising a star.
 *
 * It allows among oher things the following actions on star properies:
 * @li read;
 * @li update;
 * @li compare.
 *
 * @sa vobsSTAR_PROPERTY
 */
class vobsSTAR
{
public:
    // Constructors
    vobsSTAR();
    explicit vobsSTAR(const vobsSTAR& star);

    // assignement operator =
    vobsSTAR& operator=(const vobsSTAR&);
    
    // Destructor
    virtual ~vobsSTAR();

    // Clear means free
    void Clear(void);
    
    // Clear values
    void ClearValues(void);
    
    // Compare stars (i.e values)
    int compare(const vobsSTAR& other) const;
    
    // Set the star property values
    mcsCOMPL_STAT SetPropertyValue
                   (const char*           propertyId,
                    const char*           value,
                    const char*           origin,
                    vobsCONFIDENCE_INDEX  confidenceIndex = vobsCONFIDENCE_HIGH, 
                    mcsLOGICAL            overwrite       = mcsFALSE);

    /**
     * Set the character value of the given property.
     *
     * @param property property to use.
     * @param value property value
     * @param origin the origin of the value (catalog, computed, ...)
     * @param confidenceIndex value confidence index
     * @param overwrite booleen to know if it is an overwrite property
     *
     * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
     */
    inline mcsCOMPL_STAT SetPropertyValue(vobsSTAR_PROPERTY*   property,
                                          const char*          value,
                                          const char*          origin,
                                          vobsCONFIDENCE_INDEX confidenceIndex = vobsCONFIDENCE_HIGH, 
                                          mcsLOGICAL           overwrite       = mcsFALSE
                                         ) const __attribute__((always_inline))
    {
        // Set this property value
        return property->SetValue(value, origin, confidenceIndex, overwrite);
    }

    mcsCOMPL_STAT SetPropertyValue
                   (const char*           propertyId, 
                    mcsDOUBLE             value,
                    const char*           origin,
                    vobsCONFIDENCE_INDEX  confidenceIndex = vobsCONFIDENCE_HIGH, 
                    mcsLOGICAL            overwrite       = mcsFALSE);

    /**
     * Set the floating value of the given property.
     *
     * @param property property to use.
     * @param value property value
     * @param origin the origin of the value (catalog, computed, ...)
     * @param confidenceIndex value confidence index
     * @param overwrite booleen to know if it is an overwrite property
     *
     * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise.
     */
    inline mcsCOMPL_STAT SetPropertyValue(vobsSTAR_PROPERTY* property,
                                          mcsDOUBLE value,
                                          const char* origin,
                                          vobsCONFIDENCE_INDEX confidenceIndex = vobsCONFIDENCE_HIGH, 
                                          mcsLOGICAL overwrite       = mcsFALSE
                                         ) const __attribute__((always_inline))
    {
        // Set this property value
        return property->SetValue(value, origin, confidenceIndex, overwrite);
    }
    
    mcsCOMPL_STAT ClearPropertyValue(const char* id);
    
    /**
     * Return the next property in the list.
     *
     * This method returns a pointer on the next element of the list.
     *
     * @param init if mcsTRUE, returns the first element of the list.
     *
     * This method can be used to move forward in the list, as shown below:
     * @code
     * ...
     * for (unsigned int el = 0; el < star.NbProperties(); el++)
     * {
     *     printf("%s",star.GetNextProperty((mcsLOGICAL)(el==0))->GetName());
     * }
     * ...
     * @endcode
     *
     * @return pointer to the next element of the list, or NULL if the end of the
     * list is reached.
     */
    inline vobsSTAR_PROPERTY* GetNextProperty(mcsLOGICAL init = mcsFALSE) __attribute__((always_inline))
    {
        // if the logical value of the parameter, init is mcsTRUE, the wanted value
        // is the first
        if (init == mcsTRUE)
        {
            _propertyListIterator = _propertyList.begin();
        }
        else
        {
            // Increase the iterator to the following position
            _propertyListIterator++;

            // If this reached the end of the list
            if (_propertyListIterator == _propertyList.end())
            {
                return NULL;
            }
        }

        return *_propertyListIterator;
    }

    // Return the star RA and DEC coordinates (in arcsecond)
    mcsCOMPL_STAT GetRa (mcsDOUBLE &ra)  const;
    mcsCOMPL_STAT GetDec(mcsDOUBLE &dec) const;

    // Return the star ID
    mcsCOMPL_STAT GetId(char* starId, const mcsUINT32 maxLength) const;

    // Update the star properties with the given star ones
    mcsLOGICAL Update(const vobsSTAR &star, mcsLOGICAL overwrite = mcsFALSE, mcsINT32* propertyUpdated = NULL);
    
    // Print out all star properties
    void Display(mcsLOGICAL showPropId = mcsFALSE) const;
    void Dump(const char* separator = " ") const;

    static void FreePropertyIndex(void);

    /**
     * Get the star property at the given index.
     *
     * @param idx property index.
     * @return pointer on the found star property object on successful completion.
     * Otherwise NULL is returned.
     */
    inline vobsSTAR_PROPERTY* GetProperty(const int idx) const __attribute__((always_inline))
    {
        if (idx < 0 || idx >= (int)_propertyList.size())
        {
            return NULL;
        }

        return _propertyList[idx];
    }

    /**
     * Get the star property corresponding to the given UCD.
     *
     * @param id property id.
     *
     * @return pointer on the found star property object on successful completion.
     * Otherwise NULL is returned.
     *
     * @b Error codes:@n
     * The possible errors are :
     * @li vobsERR_INVALID_PROPERTY_ID
     */
    inline vobsSTAR_PROPERTY* GetProperty(const char* id) const __attribute__((always_inline))
    {
        // Look for property
        return GetProperty(GetPropertyIndex(id));
    }

    /**
     * Get a property character value.
     *
     * @param id property id.
     *
     * @return pointer to the found star property value on successful completion.
     * Otherwise NULL is returned.
     */
    inline const char* GetPropertyValue(const char* id) const __attribute__((always_inline))
    {
        // Look for property
        vobsSTAR_PROPERTY* property = GetProperty(id);

        return GetPropertyValue(property);
    }

    /**
     * Get a property character value.
     *
     * @param property property to use.
     *
     * @return pointer to the found star property value on successful completion.
     * Otherwise NULL is returned.
     */
    inline const char* GetPropertyValue (const vobsSTAR_PROPERTY* property) const __attribute__((always_inline))
    {
        if (property == NULL)
        {
            // Return error
            return NULL;
        }

        // Return the property value
        return (property->GetValue());
    }

    /**
     * Get a star property mcsDOUBLE value.
     *
     * @param id property id.
     * @param value pointer to store value.
     *
     * @return mcsSUCCESS on successfull completion, mcsFAILURE otherwise.
     */
    inline mcsCOMPL_STAT GetPropertyValue(const char* id, mcsDOUBLE* value) const __attribute__((always_inline))
    {
        // Look for property
        vobsSTAR_PROPERTY* property = GetProperty(id);

        return GetPropertyValue(property, value);
    }

    /**
     * Get a star property mcsDOUBLE value.
     *
     * @param property property to use.
     * @param value pointer to store value.
     *
     * @return mcsSUCCESS on successfull completion, mcsFAILURE otherwise.
     */
    inline mcsCOMPL_STAT GetPropertyValue(const vobsSTAR_PROPERTY* property, mcsDOUBLE* value) const __attribute__((always_inline))
    {
        if (property == NULL)
        {
            // Return error
            return mcsFAILURE;
        }

        // Return the property value
        return (property->GetValue(value));
    }

    /**
     * Get a star property type.
     *
     * @sa vobsSTAR_PROPERTY
     *
     * @param id property id.
     *
     * @return property type.
     */
    inline vobsPROPERTY_TYPE GetPropertyType(const char* id) const __attribute__((always_inline))
    {
        // Look for property
        vobsSTAR_PROPERTY* property = GetProperty(id);

        return GetPropertyType(property);
    }

    /**
     * Get a star property type.
     *
     * @sa vobsSTAR_PROPERTY
     *
     * @param property property to use.
     *
     * @return property type. Otherwise vobsSTRING_PROPERTY is returned.
     */
    inline vobsPROPERTY_TYPE GetPropertyType(const vobsSTAR_PROPERTY* property) const __attribute__((always_inline))
    {
       if (property == NULL)
        {
            return vobsSTRING_PROPERTY;
        }

        // Return property
        return (property->GetType());
    }

    /**
     * Get a star property confidence index.
     *
     * @sa vobsSTAR_PROPERTY
     *
     * @param id property id.
     *
     * @return property confidence index.
     */
    inline vobsCONFIDENCE_INDEX GetPropertyConfIndex(const char* id) const __attribute__((always_inline))
    {
        // Look for property
        vobsSTAR_PROPERTY* property = GetProperty(id);

        // Return property confidence index
        return (property->GetConfidenceIndex());
    }

    /**
     * Check whether the property is set or not.
     *
     * @param id property id.
     *
     * @warning If the given property id is unknown, this method returns mcsFALSE.
     *
     * @return mcsTRUE if the the property has been set, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsPropertySet(const char* id) const __attribute__((always_inline))
    {
        // Look for the property
        vobsSTAR_PROPERTY* property = GetProperty(id);

        return IsPropertySet(property);
    }

    /**
     * Check whether the property is set or not.
     *
     * @param idx property index.
     *
     * @warning If the given property id is unknown, this method returns mcsFALSE.
     *
     * @return mcsTRUE if the the property has been set, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsPropertySet(const int idx) const __attribute__((always_inline))
    {
        // Look for the property
        vobsSTAR_PROPERTY* property = GetProperty(idx);

        return IsPropertySet(property);
    }

    /**
     * Check whether the property is set or not.
     *
     * @param property property to use.
     *
     * @warning If the given property is NULL, this method returns mcsFALSE.
     *
     * @return mcsTRUE if the the property has been set, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsPropertySet(const vobsSTAR_PROPERTY* property) const __attribute__((always_inline))
    {
        if (property == NULL)
        {
            return mcsFALSE;
        }

        return (property->IsSet());
    }

    /**
     * Return whether a name correspond to a property.
     *
     * @param id property id.
     *
     * @return mcsTRUE) if the the property is known, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsProperty(const char* id) const __attribute__((always_inline))
    {
        // Look for property: see GetProperty(id)
        int idx = GetPropertyIndex(id);

        if (idx < 0 || idx >= (int)_propertyList.size())
        {
            return mcsFALSE;
        }

        return mcsTRUE;
    }

    /**
     * Return the number of properties
     *
     * @return the number of properties of the star
     */
    inline mcsINT32 NbProperties(void) const __attribute__((always_inline))
    {
        return _propertyList.size();
    }
    
    /**
     * Return whether the star is the same as another given one.
     *
     * @param star the other star.
     *
     * @return mcsTRUE if the stars are the same, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsSame(vobsSTAR* star) const __attribute__((always_inline))
    {
        // try to use first cached ra/dec coordinates for performance:

        // Get right ascension of the star. If not set return FALSE
        mcsDOUBLE ra1 = _ra;

        if ((ra1 == EMPTY_COORD_DEG) && (GetRa(ra1) == mcsFAILURE))
        {
            return mcsFALSE;
        }

        mcsDOUBLE ra2 = star->_ra;

        if ((ra2 == EMPTY_COORD_DEG) && (star->GetRa(ra2) == mcsFAILURE))
        {
            return mcsFALSE;
        }

        // Get declinaison of the star. If not set return FALSE
        mcsDOUBLE dec1 = _dec;

        if ((dec1 == EMPTY_COORD_DEG) && (GetDec(dec1) == mcsFAILURE))
        {
            return mcsFALSE;
        }

        mcsDOUBLE dec2 = star->_dec;

        if ((dec2 == EMPTY_COORD_DEG) && (star->GetDec(dec2) == mcsFAILURE))
        {
            return mcsFALSE;
        }

        // Compare coordinates
        if ((ra1 == ra2) && (dec1 == dec2))
        {
            return mcsTRUE;
        }
        return mcsFALSE;
    }
    
    /**
     * Return whether the star is the same as another given one, as
     * shown below:
     * @code
     * int nCriteria = 0;
     * vobsSTAR_CRITERIA_INFO* criterias = NULL;
     * 
     * // Initialize criteria informations:
     * if (criteriaList.InitializeCriterias() == mcsFAILURE)
     * {
     *     return mcsFAILURE;
     * }
     * 
     * // Get criterias:
     * if (criteriaList.GetCriterias(criterias, nCriteria) == mcsFAILURE)
     * {
     *     return mcsFAILURE;
     * }
     * 
     * ...
     * if (star->IsSame(anotherStar, criterias, nCriteria) == mcsTRUE)
     * {
     *     printf ("Star is same !!");
     * }
     * @endcode
     * 
     *
     * @param star the other star.
     * @param criterias vobsSTAR_CRITERIA_INFO[] list of comparison criterias 
     *                  given by vobsSTAR_COMP_CRITERIA_LIST.GetCriterias()
     * @param nCriteria number of criteria i.e. size of the vobsSTAR_CRITERIA_INFO array
     *
     * @return mcsTRUE if the stars are the same, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsSame(vobsSTAR* star, vobsSTAR_CRITERIA_INFO* criterias, mcsUINT32 nCriteria) const __attribute__((always_inline))
    {
        // assumption: the criteria list is not NULL

        int propIndex;
        vobsPROPERTY_TYPE comparisonType;
        vobsSTAR_PROPERTY* prop1 = NULL;
        vobsSTAR_PROPERTY* prop2 = NULL;

        const char* val1Str = NULL;
        const char* val2Str = NULL;
        mcsDOUBLE val1, val2; 

        // Get criteria informations
        vobsSTAR_CRITERIA_INFO* criteria = NULL;

        // Get each criteria of the list and check if the comparaison with all
        // this criteria gave a equality

        for (mcsUINT32 el = 0; el < nCriteria; el++)
        {
            criteria = &criterias[el];

            comparisonType = criteria->comparisonType;        

            switch (criteria->propCompType)
            {
                case vobsPROPERTY_COMP_RA:
                    // RA is always the first criteria:

                    // try to use first cached ra/dec coordinates for performance:

                    // Get right ascension of the star. If not set return FALSE
                    val1 = _ra;

                    if ((val1 == EMPTY_COORD_DEG) && (GetRa(val1) == mcsFAILURE))
                    {
                        return mcsFALSE;
                    }

                    val2 = star->_ra;

                    if ((val2 == EMPTY_COORD_DEG) && (star->GetRa(val2) == mcsFAILURE))
                    {
                        return mcsFALSE;
                    }
                    break;

                case vobsPROPERTY_COMP_DEC:
                    // DEC is always the second criteria:

                    // try to use first cached ra/dec coordinates for performance:

                    // Get declinaison of the star. If not set return FALSE
                    val1 = _dec;

                    if ((val1 == EMPTY_COORD_DEG) && (GetDec(val1) == mcsFAILURE))
                    {
                        return mcsFALSE;
                    }

                    val2 = star->_dec;

                    if ((val2 == EMPTY_COORD_DEG) && (star->GetDec(val2) == mcsFAILURE))
                    {
                        return mcsFALSE;
                    }

                    break;

                default:
                    propIndex = criteria->propertyIndex;       

                    prop1 = GetProperty(propIndex);
                    prop2 = star->GetProperty(propIndex);

                    // If property is a string
                    if (comparisonType == vobsSTRING_PROPERTY)
                    {
                        if (IsPropertySet(prop1) == mcsTRUE)
                        {
                            val1Str = GetPropertyValue(prop1);
                        }
                        else
                        {
                            return mcsFALSE;
                        }    

                        if (star->IsPropertySet(prop2) == mcsTRUE)
                        {
                            val2Str = star->GetPropertyValue(prop2);
                        }
                        else
                        {
                            return mcsFALSE;
                        }    

                        break; // exit from switch
                    }

                    if (IsPropertySet(prop1) == mcsTRUE)
                    {
                        if (GetPropertyValue(prop1, &val1) == mcsFAILURE)
                        {
                            return mcsFALSE;
                        }
                    }
                    else
                    {
                        return mcsFALSE;
                    }    

                    if (star->IsPropertySet(prop2) == mcsTRUE)
                    {
                        if (star->GetPropertyValue(prop2, &val2) == mcsFAILURE)
                        {
                            return mcsFALSE;
                        } 
                    }
                    else
                    {
                        return mcsFALSE;
                    }    

                    break;
            }

            // float first:
            if (comparisonType == vobsFLOAT_PROPERTY)
            {
                double delta = fabs(val1 - val2);

                if (delta > criteria->range)            
                {
                    return mcsFALSE;
                }
            } else {
                if (strcmp(val1Str, val2Str) != 0)
                {
                    return mcsFALSE;
                }            
            }

        } // loop on criteria

        return mcsTRUE;
    }
    
    /**
     * Find the property index (position) for the given property identifier
     * @param id property identifier
     * @return index or -1 if not found in the property index
     */
    inline static int GetPropertyIndex(const char* id) __attribute__((always_inline))
    {
        // Look for property
        PropertyIndexMap::iterator idxIter = vobsSTAR::vobsSTAR_PropertyIdx.find(id);

        // If no property with the given Id was found
        if (idxIter == vobsSTAR::vobsSTAR_PropertyIdx.end()) 
        {
            return -1;
        }

        return idxIter->second;
    }
    
    /**
     * Return the property meta data for the given index
     * @param idx property index
     * @return property meta (pointer)
     */
    inline static vobsSTAR_PROPERTY_META* GetPropertyMeta(const int idx) __attribute__((always_inline))
    {
        if (idx < 0 || idx >= (int)vobsSTAR::vobsStar_PropertyMetaList.size())
        {
            return NULL;
        }

        return vobsSTAR::vobsStar_PropertyMetaList[idx];
    }
    
    
protected:
    
    void ReserveProperties(unsigned int size);

    // Add a property. Should be only called by constructors.
    void AddProperty(const vobsSTAR_PROPERTY_META* meta);

    // Add a property meta data.
    static void AddPropertyMeta(const char*         id,
                         const char*        name,
                         const vobsPROPERTY_TYPE  type,
                         const char*        unit        = NULL,
                         const char*        format      = NULL,
                         const char*        link        = NULL,
                         const char*        description = NULL);

    static void initializeIndex(void);

    // RA/DEC property indexes (read-only):
    static int vobsSTAR_PropertyRAIndex;
    static int vobsSTAR_PropertyDECIndex;

    // wavelength/flux property indexes (read-only):
    static int vobsSTAR_PropertyWaveLengthIndex;
    static int vobsSTAR_PropertyFluxIndex;

protected:

    static PropertyIndexMap vobsSTAR_PropertyIdx;
    static PropertyMetaList vobsStar_PropertyMetaList;
    
private:

    static int  vobsSTAR_PropertyMetaBegin;
    static int  vobsSTAR_PropertyMetaEnd;
    static bool vobsSTAR_PropertyIdxInitialized;
    
    // ra/dec are mutable to be modified even by const methods
    mutable mcsDOUBLE         _ra;  // parsed RA
    mutable mcsDOUBLE         _dec; // parsed DEC

    PropertyList              _propertyList;   
    PropertyList::iterator    _propertyListIterator;
    
    // Method to define all star properties
    mcsCOMPL_STAT AddProperties(void);
};


#endif /*!vobsSTAR_H*/
/*___oOo___*/
