Hello, World!
======

A "Hello, World!" program is a computer program that outputs "Hello, World!" (or some variant thereof) on a display device. Because it is typically one of the simplest programs possible in most programming languages, it is by tradition often used to illustrate to beginners the most basic syntax of a programming language. It is also used to verify that a language or system is operating correctly.

Purpose
-------

A "Hello, world!" program has become the traditional first program that many people learn. In general, it is simple enough so that people who have no experience with computer programming can easily understand it, especially with the guidance of a teacher or a written guide. Using this simple program as a basis, computer science principles or elements of a specific programming language can be explained to novice programmers. Experienced programmers learning new languages can also gain a lot of information about a given language's syntax and structure from a "Hello, world!" program.

In addition, "Hello, world!" can be a useful sanity test to make sure that a language's compiler, development environment, and run-time environment are correctly installed. Configuring a complete programming toolchain from scratch to the point where even trivial programs can be compiled and run can involve substantial amounts of work. For this reason, a simple program is used first when testing a new tool chain.

A "Hello, world!" program running on Sony's PlayStation Portable as a proof of concept.

"Hello, world!" is also used by computer hackers as a proof of concept that arbitrary code can be executed through an exploit where the system designers did not intend code to be executed—for example, on Sony's PlayStation Portable. This is the first step in using homemade content ("home brew") on such a device.

History
-------

While small test programs existed since the development of programmable computers, the tradition of using the phrase "Hello, world!" as a test message was influenced by an example program in the seminal book The C Programming Language[citation needed]. The example program from that book prints "hello, world" (without capital letters or exclamation mark), and was inherited[citation needed] from a 1974 Bell Laboratories internal memorandum by Brian Kernighan, Programming in C: A Tutorial,[1] which contains the first known version:

```c
main( ) {
        printf("hello, world");
}
```

The C version was adapted[citation needed] from Kernighan's 1972 A Tutorial Introduction to the Language B,[2] where the first known version of the program is found in an example used to illustrate external variables:

```b
main(){
  extrn a,b,c;
  putchar(a); putchar(b); putchar(c); putchar('!*n');
  }

a 'hell';
b 'o, w';
c 'orld';
```

The program prints "hello, world!" on the terminal, including a newline character. The phrase is divided into multiple variables because in B, a character constant is limited to four ASCII characters. The previous example in the tutorial printed "hi!" on the terminal, so the phrase "hello, world!" was originally introduced as a slightly longer greeting that required several character constants for its expression.

It is also claimed that[by whom?] "hello, world" originated instead with BCPL (1967).[3][unreliable source?]This claim is supported by the archived notes of the inventors of BCPL, Prof. Brian Kernighan at Princeton and Martin Richards at Cambridge.[4][unreliable source?]

For modern languages, the hello world program can vary in sophistication. For example, the Go programming language introduced a multilingual hello world program,[5] Sun demonstrated a Java hello world based on scalable vector graphics,[6] and the XL programming language features a spinning Earth hello world using 3D graphics.[7] While some languages such as Python or Ruby may need only a single statement to print "hello world", a low-level assembly language may require dozens of commands.

Variations
----------

There are many variations on the punctuation and casing of the phrase. Variations include the presence or absence of the comma and exclamation mark, and the capitalization of the 'H', both the 'H' and the 'W', or neither. Some languages are forced to implement different forms, such as "HELLO WORLD!", on systems that support only capital letters, while many "hello world" programs in esoteric languages print out a slightly modified string. For example, the first non-trivial Malbolge program printed "HEllO WORld", this having been determined to be good enough.

There are variations in spirit, as well. Functional programming languages, like Lisp, ML and Haskell, tend to substitute a factorial program for Hello World, as functional programming emphasizes recursive techniques, whereas the original examples emphasize I/O, which violates the spirit of pure functional programming by producing side effects. Languages otherwise capable of Hello World (Assembly, C, VHDL) may also be used in embedded systems, where text output is either difficult (requiring additional components or communication with another computer) or nonexistent. For devices such as microcontrollers, field-programmable gate arrays, and CPLD's, "Hello, World" may thus be substituted with a blinking LED, which demonstrates timing and interaction between components.[8][9][10][11][12]

The Debian and Ubuntu Linux distributions provide the "hello world" program through the apt packaging system; this allows users to simply type "apt-get install hello" for the program to be installed, along with any software dependencies. While of itself useless, it serves as a sanity check and a simple example to newcomers of how to install a package. It is significantly more useful for developers, however, as it provides an example of how to create a .deb package, either traditionally or using debhelper, and the version of hello used, GNU Hello, serves as an example of how to write a GNU program.