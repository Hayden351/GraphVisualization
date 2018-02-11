package DrawGraph;

/**
 *
 * @author Hayden
 */

public class TestingGround 
{
    

    private static int opt = 0;
    enum Option
    {
        DIRECTED,
        SIMPLE,
        R;
        private int code;
        private Option()
        {
            this.code = 1 << opt++;
        }
    }
    public static void main(String[] args)
    {
       System.out.printf("%s\n", Option.R);
       
//        for (int i = 0; i < 10; i++)
//        {
//            for (int j = 0; j <= i; j++)
//            {
//                System.out.printf("%6s ", permutation(i,j));
//            }
//            System.out.println();
//        }
    }
    public static int factorial(int n)
    {
        int product = 1;
        for (int i = 2; i <= n; i++)
        {
            product *= i;
        }
        return product;
    }
    
    public static int permutation(int n, int k)
    {
        return factorial(n) / factorial(n - k);
    }
}
