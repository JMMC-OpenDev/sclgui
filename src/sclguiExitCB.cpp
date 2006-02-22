/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiExitCB.cpp,v 1.1 2006-02-22 13:28:02 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiCONTROLLER class.
 */

static char *rcsId="@(#) $Id: sclguiExitCB.cpp,v 1.1 2006-02-22 13:28:02 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
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
#include "sclguiCONTROLLER.h"
#include "sclguiPrivate.h"
#include "sclguiErrors.h"

/**
 * Exit callback
 */
evhCB_COMPL_STAT sclguiCONTROLLER::ExitCB(msgMESSAGE &msg, void* userData)
{
    logTrace("sclguiCONTROLLER::ExitCB()");
    
    SetStatus(true, "Bye bye");

    _mainWindow.Hide();
    
    return evhSERVER::ExitCB(msg, userData);
}

/*___oOo___*/
