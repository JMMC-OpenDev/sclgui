/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: vobsVOTABLE.cpp,v 1.12 2006-03-16 09:40:20 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2006/03/03 15:03:28  scetre
 * Changed rcsId to rcsId __attribute__ ((unused))
 *
 * Revision 1.10  2006/03/01 12:40:29  mella
 * Add group to include origin and confidence indexes, ucd still needs to be
 * updated
 *
 * Revision 1.9  2006/02/24 15:03:39  lafrasse
 * Added error management (and disabled code to generate origin and confidence
 * index as VOTable cell attributes)
 *
 * Revision 1.8  2006/01/09 16:09:25  lafrasse
 * Updated <LINK...> generation code
 *
 * Revision 1.7  2006/01/06 16:00:29  lafrasse
 * Added CDS link to make star clickable in Aladin
 *
 * Revision 1.6  2005/12/22 10:38:45  scetre
 * Updated doxygen documentation
 *
 * Revision 1.5  2005/12/07 16:49:18  lafrasse
 * Added support for 'description' attribute in VOTable column descriptors 
 * FIELD.
 *
 * Revision 1.4  2005/12/07 15:28:20  lafrasse
 * Updated VOTable generation to include information about software version, 
 * request and date
 *
 * Revision 1.3  2005/12/06 08:30:21  lafrasse
 * Added support for 'ref' attribute in VOTable column descriptors FIELD.
 * Now generate validated VOTable 1.1.
 *
 * Revision 1.2  2005/12/02 17:43:42  lafrasse
 * *** empty log message ***
 *
 * Revision 1.1  2005/11/30 15:24:37  lafrasse
 * Exported VOTable generation code from vobsSTAR_LIST to vobsVOTABLE
 *
 ******************************************************************************/

/**
 * @file
 * Definition of vobsVOTABLE class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: vobsVOTABLE.cpp,v 1.12 2006-03-16 09:40:20 mella Exp $"; 

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "miscoDYN_BUF.h"

/*
 * Local Headers 
 */
#include "vobsVOTABLE.h"
#include "vobsSTAR.h"
#include "vobsPrivate.h"
#include "vobsErrors.h"


/**
 * Class constructor
 */
vobsVOTABLE::vobsVOTABLE()
{
}


/**
 * Class destructor
 */
vobsVOTABLE::~vobsVOTABLE()
{
}


/*
 * Public methods
 */
