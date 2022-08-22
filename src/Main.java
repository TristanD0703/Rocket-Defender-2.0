import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main{
    //Defining game variables
    public static ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
    public static Random rand = new Random();
    public static Scanner input = new Scanner(System.in);
    public static int currentWaveNum = 1;
    public static boolean playing = true;
    public static int playerHealth = 100;
    public static int numDefenseUpgrades = 0;
    public static int lives = 1;
    public static int currentDamage = 5;
    public static int upgradePoints = 0;
    public static int cannonLevel = 0;
    public static int weaponAmmo = 5;
    public static boolean slow = true;

    public static void main(String[] args){
        //introduction
        printSlow("Welcome to Rocket Defender 2.0!\nYour objective is to survive as many waves as possible.\nAre you ready?\n");
        printSlow("Press enter to continue.");
        input.nextLine();


        System.out.println("Slow mode helps keep track of what's happening. Type 1 to deactivate text slow mode, or type any other number to keep it.");
        slow = input.nextInt() != 1;
        
        //Main game loop
        //Stops when player runs out of health or decides to quit the game
        while(playing){
            enemyList.clear();
            generateWave();
            upgrade();
            doWave();
            if(playerHealth <= 0){
                printSlow("Oh no! You lost all of your health! Game over.");
                break;
            }
            currentWaveNum++;
            printSlow("Nice job! You made it to wave " + currentWaveNum + "! 1 bonus upgrade point acquired.");
            upgradePoints++;

            printSlow("Keep playing?\n1. yes\n2. no");
            int continuePlaying;
            continuePlaying = input.nextInt();
            if(continuePlaying == 2){
                playing = false;
            }
            
        }
        System.out.println("Well played! you made it to wave " + currentWaveNum);
    }

    //Creates a new list of enemies to defeat for each wave. Every 5 waves includes a lieutenant. Every 10 waves includes a destroyer.
    public static void generateWave(){
        int enemyCount = rand.nextInt(5,10);
        for(int i = 0; i < enemyCount; i++){
            enemyList.add(new Enemy(1));
        }
        if(currentWaveNum % 5 == 0){
            enemyList.add(new Enemy(2));
        }
        if((currentWaveNum % 10 == 0) && !(enemyList.get(enemyList.size()-1).getType() == 2)){
            enemyList.add(new Enemy(3));
        }
    }
    
    //prompts the user to use their upgrade points on various upgrade paths. 
    public static void upgrade(){
        printSlow("Current amount of upgrade points: " + upgradePoints);
        printSlow("Number of enemies to defeat: " + enemyList.size() + "\n");

        boolean repeat = upgradePoints > 0;
        while(repeat){
            boolean cannonUnlocked = cannonLevel > 0;
            printSlow("Please choose an upgrade to perform:");
            printSlow("1. Damage upgrade: " + currentDamage);
            printSlow("2. Defense upgrade: " + numDefenseUpgrades);
            printSlow("3. Life upgrade: " + lives);
            printSlow("4. Restore 25 health.");
            if(!cannonUnlocked){
                printSlow("5. Unlock laser cannon (10 points)");
            } else{
                printSlow("5. Upgrade laser cannon: " + cannonLevel);
            }
            printSlow("6. save points");
            int user = input.nextInt();
            
            //Feature that allows the user to upgrade an option multiple times. This logic determines whether to ask for multiple point upgrades or not.
            int upgradeAmount = 0; 
            if(user < 5 || cannonUnlocked){
                upgradeAmount = askUpgrade();
            }
            
            //upgrades the selected item
            switch(user){
                case 1: 
                    currentDamage += upgradeAmount; 
                    break;
                case 2:
                    numDefenseUpgrades += upgradeAmount; 
                    break;
                case 3:
                    lives += upgradeAmount; 
                    break;
                case 4: 
                    playerHealth += upgradeAmount * 25;
                case 5:
                    if((!cannonUnlocked && upgradePoints >= 10)){
                        cannonLevel++;
                    } else if(cannonUnlocked){
                        cannonLevel += upgradeAmount;
                    }else {
                        printSlow("Not enough upgrade points.");
                    }
                    case 6: 
                        break;
                default: 
                    printSlow("Please input a valid choice.");

            }
            upgradePoints -= upgradeAmount;
            printSlow("Upgrade more?\n1. yes\nAny key: no");
            repeat = input.nextInt() == 1;
            
        }
    }
    
    //Main logic for the game. 
    public static void doWave(){
        printSlow("\nWave " + currentWaveNum + " is starting...");
        weaponAmmo = 5;
        Enemy currentEnemy;
        for(int i = 0; i < enemyList.size(); i++){
            currentEnemy = enemyList.get(i);
            printSlow("\nYou encounter an " + currentEnemy.getName() + "!");

            while(currentEnemy.isAlive() && playerHealth > 0){
                printSlow("Your current health is " + playerHealth + "\n");
                currentEnemy.printInfo();
                switch(userAttackOptions()){
                    case 1: 
                        currentEnemy.hurt(currentDamage);
                        break;
                    case 2:
                        currentEnemy.hurt((cannonLevel * 5) + currentDamage);
                }
                if(currentEnemy.isAlive()){
                    printSlow(currentEnemy.getName() + " attacks you!");
                    currentEnemy.attack();
                }
            }
            if(!currentEnemy.isAlive()){
                printSlow("You defeated " + currentEnemy.getName() + " and gained " + currentEnemy.getPointDrops() + " upgrade points!\n");
            } else {
                break;
            }
            upgradePoints += currentEnemy.getPointDrops();
        }
    }

    public static int askUpgrade(){
        int upgradeAmount;
        printSlow("How many upgrades?");
        upgradeAmount = input.nextInt();
        if(upgradeAmount > upgradePoints){
            printSlow("Input larger than amout of points available. Used all points on upgrade chosen.");
            upgradeAmount = upgradePoints;
        }
        printSlow("Upgraded " + upgradeAmount + " times.\n");
        return upgradeAmount;
    }

    public static int userAttackOptions(){
        printSlow("\nWhat will you do?");
        printSlow("Weapon ammo available: " + weaponAmmo);
        printSlow("1. Punch");
        if(cannonLevel > 0){
            printSlow("2. Use your laser cannon.");
        }

        int user = input.nextInt();
        switch(user){
            case 1: 
                return user;
            case 2:
                if(weaponAmmo > 0 && cannonLevel > 0){
                    weaponAmmo--;
                    return user;
                }
                printSlow("Not enough ammo!");
                userAttackOptions();
        }
        return 0;
    }

    public static void printSlow(String text){
        for(int i = 0; i < text.length(); i++){
            System.out.print(text.substring(i,i+1));
            if(slow){
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println();
        if(slow){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}