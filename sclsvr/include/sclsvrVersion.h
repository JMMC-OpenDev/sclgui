#ifndef sclsvrVersion_H
#define sclsvrVersion_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclsvrVersion.h,v 1.47 2011-03-03 13:12:51 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.46  2011/02/25 16:19:02  lafrasse
 * Fixed automated test throughout server code.
 *
 * Revision 1.45  2011/02/10 13:48:30  lafrasse
 * Increased default max number of stars retruned by CDS from 50 to 1000.
 * Removed I, J and H bands in Faint K.
 *
 * Revision 1.44  2011/01/13 14:26:51  lafrasse
 * Added I, J and H band in Faint K.
 *
 * Revision 1.43  2010/11/25 15:45:33  lafrasse
 * Removed CHARM querying from all scenarii.
 *
 * Revision 1.42  2010/11/24 15:27:27  lafrasse
 * Removed CHARM querying from Bright K scenario.
 *
 * Revision 1.41  2010/11/10 15:48:24  lafrasse
 * Removed "B-V<1" filter in Bright K scenario.
 * Removed EVH dependencies that prevented compilation under GCC 4.x and newer.
 *
 * Revision 1.40  2010/07/30 13:05:04  lafrasse
 * Added columns URLs, descriptions and units whenever available.
 *
 * Revision 1.39  2010/07/28 14:08:44  lafrasse
 * Renamed VFlag columns to BinFlag for detailled bright N.
 *
 * Revision 1.38  2010/07/27 11:58:36  lafrasse
 * Added sep1 and sep2 data retrieval from WDS catalog.
 *
 * Revision 1.37  2010/06/28 14:17:50  lafrasse
 * Moved CDS return limit to 1000.
 * Added properties and computations for UDs, Teff and LogG computed from SpectralType.
 *
 * Revision 1.36  2010/02/02 11:08:45  lafrasse
 * Changed output format for values read from catalog to maximum precision.
 *
 * Revision 1.35  2010/02/02 10:19:22  lafrasse
 * Changed default output precision format for numerical values to 3 significant
 * digits.
 *
 * Revision 1.34  2010/01/26 14:16:06  lafrasse
 * Added WDS catalog querying.
 *
 * Revision 1.33  2010/01/11 17:22:10  lafrasse
 * Added UD diameters columns computation.
 *
 * Revision 1.32  2009/12/17 15:14:31  lafrasse
 * *** empty log message ***
 *
 * Revision 1.31  2009/12/17 14:21:13  lafrasse
 * Changed MIDI Vflag column UCD from 'CODE_VARIAB' to 'CODE_VARIAB_MIDI' to
 * resolve bug #1259055516 form Daniel BONNEAU.
 *
 * Revision 1.30  2009/12/10 13:48:00  lafrasse
 * Enforced sceicen object detection to 1 arcsec using star separation.
 *
 * Revision 1.29  2009/05/04 15:21:34  lafrasse
 * Updated version number to 3.4.1 : Removed all references to unused CHARM catalog.
 *
 * Revision 1.28  2009/04/20 14:37:42  lafrasse
 * Added spectral binary detection (SBC9 catalog).
 *
 * Revision 1.27  2007/11/15 05:12:30  gzins
 * Updated version to 3.3.7
 *
 * Revision 1.26  2007/05/22 14:38:09  gzins
 * Updated version
 *
 * Revision 1.25  2007/05/11 15:45:59  gzins
 * Set version to 3.3.6b
 *
 * Revision 1.24  2007/04/27 09:10:08  gzins
 * Set version to 3.3.6a
 *
 * Revision 1.23  2007/03/15 12:33:24  scetre
 * Set version to 3.3.6
 *
 * Revision 1.22  2006/10/30 13:37:43  scetre
 * Set version to 3.3.5c
 *
 * Revision 1.21  2006/08/25 08:07:13  gzins
 * Set version to 3.3.5b
 *
 * Revision 1.20  2006/08/23 12:09:19  gzins
 * Set version to 3.3.5a
 *
 * Revision 1.19  2006/08/22 15:56:44  gzins
 * Set version to 3.3.5
 *
 * Revision 1.18  2006/06/19 16:17:51  gzins
 * Set version to 3.3.4a
 *
 * Revision 1.17  2006/05/11 13:58:31  mella
 * Set version to 3.3.4
 *
 * Revision 1.16  2006/04/19 12:04:40  gzins
 * Set version to 3.3.3a
 *
 * Revision 1.15  2006/04/18 11:28:07  gzins
 * Set version to 3.3.3
 *
 * Revision 1.14  2006/04/10 15:01:27  gzins
 * Set version to 3.3.2b
 *
 * Revision 1.13  2006/04/05 15:18:37  gzins
 * Set version to 3.3.2a
 *
 * Revision 1.12  2006/04/04 12:08:56  gzins
 * Set version to 3.3.2+++
 *
 * Revision 1.11  2006/03/28 13:40:25  scetre
 * Set version to 3.3.2++
 *
 * Revision 1.10  2006/03/07 08:01:43  scetre
 * Set version to 3.3.2+
 *
 * Revision 1.9  2006/02/22 11:22:16  gzins
 * Set version to 3.3.2
 *
 * Revision 1.8  2006/02/20 16:33:47  scetre
 * Changed version 3.3 to 3.3.1
 *
 * Revision 1.7  2006/02/20 10:19:50  scetre
 * Changed revision number from 3.3-beta to 3.3
 *
 * Revision 1.6  2006/01/26 13:01:45  scetre
 * Changed version number from 3.2 to 3.3-beta
 *
 * Revision 1.5  2005/10/26 11:27:24  lafrasse
 * Code review
 *
 * Revision 1.4  2005/09/13 11:08:52  scetre
 * Changed version from 3.1 to 3.2
 *
 * Revision 1.3  2005/03/10 15:13:05  scetre
 * changed version 3.0 -> 3.1
 *
 * Revision 1.2  2005/03/08 14:38:50  swmgr
 * Set version to 3.0
 *
 * Revision 1.1  2005/03/06 20:26:55  gzins
 * Created
 *
 ******************************************************************************/

/**
 * @file
 * Definition of the software version.
 */

/* The following piece of code alternates the linkage type to C for all 
functions declared within the braces, which is necessary to use the 
functions in C++-code.
*/
#ifdef __cplusplus
extern "C" {
#endif

// Software version 
#define sclsvrVERSION "3.8.0"

#ifdef __cplusplus
}
#endif


#endif /*!sclsvrVersion_H*/

/*___oOo___*/
