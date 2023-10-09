#ifndef PROJECT_HEAD
#define PROJECT_HEAD

#define MAGNITUDE_RUNS 6
#define MAXCHAR 128
#define BREAKPOINT ","
#define DATA_FILE_NAME "ocean.csv"

struct oceanRecord {
    char date[11];
    float T_degC;
    float PO4uM;
    float SiO3uM;
    float NO2uM;
    float NO3uM;
    float Salnty;
    float O2ml_L;
};

//For chain hashing (2_3)

/*
Input: Integer pointer to store the size of the return array.
Returns: Pointer to struct oceanRecord array that holds all read information.
Modifies: Integer pointer given in input so that the value it points to is the size of the array/heap.
*/
struct oceanRecord* parseCSV(int* size){
    int buf_size = 1;
    int buf_used = 0;
    
    struct oceanRecord* recordsIn = malloc(buf_size * sizeof(struct oceanRecord));
    struct oceanRecord* recordsTmp = NULL;
    
    FILE *fp;
    char buffer[MAXCHAR];
    char *token;

    fp = fopen(DATA_FILE_NAME, "r");

    //skip first line
    fgets(buffer, MAXCHAR, fp);
    while(fgets(buffer, MAXCHAR, fp)){
        if (buf_used == buf_size) {
            //double the size of allocated memory if ran out
            buf_size *= 2;
            recordsTmp = realloc(recordsIn, buf_size * sizeof(struct oceanRecord));
            recordsIn = recordsTmp;
            //TODO: Add fatal_error https://stackoverflow.com/questions/4352768/how-do-i-declare-an-array-of-undefined-or-no-initial-size
        }

        char *token = strtok(buffer, BREAKPOINT);
        strcpy(recordsIn[buf_used].date, token);
        
        token = strtok(NULL, BREAKPOINT);

        //TODO: loop with enum
        sscanf(token, "%f", &recordsIn[buf_used].T_degC);
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &recordsIn[buf_used].PO4uM);
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &recordsIn[buf_used].SiO3uM);
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &recordsIn[buf_used].NO2uM);
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &recordsIn[buf_used].NO3uM);
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &recordsIn[buf_used].Salnty);
        token = strtok(NULL, BREAKPOINT);
        sscanf(token, "%f", &recordsIn[buf_used].O2ml_L);

        buf_used++;
    }
    //Release unused memory
    recordsIn = realloc(recordsIn, buf_used * sizeof(struct oceanRecord));
    fclose(fp);

    *size = buf_used;
    return recordsIn;
}

#endif