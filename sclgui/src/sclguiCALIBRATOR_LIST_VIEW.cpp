/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiCALIBRATOR_LIST_VIEW.cpp,v 1.17 2006-08-23 12:12:36 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.16  2006/04/18 14:57:38  gzins
 * Changed UNSO to USNO
 *
 * Revision 1.15  2006/04/05 15:09:22  gzins
 * Updated according to new sclsvrCALIBRATOR_LIST API
 *
 * Revision 1.14  2006/03/22 10:45:48  scetre
 * Added TYC2, TYC3, GLAT and GLON in the detail labels
 *
 * Revision 1.13  2006/03/07 07:54:07  scetre
 * Minor changes
 *
 * Revision 1.12  2006/03/03 15:28:17  scetre
 * Changed rcsId to rcsId __attribute__ ((unused))
 *
 * Revision 1.11  2006/02/28 13:08:28  scetre
 * Changed USNO color
 *
 * Revision 1.10  2006/01/23 14:13:44  scetre
 * Removed corrected magnitude in the label.
 * Added extinction ratio, Av in the details view of the list
 *
 * Revision 1.9  2006/01/18 09:07:22  scetre
 * Removed unused U magnitude
 *
 * Revision 1.8  2005/12/22 10:15:41  scetre
 * Added distance in table 'Show Details' and 'Hide Details' for all kind of object
 *
 * Revision 1.7  2005/12/12 14:12:48  scetre
 * Used cousin properties from sclsvr module instead of vobs
 *
 * Revision 1.6  2005/12/06 08:39:12  scetre
 * Updated label for faint
 *
 * Revision 1.5  2005/11/30 10:35:58  scetre
 * Updated with new filter
 *
 * Revision 1.4  2005/11/24 15:17:03  scetre
 * Added USNO catalog legend.
 * Added complete label for faint K research
 *
 * Revision 1.3  2005/11/23 08:42:27  scetre
 * Added possibility to build label for faint K
 *
 * Revision 1.2  2005/10/18 12:52:48  lafrasse
 * First code revue
 *
 * Revision 1.1  2005/10/11 15:24:15  scetre
 * New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of sclguiCALIBRATOR_LIST_VIEW class.
 */

static char *rcsId __attribute__ ((unused))="@(#) $Id: sclguiCALIBRATOR_LIST_VIEW.cpp,v 1.17 2006-08-23 12:12:36 gzins Exp $"; 

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
#include "sclguiCALIBRATOR_LIST_VIEW.h"
#include "sclguiPrivate.h"


/**
 * Define color value for catalog
 */
#define sclguiWHITE_COLOR               "#ffffff"
#define sclguiCONFIDENCE_LOW_COLOR      "#6e6e6e"
#define sclguiCONFIDENCE_MEDIUM_COLOR   "#d8d8d8"
#define sclguiCONFIDENCE_HIGH_COLOR     "#ffffff"
#define sclguiI_280_COLOR               "#ffb6b6"
#define sclguiII_225_COLOR              "#f6b6ff"
#define sclguiII_7A_COLOR               "#b9b6ff"
#define sclguiII_246_COLOR              "#b6e8ff"
#define sclguiMERAND_COLOR              "#c89292"
#define sclguiV_50_COLOR                "#b6ffe6"
#define sclguiCHARM_COLOR               "#dfffb6"
#define sclguiCHARM2_COLOR              "#b7ff5a"
#define sclguiDENIS_COLOR               "#fff4b6"
#define sclguiDENIS_JK_COLOR            "#fffadd"
#define sclguiI_196_COLOR               "#78fb8b"
#define sclguiBORDE_COLOR               "#9778fb"
#define sclguiV_36B_COLOR               "#88a0a6"
#define sclgui_MIDI_COLOR               "#c994ca"
#define sclguiUSNO_COLOR                "#f1fb58"

/**
 * Class constructor
 */
