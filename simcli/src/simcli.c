/*+++++++++++++++
.IDENTIFICATION smbcli.c
.LANGUAGE       C
.AUTHOR         Francois Ochsenbein, Marc Wenger [CDS]
.ENVIRONMENT    
.KEYWORDS       
.VERSION  1.0   25-May-1993 Creation
.VERSION  1.1   05-Jun-1993: simbad_telldata
.VERSION  1.2   16-Jun-1993 (Pacific Time !!!): #define OK 1
                             suppress sk_routines error messages
.VERSION  1.2   16-Jun-1993 (European Time !!): Buffered results.
.VERSION  1.3   07-Dec-1993 Macro for setting specific formats on server side
.VERSION  1.4   08-Feb-1994 Use only UIF_LIST code
.VERSION  1.5   01-Mar-1994 Set an application name
.VERSION  1.6   05-May-1994: Take care of EMPTYLIST
.VERSION  1.7   07-Oct-1994  simbad_disconnect: check .answer == NULL before
                             KillData (Fix proposed by Song Yom/HEASARC on
                             7 Apr 94. - yom@astd3.gsfc.nasa.gov)
.VERSION  1.8   25-Nov-1994  Problem with empty result: no bye !!!
.VERSION  1.9   17-Jan-1994  Modification of REF_INTRO define
.VERSION  1.91  20-Jun-1995  into ANSI
.COMMENTS       
---------------*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <simclic.h>
#include <simcli.h>
#include <simcliPrivate.h>
#include "log.h"

#ifndef _PARAMS
#ifdef __STDC__
#define _PARAMS(A)      A       /* ANSI */
#define _TYPE(A)	A
#define _DCL		0
#else
#define _PARAMS(A)      ()      /* Traditional */
#define _TYPE(A)
#define _DCL		1
#endif
#endif

#define DEFAULT_NODE         "130.79.128.4"
#define DEFAULT_SERVICE      "1674"
#define MAX_HANDLES    10

#define DATABUF	  4096		/* Default & Increment for buf */
#define DATATYPE    32		/* Number of different data types */
#define DATALINE   256		/* Default Number of lines	*/
#define MAXITYPE    14		/* Maximal allowed length of a datatype */

extern char *sk_error  _PARAMS((void));

#define EOS    '\0'
#define YES    1
#undef  OK
#define OK     1
#define NO     0
#define NOK    0

typedef struct {	/* Data kept as result from Simbad Query */
    int  handle;		/* Related Handle number   */
    int  abuf, ubuf, obuf ; 	/* Allocated, Used, Offset */
    char *buf;			/* Data buffer */
    int  aline, uline, oline; 
    int  *line;			/* Array to beginning of lines */
    int  atype, utype, otype;
    int  *type;			/* Array of lines starting with data element */
    int  atell;			/*	Allocated bytes for .. */
    char *tell;			/* Space-separated list of existing datatypes */
    char dtype[MAXITYPE+2];	/* Asked datatype; room for ending blank \0 */
    int  aquery;
    char *query;		/* Query to send if data asked for */
  } SMB_DATA;

typedef struct {
	int  sock ;                 /* Socket number for the connection */
	char *question ;
	SMB_DATA *answer;
	char *errormsg ;
	int answertype ;
	int status ;
	int nbobj ;
	int objnum ;
    } SMB_HANDLE ;

#define isopen(h) (h >= 0 && h < MAX_HANDLES && smb_handle[h].status != H_FREE)
#define existdata(h) (smb_handle[hand].status == H_LIST || \
                      smb_handle[hand].status == H_OBJECT || \
                      smb_handle[hand].status == H_OBJLIST)
#define MALLOC(dtype, n)	(dtype *)malloc(n*sizeof(dtype))
#define REALLOC(p,dtype, n)	(dtype *)realloc(p, n*sizeof(dtype))

#define ERRNO(v)  	simbad_errno_value = v
#define ERRMSG(t)       simbad_err_text = t

#define ERR_TELECOM     1
#define ERR_SERVER      2
#define ERR_CLIENT      3
#define ERR_SIMBAD     11

#define H_FREE          0
#define H_NODATA        1
#define H_OBJECT        4
#define H_LIST          5
#define H_OBJLIST       6

#define APPLI_ENVAR_NAME  "SIMBAD_APPLI"
#define DEF_APPLI_NAME    "simcli"

