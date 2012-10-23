import subprocess

def main():
	# run the wig compiler on complete_example.wig with pretty printer
	subprocess.call(["java", "wig.compile.Compile", "complex_example.wig", "-pp", "-dw", "-jar", "commons-cli-1.2.jar", "commons-cli-1.2-javadoc.jar", "commons-cli-1.2-sources.jar"]) 

if __name__=="__main__":
	main()
