package util.statistics;


public final class Count extends StatisticObject {
    /**
     * The minimum of all values so far
     */
    private long min;

    /**
     * The maximum of all values so far
     */
    private long max;

    /** The current counter value */
    private long value;


    public Count(String name) {
        super(name);
        reset();
    }

    public Count(String name, long value){
        this(name);
        update(value);
    }


    /**
     * Returns the maximum value observed so far.
     *
     * @return long : The maximum value observed so far.
     */
    public long getMaximum() {
        return this.max;
    }

    /**
     * Returns the minimum value observed so far.
     *
     * @return long : The minimum value observed so far.
     */
    public long getMinimum() {
        return this.min;
    }

    /**
     * Resets this Count object by resetting (nearly) all variables to zero. If
     * the flag <code>isResetResistant</code> is set to <code>true</code>
     * the counter value will NOT be changed.
     */
    @Override
    public void reset() {
        super.reset();

        this.min = Long.MAX_VALUE;

        this.max = 0;


        this.value = 0;
    }

    /**
     * Increments the counter of this <code>Count</code> object by the value
     * given in the parameter n.
     *
     * @param n
     *            long : The number that will be added to the counter of this
     *            <code>Count</code> object.
     */
    public void update(long n) {
        incrementObservations();

        this.value += n; // update current value

        if (this.value < min) {
                min = this.value; // update min
        }

        if (this.value > max) {
                max = this.value; // update max
        }
    }

    public void inc(){
        update(1);
    }
	
    /**
     * Returns the current counter value.
     *
     * @return long : the current counter value.
     */
    public long getValue() {
        return this.value;
    }

    @Override
    public String toString(){
        return "["+getName()+"="+getValue()+"]";
    }
} 
