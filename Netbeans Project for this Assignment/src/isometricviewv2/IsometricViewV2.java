/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isometricviewv2;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Nuradin Nur - 500460539 (BEng: Computer Engineering)
 */

/**
 * <b>COE 528: Object Oriented Engineering Analysis - Final Project <br>
 * A Simple Map Editor Using Isometric Projection </b><br>
 *  <br> <br>
 *  <br>
 * A quick overview: <br>
 * - I wrote the basic functionality of this program in C about two 
 * years ago out of interest. (Graphics library used was SDL) <br>
 * - I chose to rewrite this program in Java for this project. <br>
 * - There are many improvements and new functionality is implemented. <br>
 *  <br>
 * Controls in normal mode: <br>
 * Arrow keys   -   moves your avatar <br>
 * C            -   disables clipping and enables editing mode <br>
 *  <br>
 * Controls in editing mode: <br>
 * W and S      -   changes the height of the tile you're standing on <br>
 * A and D      -   changes the style of the tile you're standing on <br>
 * X            -   enables or disables debugging mode <br>
 * C            -   disables editing mode and enables clipping <br>
 * V            -   clones the tile at your previous position onto your
 *                  current position's tile <br>
 *  <br>
 * Improvements over previous C iteration: <br>
 * - Floating tiles are held up by n-amount of soil layers <br>
 * - The ground level is also held up by 3 soil layers <br>
 * - Clipping works in all circumstances now <br>
 * - Movement is not delayed due to the delay caused by holding down a key <br>
 * - Clone tile function does not clone the wrong tile after moving near the 
 *   edges of the screen <br>
 * - Editing mode shows frames per second, current tile properties and location <br>
 * - Debugging mode created: shows the id of all non-blank tiles on screen <br>
 * - Generally more pleasing to the eyes now <br>
 ******************************************************************************/

public class IsometricViewV2
{
    private static Boolean running = true;

    public static void main(String args[]) throws InterruptedException
    {
        // Initializing what we can, so far, at the beginning of the program.
        
        int saveCycle = 0;
        Map map = new Map (20);
        Entity player = new Entity (7, 6);
        InfoLog log = new InfoLog (map, player);
        BufferedImage [] tileSheet = map.loadTileImages();
        BufferedImage [] playerSheet = map.loadPlayerImages();
        KeyInput keystrokeListener = new KeyInput(player, map);
        
        // Initializes a window then sets up important parameters.
        
        JFrame gameframe = new JFrame();
        JPanel gamepanel = new JPanel();
        
        gameframe.setPreferredSize(new java.awt.Dimension(850, 525));
        gameframe.setTitle("Isometric Projection V2 (Rewritten in Java)");
        gameframe.setResizable(false);
        gameframe.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        gameframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                map.saveMapData(map.getMapData());
                gameframe.dispose();
                running = false;
            }
        });
        gameframe.getContentPane().add(gamepanel);
        gameframe.pack();
        gameframe.setVisible(true);
        gameframe.createBufferStrategy(2);
        
        gamepanel.addKeyListener(keystrokeListener);
        gamepanel.requestFocusInWindow();
        
        // Double-buffering strategy.  If you don't know what this is:
        // There are two screens.  One screen, A, is behind the second screen,
        // B and B has the next frame drawn onto it.  B is then moved to the
        // front and A has the next frame drawn onto it.  The process repeats
        // indefinitely.  What does this accomplish?  It allows us to create
        // the illusion of continuous movement using a finite number of frames
        // without flickering.
       
        BufferStrategy buffer = gameframe.getBufferStrategy();
        
        // This is our rendering loop.  One iteration of this entire loop
        // constitutes one frame in the game window.  Everything that needs to
        // be done repeatedly goes here, like rendering every frame, calculating
        // values that need to be calculated often like the movement cooldown, etc.
        
        System.out.println(player.repOk() + "" + map.repOk() + "" + log.repOk() + "" + keystrokeListener.repOk() + map.getMapData()[0][0].repOk());
        
        
        while (running)
        {
            // Start time in nanoseconds for current frame.
            
            long currentTime = System.nanoTime();
            
            // Save the map every 300 frames.  This is every 5-6 seconds.
            
            saveCycle++;
            if (saveCycle > 300)
            {
                map.saveMapData(map.getMapData());
            }
            
            // If the player still has a movement cooldown active, decrement it.
            // Otherwise move the character if he is still holding down a key.
            
            player.decMovementCooldown();
            if (player.getMovementCooldown() == 0)
            {
                player.moveEntity(player.getFacingDirection(), player, map);
            }

            // The graphics are done here.  Instantiate a Graphics class, draw
            // the game graphics first and then update the strings used in the
            // information log.  Then draw the information log if required and
            // dispose of the graphics context.  Finally, show last buffer.
            
            Graphics graphics = buffer.getDrawGraphics();
            map.drawScene(gamepanel, graphics, tileSheet, playerSheet, player);
            if (!map.clipping())
            {
                log.updateLog(currentTime);
                log.drawLog(graphics, gamepanel);
            }
            graphics.dispose();
            buffer.show();
            
            // Sleep until the time left of the current frame is used up.
            
            while (System.nanoTime() < currentTime + 1000000000 / 60)
            {
                Thread.sleep(1);
            }
        }
    }                
}