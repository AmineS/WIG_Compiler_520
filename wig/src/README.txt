List of Benchmarks that are known to work:
We tested on the 2009 benchmarks. 
Groups which work:
Groups which do not work:

Restrictions:
 - We do not support show/exit statements in functions.
 - Join operations cannot be chained with other joins or any other tuple operations. 
 - “Return” is a keyword. (Note the upper case “R”)
 - “List” and “list” are also keywords.
 - HTML  tags in this format are not accepted: “<[^>]*/>”,
   e.g.: “<input name = “name” type=”text” />”
 - Sessions cannot be named “destroy”.

Apart from the above restrictions, everything else is handled.
For more information, read our report.

COMPILE, INSTALL and RUN WIG programs:
 - compile: java wig.compiler.Compiler -cg 
 	OR: 
 	
 - run: 




Makefile:
	Provides all options required.

wigrun.py:
	Used by Makefile

tester_files:
	.wig files
		Used for general tests

	/symbol_table_testers
		Wig scripts to test symbol table phase and print all error messages

	/type_checker_testers
		Wig scripts for testing typechecker, typeprettyprinter and displaying error messages
