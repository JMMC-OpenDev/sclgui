/****************************************************************************/
/*                      MODULE: client.c                                    */
/*                                                                          */
/* ROLE : Fonctions permettant de dialoguer avec le serveur de SIMBAD       */
/*                                                                          */
/* IMPORTATION :                                                            */
/*               libsk.a						    */
/*                                                                          */
/* EXPORTATIONS :                                                           */
/*                                                                          */
/* FONCTIONS LOCALES :                                                      */
/*                                                                          */
/* AUTEURS : Eric DIVETAIN ( Observatoire astronomique de Strasbourg )      */
/*                                                                          */
/* DATE DE CREATION : 15/07/93                                              */
/*                                                                          */
/* MODIFICATIONS :							    */ 
/*   MW - fev 94 :							    */
/*	envoi d'un code application et d'un numero de version		    */
/*   MW - mar 94 :							    */
/*      Bug dans ClientTreat(): return d'une valeur si appel recursif	    */
/*	Transfo de ClientTransFile() en ClientSendFile()		    */
/*	et rajout de ClientRcvFile()					    */
/*   FO - Jun 95: allow ANSI                                                */
/*   MW - Dec 95: initialize buffer = NULL                                  */
/*                                                                          */
/****************************************************************************/
/* Versions:
	V300: debut fev 94  - version temporarire transmise a dao et eso
	V310: 1-mar-94      - version diffusee au 1/3/94
	V320: 7-mar-94      - version avec ClientSendFile() et ClientRcvFile()
	V321: 20-jun-95     - TestVersionAppli
*/

#define VERSION "V320"

#ifndef _PARAMS
#ifdef __STDC__
#define _PARAMS(A)      A       /* ANSI */
#else
#define _PARAMS(A)      ()      /* Traditional */
#endif
#endif

/****************************************************************************/
/* INCLUDES SYSTEMES                                                        */
/****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <signal.h>
#include <errno.h>
#include <unistd.h>

#include "log.h"

/****************************************************************************/
/* INCLUDES UTILISATEUR                                                     */
/****************************************************************************/
#include <simclic.h>
#include <simcliPrivate.h>

/****************************************************************************/
/* CONSTANTES                                                               */
/****************************************************************************/
#define FBUF 100
#define	HEAD_BUF 8		/* Taille de l'entete des chaines echangees */
#define OKSUITE "OKSUITE"	/* utilise dans ClientTreat() */
#define PASUITE "PASUITE"
#define AGAIN	"AGAIN"
#define UIF_CONTROLE	20	/* Code de controle interne a l'uif */

#define CODSIMBAD "1"
#if 0
#define CODTRANFI "3"
#endif
#define CODSENDFI "3"
#define CODRCVFI  "4"

/****************************************************************************/
/* VARIABLES                                                                */
/****************************************************************************/
static char *sendstring; /* Pointeur sur la chaine a envoyer ClientSend() */
static int stringsize = 0;/* Longueur de la chaine a envoyer ClientSend() */
static char *buffer = NULL;	/* Pointeur sur le buffer recu ClientGet() */
static int  bufsize = 0;/* Longueur de la chaine recue ClientGet() */
static char dirpath[256];
static char statserveur[128];
static char statservice[32];
static char statname[32];
static char statpasswd[32];

/****************************************************************************/
/* FONCTIONS IMPORTEES                                                      */
/****************************************************************************/
/* Library: /usr/local/lib/libsk.a */
extern int sk_connect   _PARAMS((char *machine, char *service,
                                 char *username, char *password));
extern int sk_puts     _PARAMS((int plug, char *buf));
extern int sk_get      _PARAMS((int plug, char *buf, int len));
extern int sk_gets     _PARAMS((int plug, char *buf, int len));
extern int sk_close    _PARAMS((int plug));

/****************************************************************************/
/* FONCTIONS LOCALES                                                        */
/****************************************************************************/

