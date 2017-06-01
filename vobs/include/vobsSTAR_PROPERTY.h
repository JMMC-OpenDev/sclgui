#ifndef vobsSTAR_PROPERTY_H
#define vobsSTAR_PROPERTY_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * vobsSTAR_PROPERTY class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System Headers
 */
#include <map>
#include <math.h>

/*
 * Local headers
 */
#include "vobsSTAR_PROPERTY_META.h"



/*
 * Type declaration
 */

/*
 * const char* comparator used by map<const char*, ...>
 */
struct constStarPropertyMetaComparator
{

    bool operator()(const vobsSTAR_PROPERTY_META* meta1, const vobsSTAR_PROPERTY_META * meta2) const
    {
        return (meta1 == meta2) ? false : strcmp(meta1->GetId(), meta2->GetId()) < 0;
    }
} ;

/* Star property meta pointer / Catalog ID mapping */
typedef std::multimap<const vobsSTAR_PROPERTY_META*, const char*, constStarPropertyMetaComparator> vobsCATALOG_STAR_PROPERTY_CATALOG_MAPPING;

/* Star property meta pointer / Catalog ID pair */
typedef std::pair<const vobsSTAR_PROPERTY_META*, const char*> vobsCATALOG_STAR_PROPERTY_CATALOG_PAIR;

/**
 * Confidence index (4 values iso needs only 2 bits = 1 byte)
 */
typedef enum
{
    vobsCONFIDENCE_NO     = 0, /** No confidence              */
    vobsCONFIDENCE_LOW    = 1, /** Low confidence             */
    vobsCONFIDENCE_MEDIUM = 2, /** Medium confidence          */
    vobsCONFIDENCE_HIGH   = 3, /** High confidence            */
    vobsNB_CONFIDENCE_INDEX    /** number of Confidence index */
}
vobsCONFIDENCE_INDEX;

/* confidence index as label string mapping */
static const char* const vobsCONFIDENCE_STR[] = { "NO", "LOW", "MEDIUM", "HIGH" };

/* confidence index as integer string mapping */
static const char* const vobsCONFIDENCE_INT[] = { "0", "1", "2", "3" };

/**
 * Return the string literal representing the confidence index
 * @return string literal "LOW", "MEDIUM" or "HIGH"
 */
const char* vobsGetConfidenceIndex(const vobsCONFIDENCE_INDEX confIndex);

/**
 * Return the integer literal representing the confidence index
 * @return integer literal "1" (LOW), "2" (MEDIUM) or "3" (HIGH)
 */
const char* vobsGetConfidenceIndexAsInt(const vobsCONFIDENCE_INDEX confIndex);

/*
 * Class declaration
 */

/**
 * Star property.
 *
 * The vobsSTAR_PROPERTY ...
 *
 */
class vobsSTAR_PROPERTY
{
public:
    // Class constructors
    vobsSTAR_PROPERTY(const vobsSTAR_PROPERTY_META* meta);

    explicit vobsSTAR_PROPERTY(const vobsSTAR_PROPERTY&);

    vobsSTAR_PROPERTY& operator=(const vobsSTAR_PROPERTY&) ;

    // Class destructor
    ~vobsSTAR_PROPERTY();

    // Property value setters
    mcsCOMPL_STAT SetValue(const char* value,
                           vobsORIGIN_INDEX originIndex,
                           vobsCONFIDENCE_INDEX confidenceIndex = vobsCONFIDENCE_HIGH,
                           mcsLOGICAL overwrite = mcsFALSE);

    mcsCOMPL_STAT SetValue(mcsDOUBLE value,
                           vobsORIGIN_INDEX originIndex,
                           vobsCONFIDENCE_INDEX confidenceIndex = vobsCONFIDENCE_HIGH,
                           mcsLOGICAL overwrite = mcsFALSE);

    // Property error setters
    mcsCOMPL_STAT SetError(const char* error,
                           mcsLOGICAL overwrite = mcsFALSE);

