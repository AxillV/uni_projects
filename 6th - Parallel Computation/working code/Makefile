DIM ?= 16
KNN ?= 32
TRA ?= 1048576
QUE ?= 1024

LOW  ?= 0
HIGH ?= 2

OPENMP  = -fopenmp
CFLAGS  = -DPROBDIM=$(DIM) -DNNBS=$(KNN) -DTRAINELEMS=$(TRA) -DQUERYELEMS=$(QUE) 
CFLAGS += -DLB=$(LOW) -DUB=$(HIGH) -g -ggdb -O3 -DSURROGATES -Wall $(OPENMP)
LDFLAGS += -lm $(OPENMP)

all: gendata myknn

gendata: gendata.o
	gcc -o gendata gendata.o $(LDFLAGS)

gendata.o: gendata.c func.c
	gcc $(CFLAGS) -c gendata.c

myknn: myknn.o
	gcc -o myknn myknn.o $(LDFLAGS)

myknn.o: myknn.c func.c
	gcc $(CFLAGS) -c myknn.c 

clean:
	rm -f myknn *.o gendata

