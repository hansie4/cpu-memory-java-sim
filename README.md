# cpu-memory-java-sim
 A java program that uses seperate processes communicating through the use of I/O streams to simulate a CPU executing instructions from memeory. This is a project assigned to me in my third year operating systems class. The purpose of the project was to simulate how a computer's CPU interacts with main memory to fetch & execute instructions as well as read and store data.
 
# implementation
 I implemented the project in Java with a CPU class and a Memory class. Each of these classes are in their own file with their own main method. The program starts by running the Memory file. The memory process reads in the user program file into memory. The I/O stream from the memory is gotten and then passed into the CPU object when it's created. The cpu’s run function is then called starting the cycle of execution. The cpu reads the instructions from the memory and executes them with interrupts happening at specified values. The value for these interrupts to run at happens at the value passed in from the arguments when the CPU file is run. Once the user program is finished running all of the streams are closed and the Memory process is waited on to exit. One thing I included in my implementation that was not indicated in the instructions was a debugging mode in the two objects. When this debugging flag is true, the contents of the memory at each write are printed out to a file and the contents of the CPU registers are outputted to a different file. This is a rough outline on how I implemented the project.
 
 # debug mode
  The debug mode mentioned above can be turned on by passing true into the fourth argument in the CPU constructor and the second argument in the Memory constructor in their respective files. 
 
 # instruction set
 Currently 31 instructions are supported:
 
 1- Load the value into the AC

2 - Load the value at the address into the AC

3- Load the value from the address found in the given address into the AC (for example, if LoadInd 500, and 500 contains 100, then load from 100).

4- Load the value at (address+X) into the AC
(for example, if LoadIdxX 500, and X contains 10, then load from 510).

5- Load the value at (address+Y) into the AC

6- Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).

7 - Store the value in the AC into the address

8- Gets a random int from 1 to 100 into the AC

9 - If port=1, writes AC as an int to the screen
If port=2, writes AC as a char to the screen

10 - Add the value in X to the AC

11 - Add the value in Y to the AC

12 - Subtract the value in X from the AC

13 - Subtract the value in Y from the AC

14 - Copy the value in the AC to X

15 - Copy the value in X to the AC

16 - Copy the value in the AC to Y

17 - Copy the value in Y to the AC

18 - Copy the value in AC to the SP

19 - Copy the value in SP to the AC 

20 - Jump to the address

21 - Jump to the address only if the value in the AC is zero

22 - Jump to the address only if the value in the AC is not zero

23 - Push return address onto stack, jump to the address

24 - Pop return address from the stack, jump to the address

25 - Increment the value in X

26 - Decrement the value in X

27 - Push AC onto stack

28 - Pop from stack into AC

29 - Perform system call

30 - Return from system call

50 - End execution

# programs
 The 5 sample files given are examples of how to write programs the cpu reads and executes. The memory will read this file in line by line and read the first integer on each line into the next spot in memory starting at 0. If you would like to place the integer into a specific part of memery the line should have .[memory location](ex. .1500). 
 Notes when writing programs:
 * the cpu will start execution of the instruction stored at memory location 0
 * when run the program will be given a value to interupt at after that many instructions. These interupts cause execution at memory location 1000
 * memory is of size 2000 with 0 - 999 being for the user program and 1000 - 1999 for the system instructions for interupts

# sample files
 ## sample1.txt
  outputs: ABCDEFGHIJKLMNOPQRSTUVWXYZ12345678910
 ## sample2.txt
  outputs an ascii smily face
 ## sample3.txt
  'A' followed by values that change depending on the passed in interupt timer value
 ## sample4.txt
  outputs locations in memory. used to check if the stack is working as intended
 ## sample5.txt
  outputs a box of random width to the screen
