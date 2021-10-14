package sample;

public class Person {

    private String personName; //name (primary identification)
    private int personAge; //age
    private String personGender; //gender
    private double personChanceofCovid; //percent chance of covid
    private boolean[] boxesChecked; //array of which symptoms patient shows
    private int levelOfInteraction; //1 - minimal, 2 - moderate, 3 - extreme

    private boolean hasBeenTested; //get rid of/useless
    private boolean isAddedToCT; //whether patient is on the contact grid

    private boolean hasCovid; //COVID Positive
    private boolean isVaccinated; //Signed up for vax
    private boolean isRecovered; //COVID recovered, not vaccinated
    private boolean isDead; //is dead
    private boolean isDoneWithFirstVax; //got first vax
    private boolean isDoneWithSecVax; //got second vax
    private boolean isFullyImmunized; //is completely immune (first + second)
    private String vaxType; //Pfizer or Moderna

    private int daysTillFirstVax; //number of days until patient receives first vax
    private int daysTillSecondVax; //number of days until patient receives second vax
    private int daysTillFullImmunization; //number of days until patient is fully immunized
    private int daysSinceRecovered; //number of days since recovery from COVID

    //constructor - most defaults set to false
    public Person(String name){
        personName = name;
        personChanceofCovid = 0.0;
        boxesChecked = new boolean[13];
        hasBeenTested = false;
        levelOfInteraction = 1;
        isRecovered = false;
        isVaccinated = false;
        isDoneWithFirstVax = false;
        isDoneWithSecVax = false;
        isFullyImmunized = false;
        isDead = false;
        daysSinceRecovered = -1;
    }

    //calculates chance of patient having COVID based on which symptoms they have
    public void checkChanceCovid(boolean[] checked){
        //mild - 4, 5, 6, 7, 9, 10, 11 - weightage: 2x
        //severe - 0, 1, 2, 3, 8, 12 - weightage: 3x
        int sum = 0;
        for(int i = 0; i < checked.length; i++){
            if(checked[i]){
                if(i == 4 || i == 5 || i == 6 || i ==7 || i == 9 || i ==10 || i == 11){
                    sum += 2;
                }
                else{
                    sum+= 3;
                }
            }
            boxesChecked[i] = checked[i];
        }
        double x = Math.floor(((double) sum/32) * 10000);
        x = (double) x/100;
        personChanceofCovid = x;
    }

    //performs patient covid test based on vaccination status, chance of covid, age, and level of interaction
    public void patientCovidTest(){
        double chance = 0;
        if(isVaccinated){
            hasCovid = false;
            return;
        }

        if(personChanceofCovid == 0){
            chance = (Math.random() * 50) + 25;
        }
        else{
            chance = personChanceofCovid + (Math.random()* 25) + 5;
        }
        if(personAge > 65){
            chance *= 1.1;
        }
        if(levelOfInteraction == 3){
            chance *= 1.3;
        }
        else if(levelOfInteraction == 2){
            chance *= 1.2;
        }
        if(hasCovid){
            chance *= 3;
        }

        System.out.println(chance);
        hasBeenTested = true;

        if(chance/100 >= Math.random()){
            hasCovid = true;
        }
        else{
            hasCovid = false;
        }
    }

    //increments number until days left is 0, at which point first vax variable is set to true
    public void incrementDaysTillFirstVax(int d){
        daysTillFirstVax -= d;
        if(daysTillFirstVax <= 0){
            isDoneWithFirstVax = true;
        }
    }

    //increments number until days left is 0, at which point second vax variable is set to true
    public void incrementDaysTillSecVax(int d){
        daysTillSecondVax -= d;
        if(daysTillSecondVax <= 0){
            isDoneWithSecVax = true;
        }
    }

    //increments days until patient is fully immunized, at which point, cannot have COVID
    public void incrementDaysTillFullImmunization(int d){
        daysTillFullImmunization -= d;
        if(daysTillFullImmunization <= 0){
            isFullyImmunized = true;
            hasCovid = false;
        }
    }

    //randomly generates vaccination type for those registered for vaccination
    public void determineVaxType(){
        if(personAge <= 18){
            vaxType = "Pfizer";
        }
        else{
            if(Math.random() > 0.5){
                vaxType = "Moderna";
            }
            else{
                vaxType = "Pfizer";
            }
        }
    }

    /* ALL REMAINING METHODS IN THE PERSON CLASS ARE SIMPLY SET AND RETURN STATEMENTS FOR EVERY INSTANCE FIELD */

    public String getVaxType(){
        return vaxType;
    }

    public void setDaysTillFirstVax(int d){
        daysTillFirstVax = d;
    }

    public void setDaysTillFullImmunization(int d){
        daysTillFullImmunization = d;
    }

    public void setDaysTillSecondVax(int d){
        daysTillSecondVax = d;
    }

    public boolean getIsDoneWithFirstVax(){
        return isDoneWithFirstVax;
    }

    public boolean getIsDoneWithSecVax(){
        return isDoneWithSecVax;
    }

    public boolean getIsFullyImmunized(){
        return isFullyImmunized;
    }

    public void setIsRecovered(boolean b){
        isRecovered = b;
    }

    public boolean getIsRecovered(){
        return isRecovered;
    }

    public boolean getIsAddedToCT(){
        return isAddedToCT;
    }

    public void setVaccinated(boolean b){
        isVaccinated = b;
    }

    public boolean getVaccinated(){
        return isVaccinated;
    }

    public void setIsAddedToCT(boolean b){
        isAddedToCT = b;
    }

    public void setHasBeenTested(boolean b){
        hasBeenTested = b;
    }

    public boolean getHasBeenTested(){
        return hasBeenTested;
    }

    public void setLevelOfInteraction(String x){
        if(x.equals("Minimal contact/exposure")){
            levelOfInteraction = 1;
        }
        else if(x.equals("Moderate contact/exposure")){
            levelOfInteraction = 2;
        }
        else{
            levelOfInteraction = 3;
        }
    }

    public void setHasCovid(boolean covid){
        hasCovid = covid;
    }

    public boolean getHasCovid(){
        return hasCovid;
    }

    public String getStringInteraction(){
        if(levelOfInteraction == 1){
            return "Minimal contact/exposure";
        }
        else if (levelOfInteraction == 2){
            return "Moderate contact/exposure";
        }
        else if(levelOfInteraction == 3){
            return "Significant contact/exposure";
        }
        return "N/A";
    }

    public int getLevelOfInteraction(){
        return levelOfInteraction;
    }

    public void changeName(String name){
        personName = name;
    }

    public String getName(){
        return personName;
    }

    public void changeAge(int n){
        personAge = n;
    }

    public String getAge(){
        if(personAge == 0){
            return "N/A";
        }
        return personAge + "";
    }

    public void changeGender(String g){
        personGender = g;
    }

    public String getGender(){
        if(personGender == null){
            return "N/A";
        }
        return personGender;
    }

    public double getChance(){
        return personChanceofCovid;
    }

    public boolean getIsDead(){
        return isDead;
    }

    public void setIsDead(boolean b){
        isDead = b;
    }

    public void incrementDaysSinceRecovered(int x){
        daysSinceRecovered += x;
    }

    public int getDaysSinceRecovered(){
        return daysSinceRecovered;
    }
}
