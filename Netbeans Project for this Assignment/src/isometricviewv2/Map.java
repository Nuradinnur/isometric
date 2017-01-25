/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
* OVERVIEW: this class holds two arrays that essentially make up what is
* our field of view, simply named a "map".  The first array mapSize is the
* size of the viewable grid while the second array dataSize is the
* maximum size of the tile data that can be stored.  This class is also 
* responsible for limiting player movement in certain cases (clipping) and 
* the loading, saving and rendering of tile images to the screen.
* 
* This class is mutable.
*/
public class Map 
{    
    private final int mapSize;
    private final int dataSize;
    private int [] offset = new int [2];
    
    private boolean debugStatus = false;
    
    private Tile [][] tiles;
    
    private boolean clipping = true;
    
    /**
    * REQUIRES: the size of the visible grid.
    *
    * EFFECTS: instantiates the 6 important variables below.  Some are
    * simply constants defined by myself and one in particular uses the
    * output of a function to instantiate itself as a [dataSize x dataSize]
    * array.
    * 
    * @param size the side length of the visible grid.  Select a smaller number for a smaller visible map, and vice versa.
    */
    public Map (int size)
    {
        
        mapSize = size;
        dataSize = 100;
        tiles = loadMapData();
        offset[0] = 0;
        offset[1] = 0;
    }
    
    /**
     * EFFECTS: returns false if ints are not instances of Integer class OR facing direction is not 0..3 OR movementCooldown is greater than 8.  Returns true otherwise.
     */
    public Boolean repOk()
    {
        Object mSize = mapSize;
        Object dSize = dataSize;
        Object offset0 = offset[0];
        Object offset1 = offset[1];
        if (!(mSize instanceof Integer && dSize instanceof Integer && offset0 instanceof Integer && offset1 instanceof Integer))
            return false;
        if (tiles == null)
            return false;
        return true;
    }
    
    /**
     * EFFECTS: prints out: AF(c) = c has a viewable size of mapSize and a total size of dataSize, uses offset[0], offset[1] to change the viewable area of the map and stores tile data in tiles[][].  c also tracks the current game state with clipping and debugStatus.
     */
    @Override
    public String toString()
    {
        return "AF(c) = c has a viewable size of mapSize and a total size of dataSize, uses offset[0], offset[1] to change the viewable area of the map and stores tile data in tiles[][].  c also tracks the current game state with clipping and debugStatus.";
    }
    
    /**
    * REQUIRES: a text file at ../config/map.dat.
    *
    * EFFECTS: loads a [dataSize x dataSize] array into memory using values
    * from a text file and passes this array back to 
    * the instance variable "tiles".
    *
    * @return a 2 dimensional array of type Tile containing the map information
    */
    public Tile [][] loadMapData ()
    {
        
        String fileIO;
        String [] mapInfo;
        
        Tile [][] tiles = new Tile[dataSize][dataSize];
        
        try 
        {
            BufferedReader mapReader = new BufferedReader(new FileReader("config/map.dat"));
            
            for (int i = 0; i < dataSize; i++)
            {
                for (int j = 0; j < dataSize; j++)
                {
                    fileIO = mapReader.readLine();
                    mapInfo = fileIO.split(" ");
                    tiles[i][j] = new Tile(i, j, Integer.parseInt(mapInfo[0]), Integer.parseInt(mapInfo[1]));
                }
            }
            
            mapReader.close();
            return tiles;
        }
        catch (IOException ex)
        {
            System.out.println("IOException: Map file not found OR trouble loading map array.");
            return null;
        }
    }
    
    /**
    * REQUIRES: a [dataSize x dataSize] array of information in a format
    * suitable for saving to a text file.
    *
    * EFFECTS: saves this map data into a text file.
    * 
    * @param tiles a 2 dimensional array of type Tile containing the map information
    */
    public void saveMapData (Tile[][] tiles)
    {
        
        String fileIO;
        
        try
        {
            BufferedWriter mapWriter = new BufferedWriter(new FileWriter("config/map.dat"));
            
            for (int i = 0; i < dataSize; i++)
            {
                for (int j = 0; j < dataSize; j++)
                {
                    fileIO = tiles[i][j].getTileType() + " " + (tiles[i][j].getTileHeight()) + System.lineSeparator();
                    mapWriter.write(fileIO);
                }
            }
            
            mapWriter.close();
        }
        catch (IOException ex)
        {
            System.out.println("IOException: Map data could not be saved.");
        }
    }
    /**
    * REQUIRES: an image file at ../images/player.png.
    *
    * EFFECTS: loads the image file into memory then cuts it into four
    * parts.  Each sub image represents the player facing in a different 
    * direction.
    * 
    * @return an array of type BufferedImage holding the player's look while facing four different directions
    */
    public BufferedImage [] loadPlayerImages ()
    {
        
        try 
        {
            BufferedImage rawTileImage = ImageIO.read(new File("images/player.png"));
            BufferedImage [] returnedPlayerSheet = new BufferedImage [4];
            
            for (int i = 0; i < 4; i++)
            {
                returnedPlayerSheet[i] = rawTileImage.getSubimage(40 * i, 0, 40, 45);
            }
            
            return returnedPlayerSheet;
        }
        catch (IOException ex)
        {
            System.out.println("IOException: Player image files not found.");
            return null;
        }
    }
    