/****************************************************************************/
/* FONCTION :                                                               */
/*	int ClientSend( ear, ask_string )				    */
/* PRAMETRES :                                                              */
/*      -ENTREES :                                                          */
/*		int ear: numero de socket				    */
/*		char *ask_string : requete				    */
/*      -SORTIES :                                                          */
/*		retourne: OK, ERROR, CONNEXIONERROR			    */
/* ROLE :                                                                   */
/*--------------------------------------------------------------------------*/
/* Realisation :                                                            */
/*	Cette fonction permet d'envoyer une chaine de caractere associee    */
/* a la structure SimDatStruct (client.h)				    */
/****************************************************************************/

static int ClientSend( ear, askstring )

    int
    ear;
    char
    *askstring;
{
    int
        asksize;

    logExtDbg("ClientSend()"); 
    asksize = strlen( askstring );
    if( asksize + HEAD_BUF + 1 >= stringsize )
    {
        if( stringsize != 0 ) 
        {
            free(sendstring);
        }
        stringsize = asksize + HEAD_BUF + 1;
        stringsize = (stringsize+511)&~511;

        sendstring = malloc( stringsize );
        if( sendstring == NULL )
        {
            perror("malloc() in ClientSend()");
            return( ERROR );
        }
    }
    sprintf(sendstring,"%c%c%6d%s", '*','*', asksize, askstring);

	if( sk_put( ear, sendstring ) == ERROR )
	{
		return( CONNEXIONERROR );
	}
	return( OK );
}

/****************************************************************************/
/* FONCTION : ClientGet                                                     */
/* PRAMETRES :                                                              */
/*      -ENTREES : 							    */	
/*		int ear : numero de socket                                  */
/*		SimDatStruct getstruct : structure initialisee		    */
/*      -SORTIES :                                                          */
/*		retourne : OK, CONNEXIONERROR, NOEND                        */
/* ROLE :								    */
/* 	Cette fonction recupere la chaine de caractere se trouvant sur le   */
/* socket ear et initialise la structure getstruct                          */
/*--------------------------------------------------------------------------*/
/* Realisation :                                                            */
/****************************************************************************/

static int ClientGet( ear, getstruct )
	int
		ear;
	SimDatStruct
		*getstruct;
{

	char
		head_string[HEAD_BUF + 1],
		type[3],
		flag;
	int
		lenght = 0;

    logExtDbg("ClientGet()"); 
	getstruct->msg = 0;

	if( sk_get( ear, head_string, HEAD_BUF ) == ERROR )
	{
		return( CONNEXIONERROR );
	}
	head_string[HEAD_BUF] = '\0';

	type[0] = head_string[0];
	type[1] = head_string[1];
	type[2] = '\0';
	getstruct->code =  atoi(type);
	flag = head_string[2];

	lenght = atoi( &(head_string[3]) );
	getstruct->length = lenght;

	if (lenght >= bufsize)
	{
		if(buffer)
		{
			free(buffer);
		}

		bufsize = lenght + 8 ;
	    	bufsize = (bufsize+511)&~511;

	    	buffer = malloc(bufsize);
	    	if( buffer == NULL )
	    	{
			perror("malloc() in ClientGet()");
			return( ERROR );
	    	}
	}
	getstruct->msg = buffer;

	if( sk_get( ear, getstruct->msg, lenght) == ERROR )
	{
		return( CONNEXIONERROR );
	}
	getstruct->msg[lenght] = '\0';

	if( flag == 'e' )
	{
		return( OK );
	}
	else if( flag == 's' )
	{
		return( NOEND );
	}

    return( OK );
}

/****************************************************************************/
/* FONCTION :	                                                            */
/*	static int ClientConnect( display_fct, host, service, name, passwd )*/
/* PRAMETRES :                                                              */
/*      -ENTREES :                                                          */
/*		char *host: nom du serveur                                  */
/*              char *service: nom du service                               */
/*              char *name: nom different du userid                         */
/*              char *passwd: password                                      */
/*      -SORTIES :                                                          */
/*		void (*display_fct)(): Fonction de traitement des messages  */
/*		retourne: ERROR ou ear (numero de socket)		    */
/* ROLE :                                                                   */
/*	Cette fonction permet de se connecter au serveur de SIMBAD, et      */
/* retourne le numero de socket                                             */
/*--------------------------------------------------------------------------*/
/* Realisation :                                                            */
/****************************************************************************/

