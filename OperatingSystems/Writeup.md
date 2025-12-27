## Overview

For this project, we were given a program that created hashes of inputted files using fork()
to create multiple processes to increase the speed of the program. It splits inputted files
into blocks and fork()s to create a process to hash each block. The issue with this program
is that it is very slow, as processes require context switching and have their own memories.
This means it takes a long time for the processor to switch between each process and isn't 
actually that fast.

The goal of this project is, in part 1, to convert the system from a multiprocess system to a
threaded one. A thread is essentially a process contained within the same memory space as the
original process, meaning there is less context switching and wasted resources. The problem lies
with accessing information; to prevent corruption or incorrect reads information must be locked
so multiple threads cannot access them at the same time, which slows down memory. In part 2 the
goal is to take the changes from part 1 and make them even faster. How I achieved this can be
found below. How to compile and run these programs can be found at the end.

## Part 1

The code provided reads the input file in blocks each the size of 1 kilobyte. While the regular
version simply runs through and uses a Huffman Tree to hash the inputted file, multiprocess
splits the file into 1 kilobyte blocks and performs a fork() for each one each of which then
process the input into a hash, with a sempahore lock to control access to memory to prevent
corruption. For threading, I changed the pointer-to-a-pointer to a simple singular pointer
as threads share the same address space so they all recieve the same pointer. When threaded
mode is passed, the internal allocator is wrapped in a pthread_mutex_t lock so only one thread
can access the shared free_list at a time ensuring umalloc and ufree do not run concurrently.

When it comes to the threaded portion itself, the program allocates one memory slot per block
and creates threads that then run through its assigned block and returns its result before ending.
Typically the threads would free their memory before either ending or moving to the next block,
however in a small attempt to increase efficiency I made the program pre-allocate memory for each
block, then reuse those blocks for each thread before freeing the memory in one go afterward. This
reduces strain on memory, but doesn't actually increase efficiency much because the program is still
creating and deleting threads over and over which creates a lot of overhead and context switching.
I only have it running with 10 threads because more threads actually slow it down to a certain extent
because of all of this context switching and waiting for locks.

## Part 2

I implemented strategy A into the program, as I thought that reducing the need for each thread to
acquire the locks even further would be ideal. Each thread runs with its own read pointer into the
file and its own space in memory to write, meaning they do not have to acquire locks nearly as much.
On top of this, I made it so each thread is assigned a portion of the total number of blocks in the file
and runs through each of them, meaning the program doesn't need to repeatedly create threads over
and over which significantly reduces overhead. Each thread calls print_intermediate whenever it finishes
a block which means debug output will no longer be in order, but does a better job at showing concurrency.
I have this running on 32 threads as more have diminishing returns and this seems to be the best cutoff; it
can run on more, up to 113 and possibly greater if you allocate it more memory space.

### Speed on a 100 KB File:
#### sharedhash.c:
Final signature: 1895489082
real    0m0.046s
user    0m0.035s
sys     0m0.019s

#### esharedhash.c:
Final signature: 1895489082
real    0m0.015s
user    0m0.023s
sys     0m0.000s

### Speed on a 1 MB File:
#### sharedhash.c:
Final signature: 1669502966
real    0m0.472s
user    0m0.400s
sys     0m0.342s

#### esharedhash.c:
Final signature: 1669502966
real    0m0.041s
user    0m0.038s
sys     0m0.084s

### Speed on a 1.17 GB File:
#### sharedhash.c:
Final signature: 1958795124
real    9m50.387s
user    9m11.103s
sys     7m14.750s

#### esharedhash.c:
Final signature: 1958795124
real    0m17.427s
user    1m3.439s
sys     0m24.696s

### Learned:
The main holdup when it comes to concurrency is corruption. If multiple threads can access the same
read/write method they may end up overwriting eachother and causing errors. Locking prevents this
issue, but only one thread being able to access memory or writing at a time causes lots of waiting
and increases overall run time. Reducing the need for threads to lock/unlock and allocate memory
in general is a great method of reducing this run time.


## Compilation

This program was originally compiled and ran on Ubuntu 24.04.2 using the gcc command. For example:

### sharedhash.c
Compile sharedhash.c while showing debug outputs:
gcc -DDEBUG=2 -Wall -Wextra -o sharedhash sharedhash.c

Or compile without debug output:
gcc -o sharedhash sharedhash.c

Running the compiled program on a 1 MB test file:
./sharedhash test_1MB.bin -t

If you want to time how long it takes to hash the file:
time ./sharedhash test_1MB.bin -t

### esharedhash.c
Compiling and running esharedhash is much the same.
Compile esharedhash.c while showing debug outputs:
gcc -DDEBUG=2 -Wall -Wextra -o esharedhash esharedhash.c

Or compile without debug output:
gcc -o esharedhash esharedhash.c

Running the compiled program on a 1 MB test file:
./esharedhash test_1MB.bin -t

If you want to time how long it takes to hash the file:
time ./esharedhash test_1MB.bin -t