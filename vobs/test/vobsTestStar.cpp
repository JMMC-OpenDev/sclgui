/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsTestStar.cpp,v 1.11 2006-03-03 15:17:43 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2005/11/16 10:45:14  scetre
 * Updated vobs test
 *
 * Revision 1.9  2005/02/13 08:54:00  gzins
 * Updated after vobs classes changes
 * Added CVS log as modifification history
 *
 * Revision 1.8  2005/02/04 10:04:55  gzins
 * Clean-up
 *
 ******************************************************************************/

static char *rcsId __attribute__ ((unused))="@(#) $Id: vobsTestStar.cpp,v 1.11 2006-03-03 15:17:43 scetre Exp $"; 

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
#include "timlog.h"

/*
 * Local Headers 
 */
#include "vobsSTAR.h"
#include "vobsSTAR_LIST.h"
#include "vobsPrivate.h"

int main(int argc, char *argv[])
{
    mcsInit(argv[0]);

    logSetStdoutLogLevel(logTEST);
    timlogStart(MODULE_ID, logINFO, "98", "testStar");
    logInfo("Starting ...");

    vobsSTAR star;
    for (int el = 0; el < star.NbProperties(); el++)
    {
        printf("%s\t", star.GetNextProperty((mcsLOGICAL)(el==0))->GetName());
    }
    printf("\n");
    star.SetPropertyValue(vobsSTAR_POS_EQ_RA_MAIN, "-03 47 29.08", "");
    mcsFLOAT ra;
    star.GetRa(ra);
    logTest("ra (deg) = %f", ra);

    star.SetPropertyValue(vobsSTAR_POS_EQ_DEC_MAIN, "24 06 18.5", "");
    mcsFLOAT dec;
    star.GetDec(dec);
    logTest("dec (deg) = %f", dec);

    vobsSTAR star2=star;
    for (int el2 = 0; el2 < star2.NbProperties(); el2++)
    {
        printf("%s\t", star2.GetNextProperty((mcsLOGICAL)(el2==0))->GetValue());
    }
    printf("\n");
    
    logInfo("Exiting ...");
    exit(EXIT_SUCCESS);
}

/*___oOo___*/