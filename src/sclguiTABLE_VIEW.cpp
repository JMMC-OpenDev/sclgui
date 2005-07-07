/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiTABLE_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 *  Definition of sclguiTABLE_VIEW class.
 */

static char *rcsId="@(#) $Id: sclguiTABLE_VIEW.cpp,v 1.1 2005-07-07 05:07:21 gzins Exp $"; 
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
#include "sclguiTABLE_VIEW.h"
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

/**
 * Class constructor
 */
sclguiTABLE_VIEW::sclguiTABLE_VIEW(sclguiMODEL *model)
{
    // attach to the model
    _model = model;
    
    _listTable = new gwtTABLE(0, 0);
    _listTable->SetHeight(160);
    _listTable->SetVerticalOrientation(mcsTRUE);

    // build legend table
    _legendTable = new gwtTABLE(1, 14);
    _legendTable->SetHeight(14);
    //_legendTable->SetVerticalOrientation(mcsTRUE);
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
    _legendTable->SetCell(0, 13, "MIDI");
    _legendTable->SetCellBackground(0, 13, sclgui_MIDI_COLOR);
    
    // build confidence index table
    _confidenceTable = new gwtTABLE(1,3);
    _confidenceTable->SetHeight(14);
    _confidenceTable->SetLabel("Confidence Index");
    _confidenceTable->SetCell(0, 0, "Low");
    _confidenceTable->SetCellBackground(0, 0, sclguiCONFIDENCE_LOW_COLOR);
    _confidenceTable->SetCell(0, 1, "Medium");           
    _confidenceTable->SetCellBackground(0, 1, sclguiCONFIDENCE_MEDIUM_COLOR);
    _confidenceTable->SetCell(0, 2, "High");             
    _confidenceTable->SetCellBackground(0, 2, sclguiCONFIDENCE_HIGH_COLOR);

    // By default the view is with less details
    _details = mcsFALSE;
}

/**
 * Class destructor
 */
