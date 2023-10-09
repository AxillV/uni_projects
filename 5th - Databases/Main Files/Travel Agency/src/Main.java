public class Main {
    public static void main(String[] args){

        IDandPasswords idandPasswords = new IDandPasswords();
        new LoginFrame(idandPasswords.getLoginInfo());
    }
}


