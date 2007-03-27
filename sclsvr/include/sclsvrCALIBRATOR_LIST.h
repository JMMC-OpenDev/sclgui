#ifndef sclsvrCALIBRATOR_LIST_H
#define sclsvrCALIBRATOR_LIST_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrCALIBRATOR_LIST.h,v 1.27 2006-04-05 15:12:26 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.26  2005/10/26 11:27:24  lafrasse
 * Code review
 *
 * Revision 1.25  2005/06/20 14:39:28  scetre
 * Removed filter to vobs unless visibility filter
 *
 * Revision 1.24  2005/06/01 14:18:54  scetre
 * Added filters and filter list objects.
 * Changed logExtDbg to logTrace
 *
 * Revision 1.23  2005/03/07 16:06:06  gzins
 * Removed automatic sort on visibility
 *
 * Revision 1.22  2005/03/06 20:34:23  gzins
 * Improved Save/Load methods; added file header, used REQUEST/FORMAT tags and added automatic format detection when loading file
 *
 * Revision 1.21  2005/03/06 10:48:30  gzins
 * Merged FilterBySpectralType and FilterByLuminosityClass methods
 *
 * Revision 1.20  2005/03/04 16:28:52  scetre
 * Changed Call to Save method
 *
 * Revision 1.19  2005/03/04 15:51:21  scetre
 * Updated call to Save method
 *
 * Revision 1.18  2005/03/03 16:48:22  scetre
 * Added GetScienceObject method
 *
 * Revision 1.17  2005/02/17 15:31:26  gzins
 * Added request parameter to Save and Load methods
 *
 * Revision 1.16  2005/02/16 17:34:23  gzins
 * Changed prototype for Unpack; used const char* instead of miscoDYN_BUF
 *
 * Revision 1.15  2005/02/14 14:12:46  scetre
 * Added Load and Save methods
 * change miscDYN_BUF to miscoDYN_BUF in pack and unpack methods
 *
 * Revision 1.14  2005/02/08 20:47:15  gzins
 * Updated Copy(); added filterDiameterNok and filterVisibilityNok parameters
 * Removed obsolete CopyIn(), GetCoherentDiameter() and GetVisibilityOk() methods
 *
 * Revision 1.13  2005/02/08 07:24:07  gzins
 * Changed char* to const char* when applicable
 *
 * Revision 1.12  2005/02/08 04:38:50  gzins
 * Updated for new vobsREQUEST API and used new sclsvrREQUEST class
 *
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
 * @file
 * Brief description of the header file, which ends at this dot.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * System header
 */
#include <list>

/*
 * Local header
 */
#include "sclsvrCALIBRATOR.h"


/*
 * Class declaration
 */
class sclsvrCALIBRATOR_LIST: public vobsSTAR_LIST
{ 
public:
    // Constructor
    sclsvrCALIBRATOR_LIST();
    // Destructor
    virtual ~sclsvrCALIBRATOR_LIST();

    virtual mcsCOMPL_STAT Copy(vobsSTAR_LIST& list);
    virtual mcsCOMPL_STAT Copy(sclsvrCALIBRATOR_LIST& list,
                               mcsLOGICAL copyDiameterNok = mcsTRUE);
    virtual mcsCOMPL_STAT AddAtTail(sclsvrCALIBRATOR &calibrator);
    virtual mcsCOMPL_STAT AddAtTail(vobsSTAR &star);
 
    virtual mcsCOMPL_STAT Complete(sclsvrREQUEST &request);
    
    virtual mcsCOMPL_STAT Pack(miscoDYN_BUF *buffer);
    virtual mcsCOMPL_STAT UnPack(const char *buffer);
   
    virtual mcsCOMPL_STAT Delete(unsigned int starNumber);
    virtual mcsCOMPL_STAT Delete(sclsvrCALIBRATOR_LIST &list);

    virtual mcsCOMPL_STAT Save(const char *filename,
                               vobsSTAR_PROPERTY_ID_LIST ucdList,
                               sclsvrREQUEST &request,
                               mcsLOGICAL extendedFormat=mcsFALSE);
    
    virtual mcsCOMPL_STAT Save(const char *filename,
                               sclsvrREQUEST &request,
                               mcsLOGICAL extendedFormat=mcsFALSE);

    virtual mcsCOMPL_STAT Load(const char *filename,
                               sclsvrREQUEST &request);
    
    virtual mcsCOMPL_STAT GetScienceObject(sclsvrCALIBRATOR &scienceObject);


protected:


private:
    // Declaration assignment operator as private
    // methods, in order to hide them from the users.
    sclsvrCALIBRATOR_LIST& operator=(const sclsvrCALIBRATOR_LIST&);
    sclsvrCALIBRATOR_LIST (const sclsvrCALIBRATOR_LIST& list);//copy constructor
};


#endif /*!sclsvrCALIBRATOR_LIST_H*/

/*___oOo___*/