sclguiCALIBRATOR_LIST_VIEW::sclguiCALIBRATOR_LIST_VIEW()
{
    // Create the widget
    // resume text area
    _resumeTextArea.SetText("--");
    _resumeTextArea.SetRows(1);
    _resumeTextArea.SetColumns(50);
    _resumeTextArea.SetHelp("No Help");
    _resumeTextArea.SetLabel("Results");
    _resumeTextArea.SetVerticalOrientation(mcsTRUE); 

    // list table
    _listTable = new gwtTABLE(0, 0);
    _listTable->SetHeight(160);
    _listTable->SetVerticalOrientation(mcsTRUE);

    // legend table
    _legendTable = new gwtTABLE(1, 15);
    _legendTable->SetHeight(14);
    _legendTable->SetLabel("Catalog Origin");
    _legendTable->SetCell(0, 0, "I/280");
    _legendTable->SetCellBackground(0, 0, sclguiI_280_COLOR);
    _legendTable->SetCell(0, 1, "II/225");
    _legendTable->SetCellBackground(0, 1, sclguiII_225_COLOR);
    _legendTable->SetCell(0, 2, "II/7A");
    _legendTable->SetCellBackground(0, 2, sclguiII_7A_COLOR);
    _legendTable->SetCell(0, 3, "II/246");
    _legendTable->SetCellBackground(0, 3, sclguiII_246_COLOR);
    _legendTable->SetCell(0, 4, "V/50");
    _legendTable->SetCellBackground(0, 4, sclguiV_50_COLOR);
    _legendTable->SetCell(0, 5, "Borde");
    _legendTable->SetCellBackground(0, 5, sclguiBORDE_COLOR);
    _legendTable->SetCell(0, 6, "Merand");
    _legendTable->SetCellBackground(0, 6, sclguiMERAND_COLOR);
    _legendTable->SetCell(0, 7, "charm");
    _legendTable->SetCellBackground(0, 7, sclguiCHARM_COLOR);
    _legendTable->SetCell(0, 8, "charm2");
    _legendTable->SetCellBackground(0, 8, sclguiCHARM2_COLOR);
    _legendTable->SetCell(0, 9, "B/denis");
    _legendTable->SetCellBackground(0, 9, sclguiDENIS_COLOR);
    _legendTable->SetCell(0, 10, "denis J-K");
    _legendTable->SetCellBackground(0, 10, sclguiDENIS_JK_COLOR);
    _legendTable->SetCell(0, 11, "I/196");
    _legendTable->SetCellBackground(0, 11, sclguiI_196_COLOR);
    _legendTable->SetCell(0, 12, "V/36B");
    _legendTable->SetCellBackground(0, 12, sclguiV_36B_COLOR);
    _legendTable->SetCell(0, 13, "USNO");
    _legendTable->SetCellBackground(0, 13, sclguiUSNO_COLOR);
    _legendTable->SetCell(0, 14, "MIDI");
    _legendTable->SetCellBackground(0, 14, sclgui_MIDI_COLOR);
    
    // confidence table
    _confidenceTable = new gwtTABLE(1,3);
    _confidenceTable->SetHeight(14);
    _confidenceTable->SetLabel("Confidence Index");
    _confidenceTable->SetCell(0, 0, "Low");
    _confidenceTable->SetCellBackground(0, 0, sclguiCONFIDENCE_LOW_COLOR);
    _confidenceTable->SetCell(0, 1, "Medium");           
    _confidenceTable->SetCellBackground(0, 1, sclguiCONFIDENCE_MEDIUM_COLOR);
    _confidenceTable->SetCell(0, 2, "High");             
    _confidenceTable->SetCellBackground(0, 2, sclguiCONFIDENCE_HIGH_COLOR);

    // Add widget in the widget map
    Add(&_resumeTextArea);
    Add(_listTable);
    Add(_legendTable);
    Add(_confidenceTable);

    _details = mcsFALSE;
}