/**
 * Serialize a star list in a VOTable v1.1 XML file.
 *
 * @param starList the the list of stars to serialize
 * @param fileName the path to the file in which the VOTable should be saved
 * @param header header of the VO Table
 * @param softwareVersion software version
 * @param request user request
 *
 * @return mcsSUCCESS on successful completion, mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT vobsVOTABLE::Save(vobsSTAR_LIST& starList,
                                const char *fileName,
                                const char *header,
                                const char *softwareVersion,
                                const char *request)
{
    logTrace("vobsVOTABLE::Save()");

    miscoDYN_BUF buffer;

    // Add VOTable standard header
    buffer.AppendLine("<?xml version=\"1.0\"?>");
    buffer.AppendLine("");

    buffer.AppendLine("<VOTABLE version=\"1.1\"");
    buffer.AppendLine("         xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\"");
    buffer.AppendLine("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    buffer.AppendLine("         xsi:schemaLocation=\"http://www.ivoa.net/xml/VOTable/v1.1 http://www.ivoa.net/xml/VOTable/VOTable-1.1.xsd\">");
    buffer.AppendLine("");

    // Add header informations
    buffer.AppendLine(" <DESCRIPTION>");
    buffer.AppendLine("  ");
    buffer.AppendString(header);

    // Add request informations
    buffer.AppendLine("  Request parameters: ");
    buffer.AppendString(request);

    // Add current date
    mcsSTRING32 utcTime;
    if (miscGetUtcTimeStr(0, utcTime) == mcsFAILURE)
    {
        return mcsFAILURE;
    } 
    buffer.AppendLine("  Generated on (UTC): ");
    buffer.AppendString(utcTime);

    buffer.AppendLine(" </DESCRIPTION>");
    buffer.AppendLine("");

    // Add context specific informations
    buffer.AppendLine(" <COOSYS ID=\"J2000\" equinox=\"J2000.\" epoch=\"J2000.\" system=\"eq_FK5\"/>");
    buffer.AppendLine("");

    // Add software informations
    buffer.AppendLine(" <RESOURCE name=\"");
    buffer.AppendString(softwareVersion);
    buffer.AppendString("\">");

    buffer.AppendLine("  <TABLE name=\"");
    buffer.AppendString(fileName);
    buffer.AppendString("\">");

    // Get the first start of the list
    vobsSTAR* star = starList.GetNextStar(mcsTRUE);
    if (star == NULL)
    {
        errAdd(vobsERR_EMPTY_STAR_LIST);
        return mcsFAILURE;
    }

    // Serialize each of its properties with origin and confidence index
    // as VOTable column description (i.e FIELDS)
    mcsUINT32   i = 0;
    mcsSTRING16 tmp;
    vobsSTAR_PROPERTY* starProperty = star->GetNextProperty(mcsTRUE);
    while (starProperty != NULL)
    {
        // Add standard field header
        buffer.AppendLine("   <FIELD");

        // Add field name
        buffer.AppendString(" name=\"");
        const char* propertyName = starProperty->GetName();
        buffer.AppendString(propertyName);
        buffer.AppendString("\"");

        // Add field ID
        buffer.AppendString(" ID=\"");
        sprintf(tmp, "col%d", i);
        buffer.AppendString(tmp);
        buffer.AppendString("\"");

        // Add field ucd
        buffer.AppendString(" ucd=\"");
        buffer.AppendString(starProperty->GetId());
        buffer.AppendString("\"");

        // Add field ref
        if ((strcmp(propertyName, "RAJ2000") == 0) ||
            (strcmp(propertyName, "DEJ2000") == 0))
        {
            buffer.AppendString(" ref=\"J2000\"");
        }

        // Add field datatype
        buffer.AppendString(" datatype=\"");
        switch (starProperty->GetType())
        {
            case vobsSTRING_PROPERTY:
                buffer.AppendString("char\" arraysize=\"*");
                break;

            case vobsFLOAT_PROPERTY:
                buffer.AppendString("float");
                break;

            default:
                // Assertion - unknow type
                break;
        }
        buffer.AppendString("\"");

        // Add field unit if it is not "-"
        const char* unit = starProperty->GetUnit();
        if (unit != NULL)
        {
            // If the unit exists (not the default "-")
            if (strcmp(unit, "-") != 0)
            {
                // Add field unit
                buffer.AppendString(" unit=\"");
                buffer.AppendString(unit);
                buffer.AppendString("\"");
            }
        }

        // Close FIELD opened markup
        buffer.AppendString(">");
        
        // Add field description if present
        const char* description = starProperty->GetDescription();
        if (description != NULL)
        {
            buffer.AppendLine("    <DESCRIPTION>");
            buffer.AppendString(description);
            buffer.AppendString("</DESCRIPTION>");
        }
        
        // Add field link if present
        const char* link = starProperty->GetLink();
        if (link != NULL)
        {
            buffer.AppendLine("    <VALUES/>");
            buffer.AppendLine("    <LINK href=\"");
            buffer.AppendString(link);
            buffer.AppendString("\"/>");
        }
        
        // Add standard field footer
        buffer.AppendLine("   </FIELD>");

        // Add ORIGIN field
        i++;
        buffer.AppendLine("   <FIELD type=\"hidden\"");

        // Add field name
        buffer.AppendString(" name=\"");
        buffer.AppendString(propertyName);
        buffer.AppendString(".origin\"");

        // Add field ID
        buffer.AppendString(" ID=\"");
        sprintf(tmp, "col%d", i);
        buffer.AppendString(tmp);
        buffer.AppendString("\"");

        // Add field ucd
        buffer.AppendString(" ucd=\"");
        buffer.AppendString(starProperty->GetId());
        buffer.AppendString(".origin\"");

        // Add field datatype
        buffer.AppendString(" datatype=\"char\" arraysize=\"*\"");

        // Close FIELD opened markup
        buffer.AppendString(">");

        // Add field description
        buffer.AppendLine("    <DESCRIPTION>Origin of property ");
        buffer.AppendString(propertyName);
        buffer.AppendString("</DESCRIPTION>");

        // Add standard field footer
        buffer.AppendLine("   </FIELD>");


        // Add CONFIDENCE field
        i++;
        buffer.AppendLine("   <FIELD type=\"hidden\"");

        // Add field name
        buffer.AppendString(" name=\"");
        buffer.AppendString(propertyName);
        buffer.AppendString(".confidence\"");

        // Add field ID
        buffer.AppendString(" ID=\"");
        sprintf(tmp, "col%d", i);
        buffer.AppendString(tmp);
        buffer.AppendString("\"");

        // Add field ucd
        buffer.AppendString(" ucd=\"");
        buffer.AppendString(starProperty->GetId());
        buffer.AppendString(".confidence\"");

        // Add field datatype
        buffer.AppendString(" datatype=\"char\" arraysize=\"*\"");

        // Close FIELD opened markup
        buffer.AppendString(">");

        // Add field description
        buffer.AppendLine("    <DESCRIPTION>Confidence index of property ");
        buffer.AppendString(propertyName);
        buffer.AppendString("</DESCRIPTION>");

        // Add standard field footer
        buffer.AppendLine("   </FIELD>");

        // Retrieve the next property
        starProperty = star->GetNextProperty(mcsFALSE);
        i++;
    }

    // Serialize each of its properties as group description
    i = 0;
    starProperty = star->GetNextProperty(mcsTRUE);
    while (starProperty != NULL)
    {
        // Add standard group header
        buffer.AppendLine("   <GROUP");

        // Add group name
        buffer.AppendString(" name=\"");
        const char* propertyName = starProperty->GetName();
        buffer.AppendString(propertyName);
        buffer.AppendString("\"");

        // Add group ucd
        buffer.AppendString(" ucd=\"");
        buffer.AppendString(starProperty->GetId());
        buffer.AppendString("\"");

        // Close GROUP opened markup
        buffer.AppendString(">");
        
        // Add field description
        buffer.AppendLine("    <DESCRIPTION>");
        buffer.AppendString(propertyName);
        buffer.AppendString(" with its origin and confidence index</DESCRIPTION>");
 
        // Bind main field ref
        sprintf(tmp, "col%d", i);
        buffer.AppendLine("   <FIELDref ref=\"");
        buffer.AppendString(tmp);
        buffer.AppendString("\" />");

        // Bind ORIGIN field ref
        sprintf(tmp, "col%d", i+1);
        buffer.AppendLine("   <FIELDref ref=\"");
        buffer.AppendString(tmp);
        buffer.AppendString("\" />");

        // Bind CONFIDENCE field ref
        sprintf(tmp, "col%d", i+2);
        buffer.AppendLine("   <FIELDref ref=\"");
        buffer.AppendString(tmp);
        buffer.AppendString("\" />");

        // Add standard group footer
        buffer.AppendLine("   </GROUP>");
        
        // Retrieve the next property
        starProperty = star->GetNextProperty(mcsFALSE);
        i+=3;
    }
    // Serialize each star property value
    buffer.AppendLine("   <DATA>");
    buffer.AppendLine("    <TABLEDATA>");
    while (star != NULL)
    {
        // Add standard row header
        buffer.AppendLine("     <TR>");
        buffer.AppendLine("      ");

        mcsLOGICAL init = mcsTRUE;
        while((starProperty = star->GetNextProperty(init)) != NULL)
        {
            init = mcsFALSE;

            // Add standard column header beginning
            buffer.AppendString("<TD>");

            // Add value if it is not "-"
            const char* value = starProperty->GetValue();
            if (value != NULL)
            {
                // If the value exists (not the default "-")
                if (strcmp(value, "-") != 0)
                {
                    // Add value
                    buffer.AppendString(value);
                }
            }

            // Add standard column footer
            buffer.AppendString("</TD>");

            // Add ORIGIN value
            buffer.AppendString("<TD>");
            const char* origin = starProperty->GetOrigin();
            if (origin != NULL)
            {
                buffer.AppendString(origin);
            }

            // Add standard column footer
            buffer.AppendString("</TD>");
            
            // Add CONFIDENCE value
            buffer.AppendString("<TD>");
            if (starProperty->IsComputed() == mcsTRUE)
            {
                vobsCONFIDENCE_INDEX confidence =
                                             starProperty->GetConfidenceIndex();
                switch(confidence)
                {
                    case vobsCONFIDENCE_LOW:
                        buffer.AppendString("LOW");
                        break;

                    case vobsCONFIDENCE_MEDIUM:
                        buffer.AppendString("MEDIUM");
                        break;

                    case vobsCONFIDENCE_HIGH:
                        buffer.AppendString("HIGH");
                        break;
                }
            }

            // Add standard column footer
            buffer.AppendString("</TD>");
            
        }

        // Add standard row footer
        buffer.AppendLine("     </TR>");

        // Jump on the next star of the list
        star = starList.GetNextStar();
    }

    // Add SCALIB data footer
    buffer.AppendLine("    </TABLEDATA>");
    buffer.AppendLine("   </DATA>");
    buffer.AppendLine("  </TABLE>");
    buffer.AppendLine(" </RESOURCE>");

    // Add VOTable standard footer
    buffer.AppendLine("</VOTABLE>");

    // Try to save the generated VOTable in the specified file as ASCII
    return(buffer.SaveInASCIIFile(fileName));
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