static int ClientConnect( display_fct, host, service, name, passwd )
	void
		(*display_fct)();
	char
		*host,
		*service,
		*name,
		*passwd;
{
	int
		ear;
	SimDatStruct
		teststruct;

    logExtDbg("ClientConnect()"); 
	teststruct.code = 0;
	teststruct.msg = 0;
	teststruct.length = 0;

	ear = sk_connect( host, service, name, passwd );
	if( ear < 0 )
	{
		teststruct.code = UIF_CONNEXIONERROR;
		teststruct.msg = ERR_SOCKET;
		if( display_fct != NULL )
		{
			display_fct( &teststruct );
		}
		return( ERROR );
	}
	return( ear );
}

/****************************************************************************/
/* FONCTION :                                                               */
/*	int ClientOpen( display_fct, host, service, name, passwd, appli )   */
/* PRAMETRES :                                                              */
/*      -ENTREES :                                                          */
/*		char *host: nom du serveur 				    */
/*		char *service: nom du service 				    */
/*		char *name: nom different du userid 			    */
/*		char *passwd: password					    */	
/*		char *appli: nom de l'application			    */
/*      -SORTIES :                                                          */
/*		void (*display_fct)(): Fonction de traitement des messages  */
/*		retourne: ERROR ou BAD_VERSION ou ear (numero de socket)    */
/* ROLE :                                                                   */
/*	Cette fonction permet de se connecter au serveur de SIMBAD, et      */
/* transmet le motd a l'aide de la fonction 'display_fct()' qui a pour      */
/* argument l'adresse d'une structure du type 'SimDatStruct'                */
/* ( voir client.h ). Cette fonction envoie le code associe au service      */
/* SIMBAD		                                                    */	
/*--------------------------------------------------------------------------*/
/* Realisation :                                                            */
/****************************************************************************/

int ClientOpen( display_fct, host, service, name, passwd, appli )
	void
		(*display_fct)();
	char
		*host,
		*service,
		*name,
		*passwd,
		*appli;
{
	SimDatStruct welcome_msg;
	int
		ear;

    logExtDbg("ClientOpen()"); 
	strcpy( statname, name);
	strcpy( statpasswd, passwd);
	strcpy( statserveur, host);
	strcpy( statservice, service);
	welcome_msg.code = 0;
	welcome_msg.msg = 0;
	welcome_msg.length = 0;

	ear = sk_connect( host, service, name, passwd );
	if( ear < 0 )
	{
		welcome_msg.code = UIF_CONNEXIONERROR;
		welcome_msg.msg = ERR_SOCKET;
		welcome_msg.length = L_ERR_SOCKET;
		if( display_fct != NULL )
			display_fct( &welcome_msg );
		return( ERROR );
	}

	if (TestVersionAppli(ear,display_fct,appli) != OK)
		return(BAD_VERSION) ;

	if( ClientSend( ear, CODSIMBAD ) < 0 )
	{
		welcome_msg.code = UIF_CONNEXIONERROR;
		welcome_msg.msg = ERR_WRITE;
		welcome_msg.length = L_ERR_WRITE;
		if( display_fct != NULL )
			display_fct( &welcome_msg );
		return( ERROR );
	}

	if( ClientGet( ear, &welcome_msg) < 0 )
	{
		welcome_msg.code = UIF_CONNEXIONERROR;
		welcome_msg.msg = ERR_READ;
		welcome_msg.length = L_ERR_READ;
		if( display_fct != NULL )
			display_fct( &welcome_msg );
		return( ERROR );
	}
	strcpy( dirpath, welcome_msg.msg);

	if( ClientGet( ear, &welcome_msg) < 0 )
	{
		welcome_msg.code = UIF_CONNEXIONERROR;
		welcome_msg.msg = ERR_READ;
		welcome_msg.length = L_ERR_READ;
		if( display_fct != NULL )
			display_fct( &welcome_msg );
		return( ERROR );
	}

	if( display_fct != NULL )
		display_fct( &welcome_msg );

	return( ear );
}

