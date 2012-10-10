import java.util.Random;
import java.util.Vector;
import joos.lib.*;

public class ShellingSimulation
{
    
    //Simulation Parameters
    protected int toleranceLevel;
    protected int gridSize;
    protected int population;
    protected int simulationSpeed;
    protected int entriesCount;
    
    // Constants
    protected int  ERROR;
    protected Character EMPTY; 
    protected Character TYPE1;
    protected Character TYPE2;
    protected char DELIMITER;
    protected int RANDOM_NUMBER_RANGE;
    protected int ONE_SECOND_SPEED;
   
    //Simulation State 
    protected SimulationGrid grid;
    protected Vector cornerToleranceLevels; 
    protected Vector nonCornerToleranceLevels; 

    
    /*
     * Constructor
     */
    public ShellingSimulation(
     	int toleranceLevel_, 
        int gridSize_, 
    	int population_, 
    	int simulationSpeed_,
    	SimulationGrid grid_
	)
    {
	    super(); 

    	//initialize constants 
	    ERROR = 1; 
	    EMPTY = new Character('.');
        TYPE1 = new Character('*');    
        TYPE2 = new Character('@');
        DELIMITER = ' ';
	    RANDOM_NUMBER_RANGE = 100;
    	ONE_SECOND_SPEED = 1000;

	    //set parameters 
        toleranceLevel = toleranceLevel_;
        gridSize = gridSize_;
        population = population_;
        simulationSpeed = simulationSpeed_;
        
      //run initializers
        if(grid_ == null)
        {
            grid = new SimulationGrid(gridSize, gridSize); 
             
            this.initializeEmptyGrid();            
            this.initializeGridEntries(population);
        }
        else
        {
            grid = grid_;
        }
        this.initializeToleranceLevelVectors();
    }
        
    /*
     * Simulate function
     */
    public void simulate()
    {
        //output grid
        grid.printGrid(DELIMITER);
        
        //move unhappy people 
	    this.updateLocations();
	    
	    //wait a specified amount of time 
        this.sleep();
    }
    
    public void updateLocations()
    {
		int i; 
		int j;
        Character _c; 
        char _charValue;         

        
        //traverse the grid 
        for(i=0; i < gridSize; i++)
        {
            for(j=0; j < gridSize; j++)
            {                
                _c = grid.get(i,j); 

                
                //find unhappy people 
                if(
                    _c != null  && 
                    !_c.equals(EMPTY) && 
                    !this.isToleranceLevelMet(i, j, _c.charValue())
                    )
                {
                    //relocate them 
                    this.relocate(i, j);                    
                }
            }
        }
    }
    
    /*
     * Makes the program wait the specified amount of time 
     */
    public void sleep()
    {
        //JOOS Wrappers
        Thread _t1;
        JoosThread _thread; 
        
        _t1 = new Thread();          
        _thread = new JoosThread(_t1);
        
        //sleep the current thread
        _thread.currentThread(); 
        _thread.sleep(simulationSpeed * ONE_SECOND_SPEED);
    }
    
    /*
     * Move Unhappy people
     */
    public void relocate(int row_, int col_)
    {
        boolean _toleranceMetInNewLocation;
        boolean _isFallbackRandomLocationSet; 
        
        int _fallbackRandomLocationRow;
        int _fallbackRandomLocationCol;
        int i;
        int j; 
        
        Character _c;
        Character _c1; 
        Character _type; 

        _toleranceMetInNewLocation = false;  
        _isFallbackRandomLocationSet = false; 
        _fallbackRandomLocationRow = 0; 
        _fallbackRandomLocationCol = 0;

        // New location should meet tolerance level
        for(i=0; i < gridSize; i++)
        {
            for(j=0; j < gridSize; j++)
            {
                _c = grid.get(i,j);
                
                if(!_isFallbackRandomLocationSet)
                {
                    _fallbackRandomLocationRow = i;
                    _fallbackRandomLocationCol = j;
                    _isFallbackRandomLocationSet = true;
                }
        
                if(this.isToleranceLevelMet(i, j, _c.charValue()))
                {
                    _c1  = grid.get(row_, col_);               
                    grid.set(i, j, _c1);
                    grid.set(row_, col_, _c);
                    _toleranceMetInNewLocation = true;
                    return;
                }
            }
        }
        
        // Fallback random location
        _type = grid.get(row_, col_);
        grid.set(_fallbackRandomLocationRow, _fallbackRandomLocationCol, _type);
        grid.set(row_, col_, EMPTY);
    }

