#include <stdio.h>
#include <string.h>
#include <simcli.h>
#include <math.h>
#include <stdlib.h>

int main(argc,argv)
     int argc ; char **argv ;
{
    char  
    userid[MAX_USERID+1]={"t039ga"}, 
    passwd[MAX_USERID+1]={"codeporte"} ;
    char rec[256] ;
    char *p;
    char* TheData;
    int hh ;
    int HH,MM;
    float SS;
    double ss;
    double mv;
    int DH, DM;
    float DS;
    double ds;
    char sign;
    int nitems, ndata,i ;

    hh = simbad_connect(NULL,NULL,userid,passwd) ;
    if (hh < 0)
    {
        exit(1) ;
    }
    if (argc==1) exit(0);
    *rec=0;
    if (argc==2) {
        strcat(rec,argv[1]);
        p=strchr(rec,'_');
        while ( p !=NULL) {*p = ' ';  p=strchr(rec,'_');  }
    }
    else {
        for (i=1; i<argc-1; i++) {strcat(rec,argv[i]); strcat(rec," ");} strcat(rec,argv[i]);
    }
    if (rec[0] == '\0') exit(0);
    strcat(rec,"!") ; 
    nitems = simbad_query(hh,rec,"") ; /* query the object */
    *rec=0;
    for (i=1; i<argc-1; i++) {strcat(rec,argv[i]); strcat(rec,"_");} strcat(rec,argv[i]);   
    if (nitems >= 1)
    {
        simbad_retrieve(hh,0) ;  /* retrieve 1st object */
        ndata = simbad_findata(hh,"J","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            sscanf (strtok(TheData," "), "%lf", &ss);
            sscanf (strtok(NULL," "), "%lf", &ds);
            ss=ss/15.0;
            HH=ss;
            MM=((ss-HH)*60.0);
            SS=(ss-HH)*3600-(MM*60);
            sign=(ds<0.0)?'-':'+'; 
            ds = (ds<0.0)?-ds:ds;
            DH=ds;
            DM=((ds-DH)*60.0);
            DS=(ds-DH)*3600-(DM*60);
        }
        else
        {
            HH= 0;
            MM= 0;
            SS= 0;
            DH= 0;
            DM= 0;
            DS= 0;
            sign='+';
        }
        printf("%s 2000.0 %d:%02d:%07.4f %c%d:%02d:%06.3f ",rec,HH,MM,SS,sign,DH,DM,DS);
        /* 	printf("%s\n",simbad_telldata(hh)); */
        /*  	ndata = simbad_findata(hh,"C","") ; */
        /* 	printf("%d\n",ndata); */
        /* 	for (i=0; i<ndata; i++) { */
        /* 	  TheData=simbad_getdata(hh,1); */
        /* 	  printf("%s\n",TheData); */
        /* 	} */
        /*  	ndata = simbad_findata(hh,"0.JP11","") ; */
        /* 	for (i=0; i<ndata; i++) { */
        /* 	  TheData=simbad_getdata(hh,1); */
        /* 	  printf("%s\n",TheData); */
        /* 	} */
        ndata = simbad_findata(hh,"M.V","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            if (!strstr(TheData, ":")) sscanf (TheData, "%lf", &mv);
            else sscanf (TheData, "%lf:", &mv);
        } else mv=99.99;
        printf("%6.3f ",mv);
        ndata = simbad_findata(hh,"M.R","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            if (!strstr(TheData, ":")) sscanf (TheData, "%lf", &mv);
            else sscanf (TheData, "%lf:", &mv);
        } else mv=99.99;
        printf("%6.3f ",mv);
        ndata = simbad_findata(hh,"M.I","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            if (!strstr(TheData, ":")) sscanf (TheData, "%lf", &mv);
            else sscanf (TheData, "%lf:", &mv);
        } else mv=99.99;
        printf("%6.3f ",mv);
        ndata = simbad_findata(hh,"M.J","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            if (!strstr(TheData, ":")) sscanf (TheData, "%lf", &mv);
            else sscanf (TheData, "%lf:", &mv);
        } else mv=99.99;
        printf("%6.3f ",mv);
        ndata = simbad_findata(hh,"M.H","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            if (!strstr(TheData, ":")) sscanf (TheData, "%lf", &mv);
            else sscanf (TheData, "%lf:", &mv);
        } else mv=99.99;
        printf("%6.3f ",mv);
        ndata = simbad_findata(hh,"M.K","") ;
        if (ndata == 1) {
            TheData=simbad_getdata(hh,1);
            if (!strstr(TheData, ":")) sscanf (TheData, "%lf", &mv);
            else sscanf (TheData, "%lf:", &mv);
        } else mv=99.99;
        printf("%6.3f\n",mv);
    }
    simbad_disconnect(hh) ;
    return (0);
}