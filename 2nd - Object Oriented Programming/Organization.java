import java.util.*;

public class Organization
{
    //Ola einai static epeidh uparxei panta mono enas organismos sto programma, opote xreiazontai mono mia fora ta pedia tou.
    private static String name;
    private static Admin admin;
    private static List<Entity> entityList = new ArrayList<Entity>();
    private static List<Donator> donatorList = new ArrayList<Donator>();
    private static List<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
    private static RequestDonationList currentDonations = new RequestDonationList();

    public Organization(String name) {
        Organization.name = name;
    }

    public void setAdmin(Admin admin) {
        Organization.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
    }

    public static String getName() {
        return Organization.name;
    }

    public static void printAdmin() {   
        System.out.println(admin.name + ", " + admin.phone + " (" + Organization.name + ")");
        System.out.println("    You're the admin!");           
    }

    //Epistrefei anafora
    public static List<Entity> getEntityList() {
        return Organization.entityList;
    }

    //Epistrefei antigrafo
    public static List<Entity> getEntityListCopy() {
        return new ArrayList<Entity>(Organization.entityList);
    }

    //Epistrefei antigrafo
    public static RequestDonationList getCurrentDonationsCopy() {
        RequestDonationList copy = new RequestDonationList(currentDonations);
        return copy;
    }

    //Wrappers
    public static void addToDonations(RequestDonation rd) {
        currentDonations.add(rd);
    }

    public static void subtractFromDonations(RequestDonation rd) {
        RequestDonation targetRD = currentDonations.get(rd.getEntity().getId());
        
        currentDonations.modify(rd, targetRD.getQuantity() - rd.getQuantity());
    }

    public static double getRDQuantity(int id) {
        return Organization.currentDonations.getRDQuantity(id);
    }

    public static void entityAlreadyExists(Entity entityToSearchFor) throws EntityIDAlreadyExistsException{
        for (Entity entity : entityList) {
            if (entity.getId() == entityToSearchFor.getId()){
                throw new EntityIDAlreadyExistsException(entityToSearchFor);                
            }
        }
    }

        //MODIFY METHODS

    public static void addEntity(Entity entity) {
        try {
            //Check if entity already exists, if it does it will throw an exception and it won't be added.
            entityAlreadyExists(entity);
            entityList.add(entity);
        } catch (EntityIDAlreadyExistsException EIDAEe) {
            System.out.println(EIDAEe.getMessage());
        }
        
    }

    public static void removeEntity(Entity entity) {
        entityList.remove(entity);
    }

    public static void insertDonator(Donator donator) {
        donatorList.add(donator);
    }

    public static void removeDonator(int index) {
        donatorList.remove(index);
    }

    public static void findAndPrintDonator(String phone) {
        for(Donator donator : donatorList) {
            if(donator.phone.equals(phone)) {
                System.out.println(donator.name + ", " + donator.phone + " (" + Organization.name + ")");
                System.out.println("    You're a donator!");
            }
        }
    }

    public static Donator findDonator(String phone) {
        for(Donator donator : donatorList) {
            if(donator.phone.equals(phone)) {
                return donator;
            }
        }
        return null;
    }

    public static Beneficiary findBeneficiary(String phone) {
        for(Beneficiary beneficiary : beneficiaryList) {
            if(beneficiary.phone.equals(phone)) {
                return beneficiary;
            }
        }
        return null;
    }

    public static void insertBeneficiary(Beneficiary beneficiary) {
        beneficiaryList.add(beneficiary);
    }

    public static void removeBeneficiary(int index) {
        beneficiaryList.remove(index);
    }

    public static void findAndPrintBeneficiary(String phone) {
        for(Beneficiary beneficiary : beneficiaryList) {
            if(beneficiary.phone.equals(phone)) {
                System.out.println(beneficiary.name + ", " + beneficiary.phone + " (" + Organization.name + ")");
                System.out.println("  You're a beneficiary!");
            }
        }
    }

    public static void clearBeneficiaryReceivedList(int indexOfBeneficiary) throws EmptyListException {
        if (beneficiaryList.get(indexOfBeneficiary).getReceivedList().getRdEntities().isEmpty() == true) {
            throw new EmptyListException();
        }

        beneficiaryList.get(indexOfBeneficiary).clearReceivedList();
    }

    /**
     * @return true if list was not already empty else false
     */
    public static boolean clearBeneficiaryReceivedListNoMessage(int indexOfBeneficiary){
        return beneficiaryList.get(indexOfBeneficiary).clearReceivedListNoMessage();
    }

    public static void listEntities() {
        for (var entity: entityList)
            System.out.println(entity);
    }

    public static void listBeneficiaries() {
        int count = 1;
        for (var beneficiary: beneficiaryList) {
            System.out.println("║ " + count + "." + beneficiary);
            count++;
        }
    }

    public static void listDonators() {
        int count = 1;
        for (var donator: donatorList) {
            System.out.println("║ " + count + "." + donator);
            count++;
        }
    }

    public static int totalBeneficiaries(){
        return beneficiaryList.size();
    }

    public static int totalDonators(){
        return donatorList.size();
    }

    /**
     * Searches phone number
     * @return 
     * 1 if beneficiary, 
     * 2 if donator, 
     * 3 if admin, 
     * 0 if new 
     */
    public static int searchPhone (String Phone) {
        for (var user: beneficiaryList) {
            if (Phone.equals(user.phone)) {
                return 1;
            }
        }

        for (var user: donatorList) {
            if (Phone.equals(user.phone)) {
                return 2;
            }
        }

        if (Phone.equals(admin.phone)) {
            return 3;
        }

        return 0;
    }
}