    /*
     * Checks all neighbours for tolerance level
     */
    public boolean isToleranceLevelMet(int row_, int col_, char type_)
    {
        int _countSimilar;
        int _countNeighbours;
        int _equivalenceToleranceLevels;
    	Character _neighbour;
	
        _countSimilar = 0; 
        _countNeighbours = 0;
        _equivalenceToleranceLevels = 0;
        
        if(row_ >= 1)
        {
           // left top
           if(col_ >= 1)
           {          
	           _neighbour = grid.get(row_-1, col_-1);
               _countNeighbours++;

               if(type_ == _neighbour.charValue())
               {
                   _countSimilar++;
               }
           }
           
           // center top
           _neighbour = grid.get(row_-1, col_); 
           if(type_ == _neighbour.charValue())
           {    
               _countNeighbours++;
               _countSimilar++;
           }
           
           // Right top
           if(col_ <= (gridSize-2))
           {
                _neighbour = grid.get(row_-1, col_+1); 
                _countNeighbours++;

               if(type_ == _neighbour.charValue())
               {
                   _countSimilar++;
               }
           }
        }
        
        // Left 
        if(col_ >= 1)
        {
            _neighbour = grid.get(row_, col_ -1 );  
            _countNeighbours++;
            
            if(type_ == _neighbour.charValue())
            {
                _countSimilar++;
            }
        }
        
        // Right
        if(col_ <= (gridSize-2))
        {
            _neighbour = grid.get(row_, col_+1); 
            _countNeighbours++;
            
            if(type_ == _neighbour.charValue())
            {
                _countSimilar++;
            }
        }

        if(row_ <= (gridSize-2))
        {
            // Bottom left
            if(col_ >= 1)
            {
                _neighbour = grid.get(row_+1, col_-1); 
                _countNeighbours++;
                                
                if(type_ == _neighbour.charValue())
                {
                    _countSimilar++;
                }
            }
           
            // Bottom
            _neighbour = grid.get(row_+1, col_);  
            _countNeighbours++;                        

            if(type_ == _neighbour.charValue())
            {
                _countSimilar++;
            }
            
            // Bottom Right
            if(col_ <= gridSize-2)
            {
                _neighbour = grid.get(row_+1, col_+1); 
                _countNeighbours++;
                
                if(type_ == _neighbour.charValue())
                {
                    _countSimilar++;
                }
            }
        }
        
        //determines tolerance 
        if(_countNeighbours == 3 || _countNeighbours == 5)
        {
            _equivalenceToleranceLevels = this.getEquivalentToleranceLevel(_countNeighbours);
        }
        else 
        {
            _equivalenceToleranceLevels = toleranceLevel; 
        }
                
        if(_countSimilar >= _equivalenceToleranceLevels) 
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /*
     * Determines the tolerance equivalence 
     */
    public int getEquivalentToleranceLevel(int countNeighbours_)
    {
        Integer _value; 
        if(countNeighbours_ == 3)
        {
            _value = (Integer) cornerToleranceLevels.elementAt(toleranceLevel);
            return _value.intValue();
        }
        else
        {
            _value = (Integer) nonCornerToleranceLevels.elementAt(toleranceLevel);
            return _value.intValue();
        }
    }
    
    /*
     * Initializes the grid to empty value 
     */
    public void initializeEmptyGrid()
    {
        int i;
        int j; 
        
        for(i=0; i < gridSize; i++)
        {
            for(j=0; j < gridSize; j++)
            {
                grid.set(i, j, EMPTY);
            }
        }        
    }

    /*
     * Generates the tolerance vectors 
     */
    public void initializeToleranceLevelVectors()
    {                 
        Integer _zeroLevel;
        Integer _firstLevel; 
        Integer _secondLevel; 
        Integer _thirdLevel;         
        Integer _fourthLevel; 
        Integer _fifthLevel; 
        
        _zeroLevel = new Integer(0);
        _firstLevel = new Integer(1); 
        _secondLevel = new Integer(2);
        _thirdLevel = new Integer(3);
        _fourthLevel = new Integer(4); 
        _fifthLevel = new Integer(5);
        
        cornerToleranceLevels = new Vector(); 
        nonCornerToleranceLevels = new Vector();
        
        //non-corner cases 
        nonCornerToleranceLevels.insertElementAt(_zeroLevel, 0);
        nonCornerToleranceLevels.insertElementAt(_firstLevel, 1);
        nonCornerToleranceLevels.insertElementAt(_firstLevel, 2);                
        nonCornerToleranceLevels.insertElementAt(_secondLevel, 3);        
        nonCornerToleranceLevels.insertElementAt(_secondLevel, 4);        
        nonCornerToleranceLevels.insertElementAt(_thirdLevel, 5);
        nonCornerToleranceLevels.insertElementAt(_thirdLevel, 6);        
        nonCornerToleranceLevels.insertElementAt(_fourthLevel, 7);        
        nonCornerToleranceLevels.insertElementAt(_fifthLevel, 8);        
        
        //corner cases 
        cornerToleranceLevels.insertElementAt(_zeroLevel, 0);
        cornerToleranceLevels.insertElementAt(_firstLevel, 1);
        cornerToleranceLevels.insertElementAt(_firstLevel, 2);
        cornerToleranceLevels.insertElementAt(_firstLevel, 3);        
        cornerToleranceLevels.insertElementAt(_secondLevel, 4);
        cornerToleranceLevels.insertElementAt(_secondLevel, 5);
        cornerToleranceLevels.insertElementAt(_secondLevel, 6);        
        cornerToleranceLevels.insertElementAt(_thirdLevel, 7);
        cornerToleranceLevels.insertElementAt(_thirdLevel, 8);                
    }
        
    /*
     * Creates a randomly distributed grid  
     */
    public void initializeGridEntries(int populationLeft_)
    {
        Random _random; 
        int i; 
        int j; 
        int _randomInteger; 
        Character _c; 
 
        //seed random and set population count 
        _random = new Random();
        entriesCount = populationLeft_;
        
        while(entriesCount > 0)
        {
            for(i=0; i < gridSize; i++)
            {
                for(j=0; j < gridSize; j++)
                {
                    _c = grid.get(i, j); 
                    if(_c.equals(EMPTY))
                    {
                        _randomInteger = _random.nextInt(50);
                        _randomInteger = _randomInteger % 3; 
                        
                        if(_randomInteger > 0)
                        {
                            if(_randomInteger == 1)
                            {
                                 grid.set(i, j, TYPE1);
                            }
                            else
                            {
                                grid.set(i, j, TYPE2);
                            }            
                            entriesCount = entriesCount - 1;
                        }
                    }
                }
            }
        }               
    }
}
