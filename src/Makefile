#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
EXECUTABLES     =
EXECUTABLES_L   = 

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
llllll_OBJECTS   = 

#
# Scripts (public and local)
# --------------------------
SCRIPTS         = sclguiVOTableToHTML
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
JARFILES        = sclgui

#
# <brief description of sclgui jar files>
simbad_DIRS     = cds 

#
# <brief description of sclgui jar files>
sclgui_DIRS     = fr \
                  org \
                  cds \
                  com

sclgui_EXTRAS   = LogbackConfiguration.xml \
                  fr/jmmc/sclgui/resource/* \
                  org/apache/axis/client/*.wsdd

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES = SearchCal \
                   SearchCalDiffTool

#
# other Jar Files
#
JAVA_JAR = 

#
# <brief description of SearchCalibrators java program>
SearchCal_CLASS = fr.jmmc.sclgui.SearchCal

#
# <brief description of SearchCalibrators Diff tool java program>
SearchCalDiffTool_CLASS = fr.jmmc.sclgui.SearchCalDiffTool

#
# Command Definition Files (CDF)
# ---------------------------------
CDF_FILES       =

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
INSTALL_FILES = $(shell ls ../lib/*.jar)

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
all:	do_soap do_all
	@echo " . . . Copying xsl sources :"
	cp -vp fr/jmmc/sclgui/resource/*.xsl ../config
	@echo " . . . 'all' done" 

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

../lib/sclgui-doc.jar:
	jmcsHTML2HelpSet JMMC-MAN-2600-0001
	@echo " . . . user documentation done"

install : ../lib/sclgui-doc.jar install_all
	@echo "......jnlps:"
	jmcsDeployJnlp SearchCal.jnlp    
	jmcsDeployJnlp SearchCalDiffTool.jnlp    
	@echo " . . . installation done"

#
# SOAP targets
# ---------------------------------
CLASSPATH = $(shell mkfMakeJavaClasspath)
WSLD_FILE = $(shell miscLocateFile sclws.wsdl)
do_soap :
	java -classpath $(CLASSPATH) org.apache.axis.wsdl.WSDL2Java $(WSLD_FILE)

#___oOo___
