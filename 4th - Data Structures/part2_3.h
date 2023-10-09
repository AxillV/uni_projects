#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>
#include <time.h>

#include "projectshared.h"

#define HASH_SIZE 13

struct recordNode {
    struct oceanRecord oceanRecord;
    struct recordNode *next;
};

int hashFunction(char *date);
void hashOceanRecords(struct recordNode **hashTable, struct oceanRecord *records, int size);
int searchDate(struct recordNode **hashTable, char *date, struct recordNode **recordPtr, float *temperature);
void searchDateWrapper (struct recordNode **hashTable, char *date, struct recordNode **recordPtr);
int deleteDate(struct recordNode **hashTable, char *date);
int modifyDate(struct recordNode** hashTable, char *date, float temperature);
void printRecord(struct recordNode record);
void freeHashTableMemory(struct recordNode **hashTable);

int part2_3main() {
    int size;
    struct oceanRecord* recordsIn = parseCSV(&size);
    struct recordNode** hashTable = malloc(HASH_SIZE * sizeof(struct recordNode*));
    char date[11] = "01/09/2000";
    float temperature = -10;
    struct recordNode *recordPtr = NULL;

    //(solved) PROBLEM! init pointers to NULL. Otherwise if condition is random
     for (int i=0; i<13; i++)
        hashTable[i] = NULL;

    hashOceanRecords(hashTable, recordsIn, size);

    //MENU
    while (!NULL) {
        int menuSelection;
        char date[11];
        printf("1. See temperature based on date given.\n");
        printf("2. Modify temperature of date given.\n");
        printf("3. Delete record of date given.\n");
        printf("4. Quit\n");

        scanf("%d", &menuSelection);
        fflush(stdin);

        switch (menuSelection) {
        case 1:
            printf("Which date's temperature would you like to see?(mm/dd/yyyy)\n");
            scanf("%s", date);
            fflush(stdin);
            date[10] = '\0';
            searchDateWrapper(hashTable, date, &recordPtr);
            break;
        case 2:
            printf("Which date's temperature would you like to modify?(mm/dd/yyyy)\n");
            scanf("%s", date);
            fflush(stdin);
            printf("What would you like to modify the temperature for %s to?\n", date);
            scanf("%f", &temperature);
            fflush(stdin);
            modifyDate(hashTable, date, temperature);
            break;
        case 3:
            printf("Which date's records would you like to delete?(mm/dd/yyyy)\n");
            scanf("%s", date);
            fflush(stdin);
            deleteDate(hashTable, date);
            break;
        case 4:
            printf("Freeing memory...\n");
            freeHashTableMemory(hashTable);
            printf("Exiting...");
            exit(1);
            break;
        default:
            printf("Something went wrong.\n");
            continue;
        }
    }
    return 0;
}

//hashTable can be used as a pointer (*hashTable) to point first element/row
//printing an array for unknown amount of times makes it overlap to next row

/**
 * @brief Hashing function H(x) = Sum(ASCII)moduloNUMBER_OF_BUCKETS
 * 
 * @param date ASCII codes are used to find index on hash table.
 * @return Index of record in hash table
 */
int hashFunction(char *date) {
    int asciiValue = 0;

    //not using strlen due to time complexity concerns https://stackoverflow.com/a/3213955
    for (int i=0; date[i] != '\0'; i++)
        asciiValue += date[i];

    return asciiValue%HASH_SIZE;
}

//(solved) PROBLEM!!! Passing recordPtr
/**
 * @brief Hashes oceanRecords to hashTable.
 * 
 * @param hashTable Pointer to array of pointers of recordNodes
 * @param records Set of oceanRecords
 * @param size Size of oceanRecords array
 */
void hashOceanRecords(struct recordNode **hashTable, struct oceanRecord *records, int size) {
    for (int i=0; i<size; i++) {
        int hashIndex = hashFunction(records[i].date);

        struct recordNode* newNode = malloc(sizeof(struct recordNode));
        (*newNode).oceanRecord = records[i];

        //if hashIndex is empty (no oceanRecords)
        if (hashTable[hashIndex] == NULL) {
            hashTable[hashIndex] = newNode;
            //Tail of linked list points to NULL. PROBLEM IF NOT!!!
            //FIX: Tail of linked list points to itself, end condition.
            //(*hashTable)[hashIndex].next = hashTable[hashIndex];
            (*newNode).next = newNode;
        }
        //else insert newNode as first element and replace pointer of previous first element
        else {
            (*newNode).next = hashTable[hashIndex];
            hashTable[hashIndex] = newNode;
        }
    }

    printf("Hash table was successfully created.\n");
}

/**
 * @brief Linear search for date.
 * 
 * @param hashTable oceanRecords are converted to recordNodes
 * @param date Date to be searched for
 * @param recordsPtr points to found recordNode (useful for modifyDate)  
 * @param temperature equal to that of the date found
 * @return index of date if date, -1 if date not found
 */