static SMB_HANDLE smb_handle[MAX_HANDLES] ;
static int init = NO ;

static char *srv_txt ;
static int srv_code ;
static int srv_len ;
static int simbad_errno_value = 0 ;
static char *simbad_err_text = NULL ;
static char appli_name[8] = "" ;

static int simbad_exch();

/*============================================================================
 *		General Utility Routines
 *============================================================================*/
static int strloc(str, c)
/*+++++++
.PURPOSE Locate the first occurence of character `c'
.RETURNS Index of `c' in str; length of str if `c' not found.
---------*/
  char *str;	/* IN: string to scan	*/
  char c;	/* IN: char to locate	*/
{
  register char *p;

    for (p=str; *p; p++)
  	if (*p == c)	break;
    return(p-str);
}
static char *strsave(str)
	char *str ;
{
	char *newstr ;
	newstr = (char *) malloc(strlen(str)+1) ;
	strcpy(newstr,str) ;
	return(newstr) ;
}

static int CompareTypes(s1, s2)
/*++++++++++++++++
.PURPOSE  Compare First word (followed by a blank) of 2 strings
.RETURNS  Difference (0 = types agree)
-----------------*/
  char *s1;	/* IN: String to compare */
  char *s2;	/* IN: String to compare */
{
  int diff;
    for (diff = 0; diff == 0; s1++, s2++) {
	diff = *s1 - *s2;
	if (isspace(*s1)) break;
    }
    return(diff);
}

/*============================================================================
 *		Send Options to the Server
 *============================================================================*/
static int SendOptions(hno, param, str)
/*++++++++++++++++
.PURPOSE  Send option(s) made of  O$param_[name] value(s)
.RETURNS  0 / -1 (Error)
.REMARKS  Examples: 
	param = "O.UBV", str="data=M"          ==> send "OO_data M"
	param = "",   str="bibyears=1992/1995" ==> send "O_bibyears 1992/1995"
-----------------*/
  int hno;	/* IN: Simbad Handle number */
  char *param;	/* IN: Data type (e.g. O for Observation, _ for Query */
  char *str;	/* IN: String with [param=]value */
{
  char buf[128], *b;
  char *o, e;
  int len, eq, st;

	/* Construct O$param_ */
    b = buf; *(b++) = 'O'; *(b++) = '0';
    while (isalnum(*param)) *(b++) = *(param++);

	/* Split str into pieces separated by semi-colons */
    while(*str) {
	while (isspace(*str)) str++;
	len = strloc(str, ';');
	if (len == 0) { str++; continue; }
	o = b;
	e = str[len]; str[len] = 0;		/* Remove the ; */
	if (!*str) continue;
	eq = strloc(str, '=');
	if (str[eq]) {				/* There is an = sign */
	    while (*str != '=') *(o++) = *(str++);
	    str++;	/* Skip the = */
	    while (isspace(*str)) str++;
	}
	if (o[-1] != ' ') *(o++) = ' ';
	len = strlen(str) + (o-buf);
	if (len < sizeof(buf)) strcpy(o, str);
	st = simbad_exch(hno, buf);		/* Send the Question  */
	str += strlen(str);
	if (e) *(str++) = e;
	if (st != OK)	return (-1);
    }
    return (0);
}

/*============================================================================
 *		Routines dealing with SMB_DATA
 *============================================================================*/

static int ClearData(data)
/*++++++++++++++++
.PURPOSE  Clear the data buffer
.RETURNS  0
-----------------*/
  SMB_DATA *data;	/* IN: Buffer to clear */
{
    data->ubuf = data->uline = data->utype = 0;
    data->obuf = data->oline = data->otype = 0;
    *(data->dtype) = 0;
    *(data->query) = 0;
    *(data->tell) = 0;
    return(0);
}

static SMB_DATA *NewData(handle)
/*++++++++++++++++
.PURPOSE  Create new SMB_DATA structure with defaults
.RETURNS  Filled structure
.REMARKS  NULL if fails
-----------------*/
  int handle;	/* IN: Associated handle number */
{
  SMB_DATA *data;
    data = MALLOC(SMB_DATA, 1);
    data->handle = handle;
    data->abuf  = DATABUF ; data->buf  = MALLOC(char, data->abuf);
    data->aline = DATALINE; data->line = MALLOC(int, data->aline);
    data->atype = DATATYPE; data->type = MALLOC(int, data->atype);
    data->aquery = 64; data->query = MALLOC(char, data->aquery);
    data->atell = MAXITYPE*16; data->tell  = MALLOC(char, data->atell);
    ClearData(data);
    return(data);
}

