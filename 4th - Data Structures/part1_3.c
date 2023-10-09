#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "projectshared.h"
#include <sys/time.h>

#define BREAKPOINT_UNSORTED ",/"
#define DATA_FILE_NAME_SORTED "pt1_3_insertion.csv"

struct searchRecord {
    int date;
    float T_degC;
    float PO4uM;
};
struct searchRecord* parseCSV_unsorted(int* size){
    int buf_size = 1;
    int buf_used = 0;
    
    struct searchRecord* searchRecordsIn = malloc(buf_size * sizeof(struct searchRecord));
    struct searchRecord* searchRecordsTmp = NULL;
    
    FILE *fp;
    char buffer[MAXCHAR];
    char *token;

    fp = fopen(DATA_FILE_NAME, "r");

    //skip first line
    fgets(buffer, MAXCHAR, fp);
    while(fgets(buffer, MAXCHAR, fp)){
        if (buf_used == buf_size) {
            buf_size *= 2;
            searchRecordsTmp = realloc(searchRecordsIn, buf_size * sizeof(struct searchRecord));
            searchRecordsIn = searchRecordsTmp;
        }

        char *token = strtok(buffer, BREAKPOINT_UNSORTED);
        searchRecordsIn[buf_used].date = 100 * atoi(token);
        
        token = strtok(NULL, BREAKPOINT_UNSORTED);
        searchRecordsIn[buf_used].date += atoi(token);
        
        token = strtok(NULL, BREAKPOINT_UNSORTED);
        searchRecordsIn[buf_used].date += 10000 * atoi(token);
        
        token = strtok(NULL, BREAKPOINT_UNSORTED);
        sscanf(token, "%f", &searchRecordsIn[buf_used].T_degC);

        token =strtok(NULL,BREAKPOINT_UNSORTED);
        sscanf(token,"%f",&searchRecordsIn[buf_used].PO4uM);

        buf_used++;
    }
    //Release unused memory
    searchRecordsIn = realloc(searchRecordsIn, buf_used * sizeof(struct searchRecord));
    fclose(fp);

    *size = buf_used;
    return searchRecordsIn;
}

struct searchRecord* parseCSV_sorted(int* size){
    int buf_size = 1;
    int buf_used = 0;
    
    struct searchRecord* newRecordsIn = malloc(buf_size * sizeof(struct searchRecord));
    struct searchRecord* newRecordsTmp = NULL;
    
    FILE *fp;
    char buffer[MAXCHAR];
    char *token;

    fp = fopen(DATA_FILE_NAME_SORTED, "r");

    //skip first line
    fgets(buffer, MAXCHAR, fp);
    while(fgets(buffer, MAXCHAR, fp)){
        if (buf_used == buf_size) {
            buf_size *= 2;
            newRecordsTmp = realloc(newRecordsIn, buf_size * sizeof(struct searchRecord));
            newRecordsIn = newRecordsTmp;
        }

        char *token = strtok(buffer,BREAKPOINT);
        //token = strtok(NULL,BREAKPOINT);
        sscanf(token, "%d", &newRecordsIn[buf_used].date);
        
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &newRecordsIn[buf_used].T_degC);

        token =strtok(NULL,BREAKPOINT);
        sscanf(token,"%f",&newRecordsIn[buf_used].PO4uM);

        buf_used++;
    }
    //Release unused memory
    newRecordsIn = realloc(newRecordsIn, buf_used * sizeof(struct searchRecord));
    fclose(fp);

    *size = buf_used;
    return newRecordsIn;
}

void swap(struct searchRecord *x, struct searchRecord *y);
void insertionSort(struct searchRecord records[], int size);

int interpolationSearch(struct searchRecord *records,int leftIndex,int rightIndex,int valueToSearch);
int interpolationSearchNoPrint(struct searchRecord records[],int leftIndex,int rightIndex,int valueToSearch);

int binarySearch(struct searchRecord records[],int leftIndex,int rightIndex,int valueToSearch);
int binarySearchNoPrint(struct searchRecord records[],int leftIndex,int rightIndex,int valueToSearch);


int main(int argc, char** argv)
{
    int size;
    int inDate;
    int elapsed;

    struct searchRecord* searchRecordsIn = parseCSV_unsorted(&size);
    struct searchRecord* searchRecordsOut = malloc(size * sizeof(struct searchRecord));
    memcpy(searchRecordsOut,searchRecordsIn,size*sizeof(struct searchRecord));
    insertionSort(searchRecordsIn,size);

    printf("\nEnter date to find in the following format: yyyymmdd: ");
    scanf("%d", &inDate);

    struct searchRecord* newRecordsIn = parseCSV_sorted(&size);
    struct searchRecord* newRecordsOut = malloc(size * sizeof(struct searchRecord));
    
    for(int repeats=10; repeats<=10*pow(10, MAGNITUDE_RUNS-1); repeats*=10){
        struct timeval st,et;
        gettimeofday(&st,NULL);
        for(int i=0; i<repeats; i++) {
            binarySearchNoPrint(newRecordsIn,0,size-1,inDate);
        }
        gettimeofday(&et,NULL);  
        elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
        printf("\033[0;32m");
        printf("Binary search time (%d): %f micro seconds\n",repeats, ((float)elapsed/(float)repeats));
        printf("\033[0m");

        gettimeofday(&st,NULL);
        for (int i=0; i<repeats; i++){
            interpolationSearchNoPrint(newRecordsIn,0,size-1,inDate);
        }
        gettimeofday(&et,NULL);
        elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
        printf("\033[0;36m");
        printf("Interpolation search time (%d): %f micro seconds\n",repeats, ((float)elapsed/(float)repeats));
        printf("\033[0m");
    }

    binarySearch(newRecordsIn,0,size-1,inDate);
    interpolationSearch(newRecordsIn,0,size-1,inDate);

}

