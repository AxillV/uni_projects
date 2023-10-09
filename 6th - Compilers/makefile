########################################
# MODIFIED FROM LEX DOCUMENTATION BOOK #
########################################
CC=gcc
CFLAGS= -Wall
LEX=flex
LFLAGS= #-d
YACC=bison -y -d
YFLAGS = #-t -Wce 
objects = scan.o parse.o extra.o
run: $(objects)
	gcc -o myParser.exe $(objects)
scan.o: scan.l parse.c
parse.o: parse.y
extra.o: extra.c
clean: 
	$(RM) *.o *.tab.h parse.c myParser.exe output
r:
	make clean
	make
	