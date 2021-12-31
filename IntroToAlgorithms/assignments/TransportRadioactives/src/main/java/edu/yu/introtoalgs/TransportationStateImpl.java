package edu.yu.introtoalgs;

/** Implements the TransportationState interface.  
 *
 *
 * Students may ONLY use the specified constructor, and may (perhaps even
 * encouraged to) add as many other methods as they choose.
 *
 * @author Avraham Leff
 */

import java.util.Objects;

import static edu.yu.introtoalgs.TransportationState.Location.*;

public class TransportationStateImpl implements TransportationState { 

  /** Constructor:
   *
   * @param mithiumAtSrc amount of mithium at the src location, must be >= 0
   * @param cathiumAtSrc amount of cathium at the src location, must be >= 0
   * @param truckLocation location of the truck, must not be null
   * @param totalMithium sum of mithium amounts at src + dest, must be > 0
   * @param totalCathium sum of cathium amounts at src + dest, must be > 0
   *
   * @Students: you may NOT USE ANY OTHER CONSTRUCTOR SIG
   */
  private final int mithium1;
  private final int cathium1;
  private final Location location;
  private final int totalMithium;
  private final int totalCathium;
  private int mithium2 = 0;
  private int cathium2 = 0;

  //private int truckMithium = 0;
  //private int truckCathium = 0;

  /*
  public void setTruck(int m, int c){
    truckMithium = m;
    truckCathium = c;
  }
  */


  public TransportationStateImpl(final int mithiumAtSrc,
                                 final int cathiumAtSrc,
                                 final Location truckLocation,
                                 final int totalMithium,
                                 final int totalCathium)
  {
    this.mithium1 = mithiumAtSrc;
    this.cathium1 = cathiumAtSrc;
    this.location = truckLocation;
    this.totalMithium = totalMithium;
    this.totalCathium = totalCathium;
    this.mithium2 = totalMithium - mithium1;
    this.cathium2 = totalCathium - cathium1;
  } // constructor

  @Override
  public int getMithiumSrc() { return mithium1; }

  @Override
  public int getCathiumSrc() { return cathium1; }
    
  @Override
  public int getMithiumDest() { return mithium2; }
    
  @Override
  public int getCathiumDest() { return cathium2; }
    
  @Override
  public Location truckLocation() { return location; }

  @Override
  public int getTotalMithium() { return totalMithium; }

  @Override
  public int getTotalCathium() { return totalCathium; }

  @Override
  public String toString(){
    return mithium1 + " kg of mithium and " + cathium1 + " kg of cathium at src.\n"
            + getMithiumDest() + " kg of mithium and " + getCathiumDest() +  " kg of cathium at dest.\n"
            + "The truck is parked at the " + location + "\n";

   /*if(location == DEST) return "|M: " + mithium1 + ", C: " + cathium1 + "|------------------------------------TRUCK(M: " + truckMithium + ", C: " + truckCathium + ")|M:" + (mithium2 - truckMithium) + ", C: " + (cathium2 - truckCathium) +"|\n";
    else return "|M: " + (mithium1 - truckMithium) + ", C: " + (cathium1 - truckCathium) + "|TRUCK(M: " + truckMithium + ", C: " + truckCathium + ")" + "------------------------------------ |M:" + mithium2 + ", C: " + cathium2 +"|\n";
*/
  }

  @Override
  public int hashCode() {
    return Objects.hash(mithium1, mithium2, location, totalMithium, totalCathium, mithium2, cathium2);
  }

  @Override
  public boolean equals(Object o){
    if(!(o instanceof TransportationState)) return false;
    TransportationState x = (TransportationState) o;
    return this.mithium1 == x.getMithiumSrc() && this.mithium2 == x.getMithiumDest() && location == x.truckLocation()
            && totalCathium == x.getTotalCathium() && totalMithium == x.getTotalMithium() && cathium1 == x.getCathiumSrc() && cathium2 == x.getCathiumDest();
  }

  public boolean legal(){
    //no explosions and no negative numbers
    if(mithium1 < 0 || mithium2 < 0 || cathium2 < 0 || cathium1 < 0) return false;
    if(mithium1 > 0 && cathium1 > mithium1) return false;
    return mithium2 <= 0 || cathium2 <= mithium2;
  }

  public Location other(){
    if(location == SRC) return DEST;
    else return SRC;
  }


}   // class
