package edu.yu.introtoalgs;

/** Specifies the interface for generating a sequence of transportation states
 * that moves the radioactives from src to dest per the requirements doc.
 *
 * @author Avraham Leff
 */

import java.util.*;

public class TransportRadioactives {

  /** Computes a sequence of "transport radioactives" movements between the src
   * and the dest such that all of the initial methium and initial cathium are
   * transported safely from the src to the dest.  Each movement must respect
   * the constraints specified in the requirements doc.
   *
   * @param initialMithium initial amount of mithium (in kg) at the src
   * @param initialCathium initial amount of cathium (in kg) at the src
   * @return List of "transport radioactives" movements between the src and the
   * dest (if such a sequence can be computed), or an empty List if no such
   * sequence can be computed under the specified constraints.
   */
  public static HashSet<TransportationStateImpl> marked = new HashSet<>(); // Has dfs() been called for this vertex?
    public static HashMap<TransportationStateImpl, TransportationStateImpl> edgeTo = new HashMap<>(); // last vertex on known path to this vertex
    public static Queue<TransportationStateImpl> queue = new LinkedList<TransportationStateImpl>();

  public static List<TransportationState> transportIt(final int initialMithium, final int initialCathium) {
      if(initialCathium <= 0 || initialMithium <= 0) throw new IllegalArgumentException();
      if(initialCathium > initialMithium) return  new ArrayList<TransportationState>();

      TransportationStateImpl start = new TransportationStateImpl(initialMithium,initialCathium,TransportationStateImpl.Location.SRC,initialMithium,initialCathium);
      TransportationStateImpl end = new TransportationStateImpl(0,0,TransportationStateImpl.Location.DEST,initialMithium,initialCathium);
      marked.add(start);
      queue.add(start);
      while (!queue.isEmpty()) {
          if (marked.contains(end))
              break;
          TransportationStateImpl s = queue.remove();
          possibilities(s);
      }
      if (!hasPathTo(end)) return new ArrayList<>();
      LinkedList<TransportationState> path = new LinkedList<TransportationState>();
      TransportationStateImpl x = end;

      for (; x != start; x = edgeTo.get(x))
          path.addFirst(x);
      path.addFirst(start);
      return path;

  }

    public static boolean hasPathTo(TransportationStateImpl v)
    { return marked.contains(v); }


    public static void possibilities(TransportationStateImpl v){
        //Where is the truck, how much chemicals on each side and in truck
        //What are the options, will their be an explosion or not
        TransportationStateImpl.Location location = v.other();

        //Load two mith, load two cath, one of each, one mith, one cath
        int[] b = {2,0,0,2,1,1,1,0,0,1};
        int[] a = location == TransportationState.Location.DEST? new int[]{-2,0,0,-2,-1,-1,-1,0,0,-1} : new int[]{2,0,0,2,1,1,1,0,0,1};
        TransportationStateImpl x;

        for(int z = 0; z < a.length; z+=2){

            x = new TransportationStateImpl(v.getMithiumSrc()+a[z], v.getCathiumSrc() + a[z+1], location, v.getTotalMithium(), v.getTotalCathium());
            if(x.legal() && notMarked(x)) {
                //x.setTruck(b[z],b[z+1]);
                addToPath(x,v);
            }
        }
    }

    public static void addToPath(TransportationStateImpl to, TransportationStateImpl from) {
        edgeTo.put(to, from); // save last edge on a shortest path,
        marked.add(to); // mark it because path is known,
        queue.add(to);
    }

    public static boolean notMarked(TransportationStateImpl t){
        return !marked.contains(t);
    }
  } // transportIt

// TransportRadioactives

