sclguiCALIBRATOR_LIST_VIEW::
sclguiCALIBRATOR_LIST_VIEW(sclguiCALIBRATOR_LIST_MODEL &calibratorsModel,
                       sclguiREQUEST_MODEL &requestModel)
{
    // Attach model
    AttachModel(calibratorsModel, requestModel);
    
    // Create the widget
    // resume text area
    _resumeTextArea.SetText("--");
    _resumeTextArea.SetRows(1);
    _resumeTextArea.SetColumns(50);
    _resumeTextArea.SetHelp("No Help");
    _resumeTextArea.SetLabel("Results");
    _resumeTextArea.SetVerticalOrientation(mcsTRUE); 

    // list table
    _listTable = new gwtTABLE(0, 0);
    _listTable->SetHeight(160);
    _listTable->SetVerticalOrientation(mcsTRUE);

    // legend table
    _legendTable = new gwtTABLE(1, 15);
    _legendTable->SetHeight(15);
    _legendTable->SetLabel("Catalog Origin");
    _legendTable->SetCell(0, 0, "I/280");
    _legendTable->SetCellBackground(0, 0, sclguiI_280_COLOR);
    _legendTable->SetCell(0, 1, "II/225");
    _legendTable->SetCellBackground(0, 1, sclguiII_225_COLOR);
    _legendTable->SetCell(0, 2, "II/7A");
    _legendTable->SetCellBackground(0, 2, sclguiII_7A_COLOR);
    _legendTable->SetCell(0, 3, "II/246");
    _legendTable->SetCellBackground(0, 3, sclguiII_246_COLOR);
    _legendTable->SetCell(0, 4, "V/50");
    _legendTable->SetCellBackground(0, 4, sclguiV_50_COLOR);
    _legendTable->SetCell(0, 5, "Borde");
    _legendTable->SetCellBackground(0, 5, sclguiBORDE_COLOR);
    _legendTable->SetCell(0, 6, "Merand");
    _legendTable->SetCellBackground(0, 6, sclguiMERAND_COLOR);
    _legendTable->SetCell(0, 7, "charm");
    _legendTable->SetCellBackground(0, 7, sclguiCHARM_COLOR);
    _legendTable->SetCell(0, 8, "charm2");
    _legendTable->SetCellBackground(0, 8, sclguiCHARM2_COLOR);
    _legendTable->SetCell(0, 9, "B/denis");
    _legendTable->SetCellBackground(0, 9, sclguiDENIS_COLOR);
    _legendTable->SetCell(0, 10, "denis J-K");
    _legendTable->SetCellBackground(0, 10, sclguiDENIS_JK_COLOR);
    _legendTable->SetCell(0, 11, "I/196");
    _legendTable->SetCellBackground(0, 11, sclguiI_196_COLOR);
    _legendTable->SetCell(0, 12, "V/36B");
    _legendTable->SetCellBackground(0, 12, sclguiV_36B_COLOR);
    _legendTable->SetCell(0, 13, "USNO");
    _legendTable->SetCellBackground(0, 13, sclguiUSNO_COLOR);
    _legendTable->SetCell(0, 14, "MIDI");
    _legendTable->SetCellBackground(0, 14, sclgui_MIDI_COLOR);
    
    // confidence table
    _confidenceTable = new gwtTABLE(1,3);
    _confidenceTable->SetHeight(14);
    _confidenceTable->SetLabel("Confidence Index");
    _confidenceTable->SetCell(0, 0, "Low");
    _confidenceTable->SetCellBackground(0, 0, sclguiCONFIDENCE_LOW_COLOR);
    _confidenceTable->SetCell(0, 1, "Medium");           
    _confidenceTable->SetCellBackground(0, 1, sclguiCONFIDENCE_MEDIUM_COLOR);
    _confidenceTable->SetCell(0, 2, "High");             
    _confidenceTable->SetCellBackground(0, 2, sclguiCONFIDENCE_HIGH_COLOR);

    // Add widget in the widget map
    Add(&_resumeTextArea);
    Add(_listTable);
    Add(_legendTable);
    Add(_confidenceTable);

    Update();
}

