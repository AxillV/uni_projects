public class Main {
    public static void main(String[] args) {
        Organization org1 = new Organization("Amazon");

        Material wood = new Material("Wood", "piece of wood", 3546, 234, 764, 3495);
        Material bandaids = new Material("Bandaids", "5 bandaids", 1236, 123, 545, 1234);
        Material tissues = new Material("Tissues", "box of tissues", 6546, 231, 900, 3000);

        Organization.addEntity(wood);
        Organization.addEntity(bandaids);
        Organization.addEntity(tissues);

        Service MedicalSupport = new Service("MedicalSupport", "Assistance", 3542);
        Service NurserySupport = new Service("NurserySupport", "Assistance", 1231);
        Service BabySitting = new Service("BabySitting", "Assistance", 6542);

        Organization.addEntity(MedicalSupport);
        Organization.addEntity(NurserySupport);
        Organization.addEntity(BabySitting);

        Beneficiary john = new Beneficiary("Giannis", "6954712676");
        Beneficiary mike = new Beneficiary("Mixalis", "6954347506");
        Donator will = new Donator("Will", "6954391840");

        Organization.insertBeneficiary(john);
        Organization.insertBeneficiary(mike);
        Organization.insertDonator(will);

        Admin nick = new Admin("Nikos", "6954512346");
        org1.setAdmin(nick);

        //DEBUGGING Tools
            //Pre-existing donations to the org.
        Organization.addToDonations(new RequestDonation(wood, 3500));
        Organization.addToDonations(new RequestDonation(bandaids, 2));
        Organization.addToDonations(new RequestDonation(MedicalSupport, 500));
        Organization.addToDonations(new RequestDonation(NurserySupport, 1.5));
            //Epideiksh EntityIDAlreadyExcistsException
        // Organization.addEntity(new Service("IDTest1", "IDTest1 Details", 3542));
        // Organization.addEntity(new Service("IDTest2", "IDTest2 Details", 3546));
        // Organization.addEntity(new Material("IDTest3", "IDTest3 Details", 3546, 11,111,1111));
        // Organization.addEntity(tissues);
            
            /*
             * Beneficiry/Donator RD testing
             *
             * NOTE: When trying to add to received list, use addToRequestList and then commit, else
             * the needed checks won't take place!  
             * You can also skip commiting to test the functionality of the commit buttons in 
             * the command line instead.
             * Lastly, activating the try/catch clause wis mandatory for most of the methods
             * being tested because of the NegativeQuantityException
             */
        //  try {
                // Permitted request + commit
            //john.addToRequestList(new RequestDonation(wood, 200));
            //john.commit();

                // Unpermitted because of negative value
            //john.addToRequestList(new RequestDonation(bandaids, -3));

                // Unpermitted because it exceeds availabe quantity at organization
            // john.addToRequestList(new RequestDonation(bandaids, 3));
            // john.setNoPerson(10);

                // Adding more to requests list and commiting with updated beneficiary LEVEL
            // john.addToRequestList(new RequestDonation(wood, 1500));
            // john.commit();
            // john.addToRequestList(new RequestDonation(wood, 2000));
            // john.commit();

            // mike.setNoPerson(2);
            // mike.addToRequestList(new RequestDonation(MedicalSupport, 2000));
            // mike.commit();

                /*
                 * First one doesn't go through because of beneficiary LEVEL restrictions
                 * Next 2 go through, but later on cannot be commited.
                 */

            // mike.addToRequestList(new RequestDonation(wood, 765.5));
            // mike.addToRequestList(new RequestDonation(wood, 763.5));
            // mike.addToRequestList(new RequestDonation(wood, 1.5));
            // mike.commit();

            // will.addToOffersList(new RequestDonation(tissues, -400));
            // will.addToOffersList(new RequestDonation(tissues, 400));
            // will.addToOffersList(new RequestDonation(NurserySupport, 250.187));
            // will.commit();
            // } catch (NegativeQuantityException NQe){
            //    System.out.println(NQe.getMessage());
            // }

            /*
             * Prints out all donations that are currently in the system ready to be picked
             * by the beneficiaries
             */
        // for (var rD : Organization.getCurrentDonationsCopy().getRdEntities()){
        //     System.out.println(rD.getEntity().getName() + " " + rD.getQuantity());
        // }
            //Self-explanatory
        
       
        // john.monitorRequests();
        // mike.monitorRequests();
        // will.monitorOffers();

            //These are for seeing everything the beneficiaries have already received.
        // for (var rD : john.getReceivedList().getRdEntities()){
        //     System.out.println(rD.getEntity().getName() + " " + rD.getQuantity());
        // }
        // for (var rD : mike.getReceivedList().getRdEntities()){
        //     System.out.println(rD.getEntity().getName() + " " + rD.getQuantity());
        // }
        
        Menu.mainLoop();
        return;
    }
}
