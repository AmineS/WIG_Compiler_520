List of Benchmarks that are known to work:

Restrictions:
 - We do not support show/exit statements in functions.
 - Join operations cannot be chained with other joins or any other tuple operations. 
 - “Return” is a keyword. (Note the upper case “R”)
 - “List” and “list” are also keywords.
 - HTML  tags in this format are not accepted: “<[^>]*/>”,
   e.g.: “<input name = “name” type=”text” />”
 - Sessions cannot be named “destroy”.

We can handle tuple ops, comparison and assignment.
We handle string comparison and comparison between string and int.





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
