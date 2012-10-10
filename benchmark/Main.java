import joos.lib.*; 

public class Main
{
    public Main(){ super(); }

	public static void main(String args[]) 
	{	    
	    //Declarations
	    JoosIO _reader;
        ShellingSimulation _shellingSimulation;     
        SimulationGrid _grid;
        int _toleranceLevel;
        int _gridSize;  
        int _population;  
        int _simulationSpeed;
        int i; 
        int j;
        int _nextCharacterASCII; 
        Character _c;
        boolean _generateRandom;
        
        _reader = new JoosIO();
        _generateRandom =  _reader.readBoolean();        
     
        // if the random generation flag, the first value in the input file 
        // is true then generate a random grid with the given parameters
        // otherwise load the grid from the file 
        if(_generateRandom)
        {            
            //initialize simulation parameters
            _toleranceLevel = 3;
            _gridSize = 15;
            _population = 100;
            _simulationSpeed = 1;        
            
            //initialize the simulation
            _shellingSimulation = new ShellingSimulation(
                    _toleranceLevel,
                    _gridSize, 
                    _population,
                    _simulationSpeed,
                    null
                    ); 
        }
        else
        {
            //read values in for control parameters                          
            _toleranceLevel = _reader.readInt();
            _gridSize = _reader.readInt();
            _population = _reader.readInt();
            _simulationSpeed = _reader.readInt();
            
            //read grid in 
            _grid = new SimulationGrid(_gridSize, _gridSize);                        
            for(i=0; i < _gridSize; i++)
            {
                for(j=0; j < _gridSize; j++)
                {
                    // the grid should be in ACII ints and cast them to character
                    _nextCharacterASCII = _reader.readInt();
                    _c = new Character((char) _nextCharacterASCII);
                    _grid.set(i, j, _c);
                }
            }  
            
            //initialize simulation to the new grid 
            _shellingSimulation = new ShellingSimulation(
                    _toleranceLevel,
                    _gridSize, 
                    _population,
                    _simulationSpeed,
                    _grid
                    ); 
        }             
        
        //start the simulation 
        while(true)
        {
            _shellingSimulation.simulate(); 
        }        
	}
}
