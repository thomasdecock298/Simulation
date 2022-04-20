public class Slot {

    private double startTime;
    private double appTime;
    private int patientType;
    private int slotType;

    public Slot(double startTime, double appTime, int patientType, int slotType) {
        this.startTime = startTime;
        this.appTime = appTime;
        this.patientType = patientType;
        this.slotType = slotType;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getAppTime() {
        return appTime;
    }

    public void setAppTime(double appTime) {
        this.appTime = appTime;
    }

    public int getPatientType() {
        return patientType;
    }

    public void setPatientType(int patientType) {
        this.patientType = patientType;
    }

    public int getSlotType() {
        return slotType;
    }

    public void setSlotType(int slotType) {
        this.slotType = slotType;
    }

}
