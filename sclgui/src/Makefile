#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.7 2005-07-07 05:09:41 gzins Exp $"
#
# Makefile of sclgui module
#
# History
# -------
# $Log: not supported by cvs2svn $
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
EXECUTABLES     = sclguiDisplay
EXECUTABLES_L   = 

#
# <brief description of xxxxx program>
sclguiDisplay_OBJECTS   = sclguiDisplay						\
						  sclguiGetCalCB 					\
						  sclguiDISPLAY 					\
						  sclguiMODEL						\
						  sclguiREQUEST_VIEW				\
						  sclguiRESUME_VIEW					\
						  sclguiTABLE_VIEW					\
						  sclguiFILTER_VIEW					\
						  sclguiVISIBILITY_FILTER_VIEW		\
						  sclguiMAGNITUDE_FILTER_VIEW		\
						  sclguiDISTANCE_FILTER_VIEW		\
						  sclguiLUMINOSITY_FILTER_VIEW		\
						  sclguiSPTYPE_FILTER_VIEW			\
						  sclguiVARIABILITY_FILTER_VIEW		\
						  sclguiMULTIPLICITY_FILTER_VIEW
sclguiDisplay_LDFLAGS   =
sclguiDisplay_LIBS      = MCS C++ sclsvr vobs alx simcli 

#
# special compilation flags for single c sources
#yyyyy_CFLAGS   = 

#
# Includes (.h) files (public only)
# ---------------------------------
INCLUDES        =  sclgui.h 						\
				   sclguiDISPLAY.h					\
				   sclguiMODEL.h					\
				   sclguiREQUEST_VIEW.h				\
				   sclguiRESUME_VIEW.h				\
				   sclguiTABLE_VIEW.h				\
				   sclguiFILTER_VIEW.h				\
				   sclguiVISIBILITY_FILTER_VIEW.h	\
				   sclguiMAGNITUDE_FILTER_VIEW.h	\
				   sclguiDISTANCE_FILTER_VIEW.h		\
				   sclguiLUMINOSITY_FILTER_VIEW.h	\
				   sclguiSPTYPE_FILTER_VIEW.h		\
				   sclguiVARIABILITY_FILTER_VIEW.h	\
				   sclguiMULTIPLICITY_FILTER_VIEW.h
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

#___oOo___
