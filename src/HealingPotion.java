public class HealingPotion {
    private String type;
    private int healAmount;

    public HealingPotion(String type) {
        this.type = type;
        if (type.equals("m")) {
            this.healAmount = 6;  // Minor Flask
        } else if (type.equals("B")) {
            this.healAmount = 12; // Big Flask
        } else {
            throw new IllegalArgumentException("Unknown potion type: " + type);
        }
    }

    public int getHealAmount() {
        return healAmount;
    }

    public String getType() {
        return type;
    }
}
