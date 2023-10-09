public class RequestedQtyExceedsAllowedException extends Exception{
    //If user attempts to request more qty than allowed by organization, throw this exception
    public RequestedQtyExceedsAllowedException(RequestDonation rD) {
        super("The requested quantity of " + rD.getEntity().getName() + " exceeds the "
        + "allowed limits set from the organization for this beneficiary and was not applied.");
    }
}
