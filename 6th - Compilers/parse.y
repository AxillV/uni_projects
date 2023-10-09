%{
#include <stdio.h>
#include <string.h> //for strcmp
#include "extra.h"

int yylex();
int yyerror(char *s);

extern char *yytext;
extern int yylineno;
extern FILE *yyin;

//https://stackoverflow.com/questions/10310944/expected-asm-or-attribute-before-token
stringArray idArray = {.length = 0}; //holds all ids
char *latestIDParsed; //latest id parsed, pointer inside idArray

char *radioCheckedID; //attribute
stringArray radioArray = {.length =0}; //holds ids of radiobuttons

int androidChildAmount=-1; //attribute parsed
int radioChildCount=0; //radiobuttons parsed

int androidMax=-1; //max attribute
%}

%union{
	int lineno;

	struct intLineno {
		int valueInt;
		int lineno;
	}intLineno;

	struct stringLineno {
		char *valueStr;
		int lineno;
	}stringLineno;
}

%nonassoc '=' '"'
%token <intLineno> VALUEINT <stringLineno> VALUESTR
%token <lineno> WIDTH HEIGHT ID ORIENTATION COLOR SRC PADDING MAX PROGRESSMADE CHECKEDBTN TEXT CHILDAMOUNT
%token <lineno> TEXTVIEW IMAGE BUTTON PROGRESS RADIOBTN
%token <lineno> S_RADIOGRP E_RADIOGRP
%token <lineno> E_LINEAR E_RELATIVE S_LINEAR S_RELATIVE

%%
root:
	linear
	| relative
	;
linear:
	S_LINEAR linearattr '>' element E_LINEAR '>'
	;
relative:
	S_RELATIVE relativeattr '>' element E_RELATIVE '>'
	| S_RELATIVE relativeattr '>' E_RELATIVE '>'
element:
	element element
	| linear
	| relative
	| textview
	| image
	| button
	| radiogrp
	| progress
	;
textview:
	TEXTVIEW textattr '/' '>'
	;
image:
	IMAGE imageattr '/' '>'
	;
button:
	BUTTON buttonattr '/' '>'
	;
radiogrp:
	S_RADIOGRP radiogrpattr '>' radiobtn E_RADIOGRP '>'
		{
			if(radioChildCount != androidChildAmount){
				printRadioChildrenAmountError($1, androidChildAmount, radioChildCount, yyin);
				YYABORT;
			}

			androidChildAmount=-1;
			radioChildCount=0; //reset count

			//always atleast 1 element in radioArray cause of bnf
			if(radioCheckedID){
				if(!stringInArray(&radioArray, radioCheckedID)){
					printCheckedIDNotFoundError($1, radioCheckedID, yyin);
					YYABORT;
				}
					
				free(radioCheckedID);
				radioCheckedID = NULL; //reset pointer
			} //else checkedButton attribute not found
			clearArray(&radioArray);
		}
	;
radiobtn:
	radiobtn radiobtn
	| RADIOBTN radiobtnattr '/' '>' 
		{
		radioChildCount++;
		appendArray(&radioArray, latestIDParsed);
		}
	;
progress:
	PROGRESS progressattr '/' '>'
	;
linearattr:
	width height id orientation 
	;
relativeattr:
	width height id
	;
textattr:
	width height id text color
	;
imageattr:
	width height id src padding
	;
buttonattr:
	width height id text padding
	;
radiogrpattr:
	width height id childamount checkedbtn
		{
			// Reset latest ID pointer so first id is a radiobutton's id or else NULL
			latestIDParsed = NULL;
		}
	;
radiobtnattr:
	width height id text
	;
progressattr:
	width height id max progressmade

width:
	WIDTH '=' '"' VALUEINT '"'
		{
		if(!($4.valueInt>0)){
			printNotPositiveIntegerError(yylineno, $4.valueInt, yyin);
			YYABORT;
		}
		}
	| WIDTH '=' '"' VALUESTR '"'
		{
		if(!checkValidLayoutStr($4.valueStr)){
			printInvalidLayoutStringError(yylineno, $4.valueStr, yyin);
			YYABORT;
		}
						
		free($4.valueStr); 
		}
	;
