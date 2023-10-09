/* Bardakis Vasileios, 1088098 */
/* Haralabos-Marios Hallas, 1084589 */
/* Achilleas Villiotis, 1084567 */

/* header files */
#include <ctype.h>
#include <fcntl.h> //open() for hiding exec output
#include <libgen.h> // basename(3)
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h> // time measurements
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

/* global definitions */
#define DEBUG_INFO_PRINTS 0
#define DEBUG_START_STOP_PRINTS 0
#define DEBUG_STATIC_CONTINUE 0
#define DEBUG_SIMULATE_EXITED_INSIDE_FIRST_LOOP 0

#define STATIC_POLICIES {"fcfs", "sjf"}
#define NO_SPOL 2
#define DYNAMIC_POLICIES {"rr", "prio"}
#define NO_DPOL 2

#define RUNNING 2
#define READY 1
#define STOPPED 0
#define EXITED -1

/* definition and implementation of process descriptor and queue(s) */

typedef struct process {
	char *fullpath;
	int priority;
	int pid;
	int status;
	double timeUsed; //Keeps track of CPU time used for this process.
}process;

typedef struct queueNode {
	struct queueNode *prev;
	struct process *process;
	struct queueNode *next;
}queueNode;

queueNode *serialHead = NULL;
queueNode *serialTail = NULL;
queueNode *sortedHead = NULL;
queueNode *sortedTail = NULL;
queueNode *exitedHead = NULL;
queueNode *exitedTail = NULL;
process *activeProcess;

//push a new process and queue node
void push(queueNode **head, queueNode **tail, char *inFullPath, int inPriority){
	queueNode *newNode = (queueNode *) malloc(sizeof(queueNode));
	process *newProcess = (process *) malloc(sizeof(process));
	newProcess->fullpath = (char *)malloc(sizeof(inFullPath));

	newNode->prev = *tail;

	newNode->process = newProcess;
	strcpy(newProcess->fullpath, inFullPath);
	newProcess->priority = inPriority;
	newProcess->status = READY;
	newProcess->timeUsed = 0;

	newNode->next = NULL;

	// empty 
	if ((*head) == NULL) {
		*head = newNode; // make front pointer point to new node (newProcess points to it)
	}
	// not empty, previous tail node must point to new tail
	else {
		(*tail)->next = newNode;
	}

	*tail = newNode;
}

//Push an existing process to another queue
void pushProcess(queueNode **head, queueNode **tail, process *inProcess){
	queueNode *newNode = (queueNode *) malloc(sizeof(queueNode));
	newNode->prev = *tail;
	newNode->process = inProcess;
	newNode->next = NULL;

	// empty 
	if ((*head) == NULL) {
		*head = newNode; // make front pointer point to new node (newProcess points to it)
	}
	// not empty, previous tail node must point to new tail
	else {
		(*tail)->next = newNode;
	}

	*tail = newNode;
}

void removeNode(queueNode **head, queueNode *nodeToRemove){
	//base case
    if (*head == NULL || nodeToRemove == NULL)
        return;

	//node to be deleted is head
	if (*head == nodeToRemove)
		*head = nodeToRemove->next;

	//if node to remove isn't the last one (tail)
	if (nodeToRemove->next != NULL)
		nodeToRemove->next->prev = nodeToRemove->prev;

	//if node to remove isn't the first one (head)
	if (nodeToRemove->prev != NULL)
		nodeToRemove->prev->next = nodeToRemove->next;
	free(nodeToRemove);
	return;
	
}

void freeProcess(process *process){
	free(process->fullpath);
	free(process);
}

//freeProcesses==1 then also free processes, else only free queueNodes
void freeQueue(queueNode **head, int freeProcesses){
	while(*head!=NULL){
		if(freeProcesses==1){
			freeProcess((*head)->process);
		}
		removeNode(head, *head);
	}
}

