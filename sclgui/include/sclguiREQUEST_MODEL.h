#ifndef sclguiREQUEST_MODEL_H
#define sclguiREQUEST_MODEL_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiREQUEST_MODEL.h,v 1.2 2005-11-05 15:42:57 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiREQUEST_MODEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "msg.h"

#include "vobs.h"
#include "sclsvr.h"
/*
 * Class declaration
 */

/**
 * Model Class of the request
 */
class sclguiREQUEST_MODEL : public fndMVC_MODEL, public sclsvrREQUEST
{

public:
    // Class constructor
    sclguiREQUEST_MODEL();

    // Class destructor
    virtual ~sclguiREQUEST_MODEL();

    virtual mcsCOMPL_STAT Parse(msgMESSAGE &msg);
    virtual mcsCOMPL_STAT Parse(const char *cmdParamLine);    

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiREQUEST_MODEL(const sclguiREQUEST_MODEL&);
    sclguiREQUEST_MODEL& operator=(const sclguiREQUEST_MODEL&);
};

#endif /*!sclguiREQUEST_MODEL_H*/

/*___oOo___*/
