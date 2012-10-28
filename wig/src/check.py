import subprocess

def main():
	# run the wig compiler on simple_example.wig with pretty printer and weeder on
	subprocess.call(["java", "wig.compiler.Compiler", "-pp", "simple_example.wig", "wall.wig"]) 

if __name__=="__main__":
	main()
