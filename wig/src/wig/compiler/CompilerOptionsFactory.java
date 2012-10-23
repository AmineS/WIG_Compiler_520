package wig.compiler;

import org.apache.commons.cli.*;

public class CompilerOptionsFactory
{
    
    private static Option help; 
    private static Option disableWeeder; 
    private static Option prettyPrint; 
    
    
    public static Options getOptions()
    {
        // get all the options
        Options options = new Options();        
        CompilerOptionsFactory.initiateOptions(options);
        
        return options;
    }
    
    private static void initiateOptions(Options options)
    {
        // list of all the command line options
        help = new Option("help", false, "prints this message");
        disableWeeder = new Option("dw", false, "disables the weeder");
        prettyPrint = new Option("pp", false, "pretty prints the input");
        
        options.addOption(help);
        options.addOption(disableWeeder);
        options.addOption(prettyPrint);        
    }
}
