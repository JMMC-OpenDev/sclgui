/*++++++++++++++
.IDENTIFICATION simcli.h
.LANGUAGE       C
.AUTHOR         Francois Ochsenbein, Marc Wenger [CDS]
.ENVIRONMENT    SIMBAD Client/Server
.KEYWORDS
.VERSION  1.0   05-Jun-1993
.COMMENTS       Declaration of Functions for Simbad-Client
---------------*/
#ifndef SIMCLI_DEF
#define SIMCLI_DEF	0	/* To avoid recursive inclusions */

#ifndef _PARAMS
#ifdef __STDC__
#define _PARAMS(A)      A       /* ANSI */
#else
#define _PARAMS(A)      ()      /* Traditional */
#endif
#endif

#define MAX_USERID 16

#include "mcs.h"

int simbad_connect _PARAMS((char *node, char *serv, char *uname, char *pwd));
int simbad_disconnect _PARAMS((int handle) );
int simbad_query _PARAMS((int handle, char *question, char *options));
int simbad_retrieve _PARAMS((int handle, int number) );
int simbad_findata _PARAMS((int handle, char *astrotype, char *option));
char *simbad_getdata _PARAMS((int handle, int number) );
char *simbad_telldata _PARAMS((int handle) );
char *simbad_error _PARAMS((int handle) );

void cliopt _PARAMS((int n, char **v, char *h, char *s, char *u, char *p));

mcsCOMPL_STAT simcliGetCoordinates(char *name,
                                   char *ra, char *dec,
                                   mcsDOUBLE *pmRa, mcsDOUBLE *pmDec,
                                   char *spType, char *objTypes,
                                   char* mainId);

#endif /* SIMCLI_DEF */
