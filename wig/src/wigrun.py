import subprocess
import sys

def main():
	cmd = sys.argv[1]

	# check
	if (cmd=="ch"):
		# run the wig compiler on simple_example.wig with pretty printer and weeder on
		subprocess.call(["java", "wig.compiler.Compiler", "-pp", "simple_example.wig", "wall.wig"]) 

	# weed
	elif (cmd=="wd"):
		# run the wig compiler on simple_example.wig with pretty printer and weeder on
		subprocess.call(["java", "wig.compiler.Compiler", "complete_example.wig"]) 

	# pretty print
	elif (cmd=="pp"):
		# run the wig compiler on complete_example.wig with pretty printer
		subprocess.call(["java", "wig.compiler.Compiler", "complete_example.wig", "-pp", "-dw"]) 

	# unit tests
	elif (cmd=="ut"):
		subprocess.call(["java", "-cp", sys.argv[2], "org.junit.runner.JUnitCore", "wig.tests.wigUnitTests"])

	# symbol table
	elif (cmd=="st"):
		subprocess.call(["java", "wig.compiler.Compiler", "simple_example.wig", "-pst"])

	else:
		sys.exit

if __name__=="__main__":
	main()