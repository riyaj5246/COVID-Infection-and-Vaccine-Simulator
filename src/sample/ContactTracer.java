package sample;

import java.util.ArrayList;
import java.util.Random;

public class ContactTracer {
    private Person patient; //patient associated with "spot" on contact grid
    private int xLoc; //x-coordinate of spot
    private int yLoc; //y-coordinate of spot
    private long startTime; //used to track one second movements
    private int dayCounter; //tracks days since infection
    private int type;
        //type 1: fully immune (only possible through vaccination
        //type 2: non-vaccinated, has covid
        //type 3: non-covid but non-vaccinated, so susceptible to covid
    private int prevX; //previous x-coordinate
    private int prevY; //previous y-coordinate

    //constructor - no patients start as type 1
    public ContactTracer(Person p, int x, int y){
        patient = p;
        xLoc = x;
        yLoc = y;
        prevX = x;
        prevY = y;
        dayCounter = 0;
        if(p.getHasCovid()){
            type = 2;
        }
        else{
            type = 3;
        }
        startTime = System.nanoTime();
    }

    //returns all surrounding points around location
    public ArrayList<Integer> getSurroundingPoints(){
        ArrayList<Integer> coordinates = new ArrayList<>();
        for(int i = xLoc - 2; i <= xLoc + 2; i++){
            for(int j = yLoc - 2; j <= yLoc + 2; j++){
                boolean xBounds = (i >= 0) && (i < 90);
                boolean yBounds = (j >= 0) && (j < 73);
                boolean notCenter = !(i == xLoc && j == yLoc);
                if(xBounds && yBounds && notCenter){
                    coordinates.add(i);
                    coordinates.add(j);
                }
            }
        }
        return coordinates;
    }

    //moves a person to a new grid location
    public void movePerson(int[][] types){
        ArrayList<Integer> potentialCoordinatesX = new ArrayList<>();
        ArrayList<Integer> potentialCoordinatesY = new ArrayList<>();

        //collects full list of potential movement locations (accounts for outer bounds)
        for (int a = xLoc - 1; a <= xLoc + 1; a++) {
            for (int b = yLoc - 1; b <= yLoc + 1; b++) {
                if( a < 90 && a > -1 && b < 73 && b > -1){
                    if (types[b][a] <= 0 ) {
                        if(patient.getLevelOfInteraction() >= 1){
                            if(!(a == prevX && b == prevY)){
                                potentialCoordinatesX.add(a);
                                potentialCoordinatesY.add(b);
                            }
                        }
                        else{
                            potentialCoordinatesX.add(a);
                            potentialCoordinatesY.add(b);
                        }
                    }
                }
            }
        }

        if(potentialCoordinatesX.size() == 0){
            //System.out.println("no movement");
            return;
        }
        else{
            //moves person to new location
            int randIndex = (int) (Math.random() * potentialCoordinatesX.size());
            int xCoordinate = potentialCoordinatesX.get(randIndex);
            int yCoordinate = potentialCoordinatesY.get(randIndex);

            if(type == 2){
                ArrayList<Integer> coordinates = this.getSurroundingPoints();
                for(int i = 0; i< coordinates.size(); i+=2){
                    types[coordinates.get(i+1)][coordinates.get(i)] = 0;
                }
            }

            types[yCoordinate][xCoordinate] = this.type;
            types[yLoc][xLoc] = 0;
            prevX = xLoc;
            prevY = yLoc;
            xLoc = xCoordinate;
            yLoc = yCoordinate;

            if(type == 2){
                ArrayList<Integer> coordinates = this.getSurroundingPoints();
                for(int i = 0; i< coordinates.size(); i+=2){
                    types[coordinates.get(i+1)][coordinates.get(i)] = -2;
                }
            }
        }
    }

    //checks if person has been infected by type 2 (covid positive) patient
    public int checkIfInfected(int[][] types){
        ArrayList<Integer> coordinates = this.getSurroundingPoints();
        double chanceOfInfection = Math.random();
        if(patient.getIsDoneWithSecVax()){
            chanceOfInfection -= 0.1;
        }
        else if(patient.getIsDoneWithFirstVax()){
            chanceOfInfection -= 0.05;
        }
        for(int i = 0; i < coordinates.size(); i+= 2){
            //System.out.println("Grid val: " + types[coordinates.get(i+1)][coordinates.get(i)] );
            if(types[coordinates.get(i+1)][coordinates.get(i)] == -2 || types[coordinates.get(i+1)][coordinates.get(i)] == 2){
                if(chanceOfInfection > 0.05){
                    this.setType(2);
                    this.getPatient().setHasCovid(true);
                    return 0;
                }
            }
        }
        return 1;
    }

    //checks if patient is dead - based on age, randomness, and vaccination
    public boolean checkIfDead(){
        double chanceOfDeath = Math.random();
        Random bool = new Random();

        try{
            if(Integer.parseInt(patient.getAge()) > 75){
                chanceOfDeath *= 1.25;
            }
            else if(Integer.parseInt(patient.getAge()) > 60){
                chanceOfDeath *= 1.1;
            }

            if(getPatient().getIsDoneWithFirstVax()){
                chanceOfDeath *=0.9;
            }
            else if(getPatient().getIsDoneWithSecVax()){
                chanceOfDeath *= 0.8;
            }
        }catch (Exception e){}


        if(chanceOfDeath > 0.95 && bool.nextBoolean()) {
            return true;
        }
        return false;
    }

    public Person getPatient() {
        return patient;
    }

    public void setType(int i){
        type = i;
    }

    public void increaseDayCounter(int days){
        dayCounter += days;
    }

    public int getDayCounter(){
        return dayCounter;
    }

    public void resetDayCounter(){
        dayCounter = 0;
    }

    public int getType(){
        return type;
    }

    public int getxLoc(){
        return xLoc;
    }

    public int getyLoc(){
        return yLoc;
    }

    public void resetStartTime(){
        startTime = System.nanoTime();
    }

    public long getStartTime(){
        return startTime;
    }

}