//https://www.geeksforgeeks.org/insertion-sort-doubly-linked-list/
void sortedInsert(queueNode** head, queueNode* newNode){
	queueNode* current;

	// if list is empty
	if (*head == NULL){
		*head = newNode;
	}
		
	// if the node is to be inserted at the beginning of the doubly linked list
	else if ((*head)->process->priority > newNode->process->priority) {
		newNode->next = *head;
		newNode->next->prev = newNode;
		*head = newNode;
	}

	else {
		current = *head;

		// locate the node after which the new node is to be inserted
		while (current->next != NULL && current->next->process->priority <= newNode->process->priority){
			current = current->next;
		}
		newNode->next = current->next;

		// if the new node is not inserted at the end of the list
		if (current->next != NULL){
			newNode->next->prev = newNode;
		}
		current->next = newNode;
		newNode->prev = current;
	}
}

//https://www.geeksforgeeks.org/insertion-sort-doubly-linked-list/
void sortSerialIntoSortedDLLAscending(){
	queueNode* current = serialHead;
	while (current != NULL) {
		queueNode* newNode = (queueNode*) malloc(sizeof(queueNode));
		memcpy(newNode, current, sizeof(queueNode));
		newNode->prev = newNode->next = NULL;
		sortedInsert(&sortedHead, newNode);
		current = current->next;
	}
}

/* global variables and data structures */

double executionStartTime;

double get_wtime(void){     //Gia na vroume poso xrono thelei
  struct timeval t;
 
  gettimeofday(&t, NULL);
 
  return (double)t.tv_sec + (double)t.tv_usec*1.0e-6;
}

struct timespec convertMilliToTimeSpec(int milliseconds){
	struct timespec sleepTime;
	sleepTime.tv_sec = milliseconds / 1000;
	sleepTime.tv_nsec = ((long) milliseconds % 1000L) * 1000000L;
	return sleepTime;
}



char *const staticPolicies[] = STATIC_POLICIES;
char *const dynamicPolicies[] = DYNAMIC_POLICIES;

void printAvailabePolicies(){
	printf("Policy and quant combination does not exist! Availabe policies are:\n");
	printf("---------------\nStatic Policies\n---------------\n");
	for (int i=0; i<NO_SPOL; i++){
		printf("%s\n", staticPolicies[i]);
	}

	printf("----------------\nDynamic Policies\n----------------\n");
	for (int i=0; i<NO_DPOL; i++){
		printf("%s\n", dynamicPolicies[i]);
	}
	printf("\n");
}

/*https://www.includehelp.com/c-programs/c-program-to-compare-two-strings-strcmp-strcmpi.aspx
RETURNS: 0 if matching, 1 if not matching*/
int stringCmpi (char *s1,char *s2)
{
    int i=0,diff=0;
    for(i=0; s1[i]!='\0'; i++)
    {
        if (toupper(s1[i])!=toupper(s2[i]))
            return 1;           
    }
    return 0;
}

/* signal handler(s) */
void childTerminatedHandler(int signo){	
	// Catch zombie process
	while (waitpid(-1, NULL, WNOHANG) > 0)
        activeProcess->status=EXITED;   //global variable so we can do this little trick
}


/* implementation of the scheduling policies, etc. batch(), rr() etc. */

void staticExecRun(process *processToRun){
	int pid = fork();

	if (pid == 0) {
		// https://stackoverflow.com/questions/26453624/hide-terminal-output-from-execve
		// opening dev/null
		if(!DEBUG_START_STOP_PRINTS){
			int fd = open("/dev/null", O_WRONLY);
			dup2(fd, 1);
			dup2(fd, 2);
			close(fd);
		}

		char *fullpath = processToRun->fullpath;
		execl(fullpath, fullpath, NULL);
		exit(127); //command not found
	}else{
		double processStartTime = get_wtime(); //at the top because fork
		struct timespec sleepTime;
		activeProcess=processToRun;
		processToRun->status = RUNNING;
		processToRun->pid=pid;

		sleepTime.tv_sec=0;
		sleepTime.tv_nsec=100;
		if(!DEBUG_STATIC_CONTINUE){
			//sleep for 100 nanoseconds each time, child dies and handler runs
			while(processToRun->status==RUNNING)	
			nanosleep	(&sleepTime, NULL);
		}else if(DEBUG_STATIC_CONTINUE){
			//debugging static continue
			sleepTime.tv_sec=0;
			sleepTime.tv_nsec=100000000;
			nanosleep(&sleepTime, NULL);
			kill(processToRun->pid, SIGSTOP);
		}

		double processEndTime = get_wtime();
		double elapsedTime = processEndTime - processStartTime;
		double workloadTime = processEndTime - executionStartTime;
		processToRun->timeUsed += elapsedTime;

		//if process didn't finish (handler didn't set exited),then set to stopped (shouldn't happen in static algos)
		if (processToRun->status==RUNNING)
			processToRun->status = STOPPED;

		//if process finished, then print results
		if (processToRun->status==EXITED){
			double workloadTime = processEndTime - executionStartTime;
			printf("PID %d - CMD %s\n", pid, basename(processToRun->fullpath));
			printf("\t\t\tElapsed Time: %.3lf secs\n", processToRun->timeUsed);
			printf("\t\t\tWorkload Time: %.3lf secs\n", workloadTime);
		}
	}
}

