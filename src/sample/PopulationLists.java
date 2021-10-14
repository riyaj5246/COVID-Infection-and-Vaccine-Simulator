package sample;

import java.util.ArrayList;

public class PopulationLists {

    private ArrayList<Person> covidPositive; //people who have tested covid positive
    private ArrayList<Person> covidRecovered; //people who have recovered from covid
    private ArrayList<Person> covidDead; //people who died from covid
    private ArrayList<Person> nonVax; //people who are not vaccinated
    private ArrayList<Person> registeredVax; //people who registered to get vaccine
    private ArrayList<Person> vaccinatedFirst; //people who got the first dose of vaccine
    private ArrayList<Person> vaccinatedSecond; //people who got the second dose of vaccine
    private ArrayList<Person> fullyImmune; //people who are fully immune
    private ArrayList<Person> covidUnimpacted; //people who are completely untouched by COVID

    //constructor
    public PopulationLists(){
        covidPositive = new ArrayList<>();
        covidRecovered = new ArrayList<>();
        covidDead = new ArrayList<>();
        nonVax = new ArrayList<>();
        registeredVax = new ArrayList<>();
        vaccinatedFirst = new ArrayList<>();
        vaccinatedSecond = new ArrayList<>();
        fullyImmune = new ArrayList<>();
        covidUnimpacted = new ArrayList<>();
    }

    //retrieves information from person class and categorizes all people into arraylists
    public void filterPeople(ArrayList<Person> p){
        clearAllLists();
        for(Person patient: p){
            if(patient.getIsDead()){
                covidDead.add(patient);
            }
            else{
                if(patient.getHasCovid()){
                    covidPositive.add(patient);
                }
                else if(patient.getIsRecovered()){
                    covidRecovered.add(patient);
                }
                else{
                    covidUnimpacted.add(patient);
                }

                if(patient.getIsFullyImmunized()){
                    fullyImmune.add(patient);
                }
                else if(patient.getIsDoneWithSecVax() && patient.getIsDoneWithFirstVax()) {
                    vaccinatedSecond.add(patient);
                }
                else if(patient.getIsDoneWithFirstVax()){
                    vaccinatedFirst.add(patient);
                }
                else if(patient.getVaccinated()){
                    registeredVax.add(patient);
                }
                else{
                    nonVax.add(patient);
                }
            }
        }
    }

    //clears all array lists
    public void clearAllLists(){
        covidPositive.clear();
        covidRecovered.clear();
        vaccinatedFirst.clear();
        vaccinatedSecond.clear();
        fullyImmune.clear();
        covidDead.clear();
        nonVax.clear();
        registeredVax.clear();
        covidUnimpacted.clear();
    }

    //REMAINING METHODS ALL RETURN ALL ARRAYLISTS
    public ArrayList<Person> getCovidPositive(){
        return covidPositive;
    }

    public ArrayList<Person> getCovidRecovered(){
        return covidRecovered;
    }

    public ArrayList<Person> getCovidDead(){
        return covidDead;
    }

    public ArrayList<Person> getVaccinatedFirst(){
        return vaccinatedFirst;
    }

    public ArrayList<Person> getVaccinatedSecond(){
        return vaccinatedSecond;
    }

    public ArrayList<Person> getFullyImmune(){
        return fullyImmune;
    }

    public ArrayList<Person> getNonVax() {
        return nonVax;
    }

    public ArrayList<Person> getRegisteredVax() {
        return registeredVax;
    }

    public ArrayList<Person> getCovidUnimpacted(){
        return covidUnimpacted;
    }
}
