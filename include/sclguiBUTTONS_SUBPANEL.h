#ifndef sclguiBUTTONS_SUBPANEL_H
#define sclguiBUTTONS_SUBPANEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiBUTTONS_SUBPANEL class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "gwt.h"


/*
 * Class declaration
 */
/**
 * Specific Mega Widget class dedicated to the action button.
 */
class sclguiBUTTONS_SUBPANEL : public gwtCONTAINER
{

public:
    // Class constructor
    sclguiBUTTONS_SUBPANEL();

    // Class destructor
    virtual ~sclguiBUTTONS_SUBPANEL();

    // Class callbacks
    virtual mcsCOMPL_STAT SetResetCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD callbackMethod);
    virtual mcsCOMPL_STAT SetShowAllResultsCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD callbackMethod);
    virtual mcsCOMPL_STAT SetShowDetailsCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD callbackMethod);
    virtual mcsCOMPL_STAT SetHideDetailsCB(fndOBJECT &eventHandler,
                                     gwtCOMMAND::CB_METHOD callbackMethod);
protected:

private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiBUTTONS_SUBPANEL(const sclguiBUTTONS_SUBPANEL&);
    sclguiBUTTONS_SUBPANEL& operator=(const sclguiBUTTONS_SUBPANEL&);

    gwtBUTTON _resetButton;
    gwtBUTTON _showAllResultsButton;
    gwtBUTTON _hideDetailsButton;
    gwtBUTTON _showDetailsButton;
};

#endif /*!sclguiBUTTONS_SUBPANEL_H*/

/*___oOo___*/
