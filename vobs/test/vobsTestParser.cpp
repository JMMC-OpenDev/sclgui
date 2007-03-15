/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsTestParser.cpp,v 1.6 2006-03-03 15:17:43 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2005/11/16 10:45:14  scetre
 * Updated vobs test
 *
 * Revision 1.4  2005/02/15 15:26:06  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.3  2005/02/13 16:01:08  gzins
 * Changed log level to logDEBUG
 *
 * Revision 1.2  2005/02/13 08:54:00  gzins
 * Updated after vobs classes changes
 * Added CVS log as modifification history
 *
 * scetre    12-Jul-2004  Created
 *
 ******************************************************************************/

static char *rcsId __attribute__ ((unused))="@(#) $Id: vobsTestParser.cpp,v 1.6 2006-03-03 15:17:43 scetre Exp $"; 

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;
#include <string.h>

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "vobsPARSER.h"
#include "vobsPrivate.h"

/*
 * Local Variables
 */

 
/* 
 * Signal catching functions  
 */


/*
 * Local Functions
 */



int main(int argc, char *argv[])
{
    // Initialize MCS services
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        // Error handling if necessary
        
        // Exit from the application with FAILURE
        exit (EXIT_FAILURE);
    }
    logSetStdoutLogLevel(logTEST);

    logInfo("Starting ...");

    vobsSTAR_LIST starList;
    vobsPARSER    parser;
    char          *uri;

    uri = "http://vizier.u-strasbg.fr/viz-bin/asu-xml?-source=I/280";
    logTest("Try to retreive the xml file at the URL: %s", uri);
    if (parser.Parse(uri, "I/280", starList) == mcsFAILURE)
    {
        errDisplayStack();
        errCloseStack();
        exit(EXIT_FAILURE);
    }

    logTest("\t Done.");
    logInfo("Size of the list = %d", starList.Size());

    logInfo("Exiting ...");
    exit(EXIT_SUCCESS);

}


/*___oOo___*/
