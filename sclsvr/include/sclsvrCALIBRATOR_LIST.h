#ifndef sclsvrCALIBRATOR_LIST_H
#define sclsvrCALIBRATOR_LIST_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrCALIBRATOR_LIST.h,v 1.12 2005-02-08 04:38:50 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2005/02/07 14:59:51  gzins
 * Reformated file header
 *
 * Revision 1.10  2005/02/07 11:10:29  scetre
 * Removed obsolete class sclsvrSEARCH_CALIBRATOR trace and changed method name
 * in sclsvrCALIBRATOR_LIST
 *
 * Revision 1.9  2005/02/07 10:06:36  scetre
 * Changed filter method name in FilterBy...
 *
 * Revision 1.8  2005/02/07 09:15:57  gzins
 * Added method to copy list from another sclsvrCALIBRATOR_LIST instance
 *
 * Revision 1.7  2005/02/04 14:23:13  scetre
 * Sort method for GUI added
 *
 * scetre    15-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Brief description of the header file, which ends at this dot.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * header files
 */
#include<list>
#include "sclsvrCALIBRATOR.h"

/*
 * Class declaration
 */
class sclsvrCALIBRATOR_LIST: public vobsSTAR_LIST
{ 
public:
    sclsvrCALIBRATOR_LIST();//constructor
    virtual ~sclsvrCALIBRATOR_LIST();//destructor

    virtual mcsCOMPL_STAT Copy(vobsSTAR_LIST& list);
    virtual mcsCOMPL_STAT Copy(sclsvrCALIBRATOR_LIST& list);
    virtual mcsCOMPL_STAT CopyIn(sclsvrCALIBRATOR_LIST *list,
                                 mcsLOGICAL filterDiameterNok = mcsFALSE,
                                 mcsLOGICAL filterVisibilityNok = mcsFALSE);
    virtual mcsCOMPL_STAT AddAtTail(sclsvrCALIBRATOR &calibrator);
    virtual mcsCOMPL_STAT AddAtTail(vobsSTAR &star);
 
    virtual mcsCOMPL_STAT Complete(sclsvrREQUEST &request);
    
    //virtual void Display();
    
    virtual mcsCOMPL_STAT Pack(miscDYN_BUF *buffer);
    virtual mcsCOMPL_STAT UnPack(miscDYN_BUF *buffer);
   
    // Method need by the graphical user interface
    virtual mcsCOMPL_STAT GetCoherentDiameter(sclsvrCALIBRATOR_LIST *list);
    virtual mcsCOMPL_STAT GetVisibilityOk(sclsvrCALIBRATOR_LIST *list);
    virtual mcsCOMPL_STAT FilterByDistanceSeparation(char *scienceStarRa,
                                                     char *scienceStarDec,
                                                     mcsFLOAT raRange,
                                                     mcsFLOAT decRange);
    virtual mcsCOMPL_STAT FilterByMagnitude(char *band,
                                            mcsFLOAT magValue,
                                            mcsFLOAT magRange);
    virtual mcsCOMPL_STAT FilterBySpectralType(std::list<char *> spectTypeList);
    virtual mcsCOMPL_STAT FilterByLuminosityClass(std::list<char *> luminosityList);
    virtual mcsCOMPL_STAT FilterByVisibility(mcsFLOAT visMax);
    virtual mcsCOMPL_STAT FilterByVariability(mcsLOGICAL authorized=mcsTRUE);
    virtual mcsCOMPL_STAT FilterByMultiplicity(mcsLOGICAL authorized=mcsTRUE);
    virtual mcsCOMPL_STAT Delete(unsigned int starNumber);
    virtual mcsCOMPL_STAT Load();
protected:

private:
    // Declaration assignment operator as private
    // methods, in order to hide them from the users.
    sclsvrCALIBRATOR_LIST& operator=(const sclsvrCALIBRATOR_LIST&);
    sclsvrCALIBRATOR_LIST (const sclsvrCALIBRATOR_LIST& list);//copy constructor
};




#endif /*!sclsvrCALIBRATOR_LIST_H*/

/*___oOo___*/
