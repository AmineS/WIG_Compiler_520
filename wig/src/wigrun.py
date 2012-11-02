import subprocess
import sys
import os

def main():
	cmd = sys.argv[1]

	# check
	if (cmd=="ch"):
		# run the wig compiler on simple_example.wig with pretty printer and weeder on
		subprocess.call(["java", "wig.compiler.Compiler", "-pp", "-pst", "tester_files/wall.wig"]) 

	# weed
	elif (cmd=="wd"):
		# run the wig compiler on simple_example.wig with pretty printer and weeder on
		subprocess.call(["java", "wig.compiler.Compiler", "tester_files/complete_example.wig"]) 

	# pretty print
	elif (cmd=="pp"):
		# run the wig compiler on complete_example.wig with pretty printer
		subprocess.call(["java", "wig.compiler.Compiler", "tester_files/complete_example.wig", "-pp", "-dw"]) 

	# unit tests
	elif (cmd=="ut"):
		subprocess.call(["java", "-cp", sys.argv[2], "org.junit.runner.JUnitCore", "wig.tests.wigUnitTests"])

	# symbol table
	elif (cmd=="st"):
		tester_files = os.listdir("tester_files/symbol_table_testers/")
		i=0
		for fname in tester_files:
			if (fname.endswith(".wig")==0):
				continue
			print "Test" + str(i)
			subprocess.call(["java", "wig.compiler.Compiler", "tester_files/symbol_table_testers/" + fname, "-pst", "-dw"])
			print "\n\n"
			i=i+1

		print "All wig files which are being tested have a distinct error",
		print "apart from good.wig, which also tests SymbolTablePrinter by",
		print "printing the symbol tables."

	elif (cmd=="tc"):
		print "type checking"


	else:
		sys.exit(1)

if __name__=="__main__":
	main()
