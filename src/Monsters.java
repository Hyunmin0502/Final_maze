public abstract class Monsters {
    protected int hp;
    protected int damage;

    public Monsters(int hp, int damage) {
        this.hp = hp;
        this.damage = damage;
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public int getDamage() {
        return damage;
    }

    public int getHp() {
        return hp;
    }
}
