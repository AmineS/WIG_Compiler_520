import java.util.Random;
public class ShellingSimplifiedSimulation
{

        public ShellingSimplifiedSimulation(int toleranceLevel, int gridSize, int population, int simulationSpeed)
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
                        relocate(i, j, false);
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
        
        private void relocate(int row, int col, boolean randomLocationFlag)
        {
            boolean toleranceMetInNewLocation = false;

            for(int i=0; i < fGridSize; i++)
            {
                for(int j=0; j < fGridSize; j++)
                {
                    if(fGrid[i][j] == EMPTY && (isToleranceLevelMet(i, j, fGrid[i][j]) || randomLocationFlag))
                    {
                        char type = fGrid[row][col];
                        fGrid[i][j] = type;
                        fGrid[row][col] = EMPTY;
                        toleranceMetInNewLocation = true;
                        break;
                    }
                }
            }
            
            if(!toleranceMetInNewLocation)
            {
                relocate(row, col, true);
            }

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
                   countNeighbours++;
                   if(type == fGrid[i-1][j-1])
                   {
                       countSimilar++;
                   }
               }
               
               // center top
               if(type == fGrid[i-1][j])
               {
                   countNeighbours++;
                   countSimilar++;
               }
               
               // Right top
               if(j <= (fGridSize-2))
               {
                   countNeighbours++;
                   if(type == fGrid[i-1][j+1])
                   {
                       countSimilar++;
                   }
               }
            }
            
            // Left 
            if(j >= 1)
            {
                countNeighbours++;
                if(type == fGrid[i][j-1])
                {
                    countSimilar++;
                }
            }
            
            // Right
            if(j <= (fGridSize-2))
            {
                countNeighbours++;
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
                    countNeighbours++;
                    if(type == fGrid[i+1][j-1])
                    {
                        countSimilar++;
                    }
                }
                
                countNeighbours++;
                // Bottom
                if(type == fGrid[i+1][j])
                {
                    countSimilar++;
                }
                
                // Bottom Right
                if(j <= (fGridSize-2))
                {
                    countNeighbours++;
                    if(type == fGrid[i+1][j+1])
                    {
                        countSimilar++;
                    }
                }
            }
            
            int equivalentToleranceLevel = 0;
            if(countNeighbours == 3 || countNeighbours == 5)
            {
                equivalentToleranceLevel = getEquivalentToleranceLevel(countNeighbours);
            }
            else
            {
                equivalentToleranceLevel = fToleranceLevel;
            }
            if(countSimilar >= equivalentToleranceLevel)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        
        public int getEquivalentToleranceLevel(int countNeighbours)
        {
            if(countNeighbours == 3)
            {
                int[] a = new int[9];
                a[0] = 0;
                a[1] = a[2] = a[3] = 1;
                a[4] = a[5] = a[6] = 2;
                a[7] = a[8] = 3;
                return a[fToleranceLevel];
            }
            else
            {
                int[] a = new int[9];
                a[0] = 0;
                a[1] = a[2] = 1;
                a[3] = a[4] = 2;
                a[5] = a[6] = 3;
                a[7] = 4;
                a[8] = 5;
                return a[fToleranceLevel];
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
            ShellingSimplifiedSimulation shellingSimulation = new ShellingSimplifiedSimulation(3, 15, 100, 1);
            while(true)
            {
                shellingSimulation.simulate();
            }
        }
}

