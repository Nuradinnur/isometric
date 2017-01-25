/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JPanel;

/**
 * OVERVIEW: This class allows the writing of critical debugging information
 * onto the information log, the tiles and at the bottom of the screen.
 * 
 * This class is mutable.
 */
public class InfoLog 
{
    private long lastUpdateTime;
    private int updateFPSCooldown;
    private String fps;
    private String [] location;
    private String tileInfo;
    private String otherInfo;
    private Map map;
    private Entity player;
    private DecimalFormat formatter = new DecimalFormat(".00");
    
    /**
    * REQUIRES: a map to be grabbing data from and a player to use as the
    * center of focus for debugging (x, y, z)-coordinates, type of tile, etc.
    *
    * EFFECTS: initializes both of the above variables as well as does some
    * preliminary work that needs to be done before functions are called.
    * 
    * @param map information is retrieved from this instance of Map
    * @param player information is retrieved from this instance of Entity
    */
    
    public InfoLog (Map map, Entity player)
    {
        this.map = map;
        this.player = player;
        lastUpdateTime = System.nanoTime();
        location = new String [3];
        location[0] = "X:";
        location[1] = "Y:";
        location[2] = "Z:";
        otherInfo = "(A or D, changes type) (W or S, changes height) (X, toggle debugging overlay) (C, toggle clipping) (V, clone last tile)";
        updateLog(lastUpdateTime);
    }
    
    /**
     * EFFECTS: returns false if ints are not instances of Integer class OR any of the other instance variables are null.  Returns true otherwise.
     */
    public Boolean repOk()
    {
        Object lastTime = lastUpdateTime;
        Object fpsCooldown = updateFPSCooldown;
        if(!(lastTime instanceof Long))
            return false;
        if(!(fpsCooldown instanceof Integer))
            return false;
        if (fps == null || location == null || tileInfo == null || otherInfo == null || map == null || player == null || formatter == null)
            return false;
        return true;
    }
    
    /**
     * EFFECTS: prints out: AF(c) = c tracks information in map and player, calculates frames per second with lastUpdateTime every updateFPSCooldown frames and uses all of the above strings to draw the data to the screen using formatter to format certain fields.
     */
    @Override
    public String toString()
    {
        return "AF(c) = c tracks information in map and player, calculates frames per second with lastUpdateTime every updateFPSCooldown frames and uses all of the above strings to draw the data to the screen using formatter to format certain fields.";
    }
    
    /**
    * REQUIRES: graphics context and a jPanel to draw the log onto.
    *
    * EFFECTS: draws a transparent rectangle to the top corner of the
    * screen, then draws relevant information in the box, on tiles and
    * below the tiles if requested by the user (by pressing X.)
    * 
    * MODIFIES: the jPanel is drawn on by the graphics context g.  That
    * specific frame is changed permanently.
    * 
    * @param g a graphics context to print strings and the box outline with
    * @param p a jPanel to draw on
    **/
    public void drawLog (Graphics g, JPanel p)
    {
        // 350 x 120 transparent background
        g.setColor(new Color(31, 31, 31, 127));
        g.fillRect(10, 30, 350, 83);
        
        // 350 x 120 pixel outline
        g.setColor(new Color(127, 127, 127, 127));
        g.drawLine(10, 30, 360, 30);
        g.drawLine(360, 30, 360, 113);
        g.drawLine(360, 113, 10, 113);
        g.drawLine(10, 113, 10, 30);
        
        // Various pieces of information displayed in left hand corner
        g.setFont(new Font("Courier New", Font.PLAIN, 12));
        g.setColor(new Color(255, 255, 255, 255));
        g.drawString(fps, 15, 45);
        g.drawString(location[0], 15, 60);
        g.drawString(location[1], 15, 75);
        g.drawString(location[2], 15, 90);
        g.drawString(tileInfo, 15, 105);
        
        // Instructions across the bottom
        g.drawString(otherInfo, 9, 510);
        
        // Individual tile data shown as numbers on screen
        g.setFont(new Font("Calibri", Font.PLAIN, 11));
        if (map.usingDebug())
        {
            for (int i = 0; i < map.getSize(); i++)
            {
                for (int j = 0; j < map.getSize(); j++)
                {
                    if (map.getMapData()[i + map.getOffsetX()][j + map.getOffsetY()].getTileType() != 0)
                    {
                        g.setColor(new Color(0, 0, 0, 255));
                        g.drawString("" + map.getMapData()[i + map.getOffsetX()][j + map.getOffsetY()].getTileType(), p.getWidth() / 2 - 4 + 20 * (i + j - map.getSize() + 1), p.getHeight() / 2 + 21 + 10 * (j - i + 1) - map.getMapData()[i + map.getOffsetX()][j + map.getOffsetY()].getTileHeight());
                        g.setColor(new Color(255, 255, 255, 255));
                        g.drawString("" + map.getMapData()[i + map.getOffsetX()][j + map.getOffsetY()].getTileType(), p.getWidth() / 2 - 5 + 20 * (i + j - map.getSize() + 1), p.getHeight() / 2 + 20 + 10 * (j - i + 1) - map.getMapData()[i + map.getOffsetX()][j + map.getOffsetY()].getTileHeight());
                    }
                }
            }
        }
    }
    /**
    * REQUIRES: the exact time in nanoseconds when the beginning of the 
    * current frame started.
    *
    * EFFECTS: every 60 frames (roughly one second, though slightly longer)
    * the string "fps" is updated with the current frames per second.
    * Every single frame, the other strings are updated with (x, y, z)-
    * coordinates, tile type and tile height.
    * 
    * @param currentTime the current time in nanoseconds
    */
    
    public void updateLog (long currentTime)
    {
        
        if (updateFPSCooldown == 0)
        {
            fps = "Frames per second: " + formatter.format((double) 1000000000 / (currentTime - lastUpdateTime));
            updateFPSCooldown = 60;
        }
        else
        {
            updateFPSCooldown--;
        }
        
        lastUpdateTime = currentTime;
        
        location[0] = "X-coordinate: " + (player.getX() + map.getOffsetX()) + " (relative-grid: " + player.getX() + ", offset: " + map.getOffsetX() + ")";
        location[1] = "Y-coordinate: " + (player.getY() + map.getOffsetY()) + " (relative-grid: " + player.getY() + ", offset: " + map.getOffsetY() + ")";
        location[2] = "Z-coordinate: " + map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].getTileHeight() / 5;
        
        tileInfo = "Tile properties - type: " + map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].getTileType() + ", clipped: " + !map.getMapData()[player.getX() + map.getOffsetX()][player.getY() + map.getOffsetY()].isUnclipped();
    }
}
