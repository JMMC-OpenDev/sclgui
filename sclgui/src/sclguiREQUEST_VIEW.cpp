/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiREQUEST_VIEW.cpp,v 1.5 2005-11-05 15:40:23 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.3  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of sclguiREQUEST_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiREQUEST_VIEW.cpp,v 1.5 2005-11-05 15:40:23 gzins Exp $"; 
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
sclguiREQUEST_VIEW::sclguiREQUEST_VIEW()
{
    // Set text area attributes
    _requestTextArea.SetRows(4);
    _requestTextArea.SetColumns(50);
    _requestTextArea.SetHelp("No Help");
    _requestTextArea.SetVerticalOrientation(mcsTRUE);
    _requestTextArea.SetLabel("Science star");

    // Add widget in the widget map
    Add(&_requestTextArea);
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
 * Update method
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclguiREQUEST_VIEW::Update()
{
    logTrace("sclguiREQUEST_VIEW::Update()");

    // Get the value in the request in order to view constraints on the panel
    ostringstream out;
    out << "NAME\tRAJ2000\tDEJ2000\tMag";
    out << _requestModel->GetSearchBand();
    out << "\tBase-max\tLambda\tDiamVK\n";
    out << "---------\t-----------\t-----------\t------\t--------\t---------\t--------\n";
    out << _requestModel->GetObjectName();
    out << "\t" << _requestModel->GetObjectRa() << "\t" ;
    out << _requestModel->GetObjectDec() << "\t";
    out << _requestModel->GetObjectMag() << "\t";
    out << _requestModel->GetMaxBaselineLength() << "\t";
    out << _requestModel->GetObservingWlen() << "\t";

    _requestTextArea.SetText(out.str());
    cout << "request" << out << endl;
    return mcsSUCCESS;
}

/**
 * Attach model in the request view
 *
 * @ request the model to attach
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is 
 * returned.
 */
mcsCOMPL_STAT sclguiREQUEST_VIEW::AttachModel(sclguiREQUEST_MODEL &requestModel)
{
    logTrace("sclguiREQUEST_VIEW::AddRequestModel()");

     // Attach to the request model
    _requestModel = &requestModel;

    return mcsSUCCESS;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
