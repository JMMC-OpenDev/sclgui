/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiREQUEST_VIEW.cpp,v 1.2 2005-09-16 13:44:01 scetre Exp $"
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
 *  Definition of sclguiREQUEST_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiREQUEST_VIEW.cpp,v 1.2 2005-09-16 13:44:01 scetre Exp $"; 
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
#include "sclguiREQUEST_VIEW.h"
#include "sclguiPrivate.h"

/**
 * Class constructor
 */
sclguiREQUEST_VIEW::sclguiREQUEST_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;

    // Place science star information
    _requestTextArea = new gwtTEXTAREA("--", 4, 50, "No Help");
    _requestTextArea->SetVerticalOrientation(mcsTRUE);
    _requestTextArea->SetLabel("Science star");
    _requestTextArea->SetHelp("Resume of the research request");
}

/**
 * Class destructor
 */
sclguiREQUEST_VIEW::~sclguiREQUEST_VIEW()
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
mcsCOMPL_STAT sclguiREQUEST_VIEW::Update()
{
    logTrace("sclguiREQUEST_VIEW::Update()");
   
    sclsvrREQUEST *request;

    // Get the request of the model associated to this view
    request = _model->GetRequest();

    // Get the value in the request in order to view constraints on the panel
    ostringstream out;
    out << "NAME\tRAJ2000\tDEJ2000\tMag";
    out << request->GetSearchBand();
    out << "\tBase-max\tLambda\tDiamVK\n";
    out << "---------\t-----------\t-----------\t------\t--------\t---------\t--------\n";
    out << request->GetObjectName() << "\t" << request->GetObjectRa() << "\t" 
        << request->GetObjectDec() << "\t" << request->GetObjectMag() << "\t";
    out << request->GetMaxBaselineLength() << "\t" 
        << request->GetObservingWlen() << "\t";

    _requestTextArea->SetText(out.str());
    
    return mcsSUCCESS;
}

/**
 * Return the text area associated to this view
 *
 * @return text area
 */
gwtTEXTAREA * sclguiREQUEST_VIEW::GetRequestTextArea()
{
    logTrace("sclguiREQUEST_VIEW::GetRequestTextArea()");

    return _requestTextArea;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