static int KillData(data)
/*++++++++++++++++
.PURPOSE  Free allocated data buffer
.RETURNS  0
-----------------*/
  SMB_DATA *data;	/* IN: Buffer to clear */
{
    free(data->buf);
    free(data->line);
    free(data->type);
    free(data->query);
    free(data->tell);
    free(data);
    return(0);
}

static int AddData(data, str)
/*++++++++++++++++
.PURPOSE  Append data into buffer, update related info
.RETURNS  0
-----------------*/
  SMB_DATA *data;  /* IN: Buffer to clear */
  char *str;	  /* IN: Data made of lines, 1st word of each = datatype */
{
  int len, n;
  char *p, *b, *t;

    len = strlen(str); 

	/* 1) Append string to existing ones */
    n = data->ubuf + len;
    if (n >= data->abuf) {	/* Have to expand buffer ... */
	n = (n + DATABUF - 1) / DATABUF;
	data->abuf = n * DATABUF;
	data->buf = REALLOC(data->buf, char,  data->abuf);
    }
    strcpy(data->buf + data->ubuf, str);
    data->obuf = data->ubuf;
    data->ubuf += len;

	/* 2) Set the array of line beginnings & Data Types */
    t = data->utype > 0 ? 
	data->buf + data->line[data->type[data->utype-1]] 
        : " ";
    for (b = data->buf + data->obuf; ; b = p ) { 
	p = strchr (b, '\n');
	if (!p) break;
	*(p++) = 0;	/* Replace the \n */
	if (isspace(*b)) continue;	/* Empty or useless line */
	if (!strchr(b, ' ')) continue;	/* No space in line ==> No data... */
	if (data->uline >= data->aline) {	/* Have to expand line array */
	    data->aline += DATALINE;
	    data->line = REALLOC(data->line, int, data->aline);
	}
	data->line[data->uline] = b - data->buf;
	if (CompareTypes(t, b)) {		/* New Data Type */
	    if (data->utype >= data->atype-1) {	/* Have to expand... */
		data->atype += DATATYPE;
		data->type = REALLOC(data->type, int, data->atype);
	    }
	    data->type[data->utype] = data->uline;
	    data->utype += 1;
	    t = b;
	}
	data->uline += 1;
    }
    data->obuf = b - data->buf;
    data->type[data->utype] = data->uline;	/* Last line */

    return(0);
}

static int TellData(data)
/*++++++++++++++++
.PURPOSE  Prepare the "tell" substructure filled with existing astrotypes
.RETURNS  Number of different astrotypes (number of words in data->tell)
-----------------*/
  SMB_DATA *data; /* IN: Buffer to clear */
{
  int n, i, *it; 
  char *o, *p, *at;

    o = data->tell; 

	/* Find out the line starting with dtype */
    for (it = data->type, i = data->utype; --i >= 0; it++) {
	at = data->buf + data->line[*it];	/* Data Type */
	for (p = at; isgraph(*p); p++) ;
	n = p - at + 1;
	if (((o-data->tell) + n) >= data->atell) {	/* Must expand ... */
	    data->atell += MAXITYPE*4;	/* Prepare room for 4 types */
	    data->tell = REALLOC(data->tell, char, data->atell);
	}
	if (*at == '#') {	/* We've a number of data. */
	    if (!atoi(p)) continue;	/* Counter indicates 0 */
	    at++, n--;
            if (*at == 'O') at[(--n)-1] = ' ' ;    /* replace colon by space */
	}
	strncpy(o, at, n);			/* Copy including blank */
	o += n;
    }
    if (o > data->tell) {
	o--;
    	if (isgraph(*o)) o++;
    }
    *o = 0;					/* Terminate string */
    return(data->utype);	/* This is the number of different data types */
}

