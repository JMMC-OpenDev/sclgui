; IDL interpolate:

; IDX: 
; O0 - O9 => [00..09]
; B0 - B9 => [10..19]
; A0 - A9 => [20..29]
; F0 - F9 => [30..39]
; G0 - G9 => [40..49]
; K0 - K9 => [50..59]
; M0 - M9 => [60..69]

;# IDX   B-V     V-Ic    V-R     Ic-Jc   Jc-Hc   Jc-Kc   Kc-L     L-M    Mv      M ; "SPTYPE"
I=0
NCOLS=11
NROWS=48
table = DBLARR(NROWS,NCOLS)

; alxColorTableForDwarkStar.cfg
table(I++,*)=[05,	-0.30,	-0.32,	-0.17,	-0.40,	-0.06,	-0.12,	-0.02,	-0.1 ,	-5.70,	60.00 ]; O5	
table(I++,*)=[07,	-0.29,	-0.32,	-0.15,	-0.38,	-0.06,	-0.11,	-0.01,	-0.09,	-5.20,	30.00 ]; O7	
table(I++,*)=[08,	-0.28,	-0.31,	-0.15,	-0.36,	-0.07,	-0.12,	0.00 ,	-0.07,	99.99,	99.99 ]; O8	
table(I++,*)=[09,	-0.28,	-0.30,	-0.15,	-0.30,	-0.09,	-0.14,	0.00 ,	-0.09,	-4.50,	99.99 ]; O9	
table(I++,*)=[10,	-0.26,	-0.28,	-0.14,	-0.29,	-0.09,	-0.14,	-0.01,	-0.08,	99.99,	99.99 ]; B0	
table(I++,*)=[11,	-0.23,	-0.25,	-0.12,	-0.25,	-0.07,	-0.11,	-0.04,	-0.05,	-3.20,	17.50 ]; B1	
table(I++,*)=[12,	-0.21,	-0.22,	-0.10,	-0.25,	-0.02,	-0.08,	-0.03,	-0.05,	99.99,	99.99 ]; B2	
table(I++,*)=[13,	-0.18,	-0.18,	-0.08,	-0.20,	-0.04,	-0.07,	-0.03,	-0.05,	-1.60,	8.00  ]; B3	
table(I++,*)=[14,	-0.16,	-0.15,	-0.07,	-0.18,	-0.03,	-0.07,	-0.02,	-0.04,	99.99,	99.99 ]; B4	
table(I++,*)=[15,	-0.15,	-0.14,	-0.05,	-0.18,	-0.04,	-0.06,	-0.02,	-0.02,	-1.20,	5.90  ]; B5	
table(I++,*)=[16,	-0.14,	-0.13,	-0.04,	-0.16,	-0.02,	-0.05,	0.00 ,	-0.03,	99.99,	99.99 ]; B6	
table(I++,*)=[17,	-0.13,	-0.12,	-0.04,	-0.15,	-0.02,	-0.04,	0.00 ,	-0.02,	-0.60,	99.99 ]; B7	
table(I++,*)=[18,	-0.11,	-0.08,	-0.03,	-0.14,	0.00 ,	-0.02,	0.00 ,	-0.02,	-0.25,	3.80  ]; B8	
table(I++,*)=[19,	-0.07,	-0.05,	-0.01,	-0.08,	0.01 ,	0.00 ,	0.00 ,	-0.01,	99.99,	99.99 ]; B9	
table(I++,*)=[20,	-0.02,	0.00 ,	0.02 ,	-0.05,	0.00 ,	0.00 ,	0.00 ,	0.00 ,	0.65 ,	2.90  ]; A0	
table(I++,*)=[21,	0.01 ,	0.03 ,	0.04 ,	0.00 ,	0.01 ,	0.01 ,	0.00 ,	0.00 ,	99.99,	99.99 ]; A1	
table(I++,*)=[22,	0.05 ,	0.06 ,	0.07 ,	0.05 ,	0.02 ,	0.02 ,	0.01 ,	0.01 ,	1.30 ,	99.99 ]; A2	
table(I++,*)=[24,	0.08 ,	0.20 ,	0.11 ,	0.09 ,	0.05 ,	0.06 ,	0.02 ,	0.02 ,	99.99,	99.99 ]; A4	
table(I++,*)=[25,	0.15 ,	0.27 ,	0.13 ,	0.11 ,	0.06 ,	0.08 ,	0.02 ,	0.03 ,	1.95 ,	2.00  ]; A5	
table(I++,*)=[27,	0.20 ,	0.24 ,	0.20 ,	0.15 ,	0.08 ,	0.10 ,	0.03 ,	0.03 ,	2.20 ,	99.99 ]; A7	
table(I++,*)=[28,	0.25 ,	0.27 ,	0.21 ,	0.18 ,	0.11 ,	0.12 ,	0.03 ,	0.03 ,	99.99,	99.99 ]; A8	
table(I++,*)=[30,	0.30 ,	0.33 ,	0.25 ,	0.24 ,	0.12 ,	0.15 ,	0.03 ,	0.03 ,	2.70 ,	1.60  ]; F0	
table(I++,*)=[32,	0.35 ,	0.40 ,	0.32 ,	0.28 ,	0.15 ,	0.18 ,	0.03 ,	0.03 ,	3.60 ,	99.99 ]; F2	
table(I++,*)=[35,	0.44 ,	0.53 ,	0.39 ,	0.32 ,	0.21 ,	0.25 ,	0.04 ,	0.02 ,	3.50 ,	1.40  ]; F5	
table(I++,*)=[37,	0.48 ,	0.62 ,	0.44 ,	0.37 ,	0.26 ,	0.31 ,	0.04 ,	0.02 ,	4.00 ,	99.99 ]; F7	
table(I++,*)=[38,	0.52 ,	0.64 ,	0.45 ,	0.40 ,	0.27 ,	0.32 ,	0.04 ,	0.02 ,	99.99,	99.99 ]; F8	
table(I++,*)=[40,	0.58 ,	0.66 ,	0.48 ,	0.43 ,	0.28 ,	0.33 ,	0.05 ,	0.01 ,	4.40 ,	1.05  ]; G0	
table(I++,*)=[42,	0.63 ,	0.68 ,	0.52 ,	0.47 ,	0.29 ,	0.34 ,	0.05 ,	0.01 ,	4.70 ,	99.99 ]; G2	
table(I++,*)=[44,	0.66 ,	0.71 ,	0.55 ,	0.48 ,	0.30 ,	0.36 ,	0.05 ,	0.01 ,	5.10 ,	0.92  ]; G4	
table(I++,*)=[45,	0.68 ,	0.73 ,	0.58 ,	0.48 ,	0.32 ,	0.38 ,	0.05 ,	0.00 ,	99.99,	99.99 ]; G5	
table(I++,*)=[46,	0.70 ,	0.75 ,	0.59 ,	0.50 ,	0.34 ,	0.40 ,	0.05 ,	0.00 ,	5.50 ,	99.99 ]; G6	
table(I++,*)=[48,	0.74 ,	0.82 ,	0.63 ,	0.53 ,	0.37 ,	0.45 ,	0.06 ,	0.00 ,	99.99,	99.99 ]; G8	
table(I++,*)=[50,	0.81 ,	0.88 ,	0.69 ,	0.60 ,	0.41 ,	0.49 ,	0.06 ,	-0.01,	5.90 ,	0.79  ]; K0	
table(I++,*)=[51,	0.86 ,	0.93 ,	0.72 ,	0.63 ,	0.43 ,	0.53 ,	0.07 ,	-0.01,	99.99,	99.99 ]; K1	
table(I++,*)=[52,	0.91 ,	0.98 ,	0.73 ,	0.67 ,	0.46 ,	0.55 ,	0.07 ,	-0.02,	6.40 ,	99.99 ]; K2	
table(I++,*)=[53,	0.96 ,	1.07 ,	0.80 ,	0.73 ,	0.50 ,	0.58 ,	0.08 ,	-0.03,	99.99,	99.99 ]; K3	
table(I++,*)=[54,	1.05 ,	1.15 ,	0.87 ,	0.79 ,	0.53 ,	0.62 ,	0.09 ,	-0.04,	7.00 ,	99.99 ]; K4	
table(I++,*)=[55,	1.15 ,	1.22 ,	0.95 ,	0.85 ,	0.56 ,	0.66 ,	0.10 ,	-0.01,	7.35 ,	0.67  ]; K5	
table(I++,*)=[57,	1.33 ,	1.45 ,	1.14 ,	0.90 ,	0.60 ,	0.72 ,	0.11 ,	0.06 ,	8.10 ,	99.99 ]; K7	
table(I++,*)=[60,	1.35 ,	1.65 ,	1.29 ,	0.97 ,	0.68 ,	0.85 ,	0.18 ,	0.10 ,	8.80 ,	0.51  ]; M0	
table(I++,*)=[61,	1.42 ,	1.85 ,	1.33 ,	1.05 ,	0.67 ,	0.86 ,	0.21 ,	0.13 ,	9.30 ,	99.99 ]; M1	
table(I++,*)=[62,	1.50 ,	2.05 ,	1.38 ,	1.14 ,	0.66 ,	0.86 ,	0.25 ,	0.17 ,	9.90 ,	0.40  ]; M2	
table(I++,*)=[63,	1.55 ,	2.40 ,	1.55 ,	1.35 ,	0.62 ,	0.85 ,	0.28 ,	0.20 ,	10.40,	99.99 ]; M3	
table(I++,*)=[64,	1.65 ,	2.80 ,	1.80 ,	1.51 ,	0.61 ,	0.87 ,	0.37 ,	0.30 ,	11.30,	99.99 ]; M4	
table(I++,*)=[65,	1.80 ,	3.27 ,	2.17 ,	1.76 ,	0.58 ,	0.88 ,	0.40 ,	0.35 ,	12.30,	99.99 ]; M5	
table(I++,*)=[66,	1.95 ,	3.75 ,	2.55 ,	2.01 ,	0.56 ,	0.89 ,	0.43 ,	0.40 ,	99.99,	99.99 ]; M6	 
table(I++,*)=[67,	2.10 ,  4.45 ,  2.15 ,  2.44 ,  0.59 ,  0.96 ,  0.50 ,  0.50 ,  99.99,  99.99 ]; M7	 
table(I++,*)=[68,	99.99,  4.60 ,  99.99,  3.04 ,  0.69 ,  1.14 ,  0.65 ,  99.99,  99.99,  99.99 ]; M8	 

