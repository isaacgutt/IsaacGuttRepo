package edu.yu.cs.com1320.se_practice.week2;

import edu.yu.cs.com1320.se_practice.UniversityMember;
import edu.yu.cs.com1320.se_practice.week2.Professor;
import edu.yu.cs.com1320.se_practice.week2.Registrar;

import java.util.*;

public class Student extends UniversityMember {

    private long bannerID;
    private String pin;
    private final String name;
    private HashMap<Professor, Integer> classes = new HashMap<>();
    private int preRequisites = 0;
    private boolean signedIn = false;
    private final int rank;


    public Student(String name, String pin){
        this.name = name;
        this.pin = pin;
        bannerID = hashCode();
        this.rank = 1;
    }
    //Students can't change grade, can only check if professor posted them
    public void openGrades(Professor professor, Registrar registrar){
        try{
            classes.put(professor, registrar.getGrade(professor, this));
        }catch(NullPointerException e){
            System.out.println("Grade not available yet");
        }
    }

    protected void addCourse(Professor professor, UniversityMember um){
        checkAccess(um);
        classes.put(professor, null);

    }
    protected Double getGPA(){
        checkAccess(this);
        Double gpa = 0.0;
        for(Integer grade : classes.values()){
            gpa += grade;
        }
        gpa /= classes.size();
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

    protected long getBanner(UniversityMember um){
        checkAccess(um);
        return this.bannerID;
    }
    //only professor can give the student an addition to his preRecs
    public void finishedPreRec(Professor professor){
        if(professor.isSignedIn()) {
            preRequisites++;
        }
    }
    protected int getPreRequisites(UniversityMember um){
        checkAccess(um);
        return preRequisites;
    }
    protected void removeCourse(Professor professor){
        checkAccess(this);
        classes.remove(professor);
        classes.remove(professor);
    }

    protected HashMap<Professor, Integer> getAllGrades(UniversityMember um){
        checkAccess(um);
        HashMap<Professor, Integer> end = new HashMap<>();
        end.putAll(classes);
        return end;
    }
    protected HashSet getAllClasses(UniversityMember um){
        checkAccess(um);
        HashSet<Professor> end = new HashSet<>();
        end.addAll(classes.keySet());
        return end;
    }
    protected Integer getGradeOfCourse(Professor professor, UniversityMember um){
        checkAccess(um);
        try{
            return classes.get(professor);
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