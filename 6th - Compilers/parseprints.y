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
%token WIDTH HEIGHT ID ORIENTATION COLOR SRC PADDING MAX PROGRESSMADE CHECKEDBTN TEXT CHILDAMOUNT
%token TEXTVIEW IMAGE BUTTON PROGRESS RADIOBTN
%token S_RADIOGRP E_RADIOGRP
%token E_LINEAR E_RELATIVE S_LINEAR S_RELATIVE

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
			/*TODO: error handling*/
			if(radioChildCount == androidChildAmount){
				printf("Children good\n");
				androidChildAmount=-1;
			}else{
				printf("Children Bad\n");
				androidChildAmount=-1;
			}
			radioChildCount=0; //reset count

			printf("===CHECKEDID=%s===\n", radioCheckedID);
			printf("===CHILD1=%s===\n", radioArray.array[0]);
			printf("===CHILD2=%s===\n", radioArray.array[1]);
			//always atleast 1 element in radioArray cause of bnf
			if(radioCheckedID){
				if(stringInArray(&radioArray, radioCheckedID))
					printf("===GOOD===\n");
				else
					printf("===BAD===\n");
				printf("==FREE NUMERO 1\n");
				free(radioCheckedID);
				radioCheckedID = NULL; //reset pointer
				printf("=====CLEANING ARRAY IF====\n");
				/* TODO: THIS CAUSES THE FREE INVALID POINTER
				 */
				clearArray(&radioArray);
				//TODO:huuuuh&radioArray=NULL; //reset pointer
			} else {
				printf("Checked ID attribute not found!\n");
				printf("=====CLEANING ARRAY ELSE====\n");
				clearArray(&radioArray);
			}

		}
	;
radiobtn:
	radiobtn radiobtn
	| RADIOBTN radiobtnattr '/' '>' 
		{
		radioChildCount++;
		printf("ADDING TO RADIO...");
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
		if($4.valueInt>0) 
			printf("Int is positive %d, all good.\n", $4.valueInt); 
		else 
			fprintf(stderr, "Not positive value %d in line %d\n", $4.valueInt, $4.lineno); 
			/*TODO: error handling*/
		}
	| WIDTH '=' '"' VALUESTR '"'
		{
		if(checkValidLayoutStr($4.valueStr)) 
			printf("Width Good %s\n", $4.valueStr); 
		else 
			fprintf(stderr, "Width Bad\n"); 

		printf("==FREE NUMERO 2\n");
		free($4.valueStr); 
			/*TODO: error handling*/
		}
	;
height:
	HEIGHT '=' '"' VALUEINT '"'
		{
		if($4.valueInt>0) 
			printf("Int is positive %d, all good.\n", $4.valueInt); 
			/*TODO: error handling*/
		}
	| HEIGHT '=' '"' VALUESTR '"'
		{
		if(checkValidLayoutStr($4.valueStr)) 
			printf("Height Good %s\n", $4.valueStr); 
		else 
			printf("Height Bad %s\n", $4.valueStr); 
		printf("==FREE NUMERO 3\n");
		free($4.valueStr); 
			/*TODO: error handling*/
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
			//TODO: Print line number and error message
			if(stringInArray(&idArray, $4.valueStr)){
				printf("String \"%s\" already exists.\n", $4.valueStr);
			}else{
				printf("==ID is \"%s\" .\n", $4.valueStr);
				printf("ADDING TO ID...");

				appendArray(&idArray, $4.valueStr);
			}

			latestIDParsed = idArray.array[idArray.length - 1];
			printf("==FREE NUMERO ID\n");
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
		if($4.valueInt>0) 
			printf("Int is positive %d, all good", $4.valueInt); 
			/*TODO: error handling*/
		}
	;
max:
	/*empty*/
	| MAX '=' '"' VALUEINT '"' {
		androidMax = $4.valueInt;
		if (androidMax<0) {
			//TODO: Error handling
			printf("Unexpected Token. Max value is negative thisnotneeded->or was not set.\n");
		}
		}
	;
progressmade:
	/*empty*/
	| PROGRESSMADE '=' '"' VALUEINT '"' 
		{/*TODO: error handle*/ 
		if(androidMax==-1) /*androidMax attribute not set*/
			printf("max not set\n");
		else if($4.valueInt>=0 && $4.valueInt<=androidMax){
			printf("Progress good %d\n", $4.valueInt); 
			androidMax=-1; /*reset value*/
		}
		else{
			printf("Progress bad %d.\n", $4.valueInt);
			androidMax=-1; /*reset value*/
		}
		}
	;
checkedbtn:
	/*empty*/
	| CHECKEDBTN '=' '"' VALUESTR '"' 
		{
		printf("==CHECKED ID VALUE: %s", $4.valueStr);
		radioCheckedID = strdup($4.valueStr);
		}
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
	return 0;
}

int main(int argc, char **argv){
	#if YYDEBUG == 1
  		yydebug = 1;
	#endif

	++argv; --argc;
    if(argc > 0)
        yyin = fopen(argv[0], "r");
    else
        yyin = stdin;

	printf("Your program:\n");
	int c;
	FILE *file;
	file = fopen(argv[0], "r");
	if (file) {
		int line=1;
		while ((c = getc(file)) != EOF) {
			putchar(c);
			if (c=='\n') {
				printf("line %d:\t", line++); 
			}
		}

		fclose(file);
	}
	printf("=====END=====\n");

	printf("Checking syntax...\n");
    
	yyparse();

	printf("=====END=====\n");
    return 0;
}
