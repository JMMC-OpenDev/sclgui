#ifndef sclguiREQUEST_VIEW_H
#define sclguiREQUEST_VIEW_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * @file
 * Declaration of sclguiREQUEST_VIEW class.
 */

#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


/*
 * MCS header
 */
#include "mcs.h"
#include "sclguiREQUEST_MODEL.h"
#include "gwt.h"

/*
 * Class declaration
 */

/**
 * View of the request model 
 */
class sclguiREQUEST_VIEW : public gwtCONTAINER, public fndMVC_VIEW
{
public:
    // Class constructor
    sclguiREQUEST_VIEW();

    // Class destructor
    virtual ~sclguiREQUEST_VIEW();

    virtual mcsCOMPL_STAT Update();
    
    virtual mcsCOMPL_STAT AttachModel(sclguiREQUEST_MODEL &requestModel);

protected:
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
    sclguiREQUEST_VIEW(const sclguiREQUEST_VIEW&);
    sclguiREQUEST_VIEW& operator=(const sclguiREQUEST_VIEW&);

    sclguiREQUEST_MODEL *_requestModel;

    gwtTEXTAREA _requestTextArea;
};

#endif /*!sclguiREQUEST_VIEW_H*/

/*___oOo___*/
