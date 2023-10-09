#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <sys/time.h>
#include <dos.h>
#include "projectshared.h"

#define BREAKPOINT_UNSORTED ",/"
#define DATA_FILE_NAME_SORTED "pt1_3_insertion.csv"

struct searchRecord {
    int date;
    float T_degC;
    float PO4uM;
};

void swap(struct searchRecord* x, struct searchRecord* y);
void insertionSort(struct searchRecord records[], int size);
int binarySearchInterpolation(struct searchRecord *records,int left,int right,int x,int n, int printResult);
int binarySearchInterpolationImproved(struct searchRecord *records,int x,int size, int printResult);
struct searchRecord* parseCSV_unsorted(int* size);
struct searchRecord* parseCSV_sorted(int* size);

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

    printf("\n");

    struct searchRecord* newRecordsIn = parseCSV_sorted(&size);
    struct searchRecord* newRecordsOut = malloc(size * sizeof(struct searchRecord));

    for(int repeats=10; repeats<=10*pow(10, MAGNITUDE_RUNS-1); repeats*=10){
        struct timeval st, et;
        gettimeofday(&st,NULL);
        for (int i=0; i<repeats; i++) {
            binarySearchInterpolation(newRecordsIn,0,size - 1,inDate,size, 0);
        }
        gettimeofday(&et,NULL);
        int elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
        printf("Basic BSI time (%d): %f micro seconds\n",repeats,((float)elapsed/(float)repeats));

        gettimeofday(&st,NULL);
        for (int i=0; i<repeats; i++) {
            binarySearchInterpolationImproved(newRecordsIn,inDate,size, 0);
        }
        gettimeofday(&et,NULL);
        elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
       printf("Improved BSI time (%d): %f micro seconds\n",repeats,((float)elapsed/(float)repeats));
    }  

    binarySearchInterpolation(newRecordsIn,0,size - 1,inDate,size, 1);
    binarySearchInterpolationImproved(newRecordsIn,inDate,size, 1);
}

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
   fp = fopen("pt1_4_insertion.csv", "w");
   fprintf(fp, "Date, Temp, Phos\n");
   for (int i=0; i<size; i++) {
      fprintf(fp, "%d, %.3f, %.3f\n", records[i].date, records[i].T_degC, records[i].PO4uM);
      fflush(fp);//writes are being buffered and the file isnt completed when the user is asked to enter date
   }
}

void swap(struct searchRecord *x, struct searchRecord *y)
{
   struct searchRecord temp = *x;
   *x = *y;
   *y = temp;
}

/**
 * @brief 
 * 
 * @param records* pointer to records struct
 * @param left left index to search from
 * @param right right index to search from
 * @param x date to search for
 * @param n size of array
 * @param printResult 0 to not print results, else prints;
 * @return int 0 if found else -1
 */
int binarySearchInterpolation(struct searchRecord *records,int left,int right,int x,int n, int printResult)
{
    if((left>right)||((left==right)&&records[left].date!=x))
    {
        if(printResult) printf("The date was not found(Basic BSI).\n");
        return -1;
    }
    else if(left==right&&records[left].date==x)
    {
        if(printResult) printf("The date was found and the levels of temperature and phosphate were(Basic BSI): T_degC=%.3f and PO4uM=%.3f.\n",records[left].T_degC,records[left].PO4uM);
        return 0;
    }

    int point=(x-records[left].date)/(records[right].date-records[left].date);
    int mid=left+point*(right-left);

    int next;
    int i=1;

    if(mid<0)mid=0;

    if(x>records[mid].date)
    {
        for(i;;)
        {
            next=mid+(i*ceil(sqrt(n)));
            if((next>right)||(x<records[next].date))
            {
                break;
            }
            if(x==records[next].date)
            {
                if(printResult) printf("The date was found and the levels of temperature and phosphate were(Basic BSI): T_degC=%.3f and PO4uM=%.3f.\n",records[next].T_degC,records[next].PO4uM);
                return 0;
            }
            i++;
        }
        left=mid+(i-1)*(ceil(sqrt(n)))+1; 
        right=fmin(right,next-1);
        n=right-left+1;
        return binarySearchInterpolation(records,left,right,x,n, printResult); 
    }
    else if(x<records[mid].date)
    {
        for(i;;)
        {
            next=mid-(i*ceil(sqrt(n)));
            if((next<left)||(x>records[next].date))
            {
                break;
            }
            if(x==records[next].date)
            {
                if(printResult) printf("The date was found and the levels of temperature and phosphate were(Basic BSI): T_degC=%.3f and PO4uM=%.3f.\n",records[next].T_degC,records[next].PO4uM);
                return 0;
            }
            i++;
        }
        right=mid-(i-1)*(ceil(sqrt(n)));
        left=fmax(left,next+1);
        n=right-left+1;
        return binarySearchInterpolation(records,left,right,x,n, printResult);
    }
    else
    {
        if(printResult) {
            printf("The date was found and the levels of temperature and phosphate were(Basic BSI): T_degC=%.3f and PO4uM=%.3f.\n",records[mid].T_degC,records[mid].PO4uM);
        }
        return 0;
    }
}