PRINT,"NROWS:",NROWS,I
;PRINT,table(0,*)
;PRINT,table(NROWS-1,*)

TRANS=TRANSPOSE(TABLE)

; discard blanking values = NaN
TRANS(WHERE(TRANS EQ 99.99))=!VALUES.F_NAN

X=trans(0,*); IDX
;PRINT,"X: ",X

MIN=MIN(X)
MAX=MAX(X)

PRINT,"MIN: ",MIN, " MAX: ",MAX

; interpolate Y in [MIN..MAX] by 0.25 steps
STEP=0.25
GRID=MIN+FINDGEN((MAX-MIN)/STEP + 1) * STEP
;PRINT,"GRID: ",GRID

NP = N_ELEMENTS(GRID)
PRINT,"NP: ",NP

RESULT = DBLARR(NCOLS,NP)
RESULT(0,*)=GRID


; LOOP on columns
.RUN
FOR II=1, NCOLS-1 DO BEGIN
PRINT,"COL: ",II

Y=trans(II,*); input values
;PRINT,"Y: ",Y

; Interpolate Mv and colors: use /NAN to discard NAN values
;RES = INTERPOL(Y, X, GRID, /NAN, /SPLINE); /SPLINE looks better but may introduce biases
RES = INTERPOL(Y, X, GRID, /NAN); LINEAR interpolation

