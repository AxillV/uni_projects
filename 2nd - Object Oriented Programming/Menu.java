import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Iterator;
import java.util.List;

public class Menu {
    //Koinh scanner gia kathe methodo wste na mhn exoume memory leak
    private static Scanner sc;

    /**
     * Epilogh, h filosofia tou return true/false einai pws mexri na ginei return
     * false, theloume na epistre4oume sto idio menu me ta idia stoixeia.
     * Opote otan ginetai mia epilogh apo ton xrhsth, 1-2 kai teleiwsei h
     * leitourgeia ths, o kwdikas sto menuBasedOnInput 8a 3anatre3ei 
     * to idio UserMenu (opou User = Donator,Beneficiary,Admin)
     */
    public static void mainLoop(){
        while(true){
            if(!Menu.menuBasedOnInput(Menu.inputPhone())){
                break;
            };
        }
        sc.close();
    }

    /**Diabazei to input tou xrhsth, xeirizetai eswterika thn eksairesh mh egkurou input.
     * @return Epistrefei egkuro arithmo thlefwnou.
     */
    private static String inputPhone() {
        boolean isValidInput = true;
        String phone_number;
        sc = new Scanner(System.in);

        System.out.println("╔" + "═".repeat(47 + Organization.getName().length()) + "╗");
        System.out.println("║ Welcome to the donator/beneficiary system of " + Organization.getName() + " ║");
        System.out.println("╚" + "═".repeat(47 + Organization.getName().length()) + "╝");

        do {
            System.out.print("Please input your phone number to authenticate: ");
            phone_number = sc.nextLine();

            try {
                Long.parseLong(phone_number);
                isValidInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Please input a valid phone number!");
                isValidInput = false;
            }
        } while (!isValidInput);
        
        return phone_number;
    }

