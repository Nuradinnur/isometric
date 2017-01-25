/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

/** 
* OVERVIEW: This class is responsible for either player-controlled or 
* computer controlled characters within the game.  Movement is the only
* real ability that a controllable character needs in a map editor, thus
* this class handles all movement.
* 
* This class is mutable.
*/
public class Entity 
{
    
    private int [] location = new int [2];
    private int [] lastLocation = new int [2];
    private int facingDirection = 0; // South, West, North, East
    private int movementCooldown = 0;
    private boolean currentlyMoving = false;
    
    /**
     * REQUIRES: starting x-coordinate and y-coordinate.
     *
     * EFFECTS: initializes the player at that position.
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Entity(int x, int y)
    {
        this.location[0] = x;
        this.location[1] = y;
        this.lastLocation[0] = x;
        this.lastLocation[1] = y;
    }
    
    /**
     * EFFECTS: returns false if ints are not instances of Integer class OR facing direction is not 0..3 OR movementCooldown is greater than 8.  Returns true otherwise.
     */
    public Boolean repOk()
    {
        Object location0 = location[0];
        Object location1 = location[1];
        Object location2 = lastLocation[0];
        Object location3 = lastLocation[1];
        
        if (!(location0 instanceof Integer && location1 instanceof Integer))
            return false;
        if (!(location2 instanceof Integer && location3 instanceof Integer))
            return false;
        if (facingDirection < 0 || facingDirection > 3)
            return false;
        if (movementCooldown > 8)
            return false;
        return true;
    }
    
    /**
     * EFFECTS: prints out: AF(c) = c is at location[0], location[1] and c's last location is lastLocation[0], lastLocation[1].  c faces facingDirection, travels at max(movementCooldown) 'speed' and c's movement is regulated by currentlyMoving.
     */
    @Override
    public String toString()
    {
        return "AF(c) = c is at location[0], location[1] and c's last location is lastLocation[0], lastLocation[1].  c faces facingDirection, travels at max(movementCooldown) 'speed' and c's movement is regulated by currentlyMoving.";
    }

    /** EFFECTS: returns the character's heading.
    * South is 0, west is 1, north is 2, east is 3.
    * @return facing direction of the entity
    */
    public int getFacingDirection()
    {
        return facingDirection;
    }
 
    /**
     * EFFECTS: changes the character's heading.
     * @param direction the entity should be facing
     */
    public void setFacingDirection(int direction)
    {
        this.facingDirection = direction;
    }
    
    /**
     * EFFECTS: returns the x-coordinate of the player.
     * @return x-coordinate of entity
     */
    public int getX()
    {
        return location[0];
    }
    
    /**
     * EFFECTS: returns the y-coordinate of the player.
     * @return y-coordinate of entity
     */
    public int getY()
    {
        return location[1];
    }
    
    /**
     * EFFECTS: returns the previous x-coordinate of the player.
     * @return previous x-coordinate of entity
     */
    public int getLastX()
    {
        return lastLocation[0];
    }
    
    /**
     * EFFECTS: returns the previous y-coordinate of the player.
     * @return previous y-coordinate of entity
     */
    public int getLastY()
    {
        return lastLocation[1];
    }
    
    /**
     * REQUIRES: new x-coordinate for player.   
     * EFFECTS: changes the last x-coordinate to the current, and changes 
     * the current x-coordinate to the newly specified one,
     * @param location new x-coordinate of the entity
     */
    public void setX(int location)
    {
        this.lastLocation[0] = this.location[0];
        this.location[0] = location;
    }
    
    /**
     * REQUIRES: new y-coordinate for player.
     * 
     * EFFECTS: changes the last y-coordinate to the current, and changes 
     * the current y-coordinate to the newly specified one,
     * 
     * @param location new y-coordinate of the entity
     */
    public void setY(int location)
    {
    
        this.lastLocation[1] = this.location[1];
        this.location[1] = location;
    }
    
