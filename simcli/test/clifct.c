#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <ctype.h>
#include <simcli.h>
#include <simcliPrivate.h>

int main(argc, argv) 
	int argc;
	char **argv;
{
	char hostname[256], service[8],
		 userid[MAX_USERID+1], passwd[MAX_USERID+1] ;
	int hh ;
	int ndata, nitem, num, ifound ;
	char fctbuf[BUFSIZ], reqbuf[BUFSIZ], optbuf[BUFSIZ] ;
	char *atypes, *cdata ;

	cliopt(argc,argv,hostname,service,userid,passwd) ;
	hh = simbad_connect(hostname,service,userid,passwd) ;
	if (hh < 0)
	{
		fprintf(stderr,"No connection done\n") ;
		exit(1) ;
	}
	fprintf(stderr,"Main. Connection done: Handle = %d\n",hh) ;

	while(1)
	{
		printf("\nFunction: Q[uery],R[etrieve],T[elldata],F[indata],G[etdata],E[rror] : ") ;
		if (!gets(fctbuf)) break ;
		switch(tolower(*fctbuf))
		{
			case 'q':
			printf("request: ") ;
			if (!gets(reqbuf) || *reqbuf == '\0') break ;
			printf("options: ") ;
			if (!gets(optbuf)) break ;
			nitem = simbad_query(hh,reqbuf,optbuf) ;
			printf("%d items found.\n",nitem) ;
			break ;

			case 'r':
			printf("number: ") ;
			if (!gets(reqbuf)) break ;
			num = *reqbuf ? atoi(reqbuf) : 0 ;
			ifound = simbad_retrieve(hh,num) ;
			printf("object #%s retrieved. Remain: %d\n",
				num == 0 ? "next" : reqbuf,ifound) ;
			break ;
			

			case 't':
			atypes = simbad_telldata(hh) ;
			printf("Data types found: %s\n",atypes) ;
			break ;

			case 'f':
			printf("datatype: ") ;
			if (!gets(reqbuf)) break ;
			printf("options: ") ;
			if (!gets(optbuf)) break ;
			ndata = simbad_findata(hh,reqbuf,optbuf) ;
			printf("%d data found.\n",ndata) ;
			break ;

			case 'g':
			printf("number: ") ;
			if (!gets(reqbuf)) break ;
			num = *reqbuf ? atoi(reqbuf) : 0 ;
			cdata = simbad_getdata(hh,num) ;
			printf("data #%s: %s\n",num == 0 ? "next" : reqbuf,cdata) ;
			break ;

			case 'e':
			printf("Error code: %d\n",simbad_errno(hh)) ;
			printf("Error message:\n%s\n",simbad_error(hh)) ;
			break ;

			default:
			printf("Unknown command: %s\n",fctbuf) ;
			break ;
		}
	}
	simbad_disconnect(hh) ;
	printf("\n") ;
    exit(0);
}
