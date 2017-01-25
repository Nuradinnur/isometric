/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

/**
* OVERVIEW:  This class stores the various properties of a tile.
* These properties include location (x, y, z), look of the tile and
* whether or not it is a walkable tile (clipping.)  There are also
* useful functions that operate on tiles or simply return information.
*
* The (x, y) instance variables will be useful in the future, if I continue 
* developing this program after the project.  For now, they are unused.
* 
* This class is mutable.
*/
public class Tile
{
    private int x, y;
    private int type;
    private int height;
    private boolean unclipped;

    // You may walk on these tiles only...
    
    private final int [] UNCLIPPED_TILE_IDS = {0, 1, 2, 3, 4, 5, 6, 10, 11, 12, 13, 14, 15, 16, 17};
    
    // Unless you press C.
    // Then you can walk where you wish.
    
    /**
    * REQUIRES: a "3D" location (x, y, height), and a number representing 
    * the look (or type) of the tile.
    *
    * EFFECTS: initializes the instance variables.  Also sets tile to 
    * unclipped if the type appears in the integer array
    * "UNCLIPPED_TILE_IDS".
    * 
    * @param x x coordinate of this tile
    * @param y y coordinate of this tile
    * @param type the type of this tile
    * @param height the height level of this tile
    */
    public Tile (int x, int y, int type, int height) 
    {
        this.x = x;
        this.y = y;
        this.type = type;
        this.height = height;

        unclipped = false;
        for (int i = 0; i < UNCLIPPED_TILE_IDS.length; i++)
        {
            if (this.type == UNCLIPPED_TILE_IDS[i])
            {
                unclipped = true;
            }
        }
    }
    
    /**
     * EFFECTS: returns false if ints are not instances of Integer class OR facing direction is not 0..3 OR movementCooldown is greater than 8.  Returns true otherwise.
     */
    public Boolean repOk()
    {
        Object location0 = x;
        Object location1 = y;
        Object t = type;
        Object h = height;
        if (!(location0 instanceof Integer && location1 instanceof Integer && t instanceof Integer && h instanceof Integer))
            return false;
        return true;
    }
        
    /**
     * EFFECTS: prints out: AF(c) = c is a tile at x, y with type type and height height.  c is also either unclipped or clipped.
     */
    @Override
    public String toString()
    {
        return "AF(c) = c is a tile at x, y with type type and height height.  c is also either unclipped or clipped.";
    }
    
    /**
    * 
    * REQUIRES: a boolean value used as a switch for flow of execution
    * EFFECTS: changes the value of the current tile's type.  "increase"
    * being set to true will increment the tile's type.  Otherwise, the
    * tile's type is decremented.
    *
    * @param increase increment tile type if true, and otherwise decrement it
    */

    public void changeTileType (Boolean increase)
    {
        if (increase == true)
        {
            type++;
            if (type >= 46)
            {
                type = 46; 
            }
        }
        else
        {
            type--;
            if (type < 0)
            {
                type = 0;
            }
        }

        unclipped = false;
        for (int i = 0; i < UNCLIPPED_TILE_IDS.length; i++)
        {
            if (type == UNCLIPPED_TILE_IDS[i])
            {
                unclipped = true;
            }
        }
    }
    
    /**
    * REQUIRES: a boolean value used as a switch for flow of execution
    *
    * EFFECTS: changes the value of the current tile's height.  "increase"
    * being set to true will increment the tile's height.  Otherwise, the
    * tile's type is decremented.
    * 
    * @param increase increments tile height if true, and otherwise decrements it
    */

    public void changeTileHeight (Boolean increase)
    {
        
        if (increase == true)
        {
            height += 5;
        }
        else
        {
            height -= 5;
        }
    }
    
    /**
    * EFFECTS: returns the value of the type of this tile.
    * 
    * @return the type of this tile
    */
    public int getTileType ()
    {
        return type;
    }

    /**
    * EFFECTS: returns the value of the height of this tile.
    * 
    * @return the height level of this tile
    */
    public int getTileHeight ()
    {
        return height;
    }

    /**
    * EFFECTS: returns false if this tile is clipped.  Otherwise, returns
    * true.
    * 
    * @return if unclipped, false, and otherwise true
    */
    public boolean isUnclipped ()
    {
        
        return unclipped;
    }

    /**
    * REQUIRES: entity instance for accessing player location and last
    * player location.  Map instance for editing the map.
    *
    * EFFECTS: changes the currently stepped on tile to the same height and
    * type as the previously stepped on tile.
    *
    * MODIFIES: does not modify the entity class at all.  Modifies the map
    * class's tile data array.
    * 
    * @param player the entity whose location will be used to clone the tile
    * @param map the map in which the tile will be cloned
    */
    public void cloneTile (Entity player, Map map)
    {
        
        map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].type = map.getMapData()[player.getLastX() + map.getOffsetX()][player.getLastY() + map.getOffsetY()].type;
        map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].height = map.getMapData()[player.getLastX() + map.getOffsetX()][player.getLastY() + map.getOffsetY()].height;
        // System.out.println("Cloned tile at: " + player.getLastX() + "," + player.getLastY() + ".  Tile cloned to: " + player.getX() + "," + player.getY());
    }
}