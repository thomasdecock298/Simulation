import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class Simulation<simulation> extends Helper {

    String inputFileName = null; //zelf invullen
    int D = 6;
    int amountOTSlotsPerDay = 10;
    int S = 32 + amountOTSlotsPerDay;
    double slotLength = 15/60;
    double lambdaElective = 28.345;
    double[] lambdaUrgent = new double[]{2.5, 1.25};
    double weightUr = 1/9;
    double[] cumulativeProbUrgentType = new double[]{0.7, 0.8, 0.9, 0.95, 1.0};
    double[] probUrgentType = new double[]{0.7, 0.1, 0.1, 0.05, 0.05};
    double[] meanUrgentDuration = {15, 17.5, 22.5, 30, 30};
    double[] stdevUrgentDuration = {2.5, 1, 2.5, 1, 4.5};
    double weightEl = 1.0 / 168.0;      // objective weight elective appointment wait time
    double meanTardiness = 0;
    double stdevTardiness = 2.5;
    double probNoShow = 0.02;
    double meanElectiveDuration = 15;
    double stdevElectiveDuration = 3;
    List<Patient> patients = new ArrayList<>(); //zelfde comment als hieronder


    //Staan normaal onder simulation(), heb ze eens naar hier verplaatst
    int W = 10;                      // number of weeks to simulate = run lenght
    int R = 1;                      // number of replications
    int rule = 1;                   // the appointment scheduling rule to apply
    Slot[][] weekSchedule = new Slot[D][S];  //zelf D - S gekozen, weet niet of deze initiatie juist/nodig is



    // Initialization of a "simulation" object
    public void simulation(){
        // Set test case variables
        //TODO: set these variables to the correct values
        //inputFileName = "/Users/tinemeersman/Documents/project SMA 2022 student code /input-S1-14.txt";  // input file with schedule

        // Initialize variables
        double avgElectiveAppWT = 0;
        double avgElectiveScanWT = 0;
        double avgUrgentScanWT = 0;
        double avgOT = 0;
        int numberOfElectivePatientsPlanned = 0;
        int numberOfUrgentPatientsPlanned = 0;

        // Initialize arrays
        Slot[][] weekSchedule = new Slot[D][S]; //er stond Slot[D]
        for(int d = 0; d < D; d++){
            weekSchedule[d] = new Slot[S];
        }
        double[] movingAvgElectiveAppWT = new double[W];
        double[] movingAvgElectiveScanWT = new double[W];
        double[] movingAvgUrgentScanWT = new double[W];
        double[] movingAvgOT = new double[W];
    }
    //simulation::simulation(){ }

    public void resetSystem(){

        double[] movingAvgElectiveAppWT = new double[100];
        double[] movingAvgElectiveScanWT = new double[100];
        double[] movingAvgUrgentScanWT = new double[100];
        double[] movingAvgOT = new double[100];

        // reset all variables related to 1 replication
        patients.clear();
        double avgElectiveAppWT = 0;
        double avgElectiveScanWT = 0;
        double avgUrgentScanWT = 0;
        double avgOT = 0;
        int numberOfElectivePatientsPlanned = 0;
        int numberOfUrgentPatientsPlanned = 0;

        for(int w = 0; w < W; w++){
            movingAvgElectiveAppWT[w] = 0;
            movingAvgElectiveScanWT[w] = 0;
            movingAvgUrgentScanWT[w] = 0;
            movingAvgOT[w] = 0;
        }
    }

    public void setWeekSchedule(){
        // Read and set the slot types (0=none, 1=elective, 2=urgent within normal working hours)
        //ifstream inputFile; //Wat komt hier?
        //inputFile.open(inputFileName);
        int elementInt;
        for(int s = 0; s < 32; s++){
            for(int d = 0; d < D; d++){
                //inputFile >> elementInt;
                weekSchedule[d][s].setSlotType(elementInt);
                weekSchedule[d][s].setPatientType(elementInt);
            }
        }
        //inputFile.close();

        // Set the type of the overtime slots (3=urgent in overtime)
        for(int d = 0; d < D; d++){
            for(int s = 32; s < S; s++){
                weekSchedule[d][s].setSlotType(3);
                weekSchedule[d][s].setPatientType(2);
            }
        }

        // set start and appointment time
        double time;
        for(int d = 0; d < D; d++){
            time = 8; // start time slot schedule
            for(int s = 0; s < S; s++){
                // start time slot
                weekSchedule[d][s].setStartTime(time);

                // appointment time slot
                if(weekSchedule[d][s].getSlotType() != 1){    // all slot types not elective : appointment time = slot start time
                    weekSchedule[d][s].setAppTime(time);
                }else{                                   // elective slots: appointment time is set according to rule !
                    if(rule == 1){ // FIFO
                        weekSchedule[d][s].setAppTime(time);
                    }else if(rule == 2){
                        // TODO: Bailey-Welch rule
                    }else if(rule == 3){
                        // TODO: Blocking rule
                    }else if(rule == 4){
                        // TODO: Benchmark rule
                    }
                }

                //update time variable
                time += slotLength;
                if(time == 12){ time = 13;} // skip to the end of the luchbreak
            }
        }
    }

    public int getRandomScanType(){
        double r = Math.random();
        int type = -1;
        for(int i = 0; i < 5 && type == -1; i++){
            if(r < cumulativeProbUrgentType[i]){ type = i; }
        }
        return type;
    }

    public void generatePatients(){
        double arrivalTimeNext;
        int counter = 0; // total number of patients so far
        int patientType, scanType, endTime;
        double callTime, tardiness, duration, lambda;
        boolean noShow;

        //std::default_random_engine generator; NOG TE DOEN

        for(int w = 0; w < W; w++){
            for(int d = 0; d < D; d++){ // not on Sunday
                // generate ELECTIVE patients for this day
                if(d < D - 1){  // not on Saturday either
                    arrivalTimeNext = 8 + exponentialDistribution(lambdaElective) * (17-8);
                    while(arrivalTimeNext < 17){ // desk open from 8h until 17h
                        patientType = 1;                // elective
                        scanType = 0;                   // no scan type
                        callTime = arrivalTimeNext;     // set call time, i.e. arrival event time
                        tardiness = normalDistribution(meanTardiness, stdevTardiness) / 60.0;       // in practice this is not known yet at time of call
                        noShow = bernoulliDistribution(probNoShow);                                // in practice this is not known yet at time of call
                        duration = normalDistribution(meanElectiveDuration, stdevElectiveDuration) / 60.0; // in practice this is not known yet at time of call
                        Patient patient = new Patient(counter, patientType, scanType, w, d, callTime, tardiness, noShow, duration);
                        patients.add(patient);
                        counter++;
                        arrivalTimeNext = arrivalTimeNext + exponentialDistribution(lambdaElective) * (17-8); // arrival time of next patient (if < 17h)
                    }
                }

                // generate URGENT patients for this day
                if(d == 3 || d == 5){
                    lambda = lambdaUrgent[1]; // on Wed and Sat, only half a day!
                    endTime = 12;
                }else{
                    lambda = lambdaUrgent[0];
                    endTime = 17;
                }
                arrivalTimeNext = 8 + exponentialDistribution(lambda) * (endTime-8);
                while(arrivalTimeNext < endTime){ // desk open from 8h until 17h
                    patientType = 2;                // urgent
                    scanType = getRandomScanType(); // set scan type
                    callTime = arrivalTimeNext;     // set arrival time, i.e. arrival event time
                    tardiness = 0;                  // urgent patients have an arrival time = arrival event time
                    noShow = false;                 // urgent patients are never no-show
                    duration = normalDistribution(meanUrgentDuration[scanType], stdevUrgentDuration[scanType]) / 60.0; // in practice this is not known yet at time of arrival
                    Patient patient = new Patient(counter, patientType, scanType, w, d, callTime, tardiness, noShow, duration);
                    patients.add(patient);
                    counter++;
                    arrivalTimeNext = arrivalTimeNext + exponentialDistribution(lambda) * (endTime-8); // arrival time of next patient (if < 17h)
                }
            }
        }
    }

    public int getNextSlotNrFromTime(int day, int patientType, double time){
        boolean found = false;
        int slotNr = -1;
        for(int s = 0; !found && s < S; s++){
            if(weekSchedule[day][s].getAppTime() > time && patientType == weekSchedule[day][s].getPatientType()){
                found = true;
                slotNr = s;
            }
        }
        if(!found){
            System.out.println("NO SLOT EXISTS DURING TIME %.2f \n" + time);
            System.exit(0);
        }
        return slotNr;
    }

    public void schedulePatients(){

        //sort arrival events (= patient list) on arrival time (call time for elective patients, arrival time for urgent)
        patients.sort([](const Patient &patient1, const Patient &patient2){
            if (patient1.callWeek != patient2.callWeek)
                return patient1.callWeek < patient2.callWeek;
            if (patient1.callDay != patient2.callDay)
                return patient1.callDay < patient2.callDay;
            if (patient1.callTime != patient2.callTime)
                return patient1.callTime < patient2.callTime;
            if (patient1.scanType == 2)                             // if arrival time same, urgent patient before elective patient
                return true;
            if (patient2.scanType == 2)
                return false;
            return true;
        });

        int[] week = {0,0}; // week of the next available slot {elective,urgent}
        int[] day = {0,0}; // day of the next available slot {elective,urgent}
        int[] slot = {0,0}; // slotNr of the next available slot {elective,urgent}

        //find first slot of each patient type (note, we assume each day (i.e. also day 0) has at least one slot of each patient type!)
        //elective
        int d = 0;
        boolean found = false;
        for(int s = 0; s < S && !found; s++){
            if(weekSchedule[d][s].getPatientType() == 1){
                day[0] = d;
                slot[0] = s;
                found = true;
            }
        }
        //urgent
        found = false;
        for(int s = 0; s < S && !found; s++){
            if(weekSchedule[d][s].getPatientType() == 2){
                day[1] = d;
                slot[1] = s;
                found = true;
            }
        }

        // go over SORTED patient list and assign slots
        int previousWeek = 0; int numberOfElective = 0; int numberOfElectivePerWeek = 0;   // keep track of week to know when to update moving average elective appointment waiting time
        double wt; int slotNr;
        for(Patient patient = patients.begin(); patient != patients.end(); patient++){
            //Patient *pat = &*patient;

            //set index i dependant on patient type
            int i = patient.getPatientType() - 1;

            // if still within the planning horizon:
            if(week[i] < W){

                // determine week where we start searching for a slot
                if(patient.getCallWeek() > week[i]){
                    week[i] = patient.getCallWeek();
                    day[i] = 0;
                    slot[i] = getNextSlotNrFromTime(day[i], patient.getPatientType(), 0);           // note we assume there is at least one slot of each patient type per day => this line will find first slot of this type
                }
                // determine day where we start searching for a slot
                if(patient.getCallWeek() == week[i] && patient.getCallDay() > day[i]){
                    day[i] = patient.getCallDay();
                    slot[i] = getNextSlotNrFromTime(day[i], patient.getPatientType(), 0);           // note we assume there is at least one slot of each patient type per day => this line will find first slot of this type
                }
                // determine slot
                if(patient.getCallWeek() == week[i] && patient.getCallDay() == day[i] && patient.getCallTime() >= weekSchedule[day[i]][slot[i]].getAppTime()){
                    // find last slot on day "day[i]"
                    found = false; slotNr = -1;
                    for(int s = S - 1; s >= 0 && !found; s--){
                        if(weekSchedule[day[i]][s].getPatientType() == patient.getPatientType()){
                            found = true;
                            slotNr = s;
                        }
                    }
                    // urgent patients have to be treated on the same day either in normal hours or in overtime (!! make sure there are enough overtime slots)
                    // for elective patients: check if the patient call time is before the last slot, i.e. if the patient can be planned on day "day[i]"
                    if(patient.getPatientType() == 2 || patient.getCallTime() < weekSchedule[day[i]][slotNr].getAppTime()){
                        slot[i] = getNextSlotNrFromTime(day[i], patient.getPatientType(), patient.getCallTime());   // find the first elective slot after the call time on day "day[i]"
                    }else{
                        // determine the next day
                        if(day[i] < D - 1){
                            day[i] = day[i] + 1;
                        }else{
                            day[i] = 0;
                            week[i] = week[i] + 1;
                        }
                        if(week[i] < W){   // find the first slot on the next day (if within the planning horizon)
                            slot[i] = getNextSlotNrFromTime(day[i], patient.getPatientType(), 0);
                        }
                    }
                }

                // schedule the patient
                patient.getScanWeek() = week[i];
                patient.getScanDay() = day[i];
                patient.getSlotNr() = slot[i];
                patient.getAppTime() = weekSchedule[day[i]][slot[i]].getAppTime();

                // update moving average elective appointment waiting time
                if(patient.getPatientType() == 1){
                    if(previousWeek < week[i]){
                        movingAvgElectiveAppWT[previousWeek] = movingAvgElectiveAppWT[previousWeek] / numberOfElectivePerWeek;
                        numberOfElectivePerWeek = 0;
                        previousWeek = week[i];
                    }
                    wt = patient.getAppWT();
                    movingAvgElectiveAppWT[week[i]] += wt;
                    numberOfElectivePerWeek++;
                    avgElectiveAppWT += wt;
                    numberOfElective++;
                }

                // set next slot of the current patient type
                found = false; int startD = day[i]; int startS = slot[i] + 1;
                for(int w = week[i]; w < W && !found; w++){
                    for(d = startD; d < D && !found; d++){
                        for(int s = startS; s < S && !found; s++){
                            if(weekSchedule[d][s].getPatientType() == patient.getPatientType()){
                                week[i] = w;
                                day[i] = d;
                                slot[i] = s;
                                found = true;
                            }
                        }
                        startS = 0;
                    }
                    startD = 0;
                }
                if(!found) week[i] = W;
            }
        }

        // update moving average elective appointment waiting time in last week
        movingAvgElectiveAppWT[W-1] = movingAvgElectiveAppWT[W-1] / numberOfElectivePerWeek;

        // calculate objective value
        avgElectiveAppWT = avgElectiveAppWT / numberOfElective;
    }

    public void sortPatientsOnAppTime(){

        List<Patient> patients = new ArrayList<>();
        patients.sort([](const Patient &patient1, const Patient &patient2){
            // unplanned patients at the end of the list in order of their call
            if(patient1.scanWeek == -1 && patient2.scanWeek == -1){
                if (patient1.callWeek != patient2.callWeek)
                    return patient1.callWeek < patient2.callWeek;
                if (patient1.callDay != patient2.callDay)
                    return patient1.callDay < patient2.callDay;
                if (patient1.callTime != patient2.callTime)
                    return patient1.callTime < patient2.callTime;
                if (patient1.scanType == 2)                             // if arrival time same, urgent patient before elective patient
                    return true;
                if (patient2.scanType == 2)
                    return false;
                return true;
            }
            if(patient1.scanWeek == -1){
                return false;
            }
            if(patient2.scanWeek == -1){
                return true;
            }

            if (patient1.scanWeek != patient2.scanWeek)
                return patient1.scanWeek < patient2.scanWeek;
            if (patient1.scanDay != patient2.scanDay)
                return patient1.scanDay < patient2.scanDay;
            if (patient1.appTime != patient2.appTime)
                return patient1.appTime < patient2.appTime;
            if (patient1.scanType == 2)                             // if arrival time same, urgent patient before elective patient
                return true;
            if (patient2.scanType == 2)
                return false;
            if(patient1.nr < patient2.nr){
                return true;
            }
            if(patient1.nr > patient2.nr){
                return false;
            }
            return true;
        });
    }

    public void runOneSimulation() {
        generatePatients();     // create patient arrival events (elective patients call, urgent patient arrive at the hospital)
        schedulePatients();     // schedule urgent and elective patients in slots based on their arrival events => detrmine the appointment wait time
        sortPatientsOnAppTime();   // sort patients on their appointment time (unscheduled patients are grouped at the end of the list)

        // determine scan wait time per patient and overtime per day
        int prevWeek = 0;
        int prevDay = -1;
        int[] numberOfPatientsWeek = {0, 0};
        int[] numberOfPatients = {0, 0};
        double arrivalTime, wt;
        double prevScanEndTime = 0;
        boolean prevIsNoShow = false;
        // go over arrival events (i.e. the moment the patient arrives at the hospital)
        for (Patient patient = patients.begin(); patient != patients.end(); patient++) {
            if (patient.getScanWeek() == -1) { // stop at the first unplanned patient
                break;
            }

            arrivalTime = (double) patient.getAppTime() + patient.getTardiness();
            // SCAN WT
            if (!patient.isNoShow()) {
                if (patient.getScanWeek() != prevWeek || patient.getScanDay() != prevDay) {
                    patient.getScanTime() = arrivalTime;
                } else {
                    if (prevIsNoShow) {
                        patient.getScanTime() = max(weekSchedule[patient.getScanDay()][patient.getSlotNr()].startTime, max(prevScanEndTime, arrivalTime)); // note we assume we wait at least 15minutes on a no-show patient to see whether he shows or is just late
                    } else {
                        patient.getScanTime() = max(prevScanEndTime, arrivalTime);
                    }
                }
                wt = patient -> getScanWT(); //Functie nog te maken in klasse Patient
                if (patient.getPatientType() == 1) {
                    movingAvgElectiveScanWT[patient.getScanWeek()] += wt;
                } else {
                    movingAvgUrgentScanWT[patient.getScanWeek()] += wt;
                }
                numberOfPatientsWeek[patient.getPatientType() - 1]++;
                if (patient.getPatientType() == 1) {
                    avgElectiveScanWT += wt;
                } else {
                    avgUrgentScanWT += wt;
                }
                numberOfPatients[patient.getPatientType() - 1]++;
            }

            // OVERTIME
            if (prevDay > -1 && prevDay != patient.getScanDay()) {
                if (d == 3 || d == 5) {
                    movingAvgOT[prevWeek] += max(0.0, prevScanEndTime - 13);
                } else {
                    movingAvgOT[prevWeek] += max(0.0, prevScanEndTime - 17);
                }
                if (d == 3 || d == 5) {
                    avgOT += max(0.0, prevScanEndTime - 13);
                } else {
                    avgOT += max(0.0, prevScanEndTime - 17);
                }
            }

            // update moving averages if week ends
            if (prevWeek != patient.getScanWeek()) {
                movingAvgElectiveScanWT[prevWeek] = movingAvgElectiveScanWT[prevWeek] / numberOfPatientsWeek[0];
                movingAvgUrgentScanWT[prevWeek] = movingAvgUrgentScanWT[prevWeek] / numberOfPatientsWeek[1];
                movingAvgOT[prevWeek] = movingAvgOT[prevWeek] / D;
                numberOfPatientsWeek[0] = 0;
                numberOfPatientsWeek[1] = 0;
            }

            //set prev patient
            if (patient.isNoShow()) {
                //prevScanEndTime stays the same, it is the end time of the patient before the no-show patient
                prevIsNoShow = true;
            } else {
                prevScanEndTime = patient.getScanTime() + patient.getDuration();
                prevIsNoShow = false;
            }
            prevWeek = patient.getScanWeek();
            prevDay = patient.getScanDay();
        }
        // update moving averages of the last week
        movingAvgElectiveScanWT[W - 1] = movingAvgElectiveScanWT[W - 1] / numberOfPatientsWeek[0];
        movingAvgUrgentScanWT[W - 1] = movingAvgUrgentScanWT[W - 1] / numberOfPatientsWeek[1];
        movingAvgOT[W - 1] = movingAvgOT[W - 1] / D;

        // calculate objective values
        avgElectiveScanWT = avgElectiveScanWT / numberOfPatients[0];
        avgUrgentScanWT = avgUrgentScanWT / numberOfPatients[1];
        avgOT = avgOT / (D * W);

    }


        // print moving avg
    /*FILE *file = fopen("/Users/tinemeersman/Documents/project SMA 2022 student code /output-movingAvg.txt", "a"); // TODO: use your own directory
    fprintf(file,"week \t elAppWT \t elScanWT \t urScanWT \t OT \n");
    for(w = 0; w < W; w++){
        fprintf(file, "%d \t %.2f \t %.2f \t %.2f \t %.2f \n", w, movingAvgElectiveAppWT[w], movingAvgElectiveScanWT[w], movingAvgUrgentScanWT[w], movingAvgOT[w]);
    }
    fclose(file);

    }

    void simulation::runSimulations(){
        double electiveAppWT = 0;
        double electiveScanWT = 0;
        double urgentScanWT = 0;
        double OT = 0;
        double OV = 0;
        setWeekSchedule();          // set cyclic slot schedule based on given input file
        printf("r \t elAppWT \t elScanWT \t urScanWT \t OT \t OV \n");
        // run R replications
        for(r = 0; r < R; r++){
            resetSystem();          // reset all variables related to 1 replication
            srand(r);               // set seed value for random value generator
            runOneSimulation();     // run 1 simulation / replication
            electiveAppWT += avgElectiveAppWT;
            electiveScanWT += avgElectiveScanWT;
            urgentScanWT += avgUrgentScanWT;
            OT += avgOT;
            OV += avgElectiveAppWT / weightEl + avgUrgentScanWT / weightUr;
            printf("%d \t %.2f \t %.2f \t %.2f \t %.2f \t %.2f \n", r, avgElectiveAppWT, avgElectiveScanWT, avgUrgentScanWT, avgOT, avgElectiveAppWT / weightEl + avgUrgentScanWT / weightUr);
        }
        electiveAppWT = electiveAppWT / R;
        electiveScanWT = electiveScanWT / R;
        urgentScanWT = urgentScanWT / R;
        OT = OT / R;
        OV = OV / R;
        double objectiveValue = electiveAppWT / weightEl + urgentScanWT / weightUr;
        printf("Avg.: \t %.2f \t %.2f \t %.2f \t %.2f \t %.2f \n", electiveAppWT, electiveScanWT, urgentScanWT, OT, objectiveValue);

        // print results
        //FILE *file = fopen("/Users/tinemeersman/Documents/project SMA 2022 student code /output.txt", "a"); // TODO: use your own directory
        // TODO: print the output you need to a .txt file
        //fclose(file);
    }
    */

}
