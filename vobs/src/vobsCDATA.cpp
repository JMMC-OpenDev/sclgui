/*******************************************************************************
* JMMC project
*
* "@(#) $Id: vobsCDATA.cpp,v 1.7 2005-02-07 09:47:08 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
* Revision 1.6  2005/01/31 13:31:38  scetre
* changed misc...Pointer in misc...
*
* Revision 1.5  2005/01/26 08:11:28  scetre
* change history
*
* scetre    15-Nov-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * vobsCDATA class definition.
 */

static char *rcsId="@(#) $Id: vobsCDATA.cpp,v 1.7 2005-02-07 09:47:08 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


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


/*
 * Local Headers 
 */
#include "vobsSTAR.h"
#include "vobsCDATA.h"
#include "vobsPrivate.h"

/*
 * Class constructor
 */
vobsCDATA::vobsCDATA()
{
    _nbLines = 0;
    _nbLinesToSkip = 0;
    miscDynBufInit(&_buffer);
}

/*
 * Class destructor
 */
vobsCDATA::~vobsCDATA()
{
    // Free all strings containing column names
    std::vector<char *>::iterator colName;
    for (colName = _colName.begin(); colName != _colName.end(); colName++)
    {
        free(*colName);
    }

    // Free all strings containing UCD names
    std::vector<char *>::iterator ucdName;
    for (ucdName = _ucdName.begin(); ucdName != _ucdName.end(); ucdName++)
    {
        free(*ucdName);
    }

    // Free all strings containing UCD names
    miscDynBufDestroy(&_buffer);

    _colName.clear();
    _ucdName.clear();
}

/*
 * Public methods
 */

/**
 * Adds a column description (column name) at the end of the list
 *
 * This method adds the description (column name) of a column in the
 * CDATA section. The description of the columns has to be done in the same
 * order they appear in the CDATA section.
 *
 * \param colName column name to be added to the list.
 *
 * \return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::AddColName(char *colName)
{
    logExtDbg("vobsCDATA::AddColName(%s)", colName);

    _colName.push_back(strdup(colName)); 

    return mcsSUCCESS;
}
/**
 * Adds a column description (UCD) at the end of the list
 *
 * This method adds the description (UCD) of a column in the
 * CDATA section. The description of the columns has to be done in the same
 * order they appear in the CDATA section.
 *
 * \param ucdName corresponding UCD to be added to the list.
 *
 * \return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::AddUcdName(char *ucdName)
{
    logExtDbg("vobsCDATA::AddUcdName(%s)", ucdName);

    _ucdName.push_back(strdup(ucdName)); 

    return mcsSUCCESS;
}

/**
 * Returns the number of lines to be skipped in CDATA.
 * \return 
 * The number of lines to be skipped in CDATA.
 */
mcsUINT32 vobsCDATA::GetNbColumns(void) 
{
    return _colName.size();
}
 
/**
 * Returns the next column description in the CDATA section.
 *
 * This method returns the pointers to the name and teh UCD of the next column
 * of the CDATA. If \em init is mcsTRUE, it returns the escription of the
 * first column.
 * 
 * \param colName pointer to the name of the next column
 * \param ucdName pointer to the corresponding UCD 
 * \param init
 * 
 * \return mcsSUCCESS or mcsFAILURE if the end of the column list is reached.
 */
mcsCOMPL_STAT vobsCDATA::GetNextColDesc(char **colName, char **ucdName,
                                        mcsLOGICAL init) 
{
    //logExtDbg("vobsCDATA::GetNextColDesc()");

    if (init == mcsTRUE)
    {
        // Get first element
        _colNameIterator = _colName.begin();
        _ucdNameIterator = _ucdName.begin();
    }
    else
    {
        // Get next element
        _colNameIterator++;
        _ucdNameIterator++;
        if ((_colNameIterator == _colName.end()) ||
            (_ucdNameIterator == _ucdName.end()))
        {
            return mcsFAILURE;
        }
    }

    *colName = *_colNameIterator;
    *ucdName = *_ucdNameIterator;

    return mcsSUCCESS;
}

/**
 * Set the number of lines to be skipped in CDATA. 
 *
 * \param nbLines number of lines to be skipped.
 *
 * \return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::SetNbLinesToSkip(mcsINT32 nbLines)
{
    logExtDbg("vobsCDATA::SetNbLinesToSkip()");

    _nbLinesToSkip = nbLines;

    return mcsSUCCESS;
}

/**
 * Returns the number of colum CDATA.
 * \return 
 * The number of colum in CDATA.
 */
mcsUINT32 vobsCDATA::GetNbLinesToSkip(void) 
{
    return _nbLinesToSkip;
}
 
