#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <simcli.h>
#include <ctype.h>

char *getpass();

void cliopt(n, v, h, s, u, p)
int n;
char **v;
char *h, *s, *u, *p;
{
    int ip = 1;
    char uu[MAX_USERID + 4];

    *h = '\0';
    *s = '\0';
    *u = '\0';
    *p = '\0';

    if (n > 2 && isalpha(*v[ip]) && isdigit(*v[ip + 1]))
    {
        strcpy(h, v[ip++]);
        n--;
    }

    if (n > 1 && isdigit(*v[ip]))
    {
        strcpy(s, v[ip++]);
        n--;
    }

    if (n > 1)
    {
        strcpy(u, v[ip++]);
        n--;
    }

    if (n > 1)
    {
        strcpy(p, v[ip++]);
        n--;
    }

    if (! *u)
    {
        fprintf(stderr, "simbad client userid: ");
        if (fgets(uu, MAX_USERID + 2, stdin) != NULL)
        {
            uu[strlen(uu) - 1] = '\0';
            strcpy(u, uu);
        }
    }
    if (! *p)
    {
        strcpy(p, getpass("simbad client passwd: "));
    }
}
