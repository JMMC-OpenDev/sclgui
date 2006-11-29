#ifndef vobsCDATA_H
#define vobsCDATA_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsCDATA.h,v 1.26 2006-08-22 14:45:42 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.25  2006/04/10 11:55:09  scetre
* Added tabulation in ucd list on extended format. Update the read of this new file
*
* Revision 1.24  2005/11/24 13:16:51  scetre
* Added test on CODE_VARIB ucd and TYC1 for ID_ALTERNATIVE
*
* Revision 1.23  2005/10/14 08:44:24  scetre
* Updated p77 according to JMMC-MEM-2600-0004
*
* Revision 1.22  2005/09/07 12:12:46  scetre
* Changed range of flux 1.25 1.65 2.20 from 0 to 0.1
*
* Revision 1.21  2005/06/01 14:16:55  scetre
* Changed logExtDbg to logTrace
*
* Revision 1.20  2005/03/30 12:49:26  scetre
* Updated documentation
*
* Revision 1.19  2005/03/06 20:25:50  gzins
* Updated Store method to remove duplicated code
*
* Revision 1.18  2005/03/04 15:49:57  scetre
* Added in Store method the save with specific list of properties
*
* Revision 1.17  2005/02/22 15:48:38  lafrasse
* Updated because of a miscoDYN_BUFF::GetNextLine() API change
*
* Revision 1.16  2005/02/22 13:24:02  scetre
* Fixed bug in cast of wlen value when parsing magnitude from the flux
*
* Revision 1.15  2005/02/17 17:38:01  gzins
* Removed trailing and leading spaces of read values when parsing buffer
*
* Revision 1.14  2005/02/17 15:39:36  gzins
* Fixed minor bug related to remaining '\0' at the end of buffer when storing list in buffer
*
* Revision 1.13  2005/02/16 16:18:00  scetre
* Fixed remaining bugs in Extract and Unpack methods
*
* Revision 1.12  2005/02/16 15:55:42  gzins
* Fixed bug in AppendLines; missing CR after each line insertion
*
* Revision 1.11  2005/02/16 15:26:09  gzins
* Updated call to miscoDYN_BUF::GetNextLine()
*
* Revision 1.10  2005/02/14 08:56:35  gzins
* Updated to skip empty line when extracting star from buffer
*
* Revision 1.9  2005/02/14 08:45:24  scetre
* change vobsSTAR class to obj class in template Store()
*
* Revision 1.8  2005/02/13 15:15:02  gzins
* Inherited from miscoDYN_BUF
* Added method to load from file or buffer
* Added method to store and extract list of stars
*
* Revision 1.7  2005/02/11 10:37:19  gzins
* Added AddParamsDesc() method
*
* Revision 1.6  2005/02/10 10:46:33  gzins
* Changed column name to parameter name
*
* Revision 1.5  2005/02/08 20:32:27  gzins
* Added name of catalog form where data is coming from
*
* Revision 1.4  2005/02/07 09:47:08  gzins
* Renamed vobsCDATA method to be compliant with programming standards; method name starts with capital
*
* Revision 1.3  2005/01/26 08:10:32  scetre
* change history
*
* scetre    15-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * vobsCDATA class declaration.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

/*
 * system header files
 */
#include <vector>

/*
 * MCS header files
 */
#include "log.h"
#include "mcs.h"
#include "misc.h"
#include "misco.h"

/*
 * Locale header files
 */
#include "vobsSTAR_LIST.h"

/*
 * Class declaration
 */
/**
 * vobsCDATA represent the CDATA part of the resulting xml files coming from
 * the CDS.
 *
 * It is build with several part :
 * \li a list of UCD name.
 * \li a list of parameter name.
 * \li a number of line to skip in order to get the information data in the
 * CDATA block
 * \li the body of the CDATA
 * \li the number of line stored in the CDATA
 **/
class vobsCDATA : public miscoDYN_BUF
{

public:
    // Constructor
    vobsCDATA();

    // Destructor
    virtual ~vobsCDATA();

    virtual mcsCOMPL_STAT SetCatalogName(const char *name);
    virtual const char   *GetCatalogName(void);

    virtual mcsCOMPL_STAT AddParamsDesc(char *paramNameLine, char *ucdNameLine);
    virtual mcsCOMPL_STAT AddParamName(const char *paramName);
    virtual mcsCOMPL_STAT AddUcdName(const char *ucdName);

