public class Admin extends User{
    
    private boolean isAdmin = true;

    Admin(String name, String phone) {
        super(name, phone);
    }
}