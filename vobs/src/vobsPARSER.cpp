/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsPARSER.cpp,v 1.17 2005-02-11 10:41:55 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.16  2005/02/11 10:34:49  gzins
* Added GetPropertyId() method
* Improved parsing of CDATA
*
* Revision 1.15  2005/02/10 10:46:42  gzins
* Changed column name to parameter name
*
* Revision 1.14  2005/02/10 08:07:38  scetre
* changed parser and hd, hip, dm number id in order to get all of them even if
* they have the same UCD
*
* Revision 1.13  2005/02/10 06:28:29  gzins
* Fixed bug related to ERRO UCD handling
*
* Revision 1.12  2005/02/08 21:34:05  gzins
* Handled ERROR UCDs by renaming them; i.e adding previous UCD as prefix
*
* Revision 1.11  2005/02/08 20:38:37  gzins
* Added name of catalog from where data is coming from
* Changed some parameter types from char* to const char*
*
* Revision 1.10  2005/02/07 09:47:08  gzins
* Renamed vobsCDATA method to be compliant with programming standards; method
* name starts with capital
*
* Revision 1.9  2005/02/04 15:25:40  gzins
* Minor change in log message
*
* Revision 1.8  2005/02/04 07:40:53  gzins
* Limited number of logged messages for test
*
* Revision 1.7  2005/01/27 13:45:30  scetre
* Add functionnality to the parser to parse II/225 return
*
* Revision 1.6  2005/01/26 08:11:28  scetre
* change history
*
* scetre    06-Jul-2004  Created
* gzins     09-Dec-2004  Fixed cast problem with nez mcsLOGICAL enumerate
*
******************************************************************************/

static char *rcsId="@(#) $Id: vobsPARSER.cpp,v 1.17 2005-02-11 10:41:55 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <string.h>
#include <libxml/parser.h>
/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "misc.h"

/*
 * Local Headers 
 */
#include "vobsPARSER.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"

/*
 * Local Functions
 */
// Class constructor 
vobsPARSER::vobsPARSER()
{
}

// Class destructor 
vobsPARSER::~vobsPARSER()
{
}

/**
 * Load XML document from URI and parse it to extract the list of star.
 * 
 * This method loads the XML document from the URI, check it is  well formed
 * and parses it to extract the list of stars contained in the CDATA section.
 * The extracted star are stored in the list \em starList given as parameter.
 *
 * \param uri URI from where XML document has to be loaded.
 * \param catalogName catalog name from where XML document has been got.
 * \param starList list where star has to be put.
 * \param logFileName file to save the result of the asking
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned and
 * an error is added to the error stack. The possible errors are :
 * \li vobsERR_GDOME_CALL
 */
