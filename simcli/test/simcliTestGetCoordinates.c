/*******************************************************************************
* JMMC project
*
* "@(#) $Id: simcliTestGetCoordinates.c,v 1.1 2004-11-17 10:12:54 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    17-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * brief description of the program, which ends at this dot.
 *
 * \synopsis
 * \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
 *                     [\e \<option1\> ... \e \<optionN\>] 
 *
 * \param param1 : description of parameter 1, if it exists
 * \param paramN : description of parameter N, if it exists
 *
 * \n
 * \opt
 * \optname option1 : description of option 1, if it exists
 * \optname optionN : description of option N, if it exists
 * 
 * \n
 * \details
 * OPTIONAL detailed description of the c main file follows here.
 * 
 * \usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * \filename fileName1 :  usage description of fileName1
 * \filename fileName2 :  usage description of fileName2
 *
 * \n
 * \env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * \envvar envVar1 :  usage description of envVar1
 * \envvar envVar2 :  usage description of envVar2
 * 
 * \n
 * \warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * \n
 * \ex
 * OPTIONAL. Command example if needed
 * \n Brief example description.
 * \code
 * Insert your command example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa <entity to refer>
 * 
 * \bug OPTIONAL. Known bugs list if it exists.
 * \bug Bug 1 : bug 1 description
 *
 * \todo OPTIONAL. Things to forsee list, if needed. 
 * \todo Action 1 : action 1 description
 * 
 */

static char *rcsId="@(#) $Id: simcliTestGetCoordinates.c,v 1.1 2004-11-17 10:12:54 scetre Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>


/*
 * Local Headers 
 */
#include "simcli.h"
#include "simcliPrivate.h"


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
    char name[256];
    char ra[256];
    char dec[256];
    char *p;
    int i;

    if (argc==1) 
    {
        exit(0);
    }
    *name=0;
    if (argc==2) 
    {
        strcat(name,argv[1]);
        p=strchr(name,'_');
        while ( p !=NULL) 
        {
            *p = ' ';
            p=strchr(name,'_');
        }
    }
    else 
    {
        for (i=1; i<argc-1; i++) 
        {
            strcat(name,argv[i]);
            strcat(name," ");
        } 
        strcat(name,argv[i]);
    }
    if (name[0] == '\0') 
    {
        exit(EXIT_FAILURE);
    }
    strcat(name,"!") ; 

    if (simcliGetCoordinates(name, ra, dec) == -1)
    {
        exit (EXIT_FAILURE);    
    }
    printf("ra  =  %s\n", ra);
    printf("dec = %s\n", dec);
       
    /* Exit from the application with SUCCESS */
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
