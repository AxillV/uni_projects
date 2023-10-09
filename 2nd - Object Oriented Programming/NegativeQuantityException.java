public class NegativeQuantityException extends Exception {
    
    //If user attempts to input negative quantity for an entity, throw this exception
    public NegativeQuantityException() {
        super("You've entered a negative value");
    }
}
