public class EntityIDAlreadyExistsException extends Exception{
    /**If there is an entity added to the system and another one already exists with
    the same ID, throw this exception*/
    public EntityIDAlreadyExistsException(Entity entity) {
        super("The ID " + entity.getId()+ " that the new entity \"" + entity.getName() + "\""
        + " uses already exists in the system. Therefore the new entity was not added.");
    }
}
