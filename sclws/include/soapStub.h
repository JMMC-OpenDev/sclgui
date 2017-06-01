/* soapStub.h
   Generated by gSOAP 2.7.11 from ../include/sclwsWS.h
   Copyright(C) 2000-2008, Robert van Engelen, Genivia Inc. All Rights Reserved.
   This part of the software is released under one of the following licenses:
   GPL, the gSOAP public license, or Genivia's license for commercial use.
*/

#ifndef soapStub_H
#define soapStub_H
#ifndef sclwsWS_H
#define sclwsWS_H
#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif
#endif /*!sclwsWS_H*/
#ifndef WITH_NONAMESPACES
#define WITH_NONAMESPACES
#endif
#include "stdsoap2.h"

/******************************************************************************\
 *                                                                            *
 * Enumerations                                                               *
 *                                                                            *
\******************************************************************************/


/******************************************************************************\
 *                                                                            *
 * Classes and Structs                                                        *
 *                                                                            *
\******************************************************************************/


#if 0 /* volatile type: do not redeclare here */

#endif

#ifndef SOAP_TYPE_ns__GetCalOpenSessionResponse
#define SOAP_TYPE_ns__GetCalOpenSessionResponse (9)
/* ns:GetCalOpenSessionResponse */
struct ns__GetCalOpenSessionResponse
{
public:
	char **_param_1;	/* SOAP 1.2 RPC return element (when namespace qualified) */	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetCalOpenSession
#define SOAP_TYPE_ns__GetCalOpenSession (10)
/* ns:GetCalOpenSession */
struct ns__GetCalOpenSession
{
#ifdef WITH_NOEMPTYSTRUCT
private:
	char dummy;	/* dummy member to enable compilation */
#endif
};
#endif

#ifndef SOAP_TYPE_ns__GetCalSearchCalResponse
#define SOAP_TYPE_ns__GetCalSearchCalResponse (12)
/* ns:GetCalSearchCalResponse */
struct ns__GetCalSearchCalResponse
{
public:
	char **_param_4;	/* SOAP 1.2 RPC return element (when namespace qualified) */	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetCalSearchCal
#define SOAP_TYPE_ns__GetCalSearchCal (13)
/* ns:GetCalSearchCal */
struct ns__GetCalSearchCal
{
public:
	char *_param_2;	/* optional element of type xsd:string */
	char *_param_3;	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetCalQueryStatusResponse
#define SOAP_TYPE_ns__GetCalQueryStatusResponse (15)
/* ns:GetCalQueryStatusResponse */
struct ns__GetCalQueryStatusResponse
{
public:
	char **_param_6;	/* SOAP 1.2 RPC return element (when namespace qualified) */	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetCalQueryStatus
#define SOAP_TYPE_ns__GetCalQueryStatus (16)
/* ns:GetCalQueryStatus */
struct ns__GetCalQueryStatus
{
public:
	char *_param_5;	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetCalCancelSessionResponse
#define SOAP_TYPE_ns__GetCalCancelSessionResponse (20)
/* ns:GetCalCancelSessionResponse */
struct ns__GetCalCancelSessionResponse
{
public:
	bool *_param_8;	/* SOAP 1.2 RPC return element (when namespace qualified) */	/* optional element of type xsd:boolean */
};
#endif

#ifndef SOAP_TYPE_ns__GetCalCancelSession
#define SOAP_TYPE_ns__GetCalCancelSession (21)
/* ns:GetCalCancelSession */
struct ns__GetCalCancelSession
{
public:
	char *_param_7;	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetStarResponse
#define SOAP_TYPE_ns__GetStarResponse (23)
/* ns:GetStarResponse */
struct ns__GetStarResponse
{
public:
	char **voTable;	/* SOAP 1.2 RPC return element (when namespace qualified) */	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetStar
#define SOAP_TYPE_ns__GetStar (24)
/* ns:GetStar */
struct ns__GetStar
{
public:
	char *query;	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetServerStatusResponse
#define SOAP_TYPE_ns__GetServerStatusResponse (26)
/* ns:GetServerStatusResponse */
struct ns__GetServerStatusResponse
{
public:
	char **status;	/* SOAP 1.2 RPC return element (when namespace qualified) */	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_ns__GetServerStatus
#define SOAP_TYPE_ns__GetServerStatus (27)
/* ns:GetServerStatus */
struct ns__GetServerStatus
{
#ifdef WITH_NOEMPTYSTRUCT
private:
	char dummy;	/* dummy member to enable compilation */
#endif
};
#endif

#ifndef SOAP_TYPE_SOAP_ENV__Header
#define SOAP_TYPE_SOAP_ENV__Header (28)
/* SOAP Header: */
struct SOAP_ENV__Header
{
#ifdef WITH_NOEMPTYSTRUCT
private:
	char dummy;	/* dummy member to enable compilation */
#endif
};
#endif

#ifndef SOAP_TYPE_SOAP_ENV__Code
#define SOAP_TYPE_SOAP_ENV__Code (29)
/* SOAP Fault Code: */
struct SOAP_ENV__Code
{
public:
	char *SOAP_ENV__Value;	/* optional element of type xsd:QName */
	struct SOAP_ENV__Code *SOAP_ENV__Subcode;	/* optional element of type SOAP-ENV:Code */
};
#endif

#ifndef SOAP_TYPE_SOAP_ENV__Detail
#define SOAP_TYPE_SOAP_ENV__Detail (31)
/* SOAP-ENV:Detail */
struct SOAP_ENV__Detail
{
public:
	int __type;	/* any type of element <fault> (defined below) */
	void *fault;	/* transient */
	char *__any;
};
#endif

#ifndef SOAP_TYPE_SOAP_ENV__Reason
#define SOAP_TYPE_SOAP_ENV__Reason (34)
/* SOAP-ENV:Reason */
struct SOAP_ENV__Reason
{
public:
	char *SOAP_ENV__Text;	/* optional element of type xsd:string */
};
#endif

#ifndef SOAP_TYPE_SOAP_ENV__Fault
#define SOAP_TYPE_SOAP_ENV__Fault (35)
/* SOAP Fault: */
struct SOAP_ENV__Fault
{
public:
	char *faultcode;	/* optional element of type xsd:QName */
	char *faultstring;	/* optional element of type xsd:string */
	char *faultactor;	/* optional element of type xsd:string */
	struct SOAP_ENV__Detail *detail;	/* optional element of type SOAP-ENV:Detail */
	struct SOAP_ENV__Code *SOAP_ENV__Code;	/* optional element of type SOAP-ENV:Code */
	struct SOAP_ENV__Reason *SOAP_ENV__Reason;	/* optional element of type SOAP-ENV:Reason */
	char *SOAP_ENV__Node;	/* optional element of type xsd:string */
	char *SOAP_ENV__Role;	/* optional element of type xsd:string */
	struct SOAP_ENV__Detail *SOAP_ENV__Detail;	/* optional element of type SOAP-ENV:Detail */
};
#endif

/******************************************************************************\
 *                                                                            *
 * Types with Custom Serializers                                              *
 *                                                                            *
\******************************************************************************/


/******************************************************************************\
 *                                                                            *
 * Typedefs                                                                   *
 *                                                                            *
\******************************************************************************/

#ifndef SOAP_TYPE__QName
#define SOAP_TYPE__QName (5)
typedef char *_QName;
#endif

#ifndef SOAP_TYPE__XML
#define SOAP_TYPE__XML (6)
typedef char *_XML;
#endif


/******************************************************************************\
 *                                                                            *
 * Typedef Synonyms                                                           *
 *                                                                            *
\******************************************************************************/


/******************************************************************************\
 *                                                                            *
 * Externals                                                                  *
 *                                                                            *
\******************************************************************************/


/******************************************************************************\
 *                                                                            *
 * Service Operations                                                         *
 *                                                                            *
\******************************************************************************/


SOAP_FMAC5 int SOAP_FMAC6 ns__GetCalOpenSession(struct soap*, char **_param_1);

SOAP_FMAC5 int SOAP_FMAC6 ns__GetCalSearchCal(struct soap*, char *_param_2, char *_param_3, char **_param_4);

SOAP_FMAC5 int SOAP_FMAC6 ns__GetCalQueryStatus(struct soap*, char *_param_5, char **_param_6);

SOAP_FMAC5 int SOAP_FMAC6 ns__GetCalCancelSession(struct soap*, char *_param_7, bool *_param_8);

SOAP_FMAC5 int SOAP_FMAC6 ns__GetStar(struct soap*, char *query, char **voTable);

SOAP_FMAC5 int SOAP_FMAC6 ns__GetServerStatus(struct soap*, char **status);

/******************************************************************************\
 *                                                                            *
 * Stubs                                                                      *
 *                                                                            *
\******************************************************************************/


SOAP_FMAC5 int SOAP_FMAC6 soap_call_ns__GetCalOpenSession(struct soap *soap, const char *soap_endpoint, const char *soap_action, char **_param_1);

SOAP_FMAC5 int SOAP_FMAC6 soap_call_ns__GetCalSearchCal(struct soap *soap, const char *soap_endpoint, const char *soap_action, char *_param_2, char *_param_3, char **_param_4);

SOAP_FMAC5 int SOAP_FMAC6 soap_call_ns__GetCalQueryStatus(struct soap *soap, const char *soap_endpoint, const char *soap_action, char *_param_5, char **_param_6);

SOAP_FMAC5 int SOAP_FMAC6 soap_call_ns__GetCalCancelSession(struct soap *soap, const char *soap_endpoint, const char *soap_action, char *_param_7, bool *_param_8);

SOAP_FMAC5 int SOAP_FMAC6 soap_call_ns__GetStar(struct soap *soap, const char *soap_endpoint, const char *soap_action, char *query, char **voTable);

SOAP_FMAC5 int SOAP_FMAC6 soap_call_ns__GetServerStatus(struct soap *soap, const char *soap_endpoint, const char *soap_action, char **status);

/******************************************************************************\
 *                                                                            *
 * Skeletons                                                                  *
 *                                                                            *
\******************************************************************************/

SOAP_FMAC5 int SOAP_FMAC6 soap_serve(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_request(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_ns__GetCalOpenSession(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_ns__GetCalSearchCal(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_ns__GetCalQueryStatus(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_ns__GetCalCancelSession(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_ns__GetStar(struct soap*);

SOAP_FMAC5 int SOAP_FMAC6 soap_serve_ns__GetServerStatus(struct soap*);

#endif

/* End of soapStub.h */
