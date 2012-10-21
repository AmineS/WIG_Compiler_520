package wig.prettyprinter;

import java.util.*;

import wig.parser.*;
import wig.lexer.*;
import wig.node.*;
import wig.analysis.*;


public class PrettyPrinter extends DepthFirstAdapter
{
    private int tabCount = 0;
    
    public static void print(Node node)
    {
        node.apply(new PrettyPrinter());
    }
    
    private void puts(String s)
    {
        System.out.print(s);
        System.out.flush();
    }
    
/*    private void printTab()
    {
        for(int i=0; i<tabCount; ++i)
        {
            System.out.print("    ");
        }
    }
    private void incrementTabCount()
    {
        ++tabCount;
    }
    
    private void decrementTabCount()
    {
        --tabCount;
    }*/
    public void caseAService(AService node)
    {
        puts("Service \n{\n");
        
        for(PHtml html : node.getHtml())
        {
            html.apply(this);
        }
        
        for(PSchema schema : node.getSchema())
        {
            schema.apply(this);
        }
        
        for(PVariable variable : node.getVariable())
        {
            variable.apply(this);
        }
        
        for(PFunction function : node.getFunction())
        {
            function.apply(this);
        }
        
        for(PSession session : node.getSession())
        {
            session.apply(this);
        }
        
        puts("\n}");
    }
    
    public void caseAHtml(AHtml node)
    {
        for(PHtmlbody htmlbody : node.getHtmlbody())
        {
            htmlbody.apply(this);
        }
    }
    
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {
        puts("const html");
        
        node.getIdentifier().apply(this);
        
        puts("= <html> ");
        
        for(PAttribute attribute : node.getAttribute())
        {
            attribute.apply(this);
        }
    }
    
      public void caseATagEndHtmlbody(ATagEndHtmlbody node)
      {
          puts(" </html>;\n");
      }
      
      
      public void caseAHoleHtmlbody(AHoleHtmlbody node)
      {
          node.getIdentifier().apply(this);
      }
      
      public void caseAWhateverHtmlbody(AWhateverHtmlbody node)
      {
          node.getWhatever().apply(this);
      }
      
      public void caseAMetaHtmlbody(AMetaHtmlbody node)
      {
          puts("<!--");
          
          node.getMeta().apply(this);
          
          puts("-->");
      }
      
      public void caseAInputHtmlbody(AInputHtmlbody node)
      {
          node.getInput().apply(this);
          
          for(PInputattr inputAttr : node.getInputattr())
          {
              inputAttr.apply(this);
          }
          
          puts("/>");
      }
      
      public void caseASelectHtmlbody(ASelectHtmlbody node)
      {
          node.getSelectTag().apply(this);
      }
      

}
