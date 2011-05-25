#ifndef sclguiCONFIRM_SUBPANEL_H
#define sclguiCONFIRM_SUBPANEL_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiCONFIRM_SUBPANEL class.
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
 * Brief description of the class, which ends at this dot.
 * 
 * OPTIONAL detailed description of the class follows here.
 *
 * \usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * \filename fileName1 :  usage description of fileName1
 * \filename fileName2 :  usage description of fileName2
 *
 * \n
 * \env
 * OPTIONAL. If needed, environmental variables accessed by the class. For
 * each variable, name, and usage description, as below.
 * \envvar envVar1 :  usage description of envVar1
 * \envvar envVar2 :  usage description of envVar2
 * 
 * \n
 * \warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * \n
 * \ex
 * OPTIONAL. Code example if needed
 * \n Brief example description.
 * \code
 * Insert your code example here
 * \endcode
 *
 * \sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * \sa modcppMain.C
 * 
 * \bug OPTIONAL. Bugs list if it exists.
 * \bug For example, description of the first bug
 * \bug For example, description of the second bug
 * 
 * \todo OPTIONAL. Things to forsee list, if needed. For example, 
 * \todo add other methods, dealing with operations.
 * 
 */
class sclguiCONFIRM_SUBPANEL : public gwtCONTAINER
{

public:
    // Class constructor
    sclguiCONFIRM_SUBPANEL();

    // Class destructor
    virtual ~sclguiCONFIRM_SUBPANEL();

    // Class callback
    virtual mcsCOMPL_STAT SetOverwriteCB(fndOBJECT &eventHandler,
                                         gwtCOMMAND::CB_METHOD callbackMethod);

    virtual mcsCOMPL_STAT SetPopUpText(string text);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiCONFIRM_SUBPANEL(const sclguiCONFIRM_SUBPANEL&);
    sclguiCONFIRM_SUBPANEL& operator=(const sclguiCONFIRM_SUBPANEL&);

    // Confirmation dialog widgets
    gwtBUTTON _overwriteButton;
    gwtLABEL  _confirmLabel;
};

#endif /*!sclguiCONFIRM_SUBPANEL_H*/

/*___oOo___*/
