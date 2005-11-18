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
	"hd 2",
	"hd 3",
	"M31",
	"ads 7"
} ;
static char bibyears[80] = "1973/1994" ;

	char hostname[256], service[8],
		userid[MAX_USERID+1], passwd[MAX_USERID+1] ;
	int hh ;
	int i, icmd, nid, nref, lref ;
	char *p, *question;
	char buf[BUFSIZ];
	char obuf[BUFSIZ], option[BUFSIZ] ;
	char mainid[81] ;
	char *tab, *ptab, *etab ;
	char *typlist ;

	cliopt(argc,argv,hostname,service,userid,passwd) ;
	hh = simbad_connect(hostname,service,userid,passwd) ;
	if (hh < 0)
	{
		fprintf(stderr,"No connection done\n") ;
		exit(1) ;
	}
	fprintf(stderr,"Connection done: Handle = %d\n",hh) ;

	sprintf(option,"bibyears=%s",bibyears) ;
	simbad_query(hh,"",option) ;

	for (icmd=0 ; icmd < sizeof(cmdes)/sizeof(cmdes[0]) ; )
	{
		int nitems ; 
		*option = '\0' ;
		printf("identifier [%s]: ", cmdes[icmd]);
		if (!gets(buf)) break;
		if (*buf) 
		{
			question = buf ;
		   printf("  bibyears [%s]: ", bibyears); gets(obuf);
			if (*obuf)
			{
				strcpy(bibyears,obuf) ;
				sprintf(option,"bibyears=%s",bibyears) ;
			}
		} 
		else question = cmdes[icmd++] ;
		nitems = simbad_query(hh,question,option) ;
		fprintf(stderr,"%s: # of objects found: %d\n",question,nitems);
		if (nitems <= 0)
		{
			if (nitems < 0) fprintf(stderr,"Error: %s\n",simbad_error(hh)) ;
			continue ;
		}

		/* loop on all objects */
		while (nitems--)
		{
		   int stat ;
		   char *typ ;

			/* retrieve one object */
		   stat = simbad_retrieve(hh,0) ;
		   fprintf(stderr,"stat(retrieve) = %d\n",stat) ;
		   if (stat <= 0)
		   {
		   	fprintf(stderr,"retrieve error: stat=%d\n", stat) ;
		   	break ;
		   }

			/* retrieve the main identifier */
			nid = simbad_findata(hh,"I.0","") ;
			if (nid == 1) 
			{
				strcpy(mainid,simbad_getdata(hh,0)) ;
				fprintf(stderr,"\n*** id = %s, bibyears = %s\n\n",mainid,bibyears) ;
			}

			/* retrieve the bibliographical references and build a table */
			nref = simbad_findata(hh,"B","") ;
			if (nref == 0)
			{
				fprintf(stderr,"No references for %s\n",mainid) ;
				continue ;
			}
			p = simbad_getdata(hh,0) ;
			lref = strlen(p)+1 ;
			ptab = tab = (char *) calloc(nref,lref) ;
			etab = tab + nref*lref ;
			strcpy(ptab,p) ;
			for (ptab += lref ; ptab < etab ; ptab += lref)
				strcpy(ptab,simbad_getdata(hh,0)) ;

#if 0
			/* display only codes */
			for (ptab = tab ; ptab < etab ; ptab += lref)
				fprintf(stderr,"%s\n",ptab) ;
#endif

			/* display whole reference text */
			for (ptab = tab, i=0 ; ptab < etab ; ptab += lref)
			{
				fprintf(stderr,"=== %4d === %s\n",++i,ptab) ;
				if (simbad_query(hh,ptab,"") != 1 || simbad_retrieve(hh,0) <= 0)
				{
					fprintf(stderr," No reference text found\n") ;
					continue ;
				}
				typlist = simbad_telldata(hh) ;
				for (typ = strtok(typlist," ") ;
					  typ != NULL ;
					  typ = strtok(NULL," "))
				{
					char *reftxt ;
					simbad_findata(hh,typ,"") ;
					reftxt = simbad_getdata(hh,0) ;
					if (strcmp(typ,"R.R") == 0)
					{
						if (strcmp(ptab,reftxt) != 0)
						{
							fprintf(stderr," Other reference text found: %s\n",
									  reftxt) ;
							break ;
						}
					}
					else fprintf(stderr,"%s\n",reftxt) ;
				} 
			}
			free(tab) ;
		} /* end of loop on items in one query */
	} /* end of loop on commands */
	simbad_disconnect(hh) ;
    exit(0);
}
