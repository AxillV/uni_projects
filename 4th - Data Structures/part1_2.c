#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <math.h>
#include <sys/time.h>

#include "projectshared.h"

#define PO4uM_PRECISION 2

void heapSort();
void heapSortNoCSV();
void countingSort();
void countingSortNoCSV();
void findRangeOfPO4uMNumbers();

int main(int argc, char** argv) {
        int n;
        struct oceanRecord* recordsIn =  parseCSV(&n);
        struct oceanRecord* recordsOut = malloc(n * sizeof(struct oceanRecord));
        
        for(int repeats=10; repeats<=10*pow(10, MAGNITUDE_RUNS-1); repeats*=10){
            struct timeval st, et;

            // Used to calculate memcpy time (also uncomment correct printf below).
            // gettimeofday(&st,NULL);
            // for(int i=0; i<repeats; i++)memcpy(recordsOut, recordsIn, n * sizeof(struct oceanRecord));
            // gettimeofday(&et,NULL);
            // int copyTime = (((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec))/repeats;

            //Heapsort time calculation
            gettimeofday(&st,NULL);
            for (int i=0; i<repeats; i++) {
                memcpy(recordsOut, recordsIn, n * sizeof(struct oceanRecord));
                heapSortNoCSV(recordsOut, n);
            }
            gettimeofday(&et,NULL);
            int elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
            printf("HeapSort time (%d): %d micro seconds\n",repeats, (elapsed/repeats));
            // printf("HS time (%d): %d micro seconds\n",repeats, (elapsed/repeats) - copyTime);

            //Quicksort time calculation
            gettimeofday(&st,NULL);
            for (int i=0; i<repeats; i++){
                memcpy(recordsOut, recordsIn, n * sizeof(struct oceanRecord));
                countingSortNoCSV(recordsOut, n);
            }
            gettimeofday(&et,NULL);
            elapsed = ((et.tv_sec - st.tv_sec) * 1000000) + (et.tv_usec - st.tv_usec);
            printf("CountingSort time(%d): %d micro seconds\n",repeats, ((elapsed/repeats)));
            //printf("CS time(%d): %d micro seconds\n",repeats, ((elapsed/repeats) - copyTime));
            
        }
        //Create CSV files.
        heapSort(recordsOut, n);
        countingSort(recordsOut, n);
        printf(".csv files created sucessfully...");
}

/*
Input: Pointer to array of records and integer size.
Modifies: oceanRecord array given to represent the sorted array.
Returns: Creates CSV file of sorted dates/PO4uM based on PO4uM + prints runtime.
*/
void heapSort(struct oceanRecord* recordsHeap, int size){
    // Heapsort algorithm, fixed for index 0.
    int n = size;
    int l = (n)/2;
    int r = n - 1;
    int j;
    int k;

    while (r>=1){
        if (l>0){
            l -=1;
            j = l;
        }else{
            struct oceanRecord tempRec = recordsHeap[0];
            recordsHeap[0] = recordsHeap[r];
            recordsHeap[r] = tempRec;
            r -= 1;
            j = 0;
        }

        float s = recordsHeap[j].PO4uM;
        struct oceanRecord sItem = recordsHeap[j];
        while (2*j + 1<=r){
            k = 2*j + 1;
            if (k<r && recordsHeap[k].PO4uM < recordsHeap[k+1].PO4uM){
                k += 1;
            }
            if (s < recordsHeap[k].PO4uM){
                recordsHeap[j] = recordsHeap[k];
                j = k;
            } else break;
        }
        recordsHeap[j] = sItem;
    }

    //WRITE TO FILE
    FILE *fp = fopen("pt1_2_heap.csv", "w");
    fprintf(fp, "Date, PO4uM\n");
    for(int i=0; i<n; i++){
        fprintf(fp, "%s, %.3f\n", recordsHeap[i].date, recordsHeap[i].PO4uM);
    }  
};

/*
Modified version that doesn't create a csv file.
Input: Pointer to array of records and integer size.
Modifies: oceanRecord array given to represent the sorted array.
*/
void heapSortNoCSV(struct oceanRecord* recordsHeap, int size){
    // Heapsort algorithm, fixed for index 0.
    int n = size;
    int l = (n)/2;
    int r = n - 1;
    int j;
    int k;

    while (r>=1){
        if (l>0){
            l -=1;
            j = l;
        }else{
            struct oceanRecord tempRec = recordsHeap[0];
            recordsHeap[0] = recordsHeap[r];
            recordsHeap[r] = tempRec;
            r -= 1;
            j = 0;
        }

        float s = recordsHeap[j].PO4uM;
        struct oceanRecord sItem = recordsHeap[j];
        while (2*j + 1<=r){
            k = 2*j + 1;
            if (k<r && recordsHeap[k].PO4uM < recordsHeap[k+1].PO4uM){
                k += 1;
            }
            if (s < recordsHeap[k].PO4uM){
                recordsHeap[j] = recordsHeap[k];
                j = k;
            } else break;
        }
        recordsHeap[j] = sItem;
    }
};

