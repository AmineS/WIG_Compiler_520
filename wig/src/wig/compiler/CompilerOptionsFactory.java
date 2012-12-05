package wig.compiler;

import wig.commons.cli.*;

public class CompilerOptionsFactory
{
    
    private static Option help; 
    private static Option disableWeeder; 
    private static Option prettyPrint; 
    private static Option symbolTablePhase;
    private static Option symbolTablePhaseAndPrinting;
    private static Option typeChecking;
    private static Option typePrinting;
    private static Option codeEmission;
    private static Option urlPrefix;
    private static Option publicHtml;
    
    /**
     * get all options
     * @return options
     */
    public static Options getOptions()
    {
        Options options = new Options();        
        CompilerOptionsFactory.initiateOptions(options);
        return options;
    }

    /**
     * Initialise options
     * @param options
     */
    private static void initiateOptions(Options options)
    {
        // list of all the command line options
        help = new Option("help", false, "prints this message");
        disableWeeder = new Option("dw", false, "disables the weeder");
        prettyPrint = new Option("pp", false, "pretty prints the input");
        symbolTablePhase = new Option("st", false, "performs symbol table phase without printing");
        symbolTablePhaseAndPrinting = new Option("pst", false, "performs symbol table phase and prints symbol tables");
        typeChecking = new Option("tc", false, "performs type checking");
        typePrinting = new Option("tp", false, "pretty prints the types of each expression");
        codeEmission = new Option("cg", false, "generates code");
        urlPrefix = new Option("up", false, "pass the urlprefix e.g:-up localhost:1111.22 ...");        
        publicHtml = new Option("ph", false, "the path to your public_html");
        
        options.addOption(help);
        options.addOption(disableWeeder);
        options.addOption(prettyPrint);        
        options.addOption(symbolTablePhase);
        options.addOption(symbolTablePhaseAndPrinting);
        options.addOption(typeChecking);
        options.addOption(typePrinting);
        options.addOption(codeEmission);
        options.addOption(urlPrefix);
        options.addOption(publicHtml);
    }
}
