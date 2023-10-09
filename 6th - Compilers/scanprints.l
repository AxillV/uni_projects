%{
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include "y.tab.h"
void showError();
int lastLineError=-1;
%}

/* OLDER TODOs BASED ON OTHER MODEL:
 * BEGIN state for attribute reading, when see closing tag escape state 
 * Each attribute rule returns in yylval the attribute type TOKEN (RETURN) and the value of attribute (STRUCT)
 * How will value be found inside token if there is variable text (whitespaces)? Disjuction of att and attvalue needed
 * Nested comment: Parts of <!-- seem to match with smaller rules.
 * %x attributeState 
 */

/* TODO:
 * {} entry doesnt work on [] (t, c and w can not be matched with rest of string), so alphanum was written manually. Please check!
 * Print program (project requirement)
 * Extern or parse yylineno through token for syntax error line
 * Not strndup. Pointer instead. Atoi from second character and after. sscanf(%yytext[1], "%s", yylval.???); 
 * Change <LinearLayout tokens to '<' 'LinearLayout'
 */

%x IN_COMMENT
%option nounput
%option noinput
%option yylineno
/* Does order in definition matter or only in rule? */
ws          [ \t\n]*
allcharacters [a-zA-Z0-9!@#$%^&*()_+=\/?.,<> \t]
alphanum    [a-zA-Z0-9_\- ]+
/* Must trigger first, or else every number is considered alphanum */
number      "-"?[0-9]+
/*TODO: ^ use this instead of number. (change name to integer)*/
/* ASSUMPTION: Alphanumeric is numbers, letters, spaces, _ and - */
/* using 2 consecutive dashes breaks comment formation */

/* Putting comments after rule breaks it. */
/* ~ negation? */

/* VALID FORMATS */
/* apeira whitespace, kai endiamesa apo attribute kai ta values */
/* ASSUMPTION: To element name prepei na einai kollhmeno sto starting tag e.g. <Linear kai den borei na aposunthethei peraiterw */
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
    "-"{allcharacters} /* - must be followed by something else throw error below */
    {allcharacters}+ /* * or + ? */
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
        //number on top of alphanum to be matched first
        //Discard last quotation
        // yytext[yyleng-1] = '\0';
        //Discard first quotation
        // yylval.intLineno.valueInt = atoi(yytext+1);
        yylval.intLineno.valueInt = atoi(yytext);
        yylval.intLineno.lineno = yylineno;
        return VALUEINT;
        }
{alphanum} {
        //Discard last quotation
        // yytext[yyleng-1] = '\0';
        //Discard first quotation
        //yyleng-2 produces same result as yyleng-1. What? due to \0?
        // yylval.stringLineno.valueStr = (char *) strndup(yytext+1, yyleng-2);
        yylval.stringLineno.valueStr = (char *) strndup(yytext, yyleng);
        yylval.stringLineno.lineno = yylineno;
        return VALUESTR;
        }

. {
    showError();
}

[ \t\n]+ {
    ;//TODO: For errors
}

%%
/* Use \n rule to prevent newlines from echoing on output during analysis */

void showError(){
    /*
    TODO: Maybe use %x STATE? 
    When every other debug message is cleared, if lastline equals to yyline
    that means there is unmatched input from the same line, so dont put \n
    and print subsequent unmatched using the if true, and when lastline 
    is not equal to yyline print \n before printing the new error message.
    */
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
