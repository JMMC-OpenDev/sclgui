/*******************************************************************************
* JMMC project
* 
* "@(#) $Id: simcliGetCoordinates.c,v 1.1 2004-11-17 10:12:54 scetre Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    17-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * Brief description of the class file, which ends at this dot.
 * 
 * OPTIONAL detailed description of the class file follows here.
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
 * OPTIONAL. Code example if needed
 * \n Brief example description.
 * \code
 * Insert your code example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa <entity to refer>
 * 
 * \bug OPTIONAL. Bugs list if it exists.
 * \bug For example, description of the first bug
 * \bug For example, description of the second bug
 * 
 * \todo OPTIONAL. Things to forsee list, if needed. 
 * \todo Action1 : description of action 1.
 * 
 */

static char *rcsId="@(#) $Id: simcliGetCoordinates.c,v 1.1 2004-11-17 10:12:54 scetre Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


/* 
 * System Headers
 */
#include <stdio.h>
#include <stdlib.h>
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
 * Local Functions declaration
 */



/* 
 * Local functions definition
 */


/*
 * Public functions definition
 */
int simcliGetCoordinates (char *name,
                          char *ra,
                          char *dec)
{
    char  
    userid[MAX_USERID+1]={"t039ga"}, 
    passwd[MAX_USERID+1]={"codeporte"} ;
    char* TheData;
    int hh ;
    int HH,MM;
    float SS;
    double ss;
    int DH, DM;
    float DS;
    double ds;
    char sign;
    int nitems=0, ndata;

    hh = simbad_connect(NULL,NULL,userid,passwd) ;
    if (hh < 0)
    {
        return -1;
    }
    nitems = simbad_query(hh,name,"") ;
    if (nitems >= 1)
    {
        simbad_retrieve(hh,0) ;  /* retrieve 1st object */
        ndata = simbad_findata(hh,"J","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            sscanf (strtok(TheData," "), "%lf", &ss);
            sscanf (strtok(NULL," "), "%lf", &ds);
            ss=ss/15.0;
            HH=ss;
            MM=((ss-HH)*60.0);
            SS=(ss-HH)*3600-(MM*60);
            sign=(ds<0.0)?'-':'+'; 
            ds = (ds<0.0)?-ds:ds;
            DH=ds;
            DM=((ds-DH)*60.0);
            DS=(ds-DH)*3600-(DM*60);
        }
        else
        {
            HH= 0;
            MM= 0;
            SS= 0;
            DH= 0;
            DM= 0;
            DS= 0;
            sign='+';
        }
       
        sprintf(ra, "%02d %02d %07.4f", HH, MM, SS );
        sprintf(dec,"%c%d %02d %06.3f",sign,DH,DM,DS);
    }
    simbad_disconnect(hh) ;
    if (strlen(dec)==0)
    {
        return -1;
    }
    return 0;
}



/*___oOo___*/
