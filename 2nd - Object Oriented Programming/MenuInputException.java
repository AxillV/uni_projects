public class MenuInputException extends Exception {

    //If user selects an invalid option, throw this exception
    public MenuInputException(){
        super("Please input a valid choice");
    }
}
