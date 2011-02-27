/*
 * taken from http://codetrips.blogspot.com/2007/04/multi-threading-mon-amour.html
 * thanks to Marco Massenzio for sharing
 */

package Threads;

/**
 * <h1> ControllerThread </h1>
 * <p>
 *  Used to control a long-running task and monitor progress as well
 *  enable early-termination, typically following user's pressing a
 *  "cancel" button of some sort
 * </p>
 *
 * taken from http://codetrips.blogspot.com/2007/04/multi-threading-mon-amour.html
 * thanks to Marco Massenzio for sharing
 * @author Marco Massenzio
 */
public class ControllerThread implements Runnable {

    private static final int THREAD_STATE_WAIT = 0x01;
    private static final int THREAD_STATE_CHECK = 0x02;
    private static final int THREAD_STATE_EXPIRED = 0x04;
    private static final int THREAD_STATE_INACTIVE = 0x00;

    /** the task being monitored */
    LongRunningTask task;

    /** this controlling thread's state */
    volatile int wait_state = THREAD_STATE_INACTIVE;

    /** the synchronization lock for the wait/notify mechanism */
    private Object lock = new Object();

    /**
     * a failure message as returned by the controlling thread
     * only meaningful if {@link #hasFailed()} returns <code>true</code>
     */
    private String failureMessage;

    /** flag to indicate the long-running thread ended due to an abnormal condition */
    volatile private boolean failed;

    /** a short timeout interval, after which this thread checks for
     *  a cancel command and / or other termination conditions
     */
    private long waitTimeout;
    private long totalTimeout;

    /** the observer object that is invoked at task completion */
    private Observer observer;

    /**
     * use this method to check on progress
     */
    public int getState() { return wait_state; }

    public LongRunningTask getTask() { return task; }

    /**
     * this will be used by clients of this controller thread to
     * retrieve any failure messages from the long-running task
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Used to signal the task under control was terminated with an exception
     * and/or other error condition
     *
     * @return
     *      <code>true</code> if the task was terminated by an
     *      exception condition or other error condition
     */
    public boolean hasFailed() {
        return failed;
    }


    /**
     * Creates a new instance of ControllerThread that will "keep tabs" on
     * the long running task.
     * Essentially, every waitInterval msec (probably a reasonably short interval
     * whose duration must balance application responsiveness with
     * efficient use of resources) it will check upon the long running task
     * to ensure:
     * (a) that it is still running;
     * (b) that the user has not cancelled the operation (via this
     *     class's {@link LongRunningTask#cancel()} method;
     * (c) that <code>task</code> has not completed;
     *
     * It will also update (if available) a progress indicator.
     * However, if after <code>timeout</code> msec have elapsed, and
     * the long-running task has not completed its deeds, it will be
     * signaled (via its {@link LongRunningTask#cancel()} method and this
     * thread execution will terminate.
     *
     * The long-running task can signal of error conditions by throwing
     * an exception in its {@link Runnable#run} method, that will be caught
     * by this class, and the message (along with the error condition)
     * indicated by {@link #getFailureMessage()}.
     *
     */
    public ControllerThread(LongRunningTask task, long waitInterval,
            long timeout) {
        this.task = task;
        this.waitTimeout = waitInterval;
        this.totalTimeout = timeout;
    }

    public ControllerThread(LongRunningTask task, long waitInterval,
            long timeout, Observer observer) {
        this(task, waitInterval, timeout);
        setObserver(observer);

        observer.addMsg("Task: "+task.getClass().getName());
        observer.addMsg("Observer: "+observer.getClass().getName());
    }

    public void run() {
        long start = System.currentTimeMillis();
        long elapsed;
        Thread t = new Thread(new ControllerRunnable());
        t.start();
        while(!task.isDone() && !task.isStopped() && !failed) {
            wait_state = THREAD_STATE_WAIT;
            elapsed = System.currentTimeMillis()-start;
            if (elapsed > totalTimeout){
                task.cancel();
                wait_state = THREAD_STATE_EXPIRED;
                getObserver().interrupted();
                return;
            }
            synchronized (lock) {
                try {
                    // double-check to avoid race conditions
                    if(!task.isDone() && !task.isStopped())
                        lock.wait(waitTimeout);
                } catch (InterruptedException ex) {
                    failed = true;
                    failureMessage = "ControllerThread interrupted";
                    t.interrupt();
                    getObserver().interrupted();
                    return;
                }
            }
        } // while
    }

    /**
     * The long-running task will run inside this thread: this will
     * both remove the need from LongRunningTask to extend Runnable
     * and also will provide a "wrapper" to catch exceptions and
     * provide the notification when {@link LongRunningTask#execute()}
     * ends
     */
    private class ControllerRunnable implements Runnable {

//        public ControllerRunnable(LongRunningTask task)
        public void run() {
            if(task == null) {
                failureMessage = "No task to execute";
                failed = true;
                getObserver().failed(new TaskException("No task to execute"));
                return;
            }
            try {
                task.execute(getObserver());
                synchronized (lock) {
                    lock.notify();
                }
                getObserver().complete();
            } catch(TaskException ex) {
                failureMessage = new String(ex.getMessage());
                failed = true;
                getObserver().failed(ex);
            }
        }
    }

    public Observer getObserver() {
        return observer;
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

}