void staticContinueRun(process *processToRun){
	if(DEBUG_START_STOP_PRINTS)
		printf("process %d restarts\n", processToRun->pid);

	struct timespec sleepTime;
	activeProcess=processToRun;
	processToRun->status = RUNNING;
	double processStartTime = get_wtime();
	kill(processToRun->pid, SIGCONT);
	//sleep for 100 nanoseconds each time, child dies and handler runs
	while(processToRun->status==RUNNING){
		sleepTime.tv_sec=0;
		sleepTime.tv_nsec=100;
		nanosleep(&sleepTime, NULL);
	}

	double processEndTime = get_wtime();
	double elapsedTime = processEndTime - processStartTime;
	processToRun->timeUsed += elapsedTime;

	//if process didn't finish (handler didn't set exited),then set to stopped
	if (processToRun->status==RUNNING)
		processToRun->status = STOPPED;

	//if process finished, then print results
	if (processToRun->status==EXITED){
		double workloadTime = processEndTime - executionStartTime;
		printf("PID %d - CMD %s\n", processToRun->pid, basename(processToRun->fullpath));
		printf("\t\t\tElapsed Time: %.3lf secs\n", processToRun->timeUsed);
		printf("\t\t\tWorkload Time: %.3lf secs\n", workloadTime);
	}
}

void fcfs(){
	queueNode *current = serialHead;
	queueNode *tmp;
	int curStatus;
	//Loop until entire queue is empty
	while(serialHead != NULL){
		curStatus=current->process->status;
		while(curStatus != READY && curStatus != STOPPED) {
			//pop from serial queue into exited queue if exited
			if((current->process)->status == EXITED){
				pushProcess(&exitedHead, &exitedTail, current->process);
				removeNode(&serialHead, current);
			}
			current=current->next;
			if (current == NULL) break;
			curStatus=current->process->status;
		}
		// end reached, return to start
		if (current == NULL) {
			current = serialHead;
			continue;
		}

		if(curStatus==READY){
			staticExecRun(current->process);
		} else if(curStatus==STOPPED){
			staticContinueRun(current->process);
		}
		
		
		// check if there is another node after, 
		// else return to head after popping below
		if (current->next != NULL)
			tmp = current->next;
		else tmp = serialHead;
		//pop from serial queue into exited queue if exited
		if((current->process)->status == EXITED){	
			pushProcess(&exitedHead, &exitedTail, current->process);
			removeNode(&serialHead, current);
		}
		current = tmp;
		
	}
	return;
}

void sjf(){
	sortSerialIntoSortedDLLAscending();
	queueNode *tmp;
	queueNode *current = sortedHead;
	int curStatus;
	//Loop until entire queue is empty
	while(sortedHead != NULL){
		curStatus=current->process->status;
		while(curStatus!=READY && curStatus!=STOPPED){
			//pop from sorted queue into exited queue if exited
			if((current->process)->status == EXITED){
				pushProcess(&exitedHead, &exitedTail, current->process);
				removeNode(&sortedHead, current);
			}
			current=current->next;
			if (current == NULL) break;
			curStatus=current->process->status;
		}
		// end reached, return to start
		if (current == NULL) {
			current = sortedHead;
			continue;
		}

		if(curStatus==READY){
			staticExecRun(current->process);
		} else if(curStatus==STOPPED){
			staticContinueRun(current->process);
		}
		
		
		// check if there is another node after, 
		// else return to head after popping below
		if (current->next != NULL)
			tmp = current->next;
		else tmp = sortedHead;
		//pop from sorted queue into exited queue if exited
		if((current->process)->status == EXITED){	
			pushProcess(&exitedHead, &exitedTail, current->process);
			removeNode(&sortedHead, current);
		}
		current = tmp;
	}
	return;
}