mcsCOMPL_STAT vobsPARSER::Parse(const char *uri,
                                const char *catalogName,
                                vobsSTAR_LIST &starList,
                                const char *logFileName)
{    
    GdomeDOMImplementation *domimpl;
    GdomeDocument          *doc;
    GdomeElement           *root;
    GdomeException         exc;
    
    vobsCDATA cData;
    logExtDbg("vobsPARSER::MainParser()");	

    // Set the catalog name
    cData.SetCatalogName(catalogName);

    // Get a DOMImplementation reference
    domimpl = gdome_di_mkref ();

    // Load a new document from the URI
    logTest("Get XML document from '%s'", uri);
    doc = gdome_di_createDocFromURI(domimpl, uri, GDOME_LOAD_PARSING, &exc);
    if (doc == NULL) 
    {
        errAdd(vobsERR_GDOME_CALL, "gdome_di_createDocFromURI", exc);
        gdome_doc_unref (doc, &exc);
        gdome_di_unref (domimpl, &exc);
        xmlCleanupParser();
        return mcsFAILURE;
    }

    // Get reference to the root element of the document
    root = gdome_doc_documentElement (doc, &exc);
    if (root == NULL)
    {
        errAdd(vobsERR_GDOME_CALL, "gdome_doc_documentElement", exc);
        gdome_el_unref(root, &exc);            
        gdome_doc_unref (doc, &exc);
        gdome_di_unref (domimpl, &exc);
        xmlCleanupParser();
        return mcsFAILURE;
    }

    // Begin the recursif look of the tree
    if (ParseXmlSubTree((GdomeNode *)root, &cData) == mcsFAILURE)
    {
        gdome_el_unref(root, &exc);            
        gdome_doc_unref (doc, &exc);
        gdome_di_unref (domimpl, &exc);
        xmlCleanupParser();
        return mcsFAILURE;
    }

    // Print out CDATA description
    if (logGetStdoutLogLevel() >= logDEBUG)
    {
        logDebug("CDATA description");
        logDebug("   Number of lines to be skipped : %d", 
                cData.GetNbLinesToSkip());
        logDebug("   Number of parameters in table : %d", cData.GetNbParams());
        char *paramName;
        char *ucdName;
        for (unsigned int i = 0; i < cData.GetNbParams(); i++)
        {
            // Table header
            if (i == 0)
            {
                logDebug("   +----------+--------------+--------------------+");
                logDebug("   | Param #  | Name         | UCD                |");
                logDebug("   +----------+--------------+--------------------+");
            }
            // Get the parameter name and UCD
            if (cData.GetNextParamDesc(&paramName, &ucdName,
                                       (mcsLOGICAL)(i==0)) == mcsFAILURE)
            {
                return mcsFAILURE;
            }

            logDebug("   |      %3d | %12s | %18s |",
                    i+1, paramName, ucdName);
            ++paramName;
            ++ucdName;

            // Table footer
            if (i == (cData.GetNbParams() -1))
            {
                logDebug("   +----------+--------------+--------------------+");
            }
        }
    }

    // If CDATA section has been found
    if (cData.GetNbLines() != 0)
    {
        // Save CDATA (if requested)
        if (logFileName != NULL)
        {
            cData.Save(logFileName);
        }

        // Parse the CDATA section
        starList.Clear();
        if (ParseCData(&cData, starList) == mcsFAILURE)
        {
            gdome_el_unref(root, &exc);            
            gdome_doc_unref (doc, &exc);
            gdome_di_unref (domimpl, &exc);
            xmlCleanupParser();
            starList.Clear();
            return mcsFAILURE;
        }
    }

    // Free the document structure and the DOMImplementation
    gdome_el_unref(root, &exc);            
    gdome_doc_unref (doc, &exc);
    gdome_di_unref (domimpl, &exc);
    xmlCleanupParser();
      
    return mcsSUCCESS;
}

/**
 * Parse recursively XML document.
 *
 * This method recursively parses the XML document (i.e. node by node), and
 * extract informations related to the CDATA section which contains the star
 * table. These informations are the name of the parameters with the
 * corresponding UCD, the number lines to be skipped in CDATA section and the
 * pointer to the CDATA buffer (see vobsCDATA data structure).
 * Informations about parameters are given by 'FIELD' node; 'name' attribute for
 * the parameter name and 'UCD' for UCD, and the number of line to skip is given
 * by 'CSV' node and 'headlines' attribute.
 *
 * \param node  XML document node to be parsed. 
 * \param cData data structure where CDATA description has to be stored.
 *
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned
 * and an error is added to the error stack. The possible error is :
 * \li vobsERR_GDOME_CALL
 *
 * \todo
 * nblinetojump problem
 */
