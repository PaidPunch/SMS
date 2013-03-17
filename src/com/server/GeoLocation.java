package com.server;

import java.util.ArrayList;

public final class GeoLocation 
{
    public static final double geoLocFraction = 0.2;
    
    public static int coordToRegion(double latitude, double longitude)
    {
        int latIndex = getIndex(latitude, -90, 90);
        int lonIndex = getIndex(longitude, -180, 180);
        return (latIndex * 360 * (int)(1.0/geoLocFraction)) + lonIndex;
    }
    
    private static int getIndex(double point, double minVal, double maxVal)
    {
        int lowIndex = 0;
        int highIndex = (int)((maxVal - minVal) * (1.0 / geoLocFraction)) - 1;
        int midIndex = (lowIndex + highIndex)/2;
        int currentIndex = -1;
        double roundedPoint = point;
        boolean done = false;
        int count = 0;
        
        if (Utility.compareFloats(roundedPoint, minVal) == 0)
        {
            currentIndex = lowIndex;
        }
        else if (Utility.compareFloats(roundedPoint, maxVal) == 0)
        {
            currentIndex = highIndex;
        }
        else
        {
            while (!done && (count < 10000))
            {
                double left = minVal + (geoLocFraction * midIndex);
                left = Utility.round(left, 8);
                double right = left + geoLocFraction;
                right = Utility.round(right, 8);
                
                if (roundedPoint >= left && roundedPoint < right)
                {
                    currentIndex = midIndex;
                    done = true;
                }
                else
                {
                    if (roundedPoint < left)
                    {
                        highIndex = midIndex + 1;
                    }
                    else 
                    {
                        lowIndex = midIndex - 1;
                    }
                    midIndex = (lowIndex + highIndex)/2;
                }
            }
        }
        
        // If for some reason we hit the upper limit of iterations
        // just set the index to be midindex
        if (!done)
        {
            SimpleLogger.getInstance().warn(GeoLocation.class.getSimpleName(), "UnableToLocateToProperIndex|Setting region to " + currentIndex);
            currentIndex = midIndex;
        }
        
        return currentIndex;
    }
    
