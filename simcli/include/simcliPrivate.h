#ifndef simcliPrivate_H
#define simcliPrivate_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: simcliPrivate.h,v 1.3 2005-02-15 14:50:51 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * sccmgr    16-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Brief description of the header file, which ends at this dot.
 */

/* The following piece of code alternates the linkage type to C for all 
functions declared within the braces, which is necessary to use the 
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif


/*
 * Module name
 */
#define MODULE_ID "simcli"

typedef void (*FCT)();
 
/* Local functions */
int sk_setb();
int sk_iosave();
int sk_read();
int sk_get();
int sk_gets();
int sk_getl();
int sk_write();
int sk_put();
int sk_puts();
int sk_putl();
int sk_close();
FCT sk_errfct();
FILE *sk_setlog();
int sk_fromserver();

int ClientOpen();
int ClientTreat();
int ClientSendFile();
int ClientRcvFile();
void ClientClose();

void simbad_appli();
int simbad_errno();

int TestVersionAppli();

int gethostname();
#ifdef __cplusplus
}
#endif


#endif /*!simcliPrivate_H*/

/*___oOo___*/
