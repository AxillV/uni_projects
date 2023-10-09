import java.util.*;

public class RequestDonationList { 
    protected List<RequestDonation> rdEntities;


    RequestDonationList() {
        rdEntities = new ArrayList<RequestDonation>();
    }

    /**
     * This constructor is used to make copies
     */ 

    RequestDonationList(RequestDonationList original) {
        this.rdEntities = new ArrayList<RequestDonation>(original.rdEntities);
    }

    //Getters/Setters   
    public List<RequestDonation> getRdEntities() {
        return rdEntities;
    }

    public RequestDonation get(int id){
        /**
         * Searches through the list and returns the requestdonation 
         * object with matching id    
         */
        for (var rdEntity : rdEntities){
            if(id == rdEntity.getEntity().getId()){
                return rdEntity;
            }           
        }
        return null;
    }

    public RequestDonation getWithIndex(int index){
        return rdEntities.get(index);
    }

    /**
     * Me mia prospelash ths rdEntities, elegxei an to eidos me to id einai stis diathesimes prosfores tou organismou
     * @return True an to eidos einai sthn rdEntities
     */
    public boolean isInRequestDonationList(int id) {
        for (RequestDonation rdEntity : rdEntities) {
            if (rdEntity.getEntity().getId() == id) {
                return true;
            }
        }
                
        return false;
        }

    //Adds new request
    public void add(RequestDonation rdToAdd) {
        /*
         * Loop through all RequestDonations in rdEntities, if a match is found
         * add the quantity of rdToAdd to the pre-existing one, else add the
         * new RequestDonation 
         */

        for (RequestDonation rdEntity : rdEntities){
            if(rdToAdd.equals(rdEntity)){
                rdEntity.setQuantity(rdEntity.getQuantity() + rdToAdd.getQuantity());
                return;
            }   
        }     
        rdEntities.add(rdToAdd);          
    }

    /**
    *  @apiNote  needed in Requests.
    */ 
    public void add(Beneficiary beneficiary, RequestDonation rdToAdd){}

    public void remove(RequestDonation rdToRemove){
        //Loop through all RequestDonations in rdEntities, if a match is found, delete it.
        for (RequestDonation rdEntity : rdEntities){
            if(rdToRemove.equals(rdEntity)){
                rdEntities.remove(rdEntity);    
                return;
            }   
        }    
    }

    /**
     * 
     * @param rdToModify RequestDonation object to search for in list
     * @param quantity new quantity
     * quantity based on entity
     */
    public void modify(RequestDonation rdToModify, double quantity){
        //Searches for RD of argument and if a match is found, modifies quantity of it in arraylist.
        for (RequestDonation rdEntity : rdEntities){
            if(rdToModify.equals(rdEntity)){
                rdEntity.setQuantity(quantity);
                return;
            }   
        }
    }

    /**
    *  @apiNote  needed in Requests.
    */ 
    public void modify(Beneficiary beneficiary ,RequestDonation rdToModify, double quantity){}

    /**
     * Emfanizei to sunolo twn eidwn ths listas rdEntities me arithmish.
     */
    public void monitor(){
        int count = 1;
        System.out.println("╔" + "═".repeat(49));
        for (RequestDonation rdEntity : rdEntities){
            System.out.print ("║ " + count + ".");
            System.out.println(rdEntity.getEntity().getName() + " (" + rdEntity.getQuantity() + ")");
            count++;
        }
    }

    public double getRDQuantity(int id) {
        if(get(id) == null) return 0;
        return get(id).getQuantity();
    }

    //Removes everything from the list
    public void reset() throws EmptyListException {
        if (rdEntities.size()==0) throw new EmptyListException();
        rdEntities.clear();
    }

}