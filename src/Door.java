public class Door {
    private String targetRoomFile;  // 이동할 방의 CSV 파일 이름
    private boolean isMasterDoor;   // 마스터 도어인지 여부

    public Door(String targetRoomFile, boolean isMasterDoor) {
        this.targetRoomFile = targetRoomFile;
        this.isMasterDoor = isMasterDoor;
    }

    // 이동할 방 파일 이름 반환
    public String getTargetRoomFile() {
        return targetRoomFile;
    }

    // 마스터 도어 여부 반환
    public boolean isMasterDoor() {
        return isMasterDoor;
    }
}
