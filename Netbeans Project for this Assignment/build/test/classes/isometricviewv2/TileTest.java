/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Secret
 */
public class TileTest 
{
    Tile tile;
    Entity player = new Entity(10, 10);
    Map map = new Map(20);
    
    public TileTest() 
    {
        tile = new Tile(0, 4, 50, 5);
    }
    
    //
    // 6 tests are done here, 4 more in the other class (10 total)
    //

    /**
     * Test of changeTileType method, of class Tile.
     */
    @Test
    public void testChangeTileType() 
    {
        tile.changeTileType(Boolean.FALSE); // Decreases 50 by 1
        assertEquals(49, tile.getTileType());
        assertEquals(Boolean.TRUE, !tile.isUnclipped());
        
        tile.changeTileType(Boolean.TRUE); // Increases the tile type by 1.  Back to 50.
        assertEquals(46, tile.getTileType()); // Value should be 46 due to the if statement
        
        
        for (int i = 0; i < 100; i++)
        {
            // After 100 decrements this value should be 0
            tile.changeTileType(Boolean.FALSE);
        }
        
        assertEquals(0, tile.getTileType());
        assertEquals(Boolean.TRUE, tile.isUnclipped());
    }

    /**
     * Test of changeTileHeight method, of class Tile.
     */
    @Test
    public void testChangeTileHeight() 
    {
        tile.changeTileHeight(Boolean.TRUE);
        assertEquals(10, tile.getTileHeight());
        
        tile.changeTileHeight(Boolean.FALSE);
        assertEquals(5, tile.getTileHeight());
    }

    /**
     * Test of getTileType method, of class Tile.
     */
    @Test
    public void testGetTileType() 
    {
        // Tile type is 50 from constructor
        assertEquals(50, tile.getTileType());
    }

    /**
     * Test of getTileHeight method, of class Tile.
     */
    @Test
    public void testGetTileHeight() 
    {
        // Tile height is 5 from constructor
        assertEquals(5, tile.getTileHeight());
    }

    /**
     * Test of isUnclipped method, of class Tile.
     */
    @Test
    public void testIsUnclipped() 
    {
        // Type is 50 therefore should be clipped
        assertEquals(Boolean.FALSE, tile.isUnclipped());
    }

    /**
     * Test of cloneTile method, of class Tile.
     */
    @Test
    public void testCloneTile() 
    {
        map.loadMapData(); // Create map
        map.switchClipping(); // Turn off clipping
        assertEquals(Boolean.FALSE, map.clipping()); // Test to see if clipping is off
        
        // Save information about previous tile to compare later on
        int previousTileType = map.getMapData()[player.getX()][player.getY()].getTileType();
        int previousTileHeight = map.getMapData()[player.getX()][player.getY()].getTileHeight();
       
        // Move player elsewhere
        player.setX(14);
        player.setY(3);
       
        // Clone previous tile to (14, 3)
        tile.cloneTile(player, map);
        
        // Compare previous and current tiles to ensure cloning worked
        assertEquals(previousTileType, map.getMapData()[player.getX()][player.getY()].getTileHeight());
        assertEquals(previousTileHeight, map.getMapData()[player.getX()][player.getY()].getTileType());
    }
    
}
