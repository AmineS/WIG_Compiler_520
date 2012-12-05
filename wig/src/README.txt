List of Benchmarks that are known to work:
2009:
groups 2, 3, 4, 5, 7
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

Some benchmarks have html constants named with End or List these have to be changed, php doesn't allow it.
So we changed them so that there's no conflict with php and added them to the repo so that you can see it works.
It is called Benchmark2009.zip

COMPILE, INSTALL and RUN WIG programs:

 - compile: java wig.compiler.Compiler wig_file.wig -cg -up <URL> -ph <PUBLIC_HTML_PATH>
	where <URL> is your base url e.g: mine is "/~dbhage/"
			<PUBLIC_HTML_PATH> is the path to your public_html, e.g.: mine is "/home/2010/dbhage/public_html/"
	
	IMPORTANT: please do the -up part before the -ph!!!
 	
 - run: In your browser, put: "www.cs.mcgill.ca/~dbhage/wig_file.php?session=SESSION_NAME"
 	where SESSION_NAME is the session you want to start at.
	where ~dbhage needs to be replaced by your SOCS username

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