static int FindData(data, dtype)
/*++++++++++++++++
.PURPOSE  Find data in buffer
.RETURNS  Number of existing dtypes
-----------------*/
  SMB_DATA *data; /* IN: Buffer to clear */
  char *dtype;	  /* IN: Data astrotype to look for */
{
  char *b, *p, buf[MAXITYPE+3];
  int i, h, diff, *t;
   /*static int first;  */

    *(data->query) = 0;
    i = strlen(dtype); 
    if (i > MAXITYPE) return(0);	/* Too long datatype */
    b = dtype + i - 1;
    if (*b != ' ') i++;
    strcpy(data->dtype, dtype);
    if (*b != ' ') strcat(data->dtype, " ");

	/* Loop over lines received from Server; 
	   h indicates the number of data found from # */
    buf[0] = '#'; strcpy(buf+1, data->dtype);
    data->otype = -1; 
    h = 0;
    for (t = data->type, i = data->utype; --i >= 0; t++) {
	b =  data->buf + data->line[*t];
	if (*b == '#') {
	    if (h > 0) continue;		/* # already found   */
	    diff = CompareTypes(buf, b);
	    if (diff == 0) { char *q;		/* Found with #      */
		q = data->query; *(q++) = 'Q';
		for (p = b; isgraph(*p); p++, q++) 
		    *q = isalnum(*p) ? *p : '0' ;
		*q = 0;
		h = atoi(p);			/* Number (2nd word) */
	    }
	    continue;
	}
	diff = CompareTypes(buf+1, b);
	if (diff == 0) break;			/* Found without #   */
    }

    if (i >= 0)	{			/* Data were found without # */
	*data->query = 0;
	data->otype = t - data->type;	/* Agree */
	data->oline = *t;		/* First Line */
	if (h <= 0) h = t[1] - t[0];	/* This gives the number of lines */
	return (h);
    }
    if (h <= 0)  		   /* #Data was found with 0 data... */
	*data->query = 0; 

    return(h);
}

static char *GetData(data, number)
/*++++++++++++++++
.PURPOSE  Find numbered data in buffer
.RETURNS  String with data / NULL string
-----------------*/
  SMB_DATA *data; /* IN: Buffer to clear */
  int number;	  /* IN: Number in range 1...n */
{
  char *p;
  int  *t;
    if (*data->query) {		/* There is a query to perform. Do it ! */
	simbad_exch(data->handle, data->query);
	FindData(data, data->dtype);
	*data->query = 0;
    }
    if (data->otype < 0) return(NULL);
    if (number <  0) return(NULL);
    t = data->type + data->otype;
    if (number == 0) data->oline += 1;
    else data->oline = *t + number;
#ifdef TEST
    printf(".... GetData number=%d (oline=%d), limites=[%d,%d]\n", 
	number, data->oline, t[0], t[1]);
#endif

    if (data->oline > t[1]) return(NULL);
    p = data->buf + data->line[data->oline-1];
    if (CompareTypes(data->dtype, p)) {
#ifdef TEST
	printf("++++ GetData, type disagree dtype=%s, found=%s\n", 
		data->dtype, p);
#endif
	return(NULL);
    }
    p = strchr(p, ' ');
    if (!p) p++;
    return(p);
}



static void noresult(ps)
	SimDatStruct *ps ;
{
	srv_txt = ps->msg ;
	srv_code = ps->code ;
	srv_len  = ps->length;
}
static void fresult(ps)
	SimDatStruct *ps ;
{
	srv_txt = ps->msg ;
	srv_code = ps->code ;
	srv_len  = ps->length;
}


static int simbad_send(hand,str)
	int hand ;
	char *str ;
{
  int st;
	st = ClientTreat(smb_handle[hand].sock,str,noresult);
#ifdef TEST
       	printf("SENT    <%s> st=%d, code=%d, length=%d. Received Text:\n%s\n",
		    str, st, srv_code,srv_len,srv_txt);
#endif
	return(OK) ;
}

