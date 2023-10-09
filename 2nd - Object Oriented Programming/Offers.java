import java.util.Iterator;
public class Offers extends RequestDonationList {

    public void commit() {
        Iterator<RequestDonation> rdEntitiesIterator = rdEntities.iterator();
        if (rdEntities.size() == 0){
            System.out.println("No offers to be commited");
            return;
        }
        while(rdEntitiesIterator.hasNext()){
            var rd = rdEntitiesIterator.next();

            //This check happens because of the way it is implemented in main
            if (rd.getQuantity() != 0) {
                Organization.addToDonations(rd);
                rdEntitiesIterator.remove();
            }
        }
        System.out.println("The pending requests have been committed successfully"); 
    }
}