    public static ArrayList<Integer> getSurroundingRegions(int region)
    {
        ArrayList<Integer> regionArray = new ArrayList<Integer>();
        int numCols = (int)((1.0/geoLocFraction) * 360);
        int numRows = (int)((1.0/geoLocFraction) * 180);
        int firstValueInLastRow = numCols * (numRows - 1);
        int lastValueInLastRow = (numCols * numRows) - 1;
        boolean done = false;
        
        // general case
        if (!isLeftEdge(region) && !isRightEdge(region) && 
                !isTopRow(region) && !isBottomRow(region))
        {
            regionArray.add(region - numCols - 1);
            regionArray.add(region - numCols);
            regionArray.add(region - numCols + 1);
            regionArray.add(region - 1);
            regionArray.add(region + 1);
            regionArray.add(region + numCols - 1);
            regionArray.add(region + numCols);
            regionArray.add(region + numCols + 1);
            done = true;
        }

        // top row except for left and right edges (which are corners)
        if (!done && !isLeftEdge(region) && !isRightEdge(region) && isTopRow(region))
        {
            regionArray.add(region + firstValueInLastRow - 1);
            regionArray.add(region + firstValueInLastRow);
            regionArray.add(region + firstValueInLastRow + 1);
            regionArray.add(region - 1);
            regionArray.add(region + 1);
            regionArray.add(region + numCols - 1);
            regionArray.add(region + numCols);
            regionArray.add(region + numCols + 1);
            done = true;
        }

        // bottom row except for edges (which are corners)
        if (!done && !isLeftEdge(region) && !isRightEdge(region) && isBottomRow(region))
        {
            regionArray.add(region - numCols - 1);
            regionArray.add(region - numCols);
            regionArray.add(region - numCols + 1);
            regionArray.add(region - 1);
            regionArray.add(region + 1);
            regionArray.add(region - firstValueInLastRow - 1);
            regionArray.add(region - firstValueInLastRow);
            regionArray.add(region - firstValueInLastRow + 1);
            done = true;
        }

        // left edge except for corners. tricky ones are left, upper left, and lower left
        if (!done && isLeftEdge(region) && !isTopRow(region) && !isBottomRow(region))
        {
            regionArray.add(region - 1);
            regionArray.add(region - numCols);
            regionArray.add(region - numCols + 1);
            regionArray.add(region - 1 + numCols);
            regionArray.add(region + 1);
            regionArray.add(region + numCols + numCols - 1);
            regionArray.add(region + numCols);
            regionArray.add(region + numCols + 1);
            done = true;
        }

        // right edge except for corners. tricky ones are right, upper right, lower right
        if (!done && isRightEdge(region) && !isTopRow(region) && !isBottomRow(region))
        {
            regionArray.add(region - numCols - 1);
            regionArray.add(region - numCols);
            regionArray.add(region - numCols - numCols + 1);
            regionArray.add(region - 1);
            regionArray.add(region - numCols + 1);
            regionArray.add(region + numCols - 1);
            regionArray.add(region + numCols);
            regionArray.add(region + 1);
            done = true;
        }

        // upper left corner (always sector 0)
        if (!done && (region == 0))
        {
            regionArray.add(numCols * numCols - 1);
            regionArray.add(firstValueInLastRow);
            regionArray.add(firstValueInLastRow + 1);
            regionArray.add(numCols - 1);
            regionArray.add(1);
            regionArray.add(numCols + numCols - 1);
            regionArray.add(numCols);
            regionArray.add(numCols + 1);
            done = true;
        }

        // upper right corner
        if (!done && isRightEdge(region) && isTopRow(region))
        {
            regionArray.add(numCols * numCols - 2);
            regionArray.add(firstValueInLastRow);
            regionArray.add(firstValueInLastRow + 1);
            regionArray.add(numCols - 1);
            regionArray.add(1);
            regionArray.add(numCols + numCols - 1);
            regionArray.add(numCols);
            regionArray.add(numCols + 1);
            done = true;
        }

        // lower left corner (is firstValueInLastRow)
        if (!done && isLeftEdge(region) && isBottomRow(region))
        {
            regionArray.add(lastValueInLastRow - numCols);
            regionArray.add(region - numCols);
            regionArray.add(region - numCols + 1);
            regionArray.add(lastValueInLastRow);
            regionArray.add(region + 1);
            regionArray.add(numCols - 1);
            regionArray.add(0);
            regionArray.add(1);
            done = true;
        }

        // lower right corner (is lastValueInLastRow)
        if (!done && isRightEdge(region) && isBottomRow(region))
        {
            regionArray.add(region - numCols - 1);
            regionArray.add(region - numCols);
            regionArray.add(firstValueInLastRow - numCols);
            regionArray.add(region - 1);
            regionArray.add(firstValueInLastRow);
            regionArray.add(numCols - 2);
            regionArray.add(numCols - 1);
            regionArray.add(0);
            done = true;
        }
        
        return regionArray;
    }
    
    private static boolean isLeftEdge(int region)
    {
        int numCols = (int)((1.0/geoLocFraction) * 360);
        int remainder = (region % numCols);
        return (remainder == 0);
    }
    
    private static boolean isRightEdge(int region)
    {
        return isLeftEdge(region + 1);
    }
    
    private static boolean isTopRow(int region)
    {
        int numCols = (int)((1.0/geoLocFraction) * 360);
        return (region >= 0 && (region <= (numCols - 1)));
    }
    
    private static boolean isBottomRow(int region)
    {
        int numCols = (int)((1.0/geoLocFraction) * 360);
        int numRows = (int)((1.0/geoLocFraction) * 180);
        int firstValueInLastRow = numCols * (numRows - 1);
        int lastValueInLastRow = (numCols * numRows) - 1;
        return ((region >= firstValueInLastRow) && (region <= lastValueInLastRow));
    }
}