static int simbad_exch(hand,str)
	int hand ;
	char *str ;
{
  char *msg;
  int st;
#ifdef TEST
	printf("EXCH: handle=%d (socket=%d)\n%s\n",
		hand,smb_handle[hand].sock,str) ;
#endif
	/* ClientTreat returns NOEND if the answer is not complete... */
	for (msg=str; msg; ) {
	    st = ClientTreat(smb_handle[hand].sock,msg,fresult);

#ifdef TEST
       	printf("RECV to <%s> st=%d, code=%d, length=%d. Text:\n%s(Hit Return)",
		    msg, st, srv_code,srv_len,srv_txt);
	getchar();
#endif
	    if (srv_code == UIF_ERROR)
	    {
		if (smb_handle[hand].errormsg != NULL)
			free(smb_handle[hand].errormsg) ;
		smb_handle[hand].errormsg = (char *) malloc(strlen(srv_txt)+1);
		strcpy(smb_handle[hand].errormsg,srv_txt) ;
	    }
	    else
	    {
#define REF_INTRO "SIMBAD reference code: "
		char *p = srv_txt ;
		if (msg == str &&
		    strlen(srv_txt) > sizeof(REF_INTRO) &&
		    strncmp(srv_txt,REF_INTRO,sizeof(REF_INTRO)-1) == 0)
		{
			while(*p && *p != '\n') p++ ;
			p++ ;
		}
		AddData(smb_handle[hand].answer, p) ;
	    }
	    if (st == NOEND) msg  = "";
	    else msg = (char *)0;

	}
	smb_handle[hand].answertype = srv_code ;

	return(OK) ;
}

static int simbad_open(hand,node,service,userid,passwd,appli)
	int hand ;
	char *node ;
	char *service ;
	char *userid ;
	char *passwd ;
	char *appli ;
{

	int s ;

    logExtDbg("simbad_open()"); 
	s = ClientOpen(noresult,node,service,userid,passwd,appli) ;

	if (s > 0)
	{
		smb_handle[hand].sock = s ;
		return(OK) ;
	}
	else
		return(s) ;
}


static int simbad_close(hand)
	int hand ;
{
    logExtDbg("simbad_close()"); 
	ClientClose(smb_handle[hand].sock) ;
	return(OK) ;
}

static int simbad_endquery(hand)
	int hand ;
{
    logExtDbg("simbad_endquery()"); 
	if (! isopen(hand))
	{
		ERRNO(ERR_CLIENT) ;
		ERRMSG("Handle not opened") ;
		return(ERROR) ;
	}

	simbad_send(hand,"simbad search") ;
	smb_handle[hand].nbobj = 0 ;
	if (smb_handle[hand].question != NULL)
	{
		free(smb_handle[hand].question) ;
		smb_handle[hand].question = NULL ;
	}
	ClearData(smb_handle[hand].answer);
	if (smb_handle[hand].errormsg != NULL)
	{
		free(smb_handle[hand].errormsg) ;
		smb_handle[hand].errormsg = NULL ;
	}
	smb_handle[hand].status = H_NODATA ;

	return(OK) ;
}

/*============================================================================
 *		Routines dealing with SMB_HANDLE
 *============================================================================*/
int simbad_connect(node,service,userid,passwd)
/*++++++++++++++++
.PURPOSE  Connect to Simbad
.RETURNS  The "Simbad-Handle" Number / -1 if Error
-----------------*/
	char *node ;
	char *service ;
	char *userid ;
	char *passwd ;
{
    logExtDbg("simbad_connect()"); 
	int i ;
	int hand ;
	int stat ;
	char *NODE, *SERVICE ;

	/* initialize the handle list */
	if (! init)
	{
		for (i=0 ; i < MAX_HANDLES ; i++)
		{
			smb_handle[i].question = NULL ;
			smb_handle[i].answer = (SMB_DATA *)0;
			smb_handle[i].errormsg = NULL ;
			smb_handle[i].status = H_FREE ;
		}
		init = YES ;
	}

	/* suppress sk_io error display */
	sk_setlog(NULL) ;

	/* look for a free handle */
	for (hand=0 ; hand < MAX_HANDLES ; hand++)
		if (smb_handle[hand].status == H_FREE) break ;
	if (hand >= MAX_HANDLES)
	{
		ERRNO(ERR_CLIENT) ;
		ERRMSG("Max number of handles opened") ;
		return(ERROR) ;
	}

	if (! smb_handle[hand].answer) 
		smb_handle[hand].answer = NewData(hand);

	/* open the connection */
	NODE = (node==NULL || *node==EOS) ? DEFAULT_NODE : node ;
	SERVICE = (service==NULL || *service==EOS) ? DEFAULT_SERVICE : service ;

	simbad_appli(NULL) ;
	if ((stat = simbad_open(hand,NODE,SERVICE,userid,passwd,appli_name)) < 0)
	{
		smb_handle[hand].status = H_FREE ;
		hand = ERROR ;
		ERRNO(stat == -2 ? ERR_CLIENT : ERR_TELECOM) ;
		ERRMSG("The client version or application name is not accepted by the server") ;
		return(ERROR) ;
	}
	smb_handle[hand].status = H_NODATA ;

	/* send the macro setting the right parameters */
	simbad_send(hand,"simclifmt") ;

	return(hand) ;
}

