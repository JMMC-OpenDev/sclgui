/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: simcliTestGetCoordinates.c,v 1.5 2006-05-11 13:04:57 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/02/15 14:50:51  gzins
 * Added CVS log as file modification history
 *
 * scetre    17-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * brief description of the program, which ends at this dot.
 * 
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: simcliTestGetCoordinates.c,v 1.5 2006-05-11 13:04:57 mella Exp $";

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
    int i;

    /* Get star name */
    memset(name, '\0', 256); 
    if (argc >= 2) 
    {
        for (i=1; i < argc; i++) 
        {
            strcat(name, argv[i]);
            if (i != (argc-1))
            {
                strcat(name," ");
            }
        }
    }
    else 
    {
        printf ("Usage %s <star name>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    if (simcliGetCoordinates(name, ra, dec) == -1)
    {
        printf ("Star '%s' not found in Simbad\n", name);
        exit (EXIT_FAILURE);    
    }
    printf("ra  =  %s\n", ra);
    printf("dec = %s\n", dec);
       
    /* Exit from the application with SUCCESS */
    exit (EXIT_SUCCESS);
}


/*___oOo___*/
