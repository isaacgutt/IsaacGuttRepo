package edu.yu.cs.com1320.se_practice.week2;

import edu.yu.cs.com1320.se_practice.UniversityMember;

import java.util.*;

public class Dean extends UniversityMember {

    private HashSet<Professor> classes;
    private HashMap<Student, Double> studentGPA;
    private String school;
    private boolean signedIn = false;
    private String pin;
    private final Integer rank;

    public Dean(String school, String pin){
        this.school = school;
        this.pin = pin;
        this.rank = 3;
        studentGPA = new HashMap<>();
    }
    public String getSchool(){
        return school;
    }
    public Integer getRank(){
        return this.rank;
    }

    public HashSet<Professor> getClasses(){
        HashSet<Professor> end = new HashSet<>();
        end.addAll(classes);
        return end;
    }

    public void hireProfessors(HashSet<Professor> courses){
        this.classes = courses;
    }

    public boolean acceptStudent(Student student, UniversityMember um){
        checkAccess(um);
        if(!studentGPA.containsKey(student)) {
            studentGPA.put(student, null);
            return true;
        }
        return false;
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
            throw new IllegalStateException("You cannot access this information");
        }

    }
}