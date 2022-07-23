
# Pooh Bear Game
<img width="500" alt="intro" src="https://user-images.githubusercontent.com/31792170/173160983-bcf42e7c-738e-4cb4-85af-0c0ea89d9377.png">
<img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/m/aaroncorona/Pooh-Bear-Game">
<img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/aaroncorona/Pooh-Bear-Game">


## 🐻 Overview
This is a 2D game written in Java. It is inspired by Snake with added features and a Winnie-the-Pooh theme.

## 📖 Table of Contents
* [Features](#%EF%B8%8F-game-features)
* [Tech Stack](#%EF%B8%8F-tech-stack)
* [File Descriptions](#%EF%B8%8F-file-descriptions)
* [Installation](#-installation)
* [Next Release](#-next-release---version-20-features)


## 🕹️ Game Features
1. **Snake**: Traditional snake gameplay
2. **Nitro**: Double click in 1 direction to get a nitro boost, which speeds up the dull parts of the game
3. **Win the Game**: Unlike traditional snake, you can win this game by eating 33 honeys.
4. **High scores**: Save and display high scores for "Honey Eaten" to see where you rank.
5. **Pooh Bear graphics**: Watch Pooh Bear's head chase honey around. Each honey gives a randomly colored tail.
<img width="500" alt="nitro" src="https://user-images.githubusercontent.com/31792170/173169436-1af02a61-b6a8-4f53-91c7-999a734d60cc.png">
<img width="500" alt="gameover" src="https://user-images.githubusercontent.com/31792170/173163046-817ceb01-c7ff-4465-9498-21090919c827.png">
<img width="500" alt="scores" src="https://user-images.githubusercontent.com/31792170/173162995-3186db38-a1ac-4fae-8221-6f435e7d546e.png">
<img width="500" alt="win" src="https://user-images.githubusercontent.com/31792170/173169340-2e513548-d169-4510-8b36-ea39caa113ce.png">

## ⚙️ Tech Stack
* Java Swing
* IntelliJ


## 🗂️ File Descriptions
* **SnakePanel.java** - Where the Panel and all game logic lives
* **SnakeFrame.java** - The Panel is instantiated within the contstructor for a JFrame
* **Main.java** - The JFrame is instantiated
* **snake_high_scores.csv** - The CSV where high scores are stored. The SnakePanel logic reads from here


## 🚀 Installation
1. Clone this repo locally 
2. Remove rows from CSV file to track your own scores (optional)
3. Navigate to the directory where the source Java files are stored
4. Run the Main file:
```
$ javac Main.java
$ java Main
```

## 🚧 Next Release - Version 2.0 Features
* More win conditions
* Store all high scores in the Cloud so you can compete against others
* Open to suggestions!

