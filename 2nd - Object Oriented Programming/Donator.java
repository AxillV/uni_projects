public class Donator extends User{

    Donator(String name, String phone) {
        super(name, phone);
    }

    private Offers offersList = new Offers();

    public void addToOffersList(RequestDonation rd) throws NegativeQuantityException {
        
        if (rd.getQuantity()<0) {
            throw new NegativeQuantityException();
        }
        offersList.add(rd);
    }

    public void removeFromOffersList(int index) {
        offersList.remove(offersList.getWithIndex(index));
    }

    public void modifyQuantity(int index, double quantity) {
        offersList.modify(offersList.getWithIndex(index), quantity);
    }

    public void commit() {
        offersList.commit();
    }

    public void monitorOffers(){
        offersList.monitor();
    }

    public void clearOffersList(){
        try {
            offersList.reset();
            
        } catch (EmptyListException ELe) {
            System.out.println(ELe.getMessage());
            return;
        }

        System.out.println("The pending offers have been cleared successfully");
            
    }
    
    public void clearOffersListNoMessage(){
        try {
            offersList.reset();     
        } catch (EmptyListException ELe) {        
            return;
        }
    }

    public int getOffersListSize(){
        return offersList.getRdEntities().size();
    }

    @Override
    public String toString() {
        return this.name + ", " + this.phone + " (Donator at " + Organization.getName() + ")";
    } 

    public String getNameQuantityAtIndex(int index) {
        var rdEntity = offersList.getWithIndex(index);
        return rdEntity.getEntity().getName() + " (" + rdEntity.getQuantity() + ")";
    }

}
