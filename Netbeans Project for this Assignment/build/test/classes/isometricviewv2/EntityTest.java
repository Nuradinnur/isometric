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
public class EntityTest 
{
    Entity player = new Entity(10, 10);
    Map map = new Map(20);
    
    public EntityTest() 
    {
        map.loadMapData();
    }
    
    //
    // 4 tests are done here, 6 more in the other class (10 total)
    //
    
    /**
     * Test of getFacingDirection method, of class Entity.
     */
    @Test
    public void testGetFacingDirection() 
    {
        // Direction should be 0 (or south) on creation
        assertEquals(0, player.getFacingDirection());
        player.setFacingDirection(400);
        // Works for "all" directions including 400, which is not actually a direction
        // (Directions are represented with integer values 0, 1, 2, 3 only)
        assertEquals(400, player.getFacingDirection());
    }
    
    /**
     * Test of moveEntity method, of class Entity.
     */
    @Test
    public void testMoveEntity() 
    {
        int xcoord = player.getX();
        int ycoord = player.getY();
        map.loadMapData(); // Load map
        map.switchClipping(); // Disable clipping
        player.moveEntity(0, player, map); // Move south one step
        player.moveEntity(1, player, map); // Move west one step
        player.moveEntity(2, player, map); // Move north one step
        player.moveEntity(3, player, map); // Move east one step
        
        // The player should be in the original position at this point
        
        assertEquals(xcoord, player.getX());
        assertEquals(ycoord, player.getY());
    }

    /**
     * Test of incMovementCooldown method, of class Entity.
     */
    @Test
    public void testIncMovementCooldown() 
    {
        // This should set the variable movementCooldown to 8.
        player.incMovementCooldown();
        assertEquals(8, player.getMovementCooldown());
    }

    /**
     * Test of isMoving method, of class Entity.
     */
    @Test
    public void testIsMoving() 
    {
        player.setMoving(Boolean.FALSE);
        assertEquals(Boolean.FALSE, player.isMoving());
        
        player.setMoving(Boolean.TRUE);
        assertEquals(Boolean.TRUE, player.isMoving());
    }
}
