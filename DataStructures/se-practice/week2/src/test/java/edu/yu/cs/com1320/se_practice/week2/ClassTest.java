package edu.yu.cs.com1320.se_practice.week2;
import edu.yu.cs.com1320.se_practice.week2.Professor;
import edu.yu.cs.com1320.se_practice.week2.Student;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ClassTest {

    private Dean yc;

    private Professor intro;
    private Professor english;
    private Professor calculus;
    private Professor dataStructures;

    private Student patrick;

    HashSet<Dean> schools = new HashSet<>();

    HashSet<Professor> professors = new HashSet<>();

    @Before
    public void init() throws Exception {
        this.yc = new Dean("Yeshiva College", "arts");

        this.intro = new Professor("teacher1", "Computer Science", "hashcode", 0);
        this.english = new Professor("teacher2", "English", "politics", 0);
        this.calculus = new Professor("teacher3", "Calculus", "derivative", 0);
        this.dataStructures = new Professor("teacher4", "Data Structures", "hashtable", 1);

        this.patrick = new Student("Patrick", "arm");

        professors.add(this.intro);
        professors.add(this.english);
        professors.add(this.calculus);
        professors.add(this.dataStructures);
        yc.hireProfessors(professors);
    }

    @Test
    public void schoolYear(){
        Registrar registrar = new Registrar(schools);
        //Student looks into schools and professors
        for(Dean dean : registrar.getSchools()){
            System.out.println(dean.getSchool());
        }
        for(Professor p : registrar.getCoursesInSchool(yc)){
            System.out.println(p.getName() + " Course: " + p.getCourse());
        }
        //Dean not signed in yet, no registration yet
        assertThrows(IllegalStateException.class, () -> {
            registrar.applyAndAccept(patrick, yc);
        });
        this.yc.signIn("arts");
        registrar.applyAndAccept(patrick, yc);
        //Student not signed in yet
        assertThrows(IllegalStateException.class, () -> {
            registrar.addCourse(calculus, patrick);
        });
        //sign in wrong, still won't work
        patrick.signIn("pat");
        assertThrows(IllegalStateException.class, () -> {
            registrar.addCourse(calculus, patrick);
        });

        patrick.signIn("arm");
        registrar.addCourse(calculus, patrick);
        registrar.addCourse(intro, patrick);
        calculus.signIn("derivative");
        calculus.setGrade(patrick, 60,calculus);
        calculus.signOut();
        registrar.addCourse(english, patrick);

        //patrick wants to see what grade he got
        int grade = registrar.getGrade(calculus, patrick);
        assertEquals(grade, 60);
        //Patrick tries to change his grade
        assertThrows(IllegalStateException.class, () -> {
            calculus.setGrade(patrick, 100, patrick);
        });
        //Did not work
        assertEquals(registrar.getGrade(calculus, patrick), (Integer) 60);
        //Patrick failed so he leaves
        registrar.removeCourse(calculus, patrick);
        assertEquals(registrar.getCourses(patrick).size(), 2);

        //Set Patrick's grade to get GPA
        intro.signIn("hashcode");
        intro.setGrade(patrick, 88, intro);
        intro.signOut();
        english.signIn("politics");
        english.setGrade(patrick, 94, english);
        english.signOut();
        patrick.openGrades(english, registrar);
        patrick.openGrades(intro, registrar);
        yc.setStudentGPA(patrick);
        System.out.println("Your GPA is " + registrar.getGPA(patrick));

        //Patrick tries to get into Data Structures but he hasn't finished intro
        assertFalse(registrar.addCourse(dataStructures, patrick));

        //Patrick finds out the problem
        assertEquals(registrar.getPrerequisites(dataStructures), (Integer)1);


        english.signIn("politics");
        intro.signIn("hashcode");
        //end of semester
        HashMap<Professor, Integer> grades = registrar.getAllGrades(patrick);
        for(Professor p : grades.keySet()){
            System.out.println(p.getCourse() + " : " + grades.get(p));
            p.completedCourse(patrick);
        }
        intro.signOut();
        english.signOut();

        //check that patrick's classes are done and he can now sign up for Data Structures
        assertEquals(registrar.getCourses(patrick).size(), 0);
        assertTrue(registrar.addCourse(dataStructures, patrick));
        

    }


}