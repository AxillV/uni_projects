#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include "part2shared.h"

struct AvlNode{
    int date;
    float T_degC;
    struct AvlNode* leftChild;
    struct AvlNode* rightChild;
    int height;
};

struct AvlNode* newNode(int, float);
struct AvlNode* accessTree(struct AvlNode*, int);
struct AvlNode* modifyTree(struct AvlNode*, int, float);
struct AvlNode* insertNode(struct AvlNode*, int, float);
struct AvlNode* deleteNode(struct AvlNode*, int);
struct AvlNode* leftRotate (struct AvlNode*);
struct AvlNode* rightRotate (struct AvlNode*);
void inOrderTree(struct AvlNode*);

int part2_1main() {
    FILE *fp;
    char buffer[MAXCHAR];
    char *token;

    struct AvlNode *root = NULL;
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

        root = insertNode(root, dateInt, T_degCFloat);
        
    }
    fclose(fp);
    printf("AVL tree was sucessfully created.\n");

    //MAIN LOOP
    while (!NULL){
        struct AvlNode* searchNode;
        int menuSelection;
        int inDate;
        float inTemperature;
        //char term;
        printf("1.In order depiction of AVL tree.\n2.Search for temperature given date.\n3.Modify temperature for given date.\n4.Delete record for given date.\n5.Quit. ");
        scanf("%d", &menuSelection);
        switch (menuSelection)
        {
        case 1:
            printf("\n\n");
            inOrderTree(root);
            printf("\n\n");
            break;
        case 2:
            printf("\nEnter date to find in the following format: yyyymmdd: ");
            scanf("%d", &inDate);    
            searchNode = accessTree(root, inDate);
            if(searchNode != NULL){       
                printf("%d/%d/%d: %.2f\n\n", inDate/10000, (inDate%10000)/100, inDate%100, searchNode->T_degC);
            }else printf("Date was not found.\n\n");
            break;
        case 3:
            printf("\nEnter date to modify in the following format: yyyymmdd: ");
            scanf("%d", &inDate);    
            printf("\nEnter new temperature for selected date: ");
            scanf("%f", &inTemperature);    
            searchNode = modifyTree(root, inDate, inTemperature);
            if(searchNode != NULL){       
                printf("%d/%d/%d: %.2f\n\n", inDate/10000, (inDate%10000)/100, inDate%100, searchNode->T_degC);
            }else printf("Date was not found.\n\n");
            break;
        case 4:
            printf("\nEnter date to delete in the following format [yyyymmdd]: ");
            scanf("%d", &inDate);   
            root = deleteNode(root, inDate);
            printf("Record was deleted, if it existed.\n\n");
            break;
        case 5:
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

/**
 * @brief Not to be used directly in main
 * 
 * @param date 
 * @param T_degC 
 * @return struct AvlNode* pointer to new leaf node with NULL children.
 */
struct AvlNode* newNode(int date, float T_degC){
    //struct AvlNode* node = (struct AvlNode*) 
    struct AvlNode* node = malloc(sizeof(struct AvlNode));
    node->date = date;
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
int getHeight(struct AvlNode* node){
    if (node == NULL) return -1;
    int leftHeight = getHeight(node->leftChild);
    int rightHeight = getHeight(node->leftChild);
    return ((leftHeight>rightHeight) ?  leftHeight : rightHeight) + 1;
}

/**
 * @brief get balance of node
 * 
 * @param node Pointer to target node
 * @return int balance of node (right - left).
 */
int getBalance(struct AvlNode* node){
    if (node == NULL) return 0;
    return getHeight(node->rightChild) - getHeight(node->leftChild);
}

/**
 * @brief 
 * 
 * @param rootNode 
 * @return struct AvlNode* Next in order node in subtree.
 */
struct AvlNode* nextInOrder(struct AvlNode* rootNode){
    struct AvlNode* tempNode = rootNode->rightChild;
    while (tempNode->leftChild != NULL){
        tempNode = tempNode->leftChild;
    }
    return tempNode;
}

/**
 * @brief returns node if date exists
 * 
 * @param rootNode 
 * @param searchDate 
 * @return struct AvlNode* NULL if date not found, else pointer to node.
 */
struct AvlNode* accessTree(struct AvlNode* rootNode, int searchDate){
    //Node was found or doesn't exist
    if (rootNode == NULL || rootNode->date == searchDate) return rootNode;
    //Else recursively return next node's results (move to next node down).
    if (rootNode->date > searchDate){
        return accessTree(rootNode->leftChild, searchDate);
    }
    //Else node's date <= search date.
    return accessTree(rootNode->rightChild, searchDate);
    
}

/**
 * @brief modifies ode with input date to have new temperature value
 * 
 * @param rootNode 
 * @param searchDate 
 * @param newTemperature 
 * @return struct AvlNode* NULL if date not found, else pointer to node
 */
struct AvlNode* modifyTree(struct AvlNode* rootNode, int searchDate, float newTemperature){
    //Node doesn't exist
    if (rootNode == NULL) return rootNode;
    //Node was found, modify and return it for consistency across methods.
    if (rootNode->date == searchDate){
        rootNode->T_degC = newTemperature;
        return rootNode;    
    }
    //Else recursively return next node's results (move to next node down).
    if (rootNode->date > searchDate){
        return modifyTree(rootNode->leftChild, searchDate, newTemperature);
    }
    //Else node's date <= search date.
    return modifyTree(rootNode->rightChild, searchDate, newTemperature);
    
}

/**
 * @brief modifies ree to be valid AVL tree
 * 
 * @param rootNode 
 * @param date 
 * @param T_degC 
 * @param rotated pointer to int that holds 0 initially, 1 when there was a rotation to skip addiotional rotations.
 * @return struct AvlNode* Pointer to new root node
 */
struct AvlNode* insertNode(struct AvlNode* rootNode, int date, float T_degC){
    //1)BST insertion
    if (rootNode == NULL) return newNode(date, T_degC); //bottom of tree reached

    if (date < rootNode->date){
        rootNode->leftChild = insertNode(rootNode->leftChild, date, T_degC);
    } else if (date > rootNode->date){
        rootNode->rightChild = insertNode(rootNode->rightChild, date, T_degC);
    } else return rootNode; // indentical date, keep old node.

    //2)update height and get balance
    rootNode->height = getHeight(rootNode);
    int balance = getBalance(rootNode);
    
    //#3)Rotate cases.
    //RR
    if (balance > 1 && date > rootNode->rightChild->date){
        return leftRotate(rootNode);
    }
    //LL
    if (balance < -1 &&  date < rootNode->leftChild->date){
        return rightRotate(rootNode);
    }
    //RL
    if (balance > 1){
        rootNode->rightChild = rightRotate(rootNode->rightChild);
        return leftRotate(rootNode);
    }
    //LR
    if (balance < -1){
        rootNode->leftChild = leftRotate(rootNode->leftChild);
        return rightRotate(rootNode);
    }

    //Else no need for rotation.
    return rootNode;
}

/**
 * @brief modifies tree to be valid AVL tree.
 * 
 * @param rootNode Root node of tree
 * @param date date to be deleted
 * @return struct AvlNode* Pointer to new root node
 */
struct AvlNode* deleteNode(struct AvlNode* rootNode, int date){
    //1)BST deletion
    if (rootNode == NULL) return rootNode; 

    if (date < rootNode->date){
        rootNode->leftChild = deleteNode(rootNode->leftChild, date);
    } else if (date > rootNode->date){
        rootNode->rightChild = deleteNode(rootNode->rightChild, date);
    } else{
        //else date = rootNodes date, found node to delete.
        if (rootNode->rightChild == NULL || rootNode->leftChild == NULL){
            //one child
            if(rootNode->leftChild != NULL){
                struct AvlNode* tempNode = rootNode->leftChild;
                *rootNode = *(tempNode);
                free(tempNode); 
            } else if(rootNode->rightChild != NULL){
                struct AvlNode* tempNode = rootNode->rightChild;
                *rootNode = *(tempNode); 
                free(tempNode); 
            } else{
                //no children
                free(rootNode);
                rootNode = NULL;
            }
        } else {
            //else two children, switch node to be deleted with next in order, 
            //delete the leaf node (continue down the tree but searching for the
            //temp node's key now which is defunct).
            struct AvlNode* tempNode = nextInOrder(rootNode);
            rootNode->date = tempNode->date;
            rootNode->T_degC = tempNode->T_degC;
            rootNode->rightChild = deleteNode(rootNode->rightChild, rootNode->date);
        }     
    }

    //if after deletion we have 0 nodes, return.
    if(rootNode == NULL){
        return rootNode;
    }

    //2)update height and get balance
    rootNode->height = getHeight(rootNode);
    int balance = getBalance(rootNode);

    //#3)Rotate cases.
    //RR
    if (balance > 1 && date > rootNode->rightChild->date){
        return leftRotate(rootNode);
    }
    //LL
    if (balance < -1 &&  date < rootNode->leftChild->date){
        return rightRotate(rootNode);
    }
    //RL
    if (balance > 1){
        rootNode->rightChild = rightRotate(rootNode->rightChild);
        return leftRotate(rootNode);
    }
    //LR
    if (balance < -1){
        rootNode->leftChild = leftRotate(rootNode->leftChild);
        return rightRotate(rootNode);
    }

    //Else no need for rotation.
    return rootNode;
}

/**
 * @brief Left rotates around target node.
 * 
 * @param targetNode Pointer to rotate target node
 * @return struct AvlNode* New root for (sub)tree.
 */
struct AvlNode* leftRotate(struct AvlNode* targetNode){
    struct AvlNode* rightNode = targetNode->rightChild;
    struct AvlNode* middleTree = rightNode->leftChild;

    targetNode->rightChild = middleTree;
    rightNode->leftChild = targetNode;

    targetNode->height = getHeight(targetNode);
    rightNode->height = getHeight(rightNode);

    return rightNode;

}

/**
 * @brief Right rotates around target node.
 * 
 * @param targetNode Pointer to rotate target node
 * @return struct AvlNode* pointer to New root for (sub)tree.
 */
struct AvlNode* rightRotate (struct AvlNode* targetNode){
    struct AvlNode* leftNode = targetNode->leftChild;
    struct AvlNode* middleTree = leftNode->rightChild;

    targetNode->leftChild = middleTree;
    leftNode->rightChild = targetNode;

    targetNode->height = getHeight(targetNode);
    leftNode->height = getHeight(leftNode);

    return leftNode;
}

/**
 * @brief Prints AVL tree in order.
 * 
 * @param rootNote pointer to root node.
 */
void inOrderTree(struct AvlNode* rootNode){
    if (rootNode != NULL){
        inOrderTree(rootNode->leftChild);
        int date = rootNode->date;
        float temperature = rootNode->T_degC;
        printf("%d/%d/%d: %.2f\n", date/10000, (date%10000)/100, date%100, temperature);
        inOrderTree(rootNode->rightChild);
    }
}
