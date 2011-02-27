/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Threads;

/**
 * Indicates this task has encountered an exceptional condition and
 * needs to terminate.  A failure message is passed in the constructor
 * and is available either via this Exception's getMessage() or via
 * a {@link ControllerThread#getFailureMessage()}
 *
 * taken from http://codetrips.blogspot.com/2007/04/multi-threading-mon-amour.html
 * thanks to Marco Massenzio for sharing
 * @author Marco Massenzio
 */
public class TaskException extends java.lang.Exception {

    /**
     * Constructs an instance of <code>TaskException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TaskException(String msg) {
        super(msg);
    }
}
