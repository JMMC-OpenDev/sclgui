#ifndef vobsREQUEST_H
#define vobsREQUEST_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsREQUEST.h,v 1.9 2004-11-30 10:32:31 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    26-Jul-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * vobsREQUEST class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/* 
 * Constants definition
 */
#define vobsNB_REQUEST_CONSTRAINTS 14       /**< Number of contraints */
#define vobsREQUEST_CONSTRAINT_NOT_SET  "00.00"  /**< Default value of the empty
                                          contraints */

/*
 * Type definition
 */
/**
 * definition of the request constraint as a string
 **/
typedef mcsSTRING32 vobsREQUEST_CONSTRAINT;

/*
 * Enumeration type definition
 */
/**
 * vobsCONSTRAINT_ID is an enumeration which allow correspondance between 
 * an id and a CONSTRAINT.
 */
typedef enum
{
    UNKNOWN_CONSTRAINT_ID = -1,         /**< id if no CONSTRAINT */      
    STAR_NAME_ID,                       /**< star name id */
    RA_ID,                              /**< right ascension coordinate */
    DEC_ID,                             /**< declinaison coordinate */
    STAR_WLEN_ID,                       /**< wavelength id */
    STAR_MAGNITUDE_ID,                  /**< So magnitude id */
    MIN_MAGNITUDE_RANGE_ID,             /**< min magnitude range id */
    MAX_MAGNITUDE_RANGE_ID,             /**< max magnitude range id */    
    SEARCH_BOX_RA_ID,                   /**< search Ra id */
    SEARCH_BOX_DEC_ID,                  /**< search Dec id */
    STAR_EXPECTED_VIS_ID,               /**< visibility id */
    STAR_MAX_ERR_VIS_ID,                /**< absolute errors id */
    OBSERVED_BAND_ID,                   /**< band id */
    BASEMIN_ID,
    BASEMAX_ID,
} vobsCONSTRAINT_ID;

/*
 * Class declaration
 */

/**
 * vobsREQUEST is a class which caracterise the user contraints.
 *
 * A constraint is a parameter as the magnitude or the coordinates... The star
 * which will be found should have a value which should fulfill with the
 * parameter.
 * 
 * vobsREQUEST methods allow to
 * \li modify
 * \li read
 * request contraints
 * 
 */
class vobsREQUEST
{
public:
    // Constructor
    vobsREQUEST();
    vobsREQUEST(const vobsREQUEST &request);

    // Destructor
    virtual ~vobsREQUEST();
    
    // Methods to set the request contraints
    virtual mcsCOMPL_STAT SetConstraint(char *constraint, char *value);
    virtual mcsCOMPL_STAT SetConstraint(vobsCONSTRAINT_ID constraintId, char *value);
     
    // Methods to retreive the request constraints
    virtual mcsCOMPL_STAT GetConstraint(char *constraint, char *value);
    virtual mcsCOMPL_STAT GetConstraint(vobsCONSTRAINT_ID constraintId, char *value) const;
    virtual mcsCOMPL_STAT GetConstraint(char *constraint, int *value);
    virtual mcsCOMPL_STAT GetConstraint(vobsCONSTRAINT_ID constraintId , int *value) const;
    virtual mcsCOMPL_STAT GetConstraint(char *constraint, float *value);
    virtual mcsCOMPL_STAT GetConstraint(vobsCONSTRAINT_ID constraintId , float *value) const;

    // Method to check if the request is build correctly
    virtual mcsLOGICAL Check(void);
    
    // Method to print out all request constraints
    virtual void Display(void);
    
protected:
    // Method to obtain id from the CONTRAINT
    virtual vobsCONSTRAINT_ID Constraint2Id(char *constraint) const;
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    vobsREQUEST& operator=(const vobsREQUEST&);

    /** Table containing the request constraints */
    vobsREQUEST_CONSTRAINT _constraints[vobsNB_REQUEST_CONSTRAINTS];
};
#endif /*!vobsREQUEST_H*/


/*___oOo___*/
