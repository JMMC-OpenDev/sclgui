#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <simcli.h>

int name_resolver();

int main(argc, argv)
int argc;
char **argv;
{
    static char *cmdes[] = {
                            "hd 1",
                            "hd 999999",
                            "ads 7",
                            "name vega",
                            "name sirius",
                            "hd 8890"
    };
    char hostname[256], service[8], userid[MAX_USERID + 1], passwd[MAX_USERID + 1];
    int hh;
    int i, j, nbid;
    char idlist[100][50];

    cliopt(argc, argv, hostname, service, userid, passwd);
    hh = simbad_connect(hostname, service, userid, passwd);
    if (hh < 0)
    {
        fprintf(stderr, "No connection possible.\n");
        exit(1);
    }
    printf("Connection done: Handle = %d\n", hh);

    for (i = 0; i < sizeof (cmdes) / sizeof (cmdes[0]); i++)
    {
        nbid = name_resolver(hh, cmdes[i], idlist);
        printf("==== object: %s : %d identifiers:\n", cmdes[i], nbid);
        for (j = 0; j < nbid; j++)
            printf("        %d: %s\n", j, idlist[j]);
    }

    simbad_disconnect(hh);
    exit(0);
}

int name_resolver(hh, cmde, idlist)
int hh;
char *cmde;
char idlist[100][50];
{
    char str[256];
    int nitems, ndata;
    int i, stat;
    char *p;

    strcpy(str, cmde);
    strcat(str, "!");

    nitems = simbad_query(hh, str, ""); /* query the object */
    if (nitems != 1) return (0);
    stat = simbad_retrieve(hh, 0); /* retrieve the object */
    ndata = simbad_findata(hh, "I", ""); /* find the identifiers */
    for (i = 0; i < ndata; i++)
    {
        if ((p = simbad_getdata(hh, 0)) != NULL)
            strcpy(idlist[i], p);
    }
    return (ndata);
}
