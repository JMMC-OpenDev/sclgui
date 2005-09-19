#ifndef sclguiRESUME_VIEW_H
#define sclguiRESUME_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiRESUME_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiRESUME_VIEW class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "fnd.h"
#include "gwt.h"

#include "sclguiMODEL.h"

/*
 * Class declaration
 */

/**
 * Resume view of the model list.
 * 
 * This class derived from a virtual fndMVC_VIEW class wich offer the generic
 * method to register it with a model. The class is composed by a widget defined
 * in the gwt library.
 *
 * \usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * \filename fileName1 :  usage description of fileName1
 * \filename fileName2 :  usage description of fileName2
 *
 * \sa fnd library.
 * \sa gwt library.
 * 
 */
class sclguiRESUME_VIEW : public fndMVC_VIEW
{

public:
    // Class constructor
    sclguiRESUME_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiRESUME_VIEW();
   
    virtual mcsCOMPL_STAT Update();
        
    gwtTEXTAREA * GetResumeTextArea();

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiRESUME_VIEW(const sclguiRESUME_VIEW&);
    sclguiRESUME_VIEW& operator=(const sclguiRESUME_VIEW&);

    sclguiMODEL *_model;
    
    gwtTEXTAREA *_resumeTextArea;
};

#endif /*!sclguiRESUME_VIEW_H*/

/*___oOo___*/
