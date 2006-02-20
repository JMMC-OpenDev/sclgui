#ifndef sclsvrPrivate_H
#define sclsvrPrivate_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrPrivate.h,v 1.5 2005-10-26 11:27:24 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/03/06 20:28:24  gzins
 * Added sclsvrREQUEST_TAG and sclsvrFORMAT_TAG definition
 *
 * Revision 1.3  2005/02/07 15:00:13  gzins
 * Added CVS log as modification history
 *
 * sccmgr    23-Nov-2004  Created
 *
 ******************************************************************************/

/**
 * @file
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
#define MODULE_ID "sclsvr"

// Tag used to stored request into file
#define sclsvrREQUEST_TAG "# REQUEST = "

// Tag used to stored format into file
#define sclsvrFORMAT_TAG  "# FORMAT = "

#ifdef __cplusplus
}
#endif


#endif /*!sclsvrPrivate_H*/

/*___oOo___*/