mcsCOMPL_STAT vobsPARSER::ParseXmlSubTree(GdomeNode *node,
                                          vobsCDATA *cData)
{
    GdomeException exc;
    GdomeNodeList *nodeList;
    unsigned long nbChildren;
    GdomeDOMString *nodeName;
    GdomeNode *child;
    GdomeNamedNodeMap *attrList;
    unsigned long nbAttrs;
    GdomeDOMString *attrName;
    GdomeDOMString *attrValue;
    GdomeNode *attr;

    logExtDbg("vobsPARSER:ParseXmlSubTree()");

    // Get the node list containing all children of this node
    nodeList = gdome_n_childNodes (node, &exc);
    if (exc != GDOME_NOEXCEPTION_ERR)
    {
        errAdd(vobsERR_GDOME_CALL, "gdome_n_childNodes", exc);
        gdome_nl_unref(nodeList, &exc);
        return mcsFAILURE;
    }

    // Get the number of children
    nbChildren = gdome_nl_length (nodeList, &exc);
    if (exc != GDOME_NOEXCEPTION_ERR)
    {
        errAdd(vobsERR_GDOME_CALL, "gdome_nl_length", exc);
        gdome_nl_unref(nodeList, &exc);
        return mcsFAILURE;
    }

    // If there is no child; return
    if (nbChildren == 0)
    {
        gdome_nl_unref(nodeList, &exc);
        return mcsSUCCESS;
    }

    // For each child
    for (unsigned int i = 0; i < nbChildren; i++)
    {
        // Get the the child in the node list
        child = gdome_nl_item (nodeList, i, &exc);
        if (child == NULL) 
        {
            errAdd(vobsERR_GDOME_CALL, "gdome_nl_item", exc);
            gdome_n_unref(child, &exc);
            gdome_nl_unref(nodeList, &exc);
            return mcsFAILURE;
        }

        // If it is the CDATA section
        if (gdome_n_nodeType (child, &exc) == GDOME_CDATA_SECTION_NODE) 
        {
            /* Get CDATA */
            nodeName = gdome_cds_data(GDOME_CDS(child), &exc);
            if (exc != GDOME_NOEXCEPTION_ERR)
            {
                errAdd(vobsERR_GDOME_CALL, "gdome_cds_data", exc);
                gdome_str_unref(nodeName);
                gdome_n_unref(child, &exc);
                gdome_nl_unref(nodeList, &exc);
                return mcsFAILURE;
            }
            else
            {
                if (cData->AppendLines(nodeName->str) == mcsFAILURE)
                {
                    gdome_str_unref(nodeName);
                    gdome_n_unref(child, &exc);
                    gdome_nl_unref(nodeList, &exc);
                    return mcsFAILURE;
                }
            }
            gdome_str_unref(nodeName);
        }
        // If it is an element node, try to get information on attributes
        else if (gdome_n_nodeType (child, &exc) == GDOME_ELEMENT_NODE) 
        {
            // Get the node name
            nodeName = gdome_n_nodeName(child, &exc);
            if (exc != GDOME_NOEXCEPTION_ERR)
            {
                errAdd(vobsERR_GDOME_CALL, "gdome_n_nodeName", exc);
                gdome_str_unref(nodeName);
                gdome_n_unref(child, &exc);
                gdome_nl_unref(nodeList, &exc);
                return mcsFAILURE;
            }
            logDebug("Parsing node %s...", nodeName->str);

            // Get the attributes list
            attrList = gdome_n_attributes(child, &exc);
            if (exc != GDOME_NOEXCEPTION_ERR)
            {
                errAdd(vobsERR_GDOME_CALL, "gdome_n_attributes", exc);
                gdome_nnm_unref(attrList, &exc);
                gdome_str_unref(nodeName);
                gdome_n_unref(child, &exc);
                gdome_nl_unref(nodeList, &exc);
                return mcsFAILURE;
            }

            // Get the number of attributes
            nbAttrs = gdome_nnm_length (attrList, &exc);
            if (exc != GDOME_NOEXCEPTION_ERR)
            {
                errAdd(vobsERR_GDOME_CALL, "gdome_nnm_length", exc);
                gdome_nnm_unref(attrList, &exc);
                gdome_str_unref(nodeName);
                gdome_n_unref(child, &exc);
                gdome_nl_unref(nodeList, &exc);
                return mcsFAILURE;
            }

            // For each attribute
            for (unsigned int j=0; j < nbAttrs; j++)
            {
                // Get the ith attribute in the node list
                attr = gdome_nnm_item(attrList, j, &exc);
                if (exc != GDOME_NOEXCEPTION_ERR)
                {
                    errAdd(vobsERR_GDOME_CALL, "gdome_nnm_item", exc);
                    gdome_n_unref(attr, &exc);
                    gdome_nnm_unref(attrList, &exc);
                    gdome_str_unref(nodeName);
                    gdome_n_unref(child, &exc);
                    gdome_nl_unref(nodeList, &exc);
                    return mcsFAILURE;
                }
                else
                {
                    // Get the attribute name
                    attrName = gdome_n_nodeName(attr, &exc);
                    if (exc != GDOME_NOEXCEPTION_ERR)
                    {
                        errAdd(vobsERR_GDOME_CALL, "gdome_n_nodeName", exc);
                        gdome_str_unref(attrName);
                        gdome_n_unref(attr, &exc);
                        gdome_nnm_unref(attrList, &exc);
                        gdome_str_unref(nodeName);
                        gdome_n_unref(child, &exc);
                        gdome_nl_unref(nodeList, &exc);
                        return mcsFAILURE;
                    }

                    // Get the attribute name
                    attrValue = gdome_n_nodeValue(attr, &exc);
                    if (exc != GDOME_NOEXCEPTION_ERR)
                    {
                        errAdd(vobsERR_GDOME_CALL, "gdome_n_nodeValue", exc);
                        gdome_str_unref(attrValue);
                        gdome_str_unref(attrName);
                        gdome_n_unref(attr, &exc);
                        gdome_nnm_unref(attrList, &exc);
                        gdome_str_unref(nodeName);
                        gdome_n_unref(child, &exc);
                        gdome_nl_unref(nodeList, &exc);
                        return mcsFAILURE;
                    }

                    // If it is the name a parameter of CDATA 
                    if ((strcmp(nodeName->str, "FIELD") == 0) &&
                        (strcmp(attrName->str, "name")  == 0))
                    {
                        cData->AddParamName(attrValue->str); 
                    }

                    // If it is the UCD name of the corresponding
                    // parameter
                    if ((strcmp(nodeName->str, "FIELD") == 0) &&
                        (strcmp(attrName->str, "ucd")  == 0))
                    {
                        cData->AddUcdName(attrValue->str); 
                    }

                    // If it is the number of lines to be skipped
                    // before accessing to data in CDATA table 
                    // NOTE: Skip one line more than the value given by
                    // CDS because the CDATA buffer always contains
                    // an empty line at first.
                    if ((strcmp(nodeName->str, "CSV") == 0) &&
                        (strcmp(attrName->str, "headlines")  == 0))
                    {
                        cData->SetNbLinesToSkip(atoi(attrValue->str) + 1); 
                    }
                    gdome_str_unref(attrValue);                    
                    gdome_str_unref(attrName);                    
                }
                gdome_n_unref(attr, &exc);                
            }
            // If there are children nodes, parse corresponding XML sub-tree
            if (gdome_n_hasChildNodes (child, &exc))
            {
                if (ParseXmlSubTree(child, cData) == mcsFAILURE)
                {
                    gdome_str_unref(nodeName);
                    gdome_n_unref(child, &exc);
                    gdome_nl_unref(nodeList, &exc);
                    return mcsFAILURE;
                }
            }
            gdome_str_unref(nodeName);
            gdome_nnm_unref(attrList, &exc);            
        }
        // Free child instance
        if (exc != GDOME_NOEXCEPTION_ERR)
        {
            errAdd(vobsERR_GDOME_CALL, "gdome_n_unref", exc);
            gdome_nl_unref(nodeList, &exc);
            gdome_n_unref (child, &exc);
            return mcsFAILURE;
        }
        gdome_n_unref(child, &exc);        
    }

    gdome_nl_unref(nodeList, &exc);
    
    return mcsSUCCESS;
}

