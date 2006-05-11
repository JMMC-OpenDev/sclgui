#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <simcli.h>

int main(argc, argv) int argc; char **argv;
{
static char *cmdes[] = { 
	"hd 1",
	"hd 999999",
	"M31",
	"ads 7",
	"12 30 +45 34",
	"1987ApJ...315...28H",
} ;

	char hostname[256], service[8], 
		userid[MAX_USERID+1], passwd[MAX_USERID+1] ;
	int hh ;
	int i , ndata;
	char *p, *question;
	char buf[BUFSIZ];
	char obuf[BUFSIZ], *option;

	cliopt(argc,argv,hostname,service,userid,passwd) ;
printf("h=%s, s=%s, u=%s\n",hostname,service,userid);
	hh = simbad_connect(hostname,service,userid,passwd) ;
	if (hh < 0)
	{
		char *text = simbad_error(hh) ;
		fprintf(stderr,"No connection done: %s\n",text) ;
		exit(1) ;
	}
	fprintf(stderr,"Main. Connection done: Handle = %d\n",hh) ;

	for (i=0 ; i < sizeof(cmdes)/sizeof(cmdes[0]) ; )
	{
		int nitems ; 
		printf("Your request [%s]: ", cmdes[i]);
		if (!gets(buf)) break;
		if (*buf) { question = buf, option = obuf;
		    printf("++Your option: "); gets(obuf);
		} 
		else question = cmdes[i++], option = "";
		nitems = simbad_query(hh,question,option) ;
		fprintf(stderr,"\n====\n") ;
		fprintf(stderr,
		      "Main. # of objects found ('%s'): %d\n",question,nitems);
		fprintf(stderr,"====\n");
		if (nitems <= 0)
		{
			if (nitems < 0)
				fprintf(stderr,"Error: %s\n",simbad_error(hh)) ;
			continue ;
		}
		while (nitems--)
		{
		    int stat ;
		    char *astrotyplist, *typ ;

		    stat = simbad_retrieve(hh,0) ;
		    fprintf(stderr,"stat(retrieve) = %d\n",stat) ;
		    if (stat <= 0)
		    {
		    	fprintf(stderr,"retrieve error: stat=%d\n",
		    		stat) ;
		    	break ;
		    }
		    fprintf(stderr,"===== Available astrotypes: %s\n", 
			astrotyplist = simbad_telldata(hh));
		    for (typ = strtok(astrotyplist," ") ;
			 typ != NULL ;
			 typ = strtok(NULL," "))
		    {
			ndata = simbad_findata(hh,typ,"") ;
			fprintf(stderr,"===== %d data of astrotype %s:\n",
				ndata,typ) ;
			while ((p = simbad_getdata(hh,0)))
				fprintf(stderr,"    %s\n", p);
		    }
		}
	}
	simbad_disconnect(hh) ;
    exit(0);
}

