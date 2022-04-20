import static java.lang.Math.*;

public class Helper {

    public static double exponentialDistribution(double lambda){
        double j1 = Math.random();
        if(j1 == 0){ j1 = j1 + 0.001;}
        double j2 = -Math.log(j1)/lambda;
        return j2;
    }

    public static int poissonDistribution(double lambda){
        double k, L, j1, j2, j3;
        int p;
        j1 = Math.random();
        k = 0;
        L = Math.exp(-lambda);
        j3 = 0;
        do{
            j2 = L * (Math.pow(lambda, k));
            p = 1;
            for(int i6 = 0; i6 <= k; i6++){
                if(i6 == 0){
                    p = 1;
                } else p *= i6;
            }
            j2 /= p;
            j3 += j2;
            k++;
        } while (j1 >= j3);
        return (int) (k-1);
    }

    public static int normalDistribution(double mean, double stdev){
        double v1, v2, t;
        int x;
        do{
            v1 = Math.random()*2;
            v1 -= 1;
            v2 = Math.random()*2;
            v2 -= 1;
            t = v1*v1 + v2*v2;
        } while (t >= 1 || t ==0);
        double multiplier = Math.sqrt(-2*log(t)/t);
        x = (int) (v1 * multiplier * stdev + mean);
        return x;  //Waarom moet dit een int returnen?
    }
    public static int bernoulliDistribution(double prob){
        double j1 = Math.random();
        if (j1 < prob)
            return 0;
        else
            return 1;
    }

    public static int uniformDistribution(double a, double b){
        double j1 = Math.random();
        return (int) (a + (b-a) * j1);
    }

    public static int Triangular_distribution(int a, int b, int c){
        double mean, stdev,x, L;
        mean = (a+b+c)/3;
        stdev = (Math.pow(a,2)+Math.pow(b,2)+Math.pow(c,2)-a*b-a*c-b*c)/18;
        stdev = Math.sqrt(stdev);
        double j1 = Math.random();
        x = a;

        do{
            if (x <= b)
                L = Math.pow((x-a),2)/((c-a)*(b-a));
            else
                L = 1-(Math.pow(c-x,2)/((c-a)*(c-b)));
            x++;
        } while (j1 >= L);

        return (int)x-1;
    }
}