/**
 * Parse the CDATA section of XML document.
 *
 * This method parses the CDATA section defined by the \em cData parameters.
 * The CDATA section contains a list of stars which are stored in a table
 * where each line corresponds to a star; i.e. all star properties are placed
 * in one line, and separated by the '\\t' character. The list of star
 * properties to be extracted is given by \em ucd.ucdName list.
 *
 * The first lines of the CDATA containing the description of the parameters
 * has to be skipped. The number of lines to be skipped is given by \em
 * ucd.nbLineToJump 
 *
 * The found stars are put in the \em starList parameter. 
 * 
 * \param cData    data structure describing the CDATA section.
 * \param starList list where star has to be put.
 * 
 * \return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is returned and
 * an error is added to the error stack. The possible error is :
 * \li vobsERR_INVALID_CDATA_FORMAT
 */
mcsCOMPL_STAT vobsPARSER::ParseCData(vobsCDATA *cData,
                                     vobsSTAR_LIST &starList)
{
    logExtDbg("vobsPARSER::ParseCData()");  

    // Retreive the catalog name form where the data is coming from
    const char *origin;
    origin = cData->GetCatalogName();

    // For each line in buffer, get the value for each defined UCD (value are
    // separated by '\t' character), store them in star object and add this
    // new star in the list.
    char *linePtr=NULL;
    char *delimiters = "\t";
    do
    {
        // Get next line
        linePtr = cData->GetNextLine(linePtr);
        
        if (linePtr != NULL)
        {
            char line[1024];
            int  nbUcd;
            char *paramName;
            char *ucdName;
            char *ucdValue;
            char *propId;
            vobsSTAR star;

            // Copy line into temporary buffer
            strcpy(line, linePtr);

            // Number of UCDs per line
            nbUcd = cData->GetNbParams();

            // Scan UCD list
            char *nextLinePtr;
            char *currLinePtr=line;

            // temporary variable to parse in case of II/225
            mcsSTRING256 wlen;
            mcsSTRING256 flux;
            strcpy(wlen, "");
            strcpy(flux, "");

            for (int j=0; j < nbUcd; j++)
            {
                // Get the parameter name and UCD
                if (cData->GetNextParamDesc(&paramName, 
                                            &ucdName,
                                            (mcsLOGICAL)(j==0)) == mcsFAILURE)
                {
                    return mcsFAILURE;
                }

                // Get the UCD value
                ucdValue = strtok_r(currLinePtr, delimiters, &nextLinePtr);
                if (ucdValue == NULL)
                {
                    // End of line reached; stop UCD scan
                    break;
                }
                currLinePtr = nextLinePtr;

                // If UCD is not a known property ID
                if (star.IsProperty(ucdName) == mcsFALSE)
                {
                    // Check if UCD and parameter association correspond to a
                    // known property
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
                    if (strcmp(propId, vobsSTAR_INST_WAVELENGTH_VALUE) == 0)
                    {
                        strcpy(wlen, ucdValue); 
                    }
                    // If flux is found, save it
                    else if (strcmp(propId, vobsSTAR_PHOT_FLUX_IR_MISC) == 0)
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

                        // Set star property
                        if (star.SetPropertyValue(propId, ucdValue, 
                                                  origin) == mcsFAILURE)
                        {
                            return mcsFAILURE;
                        }
                    }
                }
                
                // If wavelength and flux have been found, set the corresponding
                // magnitude
                if ((strcmp(wlen, "") != 0) && (strcmp(flux, "") != 0))
                {
                    // Get the wavelength value 
                    mcsFLOAT lambdaValue;
                    if (sscanf(wlen, "%f" , &lambdaValue) == 1)
                    {
                        // Determnine to corresponding magnitude
                        char *magId;
                        if (lambdaValue == 1.25)
                        {
                            magId = vobsSTAR_PHOT_JHN_J;
                        }
                        else if (lambdaValue == 1.65)
                        {
                            magId = vobsSTAR_PHOT_JHN_H;
                        }
                        else if (lambdaValue == 2.20)
                          {
                            magId = vobsSTAR_PHOT_JHN_K;
                        }
                        else
                        {
                            magId = NULL;
                        }
     
                        // If the given flux correspond to an expected magnitude
                        if (magId != NULL)
                        {
                            logDebug("Flux = %s and wlen = %s ==> mag %s",
                                     flux, wlen, magId);
                            star.SetPropertyValue(magId, flux, origin); 
                        }
                    }
                }
            }

            // Put now the star in the star list
            if (starList.AddAtTail(star) == mcsFAILURE)
            {
                return mcsFAILURE;
            }
        }
    } while (linePtr != NULL);

    return mcsSUCCESS;
}

