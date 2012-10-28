import subprocess

def main():
	# run the wig compiler on complete_example.wig with pretty printer
	subprocess.call(["java", "wig.compiler.Compiler", "complete_example.wig", "-pp", "-dw"]) 

if __name__=="__main__":
	main()
