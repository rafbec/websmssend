/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.websmssend.thread;

/**
 *
 * taken from http://codetrips.blogspot.com/2007/04/multi-threading-mon-amour.html
 * thanks to Marco Massenzio for sharing
 * @author Marco Massenzio
 */
public interface IObserver {
    /** to add logging function */
    public void addMsg(String msg);

    /**
     * callback for signaling progress of time-consuming process, provides
     * an opportunity to either allow stopping the background operation or
     * update an UI element providing visual indication that the application
     * is not 'stuck'
     */
    public void idle();

    /** time-consuming operation complete, results, if any, are available */
    public void complete();

    /** acknowledgement of interruption, optional */
    public void interrupted();

    /** Use this instead of idle() to signal progress */
    public void announceProgress(int progress);

    /** Called when the task fails */
    public void failed(TaskException tex);
}

