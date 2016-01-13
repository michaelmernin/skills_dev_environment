package com.perficient.etm.exception;

/**
 * Exception to mark that the communication between the activiti process
 * and the back-end layer has failed due an exception in the engine
 * @author Alexando Blanco <alex.blanco@perficient.com>
 *
 */
public class ActivitiProcessInitiationException extends ETMException {

    private static final long serialVersionUID = 5970354920602329147L;

    public ActivitiProcessInitiationException(Throwable t) {
        super("Error while starting activiti process due exception",t);
    }
}
