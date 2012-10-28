package wig.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import wig.lexer.Lexer;
import wig.node.Start;
import wig.parser.Parser;
import wig.prettyprinter.PrettyPrinter;
import wig.symboltable.SymbolAnalyzer;
import wig.weeder.Weeder;

public class Compiler
{
    public static void main(String[] args) throws FileNotFoundException
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
            SymbolAnalyzer symAnalyzer = new SymbolAnalyzer();
            symAnalyzer.analyze(tree);
            
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
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
