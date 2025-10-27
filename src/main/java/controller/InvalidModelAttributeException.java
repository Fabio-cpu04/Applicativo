package controller;

/**
 * <p>Describes an exception triggered by an invalid attribute in a model entity.</p>
 */
public class InvalidModelAttributeException extends IllegalArgumentException {
    /**
     * <p>Lists the types of attribute errors.</p>
     */
    public enum InvalidAttributeType {
        //User attribute errors
        INVALID_USER_USERNAME,
        INVALID_USER_PASSWORD,

        //Noticeboard attribute errors
        INVALID_NOTICEBOARD_ID,
        INVALID_NOTICEBOARD_TITLE,
        INVALID_NOTICEBOARD_DESCRIPTION,

        //ToDo attribute errors
        INVALID_TODO_ID,
        INVALID_TODO_TITLE,
        INVALID_TODO_COLOR,
        INVALID_TODO_ACTIVITY_URL,
        INVALID_TODO_IMAGE_URL,
    }

    //Member variables
    private final InvalidAttributeType errorType;

    //Constructor
    /**
     * Instantiates an InvalidModelAttributeException with a message and an error type.
     * @param message the message
     * @param errorType the error type
     */
    public InvalidModelAttributeException(String message, InvalidAttributeType errorType) {
        super(message);
        this.errorType = errorType;
    }

    //Methods
    /**
     * Gets the error type.
     * @return the error type
     */
    public InvalidAttributeType getErrorType() {
        return errorType;
    }
}
