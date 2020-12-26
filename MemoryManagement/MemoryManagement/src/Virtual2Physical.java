import java.util.Vector;

public class Virtual2Physical {
    public static int pageNum ( long memaddr , int numpages , long block )
    {
        long high = 0;
        long low = 0;

        for (int i = 0; i <= numpages; i++) {
            low  = block * i;
            high = block * ( i + 1 );
            if ( low <= memaddr && memaddr < high )
                return i;
        }
        return -1;
    }
}