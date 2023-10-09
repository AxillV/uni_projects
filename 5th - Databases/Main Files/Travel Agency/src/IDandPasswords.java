import java.sql.ResultSet;
import java.util.HashMap;

public class IDandPasswords {
    HashMap<String,String> loginInfo = new HashMap<String,String>();

    IDandPasswords(){
        try {
            ResultSet userIDs = Methods.getTableData("it_AT", "it", null);         //Vriskw ta it_AT
            String query = "SELECT wrk_lname FROM worker WHERE ";
            while(userIDs.next()) {
                query = query + " wrk_AT = '" + userIDs.getString("it_AT") + "' OR";        //Vriskw ta epwnunma twn IT staff
            }
            query = query.substring(0, query.length() - 3);
            ResultSet userNames = Methods.customQueryReturn(query);             //Pairnw ta epwnuma kai ta password twn IT staff kai ta vazw sta login info.
            ResultSet passwords = Methods.getTableData("it_password", "it", null);
            while (userNames.next() && passwords.next()) {
                loginInfo.put(userNames.getString("wrk_lname"), passwords.getString("it_password"));
            }
        }
        catch (Exception e){
            System.out.println("Error, please check your input");
            e.printStackTrace();
        }
    }
    public HashMap<String,String> getLoginInfo(){
        return loginInfo;
    }

}
