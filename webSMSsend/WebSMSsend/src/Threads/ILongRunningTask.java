/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Threads;

/**
 * This interface encapsulates a long-running task that can be
 * monitored by a {@link ControllerThread} and cancelled.
 *
 * Loosely based on NetBeans' CancellableTask on which it does NOT
 * rely (to avoid code dependencies and additional JAR inclusions)
 *
 * taken from http://codetrips.blogspot.com/2007/04/multi-threading-mon-amour.html
 * thanks to Marco Massenzio for sharing
 * @author Marco Massenzio
 */
public interface ILongRunningTask {

    /**
     * Stops the run method at the earliest possible opportunity
     * @return
     *      true if the task was successfully cancelled, false otherwise
     */
    public boolean cancel();


    /**
     * this long-running task will use this synck lock when notifying
     * the controlling thread of any significant event
     * (eg stopped, done, etc.)
     * However, this will <strong>not</strong> be used when an abnormal
     * termination condition happens, that will be instead indicated by
     * the {@link #run()} method throwing an exception and returning
     */
//    public void setLock(Object lock);

    /** to signal the task was stopped by the user or other external event */
    public boolean isStopped();

    /**
     * Returns true if the task has completed normally, without being
     * stopped and/or errors.
     */
    public boolean isDone();

    /**
     * Optional method indicating progess as a percentage value.
     * If not implemented returns -1
     * The value returned should be assumed as indicative only and just as
     * a hint of amount of work completed.
     *
     * @return
     *    an int value between 0 (not started) and 100 (finished)
     *    -1 if not implemented
     */
    public int progress();

    /**
     * the long-running task runs here, the optional
     * observer is used by both the controller and the task to
     * communicate back to the thread/object that started it all.
     * It should NEVER be null, although it MAY not be used.
     *
     * @param observer
     *      an observing object/thread that is invoked during
     *  execution of events, to log messages, and at completion.
     */
    public void execute(IObserver observer) throws TaskException;
}