/**
 * Class destructor
 */
sclguiCALIBRATOR_LIST_VIEW::~sclguiCALIBRATOR_LIST_VIEW()
{
    delete _listTable;
    delete _legendTable;
    delete _confidenceTable;
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
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_VIEW::Update()
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::Update()");

    // Update the 'resume' textfield
    ostringstream output;
    output << "Number of stars: " 
           << _calibratorListModel->GetNbCalibrators(sclguiALL_CALIBRATORS)
           << " found, " 
           << _calibratorListModel->GetNbCalibrators
                                        (sclguiCALIBRATORS_WITH_COHERENT_DIAM)
           << " with coherent diameter and "
           << _calibratorListModel->GetNbCalibrators
                                        (sclguiCALIBRATORS_WITHOUT_VAR_MULT)
           << " without variability and multiplicity";
    _resumeTextArea.SetText(output.str());
    
    // Retrieve the calibrator list from the model
    sclsvrCALIBRATOR_LIST *calibratorList;
    calibratorList = _calibratorListModel->GetList(sclguiFILTERED_CALIBRATORS);

    // Get the list of properties (labels) to be displayed 
    vobsSTAR_PROPERTY_ID_LIST label;
    label = GetLabel(_details);

    // Get the number of labels and calibrators to set the table size
    int nbOfProperties ;
    nbOfProperties = label.size() + 1 ;
    int nbOfRows;
    nbOfRows = calibratorList->Size();

    _listTable->SetDimension(nbOfRows, nbOfProperties);
    _listTable->SetHeight(160);
    _listTable->SetVerticalOrientation(mcsTRUE);
    // Insert first column Header
    _listTable->SetColumnHeader(0, "Number");

    // Insert headers for calibrator properties
    sclsvrCALIBRATOR tmpCalibrator;    
    int propIdx = 0;
    vobsSTAR_PROPERTY_ID_LIST::iterator labelIterator;
    labelIterator = label.begin();
    while(labelIterator != label.end())
    {
        _listTable->SetColumnHeader
            (propIdx+1, 
             tmpCalibrator.GetProperty((char *)(*labelIterator).data())->
             GetName());
        propIdx++;
        labelIterator++;
    }

    // End to fill the table
    for (unsigned int el = 0; el < calibratorList->Size(); el++)
    {
        sclsvrCALIBRATOR *calibrator;
        ostringstream elStr;
        elStr << el+1;
        _listTable->SetCell(el,0,elStr.str());

        calibrator = 
            (sclsvrCALIBRATOR*)calibratorList->GetNextStar((mcsLOGICAL)(el==0));
        int i=0;
        
        // Add calibrator properties rows
        labelIterator = label.begin();
        while(labelIterator != label.end())
        {
            string propvalue;
            vobsSTAR_PROPERTY *property;
            property = calibrator->GetProperty((char *)(*labelIterator).data());
            
            propvalue.append
                (calibrator->GetPropertyValue((char *)(*labelIterator).data()));
            _listTable->SetCell(el, i+1, propvalue);
            _listTable->SetCellBackground(el, i+1, "#ffffff");
            if (strcmp(property->GetOrigin(), vobsSTAR_COMPUTED_PROP) == 0)
            {
                if (property->GetConfidenceIndex() == vobsCONFIDENCE_LOW)
                {
                    _listTable->
                        SetCellBackground(el, i+1,
                                          sclguiCONFIDENCE_LOW_COLOR);
                }
                else if (property->GetConfidenceIndex() ==
                         vobsCONFIDENCE_MEDIUM)
                {
                    _listTable->
                        SetCellBackground(el, i+1,
                                          sclguiCONFIDENCE_MEDIUM_COLOR);
                }
                else if (property->GetConfidenceIndex() == vobsCONFIDENCE_HIGH)
                {
                    _listTable->
                        SetCellBackground(el, i+1,
                                          sclguiCONFIDENCE_HIGH_COLOR);
                }
            }

            // set table background collor according to the catalog origin
            if (strcmp(property->GetOrigin(), "I/280") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiI_280_COLOR);
            }
            if (strcmp(property->GetOrigin(), "I/284") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiUSNO_COLOR);
            }
            if (strcmp(property->GetOrigin(), "II/225/catalog") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiII_225_COLOR);
            }
            if (strcmp(property->GetOrigin(), "II/7A/catalog") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiII_7A_COLOR);
            }
            if (strcmp(property->GetOrigin(), "II/246/out") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiII_246_COLOR);
            }
            if (strcmp(property->GetOrigin(), "V/50/catalog") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiV_50_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/433/1155") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiMERAND_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/386/492/charm") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiCHARM_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/431/773/charm2") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiCHARM2_COLOR);
            }
            if (strcmp(property->GetOrigin(), "B/denis") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiDENIS_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/413/1037") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiDENIS_JK_COLOR);
            }
            if (strcmp(property->GetOrigin(), "I/196/main") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiI_196_COLOR);
            }
            if (strcmp(property->GetOrigin(), "J/A+A/393/183/catalog") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiBORDE_COLOR);
            }
            if (strcmp(property->GetOrigin(), "MIDI") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclgui_MIDI_COLOR);
            }
            if (strcmp(property->GetOrigin(), "V/36B/bsc4s") == 0)
            {
                _listTable->SetCellBackground(el, i+1, sclguiV_36B_COLOR);
            }

            i++;
            labelIterator++;
        } // End for each properties

    } // End for each calibrators

    return mcsSUCCESS;
}

