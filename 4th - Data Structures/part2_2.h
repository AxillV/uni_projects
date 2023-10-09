#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include "part2shared.h"

#define MAX_DUPLICATES 10

//TODO: dates=array, keep track of amount of items
struct AvlNodeTemp{
    int date[MAX_DUPLICATES];
    int datesCount;
    float T_degC;
    struct AvlNodeTemp* leftChild;
    struct AvlNodeTemp* rightChild;
    int height;
};

struct AvlNodeTemp* newNodeTemp(int, float);
struct AvlNodeTemp* insertNodeTemp(struct AvlNodeTemp*, int, float);
struct AvlNodeTemp* leftRotateTemp (struct AvlNodeTemp*);
struct AvlNodeTemp* rightRotateTemp (struct AvlNodeTemp*);
struct AvlNodeTemp* minTemperature (struct AvlNodeTemp*);
struct AvlNodeTemp* maxTemperature (struct AvlNodeTemp*);
void inOrderTreeTemp(struct AvlNodeTemp*);
    
int part2_2main() {
    FILE *fp;
    char buffer[MAXCHAR];
    char *token;

    struct AvlNodeTemp *root = NULL;
    fp = fopen(DATA_FILE_NAME, "r");

    //skip first line
    fgets(buffer, MAXCHAR, fp);
    while(fgets(buffer, MAXCHAR, fp)){
        //convert string(m/d/y) to int (ymd)
        char *token = strtok(buffer, AVL_BREAKPOINT);
        int dateInt = 100 * atoi(token);
        
        token = strtok(NULL, AVL_BREAKPOINT);
        dateInt += atoi(token);
        
        token = strtok(NULL, AVL_BREAKPOINT);
        dateInt += 10000 * atoi(token);
        
        token = strtok(NULL, AVL_BREAKPOINT);
        float T_degCFloat;
        sscanf(token, "%f", &T_degCFloat);

        root = insertNodeTemp(root, dateInt, T_degCFloat);
        
    }
    fclose(fp);
    printf("AVL tree was sucessfuly created.\n");

    //TODO: Need to find all dates with same max/min
    //MAIN LOOP
    while (!NULL){
        struct AvlNodeTemp* searchNode;
        int menuSelection;
        int resultDate;
        //char term;
        printf("1.Find date(s) with lowest temperature.\n2.Find date(s) with highest temperature.\n3.Quit. ");
        scanf("%d", &menuSelection);
        switch (menuSelection)
        {
            //TODO: multiple dates
        case 1:
            searchNode = minTemperature(root);
            printf("===================\n");
            for (int i = 0; i < searchNode->datesCount; i++){   
                resultDate = searchNode->date[i];
                printf("%d/%d/%d: %.2f\n", resultDate/10000, (resultDate%10000)/100, resultDate%100, searchNode->T_degC);
            }
            printf("===================\n");
            break;
        case 2:
            searchNode = maxTemperature(root);
            printf("===================\n");
            for (int i = 0; i < searchNode->datesCount; i++){   
                resultDate = searchNode->date[i];
                printf("%d/%d/%d: %.2f\n", resultDate/10000, (resultDate%10000)/100, resultDate%100, searchNode->T_degC);
            }
            printf("===================\n");
            break;
        case 3:
            printf("Exiting...\n");
            exit(1);
            break;
        default:
            printf("Something went wrong.\n");
            continue;
        }
    }
    return 0;
}

/**
 * @brief Not to be used directly in main
 * 
 * @param date 
 * @param T_degC 
 * @return struct AvlNodeTemp* pointer to new leaf node with NULL children.
 */
struct AvlNodeTemp* newNodeTemp(int date, float T_degC){
    //struct AvlNodeTemp* node = (struct AvlNodeTemp*) 
    struct AvlNodeTemp* node = malloc(sizeof(struct AvlNodeTemp));
    node->date[0] = date;
    node->datesCount = 1;
    node->T_degC = T_degC;
    node->leftChild = NULL;
    node->rightChild = NULL;
    node->height = 0;
}

/**
 * @brief get height of node
 * 
 * @param node Pointer to target node
 * @return int height of target node.
 */
int getHeightTemp(struct AvlNodeTemp* node){
    if (node == NULL) return -1;
    int leftHeight = getHeightTemp(node->leftChild);
    int rightHeight = getHeightTemp(node->leftChild);
    return ((leftHeight>rightHeight) ?  leftHeight : rightHeight) + 1;
}

