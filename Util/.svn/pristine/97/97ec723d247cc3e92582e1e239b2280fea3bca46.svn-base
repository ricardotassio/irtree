package util.statistics;

public class Tally extends StatisticObject {

    /**
     * The sum of all values so far
     */
    private double sum;

    /**
     * The sum of all squared values so far
     */
    private double sumSquare;


    /**
     * The minimum of all values so far
     */
    private double min;

    /**
     * The maximum of all values so far
     */
    private double max;

    /**
     * The last value we got from the ValueSupplier
     */
    private double lastValue;



    public Tally(String name){
        super(name);
    }

    public Tally(String name, double value){
        this(name);
        update(value);
    }
    
    /**
     * Returns the last observed value of this ValueStatistics object.
     *
     * @return double : The last value observed so far.
     */
    public double getLastValue() {
        return this.lastValue;
    }

    /**
     * Returns the maximum value observed so far.
     *
     * @return double : The maximum value observed so far.
     */
    public double getMaximum() {        
        return max;
    }

    /**
     * Returns the minimum value observed so far.
     *
     * @return double : The minimum value observed so far.
     */
    public double getMinimum() {
        return min;
    }

    /**
     * Updates with the double value
     * given as parameter.
     *
     * @param val
     *            double : The value with which this
     *            object will be updated.
     */
    public void update(double val) {
        lastValue = val; // update lastValue

        incrementObservations(); // use the method from the Reportable class

        if (getObservations() <= 1){// the first onbservation?
            min = max = lastValue; // update min and max
        }

        if (lastValue < min) {
            min = lastValue; // update min
        }

        if (lastValue > max) {
            max = lastValue; // update max
        }

        sum += val;
        sumSquare += val * val;
    }

    /**
     * Returns the mean value of all the values observed so far.
     *
     * @return double : The mean value of all the values observed so far.
     */
    public double getMean() {
        if (getObservations() == 0) {
            throw new RuntimeException("Attempt to get a mean value of '"+this.getName() +
                    "' but there is not sufficient data yet.");
        }
        return sum / (double)getObservations();
    }

    /**
     * Returns the standard deviation of all the values observed so far.
     *
     * @return double : The standard deviation of all the values observed so
     *         far.
     */
    public double getStdDev() {
        long n = getObservations();

        if (n < 2) {
                throw new RuntimeException("Attempt to get a standard deviation of "+this.getName() +
                        ", but there is not sufficient data yet.");
        }

        return Math.sqrt(Math.abs(n * sumSquare - sum * sum)
                        / (n * (n - 1)));
    }

    /**
     * Resets this Tally object by resetting all variables to 0.0 .
     */
    @Override
    public void reset() {
        super.reset(); // reset the ValueStatistics, too.
        this.min = this.max = this.lastValue = 0.0;
        this.sum = this.sumSquare = 0.0;
    }

    @Override
    public String toString(){
        return "[mean="+(this.getObservations()>1?this.getMean():"-")+",stdDev="+
                (this.getObservations()>1?this.getStdDev():"-")+
                ",min="+this.getMinimum()+",max="+this.getMaximum()+"]";
    }
} // end class Tally
