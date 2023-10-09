public class RequestedQtyExceedsAvailabeException extends Exception {
    //If user attempts to request more quantity than the organization has, throw this exception
    public RequestedQtyExceedsAvailabeException(RequestDonation rD) {
        super("The requested quantity of " + rD.getEntity().getName() + " is not availabe from "
        + "the organisation and it was not applied.");
    }
}
