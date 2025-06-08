# **Text-Based Adventure Game Project: System Overview**

---

## 🌎 Project Summary
This Java-based text adventure game simulates a dungeon-style environment where a hero explores rooms, collects weapons and potions, and battles monsters to ultimately escape. The map is based on CSV files, allowing flexible room layout and configuration.

---

## 🏠 Core Components

### 1. `Main.java`
- Entry point of the program.
- Initializes the game by calling `GameSetup.setupNewRunFolder()`.
- Launches the game using the newly created room structure.

### 2. `GameSetup.java`
- **Creates a unique folder** inside `rooms/runs/` for each game session (named with a timestamp).
- **Copies all `.csv` files** from the main `rooms/` directory into this folder.
- Returns the full path of the new folder for use in game initialization.

---

## 🏛️ Game World: `Room.java`

### Room Loading
- Each room is stored as a CSV file that defines its size and grid layout.
- Symbols like `@`, `G`, `O`, `T`, `m`, `D`, `W` represent entities: hero, monsters, potions, doors, weapons, etc.
- The constructor `Room(String basePath, String filename)` loads this data and builds the internal 2D grid.

### Rendering
- `displayRoomWithStatus(Hero hero)` shows the current state of the map, hero HP, weapon, and key possession.

### Movement & Interaction
- `moveHero(char direction, Hero hero)` handles all movement logic:
  - Prevents walking into monsters or walls.
  - Allows picking up potions and weapons.
  - Handles room transitions (`d:room2.csv`) and door unlocking (`D`).

### Attack Logic
- `handleAttack(Hero hero)` checks if a monster is adjacent.
- If so, prompts the player to attack. Damage is calculated, monster HP is updated, and the monster may drop a key.

### Saving State
- `saveRoomToCSV()` saves the current room state back to its `.csv` file for persistence.

---

## 🧙 Hero System: `Hero.java`
- Tracks hero's HP, current weapon, position, and whether they hold a key.
- Offers methods for taking damage, healing, and equipping weapons.

---

## 🧹 Items

### `Weapon.java`
- Represents a weapon with a name and damage value.
- Weapons include Stick, Weak Sword, Strong Sword.

### `HealingPotion.java`
- Represents health potions: minor (`m`) and big (`B`).
- Each potion has a healing value (e.g., +5, +10).

---

## 🧼 Monsters System

### `Monsters.java` (abstract)
- Base class with HP, damage-taking logic, and isDead check.

### `Goblin.java`, `Orc.java`, `Troll.java`
- Subclasses of `Monsters`, each with different default HP.
- Trolls drop a key when defeated.

---

## 🌐 CSV Structure Example

```csv
5,6
 , ,G3, , ,
 ,m, ,W, ,
 , ,@, , ,
 , , , ,B,
 , , , , ,D
```

## ✅ Summary of Flow
Main runs the game setup.

- GameSetup creates a new run folder and copies room files.

- The first room is loaded using Room, and Hero is placed.

- The user moves, fights, and collects items.

- Progress is saved in .csv.

- The goal: reach the D door with a key and escape!
