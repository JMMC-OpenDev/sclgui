#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.4 2005-02-15 14:50:51 gzins Exp $"
#
# Makefile of ........
#
# History
# -------
# $Log: not supported by cvs2svn $
# sccmgr    16-Nov-2004  Created
#
#*******************************************************************************
# REMARKS
#    Makefile based on the VLT/ALMA Makefile
#
#-------------------------------------------------------------------------------

#
# user definable C-compilation flags
#USER_CFLAGS = 

#
# user definable javac compilation flags
#USER_JFLAGS = 

#
# additional include and library search paths
#USER_INC = 
#USER_LIB = 

#
# MODULE CODE DESCRIPTION:
# ------------------------
# As a general rule:  public file are "cleaned" and "installed"  
#                     local (_L) are not "installed".

#
# C programs (public and local)
# -----------------------------
EXECUTABLES     = simbadclient  \
				  clibib      	\
				  clicoord      \
				  clifct       	\
				  cliname       \
				  clitest       \
				  clitime		\
				  simcliTestGetCoordinates \
EXECUTABLES_L   = 

#
# <brief description of xxxxx program>
simbadclient_OBJECTS   = simbadclient
simbadclient_LDFLAGS   =
simbadclient_LIBS      = mcs log simcli

clibib_OBJECTS   = clibib
clibib_LDFLAGS   =
clibib_LIBS      = mcs log simcli

clicoord_OBJECTS   = clicoord
clicoord_LDFLAGS   =
clicoord_LIBS      = mcs log simcli

clifct_OBJECTS   = clifct
clifct_LDFLAGS   =
clifct_LIBS      = mcs log simcli

cliname_OBJECTS   = cliname
cliname_LDFLAGS   =
cliname_LIBS      = mcs log simcli

clitest_OBJECTS   = clitest
clitest_LDFLAGS   =
clitest_LIBS      = mcs log simcli

clitime_OBJECTS   = clitime
clitime_LDFLAGS   =
clitime_LIBS      = mcs log simcli

simcliTestGetCoordinates_OBJECTS   = simcliTestGetCoordinates
simcliTestGetCoordinates_LDFLAGS   =
simcliTestGetCoordinates_LIBS      = mcs log simcli
#
# special compilation flags for single c sources
#yyyyy_CFLAGS   = 

#
# Includes (.h) files (public only)
# ---------------------------------
INCLUDES        =

#
# Libraries (public and local)
# ----------------------------
LIBRARIES       = 
LIBRARIES_L     =


#
# <brief description of lllll library>
llll_OBJECTS   = 

#
# Scripts (public and local)
# --------------------------
SCRIPTS         =
SCRIPTS_L       =

#
# TCL scripts (public and local)
# ------------------------------
TCL_SCRIPTS     =
TCL_SCRIPTS_L   =

#
# <brief description of tttttt tcl-script>
tttttt_OBJECTS  =
tttttt_TCLSH    = 
tttttt_LIBS     = 

#
# TCL libraries (public and local)
# --------------------------------
TCL_LIBRARIES   =
TCL_LIBRARIES_L =

#
# <brief description of tttlll library>
tttlll_OBJECTS  = 

#
# Python scripts (public and local)
# ---------------------------------
PY_SCRIPTS     =
PY_SCRIPTS_L   =

#
# Jarfiles and their directories
# ---------------------------------
JARFILES        =

#
# <brief description of jjjfff jar files>
jjjfff_DIRS     =
jjjfff_EXTRAS   = 

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES=

#
# <brief description of jjjjjj java program>
jjjjjj_CLASS    =

#
# man pages to be done
# --------------------
MANSECTIONS =
MAN1 =
MAN3 =
MAN5 =
MAN7 =
MAN8 =

#
# other files to be installed
#----------------------------
INSTALL_FILES =

#
# list of all possible C-sources (used to create automatic dependencies)
# ----------------------------------------------------------------------
CSOURCENAMES = \
	$(foreach exe, $(EXECUTABLES) $(EXECUTABLES_L), $($(exe)_OBJECTS)) \
	$(foreach lib, $(LIBRARIES) $(LIBRARIES_L), $($(lib)_OBJECTS))

#
#>>>>> END OF standard rules

#
# INCLUDE STANDARDS
# -----------------
MAKEDIR = $(MCSROOT)/include
include $(MAKEDIR)/mkfMakefile

#
# TARGETS
# -------
all:	do_all
	@echo " . . . 'all' done" 

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all
	@echo " . . . installation done"

db : db_all
	@echo " . . . ../DB done"


#___oOo___
