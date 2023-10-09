#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "extra.h"  /* Include the header (not strictly necessary here) */

/**
 * INPUT: stringArray*, char*
 * MODIFIES: copies strToCopy into stringArray (allocates memory if NULL pointer)
*/
void appendArray(stringArray *IDarray, char *strToCopy) {
    IDarray->array = realloc(IDarray->array, sizeof( *(IDarray->array) ) * (IDarray->length +1));
    IDarray->length++;
    // Duplicate string to the newly assigned position.
    (IDarray->array)[IDarray->length -1] = strdup(strToCopy);}

/**
 * INPUT: stringArray*
 * MODIFIES: clears the array of strings, set length to 0, frees memory
*/
void clearArray(stringArray *IDarray){ 
    for(int i=0; i<IDarray->length; i++) {
        free(IDarray->array[i]);
    }
    
    IDarray->length=0;
}

/**
 * INPUT: stringArray*
 * RETURNS: 1 if string value already exists in array, else 0.
*/
int stringInArray(stringArray *IDarray, char *inStr){
    for(int i=0; i<IDarray->length; i++){
        if(strcmp((IDarray->array)[i], inStr)==0) return 1;
    }
    return 0;
}

/**
 * INPUT: stringArray*
 * RETURNS: 1 if string is valid layout value, else 0.
*/
int checkValidLayoutStr(char *inStr){
    if(strcmp(inStr, "wrap_content") == 0) return 1;
    if(strcmp(inStr, "match_parent") == 0) return 1;
    return 0;
}

/**
 * INPUT: Line of error, pointer to file.
 * PRINTS: Input program with arrow indicating error line
*/
void printErrorIndicator(int lineError, FILE *yyin) {
	
	printf("Your program:\n");
	rewind(yyin);
	int c;

	if (yyin) {
		int line=1;
		printf("%d:\t", line++); 
		while ((c = getc(yyin)) != EOF) {
			if (c=='\n') {
				//Line was incremented before entering this iteration
				if (line-1==lineError) {
					printf("\033[1;31m");
					printf(" <-- Error here.");
					printf("\033[0m");
				}
			}
			putchar(c);
			if (c=='\n') {
				printf("%d:\t", line++); 
			}
		}
	}
	printf("\nEOF\n");
}


/**
 * INPUT: int line where error occured
 * INPUT: int expected amount of children
 * INPUT: int actual amount of children inside group
*/
void printRadioChildrenAmountError(int errorLine, int errorExpected, int errorActual, FILE *yyin){
	printErrorIndicator(errorLine, yyin);
	
	printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("childrenAmount attribute and actual amount not matching\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", errorLine);
	printf("\033[0;36m");
	printf("Expected: ");
	printf("\033[0m");
	printf("%d ", errorExpected);
    printf("\033[0;36m");
	printf("Actual: ");
	printf("\033[0m");
	printf("%d\n", errorActual);
}

/**
 * INPUT: int line where error occured
 * INPUT: int wrong integer value
*/
void printNotPositiveIntegerError(int errorLine, int errorInteger, FILE *yyin){
	printErrorIndicator(errorLine, yyin);
	
    printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("Expected positive integer\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", errorLine);
	printf("\033[0;36m");
	printf("Value: ");
	printf("\033[0m");
	printf("%d\n", errorInteger);
}

/**
 * INPUT: int line where error occured
 * INPUT: int max attribute value
 * INPUT: int progress attribute value
*/
void printInvalidProgressAndMaxError(int errorLine, int errorMax, int errorProgress, FILE *yyin){
	printErrorIndicator(errorLine, yyin);

    printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("Illegal progress attribute value\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", errorLine);
	printf("\033[0;36m");
	printf("Expected: ");
	printf("\033[0m");
	printf(">0 && <=%d ", errorMax);
    printf("\033[0;36m");
	printf("Actual: ");
	printf("\033[0m");
	printf("%d\n", errorProgress);
}

/**
 * INPUT: int line where error occured
 * INPUT: *char wrong string
*/
void printInvalidLayoutStringError(int errorLine, char *errorString, FILE *yyin){
	printErrorIndicator(errorLine, yyin);

    printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("Invalid layout value\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", errorLine);
	printf("\033[0;36m");
	printf("Expected: ");
	printf("\033[0m");
	printf("A valid layout string ");
    printf("\033[0;36m");
	printf("Actual: ");
	printf("\033[0m");
	printf("%s\n", errorString);
}

/**
 * INPUT: int line where error occured
 * INPUT: *char wrong id
*/
void printDuplicateIDError(int errorLine, char *errorID, FILE *yyin){
	printErrorIndicator(errorLine, yyin);

    printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("Duplicate ID attribute\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", errorLine);
	printf("\033[0;36m");
	printf("Value: ");
	printf("\033[0m");
	printf("%s\n", errorID);
}

/**
 * INPUT: int line where error occured
 * INPUT: *char wrong checked id
*/
void printCheckedIDNotFoundError(int errorLine, char *errorCheckedID, FILE *yyin){
	printErrorIndicator(errorLine, yyin);
	
    printf("\033[1;31m");
	printf("Syntax Error\n");
	printf("checkedButton ID not found\n");
	printf("\033[0;36m");
	printf("Line: ");
	printf("\033[0m");
	printf("%d ", errorLine);
	printf("\033[0;36m");
	printf("ID: ");
	printf("\033[0m");
	printf("%s\n", errorCheckedID);
}