height:
	HEIGHT '=' '"' VALUEINT '"'
		{
		if(!($4.valueInt>0)){
			printNotPositiveIntegerError(yylineno, $4.valueInt, yyin);
			YYABORT;
		}
		}
	| HEIGHT '=' '"' VALUESTR '"'
		{
		if(!checkValidLayoutStr($4.valueStr)){
			printInvalidLayoutStringError(yylineno, $4.valueStr, yyin);
			YYABORT;
		}
		free($4.valueStr); 
		}
	;
text:
	TEXT '=' '"' VALUESTR '"' {free($4.valueStr);}
	;
src:
	SRC '=' '"' VALUESTR '"' {free($4.valueStr);}
	;
childamount:
	CHILDAMOUNT '=' '"' VALUEINT '"' {androidChildAmount = $4.valueInt;}
	;
id:
	/*empty*/
	| ID '=' '"' VALUESTR '"'
		{
		if(stringInArray(&idArray, $4.valueStr)){
			printDuplicateIDError(yylineno, $4.valueStr, yyin);
			YYABORT;
		}

		//else
		appendArray(&idArray, $4.valueStr);
		latestIDParsed = idArray.array[idArray.length - 1];
		free($4.valueStr); //free memory allocated from flex.
		}
	;
orientation:
	/*empty*/
	| ORIENTATION '=' '"' VALUESTR '"' {free($4.valueStr);}
	;
color:
	/*empty*/
	| COLOR '=' '"' VALUESTR '"' {free($4.valueStr);}
	;
padding:
	/*empty*/
	| PADDING '=' '"' VALUEINT '"'
		{
		if(!($4.valueInt>0)){
			printNotPositiveIntegerError(yylineno, $4.valueInt, yyin);
			YYABORT;
		}
		}
	;
max:
	/*empty*/
	| MAX '=' '"' VALUEINT '"' 
		{
		androidMax = $4.valueInt;
		if (!(androidMax>0)) {
			printNotPositiveIntegerError(yylineno, $4.valueInt, yyin);
			YYABORT;
		}
		}
	;
progressmade:
	/*empty*/
	| PROGRESSMADE '=' '"' VALUEINT '"' 
		{
		if(!($4.valueInt>0)){
			printNotPositiveIntegerError(yylineno, $4.valueInt, yyin);
			YYABORT;
		}

		/* By design, if androidMax is different from -1 it means that a
		valid value has been assigned to it. (from max rule)*/
		if(androidMax!=-1)
			if(!($4.valueInt>=0 && $4.valueInt<=androidMax)){
				printInvalidProgressAndMaxError(yylineno, androidMax, $4.valueInt, yyin);
				YYABORT;
			}
		androidMax=-1; //Reset value
		}
	;
checkedbtn:
	/*empty*/
	| CHECKEDBTN '=' '"' VALUESTR '"' { radioCheckedID = strdup($4.valueStr); }
	;
%%

int yyerror(char *s)
{
	printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("Unexpected Token\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", yylineno);
	printf("\033[0;36m");
	printf("Text: ");
	printf("\033[0m");
	printf("%s\n", yytext);

	printErrorIndicator(yylineno,yyin);
	return 0;
}

int main(int argc, char **argv){
	#if YYDEBUG == 1
  		yydebug = 1;
	#endif

	++argv; --argc;
    if(argc == 1)
        yyin = fopen(argv[0], "r");
    else {
		printf("\033[1;31m");
		printf("Incorrect amount of inputs given.\n");
		printf("\033[0;36m");
		return 0;
	}

	printf("Checking syntax...\n");
    
	if (!yyparse()) {
		printErrorIndicator(-1,yyin);
	}

	printf("=====END=====\n");
    return 0;
}