/****************************************************************************/
/* FONCTION :                                                               */
/*	int ClientTreat( ear, askstring, display_fct, code )		    */
/* PRAMETRES :                                                              */
/*      -ENTREES :                                                          */
/*		int ear : numero de socket				    */
/*		char *askstring : pointeur sur la requete		    */
/*		void (*display_fct)() : fonction d'affichage 		    */
/*		int code : argument de la fonction display_fct()	    */
/*      -SORTIES :                                                          */
/*		retourne : OK, CONNEXIONERROR, NOEND			    */
/* ROLE :                                                                   */
/*	Cette fonction envoie la requete 'askstring' au serveur et renvoie  */
/* le resultat a travers la fonction 'display_fct()' qui a pour argument    */
/* l'adresse d'une structure du type 'SimDatStruct' (Server.h)   	    */
/*--------------------------------------------------------------------------*/
/* Realisation :                                                            */
/****************************************************************************/

int ClientTreat( ear, askstring, display_fct, code )
	int
		ear;
	char
		*askstring;
	void
		(*display_fct)();
	int
		code;
{
	int
		suite;

	SimDatStruct
		getstruct;

    logExtDbg("ClientTreat()"); 
	if( strcmp( askstring, "" ) == 0 )
	{
		askstring = OKSUITE;
	}

	if( ClientSend( ear, askstring ) < 0 )
	{
		return( CONNEXIONERROR );
	}
	
	suite = ClientGet( ear, &getstruct );

	if( strcmp( getstruct.msg, AGAIN ) == 0 )
	{
		return(ClientTreat( ear, askstring, display_fct, code ));
         /* ??? return rajoute ici sinon aucune valeur n'est retournee lorsqu'on
            passe ici. Ceci se produit lors d'une reponse 'no' a
            'do you want to see more ?' dans une longue liste. Avec plantage
            sur sun si module client.c compile avec -O[2]. DEC ne plante pas,
            mais cela ne doit venir que du fait que c'est par hasard 0 qui est
            retourne. */
         /* essayer de supprimer la recursivite ... ??? */
	}
	else
	{
		display_fct( &getstruct, code );
		return( suite );
	}
}

/****************************************************************************/
/* FONCTION :                                                               */
/*		void ClientClose( ear )					    */
/* PRAMETRES :                                                              */
/*      -ENTREES :                                                          */
/*		int ear: numero de socket				    */
/*      -SORTIES :                                                          */
/* ROLE :                                                                   */
/*	Cette fonction libere le socket ear				    */
/*--------------------------------------------------------------------------*/
/* Realisation :                                                            */
/****************************************************************************/

void ClientClose( ear )
	int
		ear;
{
    logExtDbg("ClientClose()"); 
#if 1
	sk_close( ear );
#else
	shutdown(ear,2);
#endif
}

/***********************************************************************

 NAME CLientSendFile( )

 DESCRIPTION

	Cette fonction permet de lire un fichier dans lequel se trouve
un format et de le transmettre au serveur

 RETURN VALUES
	OK/ERROR

************************************************************************/
int ClientSendFile( display_fct, fullname, name, appli )
	void
		(*display_fct)();
	char
		*fullname,
		*name,
		*appli ;
{
	SimDatStruct
		errormsg;
	int
		ear,
		file;
	char
		newname[256];

    logExtDbg("ClientSendFile()"); 
	errormsg.code = 0;
	errormsg.msg = 0;
	errormsg.length = 0;

	ear = ClientConnect( display_fct, statserveur, statservice, statname, statpasswd );
	if( ear < 0 )
        {
                return( ERROR );
        }

	if (TestVersionAppli(ear,display_fct,appli) != OK)
		return(BAD_VERSION) ;

        if( ClientSend( ear, CODSENDFI ) < 0 )
	{
		errormsg.code = UIF_CONNEXIONERROR;
		errormsg.msg = ERR_WRITE;
		errormsg.length = L_ERR_WRITE;
		display_fct( &errormsg );
		return( ERROR );
	}

	sprintf(newname, "./%s/%s", dirpath, name);
        if( ClientSend( ear, newname ) < 0 )
	{
		errormsg.code = UIF_CONNEXIONERROR;
		errormsg.msg = ERR_WRITE;
		errormsg.length = L_ERR_WRITE;
		display_fct( &errormsg );
		return( ERROR );
	}

	file = open( fullname, 0 );
	if( file < 0 )
	{
		errormsg.code = UIF_OTHERERROR;
		errormsg.msg = ERR_OPEN_FILE;
		errormsg.length = L_ERR_OPEN_FILE;
		display_fct( &errormsg );
		return( ERROR );
	}

	sk_fromserver( ear, 1, file );
	close( file );
        return( OK );
}

