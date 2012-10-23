package wig.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.PushbackReader;

import wig.lexer.Lexer;
import wig.node.Start;
import wig.parser.Parser;
import wig.weeder.Weeder;
import wig.prettyprinter.*;

public class Compiler
{
    public static void main(String[] args) throws FileNotFoundException
    {
        try
        {
            File inputFile = new File("complete_example.wig");
            FileReader inputReader = new FileReader(inputFile);
            
            Parser p = 
                    new Parser (
                      new Lexer (
                         new PushbackReader(inputReader, 1024)));
                 
            Start tree = p.parse();
            
            Weeder.weed(tree);
            PrettyPrinter.print(tree);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
