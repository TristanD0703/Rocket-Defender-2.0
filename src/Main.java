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
    public static int pistolLevel = 0;
    public static int weaponAmmo = 5;

    public static void main(String[] args){
        //introduction
        System.out.println("Welcome to Rocket Defender 2.0!\nYour objective is to survive as many waves as possible.\nAre you ready?\n");
        System.out.println("Press enter to continue.");
        input.nextLine();
        
        //Main game loop
        //Stops when player runs out of health or decides to quit the game
        while(playing){
            enemyList.clear();
            generateWave();
            upgrade();
            doWave();
            currentWaveNum++;
            System.out.println("Nice job! You made it to wave " + currentWaveNum + "! 1 bonus upgrade point acquired.");
            upgradePoints++;

            System.out.println("Keep playing?\n1. yes\n2. no");
            int continuePlaying;
            continuePlaying = input.nextInt();
            if(continuePlaying == 2){
                playing = false;
            }
            if(playerHealth <= 0){
                System.out.println("Oh no! You lost all of your health! Game over.");
            }
        }
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
        System.out.println("Current amount of upgrade points: " + upgradePoints);
        System.out.println("Number of enemies to defeat: " + enemyList.size() + "\n");

        boolean repeat = true;
        while(repeat){
            boolean pistolUnlocked = pistolLevel > 0;
            System.out.println("Please choose an upgrade to perform:");
            System.out.println("1. Damage upgrade: " + currentDamage);
            System.out.println("2. Defense upgrade: " + numDefenseUpgrades);
            System.out.println("3. Life upgrade: " + lives);
            System.out.println("4. Restore 25 health.");
            if(!pistolUnlocked){
                System.out.println("5. Unlock laser pistol (10 points)");
            } else{
                System.out.println("5. Upgrade laser pistol: " + pistolLevel);
            }
            System.out.println("6. save points");
            int user = input.nextInt();
            
            //Feature that allows the user to upgrade an option multiple times. This logic determines whether to ask for multiple point upgrades or not.
            int upgradeAmount = 0; 
            if(user < 5 || pistolUnlocked){
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
                    if((!pistolUnlocked && upgradePoints >= 10)){
                        pistolLevel++;
                    } else if(pistolUnlocked){
                        pistolLevel += upgradeAmount;
                    }else {
                        System.out.println("Not enough upgrade points.");
                    }
                    case 6: 
                        break;
                default: 
                    System.out.println("Please input a valid choice.");

            }
            upgradePoints -= upgradeAmount;
            System.out.println("Upgrade more?\n1. yes\nAny key: no");
            repeat = input.nextInt() == 1;
            
        }
    }
    
    //Main logic for the game. 
    public static void doWave(){
        System.out.println("\nWave " + currentWaveNum + " is starting...");
        weaponAmmo = 5;
        Enemy currentEnemy;
        for(int i = 0; i < enemyList.size(); i++){
            currentEnemy = enemyList.get(i);
            System.out.println("\nYou encounter an " + currentEnemy.getName() + "!");

            while(currentEnemy.isAlive() && playerHealth > 0){
                System.out.println("Your current health is " + playerHealth + "\n");
                currentEnemy.printInfo();
                switch(userAttackOptions()){
                    case 1: 
                        currentEnemy.hurt(currentDamage);
                        break;
                    case 2:
                        currentEnemy.hurt((pistolLevel * 5) + currentDamage);
                }
                if(currentEnemy.isAlive()){
                    System.out.println(currentEnemy.getName() + "attacks you!");
                    currentEnemy.attack();
                }
            }
            System.out.println("You defeated " + currentEnemy.getName() + " and gained " + currentEnemy.getPointDrops() + " upgrade points!\n");
            upgradePoints += currentEnemy.getPointDrops();
        }
    }

    public static int askUpgrade(){
        int upgradeAmount;
        System.out.println("How many upgrades?");
        upgradeAmount = input.nextInt();
        if(upgradeAmount > upgradePoints){
            System.out.println("Input larger than amout of points available. Used all points on upgrade chosen.");
            upgradeAmount = upgradePoints;
        }
        System.out.println("Upgraded " + upgradeAmount + " times.\n");
        return upgradeAmount;
    }

    public static int userAttackOptions(){
        System.out.println("What will you do?");
        System.out.println();
        System.out.println("Weapon ammo available: " + weaponAmmo);
        System.out.println("1. Punch");
        if(pistolLevel > 0){
            System.out.println("2. Use your pistol.");
        }

        int user = input.nextInt();
        switch(user){
            case 1: 
                return user;
            case 2:
                if(weaponAmmo > 0 && pistolLevel > 0){
                    weaponAmmo--;
                    return user;
                }
                System.out.println("Not enough ammo!");
                userAttackOptions();
        }
        return 0;
    }
}