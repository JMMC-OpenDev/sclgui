#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <unistd.h>
#include <simcli.h>

#define CURRTIME time((time_t *) NULL)

int main(argc, argv)
int argc;
char **argv;
{
    static char *cmdes[] = {
                            "hd 1",
                            "hd 2",
                            "hd 3",
                            "hd 4",
                            "hd 5"
    };
    char hostname[256], service[8],
            userid[MAX_USERID + 1], passwd[MAX_USERID + 1];
    int hh;
    int t, i;
    time_t time0;

    cliopt(argc, argv, hostname, service, userid, passwd);

    time0 = CURRTIME;
    hh = simbad_connect(hostname, service, userid, passwd);

    if (hh < 0)
    {
        fprintf(stderr, "No connection done.\n");
        exit(1);
    }
    fprintf(stderr, "Main. Connection done: Handle = %d. time: %ld sec.\n",
            hh, CURRTIME - time0);

    time0 = CURRTIME;
    for (t = 0; t < 100; t++)
    {
        for (i = 0; i < sizeof (cmdes) / sizeof (cmdes[0]); i++)
        {
            int nitems;
            nitems = simbad_query(hh, cmdes[i], "");
            fprintf(stderr, ".");

            if (nitems <= 0) continue;
            while (nitems--)
            {
                int stat;
                stat = simbad_retrieve(hh, 0);
            }
        }
        if (t > 0 && (t + 1) % 10 == 0)
            fprintf(stderr, " %5d objects received.\n",
                    (t + 1) * sizeof (cmdes) / sizeof (cmdes[0]));
    }

    time0 = CURRTIME - time0;
    fprintf(stderr, " %5d objects received. Time: %ld sec (%6.2f sec/obj)\n",
            t * sizeof (cmdes) / sizeof (cmdes[0]), time0,
            (double) time0 / ((double) t * sizeof (cmdes) / sizeof (cmdes[0])));

    simbad_disconnect(hh);
    exit(0);
}
