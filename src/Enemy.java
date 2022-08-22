public class Enemy {
    private int health;
    private int strength;
    private boolean alive = true;
    private int pointDrops;
    private String enemyName;
    private int enemyType;
    private int evasiveness;


    public Enemy(int health, int strength, int pointDrops){
        this.health = health;
        this.strength = strength;
        this.alive = true;
        this.pointDrops = pointDrops;
        enemyName = "unknown";
    }

    public Enemy(int type){
        enemyType = type;
        switch(type){
            case 1:
                enemyName = "Alien foot-soldier";
                health = 15;
                strength = 5;
                pointDrops = 1;
                evasiveness = 5;
                break;
            case 2: 
                enemyName = "Alien Lieutenant";
                health = 40;
                strength = 15;
                pointDrops = 2;
                evasiveness = 15;
                break;
            case 3:
                enemyName = "Alien Destroyer";
                health = 100;
                strength = 20;
                pointDrops = 5;
                evasiveness = 20;
                break;
        }
    }
    public int getType(){
        return enemyType;
    }
    public String getName(){
        return enemyName;
    }

    public int getHealth(){
        return health;
    }

    public int getStrength(){
        return strength;
    }

    public boolean isAlive(){
        return alive;
    }

    public int getPointDrops(){
        return pointDrops;
    }

    public void attack(){
        int enemyDamage = (this.strength - Main.numDefenseUpgrades);
        Main.playerHealth -= enemyDamage;
        System.out.println("They dealt " + enemyDamage + " damage to you!");
    }

    public void printInfo(){
        System.out.println("Enemy current Health: " + health);
    }

    public void hurt(int currentDamage){
        if(!(evasiveness > Main.rand.nextInt(0,100))){
            health -= currentDamage;
        } else {
            System.out.println("The enemy avoided your attack!");
        }
        System.out.println("You dealt " + currentDamage + " damage!\n");

        alive = health > 0;
    }
}