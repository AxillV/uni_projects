#ifndef EXTRA_H_   /* Include guard */
#define EXTRA_H_

typedef struct stringArray {
    char **array;
    int length;
}stringArray;

void appendArray(stringArray *IDarray, char *strToCopy);
void clearArray(stringArray *IDarray);
int stringInArray(stringArray *IDarray, char *inStr);
int checkValidLayoutStr(char *inStr);
void printErrorIndicator(int lineError, FILE *yyin);
void printRadioChildrenAmountError(int errorLine, int errorExpected, int errorActual, FILE *yyin);
void printNotPositiveIntegerError(int errorLine, int errorInteger, FILE *yyin);
void printInvalidProgressAndMaxError(int errorLine, int errorMax, int errorProgress, FILE *yyin);
void printInvalidLayoutStringError(int errorLine, char *errorString, FILE *yyin);
void printDuplicateIDError(int errorLine, char *idError, FILE *yyin);
void printCheckedIDNotFoundError(int errorLine, char *checkedIDError, FILE *yyin);

#endif // EXTRA_H_