/**
 * Update the view in detailed mode according to the given logical flag
 *
 * @param state the logical to known the detail state
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned.
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_VIEW::Detail(mcsLOGICAL state)
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::Detail()");

    _details = state; 
   
    return Update();
}

/**
 * Return the labels
 *
 * @return labels
 */
vobsSTAR_PROPERTY_ID_LIST sclguiCALIBRATOR_LIST_VIEW::GetLabel
    (mcsLOGICAL details)
{
    logTrace("sclguiTABLE_VIEW::GetLabel()");
    
    vobsSTAR_PROPERTY_ID_LIST label;
    
    // Reset list
    label.erase(label.begin(), label.end());

    // Build label according to the research band and the faint or bright
    // research
    if (_requestModel->IsBright() == mcsTRUE)
    {
        if (strcmp(_requestModel->GetSearchBand(), "N") == 0)
        {
            GetLabelForBrighN(&label, details);
        }
        else
        {
            GetLabelForBrightKV(&label, details);
        }
    }
    else
    {
        GetLabelForFaintK(&label, details);
    }

    return label;
}

/**
 * Return wether the view is in detailed mode or not
 *
 * @return mcsTRUE if the view is detailed, mcsFALSE otherwise
 */
mcsLOGICAL sclguiCALIBRATOR_LIST_VIEW::IsDetailed()
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::IsDetailed()");
    
    return _details;
}

/**
 * Attach model in the request view
 *
 * @ request the model to attach
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT 
sclguiCALIBRATOR_LIST_VIEW::AttachModel(sclguiCALIBRATOR_LIST_MODEL &calibratorsModel,
                                      sclguiREQUEST_MODEL &requestModel)
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::AttachModel()");

     // Attach to the calibrators model
    _calibratorListModel = &calibratorsModel;
    // Attach to the request model
    _requestModel = &requestModel;

    return mcsSUCCESS;
}

/*
 * Private methods
 */

