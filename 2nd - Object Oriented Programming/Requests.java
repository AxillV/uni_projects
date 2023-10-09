import java.util.Iterator;

public class Requests extends RequestDonationList {

    @Override
    public void add(Beneficiary beneficiary, RequestDonation rdToAdd){
        try {
            //We don't care what this returns, just need to catch the exceptions thrown.
            totalCheck(beneficiary, rdToAdd);
            super.add(rdToAdd);
        } catch (RequestedQtyExceedsAvailabeException RQEAVe) {
            System.out.println(RQEAVe.getMessage());
        } catch (RequestedQtyExceedsAllowedException RQEALe) {
            System.out.println(RQEALe.getMessage());
        }

        
    }    

    @Override
    public void modify(Beneficiary beneficiary, RequestDonation rdToModify, double quantity){
        //double oldQty = rdToModify.getQuantity();
        try {
            //Create a copy of the given rd it's not affected
            var rdCopy = new RequestDonation(rdToModify.getEntity(), quantity);
            //We don't care what this returns, just need to catch the exceptions thrown.
            totalCheck(beneficiary, rdCopy);       
            
        } catch (RequestedQtyExceedsAvailabeException RQEAVe) {
            System.out.println(RQEAVe.getMessage());
            quantity = rdToModify.getQuantity();
        } catch (RequestedQtyExceedsAllowedException RQEALe) {
            System.out.println(RQEALe.getMessage());
            quantity = rdToModify.getQuantity();
        } finally {
            super.modify(rdToModify, quantity);
        }

        
    }

    //For notes on variables, look at cmpRequestQuantitiesWithMaterialLevels method below.
    private boolean validRequestDonation(Beneficiary beneficiary, RequestDonation rq) throws RequestedQtyExceedsAllowedException {     
        if(rq.getEntity().getDetails().equals("Service")){
            return true;
        } else {
            if(beneficiary.getNoPersons() == 1){           
                boolean  cmpResults = cmpRequestQuantitiesWithMaterialLevels(beneficiary, rq, 1);  
                if(!cmpResults){
                    throw new RequestedQtyExceedsAllowedException(rq);
                }
            } else if (beneficiary.getNoPersons() >= 2 && beneficiary.getNoPersons() <= 5){
                boolean  cmpResults = cmpRequestQuantitiesWithMaterialLevels(beneficiary, rq, 2);
                if(!cmpResults){
                    throw new RequestedQtyExceedsAllowedException(rq);
                }
            } else if (beneficiary.getNoPersons() >= 5){
                boolean  cmpResults = cmpRequestQuantitiesWithMaterialLevels(beneficiary, rq, 3);
                if(!cmpResults){
                    throw new RequestedQtyExceedsAllowedException(rq);
                }
            }
            //If no exception was thrown, then request is valid, return true.
            return true;
        }     
    }

    /**
     * Id: Id of entity
     * qty: qty that beneficiary already has received
     * rqQty: requested additional quantity
     * @return True if LEVEL quantity > already received + current request quantity.
     * False if else.
     */
    private boolean cmpRequestQuantitiesWithMaterialLevels(Beneficiary beneficiary, RequestDonation rq, int level) {
        //Get entity id and quantity for request, then compare them to the material level.
        int id = rq.getEntity().getId();
        double rqQty = rq.getQuantity();
        double qty; 
        double levelQty = ((Material) rq.getEntity()).getLevelQuantity(level);

        if(beneficiary.getReceivedList().get(id) == null){
            qty = 0;
        } else {
            qty = beneficiary.getReceivedList().get(id).getQuantity();
        }

        if(levelQty >= qty + rqQty){
            return true;
        }
        return false;
    }

    private void sufficientQtyCheck(RequestDonation rD) throws RequestedQtyExceedsAvailabeException {
        if (rD.getQuantity() > Organization.getRDQuantity(rD.getEntity().getId())){
            throw new RequestedQtyExceedsAvailabeException(rD);
        }
    }

    /**
     * @param beneficiary beneficiary in question
     * @param rD RequestDonation object to be checked.
     * Combines both quantity checks into one for ease of use.
     */ 
    private void totalCheck(Beneficiary beneficiary, RequestDonation rD) throws 
    RequestedQtyExceedsAvailabeException, RequestedQtyExceedsAllowedException {

        try {
            validRequestDonation(beneficiary, rD);
            sufficientQtyCheck(rD);
        } catch (RequestedQtyExceedsAvailabeException RQEAVe) {
            throw RQEAVe;
        } catch (RequestedQtyExceedsAllowedException RQEALe) {
            throw RQEALe;
        }

    }

    // Recheck for exceptions because you could add two different times such that
    // qt1+qt2>allowed/availabe, while not being over the limit themselves.

    public void commit(String phone) {   
        Iterator<RequestDonation> requestRDEntitiesIterator = rdEntities.iterator();
        Beneficiary beneficiary = Organization.findBeneficiary(phone);

        if (rdEntities.size()==0) {
            System.out.println("No requests to be commited");
            return;
        }

        while(requestRDEntitiesIterator.hasNext()){
            try{
                var requestRD = requestRDEntitiesIterator.next();

                //This throws exceptions, so you only need to call it.
                totalCheck(beneficiary, requestRD);
                
                //This check happens because of the way it is implemented in main
                if (requestRD.getQuantity() != 0) {
                    Organization.subtractFromDonations(requestRD);
                    beneficiary.addToReceivedList(requestRD);
                    requestRDEntitiesIterator.remove();
                }

            } catch (RequestedQtyExceedsAvailabeException RQEAVe) {
                System.out.println(RQEAVe.getMessage());
            } catch (RequestedQtyExceedsAllowedException RQEALe) {
                System.out.println(RQEALe.getMessage());
            } 
        }
        System.out.println("The pending requests have been committed successfully"); 
    }
}