; indexes where input values are not NAN
Z=WHERE(FINITE(Y))
MIN_X=X(MIN(Z))
MAX_X=X(MAX(Z))

FOR JJ=0, NP-1 DO BEGIN
; discard interpolated values on boundaries
IF (GRID(JJ) LT MIN_X or GRID(JJ) GT MAX_X) THEN RES(JJ)=!VALUES.F_NAN
ENDFOR

PRINT,"MIN X: ",MIN_X," MAX X: ",MAX_X
;PRINT,RES

; Plot the function:
PLOT, X, Y, XRANGE=[MIN-1,MAX+1], XSTYLE=1, psym=-1, THICK=2

; Plot the interpolated values:
OPLOT, GRID, RES, COLOR=64000, psym=-1, thick=1

; copy interpolation results
RESULT(II,*) = RES

WAIT,1

ENDFOR
END


; discard blanking values = 99.99
RESULT(WHERE(FINITE(RESULT) EQ 0))=99.99

OUTPUT=TRANSPOSE(RESULT)

PRINT,"# TC    B-V   V-Ic    V-R   Ic-Jc  Jc-Hc  Jc-Kc  Kc-L    L-M     Mv    M"
FOR II=0, NP-1 DO PRINT,format='(%"%5.2f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f")',OUTPUT(II,*)


