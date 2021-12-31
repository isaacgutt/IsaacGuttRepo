package edu.yu.cs.com1320.se_practice;
public class UniversityMember{
    private Integer rank;
    private boolean isSignedIn;

    //Each class has a final integer to differentiate between their level of control
    public Integer getRank(){
        return this.rank;
    }
    //each class can't access methods if they aren't yet signed in
    public boolean isSignedIn(){
        return this.isSignedIn;
    }

}