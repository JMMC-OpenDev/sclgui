/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: alxTestLD2UD.c,v 1.7 2011-03-10 08:07:48 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2011/02/23 15:30:10  lafrasse
 * Removed dates and file lines from output log.
 *
 * Revision 1.5  2010/01/28 16:25:12  lafrasse
 * Broaden test cases.
 * Added alxFlushUNIFORM_DIAMETERS() with FP_NAN support.
 * Enhanced log and outputs.
 *
 * Revision 1.4  2010/01/18 15:52:37  lafrasse
 * Added alxShowUNIFORM_DIAMETERS().
 *
 * Revision 1.3  2010/01/18 15:33:24  lafrasse
 * Added execution error test.
 *
 * Revision 1.2  2010/01/15 17:42:43  lafrasse
 * Added mnimal error management.
 *
 * Revision 1.1  2010/01/08 22:29:04  lafrasse
 * Added preliminary support for alxLD2UD.
 *
 ******************************************************************************/

/**
 * @file
 * brief description of the program, which ends at this dot.
 *
 * @synopsis
 * \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
 *                     [\e \<option1\> ... \e \<optionN\>] 
 *
 * @param param1 : description of parameter 1, if it exists
 * @param paramN : description of parameter N, if it exists
 *
 * @opt
 * @optname option1 : description of option 1, if it exists
 * @optname optionN : description of option N, if it exists
 * 
 * @details
 * OPTIONAL detailed description of the c main file follows here.
 * 
 * @usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * @filename fileName1 :  usage description of fileName1
 * @filename fileName2 :  usage description of fileName2
 *
 * @env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * @envvar envVar1 :  usage description of envVar1
 * @envvar envVar2 :  usage description of envVar2
 * 
 * @warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * @ex
 * OPTIONAL. Command example if needed
 * \n Brief example description.
 * @code
 * Insert your command example here
 * @endcode
 *
 * @sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * @sa \<entity to refer\>
 * 
 * @bug OPTIONAL. Known bugs list if it exists.
 * @bug Bug 1 : bug 1 description
 *
 * @todo OPTIONAL. Things to forsee list, if needed. 
 * @todo Action 1 : action 1 description
 * 
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: alxTestLD2UD.c,v 1.7 2011-03-10 08:07:48 mella Exp $"; 


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>


/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "alx.h"
#include "alxPrivate.h"


/*
 * Local Variables
 */

 

/* 
 * Signal catching functions  
 */



/* 
 * Main
 */

int main (int argc, char *argv[])
{
    /* Initializes MCS services */
    if (mcsInit(argv[0]) == mcsFAILURE)
    {
        /* Error handling if necessary */
        
        /* Exit from the application with FAILURE */
        exit (EXIT_FAILURE);
    }

    logSetStdoutLogLevel(logTRACE);
    logSetPrintDate(mcsFALSE);
    logSetPrintFileLine(mcsFALSE);

    alxUNIFORM_DIAMETERS ud;
    alxShowUNIFORM_DIAMETERS(&ud);
    alxFlushUNIFORM_DIAMETERS(&ud);
    alxShowUNIFORM_DIAMETERS(&ud);

    printf("alxComputeUDFromLDAndSP(1.185, \"K3III\"):\n");
    if (alxComputeUDFromLDAndSP(1.185, "K3III", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }

    printf("alxComputeUDFromLDAndSP(0.966557, \"B7III\"):\n");
    if (alxComputeUDFromLDAndSP(0.966557, "B7III", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }    

    printf("alxComputeUDFromLDAndSP(1.185, \"ZERTY\"):\n");
    if (alxComputeUDFromLDAndSP(1.185, "ZERTY", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        printf("There is a bug in error handling.\n");
    }

    printf("alxComputeUDFromLDAndSP(1, \"B7V\"):\n");
    if (alxComputeUDFromLDAndSP(1, "B7V", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }    

    printf("alxComputeUDFromLDAndSP(1, \"B7V\"):\n");
    if (alxComputeUDFromLDAndSP(1, "B7V", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }    
  
    printf("alxComputeUDFromLDAndSP(1, \"O5III\"):\n");
    if (alxComputeUDFromLDAndSP(1, "O5III", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }

    /* should be equivalent to O5III */
    printf("alxComputeUDFromLDAndSP(1, \"O4III\"):\n");
    if (alxComputeUDFromLDAndSP(1, "O4III", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }


    printf("alxComputeUDFromLDAndSP(1, \"M5III\"):\n");
    if (alxComputeUDFromLDAndSP(1, "M5III", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }

    /* should be equivalent to M5III */
    printf("alxComputeUDFromLDAndSP(1, \"M6III\"):\n");
    if (alxComputeUDFromLDAndSP(1, "M6III", &ud) == mcsFAILURE)
    {
        printf("ERROR\n");
    }
    else
    {
        alxShowUNIFORM_DIAMETERS(&ud);
    }

    /* Close MCS services */
    mcsExit();
    
    /* Exit from the application with SUCCESS */
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
