package edu.yu.cs.com1320.se_practice.week1;

import edu.yu.cs.com1320.se_practice.UniversityMember;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Registrar extends UniversityMember {

    private HashSet<Dean> schools;
    private final Integer rank;

    public Registrar(HashSet<Dean> schools){
        this.schools = schools;
        rank = 4;
    }
    public Integer getRank(){
        return this.rank;
    }

    public HashSet<Dean> getSchools() {
        return (HashSet<Dean>) Collections.unmodifiableSet(this.schools);
    }

    public Integer getPrerequisites(Professor professor){
        return professor.getPreRequisites();
    }
    public Integer getGrade(Professor professor, Student student){
        return professor.getGrade(student);
    }

    public HashMap<Professor,String> getCoursesInSchool(Dean school){
        return school.getClasses();
    }
    public boolean addCourse(Professor professor, Student student){
        return professor.addStudent(student);
    }

    public void removeCourse(Professor professor, Student student){
        professor.removeStudent(student);
    }

}
