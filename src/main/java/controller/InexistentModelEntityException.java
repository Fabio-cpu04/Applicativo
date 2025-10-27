package controller;

import java.util.NoSuchElementException;

/**
 * <p>Describes an exception triggered by an inexistent model entity.</p>
 */
public class InexistentModelEntityException extends NoSuchElementException {
  /**
   * <p>Lists the types of missing model entities.</p>
   */
  public enum EntityType {
    //Inexistent model entities
    INEXISTENT_USER,
    INEXISTENT_NOTICEBOARD,
    INEXISTENT_TODO,

    //Inexistent target or destination Noticeboard
    INEXISTENT_ORIGIN_NOTICEBOARD,
    INEXISTENT_TARGET_NOTICEBOARD
  }

  //Member variables
  private final EntityType entityType;

  //Constructor
  /**
   * Instantiates an InvalidModelAttributeException with a message and a model entity type.
   * @param message the message
   * @param entityType the entity type
   */
  public InexistentModelEntityException(String message, EntityType entityType) {
    super(message);
    this.entityType = entityType;
  }

  //Methods
  /**
   * Gets the entity's type.
   * @return the entity's type
   */
  public EntityType getEntityType() {
    return entityType;
  }
}