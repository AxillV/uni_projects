#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>
#include <sys/time.h>
#include "projectshared.h"
#include <math.h>

#define MAX 50

void insertionSort(struct oceanRecord records[], int size);//function for insertion sort algorithm
void insertionSortNoCSV(struct oceanRecord records[], int size);

void quickSortWrapper(struct oceanRecord records[], int size);//wrapper function for quick sort algorithm
void quickSortWrapperNoCSV(struct oceanRecord records[], int size);
void quickSort(struct oceanRecord records[], int low, int high);
int partition(struct oceanRecord records[], int low, int high);//function to partition array based on pivot
void swap(struct oceanRecord *x, struct oceanRecord *y);//function to swap two elements

int main(int argc, char** argv) {
   int n;
   
   struct oceanRecord* recordsIn = parseCSV(&n);
   struct oceanRecord* recordsOut = malloc(n * sizeof(struct oceanRecord));
   
   for(int repeats=10; repeats<=10*pow(10, MAGNITUDE_RUNS-1); repeats*=10){
      struct timeval st, et;
      gettimeofday(&st,NULL);
      for (int i=0; i<repeats; i++) {
         memcpy(recordsOut, recordsIn, n * sizeof(struct oceanRecord)); 
         insertionSortNoCSV(recordsIn, n);
      }
      gettimeofday(&et,NULL);
      int elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
      printf("Insertion Sort time (%d): %d micro seconds\n",repeats, (elapsed/repeats));
   
      for (int i=0; i<repeats; i++){
         memcpy(recordsOut, recordsIn, n * sizeof(struct oceanRecord));
         quickSortWrapperNoCSV(recordsIn, n);
      }
      gettimeofday(&et,NULL);
      elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
      printf("Quick Sort time(%d): %d micro seconds\n",repeats, ((elapsed/repeats)));
   }
   insertionSort(recordsIn, n);
   quickSortWrapper(recordsIn, n);

   return 0;
}

/**
 * @brief
 * 
 * @param records input array of result structs
 * @param size size of array
 */ 

void insertionSort(struct oceanRecord records[], int size) {

   FILE *fp;
   for(int i=0;i<size;i++){
      records[i].T_degC=records[i].T_degC*100;
   }

   for(int i=1; i<size; i++) {
      float placeholder=records[i].T_degC;
      int j=i-1;

      while (j>=0 && records[j].T_degC > placeholder) {
         //records[j+1].T_degC = records[j].T_degC;
         swap(&records[j+1], &records[j]);
         j=j-1;
      }
      records[j+1].T_degC = placeholder;
   }
   for(int i=0;i<size;i++){
      records[i].T_degC=records[i].T_degC/100;
   }
   fp = fopen("pt1_1_insertion.csv", "w");
   fprintf(fp, "Date, Temp\n");
   for (int i=0; i<size; i++) {
      fprintf(fp, "%s, %.3f\n", records[i].date, records[i].T_degC);
   }
}

void insertionSortNoCSV(struct oceanRecord records[], int size) {

   for(int i=0;i<size;i++){
      records[i].T_degC=records[i].T_degC*100;
   }

   for(int i=1; i<size; i++) {
      float placeholder=records[i].T_degC;
      int j=i-1;

      while (j>=0 && records[j].T_degC > placeholder) {
         //records[j+1].T_degC = records[j].T_degC;
         swap(&records[j+1], &records[j]);
         j=j-1;
      }
      records[j+1].T_degC = placeholder;
   }
   for(int i=0;i<size;i++){
      records[i].T_degC=records[i].T_degC/100;
   }
}

/**
 * @brief
 * 
 * @param records input array of result structs
 * @param size size of array
 */ 
void quickSortWrapper(struct oceanRecord *records, int size) {
   FILE *fp;
   srand(time(NULL));
   for(int i=0; i<size; i++)
      records[i].T_degC=records[i].T_degC*100;
   quickSort(records, 0, size-1);
   for(int i=0; i<size; i++)
      records[i].T_degC=records[i].T_degC/100;
   fp = fopen("pt1_1_quick.csv", "w");
   fprintf(fp, "Date, Temp\n");
   for (int i=0; i<size; i++) {
      fprintf(fp, "%s, %.3f\n", records[i].date, records[i].T_degC);
   }
}

void quickSortWrapperNoCSV(struct oceanRecord *records, int size) {
   srand(time(NULL));
   for(int i=0; i<size; i++)
      records[i].T_degC=records[i].T_degC*100;
   quickSort(records, 0, size-1);
   for(int i=0; i<size; i++)
      records[i].T_degC=records[i].T_degC/100;
}


/**
 * @brief
 * 
 * @param records input array of result structs
 * @param low low limit of array
 * @param high high limit of array
 */ 
void quickSort(struct oceanRecord *records, int low, int high) {
   if(low < high) {
      int pivot = partition(records, low, high);
      quickSort(records, low, pivot-1);
      quickSort(records, pivot+1, high);
   }
}

/**
 * @brief
 * 
 * @param records input array of result structs
 * @param low low limit of array
 * @param high high limit of array
 */ 

int partition(struct oceanRecord records[], int low, int high) {
   //Random number selection of a pivot point for cases where picking the high or low value results in high running time
   int pivot = low+(rand()%(high-low));
   
   if(pivot != high)
   {
      swap(&records[pivot], &records[high]);
   }
    
 
   int pivotValue = records[high].T_degC;

   int i = low;

   for(int j=low; j<high; j++)
   {
      
      if(records[j].T_degC <= pivotValue)
      {
         swap(&records[i], &records[j]);
         i++;
      }
   }
   swap(&records[i], &records[high]);
   return i;
} 

/**
 * @brief
 * 
 * @param x first of two elements to swap
 * @param y second of two elements to swap
 */

void swap(struct oceanRecord *x, struct oceanRecord *y)
{
   struct oceanRecord temp = *x;
   *x = *y;
   *y = temp;
}