import edu.yu.da.MultiMerge;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;

public class MultiMergeTest {
    @Test
    public void test(){
        MultiMerge m = new MultiMerge();
        int[][] merged = new int[6][6];
        merged[0] = new int[]{1, 3, 5,7,11,123};
        merged[1] = new int[]{2, 3, 4,34,50,75};
        merged[2] = new int[]{3, 5, 8,12,39,80};
        merged[3] = new int[]{0, 7, 12,123,124,11111};
        merged[4] = new int[]{0, 1,5,88,100,154};
        merged[5] = new int[]{10, 71, 122,123,124,11111};
        System.out.println(Arrays.toString(m.merge(merged)));
        System.out.println(m.getNCombinedMerges());
    }
    @Test
    public void testw(){
        int z = 3;
        int n = 7;
        final int [ ] [ ] arrays = new int [z][n] ;
        arrays[0] = new int[]{1, 3, 5,7,11,123};
        arrays[1] = new int[]{2, 3, 4,34,50,75};
        arrays[2] = new int[]{3, 5, 8,12,39,80};

// code to initialize and sort the individual arrays[i]
// [snip]
        final MultiMerge multiMerge = new MultiMerge ( ) ;
        final int [] result = multiMerge.merge (arrays);
        System.out.println(Arrays.toString(result));
    }

    @Test
    public void test2(){
        MultiMerge m = new MultiMerge();
        int[][] merged = new int[6][6];
        merged[0] = new int[]{1, 3, 5,7,11,123};
        merged[1] = new int[]{2, 3, 4,34,50,75};
        merged[2] = new int[]{3, 5, 8,12,39,80};
        merged[3] = new int[]{0, 7, 12,123,124,11111};
        merged[4] = new int[]{0, 1,5,88,100,154};
        merged[5] = new int[]{10, 71, 122,123,124,11111};
        m.iterativeMerge(merged, merged[0],1 );
    }

    @Test
    public void whichMergesMore(){
        MultiMerge m = new MultiMerge();
        MultiMerge n = new MultiMerge();
        int[][] merged = new int[24][6];
        merged[0] = new int[]{1, 3, 5,7,11,123};
        merged[1] = new int[]{2, 3, 4,34,50,75};
        merged[2] = new int[]{3, 5, 8,12,39,80};
        merged[3] = new int[]{0, 7, 12,123,124,11111};
        merged[4] = new int[]{0, 1,5,88,100,154};
        merged[5] = new int[]{10, 71, 122,123,124,11111};
        merged[6] = new int[]{1, 3, 5,7,11,123};
        merged[7] = new int[]{2, 3, 4,34,50,75};
        merged[8] = new int[]{1, 3, 5,7,11,123};
        merged[9] = new int[]{2, 3, 4,34,50,75};
        merged[10] = new int[]{3, 5, 8,12,39,80};
        merged[11] = new int[]{0, 7, 12,123,124,11111};
        merged[12] = new int[]{0, 1,5,88,100,154};
        merged[13] = new int[]{10, 71, 122,123,124,11111};
        merged[14] = new int[]{1, 3, 5,7,11,123};
        merged[15] = new int[]{2, 3, 4,34,50,75};
        merged[16] = new int[]{1, 3, 5,7,11,123};
        merged[17] = new int[]{2, 3, 4,34,50,75};
        merged[18] = new int[]{3, 5, 8,12,39,80};
        merged[19] = new int[]{0, 7, 12,123,124,11111};
        merged[20] = new int[]{0, 1,5,88,100,154};
        merged[21] = new int[]{10, 71, 122,123,124,11111};
        merged[22] = new int[]{1, 3, 5,7,11,123};
        merged[23] = new int[]{2, 3, 4,34,50,75};

        m.merge(merged);
        n.iterativeMerge(merged, merged[0],1 );
        System.out.printf("Divide and Conquer had %d merges and %d compares\n", m.getNCombinedMerges(), m.compares);
        System.out.printf("Iterative had %d merges and %d compares", n.getNCombinedMerges(), n.compares);
    }

    @Test
    public void test3() {
        MultiMerge m = new MultiMerge();
        int[][] merged = new int[2][6];
        merged[0] = new int[]{1, 3, 5, 7, 11, 123};
        merged[1] = new int[]{2, 3, 4, 34, 50, 75};
        System.out.println(Arrays.toString(m.merge(merged)));
        System.out.println(m.getNCombinedMerges());
    }

}