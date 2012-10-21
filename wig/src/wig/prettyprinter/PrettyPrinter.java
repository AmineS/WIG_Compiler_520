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
          puts(" <[");
          node.getIdentifier().apply(this);
          puts("]>");
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
          puts("<");
          node.getInput().apply(this);          
          for(PInputattr inputAttr : node.getInputattr())
          {
              inputAttr.apply(this);
          }          
          puts("/>");
      }
      
      public void caseASelectHtmlbody(ASelectHtmlbody node)
      {
          puts("<");
          node.getSelectTag().apply(this);                        
          for(PInputattr attr : node.getInputattr())
          {
              attr.apply(this);
          }
          puts(">");   
          
          for(PHtmlbody htmlBody : node.getHtmlbody())
          {
              htmlBody.apply(this);
          }
          
          puts("</");
          node.getSelectTag().apply(this);
          puts(">");
      }
      
      public void caseANameInputattr(ANameInputattr node)
      {
          puts(" name=\""); 
          if(node.getAttr() != null)
          {
              node.getAttr().apply(this);              
          }
          puts("\"");
      }      
      @Override
      public void caseATypeInputattr(ATypeInputattr node)
      {
          puts(" type=\"");
          if(node.getInputtype() != null)
          {
              node.getInputtype().apply(this);
          }
          puts("\"");
      }      
            
      public void caseAAttributeInputattr(AAttributeInputattr node)
      {
          if(node.getAttribute() != null)
          {
              node.getAttribute().apply(this);
          }
      }
      
      public void caseATexttypeInputtype(ATexttypeInputtype node)
      {
          if(node.getText() != null)
          {
              node.getText().apply(this);
          }
      }
      
      public void caseARadiotypeInputtype(ARadiotypeInputtype node)
      {
          if(node.getRadio() != null)
          {
              node.getRadio().apply(this);
          }
      }
      
      public void caseAStrtypeInputtype(AStrtypeInputtype node)
      {
          if(node.getStringconst() != null)
          {
              node.getStringconst().apply(this);
          }
      }
      
      public void caseAAttrAttribute(AAttrAttribute node)
      {
          if(node.getAttr() != null)
          {
              node.getAttr().apply(this);
          }
      }           
      
      public void caseAAssignAttribute(AAssignAttribute node)
      {
          if(node.getLeftAttr() != null)
          {
              node.getLeftAttr().apply(this);
          }
                    
          if(node.getLeftAttr() != null && node.getRightAttr() != null)
          {
              puts("=");
          }
          
          if(node.getRightAttr() != null)
          {
              node.getRightAttr().apply(this);
          }          
      }
      
      public void caseAIdAttr(AIdAttr node)
      {
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
      
      public void caseAStrAttr(AStrAttr node)
      {          
          if(node.getStringconst() != null)
          {
              node.getStringconst().apply(this);
          }          
      }
      
      public void caseAIconstAttr(AIconstAttr node)
      {
          if(node.getIntconst() != null)
          {
              node.getIntconst().apply(this);
          }
      }
      
      
      public void caseANegintIntconst(ANegintIntconst node)
      {
          if(node.getNegIntconst() != null)
          {
              node.getNegIntconst().apply(this);
          }
      }
      
      public void caseAPosintIntconst(APosintIntconst node)
      {
          if(node.getPosIntconst() != null)
          {
              node.getPosIntconst().apply(this);
          }
      }
            
      public void caseASchema(ASchema node)
      {
          puts("schema \n{\n");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
          
          List<PField> fields= node.getField();
          
          if(fields != null)
          {
              for(PField field : fields)
              {
                  field.apply(this);
              }
          }
          puts("}\n");
      }
      
      public void caseAField(AField node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          
          puts(" ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }          
          puts(";\n");
      }
            
      public void caseAVariable(AVariable node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          
          puts(" = ");
          
          List<TIdentifier> ids = node.getIdentifier();          
          if(ids!=null)
          {              
              for(TIdentifier id : ids)
              {
                  id.apply(this);
                  puts(" ");
              }
          }          
          puts(";\n");
      }
            
      public void caseAIdentifiers(AIdentifiers node)
      {          
          List<TIdentifier> ids = node.getIdentifier();          
          if(ids!=null)
          {              
              for(TIdentifier id : ids)
              {
                  id.apply(this);
                  puts(" ");
              }
          }          
      }

      public void caseAIntType(AIntType node)
      {
          if(node.getInt() != null)
          {
              node.getInt().apply(this);
          }
      }
      
      public void caseABoolType(ABoolType node)
      {
          if(node.getBool() != null)
          {
              node.getBool().apply(this);
          }
      }
      public void caseAStringType(AStringType node)
      {
          if(node.getString() != null)
          {
              node.getString().apply(this);
          }
      }
      public void caseAVoidType(AVoidType node)
      {
          if(node.getVoid() != null)
          {
              node.getVoid().apply(this);
          }
      }
      public void caseASimpleType(ASimpleType node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
      }
      
      public void caseATupleType(ATupleType node)
      {
          puts("tuple ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
      
      public void caseAFunction(AFunction node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          puts(" ");
          
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
          puts("(");
          
          List<PArgument> arguments = node.getArgument();
          if(arguments != null)
          {          
              for(PArgument argument : arguments)
              {
                  argument.apply(this);
                  puts(" ");
              }
          }          
          puts(")\n{\n");
          
          if(node.getCompoundstm() != null)
          {
              node.getCompoundstm().apply(this);
          }
          puts("\n}\n");
      }
            
      public void caseAArguments(AArguments node)
      {
          List<PArgument> arguments = node.getArgument();
          if(arguments != null)
          {          
              for(PArgument argument : arguments)
              {
                  argument.apply(this);
                  puts(" ");
              }
          }    
      }
      
      public void caseAArgument(AArgument node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          puts(" ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
           
      public void caseASession(ASession node)
      {
          puts("session" );
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
          puts("()\n{\n");
          if(node.getCompoundstm() != null)
          {
              node.getCompoundstm().apply(this);
          }
          puts("\n}\n");
      }
}