void dynamicExecRun(process *processToRun, int quant){
	int pid = fork();

	if (pid==0){
		//stackoverflow.com/questions/26453624/hide-terminal-output-from-execve
		// opening dev/null
		if(!DEBUG_START_STOP_PRINTS){
			int fd = open("/dev/null", O_WRONLY);
			dup2(fd, 1);
			dup2(fd, 2);
			close(fd);
		}

		char *fullpath = processToRun->fullpath;
		execl(fullpath, fullpath, NULL);
		exit(127); //command not found

	} else if (pid>0){
		double processStartTime = get_wtime();
		processToRun->status = RUNNING; // put this on top in case process is very fast!
		activeProcess = processToRun;
		processToRun->pid = pid;
		
		//this is required because max long int is ~2billion (or 2 seconds!)
		struct timespec sleepTime = convertMilliToTimeSpec(quant);
		nanosleep(&sleepTime, NULL);
		kill(pid, SIGSTOP);

		double processEndTime = get_wtime();
		double elapsedTime = processEndTime - processStartTime;
		processToRun->timeUsed += elapsedTime;

		//if process didn't finish (handler didn't set exited),then set to stopped
		if (processToRun->status==RUNNING)
			processToRun->status = STOPPED;

		//if process finished, then print results
		if (processToRun->status==EXITED){
			double workloadTime = processEndTime - executionStartTime;
			printf("PID %d - CMD %s\n", pid, basename(processToRun->fullpath));
			printf("\t\t\tElapsed Time: %.3lf secs\n", processToRun->timeUsed);
			printf("\t\t\tWorkload Time: %.3lf secs\n", workloadTime);
		}
	}	
}

void dynamicContinueRun(process *processToRun, int quant){
	if(DEBUG_START_STOP_PRINTS)
		printf("process %d restarts\n", processToRun->pid);

	processToRun->status = RUNNING;
	activeProcess = processToRun;

	//this is required because max long int is ~2billion (or 2 seconds!)
	struct timespec sleepTime = convertMilliToTimeSpec(quant);
	double processStartTime = get_wtime(); //run this here so it's closer to actual execution time
	kill(processToRun->pid, SIGCONT);
	nanosleep(&sleepTime, NULL);
	kill(processToRun->pid, SIGSTOP);

	double processEndTime = get_wtime();
	double elapsedTime = processEndTime - processStartTime;
	processToRun->timeUsed += elapsedTime;

	//if process didn't finish (handler didn't set exited),then set to stopped
	if (processToRun->status==RUNNING)
		processToRun->status = STOPPED;

	//if process finished, then print results
	if (processToRun->status==EXITED){
		double workloadTime = processEndTime - executionStartTime;
		printf("PID %d - CMD %s\n", processToRun->pid, basename(processToRun->fullpath));
		printf("\t\t\tElapsed Time: %.3lf secs\n", processToRun->timeUsed);
		printf("\t\t\tWorkload Time: %.3lf secs\n", workloadTime);
	}
}

void roundrobinAutonomous(int quant) {
	int curStatus;
	queueNode *current = serialHead;
	queueNode *tmp;

	//Loop until entire queue is empty
	while(serialHead != NULL){
		curStatus = current->process->status;
		while(curStatus != READY && curStatus != STOPPED) {
			//pop from serial queue into exited queue if exited
			if(curStatus == EXITED){
				pushProcess(&exitedHead, &exitedTail, current->process);
				removeNode(&serialHead, current);
			}
			current=current->next;
			if (current == NULL) break;
			curStatus=current->process->status;
		}
		// end reached, return to start
		if (current == NULL) {
			current = serialHead;
			continue;
		}
		
		if(curStatus==READY){
			dynamicExecRun(current->process, quant);
		} else if (curStatus==STOPPED){
			dynamicContinueRun(current->process, quant);
		} else {
			//Something went wrong???
			printf("\nERROR SOMETHING WENT WRONG!\n");
			exit(0);
		}

		// check if there is another node after, change current to that,
		// else return to head after popping below
		if (current->next != NULL)
			tmp = current->next;
		else tmp = serialHead;
		//pop from sorted queue into exited queue if exited
		if((current->process)->status == EXITED){	
			pushProcess(&exitedHead, &exitedTail, current->process);
			removeNode(&serialHead, current);
		}
		current = tmp;
	}
	return;
}