/*
Input: oceanrecord array pointer, size integer
Modifies: oceanRecord array given to represent the sorted array.
Returns: Creates CSV file of sorted dates/PO4uM based on PO4uM + prints runtime.
*/
void countingSort(struct oceanRecord* records, int size){
    int minRange, maxRange, rangeSize;
    findRangeOfPO4uMNumbers(records, size, &minRange, &maxRange);
    //Initialize table with offset minRange
    rangeSize = maxRange-minRange+1;
    int tableC[rangeSize];
    memset(tableC, 0, sizeof(tableC));
    // UNCOMMENT BELOW IF O(1) INITIALIZATION
    // int sC[rangeSize];
    // int auxC[rangeSize];
    // int topC = -1;
    
    //Phase 1:
    for(int i=0; i<size; i++){
        int j = (int)records[i].PO4uM - minRange; //index offset
        tableC[j] += 1;
    }
    // UNCOMMENT BELOW IF O(1) INITIALIZATION AND COMMENT ABOVE LOOP
    // for(int i=0; i<size; i++){
    //     int j = (int)records[i].PO4uM - minRange; //index offset
    //     if(auxC[j] >= 0 && auxC[j] <= topC && sC[auxC[j]] == j){
    //         tableC[j] += 1;
    //     } else {
    //         topC += 1;
    //         sC[topC] = j;
    //         auxC[j] = topC;
    //         tableC[j] = 1;
    //     }
    // } 

    //Phase 2:
    for (int i=1; i < rangeSize; i++){
        tableC[i] += tableC[i-1];
    }   
    // UNCOMMENT BELOW IF O(1) INITIALIZATION AND COMMENT ABOVE LOOP
    // for (int i=1; i < rangeSize; i++){
    //     if(auxC[i] >= 0 && auxC[i] <= topC && sC[auxC[i]] == i){
    //         tableC[i] += tableC[i-1];
    //     } else tableC[i] = tableC[i-1];
    // }

    //Phase 3:
    struct oceanRecord* recordsSorted = malloc(size * sizeof(struct oceanRecord));
    for (int i = size-1; i>=0; i--){
        int j = records[i].PO4uM - minRange;
        tableC[j]-=1; //subtract first because 0 index instead of 1
        recordsSorted[tableC[j]] = records[i];
        recordsSorted[tableC[j]].PO4uM /= pow(10, PO4uM_PRECISION); //restore original values
    }

    //WRITE TO FILE
    FILE *fp = fopen("pt1_2_counting.csv", "w");
    fprintf(fp, "Date, PO4uM\n");
    for(int i=0; i<size; i++){
        fprintf(fp, "%s, %.3f\n", recordsSorted[i].date, recordsSorted[i].PO4uM);
    }  
}

/*
Modified version that doesn't create a csv file.
Input: oceanrecord array pointer, size integer
Modifies: oceanRecord array given to represent the sorted array.
*/
void countingSortNoCSV(struct oceanRecord* records, int size){
    int minRange, maxRange, rangeSize;
    findRangeOfPO4uMNumbers(records, size, &minRange, &maxRange);
    //Initialize table with offset minRange
    rangeSize = maxRange-minRange+1;
    int tableC[rangeSize];
    memset(tableC, 0, sizeof(tableC));
    // UNCOMMENT BELOW IF O(1) INITIALIZATION
    // int sC[rangeSize];
    // int auxC[rangeSize];
    // int topC = -1;
    
    //Phase 1:
    for(int i=0; i<size; i++){
        int j = (int)records[i].PO4uM - minRange; //index offset
        tableC[j] += 1;
    }
    // UNCOMMENT BELOW IF O(1) INITIALIZATION AND COMMENT ABOVE LOOP
    // for(int i=0; i<size; i++){
    //     int j = (int)records[i].PO4uM - minRange; //index offset
    //     if(auxC[j] >= 0 && auxC[j] <= topC && sC[auxC[j]] == j){
    //         tableC[j] += 1;
    //     } else {
    //         topC += 1;
    //         sC[topC] = j;
    //         auxC[j] = topC;
    //         tableC[j] = 1;
    //     }
    // } 

    //Phase 2:
    for (int i=1; i < rangeSize; i++){
        tableC[i] += tableC[i-1];
    }   
    // UNCOMMENT BELOW IF O(1) INITIALIZATION AND COMMENT ABOVE LOOP
    // for (int i=1; i < rangeSize; i++){
    //     if(auxC[i] >= 0 && auxC[i] <= topC && sC[auxC[i]] == i){
    //         tableC[i] += tableC[i-1];
    //     } else tableC[i] = tableC[i-1];
    // }

    //Phase 3:
    struct oceanRecord* recordsSorted = malloc(size * sizeof(struct oceanRecord));
    for (int i = size-1; i>=0; i--){
        int j = records[i].PO4uM - minRange;
        tableC[j]-=1; //subtract first because 0 index instead of 1
        recordsSorted[tableC[j]] = records[i];
        recordsSorted[tableC[j]].PO4uM /= pow(10, PO4uM_PRECISION); //restore original values
    }
}

/*
Input: Array struct oceanRecord that has, int size and pointers to int holding min and max values.
Modifies: Integer pointer given in input so that they hold the min and max values and oceanRecord array
PO4uM values to be "integer-like" (no decimals).
*/
void findRangeOfPO4uMNumbers(struct oceanRecord* records, int size, int *minRange, int *maxRange) {
    int i=0;
    *minRange = INT_MAX;
    *maxRange = 0;

    while (i<size) {
        records[i].PO4uM = records[i].PO4uM * pow(10, PO4uM_PRECISION);

        if ((int)records[i].PO4uM < *minRange)
            *minRange = (int)records[i].PO4uM;

        if ((int)records[i].PO4uM > *maxRange)
            *maxRange = (int)records[i].PO4uM;

        i++;
    }

}
