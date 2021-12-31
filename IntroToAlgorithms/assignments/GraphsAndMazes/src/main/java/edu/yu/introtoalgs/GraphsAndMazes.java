package edu.yu.introtoalgs;

import java.util.*;

public class GraphsAndMazes {

  /** A immutable coordinate in 2D space.
   *
   * Students must NOT modify the constructor (or its semantics) in any way,
   * but can ADD whatever they choose.
   */
  public static class Coordinate {
    public final int x, y;

    
    /** Constructor, defines an immutable coordinate in 2D space.
     *
     * @param x specifies x coordinate
     * @param y specifies x coordinate
     */
    public Coordinate(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if(!(o instanceof Coordinate))return false;
      Coordinate i = (Coordinate) o;
      return i.x == this.x && i.y == this.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public String toString(){
      return "(" + this.x + ", " + this.y + ")";
    }


    /** Add any methods, instance variables, static variables that you choose
     */
  } // Coordinate class


  /** Given a maze (specified by a 2D integer array, and start and end
   * Coordinate instances), return a path (beginning with the start
   * coordinate, and terminating wih the end coordinate), that legally
   * traverses the maze from the start to end coordinates.  If no such
   * path exists, returns an empty list.  The path need need not be a
   * "shortest path".
   *
   * @param maze 2D int array whose "0" entries are interpreted as
   * "coordinates that can be navigated to in a maze traversal (can be
   * part of a maze path)" and "1" entries are interpreted as
   * "coordinates that cannot be navigated to (part of a maze wall)".
   * @param start maze navigation must begin here, must have a value
   * of "0"
   * @param end maze navigation must terminate here, must have a value
   * of "0"
   * @return a path, beginning with the start coordinate, terminating
   * with the end coordinate, and intervening elements represent a
   * legal navigation from maze start to maze end.  If no such path
   * exists, returns an empty list.  A legal navigation may only
   * traverse maze coordinates, may not contain coordinates whose
   * value is "1", may only traverse from a coordinate to one of its
   * immediate neighbors using one of the standard four compass
   * directions (no diagonal movement allowed).  A legal path may not
   * contain a cycle.  It is legal for a path to contain only the
   * start coordinate, if the start coordinate is equal to the end
   * coordinate.
   */

  public static HashSet<Coordinate> marked = new HashSet<>(); // Has dfs() been called for this vertex?
  public static HashMap<Coordinate, Coordinate> edgeTo = new HashMap<>(); // last vertex on known path to this vertex
  public static Queue<Coordinate> queue = new LinkedList<Coordinate>();

  public static List<Coordinate> searchMaze(final int[][] maze, final Coordinate start, final Coordinate end) {
    if(maze == null || start == null || end == null) throw new IllegalArgumentException();
    if((start.x < 0 || start.x > maze.length - 1) || (end.x < 0 || end.x > maze.length - 1) || (start.y < 0 || start.y > maze[0].length - 1) || (end.y < 0 || end.y > maze[0].length - 1)) throw new IllegalArgumentException();
    if(maze[start.x][start.y] == 1 || maze[end.x][end.y] == 1) throw new IllegalArgumentException();
    if(start.equals(end)) return Collections.singletonList(start);

    marked.add(start);
    queue.add(start);
    while (!queue.isEmpty()) {
      if (marked.contains(end))
        break;
      Coordinate s = queue.remove();
      openEdges(s, maze);

    }
    if (!hasPathTo(end)) return new ArrayList<>();
    LinkedList<Coordinate> path = new LinkedList<Coordinate>();
    Coordinate x = end;
    for (; x != start; x = edgeTo.get(x))
      path.addFirst(x);
    path.addFirst(start);
    return path;

}

    public static boolean hasPathTo(Coordinate v)
    { return marked.contains(v); }


    public static void openEdges(Coordinate v, int[][] maze){
    if(v.x-1 >= 0 && maze[v.x-1][v.y] == 0 && notMarked(v.x - 1, v.y))
        addToPath(new Coordinate(v.x -1, v.y), v);

      if(v.y-1 >= 0 && maze[v.x][v.y-1] == 0 && notMarked(v.x, v.y - 1))
        addToPath(new Coordinate(v.x, v.y-1), v);

      if(v.x+1 <= maze.length-1 && maze[v.x+1][v.y] == 0 && notMarked(v.x + 1, v.y))
      addToPath(new Coordinate(v.x +1, v.y),v);

      if(v.y+1 <= maze[0].length-1 && maze[v.x][v.y+1] == 0 && notMarked(v.x, v.y+1))
      addToPath(new Coordinate(v.x, v.y+1), v);
    }

    public static void addToPath(Coordinate to, Coordinate from) {
      edgeTo.put(to, from); // save last edge on a shortest path,
      marked.add(to); // mark it because path is known,
      queue.add(to);
    }

    public static boolean notMarked(int x, int y){
     return !marked.contains(new Coordinate(x,y));
    }

  /** minimal main() demonstrates use of APIs
   */
  public static void main (final String[] args) {
    final int[][] exampleMaze = {
      {0, 0, 0},
      {0, 1, 1},
      {0, 1, 0}
    };

    final Coordinate start = new Coordinate(2, 0);
    final Coordinate end = new Coordinate(0, 2);
    final List<Coordinate> path = searchMaze(exampleMaze, start, end);
    System.out.println("path="+path);
  }

}