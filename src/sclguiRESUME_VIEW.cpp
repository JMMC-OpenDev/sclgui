/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiRESUME_VIEW.cpp,v 1.2 2005-09-16 13:44:01 scetre Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/07/07 05:07:21  gzins
 * Added - Applied Model-View-Controller (MVC) design
 *
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiRESUME_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiRESUME_VIEW.cpp,v 1.2 2005-09-16 13:44:01 scetre Exp $"; 
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
#include "sclguiRESUME_VIEW.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiRESUME_VIEW::sclguiRESUME_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;

    _resumeTextArea = new gwtTEXTAREA("--", 1, 50, "No Help");
    _resumeTextArea->SetHelp("Resume of the research result");
    _resumeTextArea->SetLabel("Results");
    _resumeTextArea->SetVerticalOrientation(mcsTRUE);
}

/**
 * Class destructor
 */
sclguiRESUME_VIEW::~sclguiRESUME_VIEW()
{
}

/*
 * Public methods
 */

/**
 * Update view method
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiRESUME_VIEW::Update()
{
    logTrace("sclguiRESUME_VIEW::Update()");
   
    // Update resume textfield
    ostringstream output;
    output << "Number of stars: " << _model->GetNbCDSReturn() << " found, "  
        << _model->GetNbCoherentDiamFound() << " with coherent diameter and "
        << _model->GetNbWithoutVarMult() 
        << " without variability and multiplicity";
    _resumeTextArea->SetText(output.str());
    
    return mcsSUCCESS;
}

/**
 * Return the text area associated to this view
 *
 * @return text area
 */
gwtTEXTAREA * sclguiRESUME_VIEW::GetResumeTextArea()
{
    logTrace("sclguiRESUME_VIEW::GetResumeTextArea()");

    return _resumeTextArea;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
