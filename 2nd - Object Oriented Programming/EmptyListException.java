public class EmptyListException extends Exception {
    
    //If a list is getting deleted but is empty, throw this exception
    EmptyListException() {
        super("The list you are trying to clear is already empty!");
    }
}
