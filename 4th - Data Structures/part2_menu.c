#include <stdio.h>
#include <stdlib.h>
#include "part2_1.h"
#include "part2_2.h"
#include "part2_3.h"

int main() {
    int input = -1;
    int avlInput = -1;

    printf("Greetings!\n");
    printf("Load contents of file using AVL (1) or HASHING (2)? Type anything else to EXIT from program.\n");
    scanf("%d", &input);

    if (input < 1 || input > 2) {
        printf("Exiting...\n");
        return 0;
    }

    switch (input) {
        case 1:
            while (avlInput < 1 || avlInput > 2) {
                printf("Load to AVL based on DATE (1) or TEMPERATURE (2)? Type anything else to EXIT from program.\n");
                scanf("%d", &avlInput);
            }

            switch (avlInput) {
                case 1:
                    part2_1main();
                    break;

                case 2:
                    part2_2main();
                    break;

                default:
                    printf("Exiting...\n");
                    return 0;
                    break;
            }
            break;


        case 2:
            part2_3main();
            break;
        
        default:
            printf("Exiting...\n");
            return 0;
            break;
    }
    
    printf("Exiting...\n");
    return 0;
}