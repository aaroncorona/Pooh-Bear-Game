package com.example.poohbearsnakegame;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class SnakePanel extends JPanel implements ActionListener {

    // Constants
    static final int UNIT_SIZE = 30;
    static final int SCREEN_WIDTH = (UNIT_SIZE*35);
    static final int SCREEN_HEIGHT = (UNIT_SIZE*25);;
    static final int DELAY = 100; // milliseconds between each frame rendering (ie what the Timer counts down between creating a new Frame object)
    static final int WINNING_SCORE = 33;
    static final int BODY_PARTS_PER_APPLE = 10;
    static final int STARTING_BODY_PARTS = 10; // length of the snake (head + body), start as 3

    // Flags to trigger events
    static boolean nitro = false; // flag for if the snake is moving on nitro
    static boolean running = false; // flag for if the snake is moving
    static boolean initial_pause = true; // flag for if the snake is moving
    static boolean pause = false;
    static boolean stopHs = false; // To prevent the timer from continually printing the final score
    static boolean win = false;

    // Static tracking variables
    static long startTime;
    static Color[] randomColorArray;
    static long[][] highScoreArray;
    static int finalScore = 0;
    static int snake_x_coordinate[] = new int[WINNING_SCORE * (STARTING_BODY_PARTS + 5)]; //2000 is an arbitrary max size of the snake in width (x coordinate length)
    static int snake_y_coordinate[] = new int[WINNING_SCORE * (STARTING_BODY_PARTS + 5)]; //2000 is an arbitrary max size of the snake in height (y coordinate length)
    static int totalBodyParts = STARTING_BODY_PARTS;
    static int applesEatenScore = 0; // start at 0, then increment everytime the snake head is on the same coordinate as the apple
    static int apple_x_coordinate;
    static int apple_y_coordinate;
    static int elapsedMins; // Make public
    static int elapsedSecondsRemainder; // Make public
    static char direction = 'R'; //the direction the snake head moves every delay
    static char oldDirection = 'R';
    static Timer myTimer;

    // Menus
    // Pause Menu
    public static JPopupMenu pauseMenu = new JPopupMenu();
    public static JLabel pauseMenuLabel = new JLabel("GAME PAUSED - Press Space to Resume");
    // Game Over Menu
    public static JPopupMenu gameOverMenu = new JPopupMenu();
    public static JLabel gameOverMenuLabel1 = new JLabel(" "); //buffer
    public static JLabel gameOverMenuLabel2 = new JLabel("   * GAME OVER *    ");
    public static JLabel gameOverMenuLabel3 = new JLabel("Press Enter to Restart");
    public static JLabel gameOverMenuLabel4 = new JLabel("Press Delete to Quit");
    // Win Menu
    public static JPopupMenu winMenu = new JPopupMenu();
    // Control Menu
    public static JPopupMenu controlMenu = new JPopupMenu();
    // High Score Menu
    public static JPopupMenu highScoreMenu = new JPopupMenu();
    public static JLabel highScoreMenuLabel = new JLabel(); //buffer

    // Buttons
    JButton pauseButton = new JButton();
    JButton restartButton = new JButton();
    JButton quitButton = new JButton();

    // Create game panel (constructor)
    SnakePanel() {

        // Panel details
        Color backgroundColor = new Color(205,159,89);
        this.setBackground(backgroundColor);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setFocusable(true); // Events are only dispatched to the component that has focus. So your KeyEvent will only be dispatched to the panel if it is "focusable"

        // Add Timer
        myTimer = new Timer(DELAY, this); // creates a new object (this) after every delay time
        myTimer.start(); // Start timer to begin creating objects after every delay, this makes the actionPerformed function keep running its inner functions

        // Add Restart button on instantiation + the action listener (which is part of the class)
        restartButton.addActionListener(this);
        this.add(restartButton);
        restartButton.setText("Restart Game");
        restartButton.setSize(100, 50);
        restartButton.setFocusable(false); // Prevent the button from taking focus from the panel

        // Add Pause button on instantiation + the action listener (which is part of the class)
        pauseButton.addActionListener(this);
        this.add(pauseButton);
        pauseButton.setText("Pause Game");
        pauseButton.setSize(100, 50);
        pauseButton.setFocusable(false); // Prevent the button from taking focus from the panel

        // Add Close button on instantiation + the action listener (which is part of the class)
        quitButton.addActionListener(this);
        this.add(quitButton);
        quitButton.setText("Quit Game");
        quitButton.setSize(100, 50);
        quitButton.setFocusable(false); // Prevent the button from taking focus from the panel

        // Create Control Menu (always displayed)
        JPopupMenu controlMenu = new JPopupMenu();
        controlMenu.setLocation(1100,140);
        controlMenu.setBackground(Color.orange);
        controlMenu.setBorder(BorderFactory.createLineBorder(Color.white));
        controlMenu.setFocusable(false); // Prevent the menu from taking focus from the panel
        // Create Game Over Menu Labels
        JLabel controlMenuLabel1 = new JLabel(" Controls: "); //buffer
        controlMenuLabel1.setFont(new Font("Verdana", Font.PLAIN, 12));
        controlMenuLabel1.setForeground(Color.BLACK);
        controlMenuLabel1.setAlignmentX(CENTER_ALIGNMENT);
        controlMenuLabel1.setAlignmentY(CENTER_ALIGNMENT);
        controlMenu.add(controlMenuLabel1);
        JLabel controlMenuLabel2 = new JLabel("Press Space to Pause");
        controlMenuLabel2.setFont(new Font("Verdana", Font.PLAIN, 10));
        controlMenuLabel2.setForeground(Color.BLACK);
        controlMenuLabel2.setAlignmentX(CENTER_ALIGNMENT);
        controlMenuLabel2.setAlignmentY(CENTER_ALIGNMENT);
        controlMenu.add(controlMenuLabel2);
        JLabel controlMenuLabel3 = new JLabel("   Press Enter to Restart   ");
        controlMenuLabel3.setFont(new Font("Verdana", Font.PLAIN, 10));
        controlMenuLabel3.setForeground(Color.BLACK);
        controlMenuLabel3.setAlignmentX(CENTER_ALIGNMENT);
        controlMenuLabel3.setAlignmentY(CENTER_ALIGNMENT);
        controlMenu.add(controlMenuLabel3);
        JLabel controlMenuLabel4 = new JLabel("Press Delete to Quit");
        controlMenuLabel4.setFont(new Font("Verdana", Font.PLAIN, 10));
        controlMenuLabel4.setForeground(Color.BLACK);
        controlMenuLabel4.setAlignmentX(CENTER_ALIGNMENT);
        controlMenuLabel4.setAlignmentY(CENTER_ALIGNMENT);
        controlMenu.add(controlMenuLabel4);
        controlMenu.setVisible(true); // Reveal control menu

        // Map Keys to Action responses
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),"rightAction"); // KeyEvent responds to a right key, lower case, and false for it being pressed rather than released
        this.getActionMap().put("rightAction", new RightAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true),"rightReleaseAction"); // KeyEvent responds to a right key, lower case, and false for it being pressed rather than released
        this.getActionMap().put("rightReleaseAction", new ReleaseAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),"leftAction");
        this.getActionMap().put("leftAction", new LeftAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true),"leftReleaseAction"); // KeyEvent responds to a right key, lower case, and false for it being pressed rather than released
        this.getActionMap().put("leftReleaseAction", new ReleaseAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),"upAction");
        this.getActionMap().put("upAction", new UpAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true),"upReleaseAction"); // KeyEvent responds to a right key, lower case, and false for it being pressed rather than released
        this.getActionMap().put("upReleaseAction", new ReleaseAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),"downAction");
        this.getActionMap().put("downAction", new DownAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),"downReleaseAction"); // KeyEvent responds to a right key, lower case, and false for it being pressed rather than released
        this.getActionMap().put("downReleaseAction", new ReleaseAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),"enterAction");
        this.getActionMap().put("enterAction", new EnterAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false),"deleteAction");
        this.getActionMap().put("deleteAction", new DeleteAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),"spaceAction");
        this.getActionMap().put("spaceAction", new SpaceAction());

    }

    public static void startGame() {
        resetSnake();
        generateNewAppleCoordinates(); // Create an apple when the game begins
        pauseMenu.setVisible(false); // Close menu in case it's open
        gameOverMenu.setVisible(false); // Close menu in case it's open
        winMenu.setVisible(false); // Close menu in case it's open
        initial_pause = false; // reset (to remove initial pause menu)
        pause = false; // reset (to remove the pause menu if restarted during a pause)
        stopHs = false; // reset (to allow final score to be printed again if restarted)
        win = false; // reset (so the running can be true)
        running = true; // Launch graphic drawings and action listener

        // Fill Color Array (for the snake tail colors)
        fillTailColorArray();

        // Start stopwatch
        startTime = System.currentTimeMillis();
    }

    @ Override
    // Runs automatically on Frame rendering to execute drawEverything()
    public void paint (Graphics g) {
        super.paint(g);

        // Image Icons
        // Make the background pooh bear
        ImageIcon poohBackgroundIcon = new ImageIcon(new ImageIcon("/Users/aaroncorona/eclipse-workspace/Pooh-Bear-Snake-Game/src/assets/images/PoohBackground.png").getImage().getScaledInstance(450, 300, Image.SCALE_DEFAULT));
        poohBackgroundIcon.paintIcon(this, g, 310, 220);

        // Make the apple a honey pot
        ImageIcon honeyIcon = new ImageIcon(new ImageIcon("/Users/aaroncorona/eclipse-workspace/Pooh-Bear-Snake-Game/src/assets/images/Honey.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_DEFAULT));
        honeyIcon.paintIcon(this, g, apple_x_coordinate, apple_y_coordinate);
        drawEverything(g); // Put this after so they go on top of the images

        // Make the snake head a picture of pooh bear (head goes over the snake)
        ImageIcon poohHeadIcon = new ImageIcon(new ImageIcon("/Users/aaroncorona/eclipse-workspace/Pooh-Bear-Snake-Game/src/assets/images/PoohHead.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_DEFAULT));
        poohHeadIcon.paintIcon(this, g, snake_x_coordinate[0], snake_y_coordinate[0]);

        // Fill over first box that fills with the spawning snake color to a tree instead (this is the top layer)
        ImageIcon treeIcon = new ImageIcon(new ImageIcon("/Users/aaroncorona/eclipse-workspace/Pooh-Bear-Snake-Game/src/assets/images/Tree.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_DEFAULT));
        treeIcon.paintIcon(this, g, 0, 0);
    }

    public static void drawEverything(Graphics g) {

        // Draw the Snake initial body (yellow)
        for(int i=1; i<=STARTING_BODY_PARTS; i++) { // for the next loops for the initial body, make the rectangles yellow
            g.setColor(Color.yellow); // color the starting body yellow
            g.fillRect(snake_x_coordinate[i], snake_y_coordinate[i], UNIT_SIZE, UNIT_SIZE); // keep the apple size consistent with the snake
        }

        // Draw the snake tail (rest of the body). Each new block of 10 gets a random color
        for(int i=STARTING_BODY_PARTS+1; i <= totalBodyParts; i++) { //loop for every 1 added?
            g.setColor(randomColorArray[i]);
            g.fillRect(snake_x_coordinate[i], snake_y_coordinate[i], UNIT_SIZE, UNIT_SIZE); // keep the apple size consistent with the snake
        }

        // Display Initial pause message
        if(initial_pause == true) {
            displayInitialPause(g);
        }

        // Display current score
        displayScore(g);

        // Display stop watch
        displayStopWatch(g);

        // Display nitro boost message
        if(nitro == true) {
            displayNitro(g);
        }
    }

    public static void generateNewAppleCoordinates(){ //populates new coordinates (int variable values) for an apple, which is then created with draw (graphics object)
        apple_x_coordinate = new Random().nextInt((int) (SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE; //get random coordinate within the boundary of the unit fully fitting (use division), while also being an exact coordinate (i.e. divisable by the unit size, so use multiplication)
        apple_y_coordinate = new Random().nextInt((int) (SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE; //get random coordinate within the boundary of the unit fully fitting (use division), while also being an exact coordinate (i.e. divisable by the unit size, so use multiplication)

        // Generate a new apple if the apple spawns on one of the walls
        if(apple_x_coordinate == 0 || apple_y_coordinate == 0
                || apple_x_coordinate == SCREEN_WIDTH || apple_y_coordinate == SCREEN_HEIGHT) {
            generateNewAppleCoordinates();
        }
    }

    public static void nitroOn() {
        nitro = true;
        myTimer.setDelay(15); //Hyperspeed rendering
    }

    public static void nitroOff() {
        nitro = false;
        myTimer.setDelay(DELAY);
    }

    public static void pauseGame() { //
        pause = true;
        running = false;
        // Create Pause Menu
        // JPopupMenu pauseMenu = new JPopupMenu();
        pauseMenu.setLocation(625, 400);
        pauseMenu.setPreferredSize(new Dimension(450, 30));
        pauseMenu.setBackground(Color.green.darker());
        pauseMenu.setBorder(BorderFactory.createLineBorder(Color.white));
        pauseMenu.setFocusable(false); // Prevent the menu from taking focus from the panel
        // Create Pause Menu Label
        pauseMenuLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        pauseMenuLabel.setForeground(Color.WHITE);
        pauseMenuLabel.setAlignmentX(CENTER_ALIGNMENT);
        pauseMenuLabel.setAlignmentY(CENTER_ALIGNMENT);
        pauseMenu.add(pauseMenuLabel);
        pauseMenu.setVisible(true);
    }

    public static void resumeGame() { //
        pause = false;
        running = true;
        pauseMenu.setVisible(false);
    }

    public static void quitGame() { //
        System.exit(0);
    }

    public static void resetSnake() { //
        for(int i=0; i <= totalBodyParts; i++) {
            snake_x_coordinate[i] = 0; // put the snake back in the top left corner where it should always spawn
            snake_y_coordinate[i] = 0;
        }
        direction = 'R'; // Always move right to avoid a collision
        oldDirection = 'R';
        totalBodyParts = STARTING_BODY_PARTS;
        applesEatenScore = 0;
    }

    public static void moveSnake() { // Continually update the snake coordinates
        // Set coordinates for the body of the snake body based on the body part next to it
        for(int i = totalBodyParts; i>0; i--) {
            snake_x_coordinate[i] = snake_x_coordinate[i-1]; // set the location of the array (body part) to equal the next array
            snake_y_coordinate[i] = snake_y_coordinate[i-1]; // set the location of the array (body part) to equal the next array
        }

        // Change position of the head using the direction variable (which is updated by the keyboard)
        switch(direction) {
            case 'R':
                snake_x_coordinate[0] = snake_x_coordinate[0] + UNIT_SIZE;
                oldDirection = direction; // Update previous direction to allow the next key input to read this and avoid 180 collisions
                break;
            case 'L':
                snake_x_coordinate[0] = snake_x_coordinate[0] - UNIT_SIZE;
                oldDirection = direction; // Update previous direction to allow the next key input to read this and avoid 180 collisions
                break;
            case 'U':
                snake_y_coordinate[0] = snake_y_coordinate[0] - UNIT_SIZE; //coordinates are backwards compared to a regular map
                oldDirection = direction; // Update previous direction to allow the next key input to read this and avoid 180 collisions
                break;
            case 'D':
                snake_y_coordinate[0] = snake_y_coordinate[0] + UNIT_SIZE;
                oldDirection = direction; // Update previous direction to allow the next key input to read this and avoid 180 collisions
                break;
        }
    }

    public static void checkAppleEaten() { // If the snake head or body is at the same coordinates of the apple, increment the snake body size by 10, increment the score, and generate new apple coordinates
        for(int i=0; i <= totalBodyParts; i++) {
            if(snake_x_coordinate[i] == apple_x_coordinate
                    && snake_y_coordinate[i] == apple_y_coordinate) {
                applesEatenScore++;
                totalBodyParts = totalBodyParts + BODY_PARTS_PER_APPLE;
                checkWin();
                generateNewAppleCoordinates();
            }
        }
    }

    public static void checkStopWatch() {
        long now = System.currentTimeMillis();
        int elapsedTime = (int) (now - startTime); // Convert timestamp difference to seconds
        elapsedMins = (int) Math.floor(elapsedTime / 1000 / 60);
        elapsedSecondsRemainder = (int) Math.floor(elapsedTime / 1000 % 60);
    }

    public static void checkCollisions() { //checks if the snake head collides with the snake body or the boundary
        // check for body collision (loop needed to see if it hits any body part)
        for(int i = totalBodyParts; i>0; i--) { // start at the snake tail (largest array number), then iterate down in the array until the array before the head
            if((snake_x_coordinate[0] == snake_x_coordinate[i])
                    && (snake_y_coordinate[0] == snake_y_coordinate[i])) { // true if the snake head coordinates equal a snake body coordinate
                running = false; // stop the game (triggers end game message)
                running = false; // stop the game (triggers end game message)
                System.out.println("* GAME OVER (Body Collision)");
            }
        }
        // check for the head colliding with one of the 4 borders
        if(   (snake_x_coordinate[0] < 0)                // left border // 0 is where it starts
                || (snake_x_coordinate[0] >= SCREEN_WIDTH)     // right border
                || (snake_y_coordinate[0] < 0)                // bottom border
                || (snake_y_coordinate[0] >= SCREEN_HEIGHT)) { // top border
            running = false; // stop the game (triggers end game message)
            System.out.println("* GAME OVER (Wall Collision)");
        }
    }

    public static void checkWin() {
        // Win by reaching the winning score
        if(applesEatenScore == WINNING_SCORE) {
            finalScore = applesEatenScore; // Update final score
            running = false; // Stop the game
            win = true; // Trigger win menu
        }
        // Alternate win by filling the edges
        // (top border) check if x edges are filled by the snake
        HashSet snakeXCoordinatesNoDupsTopBorder = new HashSet(); // Deduped array // HashSet is a collection of items where every item is unique // The capacity of an Array is fixed. Whereas ArrayList can increase and decrease size dynamically
        for(int i = 0; i < snake_x_coordinate.length; i++) { // start at 1 to avoid matching with 0
            if(snake_y_coordinate[i] == 0) {
                snakeXCoordinatesNoDupsTopBorder.add(snake_x_coordinate[i]); // Add each unique coordinate
            }
        }
        // (bottom border) check if x edges are filled by the snake
        HashSet snakeXCoordinatesNoDupsBottomBorder = new HashSet(); // Deduped array
        for(int i = 0; i < snake_x_coordinate.length; i++) { // start at 1 to avoid matching with 0
            if(snake_y_coordinate[i] == (SCREEN_HEIGHT - UNIT_SIZE)) {
                snakeXCoordinatesNoDupsBottomBorder.add(snake_x_coordinate[i]); // Add each unique coordinate
            }
        }
        // (left border) check if x edges are filled by the snake
        HashSet snakeYCoordinatesNoDupsLeftBorder = new HashSet(); // Deduped array
        for(int i = 0; i < snake_y_coordinate.length; i++) { // start at 1 to avoid matching with 0
            if(snake_x_coordinate[i] == 0) {
                snakeYCoordinatesNoDupsLeftBorder.add(snake_y_coordinate[i]); // Add each unique coordinate
            }
        }
        // (right border) check if x edges are filled by the snake
        HashSet snakeYCoordinatesNoDupsRightBorder = new HashSet(); // Deduped array
        for(int i = 0; i < snake_y_coordinate.length; i++) { // start at 1 to avoid matching with 0
            if(snake_x_coordinate[i] == 1280) {
                snakeYCoordinatesNoDupsRightBorder.add(snake_y_coordinate[i]); // Add each unique coordinate
            }
        }

        int xAxisUnitLength = SCREEN_WIDTH/UNIT_SIZE;
        int yAxisUnitLength = SCREEN_HEIGHT/UNIT_SIZE;

        if(snakeXCoordinatesNoDupsTopBorder.size() == xAxisUnitLength
                && snakeXCoordinatesNoDupsBottomBorder.size() == xAxisUnitLength
                && snakeYCoordinatesNoDupsLeftBorder.size() == yAxisUnitLength
                && snakeYCoordinatesNoDupsRightBorder.size() == yAxisUnitLength
                && applesEatenScore >= 10
        ) {
            System.out.println("You filled all the sides and ate an apple! You win!");
            applesEatenScore = WINNING_SCORE;
            finalScore = applesEatenScore; // Update final score
            running = false; // Stop the game
            win = true; // Trigger win message
        }
    }

    public static void displayInitialPause(Graphics g) { // Display the current score
        g.setColor(Color.GRAY);
        g.setFont(new Font("Serif", Font.ITALIC, 50));
        g.drawString("Press Enter to begin",330,200);
    }

    public static void displayScore(Graphics g) { // Display the current score
        g.setColor(Color.blue.brighter());
        g.setFont(new Font("Serif", Font.PLAIN, 50));
        g.drawString("Honeys Eaten: " + applesEatenScore,30,80); // coordinates start in the top left
    }

    public static void displayStopWatch(Graphics g) { // Display the current score
        g.setColor(Color.orange.brighter());
        g.setFont(new Font("Serif", Font.PLAIN, 25));
        g.drawString("Time Elapsed: " + elapsedMins + " Mins and " + elapsedSecondsRemainder + " Seconds",30,112); // coordinates start in the top left
    }

    public static void displayNitro(Graphics g) { // Display the current score
        g.setColor(Color.yellow);
        g.setFont(new Font("Serif", Font.PLAIN, 50));
        g.drawString("NITRO ON",400,300); // coordinates start in the top left
    }

    public static void fillTailColorArray() {
        // Reset/create array
        randomColorArray = new Color[1000]; // Shell for an array that can hold enough colors for every snake tail addition
        for(int i=STARTING_BODY_PARTS+1; i <= 999; i++) { //start at the value of the initial tail (11th position in the array after the head and inital 10 body)
            Random rando = new Random((int) Math.floor(i/(STARTING_BODY_PARTS +0.01))); // create a random object using an int seed (i/10) that changes every 10 loops (w rouding) so that the Random object only updates to a new seed benchmark every tail addition / apple eaten (0.01 is for rounding so the random number seed changes at the 11th variable like 21, 31, etc)
            randomColorArray[i] = new Color(rando.nextInt(255), rando.nextInt(255), rando.nextInt(255)); // every loop generates a color object into the array by taking the next random int in the random sequence. The random sequence of numbers is the same (e.g. 10, 20, 30), but updates every 10 loops, thus creating a different color every 10
        }
    }

    // Append final score to text file
    public static void logFinalScore() {
        try {
            // Create or append file
            FileWriter fw = new FileWriter("/Users/aaroncorona/eclipse-workspace/Pooh-Bear-Snake-Game/src/assets/snake_high_scores.csv", true); // FileWriter append mode is triggered with true (also creates new file if none exists)
            PrintWriter write = new PrintWriter(fw);
            // Print the score to the csv file and the time on the column next to it
            write.println(); // Skip to new row
            write.print(finalScore);
            write.print(","); // comma separate to print to the next column
            write.print(System.currentTimeMillis()); // print current date
            // Close and finish the job
            write.close();
        } catch(IOException e){
            System.out.print(e);
        }
    }

    // Read high score file
    public static void generateHighScoreArray() {
        // Reset/create Array
        highScoreArray = new long[5000][2];
        try {
            // Create file object
            File fileObj = new File("/Users/aaroncorona/eclipse-workspace/Pooh-Bear-Snake-Game/src/assets/snake_high_scores.csv");
            // Create scanner object
            Scanner myScanner = new Scanner(fileObj);
            myScanner.useDelimiter("\\n|,|\\s*\\$"); // Treats commas and whitespace as dilimiters to read the csv properly (\n = line break, s = whitespace, $ = until end of line, an anchor to ensure that the entire string is matched instead of just a substring). Reads results as string
            // Fill Array by reading the file
            for(int i = 0; myScanner.hasNext(); i++) { // loop for rows
                for(int a = 0; a <= 1; a++) { // loop for columns
                    highScoreArray[i][a] = ((long)Long.parseLong(myScanner.next().trim()));
                }
            }
            // Sort Array in ascending order
            Arrays.sort(highScoreArray, Comparator.comparingDouble(a -> a[0])); // Lamba function that compares numbers
            myScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred finding the file.");
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { //Listener object to run methods (runs automatically). This reacts to timer and keeps iterating and running the inner functions, otherwise the snake move would just run once
        if(running) {
            moveSnake(); // constantly update the snake coordinates, which the draw function then responds to
            checkAppleEaten();
            checkCollisions();
            checkStopWatch();
            repaint(); // function that continually updates the graphics according to the new variables (ie for each timer object that reruns these functions, repaint must also be called)
        }

        // When the game ends (loss or win), log the final score if the game ends with a minimum score achieved, then print final score info
        if(running == false && pause == false && initial_pause == false && stopHs == false) {

            // Update the final score variable
            finalScore = applesEatenScore;

            // Log final score in the CSV file if it's past a certain minimum
            if(finalScore >= 20) {
                logFinalScore();
            }

            // Generate Array of Scores from the current CSV file
            generateHighScoreArray();

            // Get info on the top 3 scores
            int score1 = (int) highScoreArray[highScoreArray.length-1][0]; //Array is sorted in ascending order
            Timestamp ts1 = new Timestamp(highScoreArray[highScoreArray.length - 1][1]);
            int score2 = (int) highScoreArray[highScoreArray.length-2][0];
            Timestamp ts2 = new Timestamp(highScoreArray[highScoreArray.length-2][1]);
            int score3 = (int) highScoreArray[highScoreArray.length-3][0];
            Timestamp ts3 = new Timestamp(highScoreArray[highScoreArray.length-3][1]);

            // Special message if the player reached a top 3 high score
            System.out.println("* Your final score is " + finalScore); // Array is sorted in ascending order
            if(finalScore > score3 && finalScore >= 20) {
                System.out.println("* CONGRATS! That's a new high score. That puts you at top 3 all time."); // Array is sorted in ascending order
            } else {
                System.out.println("* Sorry, your score was not good enough for top 3 all time."); // Array is sorted in ascending order
            }

            // Show top 3 high scores and the times they were achieved
            System.out.println("1st place: " + score1 + " on " + ts1);
            System.out.println("2nd place: " + score2 + " on " + ts2);
            System.out.println("3rd place: " + score3 + " on " + ts3);

            // End this process
            stopHs = true; // Set to true, which is a workaround to make sure this only happens oncee

            // Lose game Menu
            if(running == false && pause == false && initial_pause == false && finalScore < WINNING_SCORE) {
                // Create Game Over Menu
                gameOverMenu.setLocation(600,300);
                gameOverMenu.setBackground(Color.red);
                gameOverMenu.setBorder(BorderFactory.createLineBorder(Color.white));
                gameOverMenu.setFocusable(false); // Prevent the menu from taking focus from the panel
                // Create Game Over Menu Labels
                gameOverMenuLabel1.setFont(new Font("Verdana", Font.PLAIN, 30)); // Buffer
                gameOverMenuLabel1.setForeground(Color.WHITE);
                gameOverMenuLabel1.setAlignmentX(CENTER_ALIGNMENT);
                gameOverMenuLabel1.setAlignmentY(CENTER_ALIGNMENT);
                gameOverMenu.add(gameOverMenuLabel1);
                gameOverMenuLabel2.setFont(new Font("Verdana", Font.PLAIN, 50));
                gameOverMenuLabel2.setForeground(Color.WHITE);
                gameOverMenuLabel2.setAlignmentX(CENTER_ALIGNMENT);
                gameOverMenuLabel2.setAlignmentY(CENTER_ALIGNMENT);
                gameOverMenu.add(gameOverMenuLabel2);
                gameOverMenuLabel1.setFont(new Font("Verdana", Font.PLAIN, 30)); // Buffer
                gameOverMenuLabel1.setForeground(Color.WHITE);
                gameOverMenuLabel1.setAlignmentX(CENTER_ALIGNMENT);
                gameOverMenuLabel1.setAlignmentY(CENTER_ALIGNMENT);
                gameOverMenu.add(gameOverMenuLabel1);
                gameOverMenuLabel3.setFont(new Font("Verdana", Font.PLAIN, 30));
                gameOverMenuLabel3.setForeground(Color.WHITE);
                gameOverMenuLabel3.setAlignmentX(CENTER_ALIGNMENT);
                gameOverMenuLabel3.setAlignmentY(CENTER_ALIGNMENT);
                gameOverMenu.add(gameOverMenuLabel3);
                gameOverMenuLabel4.setFont(new Font("Verdana", Font.PLAIN, 30));
                gameOverMenuLabel4.setForeground(Color.WHITE);
                gameOverMenuLabel4.setAlignmentX(CENTER_ALIGNMENT);
                gameOverMenuLabel4.setAlignmentY(CENTER_ALIGNMENT);
                gameOverMenu.add(gameOverMenuLabel4);
                gameOverMenuLabel1.setFont(new Font("Verdana", Font.PLAIN, 30)); // Buffer
                gameOverMenuLabel1.setForeground(Color.WHITE);
                gameOverMenuLabel1.setAlignmentX(CENTER_ALIGNMENT);
                gameOverMenuLabel1.setAlignmentY(CENTER_ALIGNMENT);
                gameOverMenu.add(gameOverMenuLabel1);
                gameOverMenu.setVisible(true);

                // Create High Score Menu
                JPopupMenu highScoreMenu = new JPopupMenu();
                highScoreMenu.setLocation(1000,60);
                highScoreMenu.setBackground(Color.orange.darker());
                highScoreMenu.setBorder(BorderFactory.createLineBorder(Color.white));
                highScoreMenu.setFocusable(false); // Prevent the menu from taking focus from the panel
                highScoreMenuLabel.setFont(new Font("Verdana", Font.PLAIN, 30)); // Buffer
                highScoreMenuLabel.setForeground(Color.WHITE);
                highScoreMenuLabel.setAlignmentX(CENTER_ALIGNMENT);
                highScoreMenuLabel.setAlignmentY(CENTER_ALIGNMENT);
                highScoreMenu.add(highScoreMenuLabel);
                highScoreMenu.setVisible(true); // Prevent the menu from taking focus from the panel
            }

            // Win game Menu
            if(win == true) {
                // Create Win Game Menu
                JPopupMenu winMenu = new JPopupMenu();
                winMenu.setLocation(600,300);
                winMenu.setBackground(Color.pink);
                winMenu.setBorder(BorderFactory.createLineBorder(Color.white));
                winMenu.setFocusable(false); // Prevent the menu from taking focus from the panel
                // Create Game Over Menu Labels
                JLabel gameWinMenuLabel1 = new JLabel(" YOU WIN! "); //buffer
                gameWinMenuLabel1.setFont(new Font("Verdana", Font.PLAIN, 50));
                gameWinMenuLabel1.setForeground(Color.WHITE);
                gameWinMenuLabel1.setAlignmentX(CENTER_ALIGNMENT);
                gameWinMenuLabel1.setAlignmentY(CENTER_ALIGNMENT);
                winMenu.add(gameWinMenuLabel1);
                winMenu.setVisible(true);
            }

            // Check for the pause button being pressed to pause or resume the game
            if (e.getSource()==pauseButton){
                if(pause == false && initial_pause == false && running == true) { // odd number means the game should be paused  // % operator returns the remainder of two numbers
                    pauseGame();
                }
                else if (pause == true && initial_pause == false && running == false) { // even number means the game should be resumed
                    resumeGame();
                }
            }

            // Check for the restart button being pressed (only activate if game is paused or over)
            if (e.getSource()==restartButton && running == false){
                startGame(); // Restart
            }

            // Check for the quit button being pressed
            if (e.getSource()==quitButton && running == false){
                quitGame();
            }

            // Continually rerun the graphics
            repaint();
        }
    }

    // Define actions to be performed (these map to key strokes)
    public static class RightAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(oldDirection != 'L') { // Make sure the snake can't do a 180 if a player presses the opposit direction, which would end the game
                direction = 'R';	// oldDirection makes sure the previous movement log actually happens (because that variable is updated by the movement method)
            }
            if(oldDirection == 'R' && direction == 'R') { // Nitro boost from clicking the same direction twice
                nitroOn();
            }
        }
    }
    public static class ReleaseAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            nitroOff();
        }
    }
    public static class LeftAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(oldDirection != 'R') {
                direction = 'L';
            }
            if(oldDirection == 'L' && direction == 'L') {
                nitroOn();
            }
        }
    }
    public static class UpAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(oldDirection != 'D') {
                direction = 'U';
            }
            if(oldDirection == 'U' && direction == 'U') {
                nitroOn();
            }
        }
    }
    public static class DownAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(oldDirection != 'U') {
                direction = 'D';
            }
            if(oldDirection == 'D' && direction == 'D') {
                nitroOn();
            }
        }
    }
    public static class EnterAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            // Enter key to restart game (only activate if game is not running, so paused or over)
            if (running == false) {
                startGame(); // Restart
            }
        }
    }
    public static class DeleteAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            // Delete key to quit game (if already stopped)
            if (running == false){
                quitGame();
            }
        }
    }
    public static class SpaceAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            // Space bar to pause or resume game
            if (pause == false && initial_pause == false && running == true) {
                pauseGame();
            } else if (pause == true && initial_pause == false && running == false) {
                resumeGame();
            }
        }
    }
}





