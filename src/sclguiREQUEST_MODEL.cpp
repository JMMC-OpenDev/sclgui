/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiREQUEST_MODEL.cpp,v 1.4 2006-03-03 15:28:17 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/11/05 15:42:53  gzins
 * Renamed BuildFromMessage to Parse
 *
 * Revision 1.2  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.1  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of sclguiREQUEST_MODEL class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiREQUEST_MODEL.cpp,v 1.4 2006-03-03 15:28:17 scetre Exp $"; 

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "sclguiREQUEST_MODEL.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiREQUEST_MODEL::sclguiREQUEST_MODEL()
{
}

/**
 * Class destructor
 */
sclguiREQUEST_MODEL::~sclguiREQUEST_MODEL()
{
}

/*
 * Public methods
 */
/**
 * Build request from a message
 *
 * @param msg message
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiREQUEST_MODEL::Parse(msgMESSAGE &msg)
{
    logTrace("sclguiREQUEST_MODEL::Parse()");

    // Build the request object from the parameters of the received command
    if (Parse(msg.GetBody()) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}

/**
 * Parses the GETCAL command parameters.
 *
 * It retrieves the GETCAL command parameter values and set its corresponding
 * members according.
 *
 * @param cmdParamLine GETCAL command parameters
 *
 * @return Always mcsSUCCESS.
 */
mcsCOMPL_STAT sclguiREQUEST_MODEL::Parse(const char *cmdParamLine)
{
    logTrace("sclguiREQUEST_MODEL::Parse()");

    // Parse command param line with Parse method of sclsvrREQUEST class
    if (sclsvrREQUEST::Parse(cmdParamLine)== mcsFAILURE)
    {
        return mcsFAILURE;
    }

    // Notify attached views
    if (NotifyViews() == mcsFAILURE)
    { 
        return mcsFAILURE;
    }


    return mcsSUCCESS;
}
/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
