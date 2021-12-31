package edu.yu.introtoalgs;

import java.util.Objects;

public class IntersectRectangles {

  /**
   * This constant represents the fact that two rectangles don't intersect.
   *
   * @warn you may not modify this constant in any way
   * @see #
   */
  public final static Rectangle NO_INTERSECTION =
          new Rectangle(0, 0, -1, -1);

  /**
   * An immutable class that represents a 2D Rectangle.
   *
   * @warn you may not modify the instance variables in any way, you are
   * encouraged to add to the current set of variables and methods as you feel
   * necessary.
   */
  public static class Rectangle {
    // safe to make instance variables public because they are final, now no
    // need to make getters
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    @Override
    public String toString(){
      return "Rectangle: [x=" + x + ", y=" + y + ", width="+ width + ", height=" + height + "]";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Rectangle rectangle = (Rectangle) o;
      return x == rectangle.x && y == rectangle.y && width == rectangle.width && height == rectangle.height;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, width, height);
    }

    /**
     * Constructor: see the requirements doc for the precise semantics.
     *
     * @warn you may not modify the currently defined semantics in any way, you
     * may add more code if you so choose.
     */
    public Rectangle
    (final int x, final int y, final int width, final int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

  }

  /**
   * If the two rectangles intersect, returns the rectangle formed by their
   * intersection; otherwise, returns the NO_INTERSECTION Rectangle constant.
   *
   * @param r1 one rectangle
   * @param r2 the other rectangle
   * @param // rectangle representing the intersection of the input parameters
   *           if they intersect, NO_INTERSECTION otherwise.  See the requirements doc
   *           for precise definition of "rectangle intersection"
   */
  public static Rectangle intersect(final Rectangle r1, final Rectangle r2) {
    if(r1 == null || r2 == null){
      throw new IllegalArgumentException();
    }

    //if either of the triangles full width doesn't reach the other's x coordinate
    // or their full height doesn't reach the other one's y coordinate then they do not intersect
    if ((r1.x + r1.width < r2.x || r1.y + r1.height < r2.y) || (r2.x + r2.width < r1.x || r2.y + r2.height < r1.y))
      return NO_INTERSECTION;

    //They intersect at each's highest coordinate points
    int x = Math.max(r1.x, r2.x);
    int y = Math.max(r1.y, r2.y);

    //The overlap ends by the lowest full height and lowest full width of each, so that minus the
    //coordinate points is how far they move from the intersections coordinate points
    int width = Math.min(r1.x + r1.width, r2.x + r2.width) - x;
    int height = Math.min(r1.y + r1.height, r2.y + r2.height) - y;
    return new Rectangle(x, y, width, height);


  }

}




 // class