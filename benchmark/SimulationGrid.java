import java.util.Vector; 
import joos.lib.*; 

public class SimulationGrid  
{
    protected int xLength; 
    protected int yLength; 
    protected Vector grid; 

    /*
     * Initializes a 2D vector grid 
     */
    public SimulationGrid(int xLength_, int yLength_)
    {
        super(); 
        int i; 

        xLength = xLength_; 
        yLength = yLength_; 

        grid = new Vector(xLength); 
        for(i = 0; i < yLength; i++)
        {
            grid.addElement(new Vector(yLength));
        }        
    }

    /*
     * get X length 
     */
    public int getXLength() 
    {
        return xLength;    
    } 

    /*
     * get y length 
     */
    public int getYLength() 
    {
        return yLength; 
    }

    /*
     * return the value at the given coorindates 
     */
    public Character get(int x_, int y_)
    {
        Vector _xVector; 
        _xVector =  (Vector)grid.elementAt(x_);
        return  (Character)_xVector.elementAt(y_); 
    }

    /*
     * set the value at the given coordinates 
     */
    public void set(int x_, int y_, Character c_)
    {
        Vector _xVector; 
        _xVector = (Vector)grid.elementAt(x_); 
        _xVector.insertElementAt(c_, y_); 
    }
    
    /*
     * Output the grid to standard out with the given 
     * delimiter 
     */
    public void printGrid(char delimiter_)
    {
        JoosIO _printer;
        int i; 
        int j; 
        Character _c; 
        
        _printer = new JoosIO(); 
        
        for (i=0; i < xLength; i++)
        {
            for(j=0; j  < yLength; j++)
            {
                _c = this.get(i,j);              
                _printer.print("" + _c.charValue() + delimiter_); 
            }                               
            _printer.print("\n");
        }
        
        _printer.println("");
    } 
}