/**
 * Append lines of the given buffer to the CDATA 
 *
 * This method appends the lines which are provided in the buffer into an
 * internal buffer. The lines of the given buffer must be seperated by '\n'.
 * The first lines, defined with SetNbLinesToSkip() method, are skipped. 
 * The GetNbLines() method gives the number of lines currently stored in
 * internal buffer. And they can be retrieved using GetNextLine().
 *
 * \param buffer pointer to the CDATA, which must be null-terminated
 *
 * \return
 * mcsSUCCESS, or mcsFAILURE if an error occurs when manipuling internal dynamic
 * buffer.
 */
mcsCOMPL_STAT vobsCDATA::AppendLines(char *buffer)
{
    logExtDbg("vobsCDATA::AppendLines()");

    // Store buffer into a temporary buffer
    miscDYN_BUF tmpBuffer;
    miscDynBufInit(&tmpBuffer);
    if (miscDynBufAppendString(&tmpBuffer, buffer) == mcsFAILURE)
    {
        return mcsFAILURE;
    }
    
    // Replace CR by '\0' in order to convert buffer to separated lines 
    miscReplaceChrByChr(miscDynBufGetBuffer(&tmpBuffer), '\n', '\0');

    logDebug("Buffer to process : %s", buffer);

    // Store usefull line into internal buffer; i.e skip header lines and
    // empty lines
    // NOTE: Skip one line more than the value given by _nbLinesToSkip because
    // the CDATA buffer always contains an empty line at first.
    int lineNum   = 0;
    char *linePtr = NULL;
    do 
    {
        linePtr = miscDynBufGetNextLine(&tmpBuffer, linePtr, mcsFALSE);
        lineNum++;
        if ((linePtr != NULL) && (lineNum > (_nbLinesToSkip +1)) &&
            (miscIsSpaceStr(linePtr) == mcsFALSE))
        {
            logDebug("   > Add line : %s", linePtr);
            
            if (miscDynBufAppendBytes
                (&_buffer, linePtr, strlen(linePtr) + 1) == mcsFAILURE)
            {
                miscDynBufDestroy(&tmpBuffer);
                return mcsFAILURE;
            }
            _nbLines++;
        }
        else
        {
            logDebug("   > Skip line : %s", linePtr);
        }
    } while (linePtr != NULL);

    miscDynBufDestroy(&tmpBuffer);

    return mcsSUCCESS;
}

/**
 * Returns the number of lines currently stored in internal buffer.
 *
 * \return 
 * The number of lines stored in internal buffer.
 */
mcsUINT32 vobsCDATA::GetNbLines(void) 
{
    return _nbLines;
}

/**
 * Return the next line stored in the internal buffer.
 *
 * \return
 * Pointer to buffer containing CDATA section.
 */
char * vobsCDATA::GetNextLine(char *linePtr)
{
    //logExtDbg("vobsCDATA::GetNextLine()");

    return (miscDynBufGetNextLine(&_buffer, linePtr, mcsFALSE));
}

/**
 * Save the buffer contents into file
 *
 * \return
 * Always mcsSUCCESS.
 */
mcsCOMPL_STAT vobsCDATA::Save(char *fileName)
{
    logExtDbg("vobsCDATA::Save()");

    FILE *filePtr;

    // If no file given, return;
    if (strcmp(fileName, "") ==0)
    {
        return mcsSUCCESS;
    }

    // Open file
    filePtr=fopen(miscResolvePath(fileName), "w+");
    if (filePtr==NULL)
    {
        logWarning("Could not open file '%s'", fileName);
        return mcsSUCCESS;
    } 

    // For each line in buffer, get the value for each defined UCD (value are
    // separated by '\t' character), store them in file.
    char *linePtr=NULL;
    char *delimiters = "\t";
    do
    {
        // Get next line
        linePtr = GetNextLine(linePtr);

        if (linePtr != NULL)
        {
            char line[1024];
            int  nbUcd;
            char *ucdValue;

            // Copy line into temporary buffer
            strcpy(line, linePtr);

            // Number of UCDs per line
            nbUcd = GetNbColumns();

            // Scan UCD list
            char *nextLinePtr;
            char *currLinePtr=line;
            for (int j=0; j < nbUcd; j++)
            {
                // Get the UCD value
                ucdValue = strtok_r(currLinePtr, delimiters, &nextLinePtr);
                if (ucdValue == NULL)
                {
                    // End of line reached; stop UCD scan
                    break;
                }
                else
                {
                    currLinePtr = nextLinePtr;

                    // Check if value if empty
                    if (miscIsSpaceStr(ucdValue) == mcsTRUE)
                    {
                        ucdValue = vobsSTAR_PROP_NOT_SET;
                    }

                    // Save UCD value
                    fprintf(filePtr, "%10s", ucdValue);
                }
            }

            // Add CR
            fprintf(filePtr, "\n");
        }
    } while (linePtr != NULL);

    // Close file
    fclose(filePtr);

    return mcsSUCCESS;
}


/*___oOo___*/