    void SetError(mcsDOUBLE  error,
                  mcsLOGICAL overwrite = mcsFALSE);

    /**
     * Clear property value
     *
     * @return mcsSUCCESS
     */
    inline void ClearValue() __attribute__ ((always_inline))
    {
        _confidenceIndex = vobsCONFIDENCE_NO;
        _originIndex     = vobsORIG_NONE;

        if (IS_NOT_NULL(_value))
        {
            delete[](_value);
            _value = NULL;
        }
        _numerical = NAN;
        _error     = NAN;
    }

    /**
     * Get value as a string or NULL if not set or not a string property
     *
     * @return value as a string or NULL
     */
    inline const char* GetValue() const __attribute__ ((always_inline))
    {
        return _value;
    }

    /**
     * Get value as a string or empty string ("") if not set or not a string property
     *
     * @return value as a string or empty string ("")
     */
    inline const char* GetValueOrBlank() const __attribute__ ((always_inline))
    {
        // Return property value
        if (IS_NULL(_value))
        {
            return "";
        }
        return _value;
    }

    /**
     * Get numerical value as a string or "" if not set or not a numerical property
     *
     * @param converted numerical value as a string or NULL
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetFormattedValue(mcsSTRING32& converted) const;

    /**
     * Get error as a string or "" if not set or not a numerical property
     *
     * @param converted error as a string or NULL
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetFormattedError(mcsSTRING32& converted) const;

    /**
     * Get value as a double.
     *
     * @param value pointer to store value.
     *
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetValue(mcsDOUBLE *value) const;

    /**
     * Get value as an integer.
     *
     * @param value pointer to store value.
     *
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetValue(mcsINT32 *value) const;

    /**
     * Get value as a boolean.
     *
     * @param value pointer to store value.
     *
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetValue(mcsLOGICAL *value) const;

    /**
     * Return mcsTRUE if this boolean property is set and equals mcsTRUE
     * @return true if this boolean property is set and equals mcsTRUE; false otherwise
     */
    bool IsTrue() const
    {
        mcsLOGICAL flag;
        return ((GetValue(&flag) == mcsSUCCESS) && IS_TRUE(flag));
    }

    inline void OverwriteOriginIndex(vobsORIGIN_INDEX originIndex) __attribute__ ((always_inline))
    {
        _originIndex = originIndex;
    }

    /**
     * Get the origin index
     *
     * @return origin index
     */
    inline vobsORIGIN_INDEX GetOriginIndex() const __attribute__ ((always_inline))
    {
        return _originIndex;
    }

    /**
     * Get value of the confidence index
     *
     * @return value of confidence index
     */
    inline vobsCONFIDENCE_INDEX GetConfidenceIndex() const __attribute__ ((always_inline))
    {
        return _confidenceIndex;
    }

    /**
     * Check whether the property is computed or not.
     *
     * @return mcsTRUE if the the property has been computed, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsComputed() const __attribute__ ((always_inline))
    {
        // Check whether property has been computed or not
        return isPropComputed(GetOriginIndex()) ? mcsTRUE : mcsFALSE;
    }

    /**
     * Check whether the property is set or not.
     *
     * @return mcsTRUE if the the property has been set, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsSet() const __attribute__ ((always_inline))
    {
        // Check if the string value is set (not null) or numerical is not NaN
        return (isnan(_numerical) && IS_NULL(_value)) ? mcsFALSE : mcsTRUE;
    }

    /**
     * Check whether the error is set or not.
     *
     * @return mcsTRUE if the the error has been set, mcsFALSE otherwise.
     */
    inline mcsLOGICAL IsErrorSet() const __attribute__ ((always_inline))
    {
        // Check if the error is not NaN
        return (isnan(_error)) ? mcsFALSE : mcsTRUE;
    }