/**
 * Get the property ID corresponding to the given parameter name and UCD. 
 *
 * This method returns the property ID corresponding to the parameter name and
 * UCD, or NULL if they do not correspond to an existing star property.
 *
 * \param paramName parameter name
 * \param ucdName UCD name
 *
 * \return property Id or NULL
 */
char *vobsPARSER::GetPropertyId(const char *paramName, const char *ucdName)
{

    // Star identifiers 
    if (strcmp(ucdName, "ID_ALTERNATIVE") == 0)
    {
        if (strcmp(paramName, "HD") == 0)
        {
            return vobsSTAR_ID_HD;
        }
        else if (strcmp(paramName, "HIP") == 0)
        {
            return vobsSTAR_ID_HIP;
        }
        else if (strcmp(paramName, "DM") == 0)
        {
            return vobsSTAR_ID_DM;
        }
    }

    // Diameters
    if (strcmp(ucdName, "EXTENSION_DIAM") == 0)
    {
        if (strcmp(paramName, "LD") == 0)
        {
            return vobsSTAR_LD_DIAM;
        }
        else if (strcmp(paramName, "UD") == 0)
        {
            return vobsSTAR_UD_DIAM;
        }
        else if (strcmp(paramName, "UDDK") == 0)
        {
            return vobsSTAR_UDDK_DIAM;
        }
    }

    // Diameter errors
    if (strcmp(ucdName, "ERROR") == 0)
    {
        if (strcmp(paramName, "e_LD") == 0)
        {
            return vobsSTAR_LD_DIAM_ERROR;
        }
        else if (strcmp(paramName, "e_UD") == 0)
        {
            return vobsSTAR_UD_DIAM_ERROR;
        }
        else if (strcmp(paramName, "e_UDDK") == 0)
        {
            return vobsSTAR_UDDK_DIAM_ERROR;
        }
    }

    // No property corresponding to the parameter name/UCD
    return NULL;
}
/*___oOo___*/
