public class Beneficiary extends User{

    Beneficiary(String name, String phone) {
        super(name, phone);
    }

    Beneficiary(String name, String phone, int noPersons) {
        super(name, phone);
        this.noPersons = noPersons;
    }

    private int noPersons = 1;

    private RequestDonationList receivedList = new RequestDonationList(); //have
    private Requests requestsList = new Requests();//want

    public void setNoPerson(int noPersons){
        this.noPersons = noPersons;
    }

    public int getNoPersons() {
        return noPersons;
    }

    public RequestDonationList getReceivedList() {
        return receivedList;
    }

    public Requests getRequestsList() {
        return requestsList;
    }

    public void addToReceivedList(RequestDonation requestDonation) {
        receivedList.add(requestDonation);
    }

    public void removeFromReceivedList(RequestDonation requestDonation) {
        receivedList.remove(requestDonation);
    }

    public void clearReceivedList(){      
        try {
            receivedList.reset();
            
        } catch (EmptyListException ELe) {
            System.out.println(ELe.getMessage());
            return;
        }

        System.out.println("The received list has been cleared successfully");
    }
    /**
     * @return true if list was not already clear else false
     */
    public boolean clearReceivedListNoMessage(){      
        try {
            receivedList.reset();  
            return true;  
        } catch (EmptyListException ELe) {
            return false;
        }
    }

    public void clearRequestsList(){
        try {
            requestsList.reset();
            
        } catch (EmptyListException ELe) {
            System.out.println(ELe.getMessage());
            return;
        }

        System.out.println("The pending requests have been cleared successfully");
    }

    public void clearRequestsListNoMessage(){
        try {
            requestsList.reset();     
        } catch (EmptyListException ELe) {        
            return;
        }
    }

    public void addToRequestList(RequestDonation request) throws NegativeQuantityException {
        if (request.getQuantity()<0) {
            throw new NegativeQuantityException();
        }
        requestsList.add(this, request);
    }

    public void removeFromRequestList(int index) {
        requestsList.remove(requestsList.getWithIndex(index));
    }

    public void modifyRequestQuantity(int index, double quantity) {
        requestsList.modify(this, requestsList.getWithIndex(index), quantity);
    }

    public void commit() {
        requestsList.commit(this.phone);      
    }

    public void monitorRequests(){
        requestsList.monitor();
    }

    public int getRequestsListSize(){
        return requestsList.getRdEntities().size();
    }

    @Override
    public String toString() {
        return this.name + ", " + this.phone + " (Beneficiary at " + Organization.getName() + ")";
    }

    public String getNameQuantityAtIndex(int index) {
        var rdEntity = requestsList.getWithIndex(index);
        return "║ " + rdEntity.getEntity().getName() + " (" + rdEntity.getQuantity() + ")";
    }

}