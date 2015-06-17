package xxl.util.statistics;

/**
 * This code is based on classes imported from DesmoJ project.
 */

public abstract class StatisticObject {    
    /**
     * The number of observations that this reportable can report about
     */
    private long observations;

    /**
     * stores the last time that this reportable object was reset.
     */
    private long lastReset;


    final private String name;

    public StatisticObject(String name) {
        observations = 0; // reset observations counter
        this.name = name;
    }

    /**
     * Returns the number of observations made by the statistic object.
     *
     * @return long : the number of observations made by the statistic object.
     */
    public long getObservations() {
        return observations;
    }

    /**
     * Increments the number of observations made.
     */
    protected void incrementObservations() {
        observations++;
    }


    /**
     * Resets the counter for observations. The point of
     * simulation time this method was called will be stored and can be
     * retrieved using method <code>resetAt()</code>.
     */
    public void reset() {
        observations = 0; // reset observations
        lastReset = System.currentTimeMillis(); // register the reset time
    }

    /**
     * Shows the point in time when the last reset of this reportable
     * was made.
     *
     * @return SimTime : The point when of the last reset was made.
     */
    public long resetAt() {
        return lastReset;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
	
} 