int searchDate(struct recordNode **hashTable, char *date, struct recordNode **recordPtr, float *temperature) {
    int index = 0;
    int hashIndex = hashFunction(date);
    //linearProber points to first
    struct recordNode* linearProber = hashTable[hashIndex];

    while (linearProber != (*linearProber).next) {

        if (strcmp(date, (*linearProber).oceanRecord.date) == 0) {
            *temperature = linearProber->oceanRecord.T_degC;
            //recordPtr points to node that linearProber also points to
            (*recordPtr) = linearProber;
            return index;
        }
        index++;
        linearProber = (*linearProber).next;
    }

    //when reaching the end, check again
    if (strcmp(date, (*linearProber).oceanRecord.date) == 0) {
            *temperature = linearProber->oceanRecord.T_degC;
            //recordPtr points to node that linearProber also points to
            (*recordPtr) = linearProber;
            return index;
    }

    //not found
    index = -1;
    recordPtr = NULL;
    return index;
}

/**
 * @brief Wrapper for searchDate, contains printfs
 * 
 * @param hashTable oceanRecords are converted to recordNodes
 * @param date Date to be searched for
 * @param recordsPtr points to found recordNode (useful for modifyDate)  
 */
void searchDateWrapper (struct recordNode **hashTable, char *date, struct recordNode **recordPtr) {
    float temperature = -10;
    if (searchDate(hashTable, date, recordPtr, &temperature) != -1) {
            printf("Temperature for %s was recorded to be %.2f degrees celsius.\n", date, temperature);
    }

    else {
        printf("Temperature for %s was not recorded.\n", date);
    }
}

/**
 * @brief Deletes a date's records.
 * 
 * @param hashTable oceanRecords are converted to recordNodes
 * @param date Date to be deleted
 * @return 1 if date found, 0 if date not found
 */
int deleteDate(struct recordNode **hashTable, char *date) {
    int hashIndex = hashFunction(date);
    struct recordNode* linearProber = hashTable[hashIndex];

    //find i-1, set .next pointer to i+1, essentially, deleting i.

    //SPECIAL CASE: element to be deleted is first node. hashIndex pointer must point to second element of array
    if (strcmp(date, (*linearProber).oceanRecord.date) == 0) {
        free(hashTable[hashIndex]);
        hashTable[hashIndex] = (*linearProber).next;
        return 1;
    }

    while ((*linearProber).next != linearProber) {
        //DEBUG PRINTFS
        //printf("deleting %s next %s next %s next %s\n", linearProber->oceanRecord.date, linearProber->next->oceanRecord.date, linearProber->next->next->oceanRecord.date, linearProber->next->next->next->oceanRecord.date);
        //printf("deleting %s next %s next %s\n", linearProber->oceanRecord.date, linearProber->next->oceanRecord.date, linearProber->next->next->oceanRecord.date);

        if (strcmp(date, (*linearProber).next->oceanRecord.date) == 0) {
            //element to be deleted is last. Previous pointer becomes tail of linked list.
            if (linearProber->next->next == linearProber->next) {
                free(linearProber->next);
                linearProber->next = linearProber;
                return 1;
            }

            //not last
            else {
                free(linearProber->next);
                linearProber->next = linearProber->next->next;
                return 1;
            }    
        }
        //if .next == itself then date does not exist.
        linearProber = (*linearProber).next;
    }
    return 0;
}

/**
 * @brief Modifies a date's temperature.
 * 
 * @param hashTable oceanRecords are converted to recordNodes
 * @param date Date to be modified
 * @param temperature New temperature of date
 * @return 0 if date not found
 */
int modifyDate(struct recordNode** hashTable, char *date, float temperature) {
    float temperatureDecoy = 0;
    struct recordNode *recordPtr = NULL;
    searchDate(hashTable, date, &recordPtr, &temperatureDecoy);

    if (recordPtr == NULL)
        return 0;

    else
        (*recordPtr).oceanRecord.T_degC = temperature;
}

/**
 * @brief Prints one row of ocean.csv. Unused test function
 */
void printRecord(struct recordNode record) {
    printf("%s ", record.oceanRecord.date);
    printf("%.3f ", record.oceanRecord.T_degC);
    printf("%.3f ", record.oceanRecord.PO4uM);
    printf("%.3f ", record.oceanRecord.SiO3uM);
    printf("%.3f ", record.oceanRecord.NO2uM);
    printf("%.3f ", record.oceanRecord.NO3uM);
    printf("%.3f ", record.oceanRecord.Salnty);
    printf("%.3f ", record.oceanRecord.O2ml_L);
    printf("\n");
}

/**
 * @brief Frees the memory that was allocated for the data structure.
 */
void freeHashTableMemory(struct recordNode **hashTable) {
    for (int i=0; i<HASH_SIZE; i++) {
        free(hashTable[i]);
    }
}