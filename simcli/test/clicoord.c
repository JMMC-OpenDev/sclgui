#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <simcli.h>

int main(argc, argv)
int argc;
char **argv;
{
    char hostname[256], service[8],
            userid[MAX_USERID + 1], passwd[MAX_USERID + 1];
    char rec[256];
    int hh;
    int nitems, ndata;

    cliopt(argc, argv, hostname, service, userid, passwd);
    hh = simbad_connect(hostname, service, userid, passwd);
    if (hh < 0)
    {
        printf("Connection not done: %s.\n", simbad_error(0));
        exit(1);
    }
    printf("Connection done: Handle = %d\n", hh);

    while (1)
    {
        printf("Give an identifier (CR to end): ");
        gets(rec);
        if (rec[0] == '\0') break;
        nitems = simbad_query(hh, rec, "");
        if (nitems >= 1)
        {
            simbad_retrieve(hh, 1); /* retrieve 1st object */
            ndata = simbad_findata(hh, "J", "");
            if (ndata == 1)
                printf("Coord B2000 = %s\n",
                       simbad_getdata(hh, 1));
            else
                printf("No coordinates\n");
        }
        else
            printf("Object not found.\n");
    }
    simbad_disconnect(hh);
    exit(0);
}
