#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.22 2007-05-22 14:20:51 gzins Exp $"
#
# Makefile of sclgui module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.21  2007/03/16 10:07:34  lafrasse
# Added support for instanciation and execution from ASPRO.
#
# Revision 1.20  2007/03/15 15:56:24  lafrasse
# Replaced reference to INTROOT with a call to miscLocateFile to find sclws.wsdl
# in order to ensure installation by th swmgr user that has no INTROOT at all.
#
# Revision 1.19  2007/02/16 13:55:37  lafrasse
# Finished SOAP preprocessing target (Java classpath retireving).
#
# Revision 1.18  2007/02/14 13:50:16  lafrasse
# Added and integrated SOAP targets, plus some cleaning.
#
# Revision 1.17  2007/02/13 13:58:44  lafrasse
# Moved sources from sclgui/src/jmmc into sclgui/src/fr and renamed packages
#
# Revision 1.16  2006/08/22 15:55:35  gzins
# Commented java generation (waiting java program fixes)
#
# Revision 1.15  2006/07/05 11:27:27  mella
# Replace Resources.prop by Resources.properties
#
# Revision 1.14  2006/07/03 13:21:32  mella
# Add Resources.prop to jar file
#
# Revision 1.13  2006/06/30 08:05:28  lafrasse
# Added packaging for the Java query view search icon in the JAR file
#
# Revision 1.12  2006/04/06 15:00:08  mella
# Previous commit was wrong
#
# Revision 1.11  2006/04/06 14:43:18  mella
# *** empty log message ***
#
# Revision 1.10  2006/03/27 11:59:58  lafrasse
# Added new experimental Java GUI
#
# Revision 1.9  2006/02/22 13:28:02  gzins
# Put command callbacks in separated source files
#
# Revision 1.8  2005/10/11 15:24:15  scetre
# New class of MVC second generation added. Removed Obsolete class. Changed Class present in the two versions.
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
EXECUTABLES     = sclguiControl
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
JARFILES        = sclgui

#
# <brief description of sclgui jar files>
sclgui_DIRS     = fr 
sclgui_EXTRAS   = fr/jmmc/scalib/sclgui/Resources.properties \
                  fr/jmmc/scalib/sclgui/search.png

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
SearchCalibrators_CLASS = fr.jmmc.scalib.sclgui.SearchCalibrators 

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
	@echo " . . . 'all' done" 

clean : clean_soap clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all
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
