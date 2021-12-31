package edu.yu.introtoalgs;


public class EstimateSecretAlgorithms2 {


    public double timeAlg(int n, BigOMeasurable measurable){
        measurable.setup(n);
        Stopwatch timer = new Stopwatch(measurable instanceof SecretAlgorithm4);
        measurable.execute();
        return timer.elapsedTime();
    }






    public class Stopwatch
    {
        private final long start;
        private final boolean nano;
        public Stopwatch(Boolean nano)
        {
            start = nano? System.nanoTime() : System.currentTimeMillis();
            this.nano = nano;
        }
        public double elapsedTime()
        {
            long now = nano? System.nanoTime() : System.currentTimeMillis();
            return (now - start);
        }


    }
}