void roundrobinPriority(queueNode *inNode, int range, int quant) {
	int curStatus;
	int index=0;
	queueNode *rrStartNode = inNode;
	queueNode *current = inNode;
	queueNode *temp;

	//Loop until range is 0 (all items executed)
	while(range>0){
		curStatus = current->process->status;
		while(curStatus != READY && curStatus != STOPPED) {
			//pop from serial queue into exited queue if exited
			if(curStatus == EXITED){
				//FIXME: if startnode is current, startnode is next
				pushProcess(&exitedHead, &exitedTail, current->process);
				removeNode(&sortedHead, current);
				range--;
				index--; //effectively cancels the one after, stay in same position
			}
			current=current->next;
			index++; //checking next in order node
			if (current == NULL || index==range) break;
			curStatus=current->process->status;
		}
		// end reached, return to start
		if (current == NULL || index==range) {
			current = rrStartNode;
			index=0;
			continue;
		}
		
		if(curStatus==READY){
			dynamicExecRun(current->process, quant);
			index++;
		} else if (curStatus==STOPPED){
			dynamicContinueRun(current->process, quant);
			index++;
		} else {
			//Something went wrong???
			printf("\nERROR SOMETHING WENT WRONG!\n");
			exit(0);
		}

		curStatus = current->process->status;
		temp=current->next;
		if(curStatus==EXITED){
			index--;
			range--;
			if(current==rrStartNode)
				rrStartNode=current->next;
			pushProcess(&exitedHead, &exitedTail, current->process);
			removeNode(&sortedHead, current);
		}

		if(index>=range){
			index=0;
			temp=rrStartNode;
		}
		current=temp;

	}
}

//Writes into range pointer the amount of processes with the same priority
void returnPriorityRRRange(queueNode *inputNode, int *range){
	int i=1;
	queueNode *currentNode = inputNode;
	queueNode *nextNode = inputNode->next;
	if(nextNode==NULL){
		*range=i;
		return;
	}
	while(nextNode->process->priority==currentNode->process->priority){
		i++;
		currentNode = nextNode;
		nextNode = currentNode->next;
		if(nextNode==NULL){
			*range=i;
			return;
		}
	}
	*range=i;
}

void priority(int quant){
	sortSerialIntoSortedDLLAscending();
	int curStatus;
	queueNode *current = sortedHead;
	queueNode *tmp;
	//Loop until entire queue is empty
	while(sortedHead!=NULL){
		curStatus=current->process->status;
		while(curStatus!=READY && curStatus!=STOPPED){
			if(curStatus == EXITED){
				pushProcess(&exitedHead, &exitedTail, current->process);
				removeNode(&sortedHead, current);
			}
			current=current->next;
			if (current == NULL) break;
		}	
		// end reached, return to start
		if (current == NULL) {
			current = sortedHead;
			continue;
		}
		int range;
		returnPriorityRRRange(current, &range);
		
		if(range==1){
			if(current->process->status==READY)
				staticExecRun(current->process);
			else if(current->process->status==STOPPED)
				staticContinueRun(current->process);
			else exit(0); //error
		}else if(range>1){
			roundrobinPriority(current, range, quant);
			//don't push a second time below (bug when cleaning queues at the end of program)
			if (current->next != NULL)
				current = current->next;
			else current = sortedHead;
			continue;
		} else {
			exit(0); //error
		}

		// check if there is another node after, 
		// else return to head after popping below
		if (current->next != NULL)
			tmp = current->next;
		else tmp = sortedHead;
		//pop from sorted queue into exited queue if exited
		if((current->process)->status == EXITED){	
			pushProcess(&exitedHead, &exitedTail, current->process);
			removeNode(&sortedHead, current);
		}
		current = tmp;
	}
	return;
}

