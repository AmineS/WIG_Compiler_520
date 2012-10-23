import subprocess

def main():
	# run the wig compiler on simple_example.wig with pretty printer and weeder on
	subprocess.call(["java", "-jar", "wig.compile.Compile", "-pp", "simple_example.wig", "commons-cli-1.2.jar", "commons-cli-1.2-javadoc.jar", "commons-cli-1.2-sources.jar"]) 

if __name__=="__main__":
	main()
