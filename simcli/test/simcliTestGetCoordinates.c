/*******************************************************************************
* JMMC project
*
* "@(#) $Id: simcliTestGetCoordinates.c,v 1.2 2005-02-03 06:05:57 gzins Exp $"
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
 */

static char *rcsId="@(#) $Id: simcliTestGetCoordinates.c,v 1.2 2005-02-03 06:05:57 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers 
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/* 
 * MCS Headers
 */
#include "log.h"

/*
 * Local Headers 
 */
#include "simcli.h"
#include "simcliPrivate.h"

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