    /**
     * Get error as a double.
     *
     * @param error pointer to store value.
     *
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetError(mcsDOUBLE *error) const;

    /**
     * Get property meta data.
     *
     * @return property meta data
     */
    inline const vobsSTAR_PROPERTY_META* GetMeta() const __attribute__ ((always_inline))
    {
        return _meta;
    }

    /**
     * Get property id.
     *
     * @return property id
     */
    inline const char* GetId() const __attribute__ ((always_inline))
    {
        return _meta->GetId();
    }

    /**
     * Get property name.
     *
     * @return property name
     */
    inline const char* GetName() const __attribute__ ((always_inline))
    {
        return _meta->GetName();
    }

    /**
     * Get property type.
     *
     * @return property type
     */
    inline vobsPROPERTY_TYPE GetType() const __attribute__ ((always_inline))
    {
        return _meta->GetType();
    }

    /**
     * Get property unit.
     *
     * @sa http://vizier.u-strasbg.fr/doc/catstd-3.2.htx
     *
     * @return property unit if present, NULL otherwise.
     */
    inline const char* GetUnit() const __attribute__ ((always_inline))
    {
        return _meta->GetUnit();
    }

    /**
     * Get property description.
     *
     * @sa http://vizier.u-strasbg.fr/doc/catstd-3.2.htx
     *
     * @return property description if present, NULL otherwise.
     */
    inline const char* GetDescription() const __attribute__ ((always_inline))
    {
        return _meta->GetDescription();
    }

    /**
     * Get property CDS link.
     *
     * @return property CDS link if present, NULL otherwise.
     */
    inline const char* GetLink() const __attribute__ ((always_inline))
    {
        return _meta->GetLink();
    }

    /**
     * Get property error meta data.
     *
     * @return property error meta data or NULL
     */
    inline const vobsSTAR_PROPERTY_META* GetErrorMeta() const __attribute__ ((always_inline))
    {
        return _meta->GetErrorMeta();
    }

    /**
     * Get property error id.
     *
     * @return property error id or "" if undefined property error meta data
     */
    inline const char* GetErrorId() const __attribute__ ((always_inline))
    {
        const vobsSTAR_PROPERTY_META* errorMeta = GetErrorMeta();
        if (IS_NULL(errorMeta))
        {
            return "";
        }
        return errorMeta->GetId();
    }

    /**
     * Get property error name.
     *
     * @return property error name or "" if undefined property error meta data
     */
    inline const char* GetErrorName() const __attribute__ ((always_inline))
    {
        const vobsSTAR_PROPERTY_META* errorMeta = GetErrorMeta();
        if (IS_NULL(errorMeta))
        {
            return "";
        }
        return errorMeta->GetName();
    }

    // Get the object summary as a string, including all its member's values
    const std::string GetSummaryString() const;

private:
    /* Memory footprint (sizeof) = 40 bytes (64-bytes alignment) */

    // metadata pointer (constant):
    const vobsSTAR_PROPERTY_META* _meta;    // 8 bytes

    // data:
    // Confidence index
    vobsCONFIDENCE_INDEX _confidenceIndex;  // 4 byte

    // Origin index
    vobsORIGIN_INDEX _originIndex;          // 4 byte

    // Value (as new char* for string values ONLY)
    char* _value;                           // 8 bytes

    // Value as a double-precision floating point
    mcsDOUBLE _numerical;                   // 8 bytes

    // Error as a double-precision floating point
    mcsDOUBLE _error;                       // 8 bytes


    void copyValue(const char* value);

    /**
     * Get property format.
     *
     * @return property format
     */
    inline const char* GetFormat(void) const __attribute__ ((always_inline))
    {
        // Return property format
        return _meta->GetFormat();
    }

    /**
     * Get given value as a string or "" if not set or not a numerical property
     *
     * @param value input value to format
     * @param converted numerical value as a string or NULL
     * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned.
     */
    mcsCOMPL_STAT GetFormattedValue(mcsDOUBLE value, mcsSTRING32& converted) const;

} ;

#endif /*!vobsSTAR_PROPERTY_H*/

/*___oOo___*/
