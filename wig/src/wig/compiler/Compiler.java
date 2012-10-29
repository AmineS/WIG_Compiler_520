package wig.compiler;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

import wig.commons.cli.CommandLine;
import wig.commons.cli.CommandLineParser;
import wig.commons.cli.HelpFormatter;
import wig.commons.cli.Options;
import wig.commons.cli.PosixParser;
import wig.lexer.Lexer;
import wig.lexer.LexerException;
import wig.node.Start;
import wig.parser.Parser;
import wig.parser.ParserException;
import wig.prettyprinter.PrettyPrinter;

import wig.symboltable.SymbolAnalyzer;
import wig.symboltable.SymbolCollector;

import wig.weeder.Weeder;

import java.util.LinkedList;

import wig.symboltable.SymbolTablePrinter;

public class Compiler
{
    private static Options compilerOptions; 
    private static CommandLineParser cliParser;
    private static CommandLine commandLine;
    
    public static void main(String[] args)
    {             
        try
        {
            File inputFile = new File("simple_example.wig");
            FileReader inputReader = new FileReader(inputFile);
            
            Parser p = 
                    new Parser (
                      new Lexer (
                         new PushbackReader(inputReader, 1024)));
                 
            Start tree = p.parse();

            SymbolCollector symCollector = new SymbolCollector();

            symCollector.analyze(tree);
            
            System.out.println("LinkedList size: " + symCollector.getSymbolTables().size());
            
            SymbolAnalyzer symAnalyzer = new SymbolAnalyzer(symCollector.getServiceTable());
            
            symAnalyzer.analyze(tree);
            
            
            //SymbolTablePrinter stp = new SymbolTablePrinter(symAnalyzer);
            //stp.printAll();
           
            
//            //Weeder.weed(tree);
//            PrettyPrinter.print(tree);
////            
////            File inputFile2 = new File("testingpppppp.txt");
////            FileReader inputReader2 = new FileReader(inputFile2);
////            
////            Parser p2 = 
////                    new Parser (
////                      new Lexer (
////                         new PushbackReader(inputReader2, 1024)));
////                 
////            Start tree2 = p2.parse();
////            PrettyPrinter.print(tree2);
//            
            // generate command line argument reader
            compilerOptions = CompilerOptionsFactory.getOptions();
            cliParser = new PosixParser();
            commandLine = cliParser.parse(compilerOptions, args);
               /*
            String[] files = commandLine.getArgs();
                        
            if(commandLine.hasOption("help"))
            {
                // print help and suppress all other commands
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("wig", compilerOptions);
            }
            else if(files == null || files.length == 0)
            {
                // if no files given as input then use stdin
                compileInput(new InputStreamReader(System.in));
            }
            else
            {
                // compile each file
                for(String file : files)
                {
                    System.out.println("-------Generating output: " + file +"-------");
                    compileInput(getFileReader(file));
                    System.out.println();
                }
            }    */                  
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    

    // Generates a reader for a file and ouputs an error with the filename if
    // file not found
    private static Reader getFileReader(String filename)
    {       
        File file;
        Reader reader = null;
        
        try
        {
            file = new File(filename);
            reader = new FileReader(file);           
        }
        catch(FileNotFoundException e)
        {
            System.out.println("The following file was not found: " + filename);
            System.exit(-1);
        }        
        return reader;
    }
    
    private static void compileInput(Reader inputReader) throws ParserException, LexerException, IOException
    {
        // parse
        Parser p = 
                new Parser (
                  new Lexer (
                     new PushbackReader(inputReader, 1024)));                 
        Start tree = p.parse();
        
        // if weeding isn't suppressed then weed 
        if(!commandLine.hasOption("dw"))
        {
            Weeder.weed(tree);
        }
        
        // if pretty printing was requested then pretty print
        if(commandLine.hasOption("pp"))
        {
            PrettyPrinter.print(tree);
        }                        
    }
}
