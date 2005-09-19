#ifndef sclguiREQUEST_VIEW_H
#define sclguiREQUEST_VIEW_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: sclguiREQUEST_VIEW.h,v 1.1 2005-07-07 05:10:54 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * \file
 * Declaration of sclguiREQUEST_VIEW class.
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
 * Table view of the model list.
 * 
 * This class derived from a virtual fndMVC_VIEW class wich offer the generic
 * method to register it with a model. The class is composed by a widget defined
 * in the gwt library.
 *
 * \sa fnd library.
 * \sa gwt library.
 *
 */
class sclguiREQUEST_VIEW : public fndMVC_VIEW
{

public:
    // Class constructor
    sclguiREQUEST_VIEW(sclguiMODEL *model);

    // Class destructor
    virtual ~sclguiREQUEST_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    gwtTEXTAREA * GetRequestTextArea();
    
protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiREQUEST_VIEW(const sclguiREQUEST_VIEW&);
    sclguiREQUEST_VIEW& operator=(const sclguiREQUEST_VIEW&);
    
    sclguiMODEL *_model;

    gwtTEXTAREA *_requestTextArea;    
};

#endif /*!sclguiREQUEST_VIEW_H*/

/*___oOo___*/
