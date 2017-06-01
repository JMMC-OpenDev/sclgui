; IDL interpolate:
PRO idl_interpol_teff_logg
; IDX: 
; O0 - O9 => [00..09]
; B0 - B9 => [10..19]
; A0 - A9 => [20..29]
; F0 - F9 => [30..39]
; G0 - G9 => [40..49]
; K0 - K9 => [50..59]
; M0 - M9 => [60..69]
spt=["O","B","A","F","G","K","M"]
;
I=0
NCOLS=10
NROWS=64
table = DBLARR(NROWS,NCOLS)
;            [SP,       Teff_dw, logg_dw, m_dw  ,      etc
; alxTableTeffLogg.cfg
Table(I++,*)=[03,	51230,	-0.229,	120	,	50960,	-0.294,	99.99,	50680,	-0.365,	125.8	];O3;
table(I++,*)=[04,	48670,	-0.272,	84.9	,	48180,	-0.373,	99.99,	47690,	-0.45,	124.4	];O4;
table(I++,*)=[05,	46120,	-0.297,	60	,	45410,	-0.447,	99.99,	44700,	-0.578,	109.2	];O5;
table(I++,*)=[06,	43560,	-0.336,	37	,	42640,	-0.523,	99.99,	41710,	-0.688,	86.9	];O6;
table(I++,*)=[07,	41010,	-0.357,	29.2	,	39860,	-0.596,	20.0,	38720,	-0.801,	72.5	];O7;
table(I++,*)=[08,	38450,	-0.389,	23	,	37090,	-0.678,	99.99,	35730,	-0.922,	58.2	];O8;
table(I++,*)=[09,	35900,	-0.419,	20.1	,	34320,	-0.757,	99.99,	32740,	-1.045,	45.9	];O9;
table(I++,*)=[10,	33340,	-0.446,	17.5	,	31540,	-0.836,	99.99,	26000,	-1.600,	33.8	];B0;
table(I++,*)=[11,	24000,	-0.50,	13.3	,	23000,	-1.000,	7.00,	21000,	-1.700,	29.7	];B1;
table(I++,*)=[12,	20900,	-0.50,	10.0	,	20000,	-1.00,	99.99,	17600,	-1.8,	25.5	];B2;
table(I++,*)=[13,	19000,	-0.50,	7.6	,	17000,	-1.00,	99.99,	15500,	-1.9,	21.4	];B3;
table(I++,*)=[14,	17500,	-0.45,	6.7	,	15500,	-1.00,	99.99,	14500,	-2.0,	18.4	];B4;
table(I++,*)=[15,	15200,	-0.40,	5.9	,	14000,	-0.95,	99.99,	13600,	-2.0,	15.5	];B5;
table(I++,*)=[16,	14000,	-0.40,	5.1	,	13000,	-1.00,	4.0,	12500,	-2.1,	14.2	];B6;
table(I++,*)=[17,	12500,	-0.40,	4.4	,	12000,	-1.00,	99.99,	11700,	-2.2,	12.9	];B7;
table(I++,*)=[18,	11400,	-0.40,	3.8	,	11100,	-1.00,	99.99,	11100,	-2.2,	11.7	];B8;
table(I++,*)=[19,	10500,	-0.35,	3.3	,	10500,	-1.00,	99.99,	10500,	-2.3,	10.5	];B9;
table(I++,*)=[20,	9790,	-0.30,	2.9	,	9980,	-1.00,	99.99,	9980,	-2.3,	9.4	];A0;
table(I++,*)=[21,	9500,	-0.25,	2.7	,	9600,	-1.00,	99.99,	9600,	-2.3,	8.9	];A1;
table(I++,*)=[22,	9000,	-0.20,	2.5	,	9380,	-1.00,	99.99,	9380,	-2.3,	8.5	];A2;
table(I++,*)=[23,	8600,	-0.20,	2.3	,	9000,	-1.00,	99.99,	9000,	-2.4,	8.1	];A3;
table(I++,*)=[24,	8400,	-0.15,	2.2	,	8750,	-1.05,	99.99,	8750,	-2.4,	7.7	];A4;
table(I++,*)=[25,	8180,	-0.15,	2.0	,	8500,	-1.10,	99.99,	8610,	-2.4,	7.2	];A5;
table(I++,*)=[26,	7750,	-0.15,	1.9	,	8250,	-1.10,	99.99,	8250,	-2.5,	7.2	];A6;
table(I++,*)=[27,	7600,	-0.15,	1.8	,	8000,	-1.10,	99.99,	8150,	-2.6,	7.2	];A7;
table(I++,*)=[28,	7500,	-0.10,	1.7	,	7600,	-1.10,	99.99,	7900,	-2.6,	7.2	];A8;
table(I++,*)=[29,	7350,	-0.10,	1.7	,	7400,	-1.20,	99.99,	7600,	-2.7,	7.1	];A9;
table(I++,*)=[30,	7300,	-0.10,	1.6	,	7000,	-1.20,	99.99,	7460,	-2.7,	7.1	];F0;
table(I++,*)=[31,	7150,	-0.10,	1.6	,	6750,	-1.20,	99.99,	7300,	-2.8,	6.8	];F1;
table(I++,*)=[32,	7000,	-0.10,	1.5	,	6500,	-1.25,	99.99,	7030,	-2.9,	6.5	];F2;
table(I++,*)=[33,	6900,	-0.10,	1.5	,	6300,	-1.30,	99.99,	6950,	-2.9,	6.3	];F3;
table(I++,*)=[34,	6800,	-0.10,	1.4	,	6100,	-1.30,	99.99,	6750,	-2.9,	6	];F4;
table(I++,*)=[35,	6650,	-0.10,	1.4	,	6000,	-1.35,	1.0,	6370,	-3.0,	5.7	];F5;
table(I++,*)=[36,	6500,	-0.05,	1.3	,	5900,	-1.40,	99.99,	6250,	-3.0,	5.7	];F6;
table(I++,*)=[37,	6200,	-0.05,	1.2	,	5800,	-1.40,	99.99,	6150,	-3.0,	5.6	];F7;
table(I++,*)=[38,	6250,	-0.05,	1.2	,	5700,	-1.45,	99.99,	5750,	-3.0,	5.6	];F8;
table(I++,*)=[40,	5940,	-0.05,	1.0	,	5600,	-1.50,	1.1,	5370,	-3.1,	2.1	];G0;
table(I++,*)=[41,	5900,	0.00,	1.02	,	5500,	-1.50,	99.99,	5250,	-3.2,	2.2	];G1;
table(I++,*)=[42,	5790,	0.00,	1.00	,	5400,	-1.60,	99.99,	5190,	-3.2,	2.3	];G2;
table(I++,*)=[43,	5700,	0.00,	0.97	,	5250,	-1.70,	99.99,	5100,	-3.2,	2.4	];G3;
table(I++,*)=[44,	5650,	0.00,	0.94	,	5150,	-1.80,	99.99,	4970,	-3.3,	2.5	];G4;
table(I++,*)=[45,	5560,	0.05,	0.92	,	5050,	-1.90,	99.99,	4930,	-3.3,	2.6	];G5;
table(I++,*)=[46,	5500,	0.05,	0.89	,	4950,	-2.00,	1.1,	4800,	-3.3,	2.7	];G6;
table(I++,*)=[47,	5300,	0.05,	0.87	,	4900,	-2.10,	99.99,	4750,	-3.4,	2.8	];G7;
table(I++,*)=[48,	5310,	0.05,	0.84	,	4800,	-2.15,	99.99,	4700,	-3.4,	2.9	];G8;
table(I++,*)=[49,	5250,	0.05,	0.81	,	4700,	-2.20,	99.99,	4600,	-3.5,	3	];G9;
table(I++,*)=[50,	5150,	0.05,	0.79	,	4660,	-2.30,	99.99,	4550,	-3.5,	3.1	];K0;
table(I++,*)=[51,	4800,	0.10,	0.76	,	4500,	-2.30,	1.2,	4400,	-3.6,	3.3	];K1;
table(I++,*)=[52,	4830,	0.10,	0.74	,	4390,	-2.35,	99.99,	4310,	-3.7,	3.4	];K2;
table(I++,*)=[53,	4700,	0.10,	0.72	,	4250,	-2.40,	99.99,	4250,	-3.8,	3.6	];K3;
table(I++,*)=[54,	4550,	0.10,	0.69	,	4150,	-2.50,	1.2,	4100,	-4.0,	3.8	];K4;
table(I++,*)=[55,	4410,	0.10,	0.67	,	4050,	-2.70,	99.99,	3990,	-4.1,	3.9	];K5;
table(I++,*)=[56,	4200,	0.15,	0.63	,	3950,	-2.80,	1.3,	3800,	-4.2,	4.2	];K6;
table(I++,*)=[57,	4000,	0.15,	0.60	,	3850,	-2.95,	99.99,	3700,	-4.3,	4.4	];K7;
table(I++,*)=[60,	3840,	0.15,	0.51	,	3690,	-3.10,	99.99,	3620,	-4.3,	8.2	];M0;
table(I++,*)=[61,	3700,	0.20,	0.45	,	3600,	-3.30,	99.99,	3500,	-4.4,	7.8	];M1;
table(I++,*)=[62,	3520,	0.25,	0.40	,	3540,	-3.55,	99.99,	3370,	-4.5,	7.4	];M2;
table(I++,*)=[63,	3400,	0.30,	0.32	,	3500,	-3.75,	99.99,	3250,	-4.6,	7.1	];M3;
table(I++,*)=[64,	3300,	0.40,	0.26	,	3400,	-4.10,	99.99,	3000,	-4.7,	6.7	];M4;
table(I++,*)=[65,	3170,	0.50,	0.21	,	3380,	-4.50,	99.99,	2880,	-4.8,	6.4	];M5;
table(I++,*)=[66,	2911,	0.812,	0.14	,	3200,	-4.5,	99.99,	99.99,	99.99,	6.3	];M6;
table(I++,*)=[67,	2879,	0.932,	0.09	,	3000,	-4.5,	99.99,	99.99,	99.99,	6.2	];M7;
table(I++,*)=[68,	2806,	1.152,	0.06	,	2750,	-4.5,	99.99,	99.99,	99.99,	6.1	];M8;
table(I++,*)=[69,	2753,	0.722,	0.03	,	99.99,	99.99,	1.0,	99.99,	99.99,	6	];M9;

PRINT,"NROWS:",NROWS,I 
;PRINT,table(0,*)
;PRINT,table(NROWS-1,*)

; discard blanking values = NaN
TABLE(WHERE(abs(TABLE-99.99) lt 0.1 ))=!VALUES.F_NAN
; add logg_sun to logg after nans!
table[*,[2,5,8]]+=4.415  ; JMMC-MEM-2610-0001 says 4.378. I take Mamajek's value 

TRANS=TRANSPOSE(TABLE)


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
;.RUN
FOR II=1, NCOLS-1 DO BEGIN
PRINT,"COL: ",II

Y=trans(II,*); input values
;PRINT,"Y: ",Y

; Interpolate Mv and colors: use /NAN to discard NAN values
RES = INTERPOL(Y, X, GRID, /NAN, /SPLINE); /SPLINE looks better but may introduce biases
;RES = INTERPOL(Y, X, GRID, /NAN); LINEAR interpolation

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
;END


; discard blanking values = 99.99
RESULT(WHERE(FINITE(RESULT) EQ 0))=99.99

OUTPUT=TRANSPOSE(RESULT)
PRINT,format='(%"# TC\tTeff_dw\tlogg_dw\tM_dw\tTeff_gt\tlogg_gt\tM_gt\tTeff_sgt\tlogg-sgt\tM_sgt")'
FOR II=0, NP-1 DO PRINT,format='(%"%4.2f %8.2f %6.2f %6.2f %8.2f %6.2f %5.1f %8.2f %6.2f %5.1f")',OUTPUT(II,0) MOD 10, OUTPUT(II,1:NCOLS-1)


END