sclguiTABLE_VIEW::~sclguiTABLE_VIEW()
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
mcsCOMPL_STAT sclguiTABLE_VIEW::Update()
{
    logTrace("sclguiTABLE_VIEW::Update()");
  
    // retrieve calibrator list from the model
    sclsvrCALIBRATOR_LIST *calibratorList;
    calibratorList = _model->GetCalibratorList();
    
    // Build looking label
    BuildLabel(&_label, _details);

    // Get number of label and number of calibrators to set the table size
    int nbOfProperties ;
    nbOfProperties = _label.size() + 1 ;
    int nbOfRows;
    nbOfRows = calibratorList->Size(); 
    
    _listTable->SetDimension(nbOfRows, nbOfProperties);
    _listTable->SetHeight(160);
    _listTable->SetVerticalOrientation(mcsTRUE);
    // Insert first column Header
    _listTable->SetColumnHeader(0, "Number");

    sclsvrCALIBRATOR tmpCalibrator;    
    // Insert headers for calibrator properties
    int propIdx=0;
    vobsSTAR_PROPERTY_ID_LIST::iterator labelIterator;
    labelIterator = _label.begin();
    while(labelIterator != _label.end())
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
        
        // Add calibrator properties raws
        labelIterator = _label.begin();
        while(labelIterator != _label.end())
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
 * Details or not the view
 *
 * @param state the logical to known the detail state
 *
 * @return always mcsSUCCESS;
 */
mcsCOMPL_STAT sclguiTABLE_VIEW::Details(mcsLOGICAL state)
{
    logTrace("sclguiTABLE_VIEW::Details()");

    _details = state; 
   
    return Update();
}

/**
 * Return the table associated to this view
 *
 * @return table
 */
gwtTABLE * sclguiTABLE_VIEW::GetListTable()
{
    logTrace("sclguiRESUME_VIEW::GetListTable()");

    return _listTable;
}

/**
 * Return the color table associated to this view
 *
 * @return  color table
 */
gwtTABLE * sclguiTABLE_VIEW::GetColorTable()
{
    logTrace("sclguiRESUME_VIEW::GetColorTable()");

    return _legendTable;
}

/**
 * Return the confidence index table associated to this view
 *
 * @return confidence index
 */
gwtTABLE * sclguiTABLE_VIEW::GetConfIdxTable()
{
    logTrace("sclguiRESUME_VIEW::GetConfIdxTable()");

    return _confidenceTable;
}

/**
 * Return the labels
 *
 * @return labels
 */
vobsSTAR_PROPERTY_ID_LIST sclguiTABLE_VIEW::GetLabel(mcsLOGICAL details)
{
    logTrace("sclguiTABLE_VIEW::GetLabel()");
    
    vobsSTAR_PROPERTY_ID_LIST label;
    BuildLabel(&label, details);

    return label;
}

/**
 * Say if the actual view is details or not
 *
 * @return mcsTRUE if it is a details state, otherwise mcsFALSE is returned
 */
mcsLOGICAL sclguiTABLE_VIEW::IsDetailsView()
{
    logTrace("sclguiTABLE_VIEW::IsDetailsView()");
    
    return _details;
}

/*
 * Protected methods
 */


/*
 * Private methods
 */
/**
 * Reset the list of wanted UCD
 *
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiTABLE_VIEW::ResetLabel()
{
    logTrace("sclguiTABLE_VIEW::ResetWantedUcdList()");

    _label.erase(_label.begin(), _label.end());
    
    return mcsSUCCESS;
}

/**
 * Build Result table label
 *
 * @param label label of the table
 * @param details boolean to know if it is a detail view or not
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiTABLE_VIEW::BuildLabel(vobsSTAR_PROPERTY_ID_LIST *label,
                                           mcsLOGICAL details)
{
    logTrace("sclguiTABLE_VIEW::BuildLabel()");

    // ResetLabel
    ResetLabel();

    // According to the request write the correct label
    sclsvrREQUEST *request;
    request = _model->GetRequest();

    // Build label accordinf to the research band
    if (strcmp(request->GetSearchBand(), "N") == 0)
    {
        if (details == mcsFALSE)
        {
            BuildLabelN(label);
        }
        else
        {
            BuildLabelNComplete(label);
        }
    }
    else
    {
        if (details == mcsFALSE)
        {
            BuildLabelKV(label);
        }
        else
        {
            BuildLabelKVComplete(label);
        }
    }
    
    return mcsSUCCESS;
}

/**
 * Build label for K and V band
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiTABLE_VIEW::BuildLabelKV(vobsSTAR_PROPERTY_ID_LIST *label)
{
    logTrace("sclguiTABLE_VIEW::BuildLabelKV()");
    
    label->push_back(vobsSTAR_ID_HD);
    label->push_back(vobsSTAR_POS_EQ_RA_MAIN);
    label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
    label->push_back(sclsvrCALIBRATOR_VIS2);
    label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
    label->push_back(sclsvrCALIBRATOR_DIAM_VK);
    label->push_back(sclsvrCALIBRATOR_DIAM_VK_ERROR);
    label->push_back(vobsSTAR_SPECT_TYPE_MK);
    label->push_back(sclsvrCALIBRATOR_VO);
    label->push_back(sclsvrCALIBRATOR_JO);
    label->push_back(sclsvrCALIBRATOR_HO);
    label->push_back(sclsvrCALIBRATOR_KO);
    
    return mcsSUCCESS;
}

/**
 * Build label for K and V bands for detail view
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT
sclguiTABLE_VIEW::BuildLabelKVComplete(vobsSTAR_PROPERTY_ID_LIST *label)
{
    logTrace("sclguiTABLE_VIEW::BuildLabelKVComplete()");

    // build ucdNameforKV list
    label->push_back(sclsvrCALIBRATOR_VIS2            );
    label->push_back(sclsvrCALIBRATOR_VIS2_ERROR      );
    label->push_back(sclsvrCALIBRATOR_DIAM_BV         );
    label->push_back(sclsvrCALIBRATOR_DIAM_VR         );
    label->push_back(sclsvrCALIBRATOR_DIAM_VK         );
    label->push_back(sclsvrCALIBRATOR_DIAM_VK_ERROR   );
    label->push_back(vobsSTAR_ID_HIP                  );
    label->push_back(vobsSTAR_ID_HD                   );
    label->push_back(vobsSTAR_ID_DM                   );
    label->push_back(vobsSTAR_POS_EQ_RA_MAIN          );
    label->push_back(vobsSTAR_POS_EQ_DEC_MAIN         );
    label->push_back(vobsSTAR_POS_EQ_PMDEC            );
    label->push_back(vobsSTAR_POS_EQ_PMRA             );
    label->push_back(vobsSTAR_POS_PARLX_TRIG          );
    label->push_back(vobsSTAR_SPECT_TYPE_MK           );
    label->push_back(vobsSTAR_CODE_VARIAB             );
    label->push_back(vobsSTAR_CODE_MULT_FLAG          );
    label->push_back(vobsSTAR_POS_GAL_LAT             );
    label->push_back(vobsSTAR_POS_GAL_LON             );
    label->push_back(vobsSTAR_VELOC_HC                );
    label->push_back(vobsSTAR_VELOC_ROTAT             );
    label->push_back(vobsSTAR_LD_DIAM                 );
    label->push_back(vobsSTAR_LD_DIAM_ERROR           );
    label->push_back(vobsSTAR_UD_DIAM                 );
    label->push_back(vobsSTAR_UD_DIAM_ERROR           );
    label->push_back(vobsSTAR_OBS_METHOD              );
    label->push_back(vobsSTAR_INST_WAVELENGTH_VALUE   );
    label->push_back(vobsSTAR_UDDK_DIAM               );
    label->push_back(vobsSTAR_UDDK_DIAM_ERROR         );
    label->push_back(vobsSTAR_PHOT_JHN_U              );
    label->push_back(vobsSTAR_PHOT_JHN_B              );
    label->push_back(vobsSTAR_PHOT_JHN_V              );
    label->push_back(vobsSTAR_PHOT_JHN_R              );
    label->push_back(vobsSTAR_PHOT_JHN_I              );
    label->push_back(vobsSTAR_PHOT_JHN_J              );
    label->push_back(vobsSTAR_PHOT_JHN_H              );
    label->push_back(vobsSTAR_PHOT_JHN_K              );
    label->push_back(vobsSTAR_PHOT_JHN_L              );
    label->push_back(vobsSTAR_PHOT_JHN_M              );
    label->push_back(vobsSTAR_PHOT_JHN_N              );
    label->push_back(sclsvrCALIBRATOR_MO              );
    label->push_back(sclsvrCALIBRATOR_LO              );
    label->push_back(sclsvrCALIBRATOR_KO              );
    label->push_back(sclsvrCALIBRATOR_HO              );
    label->push_back(sclsvrCALIBRATOR_JO              );
    label->push_back(sclsvrCALIBRATOR_IO              );
    label->push_back(sclsvrCALIBRATOR_RO              );
    label->push_back(sclsvrCALIBRATOR_VO              );
    label->push_back(sclsvrCALIBRATOR_BO              );
    
    return mcsSUCCESS;
}

/**
 * Build label for N band
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT sclguiTABLE_VIEW::BuildLabelN(vobsSTAR_PROPERTY_ID_LIST *label)
{
    logTrace("sclguiTABLE_VIEW::BuildLabelN()");

    label->push_back(vobsSTAR_ID_HD);
    label->push_back(vobsSTAR_POS_EQ_RA_MAIN );
    label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
    label->push_back(sclsvrCALIBRATOR_VIS2      );
    label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
    label->push_back(vobsSTAR_DIAM12      );
    label->push_back(vobsSTAR_DIAM12_ERROR);
    label->push_back(vobsSTAR_PHOT_FLUX_IR_12);
    label->push_back(vobsSTAR_SPECT_TYPE_MK);
    label->push_back(vobsSTAR_PHOT_JHN_N);
    label->push_back(sclsvrCALIBRATOR_VIS2_8       );
    label->push_back(sclsvrCALIBRATOR_VIS2_8_ERROR );
    label->push_back(sclsvrCALIBRATOR_VIS2_13      );
    label->push_back(sclsvrCALIBRATOR_VIS2_13_ERROR);
    
    return mcsSUCCESS;
}

/**
 * Build label for N band for detail view
 *
 * @param label label list to build
 * 
 * @return always mcsSUCCESS
 */
mcsCOMPL_STAT 
sclguiTABLE_VIEW::BuildLabelNComplete(vobsSTAR_PROPERTY_ID_LIST *label)
{
    logTrace("sclguiTABLE_VIEW::BuildLabelNComplete()");

    label->push_back(vobsSTAR_ID_HD);
    label->push_back(vobsSTAR_POS_EQ_RA_MAIN );
    label->push_back(vobsSTAR_POS_EQ_DEC_MAIN);
    label->push_back(sclsvrCALIBRATOR_VIS2      );
    label->push_back(sclsvrCALIBRATOR_VIS2_ERROR);
    label->push_back(vobsSTAR_DIAM12      );
    label->push_back(vobsSTAR_DIAM12_ERROR);
    label->push_back(vobsSTAR_IR_FLUX_ORIGIN);
    label->push_back(vobsSTAR_PHOT_FLUX_IR_12);
    label->push_back(vobsSTAR_PHOT_FLUX_IR_12_ERROR);
    label->push_back(vobsSTAR_SPECT_TYPE_MK);
    label->push_back(vobsSTAR_PHOT_JHN_N);
    label->push_back(sclsvrCALIBRATOR_VIS2_8       );
    label->push_back(sclsvrCALIBRATOR_VIS2_8_ERROR );
    label->push_back(sclsvrCALIBRATOR_VIS2_13      );
    label->push_back(sclsvrCALIBRATOR_VIS2_13_ERROR);
    label->push_back(vobsSTAR_REF_STAR);
    label->push_back(vobsSTAR_CODE_MULT_FLAG);
    label->push_back(vobsSTAR_CODE_VARIAB);
    label->push_back(vobsSTAR_PHOT_JHN_V);
    label->push_back(vobsSTAR_PHOT_JHN_H);
    label->push_back(vobsSTAR_POS_PARLX_TRIG);
    label->push_back(vobsSTAR_POS_PARLX_TRIG_ERROR);
    label->push_back(vobsSTAR_POS_EQ_PMRA);
    label->push_back(vobsSTAR_POS_EQ_PMDEC);
    label->push_back(vobsSTAR_PHOT_EXTINCTION_TOTAL);
    label->push_back(vobsSTAR_CHI2_QUALITY);   
    label->push_back(vobsSTAR_SP_TYP_PHYS_TEMP_EFFEC);
    
    return mcsSUCCESS;
}


/*___oOo___*/
