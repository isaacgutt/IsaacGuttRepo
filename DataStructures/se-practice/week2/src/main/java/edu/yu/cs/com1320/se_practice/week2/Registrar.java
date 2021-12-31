package edu.yu.cs.com1320.se_practice.week2;


import edu.yu.cs.com1320.se_practice.UniversityMember;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Registrar extends UniversityMember{

    private HashSet<Dean> schools;
    private final Integer rank;
    private boolean signedIn;

    public Registrar(HashSet<Dean> schools){
        this.schools = schools;
        rank = 4;
        signedIn = true;
    }
    public boolean applyAndAccept(Student student, Dean dean){
        return dean.acceptStudent(student, dean);
    }


    public HashSet<Dean> getSchools() {
        HashSet<Dean> end = new HashSet<>();
        end.addAll(schools);
        return end;
    }

    public Integer getPrerequisites(Professor professor){
        return professor.getPreRequisites();
    }

    public Integer getGrade(Professor professor, Student student){
        return professor.getGrade(student);
    }

    public HashSet<Professor> getCoursesInSchool(Dean school){
        return school.getClasses();
    }

    public boolean addCourse(Professor professor, Student student){
        return professor.addStudent(student, this);
    }

    public void removeCourse(Professor professor, Student student){
        professor.removeStudent(student);
        student.removeCourse(professor);
    }
    public Double getGPA(Student student){
        return student.getGPA();
    }
    public HashMap<Professor, Integer> getAllGrades(Student student){
        return student.getAllGrades(student);
    }
    public HashSet<Professor> getCourses(Student student){
        return student.getAllClasses(student);
    }
    @Override
    public Integer getRank(){
        return this.rank;
    }
    @Override
    public boolean isSignedIn(){
        return signedIn;
    }



}
