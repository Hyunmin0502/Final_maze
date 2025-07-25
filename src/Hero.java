public class Hero {
    private int row, col;
    private int hp = 25;
    private int maxHp = 25;
    private Weapon weapon = null;
    private boolean hasKey = false;

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }

    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }

    public boolean hasKey() { return hasKey; }
    public void obtainKey() { this.hasKey = true; }

    // Used for potion healing
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            System.out.println("You have fallen. Game over.");
            System.exit(0);
        }
    }


}
