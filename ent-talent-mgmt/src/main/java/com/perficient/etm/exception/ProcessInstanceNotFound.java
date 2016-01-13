package com.perficient.etm.exception;

/**
 * Exception class that will mark when a review process
 * instance can't be found in the historic data of  Activiti engine
 *
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 */
public class ProcessInstanceNotFound extends Exception {

    private static final long serialVersionUID = -1069208892635434961L;

    public ProcessInstanceNotFound(String id) {
        super(String.format("Process With instance %s was not found", id));
    }

    public ProcessInstanceNotFound(String id, Throwable t) {
        super(String.format("Process With instance %s was not found due the following exception", id), t);
    }

}
