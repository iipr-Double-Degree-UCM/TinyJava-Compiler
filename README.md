# TinyJava-Compiler
Compiler for a language named TinyJava, that looks like Java but that in no case achieves all of its potential.

A compiler combines different phases in which several analysis are performed, each of them relying on the previous ones. It begins by analyzing the words that form the given code, and it associates to each of them different lexical units. In order to do this, we use JFlex. Afterwards, it ensures that the sentences are well formed by means of a context-free grammar aided by CUP. The next analysis verifies identifier restrictions of the variables and performs a type analysis. Lastly, it generates portable code that can be executed on a p-machine.

Author: Ignacio Iker Prado Rujas.

Languages: Java, JFlex, CUP.
