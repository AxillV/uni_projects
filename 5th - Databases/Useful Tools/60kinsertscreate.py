import os
import random


f = open(os.getcwd()+'\\names.txt', mode='r', encoding='utf-16')
names=[]

for line in f:
    line=line.split()
    names.append([line[0], line[1]])


f = open(os.getcwd()+'\\insert60.sql', mode='w')
f.write("USE travel_agency;\n\n")
f.write("INSERT INTO reservation_offers VALUES\n")

for i in range(3):
    for j in range(20000):
        downpay = round(random.uniform(50, 200), 2)
        firstName = int(round(random.uniform(0, 9999), 0))
        lastName = int(round(random.uniform(0, 9999), 0))

        if(i==2 and j==19999):
            output = "(NULL,'" + str(names[firstName][0]) + "','" + str(names[lastName][1]) + "'," + str(i+1) + "," + str(downpay) + ");\n"
            f.write(output)
            break

        output = "(NULL,'" + str(names[firstName][0]) + "','" + str(names[lastName][1]) + "'," + str(i+1) + "," + str(downpay) + "),\n"
        f.write(output)
