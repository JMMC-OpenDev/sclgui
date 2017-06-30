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
#include "mcs.h"
#include "thrd.h"

/*
 * Local Headers
 */
#include "simcli.h"
#include "simcliPrivate.h"

/**
 * Shared mutex to circumvent un thread safe simbad client
 */
thrdMUTEX simcliMutex = MCS_MUTEX_STATIC_INITIALIZER;

#define SIMCLI_LOCK() {                            \
    if (thrdMutexLock(&simcliMutex) == mcsFAILURE) \
    {                                              \
        return mcsFAILURE;                         \
    }                                              \
}

#define SIMCLI_UNLOCK() {                            \
    if (thrdMutexUnlock(&simcliMutex) == mcsFAILURE) \
    {                                                \
        return mcsFAILURE;                           \
    }                                                \
}

/*
 * Public functions definition
 */
mcsCOMPL_STAT simcliGetCoordinates(char *name,
                                   char *ra, char *dec,
                                   mcsDOUBLE *pmRa, mcsDOUBLE *pmDec,
                                   char *spType, char *objTypes,
                                   char* mainId)
{
    char
    userid[MAX_USERID + 1] = {"t039ga"},
    passwd[MAX_USERID + 1] = {"codeporte"};
    char* TheData;
    int hh;
    int HH, MM;
    mcsFLOAT SS;
    mcsDOUBLE ss;
    int DH, DM;
    mcsFLOAT DS;
    mcsDOUBLE ds;
    char sign;
    int nitems = 0, ndata;
    mcsDOUBLE pmRA, pmDEC;
    int i;
    char *token;

    /* Replace blank by '_', and add '!' at end of the star name */
    char starName[256];
    char *p;
    memset(starName, '\0', 256);
    strcpy(starName, name);
    strcat(starName, "!");
    p = strchr(starName, '_');
    while (p != NULL)
    {
        *p = ' ';
        p = strchr(starName, '_');
    }

    mcsCOMPL_STAT status = mcsSUCCESS;


    SIMCLI_LOCK();

    /* Call simbad */
    hh = simbad_connect(NULL, NULL, userid, passwd);
    if (hh < 0)
    {
        SIMCLI_UNLOCK();

        return mcsFAILURE;
    }

    nitems = simbad_query(hh, starName, NULL); /* no options */

    if (nitems >= 1)
    {
        /* retrieve 1st object */
        simbad_retrieve(hh, 0);

        ndata = simbad_findata(hh, "J", NULL);

        if (ndata == 1)
        {
            TheData = simbad_getdata(hh, 1);

            sscanf(strtok(TheData, " "), "%lf", &ss);
            /* other token */
            sscanf(strtok(NULL, " "), "%lf", &ds);
            ss = ss / 15.0;
            HH = ss;
            MM = ((ss - HH)*60.0);
            SS = (ss - HH)*3600 - (MM * 60);
            sign = (ds < 0.0) ? '-' : '+';
            ds = (ds < 0.0) ? -ds : ds;
            DH = ds;
            DM = ((ds - DH)*60.0);
            DS = (ds - DH)*3600 - (DM * 60);
        }
        else
        {
            HH = 0;
            MM = 0;
            SS = 0;
            DH = 0;
            DM = 0;
            DS = 0;
            sign = '+';
        }

        sprintf(ra, "%02d %02d %07.4f", HH, MM, SS);
        sprintf(dec, "%c%d %02d %06.3f", sign, DH, DM, DS);

        /* Proper motion */
        ndata = simbad_findata(hh, "P", NULL);

        if (ndata == 1)
        {
            TheData = simbad_getdata(hh, 1);

            /*
            "P"     Proper motions
            444.95 -570.94 [2.82 2.03 0] A 2007A&A...474..653V pmRa pmDec in mas/yr
             */
            sscanf(strtok(TheData, " "), "%lf", &pmRA);
            /* other token */
            sscanf(strtok(NULL, " "), "%lf", &pmDEC);
        }
        else
        {
            pmRA = 0.;
            pmDEC = 0.;
        }

        *pmRa = pmRA;
        *pmDec = pmDEC;

        /* Spectral type */
        ndata = simbad_findata(hh, "S", NULL);

        if (ndata == 1)
        {
            TheData = simbad_getdata(hh, 1);

            /*
            "S"     Spectral class                    Ascii string (max: 36 chars)
                    (only for stars)                  Ex: S F7:Ib-IIvar
             */
            sscanf(strtok(TheData, " "), "%s", spType);
        }
        else
        {
            strcpy(spType, "\0");
        }

        /* Object types */
        ndata = simbad_findata(hh, "C", NULL);

        if (ndata > 0)
        {
            strcpy(objTypes, ",");
            for (i = 1; i <= ndata; i++)
            {
                TheData = simbad_getdata(hh, i);

                /* trim space character (left/right) */

                /* get the first token */
                token = strtok(TheData, " ");

                /* walk through other tokens */
                while (token != NULL)
                {
                    strcat(objTypes, token);

                    token = strtok(NULL, " ");
                }

                strcat(objTypes, ",");
            }
        }
        else
        {
            strcpy(objTypes, "\0");
        }

        /* Main Id */
        strcpy(mainId, "\0");
        ndata = simbad_findata(hh, "I.0", NULL);

        if (ndata > 0)
        {
            /*
            "I"     Identification (alias)      Ascii string. 
                                                Max non definitive: 40 chars
                                                Ex: I GEN# +1.00008890A
             */
            TheData = simbad_getdata(hh, 1);

            /* trim space character (left/right) */

            /* get the first token */
            token = strtok(TheData, " ");

            /* walk through other tokens */
            while (token != NULL)
            {
                if (strlen(mainId) != 0) {
                    strcat(mainId, " ");
                }
                strcat(mainId, token);

                token = strtok(NULL, " ");
            }
        }
    }
    else
    {
        status = mcsFAILURE;
        strcpy(ra, "");
        strcpy(dec, "");

        *pmRa = 0.;
        *pmDec = 0.;

        strcpy(spType, "\0");
        strcpy(objTypes, "\0");
        strcpy(mainId, "\0");
    }
    simbad_disconnect(hh);

    SIMCLI_UNLOCK();

    return status;
}



/*___oOo___*/
