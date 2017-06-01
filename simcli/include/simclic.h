/* client.h 04.03.93 */

#define OK 0
#define ERROR	-1
#define BAD_VERSION  -2
#define	NOEND	-5
#define UNABLEOPEN	-10	/* Unable to open the socket */
#define UIF_CONNEXIONERROR  -11
#define CONNEXIONERROR  -11

/* data types */
#define UIF_DATA 1
#define UIF_HELP 2
#define UIF_CLASS 3
#define UIF_PILE 4
#define UIF_MENU 5
#define UIF_ERROR 6
#define UIF_PROMPT 7
#define UIF_PROGRAM 8

#define UIF_LIST 9
/*
#define UIF_LIST 10
 */
#define UIF_EMPTYLIST 11
#define UIF_OTHERERROR 12

/* Receive types */
#define RCV_FILE      0      /* pas d'interpretation du nom distant */
#define RCV_FORMAT    1      /* interpretation de type format SIMBAD */

/* Messages associes aux erreurs de protocole cote client */
#define ERR_SOCKET	"Impossible to open the socket"
#define ERR_READ	"Impossible to read the socket"
#define ERR_WRITE	"Impossible to write on the socket"
#define ERR_OPEN_FILE 	"Impossible to open the file"
#define ERR_VERSION		"The client version is not accepted by the server"

/* Longueur des messages */
#define L_ERR_SOCKET 30
#define L_ERR_READ 30
#define L_ERR_WRITE 33
#define L_ERR_OPEN_FILE 28
#define L_ERR_VERSION  49

/* Structure utilisee pour transmettre le resultat des requetes */
typedef struct
{
    int code;
    int length;
    char *msg;
} SimDatStruct;