/**
 * @brief 
 * 
 * @param records input array of result structs
 * @param leftIndex left index to search
 * @param rightIndex right index to search
 * @param valueToSearch value to search for (date)
 * @param printResult 0 doesn't print results, else prints
 * @return int 0 if found else -1
 */
int binarySearch(struct searchRecord *records,int leftIndex,int rightIndex,int valueToSearch, int printResult)
{
    int mid=leftIndex+(rightIndex-leftIndex)/2;
 
    if(leftIndex>rightIndex) {
        if(printResult) printf("The date was not found(Improved BSI)\n");
        return -1;
    }
    
    if(records[mid].date==valueToSearch)
    {
        if(printResult) printf("The date was found and the levels of temperature and phosphate were(Improved BSI): T_degC=%.3f and PO4uM=%.3f.\n",records[mid].T_degC,records[mid].PO4uM);
        return 0;
    }
    else if(records[mid].date>valueToSearch)
    {
        return binarySearch(records,leftIndex,mid-1,valueToSearch, printResult);
    }
    else 
    {
        return binarySearch(records,mid+1,rightIndex,valueToSearch, printResult);
    }
}

/**
 * @brief 
 * 
 * @param records* pointer to records struct
 * @param left left index to search from
 * @param right right index to search from
 * @param x date to search for
 * @param size size of array
 * @param printResult 0 to not print results, else prints;
 * @return int 0 if found else -1
 */
int binarySearchInterpolationImproved(struct searchRecord *records,int x,int size, int printResult){
    if(x > records[size-1].date || x < records[0].date) {
        if(printResult) printf("The date was not found(Improved BSI)\n");
        return -1;
    }
    int left = 0;
    int right = size - 1;
    int next = ceil(size*(x-records[left].date)/(records[right].date-records[left].date));
    //checks if we the date we searh for is to the left or to the right of the next index
    if (x > records[next].date){
        int i = 0;
        int point = next + ceil(sqrt(size));
        while(x > records[point].date){
            i++;
            int point = next + pow(2, i) * ceil(sqrt(size));
            if (point > size-1) point = size-1;
        }
        int leftIndex = next + pow(2, i-1) * ceil(sqrt(size)) - 1;
        int rightIndex = next + pow(2, i) * ceil(sqrt(size)) + 1;
        if(i==0) leftIndex = next;
        return binarySearch(records, leftIndex, rightIndex, x, printResult);

    } else if (x < records[next].date) {
        int i = 0;
        int point = next - ceil(sqrt(size));
        while(x < records[point].date){
            i++;
            int point = next - pow(2, i) * ceil(sqrt(size));
            if (point > size-1) point = size-1;
        }
        int leftIndex = next - pow(2, i) * ceil(sqrt(size)) - 1;
        int rightIndex = next - pow(2, i-1) * ceil(sqrt(size)) + 1;
        if(i==0) rightIndex = next;
        return binarySearch(records, leftIndex, rightIndex, x, printResult);
    } else {
        //we were lucky and the next index is the correct one.
        if(printResult) printf("The date was found and the levels of temperature and phosphate were(Improved BSI): T_degC=%.3f and PO4uM=%.3f.\n",records[next].T_degC,records[next].PO4uM);
        return 0;
    }
    return -1;

}
