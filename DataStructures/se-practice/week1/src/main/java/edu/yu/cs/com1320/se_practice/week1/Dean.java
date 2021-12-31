package edu.yu.cs.com1320.se_practice.week1;

import edu.yu.cs.com1320.se_practice.UniversityMember;

import java.util.*;

public class Dean extends UniversityMember {

    private HashMap<Professor, String> classes;
    private HashMap<Student, Double> studentGPA;
    private String school;
    private boolean signedIn = false;
    private String pin;
    private final Integer rank;

    public Dean(String school, String pin){
        this.school = school;
        this.pin = pin;
        this.rank = 3;
    }
    public Integer getRank(){
        return this.rank;
    }

    public HashMap<Professor,String> getClasses(){
        HashMap<Professor, String> end = new HashMap<>();
        end.putAll(classes);
        return end;
    }

    public void hireProfessors(HashMap<Professor, String> courses, UniversityMember um){
        checkAccess(um);
        classes.putAll(courses);
    }

    public void acceptStudent(Student student, UniversityMember um){
        checkAccess(um);
        studentGPA.put(student, null);

    }
    public boolean isSignedIn(){
        return this.signedIn;
    }
    public void setStudentGPA(Student student){
        studentGPA.put(student, student.getGPA());
    }

    public boolean signIn(String password){
        if(password.equals(pin)){
            signedIn = true;
            return true;
        }
        System.out.println("Wrong password, try again;");
        return false;
    }
    private void checkAccess(UniversityMember um) {
        if(um.equals(this) && !signedIn){
            throw new IllegalStateException("Please sign in with signIn()");

        }
        if(um.getRank() < this.rank || !um.isSignedIn()){
            System.out.println(um.getRank() + " = " + um.isSignedIn());
            throw new IllegalStateException("You cannot access this information");
        }

    }
}