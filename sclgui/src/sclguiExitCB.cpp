/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiCONTROLLER class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiExitCB.cpp,v 1.2 2006-03-03 15:28:17 scetre Exp $"; 

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