    virtual mcsUINT32     GetNbParams(void);
    virtual mcsCOMPL_STAT GetNextParamDesc(char **paramName,
                                           char **ucdName,
                                           mcsLOGICAL init);
    virtual mcsCOMPL_STAT SetNbLinesToSkip(mcsINT32 nbLines);
    virtual mcsUINT32     GetNbLinesToSkip(void);
    virtual mcsCOMPL_STAT AppendLines(char *buffer, mcsINT32 nbLinesToSkip);
    virtual mcsUINT32     GetNbLines(void);

    virtual mcsCOMPL_STAT LoadFile(const char *fileName); 
    virtual mcsCOMPL_STAT LoadBuffer(const char *buffer); 

    /**
     * Store a star list into the CDATA object
     *
     * It stores all the star properties into the internal buffer. The star
     * properties are separated by tabs with one line per star.
     *
     * \param object object contained in the list
     * \param objectList list of objects
     * \param ucdList list of UCD
     * \param extendedFormat if true, each property is stored with its
     * attributes (origin and confidence index), otherwise only only property is
     * stored.
     *
     * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
     * returned. 
     */
    template <class obj, class list>
        mcsCOMPL_STAT vobsCDATA::Store(obj &object, list &objectList,
                                       vobsSTAR_PROPERTY_ID_LIST ucdList, 
                                       mcsLOGICAL extendedFormat=mcsFALSE)
        {
            logPrint("vobs", logTRACE, __FILE_LINE__, "vobsCDATA::Store()");

            vobsSTAR_PROPERTY_ID_LIST propIdList;

            // If the list is empty, then store all star properties
            if (ucdList.size() != 0)
            {
                propIdList = ucdList;
            }
            else
            {
                obj star;
                for (mcsINT32 propIdx = 0; propIdx < star.NbProperties(); propIdx++)
                {
                    vobsSTAR_PROPERTY *property;
                    property = star.GetNextProperty((mcsLOGICAL)(propIdx==0));
                    propIdList.push_back((char *)property->GetId());
                }
            }
            
            vobsSTAR_PROPERTY_ID_LIST::iterator propIdIterateur;
            obj star;
            // Write each property Id corresponding with the ucd into the
            // buffer
            propIdIterateur = propIdList.begin();
            while (propIdIterateur != propIdList.end())
            {
                vobsSTAR_PROPERTY *property;
                property = star.GetProperty((char *)(*propIdIterateur).data());
                AppendString(property->GetId());
                AppendString("\t");
                if (extendedFormat == mcsTRUE)
                {
                    AppendString("\t\t");
                }
                AddUcdName(property->GetId());
                propIdIterateur++;
            }
            AppendString("\n");

            // Write each property name corresponding with the ucd into the
            // buffer
            propIdIterateur = propIdList.begin();
            while (propIdIterateur != propIdList.end())
            {
                vobsSTAR_PROPERTY *property;
                property = star.GetProperty((char *)(*propIdIterateur).data());
                AppendString(property->GetName());
                AppendString("\t");
                if (extendedFormat == mcsTRUE)
                {
                    AppendString("\t\t");
                }
                AddParamName(property->GetName());
                propIdIterateur++;
            }
            AppendString("\n");

            // For each object of the list    
            obj *starPtr;
            mcsUINT32 starIdx;
            for (starIdx = 0; starIdx < objectList.Size(); starIdx++)
            {
                // Get each object of the list
                starPtr = (obj *)objectList.GetNextStar((mcsLOGICAL)
                                                        (starIdx==0));
                // For each property of the object
                propIdIterateur = propIdList.begin();
                while (propIdIterateur != propIdList.end())
                {
                    // Get each property
                    vobsSTAR_PROPERTY *property;
                    property =
                        starPtr->GetProperty((char *)(*propIdIterateur).data());  
                    
                    // Each star property is placed in buffer in form :
                    // 'value origin confidenceIndex'
                    AppendString(property->GetValue());
                    AppendString("\t");
                    if (extendedFormat == mcsTRUE)
                    {
                        AppendString(property->GetOrigin());
                        AppendString("\t");
                        mcsSTRING16 confidenceIndex;
                        sprintf(confidenceIndex, "%d", property->GetConfidenceIndex());
                        AppendString(confidenceIndex);
                        AppendString("\t");
                    }
                    propIdIterateur++;
                }

                // If it's not the last star of the list '\n' is written in
                // order to go to the next line
                if (starIdx != (objectList.Size() - 1))
                {
                    AppendString("\n");
                }
            }
            // Removed the '\0' character at the end of the buffer
            _dynBuf.storedBytes -= 1;

            return mcsSUCCESS;
        }


