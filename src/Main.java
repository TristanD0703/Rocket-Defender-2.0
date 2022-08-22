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
    public static int maxHealth = 100;
    public static int numDefenseUpgrades = 0;
    public static int currentDamage = 5;
    public static int upgradePoints = 0;
    public static boolean cannonUnlocked = false;
    public static int weaponAmmo;
    public static int ammoCapacity = 3;
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
            printSlow("Nice job! You made it to wave " + currentWaveNum + "!");

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
            if(rand.nextInt(1,5) > 1){
                enemyList.add(new Enemy(1));
            } else {
                enemyList.add(new Enemy(4));
            }
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
        printSlow("Number of enemies to defeat: " + enemyList.size() + "\n");

        boolean repeat = upgradePoints > 0;
        while(repeat){
            printSlow("Current amount of upgrade points: " + upgradePoints);
            printSlow("Please choose an upgrade to perform:");
            printSlow("1. Damage upgrade: " + currentDamage);
            printSlow("2. Defense upgrade: " + numDefenseUpgrades);
            printSlow("3. increase max health by 25.");
            printSlow("4. Upgrade ammo capacity (10 is the maximum upgrade): " + ammoCapacity);
            printSlow("5. save points");
            if(!cannonUnlocked){
                printSlow("6. Unlock laser cannon (10 points)");
            }
            int user = input.nextInt();
            
            //Feature that allows the user to upgrade an option multiple times. This logic determines whether to ask for multiple point upgrades or not.
            int upgradeAmount = 0; 
            if(user < 5){
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
                    maxHealth += upgradeAmount * 25;
                case 4:
                    int previousAmmoCapacity = ammoCapacity;
                    if((upgradeAmount + ammoCapacity) > 10){
                        ammoCapacity = 10;
                        upgradeAmount = ammoCapacity - previousAmmoCapacity;
                        System.out.println("Upgraded ammo to the max (10)");
                    } else {
                        ammoCapacity += upgradeAmount;
                    }
                case 5: 
                    break;
                case 6:
                    if((upgradePoints >= 10)){
                        cannonUnlocked = true;
                        upgradePoints -= 10;
                        printSlow("Laser Cannon Unlocked!!");
                    }else {
                        printSlow("Not enough upgrade points.");
                    }
                    break;
                default: 
                    printSlow("Please input a valid choice.");

            }
            upgradePoints -= upgradeAmount;
            if(upgradePoints > 0){
                printSlow("Upgrade more?\n1. yes\nAny key: no");
                repeat = input.nextInt() == 1;
            } else {
                repeat = false;
            }
            
        }
    }
    
    //Main logic for the game. 
    public static void doWave(){
        printSlow("\nWave " + currentWaveNum + " is starting...");
        playerHealth = maxHealth;
        weaponAmmo = ammoCapacity;
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
                        currentEnemy.hurt(15 + currentDamage);
                        break;
                    case 3:
                        currentEnemy.hurt(30 + currentDamage);
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
            printSlow("Enemies left in this wave: " + ((enemyList.size()-1) - i));
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
        printSlow("2. Use your laser pistol");
        if(cannonUnlocked){
            printSlow("3. Use your laser cannon.");
        }

        int user = input.nextInt();
        switch(user){
            case 1: 
                return user;
            case 2:
                if(weaponAmmo > 0){
                    weaponAmmo--;
                    return user;
                }
            case 3:
                if(weaponAmmo > 0 && cannonUnlocked){
                    weaponAmmo -= 5;
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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}