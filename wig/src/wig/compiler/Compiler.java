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
import wig.emitter.Emitter;
import wig.lexer.Lexer;
import wig.lexer.LexerException;
import wig.node.Start;
import wig.parser.Parser;
import wig.parser.ParserException;
import wig.prettyprinter.PrettyPrinter;
import wig.prettyprinter.TypePrettyPrinter;

import wig.symboltable.SymbolAnalyzer;
import wig.symboltable.SymbolCollector;

import wig.weeder.Weeder;

import java.util.LinkedList;

import wig.symboltable.SymbolTablePrinter;
import wig.type.TypeChecker;

public class Compiler
{
    private static Options compilerOptions; 
    private static CommandLineParser cliParser;
    private static CommandLine commandLine;
    private static String urlPrefix = "";
    private static String fileName = "";
    private static String pHtml = "";
    
    public static void main(String[] args)
    {             
        try
        {   
            // generate command line argument reader
            compilerOptions = CompilerOptionsFactory.getOptions();
            cliParser = new PosixParser();
            commandLine = cliParser.parse(compilerOptions, args);
               
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
                    if (file.endsWith(".wig"))
                    {
                        fileName = file;
                    }
                    else
                    {
                        if (urlPrefix == "")
                        {
                            urlPrefix = file;
                        }
                        else
                        {
                            pHtml = file;
                        }
                    }
                }
                if (commandLine.hasOption("up") && urlPrefix == "")
                {
                    System.out.println("User asked for Code Generation without providing URL prefix!");
                    System.exit(-1);
                }
                if (commandLine.hasOption("ph") && pHtml == "")
                {
                    System.out.println("User needs to add public html path!");
                    System.exit(-1);
                }
                System.out.println("------- Generating output: " + fileName + "-------");
                compileInput(getFileReader(fileName));
                System.out.println();
            }          
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates a reader for a file and outputs an error with the filename if
     * file does not exist
     * @param filename
     * @return Reader
     */
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
    
    /**
     * Compile input parameters
     * @param inputReader
     * @throws ParserException
     * @throws LexerException
     * @throws IOException
     */
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
            System.out.println("\n..............................................................\nPretty Print:\n");
            PrettyPrinter.print(tree);
            System.out.println("\n\n..............................................................");
        }              
        
        // if symbol table phase was requested, perform symbol table phase
        if(commandLine.hasOption("st") || commandLine.hasOption("pst"))
        {
            System.out.println("\n..............................................................");
            System.out.println("Symbol Table Phase:");
            
            // collect symbols
            System.out.println("\nCollecting Symbols...");
            SymbolCollector symCollector = new SymbolCollector();
            symCollector.collect(tree);
            
            // analyze symbols
            System.out.println("\nAnalyzing Symbols...");
            SymbolAnalyzer symAnalyzer = new SymbolAnalyzer(symCollector.getServiceTable());
            symAnalyzer.analyze(tree);
            
            // print symbol tables if required:
            if (commandLine.hasOption("pst"))
            {
                SymbolTablePrinter stp = new SymbolTablePrinter(symCollector);
                stp.printAll();
            }
            
            // st phase done
            System.out.println("\nSymbol Table Phase Done.");
            System.out.println("..............................................................");
        }

        // if type checking was requested, perform type checking
        if (commandLine.hasOption("tc")  || commandLine.hasOption("tp"))
        {
            System.out.println("\n..............................................................");
            System.out.println("Type Checking Phase:");
            
            // collect symbols
            SymbolCollector symCollector = new SymbolCollector();
            symCollector.collect(tree);
            
            TypeChecker typeChecker = new TypeChecker(symCollector.getServiceTable());
            typeChecker.typeCheck(tree);
            
            System.out.println("\nType Checking Phase Done.");
            System.out.println("..............................................................");

            // if pretty printing with types was requested, pretty print with types
            if (commandLine.hasOption("tp"))
            {
                System.out.println("\n..............................................................\nPretty Print with Types:\n");
                TypePrettyPrinter tpp = new TypePrettyPrinter(typeChecker.getTypeTable());
                tpp.print(tree);
                System.out.println("\n\n..............................................................");
            }
        }
        
        if (commandLine.hasOption("cg"))
        {
            System.out.println("\n..............................................................");
            System.out.println("Symbol Table Phase:");
            
            // collect symbols
            System.out.println("\nCollecting Symbols...");
            SymbolCollector symCollector = new SymbolCollector();
            symCollector.collect(tree);
            
            // analyze symbols
            System.out.println("\nAnalyzing Symbols...");
            SymbolAnalyzer symAnalyzer = new SymbolAnalyzer(symCollector.getServiceTable());
            symAnalyzer.analyze(tree);
            
            // st phase done
            System.out.println("\nSymbol Table Phase Done.");
            System.out.println("..............................................................");

            System.out.println("\n..............................................................");
            System.out.println("Type Checking Phase:");
            
            TypeChecker typeChecker = new TypeChecker(symCollector.getServiceTable());
            typeChecker.typeCheck(tree);
            
            System.out.println("\nType Checking Phase Done.");
            System.out.println("..............................................................");
            
            String temp = fileName.replaceAll("[.]wig", "");
            String [] split = temp.split("/");
            fileName = split[split.length-1];
            
            Emitter em = new Emitter(symAnalyzer.getServiceSymbolTable(), typeChecker.getTypeTable(), urlPrefix, fileName, pHtml);
            em.emit(tree);
        }
    }
}
