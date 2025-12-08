
package Domain;

public class Equipment {
    private int equipmentId;
    private String name;

    public Equipment(int equipmentId, String name) {
        this.equipmentId = equipmentId;
        this.name = name;
    }

    public int getEquipmentId() { return equipmentId; }
    public String getName() { return name; }

    @Override
    public String toString() { return equipmentId + " - " + name; }
}
