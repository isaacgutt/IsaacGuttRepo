package edu.yu.cs.com1320.se_practice.week1;

import edu.yu.cs.com1320.se_practice.UniversityMember;

import java.util.*;

public class Student extends UniversityMember {

    private long bannerID;
    private String pin;
    private final String name;
    private HashMap<Professor, Integer> grades = new HashMap<>();
    private HashMap<Professor, String> classes = new HashMap<>();
    private int preRequisites = 0;
    private boolean signedIn = false;
    private final int rank;
    private Registrar registrar;


    public Student(String name, String pin, Registrar registrar){
        this.name = name;
        this.pin = pin;
        this.registrar = registrar;
        bannerID = hashCode();
        this.rank = 1;
    }
    //Students can't change grade, can only check if professor posted them
    public void openGrades(Professor professor){
        try{
            grades.put(professor, registrar.getGrade(professor, this));
        }catch(NullPointerException e){
            System.out.println("grades not available yet");
        }
    }
    public Double getGPA(){
        Double gpa = 0.0;
        for(Integer grade : grades.values()){
            gpa += grade;
        }
        gpa /= grades.size();
        return gpa*0.04;
    }
    public Integer getRank(){
        return this.rank;
    }
    public String getName(UniversityMember um){
        checkAccess(um);
        return this.name;
    }
    public boolean signIn(String password){
        if(password.equals(pin)){
            signedIn = true;
            return true;
        }
        System.out.println("Wrong password, try again;");
        return false;
    }
    public long getBanner(UniversityMember um){
        checkAccess(um);
        return this.bannerID;
    }
    //only professor can give the student an addition to his preRecs
    public void finishedPreRec(Professor professor){
        if(professor.isSignedIn()) {
            preRequisites++;
        }
    }
    public int getPreRequisites(UniversityMember um){
        checkAccess(um);
        return preRequisites;
    }
    public void removeCourse(Professor professor){
        checkAccess(this);
        registrar.removeCourse(professor, this);
        grades.remove(professor);
        classes.remove(professor);
    }

    public void addCourse(Professor professor){
        checkAccess(this);
        //check if preRecs are enough to be addes
        boolean check = registrar.addCourse(professor,this);
        if(check) {
            classes.put(professor, professor.getCourse());
        }
    }
    protected HashMap getAllGrades(UniversityMember um){
        checkAccess(um);
        HashMap<Professor, Integer> end = new HashMap<>();
        end.putAll(grades);
        return end;
    }
    protected HashMap getAllClasses(UniversityMember um){
        checkAccess(um);
        HashMap<Professor, String> end = new HashMap<>();
        end.putAll(classes);
        return end;
    }
    protected Integer getGradeOfCourse(Professor professor, UniversityMember um){
        checkAccess(um);
        try{
            return grades.get(professor);
        }catch (NullPointerException e){
            return null;
        }
    }
    //If it's a student, he needs to sign in. If it's any other level,
    //they're allowed to use the method if they're signed in
    private void checkAccess(UniversityMember um){
        if(um instanceof Student) {
            if (!signedIn) {
                throw new IllegalStateException("Please sign in with signIn()");

            }
        }
        else if(!um.isSignedIn()){
            throw new IllegalStateException("Please Sign in with SignIn()");
        }


    }
}