int simbad_disconnect(hand)
	int hand ;
{
	int stat ;

    logExtDbg("simbad_disconnect()"); 
	if (! isopen(hand))
	{
		ERRNO(ERR_CLIENT) ;
		ERRMSG("Handle not opened") ;
		return(ERROR) ;
	}

	simbad_send(hand,"simbad-reflist reset") ;
	simbad_send(hand,"session exit") ;

	stat = simbad_close(hand) ;

	if (smb_handle[hand].question != NULL)
	{
		free(smb_handle[hand].question) ;
		smb_handle[hand].question = NULL ;
	}
	if (smb_handle[hand].answer != NULL)
	{
		KillData(smb_handle[hand].answer);
		smb_handle[hand].answer = NULL ;
	}
	if (smb_handle[hand].errormsg != NULL)
	{
		free(smb_handle[hand].errormsg) ;
		smb_handle[hand].errormsg = NULL ;
	}
	smb_handle[hand].status = H_FREE ;

	return(stat) ;
}

int simbad_query(hand,question,options)
	int hand ;
	char *question ;
	char *options ;
{
	int minlg ;

    logExtDbg("simbad_query()"); 
	if (! isopen(hand))
	{
		ERRNO(ERR_CLIENT) ;
		ERRMSG("Handle not opened") ;
		return(ERROR) ;
	}

	/* the previous query is ended */
	simbad_endquery(hand) ;

	/* Send Options */
	if (options) {
		if (SendOptions(hand, "", options) < 0)
			return(ERROR);
	}

	/* Return if question is empty */
	if (!*question) return(0) ;

	/* Save the question request */
	smb_handle[hand].question = strsave(question) ;

	/* Send the question and get the answer */
	simbad_exch(hand,question) ;

	/* the answer can be (see srv_code):
	 *   of type UIF_LIST, wich needs to extract the number of objects
	 *   of type UIF_DATA, which correspond to a data string for an unique
         *                     object to put in the handle struct.
	 *   of type UIF_ERROR,in which case, the error message is in the
	 *                     handle structure.
	 */

	/* Examine the returned code
	 */
	if ((minlg = smb_handle[hand].answer->ubuf) > 4) minlg = 4 ;
	switch(srv_code) {
	  case UIF_ERROR:
		ERRNO(ERR_SIMBAD) ;
		return(ERROR) ;
	  case UIF_LIST:
#if 0
	    strncmp(smb_handle[hand].answer->buf,"====",minlg) == 0 ||
	    strncmp(smb_handle[hand].answer->buf,"Numb",minlg) == 0)
#endif
	    {
		char *p ;

		/* Object list build on server side
		 * The number of object is the last number in the result string
		 */
		for (p = srv_txt + strlen(srv_txt) - 1 ;
		     p >= srv_txt && ! isdigit(*p) ;
		     p--) ;  /* go backwards to the last digit */
		while(p >= srv_txt && isdigit(*p)) p-- ;
			     /* go backwards to the beginning of the number */
		if (p < srv_txt)
		{
			ERRNO(ERR_SERVER) ;
			ERRMSG("Incorrect object list header send by the server") ;
			return(ERROR) ;
		}
		p++ ;
		smb_handle[hand].nbobj = atoi(p) ;
		smb_handle[hand].objnum = 0 ;
		smb_handle[hand].status = H_LIST ;
		ClearData(smb_handle[hand].answer) ;
	    }
	    break;
	  case UIF_EMPTYLIST:
		smb_handle[hand].nbobj = 0;
		smb_handle[hand].objnum = 0 ;
		break;
	  default:
		/* Data received directly */
		smb_handle[hand].nbobj = 1 ;
		smb_handle[hand].objnum = 0 ;
		smb_handle[hand].status = H_OBJECT ;
	}
	return(smb_handle[hand].nbobj) ;
}