/**
 * Build label for K and V band
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_VIEW::GetLabelForBrightKV
    (vobsSTAR_PROPERTY_ID_LIST *label, mcsLOGICAL details)
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::GetLabelForBrightKV()");
    
    if (details == mcsFALSE)
    {
        label->push_back(sclsvrCALIBRATOR_DIST);
        label->push_back(vobsSTAR_ID_HD);
        label->push_back(vobsSTAR_POS_EQ_RA_MAIN);
        label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
        label->push_back(sclsvrCALIBRATOR_VIS2);
        label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
        label->push_back(sclsvrCALIBRATOR_DIAM_VK);
        label->push_back(sclsvrCALIBRATOR_DIAM_VK_ERROR);
        label->push_back(vobsSTAR_SPECT_TYPE_MK);
        label->push_back(vobsSTAR_PHOT_JHN_V);
        label->push_back(vobsSTAR_PHOT_JHN_J);
        label->push_back(vobsSTAR_PHOT_JHN_H);
        label->push_back(vobsSTAR_PHOT_JHN_K);
    }
    else
    {
        label->push_back(sclsvrCALIBRATOR_DIST);
        label->push_back(sclsvrCALIBRATOR_VIS2);
        label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
        label->push_back(sclsvrCALIBRATOR_DIAM_BV);
        label->push_back(sclsvrCALIBRATOR_DIAM_VR);
        label->push_back(sclsvrCALIBRATOR_DIAM_VK);
        label->push_back(sclsvrCALIBRATOR_DIAM_VK_ERROR);
        label->push_back(vobsSTAR_ID_HIP);
        label->push_back(vobsSTAR_ID_HD);
        label->push_back(vobsSTAR_ID_DM);
        label->push_back(vobsSTAR_POS_EQ_RA_MAIN);
        label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
        label->push_back(vobsSTAR_POS_EQ_PMDEC);
        label->push_back(vobsSTAR_POS_EQ_PMRA);
        label->push_back(vobsSTAR_POS_PARLX_TRIG);
        label->push_back(vobsSTAR_SPECT_TYPE_MK);
        label->push_back(vobsSTAR_CODE_VARIAB_V3);
        label->push_back(vobsSTAR_CODE_MULT_FLAG);
        label->push_back(vobsSTAR_POS_GAL_LAT);
        label->push_back(vobsSTAR_POS_GAL_LON);
        label->push_back(vobsSTAR_VELOC_HC);
        label->push_back(vobsSTAR_VELOC_ROTAT);
        label->push_back(vobsSTAR_LD_DIAM);
        label->push_back(vobsSTAR_LD_DIAM_ERROR);
        label->push_back(vobsSTAR_UD_DIAM);
        label->push_back(vobsSTAR_UD_DIAM_ERROR);
        label->push_back(vobsSTAR_OBS_METHOD);
        label->push_back(vobsSTAR_INST_WAVELENGTH_VALUE);
        label->push_back(vobsSTAR_UDDK_DIAM);
        label->push_back(vobsSTAR_UDDK_DIAM_ERROR);
        label->push_back(vobsSTAR_PHOT_JHN_B);
        label->push_back(vobsSTAR_PHOT_JHN_V);
        label->push_back(vobsSTAR_PHOT_JHN_R);
        label->push_back(vobsSTAR_PHOT_JHN_I);
        label->push_back(vobsSTAR_PHOT_JHN_J);
        label->push_back(vobsSTAR_PHOT_JHN_H);
        label->push_back(vobsSTAR_PHOT_JHN_K);
        label->push_back(vobsSTAR_PHOT_JHN_L);
        label->push_back(vobsSTAR_PHOT_JHN_M);
        label->push_back(vobsSTAR_PHOT_JHN_N);
        label->push_back(sclsvrCALIBRATOR_EXTINCTION_RATIO);
    } 
    return mcsSUCCESS;
}

/**
 * Build label for N band
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_VIEW::GetLabelForBrighN
    (vobsSTAR_PROPERTY_ID_LIST *label, mcsLOGICAL details)
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::GetLabelForBrighN()");

    if (details == mcsFALSE)
    {
        label->push_back(sclsvrCALIBRATOR_DIST);
        label->push_back(vobsSTAR_ID_HD);
        label->push_back(vobsSTAR_POS_EQ_RA_MAIN);
        label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
        label->push_back(sclsvrCALIBRATOR_VIS2);
        label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
        label->push_back(vobsSTAR_DIAM12);
        label->push_back(vobsSTAR_DIAM12_ERROR);
        label->push_back(vobsSTAR_PHOT_FLUX_IR_12);
        label->push_back(vobsSTAR_SPECT_TYPE_MK);
        label->push_back(vobsSTAR_PHOT_JHN_N);
        label->push_back(sclsvrCALIBRATOR_VIS2_8);
        label->push_back(sclsvrCALIBRATOR_VIS2_8_ERROR);
        label->push_back(sclsvrCALIBRATOR_VIS2_13);
        label->push_back(sclsvrCALIBRATOR_VIS2_13_ERROR);
    }
    else
    {
        label->push_back(sclsvrCALIBRATOR_DIST);
        label->push_back(vobsSTAR_ID_HD);
        label->push_back(vobsSTAR_POS_EQ_RA_MAIN );
        label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
        label->push_back(sclsvrCALIBRATOR_VIS2);
        label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
        label->push_back(vobsSTAR_DIAM12);
        label->push_back(vobsSTAR_DIAM12_ERROR);
        label->push_back(vobsSTAR_IR_FLUX_ORIGIN);
        label->push_back(vobsSTAR_PHOT_FLUX_IR_12);
        label->push_back(vobsSTAR_PHOT_FLUX_IR_12_ERROR);
        label->push_back(vobsSTAR_SPECT_TYPE_MK);
        label->push_back(vobsSTAR_PHOT_JHN_N);
        label->push_back(sclsvrCALIBRATOR_VIS2_8);
        label->push_back(sclsvrCALIBRATOR_VIS2_8_ERROR);
        label->push_back(sclsvrCALIBRATOR_VIS2_13);
        label->push_back(sclsvrCALIBRATOR_VIS2_13_ERROR);
        label->push_back(vobsSTAR_REF_STAR);
        label->push_back(vobsSTAR_CODE_MULT_FLAG);
        label->push_back(vobsSTAR_CODE_VARIAB_V3);
        label->push_back(vobsSTAR_PHOT_JHN_V);
        label->push_back(vobsSTAR_PHOT_JHN_H);
        label->push_back(vobsSTAR_POS_PARLX_TRIG);
        label->push_back(vobsSTAR_POS_PARLX_TRIG_ERROR);
        label->push_back(vobsSTAR_POS_EQ_PMRA);
        label->push_back(vobsSTAR_POS_EQ_PMDEC);
        label->push_back(vobsSTAR_PHOT_EXTINCTION_TOTAL);
        label->push_back(vobsSTAR_CHI2_QUALITY);   
        label->push_back(vobsSTAR_SP_TYP_PHYS_TEMP_EFFEC);
    } 
    return mcsSUCCESS;
}

/**
 * Build label for faint K band
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiCALIBRATOR_LIST_VIEW::GetLabelForFaintK
    (vobsSTAR_PROPERTY_ID_LIST *label, mcsLOGICAL details)
{
    logTrace("sclguiCALIBRATOR_LIST_VIEW::GetLabelForFaintK()");

    if (details == mcsFALSE)
    {
        label->push_back(sclsvrCALIBRATOR_DIST);
        label->push_back(vobsSTAR_ID_2MASS);
        label->push_back(vobsSTAR_POS_EQ_RA_MAIN);
        label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
        label->push_back(sclsvrCALIBRATOR_VIS2);
        label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
        label->push_back(sclsvrCALIBRATOR_DIAM_MEAN);
        label->push_back(sclsvrCALIBRATOR_DIAM_MEAN_ERROR);
        label->push_back(vobsSTAR_PHOT_JHN_V);
        label->push_back(vobsSTAR_PHOT_PHG_V);
        label->push_back(vobsSTAR_PHOT_JHN_I);
        label->push_back(vobsSTAR_PHOT_COUS_I);
        label->push_back(vobsSTAR_PHOT_PHG_I);
        label->push_back(vobsSTAR_PHOT_JHN_J);
        label->push_back(sclsvrCALIBRATOR_PHOT_COUS_J);
        label->push_back(vobsSTAR_PHOT_JHN_H);
        label->push_back(sclsvrCALIBRATOR_PHOT_COUS_H);
        label->push_back(vobsSTAR_PHOT_JHN_K);
        label->push_back(sclsvrCALIBRATOR_PHOT_COUS_K);
    }
    else
    {
        label->push_back(sclsvrCALIBRATOR_DIST);
        label->push_back(sclsvrCALIBRATOR_VIS2);
        label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
        label->push_back(sclsvrCALIBRATOR_DIAM_IJ);
        label->push_back(sclsvrCALIBRATOR_DIAM_IK);
        label->push_back(sclsvrCALIBRATOR_DIAM_JH);
        label->push_back(sclsvrCALIBRATOR_DIAM_JK);
        label->push_back(sclsvrCALIBRATOR_DIAM_HK);
        label->push_back(sclsvrCALIBRATOR_DIAM_MEAN);
        label->push_back(sclsvrCALIBRATOR_DIAM_MEAN_ERROR);
        label->push_back(vobsSTAR_ID_2MASS);
        label->push_back(vobsSTAR_ID_DENIS);
        label->push_back(vobsSTAR_ID_TYC1);
        label->push_back(vobsSTAR_ID_TYC2);
        label->push_back(vobsSTAR_ID_TYC3);
        label->push_back(vobsSTAR_ID_HIP);
        label->push_back(vobsSTAR_ID_HD);
        label->push_back(vobsSTAR_ID_DM);
        label->push_back(vobsSTAR_POS_EQ_RA_MAIN);
        label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
        label->push_back(vobsSTAR_POS_EQ_PMRA);
        label->push_back(vobsSTAR_POS_EQ_PMDEC);
        label->push_back(vobsSTAR_POS_GAL_LAT);
        label->push_back(vobsSTAR_POS_GAL_LON);
        label->push_back(vobsSTAR_POS_PARLX_TRIG);
        label->push_back(vobsSTAR_SPECT_TYPE_MK);
        label->push_back(vobsSTAR_CODE_VARIAB_V1);
        label->push_back(vobsSTAR_CODE_VARIAB_V2);
        label->push_back(vobsSTAR_CODE_VARIAB_V3);
        label->push_back(vobsSTAR_CODE_MULT_FLAG);
        label->push_back(vobsSTAR_LD_DIAM);
        label->push_back(vobsSTAR_LD_DIAM_ERROR);
        label->push_back(vobsSTAR_UD_DIAM);
        label->push_back(vobsSTAR_UD_DIAM_ERROR);
        label->push_back(vobsSTAR_OBS_METHOD);
        // lambda
        label->push_back(vobsSTAR_PHOT_JHN_B);
        label->push_back(vobsSTAR_PHOT_PHG_B);
        label->push_back(vobsSTAR_PHOT_JHN_V);
        label->push_back(vobsSTAR_PHOT_PHG_V);
        label->push_back(vobsSTAR_PHOT_PHG_R);
        label->push_back(vobsSTAR_PHOT_JHN_I);
        label->push_back(vobsSTAR_PHOT_COUS_I);
        label->push_back(vobsSTAR_PHOT_PHG_I);
        label->push_back(vobsSTAR_PHOT_JHN_J);
        label->push_back(sclsvrCALIBRATOR_PHOT_COUS_J);
        label->push_back(vobsSTAR_PHOT_JHN_H);
        label->push_back(sclsvrCALIBRATOR_PHOT_COUS_H);
        label->push_back(vobsSTAR_PHOT_JHN_K);
        label->push_back(sclsvrCALIBRATOR_PHOT_COUS_K);
        label->push_back(sclsvrCALIBRATOR_EXTINCTION_RATIO);
    }
    return mcsSUCCESS;
}

/*___oOo___*/
