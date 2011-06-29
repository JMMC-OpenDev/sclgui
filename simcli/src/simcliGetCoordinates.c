/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Brief description of the class file, which ends at this dot.
 * 
 */


/* 
 * System Headers
 */
#include <stdio.h>
#include <stdlib.h>
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
 * Public functions definition
 */
mcsCOMPL_STAT simcliGetCoordinates (char *name,
                                    char *ra,
                                    char *dec)
{
    logTrace("simcliGetCoordinates()"); 
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

    /* Replace blank by '_', and add '!' at end of the star name */
    char starName[256] ;
    char *p;
    memset(starName, '\0', 256); 
    strcpy(starName, name);
    strcat(starName,"!") ; 
    p=strchr(starName, '_');
    while (p != NULL) 
    {
        *p = ' ';
        p=strchr(starName, '_');  
    }

    /* Call simbad */
    mcsCOMPL_STAT status = mcsSUCCESS; 
    hh = simbad_connect(NULL,NULL,userid,passwd) ;
    if (hh < 0)
    {
        return -1;
    }
    nitems = simbad_query(hh,starName,"") ;
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
    else
    {
        status = mcsFAILURE;
        strcpy(ra, "");
        strcpy(dec, "");
    }
    simbad_disconnect(hh) ;

    return status;
}



/*___oOo___*/
