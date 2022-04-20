public class Patient {

    private int nr;
    private int patientType;
    private int ScanType;
    private int callWeek;
    private int callDay;
    private double callTime;
    private int scanDay = -1;
    private int slotNr = -1;
    private int scanWeek = -1;
    private double appTime = -1;
    private double tardiness;
    private boolean isNoShow;
    private double scanTime = -1;
    private double duration;

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public int getPatientType() {
        return patientType;
    }

    public void setPatientType(int patientType) {
        this.patientType = patientType;
    }

    public int getScanType() {
        return ScanType;
    }

    public void setScanType(int scanType) {
        ScanType = scanType;
    }

    public int getCallWeek() {
        return callWeek;
    }

    public void setCallWeek(int callWeek) {
        this.callWeek = callWeek;
    }

    public int getCallDay() {
        return callDay;
    }

    public void setCallDay(int callDay) {
        this.callDay = callDay;
    }

    public double getCallTime() {
        return callTime;
    }

    public void setCallTime(double callTime) {
        this.callTime = callTime;
    }

    public int getScanDay() {
        return scanDay;
    }

    public void setScanDay(int scanDay) {
        this.scanDay = scanDay;
    }

    public int getSlotNr() {
        return slotNr;
    }

    public void setSlotNr(int slotNr) {
        this.slotNr = slotNr;
    }

    public int getScanWeek() {
        return scanWeek;
    }

    public void setScanWeek(int scanWeek) {
        this.scanWeek = scanWeek;
    }

    public double getAppTime() {
        return appTime;
    }

    public void setAppTime(double appTime) {
        this.appTime = appTime;
    }

    public double getTardiness() {
        return tardiness;
    }

    public void setTardiness(double tardiness) {
        this.tardiness = tardiness;
    }

    public boolean isNoShow() {
        return isNoShow;
    }

    public void setNoShow(boolean noShow) {
        isNoShow = noShow;
    }

    public double getScanTime() {
        return scanTime;
    }

    public void setScanTime(double scanTime) {
        this.scanTime = scanTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}