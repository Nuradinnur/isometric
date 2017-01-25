/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
* OVERVIEW: this class handles the detection of key presses from the user.
* Each key press corresponds to a particular function defined in other
* classes.
* 
* This class is mutable.
*/
public class KeyInput implements KeyListener
{
    
    private Entity player;
    private Map map;
    
    /**
    * REQUIRES: an instance of an entity and a map to operate on.
    *
    * EFFECTS: instantiates the two following variables to refer to the 
    * entity and map being operated on by this class.
    * 
    * @param player the entity that will be manipulated via key strokes
    * @param map the map in which this entity is performing actions on
    */
    public KeyInput (Entity player, Map map)
    {
        
        this.player = player;
        this.map = map;
    }
    
    /**
     * EFFECTS: returns false if any instance variable is null.  Returns true otherwise.
     */
    public Boolean repOk()
    {
        if (player == null || map == null)
            return false;
        return true;
    }
    
    /**
     * EFFECTS: prints out: AF(c) = c alters the states of map and player.
     */
    @Override
    public String toString()
    {
        return "AF(c) = c alters the states of map and player.";
    }
    
    /**
    * DOES NOTHING.
    * KeyListener requires that this function be overridden.
    * 
    * @param e the value of the next key typed (typing a key implies pressing and releasing)
    */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
    * REQUIRES: a "key pressed" event (caused by pressing a key down)
    *
    * EFFECTS: many different aspects of the current entity or map.  This
    * may include the map offset, the player's faced direction or the 
    * clipping of tiles around you.  A full description of what it modifies
    * is below.
    *
    * If a key is pressed down, use the key code event to do a particular
    * function (defined in other classes.)
    *
    * @param e the value of the next key pressed (and not necessarily let go)
    */
    @Override
    public void keyPressed(KeyEvent e) 
    {
        
        // Current list of commands:
        
        // Controls in normal mode:
        // Arrow keys   -   moves your avatar
        // C            -   disables clipping and enables editing mode
         
        // Controls in editing mode:
        // W and S      -   changes the height of the tile you're standing on
        // A and D      -   changes the style of the tile you're standing on
        // X            -   enables or disables debugging mode
        // C            -   disables editing mode and enables clipping
        // V            -   clones the tile at your previous position onto your
        //                  current position's tile
        
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_W:
                if (!map.clipping())
                {
                    map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].changeTileHeight(true);
                }
                break;
            case KeyEvent.VK_A:
                if (!map.clipping())
                {
                    map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].changeTileType(false);
                }
                break;
            case KeyEvent.VK_S:
                if (!map.clipping())
                {
                    map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].changeTileHeight(false);
                }
                break;
            case KeyEvent.VK_D:
                if (!map.clipping())
                {
                    map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].changeTileType(true);
                }
                break;
            case KeyEvent.VK_X:
                if (!map.clipping())
                {
                    map.switchDebug();
                }
                break;
            case KeyEvent.VK_C:
                map.switchClipping();
                break;
            case KeyEvent.VK_V:
                if (!map.clipping())
                {
                    map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].cloneTile(player, map);
                }
                break;
            case KeyEvent.VK_UP:
                player.setFacingDirection(2);
                player.setMoving(true);
                break;
            case KeyEvent.VK_LEFT:
                player.setFacingDirection(1);
                player.setMoving(true);
                break;  
            case KeyEvent.VK_DOWN:
                player.setFacingDirection(0);
                player.setMoving(true);
                break;
            case KeyEvent.VK_RIGHT:
                player.setFacingDirection(3);
                player.setMoving(true);
                break;
        }
    }
    
    /**
    * REQUIRES: a "key released" event (caused by releasing a key)
    *
    * EFFECTS: stops the character from moving.
    *
    * When any key below is pressed down, a boolean variable is changed
    * to true in the entity class.  When it is released, this function 
    * sets that same boolean variable to false.  This is done so that 
    * player movement velocity is not changed due to the delay caused by 
    * repeating keys on the keyboard.
    * 
    * @param e the value of the next key released
    */
    @Override
    public void keyReleased(KeyEvent e) 
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                player.setMoving(false);
                break;
            case KeyEvent.VK_LEFT:
                player.setMoving(false);
                break;
            case KeyEvent.VK_DOWN:
                player.setMoving(false);
                break;
            case KeyEvent.VK_RIGHT:
                player.setMoving(false);
                break;
        }
    }
}