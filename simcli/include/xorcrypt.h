#define XORKEY (unsigned char) 0xdd
#define EXCH(c) (((c & 0x0f)<<4) | ((unsigned char) (c & 0xf0)>>4))
#define XORC(c) (EXCH(c)^XORKEY)
char *XORD(s)
        unsigned char *s ;
{
        unsigned char *p, c;

        for (p=s; p && *p; p++)
        {
                c = *p^XORKEY;
                *p = EXCH(c);
        }

        return((char *) s);
}

#if 0
#define XORKEY 0x57
#define XORC(c) (c^XORKEY)
char *XORD(s)
	unsigned char *s ;
{
	unsigned char *p = s;
	while(*p) *p++ ^= XORKEY ;
	return((char *) s);
}
#endif