int main(int argc,char **argv)
{	
	/* local variables */
	char *policy = NULL;
	int quant;
	char *fileName = NULL;

	/* setup sigaction */
	struct sigaction sigact;
	sigemptyset(&sigact.sa_mask);
	sigact.sa_flags = 0 | SA_NOCLDSTOP;
	sigact.sa_handler = childTerminatedHandler;
	sigaction(SIGCHLD, &sigact, NULL);

	/* parse input arguments (policy, quantum (if required), input filename */
	switch(argc){
		case 3:
			for (int i=0; i<NO_SPOL; i++){
				if(stringCmpi(argv[1], staticPolicies[i])==0){
					policy=staticPolicies[i];
				}
			}	
			fileName=argv[2];
			break;
	
		case 4:
			for (int i=0; i<NO_DPOL; i++){
				if(stringCmpi(argv[1], dynamicPolicies[i])==0){
					policy=dynamicPolicies[i];
				}
			}	
			fileName=argv[3];
			quant = atoi(argv[2]);
			break;

		default:
			printf("Wrong amount of arguments given!\n");
			printf("Correct usage is: ./scheduler algorithm [quant] sourceFile\n");
			printAvailabePolicies();
			exit(0);
	}
	if(policy){
		if(DEBUG_INFO_PRINTS==1){
			if(quant){
				printf("Selected policy is %s, with quant %d and file %s\n", policy, quant, fileName);
			} else {
				printf("Selected policy is %s, with file %s\n", policy, fileName);
			}
		}
	} else {
		printAvailabePolicies();
		exit(0);
	}


	/* read input file - populate queue */
	//https://stackoverflow.com/questions/1384264/parsing-text-in-c
	//https://www.tutorialspoint.com/c_standard_library/c_function_fscanf.htm#
	char path[128];
	int prio;
	FILE *fp = fopen(fileName, "r");
	if(DEBUG_INFO_PRINTS==1){
		printf("\nCommencing list creation...\n");
		printf("\nParsing jobs...\n");
	}

	while (EOF != fscanf(fp, "%s\t%d", path, &prio)){
		if(DEBUG_INFO_PRINTS==1)
			printf("%s | %d\n", path, prio);
		push(&serialHead, &serialTail, path, prio);
	}
	if(DEBUG_INFO_PRINTS==1){
		printf("\nList creation complete!\n");
		printf("\nCreating work files...\n");
	}

	fclose(fp);

	if(DEBUG_INFO_PRINTS==1)
		system("cd ../work && make all");
	else
		system("cd ../work && make all 2>&1 >/dev/null");

	/* call selected scheduling policy */
	if(DEBUG_SIMULATE_EXITED_INSIDE_FIRST_LOOP==1)
		serialHead->next->process->status=EXITED;

	executionStartTime = get_wtime();
	if (strcmp(policy, "fcfs") == 0) {
		if(DEBUG_INFO_PRINTS==1)
			printf("\n\nCommencing FCFS...\n\n");
		fcfs();
	}
	else if (strcmp(policy, "sjf") == 0) {
		if(DEBUG_INFO_PRINTS==1)
			printf("\n\n Commencing SJF..\n\n");
		sjf();
	}
	else if (strcmp(policy, "rr") == 0) {
		if(DEBUG_INFO_PRINTS==1)
			printf("\n\nCommencing RR...\n\n");
		roundrobinAutonomous(quant);
	}
	else if (strcmp(policy, "prio") == 0) {
		if(DEBUG_INFO_PRINTS==1)
			printf("Commencing PRIO...\n\n");
		priority(quant);
	}

	double executionEndTime = get_wtime();
	printf("\nWORKLOAD TIME: %.3lf secs\n", executionEndTime-executionStartTime);

	/* print information and statistics */
	if(DEBUG_INFO_PRINTS==1){
		printf("\nDeleting executables...\n");
		system("cd ../work && make clean");
	} else{
		system("cd ../work && make clean 2>&1 >/dev/null");
	}
	
	if(DEBUG_INFO_PRINTS==1)
		printf("\nFreeing queues...\n");
	freeQueue(&serialHead, 0);
	freeQueue(&sortedHead, 0);
	freeQueue(&exitedHead, 1);
	
	if(DEBUG_INFO_PRINTS==1)
		printf("\nScheduler exits...\n");
	return 0;
}
