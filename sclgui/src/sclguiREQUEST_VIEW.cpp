/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiREQUEST_VIEW.cpp,v 1.7 2006-03-06 17:12:02 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2006/03/03 15:28:17  scetre
 * Changed rcsId to rcsId __attribute__ ((unused))
 *
 * Revision 1.5  2005/11/05 15:40:23  gzins
 * Removed unused constructor
 *
 * Revision 1.4  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.3  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 * Definition of sclguiREQUEST_VIEW class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiREQUEST_VIEW.cpp,v 1.7 2006-03-06 17:12:02 lafrasse Exp $"; 

/* 
 * System Headers 
 */
#include <iostream>
#include <iomanip>
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

    mcsFLOAT diamVK = 0.0;
    mcsLOGICAL diamVKDefined = _requestModel->IsDiamVKDefined();
    if (diamVKDefined == mcsTRUE)
    {
        diamVK = _requestModel->GetDiamVK();
    }

    // Get the value in the request in order to view constraints on the panel
    ostringstream out;
    out << fixed;
    out << "NAME\tRAJ2000\tDEJ2000\tMag" << _requestModel->GetSearchBand()
        << "\tBase-max\tLambda\t";
    if (diamVKDefined == mcsTRUE)
    {
        out << "DiamVK";
    }
    out << endl;

    out << "---------\t-----------\t-----------\t------\t--------\t---------\t";
    if (diamVKDefined == mcsTRUE)
    {
        out << "--------";
    }
    out << endl;

    out <<                    _requestModel->GetObjectName()        << "\t"
        <<                    _requestModel->GetObjectRa()          << "\t"
        <<                    _requestModel->GetObjectDec()         << "\t"
        << setprecision(3) << _requestModel->GetObjectMag()         << "\t"
        << setprecision(2) << _requestModel->GetMaxBaselineLength() << "\t"
        <<                    _requestModel->GetObservingWlen()     << "\t";
    if (diamVKDefined == mcsTRUE)
    {
        out << setprecision(3) << diamVK;
    }

    _requestTextArea.SetText(out.str());
    cout << "request" << out << endl;
    return mcsSUCCESS;
}

/**
 * Attach model in the request view
 *
 * @param request the model to attach
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