    /**
     * Parse the CDATA section
     *
     * This method parses the CDATA section defined by the parameter
     * description. The CDATA section contains a list of stars which are stored
     * in a table where each line corresponds to a star; i.e. all star
     * properties are placed in one line, and separated by the '\\t' character.
     * The list of star properties to be extracted is given by \em ucd.ucdName
     * list.
     *
     * The first lines of the CDATA containing the description of the parameters
     * has to be skipped. The number of lines to be skipped is given by \em
     * nbLineToJump 
     *
     * The found stars are put in the \em starList parameter. 
     * 
     * \param object object contained in the list
     * \param objectList list where star has to be put.
     * \param extendedFormat if true, each property is has been stored with its
     * attributes (origin and confidence index), otherwise only only property
     * has been stored.
     * 
     * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
     * returned and an error is added to the error stack.
     */
    template <class obj, class list>
        mcsCOMPL_STAT Extract(obj &object, list &objectList,
                              mcsLOGICAL extendedFormat=mcsFALSE)
        {
            logPrint("vobs", logTRACE, __FILE_LINE__, "vobsCDATA::Extract()");

            // For each line in buffer, get the value for each defined UCD
            // (value are separated by '\t' character), store them in object
            // object and add this new object in the list.
            const char*   from=NULL;
            mcsSTRING2048 line;
            mcsUINT32     maxLineLength = sizeof(line);
            mcsINT32      lineNum = 0;
            do
            {
                // Get next line
                from = GetNextLine(from, line, maxLineLength);
                lineNum++;

                logPrint("vobs", logDEBUG, __FILE_LINE__,
                         "Next line = %s\n", line);
                if ((lineNum > _nbLinesToSkip) && 
                    (from != NULL) && 
                    (miscIsSpaceStr(line) != mcsTRUE))
                {
                    mcsSTRING256 lineSubStrings[1024];
                    mcsUINT32    nbOfSubStrings;
                    mcsUINT32    nbUcd;
                    char        *paramName;
                    char        *ucdName;
                    char        *ucdValue;
                    char        *propId;
                    const char  *origin;
                    vobsCONFIDENCE_INDEX confidenceIndex;
                    obj          object;

                    // Split line into sub-strings 
                    if (miscSplitString(line, '\t', lineSubStrings, 
                                        1024, &nbOfSubStrings) == mcsFAILURE)
                    {
                        return mcsFAILURE;
                    }
                    // and remove trailind and leading blanks
                    for (mcsUINT32 i = 0; i < nbOfSubStrings; i++)
                    {
                        miscTrimString(lineSubStrings[i], " ");
                    }

                    // Determine the number of attributes per property
                    mcsUINT32 nbAttrs;
                    // If extended format then nb attributes per properties is
                    // 3 (value, origin, confidence index) else 1 (value only)
                    if (extendedFormat == mcsFALSE)
                    {
                        nbAttrs = 1;
                    }
                    else
                    {
                        nbAttrs = 3;
                    }
                        
                    // Number of UCDs per line
                    nbUcd = (mcsUINT32)GetNbParams();

                    // temporary variable to parse in case of II/225
                    mcsSTRING256 wlen;
                    mcsSTRING256 flux;
                    strcpy(wlen, "");
                    strcpy(flux, "");

                    for (mcsUINT32 propIdx = 0; propIdx < nbUcd; propIdx++)
                    {
                        // Get the parameter name and UCD
                        if (GetNextParamDesc
                            (&paramName, &ucdName,
                             (mcsLOGICAL)(propIdx==0)) == mcsFAILURE)
                        {
                            return mcsFAILURE;
                        }

                        // Get the UCD value
                        if ((propIdx * nbAttrs) < nbOfSubStrings)
                        {
                            ucdValue = lineSubStrings[propIdx * nbAttrs];
                            if (extendedFormat == mcsTRUE)
                            {
                                int value;
                                origin = lineSubStrings[(propIdx*nbAttrs) + 1];
                                sscanf(lineSubStrings[(propIdx*nbAttrs) + 2],
                                       "%d", &value);
                                confidenceIndex = (vobsCONFIDENCE_INDEX)value;
                            }
                            else
                            {
                                // In local catalog case, load the properties
                                // with the global catalog name as origin
                                origin = GetCatalogName();
                                confidenceIndex = vobsCONFIDENCE_HIGH;
                            }
                        }
                        else 
                        {
                            // End of line reached; stop UCD scan
                            break;
                        }

                        // If UCD is not a known property ID
                        if (object.IsProperty(ucdName) == mcsFALSE)
                        {
                            // Check if UCD and parameter association
                            // correspond to a known property
                            propId = GetPropertyId(paramName, ucdName);
                        }
                        // Else
                        else
                        {
                            // Property ID is the UCD
                            propId = ucdName;
                        }
                        // End if

                        // If it is a known property
                        if (propId != NULL)
                        {
                            // Specific treatement of the flux
                            // If wavelength is found, save it
                            if (strcmp(propId,
                                       vobsSTAR_INST_WAVELENGTH_VALUE) == 0)
                            {
                                strcpy(wlen, ucdValue); 
                            }
                            // If flux is found, save it
                            else if (strcmp(propId,
                                            vobsSTAR_PHOT_FLUX_IR_MISC) == 0)
                            {
                                strcpy(flux, ucdValue);
                            }
                            else
                            {
                                // Check if value if empty
                                if (miscIsSpaceStr(ucdValue) == mcsTRUE)
                                {
                                    ucdValue = vobsSTAR_PROP_NOT_SET;
                                }

                                // Set object property
                                if (object.SetPropertyValue
                                    (propId, ucdValue, 
                                     origin, confidenceIndex) == mcsFAILURE)
                                {
                                    return mcsFAILURE;
                                }
                            }
                        }

                        // If wavelength and flux have been found, set the 
                        // corresponding magnitude
                        if ((strcmp(wlen, "") != 0) && (strcmp(flux, "") != 0))
                        {
                            // Get the wavelength value 
                            mcsFLOAT lambdaValue;
                            if (sscanf(wlen, "%f" , &lambdaValue) == 1)
                            {
                                // Determnine to corresponding magnitude
                                char *magId;
                                if ((lambdaValue >= (mcsFLOAT)1.24) ||
                                    (lambdaValue <= (mcsFLOAT)1.26))
                                {
                                    magId = vobsSTAR_PHOT_JHN_J;
                                }
                                else if ((lambdaValue >= (mcsFLOAT)1.64) ||
                                         (lambdaValue <= (mcsFLOAT)1.66))
                                {
                                    magId = vobsSTAR_PHOT_JHN_H;
                                }
                                else if ((lambdaValue >= (mcsFLOAT)2.19) ||
                                         (lambdaValue <= (mcsFLOAT)2.21))
                                {
                                    magId = vobsSTAR_PHOT_JHN_K;
                                }
                                else if ((lambdaValue >= (mcsFLOAT)3.49) ||
                                         (lambdaValue <= (mcsFLOAT)3.51))
                                {
                                    magId = vobsSTAR_PHOT_JHN_L;
                                }
                                else if ((lambdaValue >= (mcsFLOAT)4.99) ||
                                         (lambdaValue <= (mcsFLOAT)5.01))
                                {
                                    magId = vobsSTAR_PHOT_JHN_M;
                                }
                                else if ((lambdaValue >= (mcsFLOAT)9.99) ||
                                         (lambdaValue <= (mcsFLOAT)10.01))
                                {
                                    magId = vobsSTAR_PHOT_JHN_N;
                                }
                                else
                                {
                                    magId = NULL;
                                }

                                // If the given flux correspond to an expected
                                // magnitude
                                if (magId != NULL)
                                {
                                    logPrint("vobs", logDEBUG, __FILE_LINE__,
                                             "Flux= %s and wlen= %s==>mag %s",
                                             flux, wlen, magId);
                                    object.SetPropertyValue(magId, flux,
                                                            origin); 
                                }
                            }
                        }
                    }

                    if (logGetStdoutLogLevel() >= logDEBUG)
                    {
                        object.Display(mcsTRUE);
                    }

                    // Put now the object in the object list
                    if (objectList.AddAtTail(object) == mcsFAILURE)
                    {
                        return mcsFAILURE;
                    }
                }
            } while (from != NULL);
            return mcsSUCCESS;
        }

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    vobsCDATA(const vobsCDATA&);
    vobsCDATA& operator=(const vobsCDATA&);
    
    mcsCOMPL_STAT SetParamsDesc(void);
    char *GetPropertyId(const char *paramName, const char *ucdName);
    
    std::vector<char*> _paramName; // Name of parameters
    std::vector<char*> _ucdName; // Name of corresponding UCD
    std::vector<char *>::iterator _paramNameIterator;
    std::vector<char *>::iterator _ucdNameIterator;

    int _nbLinesToSkip;          // Number of lines to be skipped in CDATA
                                 // section
    int _nbLines;                // Number of lines stored in buffer

    string _catalogName;         // Catalog name from where CDATA comming from 
};

#endif /*!vobsCDATA_H*/

/*___oOo___*/