/***********************************************************************

 NAME ClientRcvFile( )

 DESCRIPTION

	Cette fonction permet de recevoir n'importe que fichier du serveur

 RETURN VALUES
	OK/ERROR

************************************************************************/
int ClientRcvFile(datatype, display_fct, localfile, distantfile, appli )
	int
		datatype ;
	void
		(*display_fct)();
	char
		*localfile,
		*distantfile,
		*appli ;
{
	SimDatStruct
		errormsg;
	int
		ear,
		file;
	char
		newname[256] ;

    logExtDbg("ClientRcvFile()"); 
	errormsg.code = 0;
	errormsg.msg = 0;
	errormsg.length = 0;

	ear = ClientConnect( display_fct, statserveur, statservice, statname, statpasswd );
	if( ear < 0 )
        {
                return( ERROR );
        }

	if (TestVersionAppli(ear,display_fct,appli) != OK)
		return(BAD_VERSION) ;

        if( ClientSend( ear, CODRCVFI ) < 0 )
	{
		errormsg.code = UIF_CONNEXIONERROR;
		errormsg.msg = ERR_WRITE;
		errormsg.length = L_ERR_WRITE;
		display_fct( &errormsg );
		return( ERROR );
	}

	if (datatype == RCV_FILE)
		strcpy(newname,distantfile) ;
	else if (datatype == RCV_FORMAT)
		sprintf(newname, "../%s.fmt", distantfile);

        if( ClientSend( ear, newname ) < 0 )
	{
		errormsg.code = UIF_CONNEXIONERROR;
		errormsg.msg = ERR_WRITE;
		errormsg.length = L_ERR_WRITE;
		display_fct( &errormsg );
		return( ERROR );
	}

	file = open( localfile, O_CREAT|O_RDWR, 0660 );
	if( file < 0 )
	{
		errormsg.code = UIF_OTHERERROR;
		errormsg.msg = ERR_OPEN_FILE;
		errormsg.length = L_ERR_OPEN_FILE;
		display_fct( &errormsg );
		return( ERROR );
	}

	sk_fromserver( ear, file, 0 );
	close( file );
        return( OK );
}

int TestVersionAppli(ear,display_fct,appli)
    int ear ;
    void (*display_fct)();
    char *appli ;
{
    SimDatStruct
        welcome_msg;
    char
        ctrl_str[256],
    version_ack[8];
    int
        ctrl_str_lg,
    version_ack_lg ;

    logExtDbg("TestVersionAppli()"); 
    /* Envoi "version;appli" et test du numero de version */
    version_ack_lg = sizeof(version_ack) ;
    strcpy(ctrl_str,VERSION) ;
    strcat(ctrl_str,";") ;
    strcat(ctrl_str,appli) ;
    ctrl_str_lg = strlen(ctrl_str)+1 ;  /* +1 for the EOS char !!! */
    if (sk_write(ear, ctrl_str, ctrl_str_lg) != ctrl_str_lg ||
        sk_read(ear,version_ack,version_ack_lg) <= 0 ||
        strcmp(version_ack,"OK") != 0)
    {
        welcome_msg.code = UIF_CONNEXIONERROR;
        welcome_msg.msg = ERR_VERSION ;
        welcome_msg.length = L_ERR_VERSION ;
        if( display_fct != NULL )
            display_fct( &welcome_msg );
        return(BAD_VERSION) ;
    }
    return(OK) ;
}

