%{
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include "y.tab.h"
void showError();
int lastLineError=-1;
%}

%x IN_COMMENT
%option nounput
%option noinput
%option yylineno
ws          [ \t\n]*
allcharacters [a-zA-Z0-9!@#$%^&*()_+=\/?.,<> \t]
alphanum    [a-zA-Z0-9_\- ]+
/* Must trigger first, or else every number is considered alphanum */
number      "-"?[0-9]+

/* NOTE: Putting comments after rule breaks it. */

/* VALID FORMATS */
/* apeira whitespace, kai endiamesa apo attribute kai ta values */
/* ASSUMPTION: To element name prepei na einai kollhmeno sto starting tag e.g. <Linear kai den borei na aposunthethei peraiterw */
/* ASSUMPTION: Alphanumeric is numbers, letters, spaces, _ and - */
/* https://codebeautify.org/xmlviewer */
/* ASSUMPTION: Comments are the only thing in the line, never inbetween code */

slinear "<LinearLayout"
elinear "</LinearLayout"{ws}

srelative "<RelativeLayout"
erelative "</RelativeLayout"{ws}

sradio "<RadioGroup"
eradio "</RadioGroup"{ws}

textview "<TextView"
image "<ImageView"
button "<Button"
radiobtn "<RadioButton"
progressbar "<ProgressBar"

width "android:layout_width"
height "android:layout_height"
src "android:src"
padding "android:padding"
text "android:text"
id "android:id"
orientation "android:orientation"
textcolor "android:textColor"
checkedbutton "android:checkedButton"
progress "android:progress"
max "android:max"
childrenAmount "android:childrenAmount"

%%
">"|"="|"/"|"\"" {return yytext[0];}

"<!--" {BEGIN(IN_COMMENT);}
<IN_COMMENT>{
    "-->" {BEGIN(0);}
    "-"{allcharacters}  /* - must be followed by something else throw error below */
    {allcharacters}+
    \n {yylineno++;}
    "-" {showError();}
}


{slinear} {yylval.lineno = yylineno; return S_LINEAR;}
{elinear} {yylval.lineno = yylineno; return E_LINEAR;}

{srelative} {yylval.lineno = yylineno; return S_RELATIVE;}
{erelative} {yylval.lineno = yylineno; return E_RELATIVE;}

{sradio} {yylval.lineno = yylineno; return S_RADIOGRP;}
{eradio} {yylval.lineno = yylineno; return E_RADIOGRP;}

{textview} {yylval.lineno = yylineno; return TEXTVIEW;}
{image} {yylval.lineno = yylineno; return IMAGE;}
{button} {yylval.lineno = yylineno; return BUTTON;}
{progressbar} {yylval.lineno = yylineno; return PROGRESS;}
{radiobtn} {yylval.lineno = yylineno; return RADIOBTN;}

{width} {yylval.lineno = yylineno; return WIDTH;} 
{height} {yylval.lineno = yylineno; return HEIGHT;}
{src} {yylval.lineno = yylineno; return SRC;}
{padding} {yylval.lineno = yylineno; return PADDING;}
{text} {yylval.lineno = yylineno; return TEXT;}
{id} {yylval.lineno = yylineno; return ID;}
{orientation} {yylval.lineno = yylineno; return ORIENTATION;}
{textcolor} {yylval.lineno = yylineno; return COLOR;}
{checkedbutton} {yylval.lineno = yylineno; return CHECKEDBTN;}
{progress} {yylval.lineno = yylineno; return PROGRESSMADE;}
{max} {yylval.lineno = yylineno; return MAX;}
{childrenAmount} {yylval.lineno = yylineno; return CHILDAMOUNT;}

{number} {
        yylval.intLineno.valueInt = atoi(yytext);
        yylval.intLineno.lineno = yylineno;
        return VALUEINT;
        }
{alphanum} {
        yylval.stringLineno.valueStr = (char *) strndup(yytext, yyleng);
        yylval.stringLineno.lineno = yylineno;
        return VALUESTR;
        }

. {
    showError();
}

[ \t\n]+ {/* Eats up whitespace */}

%%

void showError(){
    if (lastLineError == yylineno) {
        ;//printf("%s", yytext);
    } else {
    printf("No lexical rule matched at line %d for %s.\n", yylineno, yytext);
    lastLineError = yylineno;
    }
}
int yywrap(){
    return 1;
}
