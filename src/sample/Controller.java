package sample;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    @FXML
    private Button initializeBtn, addCTBtn, removeCTBtn, loadBtn, saveBtn, registerForVaxBtn, testForCovidBtn, massVaxBtn, pauseButton, popDataBtn;

    @FXML
    private ListView contactTracerUpdates, covidPositiveList, covidRecoveredList, covidDeathsList, vaxFirstDoseList, vaxSecondDoseList, fullyImmunizedList, patientInfoList;

    @FXML
    private DatePicker vaxDatePicker;

    @FXML
    private Label chanceLbl, testedPositiveLbl, testedNegativeLbl, vaxErrorMessage, firstVaxTxt, secondVaxTxt, congratsTxt, deadLbl, statusLbl;

    @FXML
    private ChoiceBox<String> patientSelector, genderSelector, movementSelector, patientSearchField;

    @FXML
    private Rectangle coverRect;

    @FXML
    private TextField nameField, ageField, searchBar, patientSearchBar;

    @FXML
    private Text startWithAddPatientErrorText, identicalNameErrorText, nonNumberErrorText, addCTErrorLbl, totalPopTxt, contactPtsTxt, nameTxt, ageTxt;

    @FXML
    private CheckBox symptomsCoughing, symptomsFever, symptomsBreathing, symptomsFatigue, symptomsAches, symptomsHeadache, symptomsSmell, symptomsThroat, symptomsCongestion, symptomsNausea, symptomsDiarrhea,symptomsTravel, symptomsElder;

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    XYChart.Series series = new XYChart.Series();

    CategoryAxis xAxis2 = new CategoryAxis();
    NumberAxis yAxis2 = new NumberAxis();
    XYChart.Series series2 = new XYChart.Series();

    @FXML
    private BarChart<String, Number> popBarGraph = new BarChart<String,Number>(xAxis,yAxis);
    @FXML
    private BarChart<String, Number> popChart2 = new BarChart<String, Number>(xAxis2, yAxis2);
    private int numContact;

    @FXML
    private GridPane contactGrid;
    private ImageView[][] contactImages = new ImageView[73][90];
    private int[][] contactTypes = new int[73][90];

    private Image lightGreen = new Image("resources/lightGreenSquare.jpg");
    private Image brightRed = new Image("resources/brightRedSquare.jpg");
    private Image pink = new Image("resources/pinkSquare.jpg");
    private Image limeGreen = new Image("resources/limeGreenSquare.jpg");
    private Image yellow = new Image("resources/yellowSquare.jpg");
    private Image darkBlue = new Image("resources/darkBlueSquare.jpg");

    private CheckBox[] symptomsList = new CheckBox[13];
    ObservableList<String> namesList;
    ObservableList<String> namesListContact;
    ObservableList<String> genderList;
    ObservableList<String> movementList;

    private PopulationLists dataLists = new PopulationLists();
    public List<Person> patients = new ArrayList<Person>();
    public List<ContactTracer> contactTracerPatients = new ArrayList<ContactTracer>();

    private boolean isPaused; //whether contact simulation is running

    //enacted upon initialization - enables necessary FX features, adds functionality to selectors/drop downs, eventhandler and calls start method
    @FXML
    private void handleStart(ActionEvent event){
        isPaused = true;
        initializeBtn.setVisible(false);
        testForCovidBtn.setDisable(true);
        coverRect.setVisible(false);
        numContact = 0;
        setGrid();
        genderList = genderSelector.getItems();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Non-binary");

        movementList = movementSelector.getItems();
        movementList.add("Minimal contact/exposure");
        movementList.add("Moderate contact/exposure");
        movementList.add("Significant contact/exposure");

        setSymptomsList();
        namesList = patientSelector.getItems();
        patientSelector.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(t1.intValue() >= 0){
                    coverRect.setVisible(false);
                    nameField.setText(namesList.get(t1.intValue()));
                    ageField.setText(getPatientByName(namesList.get(t1.intValue())).getAge());
                    genderSelector.setValue(getPatientByName(namesList.get(t1.intValue())).getGender());
                    chanceLbl.setText("Chance: " + getPatientByName(namesList.get(t1.intValue())).getChance() + "%");
                    movementSelector.setValue(getPatientByName(namesList.get(t1.intValue())).getStringInteraction());
                    startWithAddPatientErrorText.setVisible(false);
                    identicalNameErrorText.setVisible(false);
                    nonNumberErrorText.setVisible(false);

                    if(getPatientByName(namesList.get(t1.intValue())).getIsAddedToCT()){
                        addCTBtn.setDisable(true);
                        removeCTBtn.setDisable(false);
                    }
                    else{
                        addCTBtn.setDisable(false);
                        removeCTBtn.setDisable(true);
                    }
                    if((getPatientByName(namesList.get(t1.intValue())).getHasBeenTested())){
                        if(getPatientByName(namesList.get(t1.intValue())).getHasCovid()){
                            testedPositiveLbl.setVisible(true);
                            testedNegativeLbl.setVisible(false);
                        }
                        else{
                            testedNegativeLbl.setVisible(true);
                            testedPositiveLbl.setVisible(false);
                        }
                    }
                    //else{
                   //     System.out.println("here2 = enabled");
                   //     testForCovidBtn.setDisable(false);
                   // }

                    for(CheckBox c: symptomsList){
                        c.setSelected(false);
                    }
                    if(getPatientByName(namesList.get(t1.intValue())).getIsDead()){
                        coverRect.setVisible(true);
                    }
                    if(getPatientByName(namesList.get(t1.intValue())).getVaccinated()){
                        if(getPatientByName(namesList.get(t1.intValue())).getIsFullyImmunized()){
                            statusLbl.setText("Status: Fully immunized, " + getPatientByName(namesList.get(t1.intValue())).getVaxType());
                        }
                        else{
                            statusLbl.setText("Status: Partially immunized, " + getPatientByName(namesList.get(t1.intValue())).getVaxType());
                        }
                    }
                    else{
                        statusLbl.setText("Status: Unvaccinated");

                    }
                }
            }
        });

        namesListContact = patientSearchField.getItems();
        patientSearchField.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(t1.intValue() >=0){
                    nameTxt.setText("Name: " + namesListContact.get(t1.intValue()));
                    ageTxt.setText("Age: " + getPatientByName(nameTxt.getText().substring(6)).getAge());
                    congratsTxt.setVisible(false);
                    firstVaxTxt.setVisible(false);
                    secondVaxTxt.setVisible(false);
                    vaxErrorMessage.setVisible(false);
                    deadLbl.setVisible(false);

                    testForCovidBtn.setDisable(false);
                    testedNegativeLbl.setVisible(false);
                    testedPositiveLbl.setVisible(false);
                    vaxDatePicker.getEditor().clear();

                    //System.out.println("boolean " + getPatientByName(namesList.get(t1.intValue())).getHasBeenTested());

                    if(getPatientByName(namesList.get(t1.intValue())).getHasBeenTested()){
                        testForCovidBtn.setDisable(true);
                        if(getPatientByName(namesList.get(t1.intValue())).getHasCovid()){
                            testedPositiveLbl.setVisible(true);
                        }
                        else{
                            testedNegativeLbl.setVisible(true);
                        }
                    }
                    if(getPatientByName(namesList.get(t1.intValue())).getVaccinated()){
                        registerForVaxBtn.setDisable(true);
                    }
                    else{
                        registerForVaxBtn.setDisable(false);
                    }
                    if(getPatientByName(namesList.get(t1.intValue())).getIsDead()){
                        deadLbl.setVisible(true);
                        registerForVaxBtn.setDisable(true);
                    }
                }

            }
        });

        genderSelector.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(t1.intValue() >= 0){
                   try{
                        getPatientByName(patientSelector.getSelectionModel().getSelectedItem()).changeGender(genderList.get(t1.intValue()));
                    }
                   catch(Exception e){}
                    patientSelector.setValue(genderList.get(t1.intValue()));
                }
            }
        });

        movementSelector.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(t1.intValue() >= 0){
                    try{
                        getPatientByName(patientSelector.getSelectionModel().getSelectedItem()).setLevelOfInteraction(movementList.get(t1.intValue()));
                    }
                    catch(Exception e){}
                    patientSelector.setValue(movementList.get(t1.intValue()));
                }
            }
        });

        EventHandler z = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                int yPos = contactGrid.getRowIndex((ImageView) t.getSource());
                int xPos = contactGrid.getColumnIndex((ImageView) t.getSource());

                patientInfoList.getItems().clear();

                if(contactImages[yPos][xPos].getImage().equals(lightGreen)){
                    patientInfoList.getItems().add(0, "No patient at this spot");
                }
                else{
                    for(ContactTracer c: contactTracerPatients){
                        if(c.getxLoc() == xPos && c.getyLoc() == yPos){
                            Person p = c.getPatient();
                            patientInfoList.getItems().add(0, p.getName());
                            break;
                        }
                        else{
                            for(int i = 0; i < c.getSurroundingPoints().size(); i+= 2){
                                if(xPos == c.getSurroundingPoints().get(i) && yPos == c.getSurroundingPoints().get(i + 1)){
                                    Person p = c.getPatient();
                                    patientInfoList.getItems().add(0, p.getName());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
        for (int i = 0; i < contactImages.length; i++) {
            for (int j = 0; j < contactImages[0].length; j++) {
                contactImages[i][j].setOnMouseClicked(z);
            }
        }
        start();
    }

    //recursive method that with timer (called every second to update contact grid)
    private void start(){
        new AnimationTimer(){
            @Override
            public void handle(long now) {
                if (contactTracerPatients.size() > 0) {
                    if (!isPaused) {
                        for (ContactTracer c : contactTracerPatients) {
                            if (now - c.getStartTime() > 1000000000.0) {
                                if (!c.getPatient().getIsFullyImmunized() && c.getPatient().getVaccinated()) {
                                    if (!c.getPatient().getIsDoneWithSecVax()) {
                                        if (!c.getPatient().getIsDoneWithFirstVax()) {
                                            c.getPatient().incrementDaysTillFirstVax(1);
                                        }
                                        c.getPatient().incrementDaysTillSecVax(1);
                                    }
                                    c.getPatient().incrementDaysTillFullImmunization(1);
                                    //System.out.println("increment");
                                }
                                if (c.getPatient().getIsFullyImmunized()) {
                                    c.setType(1);
                                    contactTracerUpdates.getItems().add(0, c.getPatient().getName() + " is fully vaccinated!");
                                }

                                // if(c.checkMovement()){
                                c.movePerson(contactTypes);
                                //}

                                if (c.getType() == 3) {
                                    if (c.getPatient().getIsRecovered()) {
                                        c.getPatient().incrementDaysSinceRecovered(1);
                                    }
                                    if (c.getPatient().getDaysSinceRecovered() > 10 || c.getPatient().getDaysSinceRecovered() == -1) {
                                        int x = c.checkIfInfected(contactTypes);
                                        if (x == 0) {
                                            contactTracerUpdates.getItems().add(0, c.getPatient().getName() + " contracted COVID!");
                                            numContact++;
                                            c.getPatient().setHasCovid(true);
                                            c.getPatient().setIsRecovered(false);
                                            c.getPatient().incrementDaysSinceRecovered(-1 * c.getPatient().getDaysSinceRecovered() + 1);
                                        }
                                    }
                                }
                                if (c.getType() == 2) {
                                    c.increaseDayCounter(1);

                                    if (c.getDayCounter() > (Math.random() * 70 + 20)) {
                                        c.setType(3); //non-vax w/out covid
                                        c.getPatient().setHasCovid(false);
                                        c.getPatient().setIsRecovered(true);
                                        c.getPatient().incrementDaysSinceRecovered(1);
                                        contactTracerUpdates.getItems().add(0, c.getPatient().getName() + " has recovered!");
                                        ////to be removed - // just for testing
                                        ArrayList<Integer> coordinates = c.getSurroundingPoints();
                                        for (int i = 0; i < coordinates.size(); i += 2) {
                                            //System.out.println(" surrounding points of recovered patients :" + contactTypes[coordinates.get(i + 1)][coordinates.get(i)]);
                                            contactTypes[coordinates.get(i + 1)][coordinates.get(i)] = 0;
                                            ///// test code over
                                        }
                                    } else if (c.checkIfDead() && c.getDayCounter() > 20) {
                                        c.getPatient().setIsDead(true);
                                        contactTracerUpdates.getItems().add(0, c.getPatient().getName() + " has died.");
                                    }
                                }
                                c.resetStartTime();
                            }
                        }
                        checkDead();
                    }
                    updateGridVisuals();
                    setDataText();
                    updateListViews();
                    updateBarGraph();
                }
            }
        }.start();
    }

    //initializes contact grid - each cell has an image and integer type
    private void setGrid(){
        contactGrid.setGridLinesVisible(false);
        //System.out.println(contactGrid.getRowCount() + ", " + contactGrid.getColumnCount());
        for(int i = 0; i < contactGrid.getRowCount(); i++){
            for(int j = 0; j < contactGrid.getColumnCount(); j++){
                //System.out.println(i + ", " + j);
                contactImages[i][j] = new ImageView();
                contactImages[i][j].setImage(lightGreen);
                contactImages[i][j].setFitHeight(8);
                contactImages[i][j].setFitWidth(8);
                contactTypes[i][j] = 0;
                contactGrid.add(contactImages[i][j], j ,i);
            }
        }
    }

    //sets arrayList of symptoms seen on patient info page
    private void setSymptomsList(){
        symptomsList[0] = symptomsCoughing;
        symptomsList[1] = symptomsFever;
        symptomsList[2] = symptomsBreathing;
        symptomsList[3] = symptomsFatigue;
        symptomsList[4] = symptomsAches;
        symptomsList[5] = symptomsHeadache;
        symptomsList[6] = symptomsSmell;
        symptomsList[7] = symptomsThroat;
        symptomsList[8] = symptomsCongestion;
        symptomsList[9] = symptomsNausea;
        symptomsList[10] = symptomsDiarrhea;
        symptomsList[11] = symptomsTravel;
        symptomsList[12] = symptomsElder;
    }

    @FXML //sets or changes age of person
    private void handleSetAge(){
        nonNumberErrorText.setVisible(false);
        try{
            int age = Integer.parseInt(ageField.getText());
            Person p = getPatientByName(patientSelector.getValue());
            p.changeAge(age);
            enableTestForCovid();

        }
        catch (Exception e){
            nonNumberErrorText.setVisible(true);
        }
    }

    //filters through all Person objects based on person name, returns the person associated with name
    public Person getPatientByName(String name){
        for(Person p: patients){
            try{
                if(name.equals(p.getName())){
                    return p;
                }
          } catch (Exception e) {}
        }
        return null;
    }

    @FXML //called when you need to change name - pressing "enter key" inside name textbox
    private void handleSetName(){
        identicalNameErrorText.setVisible(false);
        int selectedIndex = patientSelector.getSelectionModel().getSelectedIndex();
        if(selectedIndex < 0){
            startWithAddPatientErrorText.setVisible(true);
            nameField.clear();
            ageField.clear();
            return;
        }
        if(patients.get(selectedIndex).getName().equals(nameField.getText())){
            return;
        }
        for(Person p : patients){
            if(nameField.getText().equals(p.getName())){
                identicalNameErrorText.setVisible(true);
                nameField.clear();
                return;
            }
        }
        patients.get(selectedIndex).changeName(nameField.getText());
        namesList.clear();
        for(Person p: patients){
            namesList.add(p.getName());
        }
        patientSelector.setValue(nameField.getText());
        enableTestForCovid();
        // nameField.clear();

    }

    @FXML //filtering names based on search entry
    private void handleFilterNames(){
        namesList.clear();
        for(Person p: patients){
            namesList.add(p.getName());
        }
        String searchString = searchBar.getText();
        ArrayList<String> tempNamesList = new ArrayList<String>();

        for(String name: namesList){
            if(name.contains(searchString)){
                tempNamesList.add(name);
            }
        }

       // System.out.println(tempNamesList);
        namesList.clear();
        for(String name : tempNamesList){
            namesList.add(name);
        }
    }

    @FXML // adding new patient
    private void handleAddPatient() {
        //System.out.println("entered handle AddPatient");
        Person x = new Person("New Patient");
        patients.add(x);
        namesList.add(patients.get(patients.size() - 1).getName());
        patientSelector.setValue(namesList.get(namesList.size() - 1));
        testedPositiveLbl.setVisible(false);
        testedNegativeLbl.setVisible(false);
        startWithAddPatientErrorText.setVisible(false);
    }

    @FXML //calculates "chance" of having covid based on symptoms selected
    private void handleCalculateChance(){
        boolean[] boxChecked = new boolean[symptomsList.length];

        for(int i = 0; i < boxChecked.length; i++){
            if(symptomsList[i].isSelected()){
                boxChecked[i] = true;
            }
            else{
                boxChecked[i] = false;
            }
        }
        Person selected = getPatientByName(patientSelector.getValue());
        try {
            selected.checkChanceCovid(boxChecked);
            chanceLbl.setText("Chance: " + selected.getChance() + "%");
        }
        catch(Exception e){

        }
    }

    @FXML //loads pre-created patients (from txt file) into program
    private void handleLoad(ActionEvent event) {
        loadBtn.setDisable(true);
        saveBtn.setDisable(false);
        try{
            FileReader reader = new FileReader("src/resources/names.txt");
            Scanner in = new Scanner(reader);
            while(in.hasNextLine())
            {
                String temp = in.nextLine();
                String name = temp.substring(0, temp.indexOf(", "));
                Person x = new Person(name);
                namesList.add(name);

                temp = temp.substring(temp.indexOf(", ") + 1);
                x.changeAge(Integer.parseInt(temp.substring(1, temp.indexOf(","))));

                temp = temp.substring(temp.indexOf(", ") + 1);
                x.changeGender(temp.substring(1, temp.indexOf(",")));

                temp = temp.substring(temp.indexOf(", ") + 1);
                x.setLevelOfInteraction(temp.substring(1));

                patients.add(x);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("SOMETHING HAS GONE HORRIBLY WRONG WE'RE ALL GONNA DIE!");
        }
        addPeopleToContactGrid();
        dataLists.filterPeople((ArrayList<Person>) patients);
    }

    @FXML //saves all patients to txt file (preloaded + manually added)
    private void handleSave(ActionEvent event){
        String outFile = "src/resources/names.txt";
        try {
            PrintWriter out = new PrintWriter(outFile);
            for(int i = 0; i < patients.size(); i++)
            {
                String patient = patients.get(i).getName() + ", " + patients.get(i).getAge() + ", " + patients.get(i).getGender() + ", " + patients.get(i).getStringInteraction();
                out.println(patient);
            }
            out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Something went wrong!");
        }
    }

    //adds all preloaded patients to contact grid to track movement
    private void addPeopleToContactGrid(){
        ArrayList<Integer> xList = new ArrayList<>();
        ArrayList<Integer> yList = new ArrayList<>();

        for(Person patient: patients){
            if(Math.random() > 0.9){
                //System.out.println(patient.getName() + " covid true");
                patient.setHasCovid(true);
            }
            else{
                patient.setHasCovid(false);
            }
            patient.setHasBeenTested(true);
            patient.setIsAddedToCT(true);

            int xPt = (int) (Math.random() * contactGrid.getColumnCount());
            int yPt = (int) (Math.random() * contactGrid.getRowCount());
            for(int i = 0; i < xList.size(); i++){
                while (xPt == xList.get(i) && yPt == yList.get(i)){
                    xPt = (int) (Math.random() * contactGrid.getColumnCount());
                    yPt = (int) (Math.random() * contactGrid.getRowCount());
                }
            }
            xList.add(xPt);
            yList.add(yPt);

            ContactTracer x = new ContactTracer(patient, xPt, yPt);
            contactTracerPatients.add(x);
            if(x.getType() == 2){
                contactImages[x.getyLoc()][x.getxLoc()].setImage(brightRed);
                contactTypes[x.getyLoc()][x.getxLoc()] = 2;
                for(int i = 0; i < x.getSurroundingPoints().size(); i+=2){
                    contactImages[x.getSurroundingPoints().get(i+1)][x.getSurroundingPoints().get(i)].setImage(pink);
                    contactTypes[x.getSurroundingPoints().get(i+1)][x.getSurroundingPoints().get(i)] = -2;
                }
            }
            if(x.getType() == 3){
                contactImages[x.getyLoc()][x.getxLoc()].setImage(limeGreen);
                contactTypes[x.getyLoc()][x.getxLoc()] = 3;
                for(int i = 0; i < x.getSurroundingPoints().size(); i+=2){
                    contactImages[x.getSurroundingPoints().get(i+1)][x.getSurroundingPoints().get(i)].setImage(yellow);
                }

            }
        }
    }

    //adds individual patient to contact tracer (patients manually filed/added)
    private void addNewContactTracer(Person p){
        ArrayList<Integer> xList = new ArrayList<>();
        ArrayList<Integer> yList = new ArrayList<>();
        for(ContactTracer c: contactTracerPatients){
            xList.add(c.getxLoc());
            yList.add(c.getyLoc());
        }

        int xPt = (int) (Math.random() * contactGrid.getColumnCount());
        int yPt = (int) (Math.random() * contactGrid.getRowCount());

        for(int i = 0; i < xList.size(); i++){
            while (xPt == xList.get(i) && yPt == yList.get(i)){
                xPt = (int) (Math.random() * contactGrid.getColumnCount());
                yPt = (int) (Math.random() * contactGrid.getRowCount());
            }
        }

        ContactTracer x = new ContactTracer(p, xPt, yPt);
        contactTracerPatients.add(x);

    }

    @FXML //calls addNewContactTracer if preconditions are met
    private void handleAddContactTracer(){
        Person p = getPatientByName(patientSelector.getValue());
        //System.out.println("reached");
        try {
            if (p.getHasBeenTested()) {
                //System.out.println(getPatientByName(p.getName()));
                addNewContactTracer(getPatientByName(patientSelector.getValue()));
                addCTErrorLbl.setVisible(false);
                getPatientByName(patientSelector.getValue()).setIsAddedToCT(true);
                addCTBtn.setDisable(true);
                removeCTBtn.setDisable(false);
            } else {
                addCTErrorLbl.setVisible(true);
            }
        }
        catch(Exception e){

        }
    }

    @FXML //removes a patient currently on the grid from the grid
    private void handleRemoveContactTracer(){
        try {
            if (getPatientByName(patientSelector.getValue()).getIsAddedToCT()) {
                for (int i = 0; i < contactTracerPatients.size(); i++) {
                    if (contactTracerPatients.get(i).getPatient().equals(getPatientByName(patientSelector.getValue()))) {
                        contactTypes[contactTracerPatients.get(i).getyLoc()][contactTracerPatients.get(i).getxLoc()] = 0;
                        contactTracerPatients.remove(i);
                        getPatientByName(patientSelector.getValue()).setIsAddedToCT(false);
                        addCTBtn.setDisable(false);
                        removeCTBtn.setDisable(true);
                        updateGridVisuals();
                    }
                }
            }
        }
        catch(Exception e){}
    }

    //changes colors and values of squares on contact grid to display newest populations
    private void updateGridVisuals(){
        for(int i = 0; i < contactTypes.length; i++){
            for(int j = 0; j < contactTypes[0].length; j++){
               if(contactTypes[i][j] <= 0){
                    contactImages[i][j].setImage(lightGreen);
                }
               else{
                   contactImages[i][j].setImage(darkBlue);
               }
            }
        }
        for(ContactTracer c:contactTracerPatients){
            if(c.getType() == 1){
                contactImages[c.getyLoc()][c.getxLoc()].setImage(darkBlue);
            }
            if(c.getType() == 2){
                if(c.getPatient().getHasCovid() == c.getPatient().getIsRecovered()){
                    //System.out.println("has covid " + c.getPatient().getHasCovid() + ", recovery " + c.getPatient().getIsRecovered() + ", type " + c.getType());
                }
                for(int i = 0; i < c.getSurroundingPoints().size(); i+=2){
                    if(contactTypes[c.getSurroundingPoints().get(i + 1)][c.getSurroundingPoints().get(i)] <= 0){
                        contactImages[c.getSurroundingPoints().get(i+1)][c.getSurroundingPoints().get(i)].setImage(pink);
                        //contactTypes[c.getSurroundingPoints().get(i+1)][c.getSurroundingPoints().get(i)] = -2;
                    }
                }
                contactImages[c.getyLoc()][c.getxLoc()].setImage(brightRed);
            }
            if(c.getType() == 3){
                for(int i = 0; i < c.getSurroundingPoints().size(); i+=2){
                    if(contactTypes[c.getSurroundingPoints().get(i + 1)][c.getSurroundingPoints().get(i)] <= 0){
                        contactImages[c.getSurroundingPoints().get(i+1)][c.getSurroundingPoints().get(i)].setImage(yellow);
                    }
                }
                contactImages[c.getyLoc()][c.getxLoc()].setImage(limeGreen);
            }
        }
        //contactImages[72][89].setImage(darkBlue);
    }

    //check to see if patient on contact tracer is dead, and if so, remove from grid
    private void checkDead(){
        for(int i = contactTracerPatients.size() - 1; i >=0; i--){
            if (contactTracerPatients.get(i).getPatient().getIsDead()){
                contactTypes[contactTracerPatients.get(i).getyLoc()][contactTracerPatients.get(i).getxLoc()] = 0;
                for(int a = 0; a < contactTracerPatients.get(i).getSurroundingPoints().size(); a+=2){
                    contactTypes[contactTracerPatients.get(i).getSurroundingPoints().get(a+1)][contactTracerPatients.get(i).getSurroundingPoints().get(a)] = 0;
                }
                contactTracerPatients.remove(i);

            }
        }
    }

    @FXML //if patient has not been tested, performs a covid test to get covid positive/negative
    private void handleCovidTest(){
        //System.out.println(patientSelector.getValue());
        try {
            getPatientByName(patientSelector.getValue()).patientCovidTest();
            if (getPatientByName(patientSelector.getValue()).getHasCovid()) {
                testedPositiveLbl.setVisible(true);
                testedNegativeLbl.setVisible(false);
                if (getPatientByName(patientSelector.getValue()).getIsAddedToCT()) {
                    for (ContactTracer c : contactTracerPatients) {
                        if (c.getPatient().equals(getPatientByName(patientSelector.getValue()))) {
                            c.setType(3);
                        }
                    }
                }
            } else {
                testedNegativeLbl.setVisible(true);
                testedPositiveLbl.setVisible(false);
            }
            testForCovidBtn.setDisable(true);
        }
        catch (Exception e){}
    }

    @FXML //called to set up bar graphs and text displaying population data
    private void handlePopData(){
        setBarGraph();
        setDataText();
        popDataBtn.setDisable(true);
    }

    //sets settings for two population bar graphs
    private void setBarGraph(){
        popBarGraph.setTitle("COVID Infection Summary");
        xAxis.setLabel("Status");
        xAxis.setTickLabelRotation(45);
        yAxis.setLabel("Number of People");
        yAxis.setUpperBound(100);
        yAxis.setLowerBound(0);
        yAxis.setAutoRanging(false);

        series.getData().add(new XYChart.Data("COVID Unimpacted" , dataLists.getCovidUnimpacted().size()));
        series.getData().add(new XYChart.Data("COVID Positive", dataLists.getCovidPositive().size()));
        series.getData().add(new XYChart.Data("COVID Recovered", dataLists.getCovidRecovered().size()));
        series.getData().add(new XYChart.Data("COVID Deaths", dataLists.getCovidDead().size()));

        popBarGraph.getData().add(series);

        popChart2.setTitle("Vaccination Summary");
        xAxis2.setLabel("Status");
        yAxis2.setLabel("Number of People");
        xAxis2.setTickLabelRotation(45);
        yAxis2.setLowerBound(0);
        yAxis2.setUpperBound(100);

        series2.getData().add(new XYChart.Data("Not Registered for Vax", dataLists.getNonVax().size()));
        series2.getData().add(new XYChart.Data("Registered for Vax", dataLists.getRegisteredVax().size()));
        series2.getData().add(new XYChart.Data("Received First Dose",dataLists.getVaccinatedFirst().size()));
        series2.getData().add(new XYChart.Data("Received Second Dose", dataLists.getVaccinatedSecond().size()));
        series2.getData().add(new XYChart.Data("Fully Immunized", dataLists.getFullyImmune().size()));
        series2.getData().add(new XYChart.Data("Fully Immunized", dataLists.getFullyImmune().size()));

        popChart2.getData().add(series2);
    }

    //sets text that is shown below the bar graphs
    private void setDataText(){
        totalPopTxt.setText("Total Population: " + patients.size());
        contactPtsTxt.setText("Number of Points of Contact: " + numContact);
    }

    //updates data shown on the bar graph
    private void updateBarGraph(){
        series.getData().clear();

        series.getData().add(new XYChart.Data("COVID Unimpacted" , dataLists.getCovidUnimpacted().size()));
        series.getData().add(new XYChart.Data("COVID Positive", dataLists.getCovidPositive().size()));
        series.getData().add(new XYChart.Data("COVID Recovered", dataLists.getCovidRecovered().size()));
        series.getData().add(new XYChart.Data("COVID Deaths", dataLists.getCovidDead().size()));

        series2.getData().clear();
        series2.getData().add(new XYChart.Data("Not Registered For Vaccination", dataLists.getNonVax().size()));
        series2.getData().add(new XYChart.Data("Registered for Vaccination", dataLists.getRegisteredVax().size()));
        series2.getData().add(new XYChart.Data("Received First Dose",dataLists.getVaccinatedFirst().size()));
        series2.getData().add(new XYChart.Data("Received Second Dose", dataLists.getVaccinatedSecond().size()));
        series2.getData().add(new XYChart.Data("Fully Immunized", dataLists.getFullyImmune().size()));
        series2.getData().add(new XYChart.Data("Fully Immunized", dataLists.getFullyImmune().size()));

    }

    @FXML //manages search bar on contact tracer page
    private void handleFilterNamesContact(){
        namesListContact.clear();
        //System.out.println("In handleFilerNamesContact... All patients names");
        for(Person p: patients){
            namesListContact.add(p.getName());
           // System.out.println(p.getName() + " is dead " + p.getIsDead());
        }
        String searchString = patientSearchBar.getText();
        ArrayList<String> tempNamesList = new ArrayList<String>();

        for(String name: namesListContact){
            if(name.toLowerCase().contains(searchString.toLowerCase())){
                tempNamesList.add(name);
            }
        }
        namesListContact.clear();
        for(String name : tempNamesList){
            namesListContact.add(name);
        }
    }

    @FXML //signs people up for vaccination
    private void registerForVax(){
        Person p = getPatientByName(patientSearchField.getValue());
        vaxErrorMessage.setVisible(true);

        if (p.getAge() == "N/A"){
            vaxErrorMessage.setText("Patient age needed to proceed");
        }
        else if(Integer.parseInt(p.getAge()) <= 15){
            vaxErrorMessage.setText("Patient does not qualify for vaccine (age)");
        }
        else if(p.getHasCovid()){
            vaxErrorMessage.setText("Patient may not get vaccinated while infected");
        }
        else if(vaxDatePicker.getValue() == null){
            vaxErrorMessage.setText("Please select first date");
        }
        else if(ChronoUnit.DAYS.between(java.time.LocalDate.now(), vaxDatePicker.getValue()) < 1){
            vaxErrorMessage.setText("Please select a day in the future");
        }
        else{
            vaxErrorMessage.setVisible(false);
            LocalDate daySelected = vaxDatePicker.getValue();
            LocalDate secondDate = daySelected.plusDays(28);
            firstVaxTxt.setText("Vaccination 1 date: " + daySelected);
            secondVaxTxt.setText("Vaccination 2 date: " + secondDate);
            congratsTxt.setVisible(true);
            firstVaxTxt.setVisible(true);
            secondVaxTxt.setVisible(true);
            p.setVaccinated(true);

            LocalDate today = java.time.LocalDate.now();
            long daysTillVax = ChronoUnit.DAYS.between(today, daySelected);

            p.setDaysTillFirstVax((int) daysTillVax);
            //System.out.println("Days till first vax: " + daysTillVax);
            p.setDaysTillSecondVax((int)daysTillVax + 28);
            p.setDaysTillFullImmunization((int) daysTillVax + 28 + 10);
            p.determineVaxType();
        }
    }

    //updates people shown in each listView on contact page
    private void updateListViews(){
        dataLists.filterPeople((ArrayList<Person>) patients);
        covidPositiveList.getItems().clear();
        for(Person p: dataLists.getCovidPositive()){
            covidPositiveList.getItems().add(p.getName());
        }
        covidRecoveredList.getItems().clear();
        for(Person p: dataLists.getCovidRecovered()){
            covidRecoveredList.getItems().add(p.getName());
        }
        covidDeathsList.getItems().clear();
        for(Person p: dataLists.getCovidDead()){
            covidDeathsList.getItems().add(p.getName());
        }
        vaxFirstDoseList.getItems().clear();
        for(Person p: dataLists.getVaccinatedFirst()){
            vaxFirstDoseList.getItems().add(p.getName());
        }
        vaxSecondDoseList.getItems().clear();
        for(Person p: dataLists.getVaccinatedSecond()){
            vaxSecondDoseList.getItems().add(p.getName());
        }
        fullyImmunizedList.getItems().clear();
        for(Person p: dataLists.getFullyImmune()){
            fullyImmunizedList.getItems().add(p.getName());
        }
    }

    //enables covid test button for patients not previously tested
    private void enableTestForCovid(){
        Person p = getPatientByName(patientSelector.getValue());
        if(!p.getName().equals("New Patient") && !p.getAge().equals("N/A")){
               testForCovidBtn.setDisable(false);
        }
    }

    @FXML //vaccinates a large number of patients all at once
    private void handleMassVax(){
        // eliminate patients who has covid, registered for vaccinated, below 16, dead
        //System.out.println("In handleMassVax... ");
        int daysTillVax = 1;
        boolean alternatePatient = true;
        for(Person p: patients){

            if (!p.getHasCovid() && !p.getIsDead() && !p.getVaccinated() && Integer.parseInt(p.getAge()) >= 12) {
                if (alternatePatient){
                    //System.out.println(p.getName() );
                    p.setVaccinated(true);
                    p.setDaysTillFirstVax(daysTillVax);
                    p.setDaysTillSecondVax(daysTillVax + 28);
                    p.setDaysTillFullImmunization(daysTillVax + 28 + 10);
                    daysTillVax += 2;
                    p.determineVaxType();

                    ContactTracer c = getCTFromPerson(p);
                    if(c != null){
                        ArrayList<Integer> coordinates = c.getSurroundingPoints();
                        for(int i = 0; i< coordinates.size(); i+=2){
                            contactTypes[coordinates.get(i+1)][coordinates.get(i)] = 0;
                        }
                    }
                }
                alternatePatient = !alternatePatient;
            }
        }
        //System.out.println("In handleMassVax...for loop completed ");
    }

    @FXML //pause-play button for simulation
    private void handlePause(){
        if(isPaused){
            isPaused = false;
            pauseButton.setText("Pause");
        }
        else{
            isPaused = true;
            pauseButton.setText("Play");
        }
    }

    @FXML //given a Person object, returns corresponding contact tracer object through linear search
    private ContactTracer getCTFromPerson(Person p){
        for(ContactTracer c: contactTracerPatients){
            if(c.getPatient().equals(p)){
                return c;
            }
        }
        return null;
    }
}

