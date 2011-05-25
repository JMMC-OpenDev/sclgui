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
# Next line was needed in one very old architecture
#EXECUTABLES     = sclguiControl
EXECUTABLES     =
EXECUTABLES_L   = 

#
# <brief description of xxxxx program>
sclguiControl_OBJECTS   = sclguiControl				  		\
                          sclguiCALIBRATOR_LIST_MODEL  		\
                          sclguiREQUEST_MODEL		   		\
                          sclguiFILTER_LIST_MODEL	   		\
                          sclguiBUTTONS_SUBPANEL	   		\
                          sclguiACTIONS_VIEW		   		\
                          sclguiFILTER_VIEW			   		\
                          sclguiDISTANCE_FILTER_VIEW   		\
                          sclguiVISIBILITY_FILTER_VIEW 		\
                          sclguiVARIABILITY_FILTER_VIEW		\
                          sclguiMULTIPLICITY_FILTER_VIEW 	\
                          sclguiLUMINOSITY_FILTER_VIEW		\
                          sclguiMAGNITUDE_FILTER_VIEW		\
                          sclguiSPECTRAL_TYPE_FILTER_VIEW	\
                          sclguiCALIBRATOR_LIST_VIEW   		\
                          sclguiREQUEST_VIEW		   		\
                          sclguiCONFIRM_SUBPANEL	   		\
                          sclguiCONTROLLER			   		\
                          sclguiGetCalCB					\
                          sclguiExitCB
sclguiControl_LDFLAGS   =
sclguiControl_LIBS      = MCS C++ sclsvr vobs alx simcli 

#
# special compilation flags for single c sources
#yyyyy_CFLAGS   = 

#
# Includes (.h) files (public only)
# ---------------------------------
INCLUDES        = sclgui.h 							\
				  sclguiCALIBRATOR_LIST_MODEL.h				\
				  sclguiREQUEST_MODEL.h					\
				  sclguiFILTER_LIST_MODEL.h				\
				  sclguiBUTTONS_SUBPANEL.h				\
				  sclguiACTIONS_VIEW.h			\
				  sclguiFILTER_VIEW.h				\
				  sclguiDISTANCE_FILTER_VIEW.h		\
				  sclguiVISIBILITY_FILTER_VIEW.h	\
				  sclguiMULTIPLICITY_FILTER_VIEW.h	\
				  sclguiLUMINOSITY_FILTER_VIEW.h	\
				  sclguiMAGNITUDE_FILTER_VIEW.h		\
				  sclguiSPECTRAL_TYPE_FILTER_VIEW.h		\
				  sclguiCALIBRATOR_LIST_VIEW.h			\
				  sclguiREQUEST_VIEW.h				\
				  sclguiCONFIRM_SUBPANEL.h			\
				  sclguiCONTROLLER.h					\

#
# Libraries (public and local)
# ----------------------------
LIBRARIES       = 
LIBRARIES_L     =

#
# <brief description of lllll library>
llllll_OBJECTS   =  			   		\

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
## For SIMBAD SOAP WebService (do not work here)
#JARFILES        = simbad sclgui

#
# <brief description of sclgui jar files>
simbad_DIRS     = cds 

#
# <brief description of sclgui jar files>
sclgui_DIRS     = fr \
                  org
sclgui_EXTRAS   = fr/jmmc/scalib/sclgui/Resources.properties \
                  fr/jmmc/scalib/sclgui/*.xsl \
                  fr/jmmc/scalib/sclgui/ApplicationData.xml \
                  org/apache/axis/client/*.wsdd

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES = SearchCalibrators 

#
# other Jar Files
#
JAVA_JAR = 

#
# <brief description of SearchCalibrators java program>
SearchCalibrators_CLASS = fr.jmmc.scalib.sclgui.SearchCalibrators \

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
	cp -vp fr/jmmc/scalib/sclgui/*.xsl ../config
	@echo " . . . 'all' done" 

clean : clean_soap clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

../lib/sclgui-doc.jar:
	jmcsHTML2HelpSet JMMC-MAN-2600-0001
	@echo " . . . user documentation done"

install : ../lib/sclgui-doc.jar install_all
	@echo "......jnlps:"
	jmcsDeployJnlp SearchCal.jnlp    
	@echo " . . . installation done"

#
# SOAP targets
# ---------------------------------
CLASSPATH = $(shell mkfMakeJavaClasspath)
WSLD_FILE = $(shell miscLocateFile sclws.wsdl)
do_soap :
	java -classpath $(CLASSPATH) org.apache.axis.wsdl.WSDL2Java $(WSLD_FILE)

clean_soap :
	rm -rf fr/jmmc/sclws_wsdl


#___oOo___
