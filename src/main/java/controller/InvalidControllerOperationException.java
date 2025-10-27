package controller;

/**
 * <p>Describes an exception triggered by an invalid controller operation performed on the model.</p>
 */
public class InvalidControllerOperationException extends IllegalStateException {
  /**
   * <p>Lists the types of invalid operations.</p>
   */
  public enum InvalidOperationType {
    //Registering & Authentication errors
    USER_ALREADY_EXISTS,

    //Same title errors
    NOTICEBOARD_TITLE_ALREADY_EXISTS,
    TODO_TITLE_ALREADY_EXISTS,

    //ToDo sharing errors
    TODO_IS_ALREADY_SHARED,
    TODO_IS_NOT_ALREADY_SHARED,
    CANNOT_SHARE_TODO_WITH_YOURSELF,

    //ToDo move errors
    USER_DOES_NOT_OWN_TARGET_NOTICEBOARD
  }

  //Member variables
  private final InvalidOperationType errorType;

  //Constructor
  /**
   * Instantiates an InvalidControllerOperationException with a message and an error type.
   * @param message the message
   * @param errorType the error type
   */
  public InvalidControllerOperationException(String message, InvalidOperationType errorType) {
    super(message);
    this.errorType = errorType;
  }

  //Methods
  /**
   * Gets the error type.
   * @return the error type
   */
  public InvalidOperationType getErrorType() {
    return errorType;
  }
}