    /**
    * REQUIRES: an image file at ../images/tiles.png.
    *
    * EFFECTS: loads the image file into memory then cuts it into 
    * (width of picture) / 40 pixels since each tile should be 40 pixels
    * in length.  Each sub image represents a different type of tile.
    * @return an array of type BufferedImage containing images that represent one of 46 different tiles
    */
    public BufferedImage [] loadTileImages ()
    {
        
        try 
        {
            BufferedImage rawTileImage = ImageIO.read(new File("images/tiles.png"));
            int numberOfTiles = rawTileImage.getWidth() / 40;
            BufferedImage [] returnedTileSheet = new BufferedImage [numberOfTiles];
            
            for (int i = 0; i < numberOfTiles; i++)
            {
                returnedTileSheet[i] = rawTileImage.getSubimage(40 * i, 0, 40, 60);
            }
            
            return returnedTileSheet;
        }
        catch (IOException ex)
        {
            System.out.println("IOException: Tile image files not found.");
            return null;
        }
    }
    /**
    * REQUIRES: a jPanel to draw on, a graphics context to draw with, 
    * the sub images representing both tiles and the player, as well as
    * the entity instance holding player information.  Objects cannot
    * be null.
    *
    * EFFECTS: draws the entire scene, tile by tile in top to bottom order so
    * the tiles are overlapping in the correct order.  Also draws the player
    * once it encounters the specific tile the player is drawn on.  Also 
    * draws "soil tiles" underneath any floating tiles.
    *
    * MODIFIES: this function draws on the jPanel, rendering it permanently
    * changed.  No other changes are made to input objects.
    * 
    * @param panel a JPanel to draw on
    * @param g a Graphics context to draw with
    * @param tileImages the set of sub-images that represent all possible tiles
    * @param playerImages the set of sub-images that represent all faced directions for the player
    * @param player the Entity instance that represents the player
    */
    public void drawScene (JPanel panel, Graphics g, BufferedImage [] tileImages, BufferedImage [] playerImages, Entity player)
    {
        
        int midTileLeft = panel.getWidth() / 2 - 20;
        int midTileTop = panel.getHeight() / 2 - 20;
        g.setColor(new Color(31, 31, 31));
        g.fillRect(0, 0, panel.getWidth() * 2, panel.getHeight() * 2);
        
        // For each displayable tile on the grid (0 to mapSize - 1.)
        
        for (int i = mapSize - 1;  i >= 0; i--)
        {
            for (int j = 0; j <= mapSize - 1; j++)
            {
                // If tile height is greater than a certain underground level, then you put more tiles under it to not show floating green/blue/yellow tiles.
                // This gives it an interesting look.
                
                if (tiles[i + offset[0]][j + offset[1]].getTileHeight() > -15)
                {
                    for (int k = -3; k < tiles[i + offset[0]][j + offset[1]].getTileHeight() / 5; k++)
                    {
                        g.drawImage(tileImages[6], 
                        midTileLeft + 20 * (i + j - mapSize + 1),
                        midTileTop + 10 * (j - i + 1) - 5 * k,
                        panel);
                    }
                }
                
                // Actually drawing the tile at (i, j).
                
                g.drawImage(tileImages[tiles[i + offset[0]][j + offset[1]].getTileType()], 
                        midTileLeft + 20 * (i + j - mapSize + 1),
                        midTileTop + 10 * (j - i + 1) - tiles[i + offset[0]][j + offset[1]].getTileHeight(),
                        panel);
                
                // If this specific tile has the player also on it, the player is drawn now rather than after the entire scene.
                // Provides an interesting 3d effect where the player might be hidden behind a tree or shrine.
                
                if (i == player.getX() && j == player.getY())
                {
                    drawPlayer(panel, g, playerImages, player);
                }
            }
        }
    }
    /**
    * REQUIRES: jPanel to draw on, graphics context to draw with, a set of 
    * images representing the player and the entity instance holding the
    * player's information.  Objects cannot be null.
    *
    * EFFECTS: draws the player's current avatar at the player's current
    * position on the grid.
    * 
    * @param panel a JPanel to draw on
    * @param g a Graphics context to draw with
    * @param playerImages the set of sub-images that represent all faced directions for the player
    * @param player the Entity instance that represents the player
    */
    public void drawPlayer(JPanel panel, Graphics g, BufferedImage [] playerImages, Entity player)
    {
        int midPlayerLeft = panel.getWidth() / 2 - 20;
        int midPlayerTop = panel.getHeight() / 2 - 20;
        g.drawImage(playerImages[player.getFacingDirection()],
                midPlayerLeft + 20 * (player.getX() + player.getY() - mapSize + 1) - 2,
                midPlayerTop + 10 * (player.getY() - player.getX()) - (tiles[player.getX() + offset[0]][player.getY() + offset[1]].getTileHeight()) + 3,
                panel);
    }
    
