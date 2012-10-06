import java.util.Random;


public class ShellingSimulation
{
    public ShellingSimulation(int toleranceLevel, int gridSize, int population, int simulationSpeed)
    {
        fToleranceLevel = toleranceLevel;
        fGridSize = gridSize;
        fPopulation = population;
        fSimulationSpeed = simulationSpeed;
        fGrid = new char[fGridSize][fGridSize];
        initializeEmptyGrid();
        initializeGridEntries(fPopulation);
    }
        
    public void simulate()
    {
        printGrid();
        updateLocations();
        sleep();
    }
    
    private void updateLocations()
    {
        for(int i=0; i < fGridSize; i++)
        {
            for(int j=0; j < fGridSize; j++)
            {
                if(fGrid[i][j] != EMPTY && !isToleranceLevelMet(i, j, fGrid[i][j]))
                {
                    relocate(i, j);
                }
            }
        }
    }
    
    private void sleep()
    {
        try
        {
            Thread.currentThread();
            Thread.sleep(fSimulationSpeed * ONE_SECOND_SPEED);
        } catch (InterruptedException e)
        {
            // Vomit
            e.printStackTrace();
        
        }
    }
    
    private void relocate(int row, int col)
    {
        boolean toleranceMetInNewLocation = false;
        boolean isFallbackRandomLocationSet = false;
        int fallbackRandomLocationRow = 0;
        int fallbackRandomLocationCol = 0;

        // New location should meet tolerance level
        for(int i=0; i < fGridSize; i++)
        {
            for(int j=0; j < fGridSize; j++)
            {
                if(!isFallbackRandomLocationSet && fGrid[i][j] == EMPTY)
                {
                    fallbackRandomLocationRow = i;
                    fallbackRandomLocationCol = j;
                    isFallbackRandomLocationSet = true;
                }
                if(fGrid[i][j] == EMPTY && (isToleranceLevelMet(i, j, fGrid[i][j])))
                {
                    char type = fGrid[row][col];
                    fGrid[i][j] = type;
                    fGrid[row][col] = EMPTY;
                    toleranceMetInNewLocation = true;
                    return;
                }
            }
        }
        
        // Fallback random location
        char type = fGrid[row][col];
        fGrid[fallbackRandomLocationRow][fallbackRandomLocationCol] = type;
        fGrid[row][col] = EMPTY;
    }
    private boolean isToleranceLevelMet(int i, int j, char type)
    {
        int countSimilar = 0;
        int countNeighbours = 0;
        
        if(i >= 1)
        {
            
           // left top
           if(j >= 1)
           {
               if(fGrid[i-1][j-1] != EMPTY)
               {
                   countNeighbours++;
               }
               if(type == fGrid[i-1][j-1])
               {
                   countSimilar++;
               }
           }
           
           // center top
           if(type == fGrid[i-1][j])
           {
               if(fGrid[i-1][j] != EMPTY)
               {
                   countNeighbours++;
               }
               countSimilar++;
           }
           
           // Right top
           if(j <= (fGridSize-2))
           {
               if(fGrid[i-1][j+1] != EMPTY)
               {
                   countNeighbours++;
               }
               if(type == fGrid[i-1][j+1])
               {
                   countSimilar++;
               }
           }
        }
        
        // Left 
        if(j >= 1)
        {
            if(fGrid[i][j-1] != EMPTY)
            {
                countNeighbours++;
            }
            if(type == fGrid[i][j-1])
            {
                countSimilar++;
            }
        }
        
        // Right
        if(j <= (fGridSize-2))
        {
            if(fGrid[i][j+1] != EMPTY)
            {
                countNeighbours++;
            }
            if( type == fGrid[i][j+1])
            {
                countSimilar++;
            }
        }
        if(i <= (fGridSize-2))
        {
            // Bottom left
            if(j >= 1)
            {
                if(fGrid[i+1][j-1] != EMPTY)
                {
                    countNeighbours++;
                }
                if(type == fGrid[i+1][j-1])
                {
                    countSimilar++;
                }
            }
            
            if(fGrid[i+1][j] != EMPTY)
            {
                countNeighbours++;
            }
            // Bottom
            if(type == fGrid[i+1][j])
            {
                countSimilar++;
            }
            
            // Bottom Right
            if(j <= (fGridSize-2))
            {
                if(fGrid[i+1][j+1] != EMPTY)
                {
                    countNeighbours++;
                }
                if(type == fGrid[i+1][j+1])
                {
                    countSimilar++;
                }
            }
        }
        if(((double)countSimilar/(double)countNeighbours) >= ((double)((double)fToleranceLevel/100) - ERROR))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void printGrid()
    {
        for(int i=0; i < fGridSize; i++)
        {
            for(int j=0; j < fGridSize; j++)
            {
                String s = fGrid[i][j]+"";
                System.out.print(s + DELIMITER);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    private void initializeEmptyGrid()
    {
        for(int i=0; i < fGridSize; i++)
        {
            for(int j=0; j < fGridSize; j++)
            {
                fGrid[i][j] = EMPTY;
            }
        }
    }
    private void initializeGridEntries(int populationLeft)
    {
        entriesCount = populationLeft;
        Random random = new Random(19580427);
        
        for(int i=0; i < fGridSize; i++)
        {
            for(int j=0; j < fGridSize; j++)
            {
                if(fGrid[i][j] == EMPTY)
                {
                    if(random.nextInt(RANDOM_NUMBER_RANGE) >= (RANDOM_NUMBER_RANGE/2))
                    {
                        if(random.nextInt(RANDOM_NUMBER_RANGE) >= (RANDOM_NUMBER_RANGE/2))
                        {
                            fGrid[i][j] = TYPE1;
                        }
                        else
                        {
                            fGrid[i][j] = TYPE2;
                        }
                        entriesCount--;
                        if(entriesCount == 0)
                        {
                            return;
                        }
                    }
                }
            }
        }
        while(entriesCount > 0)
        {
            initializeGridEntries(entriesCount);
        }
    }
    
    // Tolerance level represents the required minimum of similar people
    private final int fToleranceLevel;
    private final int fGridSize;
    private final int fPopulation;
    private final int fSimulationSpeed;
    private char[][] fGrid;
    private int entriesCount;
    
    
    
    // Constants
    private double ERROR = 0.01;
    private final char EMPTY = '.';
    private final char TYPE1 = '*';
    private final char TYPE2 = '@';
    private final char DELIMITER = ' ';
    private final int RANDOM_NUMBER_RANGE = 100;
    private final int ONE_SECOND_SPEED = 1000;
    
    public static void main(String args[])
    {
        // TODO write code to read values
        // First value is tolerance and is on a scale of 0 - 100(intolerant)
        // Second is gridSize
        // Third is population
        // 4th is speed in seconds
        ShellingSimulation shellingSimulation = new ShellingSimulation(80, 15, 100, 1);
        while(true)
        {
            shellingSimulation.simulate();
        }
    }
}