void insertionSort(struct searchRecord records[], int size) {

   FILE *fp;

   for(int i=1; i<size; i++) {
      int placeholder=records[i].date;
      int j=i-1;

      while (j>=0 && records[j].date > placeholder) {
         swap(&records[j+1], &records[j]);
         j=j-1;
      }
      records[j+1].date = placeholder;
   }
   fp = fopen("pt1_3_insertion.csv", "w");
   fprintf(fp, "Date, Temp, Phos\n");
   for (int i=0; i<size; i++) {
      fprintf(fp, "%d, %.3f, %.3f\n", records[i].date, records[i].T_degC, records[i].PO4uM);
      fflush(fp);//writes are being buffered and the file isnt completed when the user is asked to enter date
   }
}
/**
 * @brief
 * 
 * @param x first of two elements to swap
 * @param y second of two elements to swap
 */

void swap(struct searchRecord *x, struct searchRecord *y)
{
   struct searchRecord temp = *x;
   *x = *y;
   *y = temp;
}
/**
 * @brief
 * 
 * @param records input array of result structs
 * @param leftIndex left index to search
 * @param rightIndex right index to search
 * @param valueToSearch value to search for (date)
 */
int binarySearch(struct searchRecord *records,int leftIndex,int rightIndex,int valueToSearch)
{
    int mid=leftIndex+(rightIndex-leftIndex)/2;
 
    if(leftIndex>rightIndex) {
        printf("\033[0;32m");
        printf("The date was not found (Binary Search)\n");
        printf("\033[0m");
        return -1;
    }
    
    if(records[mid].date==valueToSearch)
    {
        printf("\033[0;32m");
        printf("The date was found and the levels of temperature and phosphate were : T_degC=%.3f and PO4uM=%.3f.\n",records[mid].T_degC,records[mid].PO4uM);
        printf("\033[0m");
        return 0;
    }
    else if(records[mid].date>valueToSearch)
    {
        return binarySearch(records,leftIndex,mid-1,valueToSearch);
    }
    else 
    {
        return binarySearch(records,mid+1,rightIndex,valueToSearch);
    }
}
//in case magnitude runs method is used in order to be used for time calculation
//without printing
int binarySearchNoPrint(struct searchRecord *records,int leftIndex,int rightIndex,int valueToSearch)
{
    int mid=leftIndex+(rightIndex-leftIndex)/2;
    
    if(leftIndex>rightIndex) {
        return -1;
    }
    
    if(records[mid].date==valueToSearch)
    {
        return 0;
    }
    else if(records[mid].date>valueToSearch)
    {
        return binarySearchNoPrint(records,leftIndex,mid-1,valueToSearch);
    }
    else 
    {
        return binarySearchNoPrint(records,mid+1,rightIndex,valueToSearch);
    }
}

/**
 * @brief
 * 
 * @param records input array of result structs
 * @param leftIndex left index to search
 * @param rightIndex right index to search
 * @param valueToSearch value to search for (date)
 */

int interpolationSearch(struct searchRecord *records,int leftIndex,int rightIndex,int valueToSearch)
{ 
    int key;
    if(leftIndex<=rightIndex&&valueToSearch>=records[leftIndex].date&&valueToSearch<=records[rightIndex].date)
    {
        int key=leftIndex+(((double)(rightIndex-leftIndex)/(records[rightIndex].date-records[leftIndex].date))*(valueToSearch-records[leftIndex].date));
        
        if(records[key].date<valueToSearch)
        {
            return interpolationSearch(records,key+1,rightIndex,valueToSearch);
            
        }
        else if(records[key].date>valueToSearch)
        {
            return interpolationSearch(records,leftIndex,key-1,valueToSearch);
        }
        else
        {
            printf("\033[0;36m");
            printf("The date was found and the levels of temperature and phosphate were : T_degC=%.3f and PO4uM=%.3f.\n",records[key].T_degC,records[key].PO4uM);
            printf("\033[0m");
            return 0;
        }
    }
    printf("\033[0;36m");
    printf("The date was not found (Interpolation Search)\n");
    printf("\033[0m");
    return -1;
       
}

int interpolationSearchNoPrint(struct searchRecord *records,int leftIndex,int rightIndex,int valueToSearch)
{ 
    int key;
    if(leftIndex<=rightIndex&&valueToSearch>=records[leftIndex].date&&valueToSearch<=records[rightIndex].date)
    {
        int key=leftIndex+(((double)(rightIndex-leftIndex)/(records[rightIndex].date-records[leftIndex].date))*(valueToSearch-records[leftIndex].date));
        
        if(records[key].date<valueToSearch)
        {
            interpolationSearchNoPrint(records,key+1,rightIndex,valueToSearch);
        }
        else if(records[key].date>valueToSearch)
        {
            interpolationSearchNoPrint(records,leftIndex,key-1,valueToSearch);
        }
        else
        {
            return 0;
        }
    }
    return -1;
       
}