/**
 * @brief get balance of node
 * 
 * @param node Pointer to target node
 * @return int balance of node (right - left).
 */
int getBalanceTemp(struct AvlNodeTemp* node){
    if (node == NULL) return 0;
    return getHeightTemp(node->rightChild) - getHeightTemp(node->leftChild);
}

/**
 * @brief modifies ree to be valid AVL tree
 * 
 * @param rootNode 
 * @param date 
 * @param T_degC 
 * @return struct AvlNodeTemp* Pointer to new root node
 */
struct AvlNodeTemp* insertNodeTemp(struct AvlNodeTemp* rootNode, int date, float T_degC){
    //1)BST insertion
    if (rootNode == NULL) return newNodeTemp(date, T_degC); //bottom of tree reached

    if (T_degC < rootNode->T_degC){
        rootNode->leftChild = insertNodeTemp(rootNode->leftChild, date, T_degC);
    } else if (T_degC > rootNode->T_degC){
        rootNode->rightChild = insertNodeTemp(rootNode->rightChild, date, T_degC);
    } else {
        // indentical T_degC, add to array if space, else don't.
        if(rootNode->datesCount<MAX_DUPLICATES){
            rootNode->date[rootNode->datesCount] = date;
            rootNode->datesCount++;
            return rootNode;
        }
        //date array is full, don't add.
        return rootNode;
    } 

    //2)update height and get balance
    rootNode->height = getHeightTemp(rootNode);
    int balance = getBalanceTemp(rootNode);

    //#3)Rotate cases.
    //RR
    if (balance > 1 && T_degC > rootNode->rightChild->T_degC){
        return leftRotateTemp(rootNode);
    }
    //LL
    if (balance < -1 &&  T_degC < rootNode->leftChild->T_degC){
        return rightRotateTemp(rootNode);
    }
    //RL
    if (balance > 1){
        rootNode->rightChild = rightRotateTemp(rootNode->rightChild);
        return leftRotateTemp(rootNode);
    }
    //LR
    if (balance < -1){
        rootNode->leftChild = leftRotateTemp(rootNode->leftChild);
        return rightRotateTemp(rootNode);
    }

    //Else no need for rotaiton.
    return rootNode;
}

/**
 * @brief Left rotates around target node.
 * 
 * @param targetNode Pointer to rotate target node
 * @return struct AvlNodeTemp* New root for (sub)tree.
 */
struct AvlNodeTemp* leftRotateTemp(struct AvlNodeTemp* targetNode){
    struct AvlNodeTemp* rightNode = targetNode->rightChild;
    struct AvlNodeTemp* middleTree = rightNode->leftChild;

    targetNode->rightChild = middleTree;
    rightNode->leftChild = targetNode;

    targetNode->height = getHeightTemp(targetNode);
    rightNode->height = getHeightTemp(rightNode);

    return rightNode;

}

/**
 * @brief Right rotates around target node.
 * 
 * @param targetNode Pointer to rotate target node
 * @return struct AvlNodeTemp* pointer to New root for (sub)tree.
 */
struct AvlNodeTemp* rightRotateTemp (struct AvlNodeTemp* targetNode){
    struct AvlNodeTemp* leftNode = targetNode->leftChild;
    struct AvlNodeTemp* middleTree = leftNode->rightChild;

    targetNode->leftChild = middleTree;
    leftNode->rightChild = targetNode;

    targetNode->height = getHeightTemp(targetNode);
    leftNode->height = getHeightTemp(leftNode);

    return leftNode;
}

/**
 * @brief finds node with min temperature.
 * 
 * @param rootNode 
 * @return struct AvlNodeTemp* node with min temperature
 */
struct AvlNodeTemp* minTemperature (struct AvlNodeTemp* rootNode){
    //Node was found or doesn't exist
    if (rootNode == NULL || !(rootNode->leftChild)) return rootNode;
    //Else recursively return next node's results (move to next node down).
    return minTemperature(rootNode->leftChild);
}

/**
 * @brief finds node with max temperature.
 * 
 * @param rootNode 
 * @return struct AvlNodeTemp* node with max temperature
 */
struct AvlNodeTemp* maxTemperature (struct AvlNodeTemp* rootNode){
    //Node was found or doesn't exist
    if (rootNode == NULL || !(rootNode->rightChild)) return rootNode;
    //Else recursively return next node's results (move to next node down).
    return maxTemperature(rootNode->rightChild);    
}