    /**@return Returns true if method needs to be rerun, returns false if escape
     * back to menuBasedOnInput, or lastly exit program   
     */ 
    private static boolean DonatorMenu(String phone) {
        System.out.println("╔" + "═".repeat(38) + "╗");
        System.out.println("║" + "█".repeat(14) + " Welcome! " + "█".repeat(14) + "║");
        System.out.println("╚" + "═".repeat(38) + "╝");
        Organization.findAndPrintDonator(phone);

        //Ektupwsh epilogwn
        System.out.println("╔" + "═".repeat(24) + "╗");
        System.out.println("║" + " ".repeat(8) + "Options" + " ".repeat(9) + "║");
        System.out.println("╠" + "═".repeat(24) + "╣");
        System.out.println("║ 1.Add Offers           ║");
        System.out.println("║ 2.Show Offers          ║");
        System.out.println("║ 3.Commit               ║");
        System.out.println("║ 4.Back                 ║");
        System.out.println("║ 5.Logout               ║");
        System.out.println("║ 6.Exit                 ║");

        //Epilogh 1 ews 6
        int inChoice = -1;
        do {
            try{
                inChoice = readInputIntRange(1, 6);
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
        } while (inChoice == -1);

        /**
         * Epilogh, h filosofia tou return true/false einai pws mexri na ginei return
         * false, theloume na epistrepsoume sto idio menu me ta idia stoixeia.
         * Opote otan ginetai mia epilogh apo ton xrhsth, 1-3 kai teleiwsei h
         * leitourgeia ths, o kwdikas sto menuBasedOnInput 8a ksanatreksei 
         * to idio UserMenu (opou User = Donator,Beneficiary,Admin)
         */
        switch(inChoice) {
            case 1:
                while(true){
                    if(!DonatorAddOfferMenu(phone)) break;
                }
                return true;

            case 2:
                while(true){
                    if(!DonatorShowOffersMenu(phone)) break;
                }
                return true;
                
            case 3:
                Organization.findDonator(phone).commit();
                return true;

            case 4:
                Organization.findDonator(phone).clearOffersListNoMessage();
                return false;

            case 5:
                Organization.findDonator(phone).clearOffersListNoMessage();
                return false;

            case 6:
                System.out.println("╔" + "═".repeat(36) + "╗");
                System.out.println("║" + " The program will now be terminated " + "║");
                System.out.println("╚" + "═".repeat(36) + "╝");
                System.exit(0);
                return false;
        }
        return false;
    }

    /**@return Returns true if method needs to be rerun, returns false if escape
    */
    private static boolean DonatorAddOfferMenu(String phone) {
        //Proswrinh lista gia thn emfanish material/services ston xrhsth.
        RequestDonationList availableDonations = new RequestDonationList();

        System.out.println("╔" + "═".repeat(14) + "╗");
        System.out.println("║ Categories:  ║");
        System.out.println("╠" + "═".repeat(14) + "╣");
        System.out.println("║ 1.Materials  ║");
        System.out.println("║ 2.Services   ║");
        System.out.println("║ 3.Back       ║");
        
        int inChoice = -1;
        do {
            try{
                inChoice = readInputIntRange(1, 3);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
        } while (inChoice == -1); 
        
        if (inChoice == 3) return false;    //Return false aka. return to donmenu

        double quantity = 0;                //H posothta tou eidous pou tha prosferei o xrhsths
        Entity selectedEntity = null;       //To eidos pou epilegei o xrhsths  

        switch(inChoice) {
            //Case for Materials selected
            case 1: 
                //Display materials until back is pressed, then go back (rerun AddOfferMenu)
                while(true){
                    //Reload arraylist in case user viewed another category before
                    try {
                        availableDonations.reset();
                    } catch (EmptyListException ELe){}
                    availableDonations = getAvailabeDonationsOfType(1);
                    
                    //Print all available products for donator and then the back button (number of back button is last donation +1)
                    availableDonations.monitor();
                    System.out.println("║ " + (availableDonations.getRdEntities().size()+1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));
                    
                    inChoice = -1;
                    while(inChoice == -1){
                        try {     
                            inChoice = readInputIntRange(1, availableDonations.getRdEntities().size() + 1);
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
                    }
                    
                    //Return true (go back a step) if back is pressed
                    if (inChoice == availableDonations.getRdEntities().size() + 1) return true; 

                    selectedEntity = availableDonations.getWithIndex(inChoice-1).getEntity();
                    System.out.println(selectedEntity);
                    
                    String yesNoIn = "";
                    System.out.print("Would you like to donate quantity? (y/n): ");
                    yesNoIn = sc.nextLine();
                    
                    while(!(yesNoIn.equals("y") || yesNoIn.equals("n"))){
                        System.out.print("Please input a valid answer! (y/n): ");
                        yesNoIn = sc.nextLine();
                    }

                    if(yesNoIn.equals("n")) continue; //Go back to showing all materials

                    quantity = -1;
                    while (quantity < 0){
                        try {
                            quantity = readInputQuantity();
                        } catch (NegativeQuantityException NQe) {
                            System.out.println(NQe.getMessage());
                        }
                    }
                    try {
                        Organization.findDonator(phone).addToOffersList(new RequestDonation(selectedEntity, quantity));
                    } catch (NegativeQuantityException NQe) {}
                }

            //Case for services selected
            case 2:
                //Display services until back is pressed, then go back (rerun AddOfferMenu)
                while(true){
                    //Reload arraylist in case user viewed another category before
                    try {
                        availableDonations.reset();
                    } catch (EmptyListException ELe){}
                    availableDonations = getAvailabeDonationsOfType(2);

                    //Print all available products for donator and then the back button (number of back button is last donation +1)
                    availableDonations.monitor();       
                    System.out.println("║ " + (availableDonations.getRdEntities().size()+1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));

                    inChoice = -1;
                    while(inChoice == -1){
                        try {
                            inChoice = readInputIntRange(1, availableDonations.getRdEntities().size() + 1);
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
                    }

                    //return true (go back a step) if back is pressed
                    if (inChoice == availableDonations.getRdEntities().size() + 1) return true; 

                    selectedEntity = availableDonations.getWithIndex(inChoice-1).getEntity();
                    System.out.println(selectedEntity);
                    
                    String yesNoIn = "";
                    System.out.print("Would you like to donate quantity? (y/n): ");
                    yesNoIn = sc.nextLine();
                    
                    while(!(yesNoIn.equals("y") || yesNoIn.equals("n"))){
                        System.out.print("Please input a valid answer! (y/n): ");
                        yesNoIn = sc.nextLine();
                    }

                    if(yesNoIn.equals("n")) continue; //Go back to showing all materials

                    quantity = -1;
                    while (quantity < 0){
                        try {
                            quantity = readInputQuantity();
                        } catch (NegativeQuantityException NQe) {
                            System.out.println(NQe.getMessage());
                        }
                    }
                    try {
                        Organization.findDonator(phone).addToOffersList(new RequestDonation(selectedEntity, quantity));
                    } catch (NegativeQuantityException NQe) {}
                }
            }
              
        return false; //if all else fails, go back to previous menu            
    }

    /**
     * @return Returns true if method needs to be rerun, returns false if escape
     */  
    private static boolean DonatorShowOffersMenu(String phone) { 
        int offersListSize = Organization.findDonator(phone).getOffersListSize();

        Organization.findDonator(phone).monitorOffers();
        System.out.println("║ " + (offersListSize + 1) + ".Clear all requests\n"
                        + "║ " + (offersListSize + 2) + ".Commit\n"  
                        + "║ " + (offersListSize + 3) + ".Back"); 
        System.out.println("╚" + "═".repeat(49));

        int inChoice = -1;
        while(inChoice == -1){
            try{
                inChoice = readInputIntRange(1, offersListSize+3);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
        }

        //Back
        if(inChoice==offersListSize+3) return false;

        if(inChoice==offersListSize+2){
            Organization.findDonator(phone).commit();
            return true;
        }

        if(inChoice==offersListSize+1){
            Organization.findDonator(phone).clearOffersList();
            return true;
        }

        //Else selection HAS to be an offer.
        while(true){
            System.out.println("╔" + "═".repeat(49));
            System.out.println(Organization.findDonator(phone).getNameQuantityAtIndex(inChoice-1));
            System.out.println("║ 1.Delete this offer\n"
                            + "║ 2.Change quantity\n"
                            + "║ 3.Back");
            System.out.println("╚" + "═".repeat(49));
        
            int inChoiceTwo = -1;
            while(inChoiceTwo == -1){
                try{
                    inChoiceTwo = readInputIntRange(1, 3);    
                } catch (MenuInputException MIe) {
                    System.out.println(MIe.getMessage());
                }
            }

            if(inChoiceTwo == 3) return true; //back is pressed.
            
            if(inChoiceTwo == 1){
                Organization.findDonator(phone).removeFromOffersList(inChoice-1);
                System.out.println("The donation has been deleted successfully.");
                return true; //return true, go back to showing everything
                
            } else if (inChoiceTwo == 2) {
                double quantity = -1;
                while (quantity < 0){
                    try {
                        quantity = readInputQuantity();
                    } catch (NegativeQuantityException NQe) {
                        System.out.println(NQe.getMessage());
                    }
                }
                
                Organization.findDonator(phone).modifyQuantity(inChoice-1, quantity);

                //after changing quantity don't break so you can stay at the same entity
            }
        }
        
    }
    
    /**@return Returns true if method needs to be rerun, returns false if escape
     * back to menuBasedOnInput, or lastly exit program   
     */ 
    private static boolean BeneficiaryMenu(String phone) {
        System.out.println("╔" + "═".repeat(38) + "╗");
        System.out.println("║" + "█".repeat(14) + " Welcome! " + "█".repeat(14) + "║");
        System.out.println("╚" + "═".repeat(38) + "╝");
        Organization.findAndPrintBeneficiary(phone);
        
        //ektupwsh epilogwn
        System.out.println("╔" + "═".repeat(24) + "╗");
        System.out.println("║" + " ".repeat(8) + "Options" + " ".repeat(9) + "║");
        System.out.println("╠" + "═".repeat(24) + "╣");
        System.out.println("║ 1.Add Requests         ║");
        System.out.println("║ 2.Show Requests        ║");
        System.out.println("║ 3.Commit               ║");
        System.out.println("║ 4.Back                 ║");
        System.out.println("║ 5.Logout               ║");
        System.out.println("║ 6.Exit                 ║");

        int inChoice = -1;
        //input
        do {
            try{
                inChoice = readInputIntRange(1, 6);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }

        } while (inChoice == -1);

        /**
         * Epilogh, h filosofia tou return true/false einai pws mexri na ginei return
         * false, theloume na epistrepsoume sto idio menu me ta idia stoixeia.
         * Opote otan ginetai mia epilogh apo ton xrhsth, 1-3 kai teleiwsei h
         * leitourgeia ths, o kwdikas sto menuBasedOnInput 8a ksanatreksei 
         * to idio UserMenu (opou User = Donator,Beneficiary,Admin)
         */
        switch(inChoice) {
            case 1:
                while(true){
                    if(!BeneficiaryAddRequestMenu(phone)) break;
                } 
                return true;

            case 2:
                while(true) {
                    if(!BeneficiaryShowRequestsMenu(phone)) break;
                }
                return true;

            case 3:
                Organization.findBeneficiary(phone).commit();
                return true;

            case 4:
                Organization.findBeneficiary(phone).clearRequestsListNoMessage();
                return false;

            case 5:
                Organization.findBeneficiary(phone).clearRequestsListNoMessage();
                return false;

            case 6:
                System.out.println("╔" + "═".repeat(36) + "╗");
                System.out.println("║" + " The program will now be terminated " + "║");
                System.out.println("╚" + "═".repeat(36) + "╝");
                System.exit(0);
                return false;
        }
        return false;
    }

    /**@return Returns true if method needs to be rerun, returns false if escape
    */
    private static boolean BeneficiaryAddRequestMenu(String phone) {
        //Proswrinh lista gia thn emfanish material/services ston xrhsth.
        RequestDonationList availableDonations = new RequestDonationList();

        System.out.println("╔" + "═".repeat(14) + "╗");
        System.out.println("║ Categories:  ║");
        System.out.println("╠" + "═".repeat(14) + "╣");
        System.out.println("║ 1.Materials  ║");
        System.out.println("║ 2.Services   ║");
        System.out.println("║ 3.Back       ║");
    
        int inChoice = -1;
        do {
            try{
                inChoice = readInputIntRange(1, 3);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
        } while (inChoice == -1); 

        if (inChoice == 3) return false;    //Return false aka. return to donmenu

        double quantity = 0;
        Entity selectedEntity = null;    
    
        switch(inChoice) {
            //Case for Materials selected
            case 1:
                //Display materials until back is pressed, then go back (rerun AddRequestMenu)
                while(true){
                    //Reload arraylist in case user viewed another category before
                    try {
                        availableDonations.reset();
                    } catch (EmptyListException ELe){}

                    availableDonations = getAvailabeDonationsOfType(1);

                    inChoice = -1;
                    availableDonations.monitor();
                    System.out.println("║ " + (availableDonations.getRdEntities().size()+1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));
                    
                    while(inChoice == -1){
                        try {
                            inChoice= readInputIntRange(1, availableDonations.getRdEntities().size() + 1);
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
                    }

                    //return true (go back a step) if back is pressed
                    if (inChoice == availableDonations.getRdEntities().size() + 1) return true;

                    selectedEntity = availableDonations.getWithIndex(inChoice-1).getEntity();
                    System.out.println(selectedEntity.toString());

                    String yesNoIn = "";
                    System.out.print("Would you like to receive quantity? (y/n): ");
                    yesNoIn = sc.nextLine();
                    
                    while(!(yesNoIn.equals("y")| yesNoIn.equals("n"))){
                        System.out.print("Please input a valid answer! (y/n): ");
                        yesNoIn = sc.nextLine();
                    }

                    if(yesNoIn.equals("n")) continue; //Go back to showing all materials

                    quantity = -1;
                    while (quantity < 0){
                        try {
                            quantity = readInputQuantity();
                        } catch (NegativeQuantityException NQe) {
                            System.out.println(NQe.getMessage());
                        }                    
                    }
                    
                    try {
                        Organization.findBeneficiary(phone).addToRequestList(new RequestDonation(selectedEntity, quantity));        
                    } catch (NegativeQuantityException NQe) {}           
                    
                }
              
            //Case for services selected
            case 2:    
                //Display services until back is pressed, then go back (rerun AddOfferMenu)
                while(true){
                    //Reload arraylist in case user viewed another category before
                    try {
                        availableDonations.reset();
                    } catch (EmptyListException ELe){}
                    availableDonations = getAvailabeDonationsOfType(2);

                    inChoice = -1;
                    availableDonations.monitor();
                    System.out.println("║ " + (availableDonations.getRdEntities().size()+1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));

                    while(inChoice == -1){
                        try {
                            inChoice = readInputIntRange(1, availableDonations.getRdEntities().size() + 1);
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
                    }

                    //return true (go back a step) if back is pressed
                    if (inChoice == availableDonations.getRdEntities().size() + 1) return true; 

                    selectedEntity = availableDonations.getWithIndex(inChoice-1).getEntity();
                    System.out.println(selectedEntity.toString());

                    String yesNoIn = "";
                    System.out.print("Would you like to receive quantity? (y/n): ");
                    yesNoIn = sc.nextLine();
                    
                    while(!(yesNoIn.equals("y")| yesNoIn.equals("n"))){
                        System.out.print("Please input a valid answer! (y/n): ");
                        yesNoIn = sc.nextLine();
                    }
                    if(yesNoIn.equals("n")) continue; //Go back to showing all materials

                    System.out.println("\nInput the quantity you would like to receive:");

                    quantity = -1;
                    while (quantity < 0){
                        try {
                            quantity = readInputQuantity();
                        } catch (NegativeQuantityException NQe) {
                            System.out.println(NQe.getMessage());
                        }
                    }
                    try {
                        Organization.findBeneficiary(phone).addToRequestList(new RequestDonation(selectedEntity, quantity));
                    } catch (NegativeQuantityException NQe) {}
                

                    
                }
        }

        return false; //if all else fails, go back to previous menu  
    }

    private static boolean BeneficiaryShowRequestsMenu(String phone) {
        int requestsListSize = Organization.findBeneficiary(phone).getRequestsListSize();

        Organization.findBeneficiary(phone).monitorRequests();
        System.out.println("║ " + (requestsListSize + 1) + ".Clear all requests\n"
                        + "║ " + (requestsListSize + 2) + ".Commit\n"  
                        + "║ " + (requestsListSize + 3) + ".Back"); 
        System.out.println("╚" + "═".repeat(49));

        int inChoice = -1;
        while(inChoice == -1){
            try{
                inChoice = readInputIntRange(1, requestsListSize+3);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
        }

        if(inChoice==requestsListSize+3) return false; //Back to previous menu

        if(inChoice==requestsListSize+2){
            Organization.findBeneficiary(phone).commit();
            return true;
        }

        if(inChoice==requestsListSize+1){
            Organization.findBeneficiary(phone).clearRequestsList();
            return true;
        }

        //Else selection HAS to be a request.
        while(true){
            System.out.println("╔" + "═".repeat(49));
            System.out.println(Organization.findBeneficiary(phone).getNameQuantityAtIndex(inChoice-1));
            System.out.println("╠" + "═".repeat(49));
            System.out.println("║ 1.Delete this request\n"
                            + "║ 2.Change quantity\n"
                            + "║ 3.Back");
            System.out.println("╚" + "═".repeat(49));
           
            int inChoiceTwo = -1;
            while(inChoiceTwo == -1){
                try{
                    inChoiceTwo = readInputIntRange(1, 3);    
                } catch (MenuInputException MIe) {
                    System.out.println(MIe.getMessage());
                }
            }

            if(inChoiceTwo == 3) return true; //back is pressed

            if(inChoiceTwo == 1){
                Organization.findBeneficiary(phone).removeFromRequestList(inChoice-1);
                System.out.println("The request has been deleted successfully.");
                return true; //return true so go back to showing everything
                
            } else if (inChoiceTwo == 2){
                double quantity = -1;
                while (quantity < 0){
                    try {
                        quantity = readInputQuantity();
                    } catch (NegativeQuantityException NQe) {
                        System.out.println(NQe.getMessage());
                    }
                }
                
                Organization.findBeneficiary(phone).modifyRequestQuantity(inChoice-1, quantity);
            
            }
        }
    }

    /**@return Returns true if method needs to be rerun, returns false if escape
     * back to menuBasedOnInput, or lastly exit program   
     */ 
    private static boolean AdminMenu(String phone) {
        System.out.println("╔" + "═".repeat(38) + "╗");
        System.out.println("║" + "█".repeat(14) + " Welcome! " + "█".repeat(14) + "║");
        System.out.println("╚" + "═".repeat(38) + "╝");
        Organization.printAdmin();
        
        //ektupwsh epilogwn
        System.out.println("╔" + "═".repeat(24) + "╗");
        System.out.println("║" + " ".repeat(8) + "Options" + " ".repeat(9) + "║");
        System.out.println("╠" + "═".repeat(24) + "╣");
        System.out.println("║ 1.View                 ║");      
        System.out.println("║ 2.Monitor Organization ║");
        System.out.println("║ 3.Back                 ║");
        System.out.println("║ 4.Logout               ║");
        System.out.println("║ 5.Exit                 ║");

        //input
        int inChoice = -1;
        do {
            try{
                inChoice = readInputIntRange(1, 5);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }

        } while (inChoice == -1);

        switch(inChoice) {
            case 1:
                while(true){
                    if(!AdminViewMenu()){
                        break;
                    };
                }
                
                return true;

            case 2:
                while(true){
                    if(!AdminMonitorOrgMenu()){
                        break;
                    };
                }
                return true;

            case 3:
                //back
                return false;

            case 4:
                //logout
                return false;
                
            case 5:
                System.out.println("╔" + "═".repeat(36) + "╗");
                System.out.println("║" + " The program will now be terminated " + "║");
                System.out.println("╚" + "═".repeat(36) + "╝");
                System.exit(0);
                return false;
        }
        return false;
    }
    /**
     * @return Returns true if needs to be rerun, else return false to go back a menu
     */
    private static boolean AdminViewMenu(){
        int inChoice = -1;

        do {
            System.out.println("╔" + "═".repeat(14) + "╗");
            System.out.println("║  Categories  ║");
            System.out.println("╠" + "═".repeat(14) + "╣");
            System.out.println("║ 1.Materials  ║");
            System.out.println("║ 2.Services   ║");
            System.out.println("║ 3.Back       ║");
                            
            try{
                inChoice = readInputIntRange(1, 3);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
            
        } while (inChoice == -1);

        switch (inChoice) {
            case 1:
                //While back option is not selected, allow user to select a material to see the info
                while (true){
                    //Get all availabematerials
                    var availabeMaterials = getAvailabeDonationsOfType(1);

                    inChoice = -1;
                    
                    availabeMaterials.monitor();
                    System.out.println("║ " + (availabeMaterials.getRdEntities().size()+1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));

                    try{
                        inChoice = readInputIntRange(1, availabeMaterials.getRdEntities().size()+1);    
                    } catch (MenuInputException MIe) {
                        System.out.println(MIe.getMessage());
                    }
                    
                    if(inChoice == availabeMaterials.getRdEntities().size()+1) break;

                    System.out.println(availabeMaterials.getWithIndex(inChoice-1).getEntity());
                }

                return true;
            
            case 2:
                //While back option is not selected, allow user to select a service to see the info
                while (true){
                    //Get all availabe services
                    var availabeServices = getAvailabeDonationsOfType(2);

                    inChoice = -1;
                    
                    availabeServices.monitor();
                    System.out.println("║ " + (availabeServices.getRdEntities().size()+1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));


                    try{
                        inChoice = readInputIntRange(1, availabeServices.getRdEntities().size()+1);    
                    } catch (MenuInputException MIe) {
                        System.out.println(MIe.getMessage());
                    }
                    
                    if(inChoice == availabeServices.getRdEntities().size()+1) break;

                    System.out.println(availabeServices.getWithIndex(inChoice-1).getEntity().toString());
                }

                return true;
            case 3:
                return false;
        }
        return false;
    }

    private static boolean AdminMonitorOrgMenu(){
        int inChoice = -1;

        do {
            System.out.println("╔" + "═".repeat(28) + "╗");
            System.out.println("║         Categories         ║");
            System.out.println("╠" + "═".repeat(28) + "╣");
            System.out.println("║ 1.List Beneficiaries       ║");      
            System.out.println("║ 2.List Donators            ║");
            System.out.println("║ 3.Reset Beneficiaries List ║");
            System.out.println("║ 4.Back                     ║");

            try{
                inChoice = readInputIntRange(1, 4);    
            } catch (MenuInputException MIe) {
                System.out.println(MIe.getMessage());
            }
            
        } while (inChoice == -1);

        switch (inChoice) {
            case 1:
                inChoice = -1;

                // While back is not pressed, always ask user to select a beneficiary and then go
                // into the options menu for that beneficiary
                while(true){
                    System.out.println("╔" + "═".repeat(49));
                    Organization.listBeneficiaries();
                    System.out.println("║ " + (Organization.totalBeneficiaries() + 1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));
                    do {
                        try{
                            inChoice = readInputIntRange(1, Organization.totalBeneficiaries() + 1 ); 
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
                    } while (inChoice == -1);
                    
                    if (inChoice == Organization.totalBeneficiaries() + 1) break;

                    //Show options for beneficiary until user presses back, then go back into selecting a beneficiary.
                    while (true){
                        if(!AdminMonitorOrgSelectedUserMenu(1, inChoice)) break;
                    }
                }
                
                return true;
            case 2:
                inChoice = -1;

                // While back is not pressed, always ask user to select a donator and then go
                // into the options menu for that donator
                while(true){
                    System.out.println("╔" + "═".repeat(49));
                    Organization.listDonators();
                    System.out.println("║ " + (Organization.totalDonators() + 1) + ".Back");
                    System.out.println("╚" + "═".repeat(49));
                    
                    do {
                        try{
                            inChoice = readInputIntRange(1, Organization.totalDonators() + 1 ); 
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
                    } while (inChoice == -1);
                    
                    if (inChoice == Organization.totalDonators() + 1) break;

                    //Show options for donator until user presses back, then go back into selecting a donator.
                    while (true){
                        if(!AdminMonitorOrgSelectedUserMenu(2, inChoice)) break;
                    }
                }
                
                return true;
            case 3:
                //If all lists were empty, show a different message than if a single one was not.
                boolean allListsEmpty = true;
                for(int i=0; i < Organization.totalBeneficiaries(); i++){
                    if(Organization.clearBeneficiaryReceivedListNoMessage(i)){
                        allListsEmpty = false;
                    }
                }
                if(allListsEmpty){
                    System.out.println("All lists were already empty");
                } else System.out.println("The lists have been cleared sucessfuly!");
                return true;
            case 4:
                return false;
        }
        return false;
    }

    /**
     * @param userType 1 for Beneficiaries, 2 for Donators.
     * @return true, until user presses back button, then false
     */
    private static boolean AdminMonitorOrgSelectedUserMenu(int userType, int indexOfUser){
        int inChoice = -1;

        // if 1 == beneficiary, else 2 == 2 donator, show needed actions
        if (userType == 1){
            do {
                System.out.println("╔" + "═".repeat(37) + "╗");
                System.out.println("║ 1.Clear beneficiary's received list ║");
                System.out.println("║ 2.Delete beneficiary                ║");
                System.out.println("║ 3.Back                              ║");
                System.out.println("╚" + "═".repeat(37) + "╝");

                try{
                    inChoice = readInputIntRange(1, 3);
                } catch (MenuInputException MIe) {
                    System.out.println(MIe.getMessage());
                }
            } while (inChoice == -1);
            if(inChoice == 3) return false;

            if(inChoice == 1){
                try {
                Organization.clearBeneficiaryReceivedList(indexOfUser-1);
                } catch (EmptyListException ELe) {
                    System.out.println(ELe.getMessage());
                }
            
            } else if (inChoice == 2){
                Organization.removeBeneficiary(indexOfUser-1);
                return false;
                //return false because the beneficiary doesn't exist anymore.
            }
            
            // Return true so this method repeats itself, aka show options again.
            return true;

        } else if (userType == 2){
            do {
                System.out.println("╔" + "═".repeat(18) + "╗");
                System.out.println("║ 1.Delete donator ║");
                System.out.println("║ 2.Back           ║");
                System.out.println("╚" + "═".repeat(18) + "╝");
                                  
                try{
                    inChoice = readInputIntRange(1, 2);
                } catch (MenuInputException MIe) {
                    System.out.println(MIe.getMessage());
                }
            } while (inChoice == -1);
            
            if(inChoice == 2) return false;

            if(inChoice == 1){
                Organization.removeDonator(indexOfUser-1);
                return false;
                //return false because the donator doesn't exist anymore.
            }
            
            //return to previous menu, error-protection, for the most part
            //irrelevant because you either pressed back -> false or deleted -> false.
            return false;

        }
        //If all else fails, return to previous menu.
        return false;
    }

    /**@return Returns true if method needs to be rerun, returns false if escape
     * to program exit or finally there is the possibility of immediately 
     * exiting the program.
     */
    private static boolean menuBasedOnInput(String phone) {
        sc = new Scanner(System.in);

        switch (Organization.searchPhone(phone)) {
            case 0:
                System.out.print("You are not a registered user, would you like to register? Press (y) to begin registration " +
                "or any other key to go back. ");  

                if (sc.nextLine().equals("y")) {
                    System.out.print("Would you like to register as a beneficiary (1) or donator (2)? ");
        
                    int inChoice = -1;
                    do {
                        try{
                            inChoice = readInputIntRange(1, 2);    
                        } catch (MenuInputException MIe) {
                            System.out.println(MIe.getMessage());
                        }
            
                    } while (inChoice == -1);
                    
                    String inName = new String();
                    switch (inChoice) {
                        case 1:
                            System.out.print("Please enter your name: "); 
                            //inName = sc.next();
                            inName = sc.nextLine();
                            Organization.insertBeneficiary(new Beneficiary(inName, phone));
                            System.out.println("Your registration as a beneficiary has been completed!\n");
                            while(true){
                                if(!BeneficiaryMenu(phone)){
                                    break;
                                };
                            }
                            return true;

                        case 2:
                            System.out.print("Please enter your name: ");
                            // inName = sc.next();
                            inName = sc.nextLine();
                            Organization.insertDonator(new Donator(inName, phone));
                            System.out.println("Your registration as a donator has been completed!\n");
                            while(true){
                                if(!DonatorMenu(phone)){
                                    break;
                                };
                            } 
                            return true;

                        default:
                            System.out.println("Cancelling registration...");
                            return true;

                    }
                } else {
                    System.out.println("Cancelling registration...\n");
                    return true;
                }

            case 1:
                while(true){
                    if(!BeneficiaryMenu(phone)){
                        break;
                    };
                }
                return true;

            case 2:
                while(true){
                    if(!DonatorMenu(phone)){
                        break;
                    };
                }
                return true;

            case 3:
                while(true){
                    if(!AdminMenu(phone)){
                        break;
                    }
                }
                return true;
        }
        //sc.close();
        return false;
    }   

    /**
     * @return Returns the input if it was valid, else returns -1 (handled externally with a do-while check)
     * @throws MenuInputException
     */
    private static int readInputIntRange(int lowerBound, int upperBound) throws MenuInputException{
        int inChoice = -1;
        
        try {
        inChoice = sc.nextInt();
        sc.nextLine();
        // nextLine is needed because of problems when reading a String line (eg. the name)
            if (inChoice > upperBound || inChoice < lowerBound){
                inChoice = -1;
                throw new MenuInputException();   
            }

        } catch (InputMismatchException IMe) {
            inChoice = -1;
            System.out.println("\nYou entered characters! Please input a valid integer!");
            sc.next();
        }
        return inChoice;
    }

    private static double readInputQuantity() throws NegativeQuantityException{    
        double quantity = -1;
         
        try {
            System.out.print("\nInput quantity: ");
            quantity = Double.parseDouble(sc.next());
        } catch (NumberFormatException NFe) {
            System.out.println("Please insert a valid number!");
            return -1;
        } 
        
        if(quantity < 0) throw new NegativeQuantityException();

        return quantity;     
    }

    /**
     * @param choice 1 for Material, 2 for Service
     * @return Returns a requestdonationlist that contains every material/service depending on choice,
     * if it doesn't exist currently in the organization currentDonations, it adds it with 0 quantity.
     */
    private static RequestDonationList getAvailabeDonationsOfType(int choice){
        var entityListOneTypeOnly = Organization.getEntityListCopy();
        var availabeRDOfType = Organization.getCurrentDonationsCopy();
        
        Iterator<RequestDonation> rDIter = availabeRDOfType.getRdEntities().iterator();
        Iterator<Entity> entIter = entityListOneTypeOnly.iterator();

        switch(choice){
            case 1:
                //Get a copy of all donations, remove everything that's not a material
                while(rDIter.hasNext()){
                    Entity ent = rDIter.next().getEntity();
                    if (ent.getDetails().equals("Service")) rDIter.remove();
                }

                //next, do the same but for the entities
                while(entIter.hasNext()){
                    Entity ent = entIter.next();
                    if (ent.getDetails().equals("Service")){
                        entIter.remove();
                        continue;
                    } 
                    
                    //If entity doesn't exist as RD in availabe materials, add it with 0 quant.
                    if(!availabeRDOfType.isInRequestDonationList(ent.getId())){
                        availabeRDOfType.add(new RequestDonation(ent, 0));
                    }
                }
                return availabeRDOfType;

            case 2:
                //Get a copy of all donations, remove everything that's not a service
                while(rDIter.hasNext()){
                    Entity ent = rDIter.next().getEntity();
                    if ( !(ent.getDetails().equals("Service"))) rDIter.remove();
                }

                //next, do the same but for the entities
                while(entIter.hasNext()){
                    Entity ent = entIter.next();
                    if ( !(ent.getDetails().equals("Service"))) {
                        entIter.remove();
                        continue;
                    }
                    
                    
                    //If entity doesn't exist as RD in availabe services, add it with 0 quant.
                    if(!availabeRDOfType.isInRequestDonationList(ent.getId())){
                        availabeRDOfType.add(new RequestDonation(ent, 0));
                    }
                } 
            return availabeRDOfType;      
        } 
        
        //In case of total failure, should never happen.
        return null;    
    }
}