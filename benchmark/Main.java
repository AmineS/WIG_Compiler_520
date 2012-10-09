import joos.lib.*; 

public class Main
{
    public Main(){ super(); }

	public static void main(String args[]) 
	{
	    
	    //Declarations 
        ShellingSimulation _shellingSimulation;     
        int _toleranceLevel;
        int _gridSize;  
        int _population;  
        int _simulationSpeed;
        
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
                _simulationSpeed
                ); 
             
        
        //start the simulation 
        while(true)
        {
            _shellingSimulation.simulate(); 
        }
        
	}

}