    /**
    * REQUIRES: a direction vector (out of 4 different possible values,
    * south, west, north, east, in that order), an entity being moved and
    * the map to be moving around in.
    *   
    * EFFECTS: **IF** the player is able to move to the specified adjacent
    * tile, then it will move the player there.  Things that can stop you
    * from moving to the tile include the edges of the map and clipped tiles
    *
    * MODIFIES: changes the value of the location array of the entity.
    * May also change the height location of the entity if tile being moved
    * to is at a different height level.  May also change the map's offset 
    * values depending on if the player is too close to the edges of the 
    * on-screen grid.
    * 
    * @param direction direction the entity is moving in
    * @param entity reference to the entity
    * @param map the map to be moving around in
    */
    public void moveEntity (int direction, Entity entity, Map map)
    {
        if (currentlyMoving)
        {
            switch (direction)
            {
                case 0: // South
                    if (getY() + map.getOffsetY() < 99)
                    {
                        if (map.getMapData()[getX() + map.getOffsetX()][getY() + map.getOffsetY() + 1].isUnclipped() || !map.clipping())
                        {
                            if (getY() > map.getSize() - 5 && map.getOffsetY() < map.getDataSize() - map.getSize())
                            {
                                map.changeOffsetY(true);
                            }
                            else
                            {
                                setX(getX());
                                setY(getY() + 1);
                            }

                            entity.incMovementCooldown();
                        }
                    }
                    break;
                case 1: // West
                    if (getX() - 1 >= 0)
                    {
                        if (map.getMapData()[getX() + map.getOffsetX() - 1][getY() + map.getOffsetY()].isUnclipped() || !map.clipping())
                        {
                            if (getX() < 4 && map.getOffsetX() > 0)
                            {
                                map.changeOffsetX(false);
                            }
                            else
                            {
                                setX(getX() - 1);
                                setY(getY());
                            }

                            entity.incMovementCooldown();
                        }
                    }
                    break;
                case 2: // North
                    if (getY() - 1 >= 0)
                    {
                        if (map.getMapData()[getX() + map.getOffsetX()][getY() + map.getOffsetY() - 1].isUnclipped() || !map.clipping())
                        {
                            if (getY() < 4 && map.getOffsetY() > 0)
                            {
                                map.changeOffsetY(false);
                            }
                            else
                            {
                                setX(getX());
                                setY(getY() - 1);
                            }

                            entity.incMovementCooldown();
                        }
                    }
                    break;
                case 3: // East
                    if (getX() + map.getOffsetX() < 99)
                    {
                        if (map.getMapData()[getX() + map.getOffsetX() + 1][getY() + map.getOffsetY()].isUnclipped() || !map.clipping())
                        {
                            if (getX() > map.getSize() - 5 && map.getOffsetX() < map.getDataSize() - map.getSize())
                            {
                                map.changeOffsetX(true);
                            }
                            else
                            {
                                setX(getX() + 1);
                                setY(getY());
                            }

                            entity.incMovementCooldown();
                        }
                    }
                    break;
            }
        }
    }
    
    /**
     * EFFECTS: decrements the movement cooldown, once per frame.
     */
    public void decMovementCooldown ()
    {
        movementCooldown--;
        if (movementCooldown < 0)
        {
            movementCooldown = 0;
        }
    }
    
    /**
     * EFFECTS: incurs an 8 frame cooldown on movement after every step.
     * This number may be changed for different entities to control speed.
     *
     * MODIFIES: this.movementCooldown.
     */
    public void incMovementCooldown ()
    {
        
        movementCooldown = 8;
    }
    
    /**
     * EFFECTS: simply retrieves the current movement cooldown timer.
     * @return movement cooldown for this entity
     */
    public int getMovementCooldown ()
    {
        return movementCooldown;
    }
    
    /**
     * EFFECTS: is a keyboard button being pressed?  If so, this returns
     * true (as currentlyMoving is set to true by the next function)
     * otherwise returns false.
     *  
     * Small note: this and the next function are used to avoid the keyboard
     * delay when a key is first held down. 
     * 
     * @return true if moving, false if not moving
     */
    public boolean isMoving()
    { 
        return currentlyMoving;
    }
    
    /**
     * EFFECTS: starts movement, in any direction.  Once this is set to true,
     * the avatar moves by itself until the boolean is set to false.
     * @param b true if moving, false if not moving
     */
    public void setMoving(boolean b)
    {
        
        currentlyMoving = b;
    }
}