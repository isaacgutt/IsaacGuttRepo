package edu.yu.cs.com1320.se_practice.week2;

import edu.yu.cs.com1320.se_practice.UniversityMember;

import java.util.*;

public class Professor extends UniversityMember {

    private final String name;
    private final long bannerId;
    private final String pin;
    private HashMap<Student, Integer> students = new HashMap<>();
    private final String course;
    private boolean signedIn = false;
    private final int preRequisites;
    private final Integer rank;

    public Professor(String name, String course, String pin, int preRec) {
        this.name = name;
        this.course = course;
        this.pin = pin;
        preRequisites = preRec;
        bannerId = hashCode();
        rank = 2;
    }
    public Integer getRank(){
        return this.rank;
    }

    public boolean addStudent(Student student, UniversityMember um){
        if (student.getPreRequisites(um) < preRequisites) {
            return false;
        }
        students.put(student, null);
        student.addCourse(this, student);
        return true;
    }
    public void removeStudent(Student student){
        students.remove(student);
    }

    public void setGrade(Student student, int grade, UniversityMember um) {
        checkAccess(um);
        students.put(student, grade);
    }

    public Integer getGrade(Student student) {
        if (students.containsKey(student)) {
            return students.get(student);
        }
        return null;
    }

    public String getCourse() {
        return course;
    }

    public long getBannerId(UniversityMember um){
        checkAccess(um);
        return bannerId;
    }
    public void completedCourse(Student student){
        student.finishedPreRec(this);
        removeStudent(student);
        student.removeCourse(this);
    }

    public int getPreRequisites(){
        return preRequisites;
    }
    
    public void classCompleted(Student student){
        checkAccess(this);
        student.finishedPreRec(this);
    }

    public String getName() {
        return name;
    }

    public boolean isSignedIn(){
        return this.signedIn;
    }

    public boolean signIn(String password) {
        if (password.equals(pin)) {
            signedIn = true;
            return true;
        }
        System.out.println("Wrong password, try again;");
        return false;
    }
    public void signOut(){
        signedIn = false;
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