    /**
    * EFFECTS: returns map size.
    * 
    * @return mapSize the side length of the visible grid
    */
    public int getSize ()
    {
        return mapSize;
    }
    
    /**
    * EFFECTS: returns map data size.
    * 
    * @return mapSize the number of rows (or columns) that the data size array contains
    */
    public int getDataSize ()
    {
        return dataSize;
    }
    
    /**
     * EFFECTS: returns [dataSize x dataSize] array of tile information.
     * 
     * @return a 2 dimensional array containing the map's tile data
     */
    public Tile [][] getMapData ()
    {
        return tiles;
    }
    /**
     * REQUIRES: a boolean value.  If true, increases X offset.
     * Otherwise, decreases X offset.
     *
     * EFFECTS: used to change offset after the player has walked
     * towards the edge of walkable map.
     * 
     * @param increase if true, increases the offset and otherwise, decreases the offset.
     */
    public void changeOffsetX(boolean increase)
    {        
        if (increase)
        {
            offset[0]++;
        }
        else
        {
            offset[0]--;
            if (offset[0] < 0)
            {
                offset[0] = 0;
            }
        }
    }
    
    /**
     * REQUIRES: a boolean value.  If true, increases Y offset.
     * Otherwise, decreases Y offset.
     *
     * EFFECTS: used to change offset after the player has walked
     * towards the edge of walkable map.
     * 
     * @param increase if true, increases the offset and otherwise, decreases the offset.
     */
    public void changeOffsetY(boolean increase)
    {        
        if (increase)
        {
            offset[1]++;
        }
        else
        {
            offset[1]--;
            if (offset[1] < 0)
            {
                offset[1] = 0;
            }
        }
    }
    
    /**
     * EFFECTS: returns X offset value.
     * 
     * @return the x coordinate of the map offset
     */
    public int getOffsetX()
    {
        
        return offset[0];
    }
    
    /**
     * EFFECTS: returns Y offset value.
     * 
     * @return the y coordinate of the map offset
     */
    public int getOffsetY()
    {
        return offset[1];
    }
    
    /**
    * EFFECTS: if clipping is disabled, enable it.  If clipping is
    * conversely enabled, disable it.
    */
    public void switchClipping()
    {
        if (clipping)
        {
            clipping = false;
        }
        else
        {
            clipping = true;
        }
    }
    
    /**
    * EFFECTS: returns true if clipping is enabled, and false otherwise.
    * 
    * @return true if clipping is enabled, and false otherwise
    */
    public boolean clipping()
    {        
        return clipping;
    }
    
    /**
     * EFFECTS: returns true if debugging mode is enabled, and false
     * otherwise.
     * 
     * @return true if debugging mode is on, and otherwise false
     */
    public boolean usingDebug ()
    {
        if (clipping())
        {
            return false;
        }
        else
        {
            return debugStatus;
        }
    }
    /**
     * EFFECTS: if debugging mode is enabled, disable it.  Conversely, if
     * debugging mode is disabled, enable it.
     */
    public void switchDebug ()
    {        
        if (!debugStatus)
        {
            debugStatus = true;
        }
        else
        {
            debugStatus = false;
        }
    }
}