int simbad_retrieve(hand,num)
	int hand ;
	int num ;
{
	char str[32] ;
    logExtDbg("simbad_retrieve()");
	if (! isopen(hand) || ! existdata(hand)) 
	{
		ERRNO(ERR_CLIENT) ;
		ERRMSG("Handle not opened, or no object available") ;
		return(ERROR) ;
	}

	/* compute the right object number */
	if (num == 0) smb_handle[hand].objnum++ ;
	else          smb_handle[hand].objnum = num ;
	if (smb_handle[hand].objnum <= 0 || 
	    smb_handle[hand].objnum > smb_handle[hand].nbobj) return(0) ;
#ifdef TEST
		smb_handle[hand].objnum,smb_handle[hand].status) ;
#endif

	/* put the standard data in the handle answer string */
	switch(smb_handle[hand].status)
	{
		case H_OBJECT: /* single object already on client side */
		break ; /* nothing to do */

		case H_OBJLIST: /* an object in the list is current */
		ClearData(smb_handle[hand].answer) ;
		simbad_send(hand,"bye") ; /* comes back to the list object */
		smb_handle[hand].status = H_LIST ;
		/* no break: must continue with the next case */

		case H_LIST:    /* the server is at the list prompt */
		sprintf(str,"%d",smb_handle[hand].objnum) ;
		simbad_exch(hand,str) ;
#if 0
		smb_handle[hand].status = H_OBJLIST ;
#else /*****FO**** PROBLEM WITH EMPTY RESULT: no BYE !!! */
		smb_handle[hand].status =
			(smb_handle[hand].answer)-> ubuf ? H_OBJLIST : H_LIST;
#endif
		break ;

		default:        /* should not happen */
		ERRNO(ERR_SERVER) ;  /*  ??? Error from the UIF class */
		ERRMSG("Bad data code returned by the server") ;
		return(ERROR) ;
	}
	return(smb_handle[hand].nbobj - smb_handle[hand].objnum + 1) ;
}

char *simbad_telldata(hand)
  int hand;		/* IN: Simbad Connection number */
{
    logExtDbg("simbad_telldata()"); 
    if (! isopen(hand) || ! existdata(hand)) return(NULL) ;
    TellData(smb_handle[hand].answer);
    return((smb_handle[hand].answer)->tell);
}
  
int simbad_findata(hand, astrotype, options)
  int hand;		/* IN: Simbad Connection number */
  char *astrotype;	/* IN: Data type to look at */
  char *options;	/* IN: Option string        */
{
    logExtDbg("simbad_findata()"); 
    if (! isopen(hand) || ! existdata(hand))
    {
	ERRNO(ERR_CLIENT) ;
	ERRMSG("Handle not opened, or no object available") ;
	return(ERROR) ;
    }

    	/* Send Options */
    if (options) {
	if (SendOptions(hand, astrotype, options) < 0)
	    return(ERROR);
    }
    return(FindData(smb_handle[hand].answer, astrotype));
}

char *simbad_getdata(hand, number)
  int hand;		/* IN: Simbad Connection number */
  int number;		/* IN: Number (0 = Next)    */
{
    logExtDbg("simbad_getdata()"); 
    if (! isopen(hand) || ! existdata(hand))
    {
	ERRNO(ERR_CLIENT) ;
	ERRMSG("Handle not opened, or no object available") ;
	return(NULL) ;
    }
    return(GetData(smb_handle[hand].answer, number));
}

char *simbad_error(h)
	int h ;
{
    logExtDbg("simbad_error()"); 
	switch(simbad_errno_value)
	{
		case ERR_TELECOM:
			return(sk_error()) ;
		case ERR_SERVER:
		case ERR_CLIENT:
			return(simbad_err_text) ;
		case ERR_SIMBAD:
			return(smb_handle[h].errormsg) ;
		default:
			return("Unknown error code") ;
	}
}


int simbad_errno()
{
    logExtDbg("simbad_errno()"); 
	return(simbad_errno_value) ;
}


void simbad_appli(str)
	char *str ;
{
	char *p ;

    logExtDbg("simbad_appli()"); 
	if (str && *str)
		p = str ;
	else 
		/* Initialisation by env. variable or default name */
		if ((p = getenv(APPLI_ENVAR_NAME)) == NULL)
			p = DEF_APPLI_NAME ;
	strncpy(appli_name,p,sizeof(appli_name)-1) ;
	appli_name[sizeof(appli_name)-1] = '\0' ;
}

