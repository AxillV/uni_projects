import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

//for list of foreign keys
import java.util.*;

public class Methods implements ActionListener, ListSelectionListener, EventListener {

    public static JLabel datetimeLabel;
    public static JLabel questionLabel;
    public static int counterStatic = 0;
    static boolean first=true;
    private static int columnCounterStatic = 0;
    public static int selColumnStatic;
    public static int selRowStatic;
    static JComboBox[] chooseDateBox = new JComboBox[7];    //Gia to trip info button
    static JComboBox<String> chooselName;
    private static String primaryName;
    private static String selectedlName;
    private static String tableStatic;
    //private static String[] columnNameStatic = new String[10];
    public static ArrayList<String> columnNameStatic = new ArrayList<String>();
    private static String[] columnTypeStatic = new String[10];
    public static JLabel columnLabel = new JLabel();
    private static int[] dropdownArray = new int[3];
    //https://stackoverflow.com/questions/16213836/java-swing-jtextfield-set-placeholder
    public static ArrayList<JTextField> fields = new ArrayList<JTextField>() {
        {
            add(new JTextField("First"));
        }
    };

    static JButton submitButton = new JButton("Submit");
    static JButton submitITButton = new JButton("Submit");
    static JButton submitDateButton = new JButton("Submit Date");
    static JButton deleteButton = new JButton("Delete");
    private static String valueStatic[] = new String[10];
    private static JFrame frameStatic;
    static boolean changed=false;
    static boolean changedFeb=false;

    static JScrollPane scrollPane = new JScrollPane();
    static JScrollPane scrollPaneSalary = new JScrollPane();

    static JLabel salaryLabel = new JLabel();
    static public ArrayList<JComboBox<String>> chooseKey = new ArrayList<JComboBox<String>>();

    //https://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html
    //https://stackoverflow.com/questions/18716651/get-all-foreign-keys-using-jdbc
    static public ArrayList<String> getFKeyData(String tableName, int i) throws SQLException {
        ArrayList<String> fkTableData = new ArrayList<String>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root","root");
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getImportedKeys(null, null, tableName);
            while (rs.next()) {
                fkTableData.add(rs.getString(i));
            }
        }
        catch (Exception e) {
            System.out.println("Error, please check your input.");
            e.printStackTrace();
        }
        return fkTableData;
    }


    //Custom query. Returns rows as ResultSet
    public static ResultSet customQueryReturn(String query){     //Pairnei ws orisma ena String query, trexei thn entolh sthn mysql kai epistrefei to resultSet
        ResultSet resultSet = null;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root","root");  //Kanei connection me to database
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  //Scroll Insensitive gia na mporoume na ksanagurisoume sthn arxh tou result set
            resultSet = statement.executeQuery(query);     //Stelnoume to statement sto database mesw tou jdbc
        }
        catch (Exception e) {
            System.out.println("Error, please check your input.");
            e.printStackTrace();
        }
        return resultSet;
    }

    public static void customQuery(String query){     //To idio me thn proigoumenh apla den epistrefei resultSet (executeUpdate anti gia executeQuery)
        boolean isException = false;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root", "root");
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch (Exception e) {
            System.out.println("Error, please check your input.");
            isException = true;
            e.printStackTrace();
        }
        
        if (!isException)
            System.out.println("Database has been updated!");

    }

    //SELECT field FROM table [WHERE where]. Returns query's rows as ResultSet
    public static ResultSet getTableData(String field, String table, String where) { //Ama to where einai null tote trexei xwris where
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root", "root");
            Statement statement = connection.createStatement();
            String query;
            if (where == null)
                //TODO: Wrap all queries with '' so spaces are allowed
                query = "SELECT " + field + " FROM " + table;
            else
                query = "SELECT " + field + " FROM " + table + " WHERE " + where;


            ResultSet resultSet = statement.executeQuery(query);

            return resultSet;

        } catch (Exception e) {
            System.out.println("Error, please check your input.");
            e.printStackTrace();
        }
        return null;
    }

    //UPDATE table
    public static void updateTableData(String table, String columnName, String columnType,String newValue, String condition) {    //Kanei update to mysql database
        String query;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root","root");
            Statement statement = connection.createStatement();

            query = "UPDATE " + table + " SET " + columnName + " = '" + newValue + "' WHERE" + condition;

            statement.executeUpdate(query);
            System.out.println("The table changed");
        } catch (SQLException throwables) {
            //https://docs.oracle.com/javase/6/docs/api/java/sql/SQLException.html#getSQLState()
            System.out.println(throwables.getMessage());
            System.out.println("Error, please check your input.");
            throwables.printStackTrace();
        }
    }


    public static void insertTableData(String table, JFrame frame) {         //Vazoume to table kai meta vazoume values

        chooseKey.removeAll(chooseKey);     //Vgazoume oti uphrxe apo proigoumeno treksimo ths insertTableData
        chooseKey.trimToSize();             //Prosarmozoume to megethos
        dropdownArray = new int[] {0, 0, 0, 0};   //Arxikopoihsh dropdownArray to opoio krataei poia fields mporoun na ginoun me dropdown
        tableStatic = table;                   //To xrhsimopoiw sto button
        submitButton = new JButton("Submit");
        submitButton.setBounds(590, 750, 100, 50);
        submitButton.setFocusable(true);
        frame.add(submitButton);
        frame.setVisible(true);
        submitButton.addActionListener(new Methods());
        int count=0;
        String names="";

        try {
            //List that contains all special cases.
            //If column is detected to be a foreign key (found in arraylist containing the foreignkeys) then dropdown. Else normal field
            //https://stackoverflow.com/questions/16213836/java-swing-jtextfield-set-placeholder
            //https://stackoverflow.com/questions/33799800/java-local-variable-mi-defined-in-an-enclosing-scope-must-be-final-or-effective
            ResultSet resultSet = getTableData("*",table,null);         //Pairnw plhrofories gia to table
            ResultSetMetaData rsmd = resultSet.getMetaData();

            counterStatic = rsmd.getColumnCount();         //Vriskoume posa columns exoume
            frame.setVisible(true);

            //Grabs referenced table name
            ArrayList<String> referenced_tables = getFKeyData(table,3);
            //Grabs key column name from referenced table
            ArrayList<String> referenced_table_column = getFKeyData(table,4);
            //Grabs key column name from current table
            ArrayList<String> current_table_column = getFKeyData(table,8);
            //int keyRowCount = referenced_tables.size();

            //Fields where user types
            int i = 0;
            for (i = 1; i <= counterStatic; i++) {
                fields.add(i,new JTextField(rsmd.getColumnName(i)));       //Vazoume ta fields sta opoia tha grafei o xrhsths
                columnNameStatic.add(rsmd.getColumnName(i));
                fields.get(i).setBounds(490, 430 + i *32, 200, 25);
                //workaround
                final int inner_i = i;
                String columnName = rsmd.getColumnName(i);


                int indexOfKey = current_table_column.indexOf(columnName);   //Pairnoume to index tou kleidiou

                //column is foreign key
                if (indexOfKey != -1) {     //An einai kleidi, tote kanw dropdown lista me auto (JComboBox chooseKey)
                    chooseKey.add(new JComboBox<String>());

                    dropdownArray[count] = i;

                    //Ftiaxnw ta onomata twn jcombobox me ena JLabel
                    count++;
                    if(count==1) {                              //Gia to prwto jcombobox ftiaxnw ta bounds kai to alignment, arxikopoiw to string names
                        names=columnName;
                        columnLabel.setText(names);
                        columnLabel.setVerticalAlignment(SwingConstants.TOP);
                        columnLabel.setBounds(410, 433 + i * 32, 200, 300);
                    }
                    else{                                                               //Tis epomenes fores apla prosthetw sto names newlines kai to columnName
                        names ="<html><body>" + names + "<br/> <br/>" +columnName + "<html>";
                        columnLabel.setText(names);
                    }
                    //these are the values that can be selected from combo box
                    resultSet = getTableData(referenced_table_column.get(indexOfKey), referenced_tables.get(indexOfKey), null);  //Pairnoume tis times pou mporei na parei to kleidi

                    while (resultSet.next()) {
                        chooseKey.get(chooseKey.size()-1).addItem(resultSet.getString(1));  //Prosthetoume ta item sto jComboBox
                    }


                    chooseKey.get(chooseKey.size()-1).setBounds(490, 430 + i * 32, 200, 25);
                    frame.add(chooseKey.get(chooseKey.size()-1));
                    frame.add(columnLabel);
                    frame.setVisible(true);
                } else {
                    fields.get(i).addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            if (fields.get(inner_i).getText().equals(columnName)) {
                                fields.get(inner_i).setText("");
                                //fields[inner_i].setForeground(Color.BLACK);
                            }
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            if (fields.get(inner_i).getText().isEmpty()) {
                                fields.get(inner_i).setText(columnName);
                            }
                        }
                    });

                    frame.add(fields.get(i));
                    // frame.add(fields[i]);
                    frame.setVisible(true);
                    columnTypeStatic[i] = rsmd.getColumnTypeName(i);
                }


            submitButton.setBounds(590, 430 + i*32 + 50, 100, 50);
            }

        } catch (Exception e) {
            System.out.println("Error, please check your input.");
            e.printStackTrace(); 
        }
    }
    

    //When "update table" or "delete row" is pressed
    public static void updateTableButton(String table, JFrame frame, boolean delete) {  //Xrhsimopoieitai sto update data koumpi
        int i = 0;
        tableStatic=table;
        String[] columnNames;
        String[][] rowInfo;

        //delete row
        if(delete){
            tableStatic = table;                   //To xrhsimopoiw sto button
            deleteButton = new JButton("Delete Row");      //Ftiaxnoume neo button
            deleteButton.setBounds(807, 700, 100, 50);
            deleteButton.setFocusable(true);
            frame.add(deleteButton);
            frame.setVisible(true);
            SwingUtilities.updateComponentTreeUI(frame);
            deleteButton.addActionListener(new Methods());
        }


        try {
            //Ftiaxnw connection me to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travel_agency", "root","root"); //Den kalesa apla thn getTableData giati xreiazomai to connection gia na vrw to unique key
            Statement statement = connection.createStatement();

            //Vriskw posa rows exw
            String query = "SELECT COUNT(*) FROM " + table;
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            int rowCount = resultSet.getInt(1);

            //Vriskw to primary key
            DatabaseMetaData dbmd = connection.getMetaData();
            try (ResultSet rs = dbmd.getIndexInfo(null, null, table, true, true)) {
                rs.next();
                primaryName = rs.getString("COLUMN_NAME");
            }


            //Pairnoume ton pinaka se afksousa seira tou primary key (to kanw gia na mporw na vrw meta poia timh tha allaksw sto database)
            query = "SELECT * FROM " + table + " ORDER BY " + primaryName + " ASC";
            resultSet = statement.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();



            columnCounterStatic = rsmd.getColumnCount();         //Vriskoume posa columns exoume
            rowInfo = new String[rowCount][columnCounterStatic];

            columnNames = new String[columnCounterStatic];
            for(i=0;i<columnCounterStatic;i++){
                columnNames[i] = rsmd.getColumnName(i+1);     //Vazoume ta onomata twn columns ston pinaka columnNames
                columnNameStatic.add(columnNames[i]);
                columnTypeStatic[i] = rsmd.getColumnTypeName(i+1);
            }
            for(i=0;i<rowCount;i++){
                resultSet.next();
                for(int j=0;j<columnCounterStatic;j++){
                    rowInfo[i][j]=resultSet.getString(columnNames[j]);       //Vazoume ta data sthn lista rowInfo pou tha xrhsimopoihsoume gia to JTable
                }
            }

            //Ftiaxnoume to table kai to pane kai to vazoume sto frame, yo
            JTable jTable = new JTable(rowInfo,columnNames);
            jTable.setPreferredScrollableViewportSize(new Dimension(50,63));
            jTable.setFillsViewportHeight(true);
            if(delete){     //Periptwsh pou thelw na diagrapsw
                jTable.setCellSelectionEnabled(false);         //Den afhnw ta kelia na einai editable
                jTable.setRowSelectionAllowed(true);
            }
            scrollPane = new JScrollPane(jTable);
            scrollPane.setBounds(400, 250, 507, 300);
            SwingUtilities.updateComponentTreeUI(frame);    //Kanoume refresh gia na fugei oti exei ksemeinei
            frame.add(scrollPane);


            //Action listener gia to jTable, pairnei to row kai to column otan epilegw kapoio kelh
            jTable.getSelectionModel().addListSelectionListener(e -> {
                int selColumn = jTable.getSelectedColumn();
                int selRow = jTable.getSelectedRow();
                selColumnStatic=selColumn;
                selRowStatic=selRow;
                int j;
                for(j=0;j<columnCounterStatic;j++) {
                    valueStatic[j] = (String) jTable.getValueAt(selRow, j);
                }
            });

            //Se periptwsh pou exw pathsei to update button
            if(!delete) {
                String[] finalColumnNames1 = columnNames;        //Den me afhne xwris an to kanw final giati einai lamda expression
                jTable.getModel().addTableModelListener(e -> {         //Energopoieitai otan teleiwsw na kanw tropopoihsh ena kelh
                    String condition=" ";     //Tha einai o elegxos gia thn WHERE sto query ths mysql
                    int j;
                    for(j=0;j<columnCounterStatic;j++){
                        if(valueStatic[j]!=null) {
                            condition = condition + columnNames[j] + "='" + valueStatic[j] + "'";
                            if (columnCounterStatic - 1 > j && valueStatic[j+1]!=null) //An den eimai sto teleutaio column vazw AND
                                condition = condition + " AND ";


                        }
                    }

                    if (valueStatic[selColumnStatic] != null && jTable.getValueAt(selRowStatic, selColumnStatic) != null) {                  //Periptwsh pou den exw null value prin h meta thn tropopoihsh,
                        if (!valueStatic[selColumnStatic].equals((String) jTable.getValueAt(selRowStatic, selColumnStatic))) {     //Elegxw an allakse h timh pou mphke sto kelh einai idia me auth pou htan hdh mesa
                            try {
                                updateTableData(table, finalColumnNames1[selColumnStatic], rsmd.getColumnTypeName(selColumnStatic+1), (String) jTable.getValueAt(selRowStatic, selColumnStatic), condition);
                                valueStatic[selColumnStatic]=(String) jTable.getValueAt(selRowStatic, selColumnStatic);    //Enhmerwnw ton pinaka me ta values tou table
                            } catch (SQLException throwables) {
                                System.out.println(throwables.getMessage());
                                System.out.println("Error, please check your input.");
                                throwables.printStackTrace();
                            }
                        }
                    } else                //Periptwsh pou eixa null value, tote den kanw check an h proigoumenh timh einai idia me prin
                        try {
                            updateTableData(table, finalColumnNames1[selColumnStatic], rsmd.getColumnTypeName(selColumnStatic+1), (String) jTable.getValueAt(selRowStatic, selColumnStatic), condition);
                            valueStatic[selColumnStatic]=(String) jTable.getValueAt(selRowStatic, selColumnStatic);
                        } catch (SQLException throwables) {
                            System.out.println(throwables.getMessage());
                            System.out.println("Error, please check your input.");
                            throwables.printStackTrace();
                        }
                });
            }



        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            System.out.println("Error, please check your input.");
            throwables.printStackTrace();
        }


    }

    public static void logTableButton(JFrame frame) {
        String[] columnNames;
        String[][] rowInfo;

        try {
            ResultSet resultSet = getTableData("COUNT(*)", "log", null);    //Vriskw posa rows exw sto log
            resultSet.next(); //move cursor to start
            int rowCount = resultSet.getInt(1);

            resultSet = getTableData("lg_tableName 'Table', lg_actionName Action, lg_actionNum ID, lg_wrk_lname Lastname, lg_timeofchange Datetime", "log", null);
            //resultSet.next(); //Dont do next before getmetadata! (stored before first index)
            //https://bugs.mysql.com/bug.php?id=40256
            //https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-connp-props-jdbc-compliance.html
            ResultSetMetaData rsmd = resultSet.getMetaData();
            columnCounterStatic = rsmd.getColumnCount();
            rowInfo = new String[rowCount][columnCounterStatic];

            //Store column names
            columnNames = new String[columnCounterStatic];    //Vriskw ta onomata twn columns
            for(int i=0;i<columnCounterStatic;i++){
                columnNames[i] = rsmd.getColumnLabel(i+1);
            }

            //Fill every row first, then column
            for(int i=0;i<rowCount;i++) {               //Arxikopoiw ton pinaka rowInfo pou exei ta dedomena twn rows
                resultSet.next();
                for(int j=0;j<columnCounterStatic;j++){
                    rowInfo[i][j]=resultSet.getString(columnNames[j]);
                }
            }

            //Ftiaxnoume to table kai to pane kai to vazoume sto frame, yo
            JTable jTable = new JTable(rowInfo,columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {        //Den thelw na einai editable ta kelia
                    return false;
                }
            };
            jTable.setPreferredScrollableViewportSize(new Dimension(450,63));
            jTable.setFillsViewportHeight(true);
            jTable.setCellSelectionEnabled(false);
            jTable.setRowSelectionAllowed(false);

            scrollPane = new JScrollPane(jTable);
            scrollPane.setBounds(400, 250, 607, 300);
            SwingUtilities.updateComponentTreeUI(frame);
            frame.add(scrollPane);
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error, please check your input.");
            e.printStackTrace();
        }
    }

    public static void addITButton(JFrame frame) {
        submitITButton = new JButton("Submit");      //Ftiaxnoume neo button
        submitITButton.setBounds(590, 700, 100, 50);
        submitITButton.setFocusable(true);
        frame.add(submitITButton);
        frame.setVisible(true);
        submitITButton.addActionListener(new Methods());

        try {
            ResultSet resultSet = getTableData("*","it",null);         //Pairnw plhrofories gia to table
            ResultSetMetaData rsmd = resultSet.getMetaData();
            counterStatic = 8; //4 apo it kai 4 apo worker!
            frame.setVisible(true);

            for (int i = 1; i < 5; i++) {                //Ftiaxnoume fields gia na eisagei o xrhsths ta dedomena
                fields.add(i, new JTextField(rsmd.getColumnName(i)));
                columnNameStatic.add(rsmd.getColumnName(i));
                fields.get(i).setBounds(490, 400 + i * 32, 200, 25);

                //workaround
                final int inner_i = i;
                String columnName = rsmd.getColumnName(i);

                fields.get(i).addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (fields.get(inner_i).getText().equals(columnName)) {
                            fields.get(inner_i).setText("");
                        }

                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (fields.get(inner_i).getText().isEmpty()) {
                            fields.get(inner_i).setText(columnName);
                        }
                    }
                    });

                frame.add(fields.get(i));       //Vazoume ta fields sto frame
                frame.setVisible(true);
                columnTypeStatic[i] = rsmd.getColumnTypeName(i);

            }

            resultSet = getTableData("*","worker",null);         //Pairnw plhrofories gia to table
            rsmd = resultSet.getMetaData();
            frame.setVisible(true);

            for (int i = 1; i < 4; i++) {                //Ftiaxnoume fields gia na eisagei o xrhsths ta dedomena
                fields.add(i+4, new JTextField(rsmd.getColumnName(i+1)));
                columnNameStatic.add(rsmd.getColumnName(i+1));
                fields.get(i+4).setBounds(490, 400 + (i+4) * 32, 200, 25);

                //workaround
                final int inner_i = i+4;
                String columnName = rsmd.getColumnName(i+1);

                fields.get(i+4).addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (fields.get(inner_i).getText().equals(columnName)) {
                            fields.get(inner_i).setText("");
                        }
                    }
                    
        
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (fields.get(inner_i).getText().isEmpty()) {
                            fields.get(inner_i).setText(columnName);
                        }
                    }
                });

                frame.add(fields.get(i+4));

                frame.setVisible(true);
                columnTypeStatic[i+4] = rsmd.getColumnTypeName(i+1);

            }

            //wrk_br_code
            chooseKey.removeAll(chooseKey);     //Vgazoume oti uphrxe apo proigoumeno treksimo ths insertTableData
            chooseKey.trimToSize();             //Prosarmozoume to megethos
            chooseKey.add(new JComboBox<String>());     //Ftiaxnoume dropdown lista gia to branch code
            resultSet = getTableData("br_code", "branch ORDER BY br_code ASC", null);

            while (resultSet.next()) {
                chooseKey.get(chooseKey.size()-1).addItem(resultSet.getString(1));  //Vazoume ola ta branches sto JComboBox chooseKey
            }

            // i =4
            chooseKey.get(chooseKey.size()-1).setBounds(490, 400 + 8 * 32, 200, 25);
            frame.add(chooseKey.get(chooseKey.size()-1));
            frame.setVisible(true);
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error, please check your input.");
            e.printStackTrace();
        }
    }

    public static void branchInfoButton(JFrame frame) {
        String [] columnNames;
        String[][] rowInfo;

        SwingUtilities.updateComponentTreeUI(frame);    //Kanoume refresh gia na fugei oti exei ksemeinei

        try {


            ResultSet resultSet = getTableData("COUNT(*)",    //Pairnoume ton arithmo rows twn apotelesmatwn ths stored procedure
                    "(SELECT Branch, Street, City, Manager, SUM(Reservations) as Reservations, SUM(Profit) as Profit FROM (SELECT b.br_code Branch, CONCAT(b.br_street, ' ', b.br_num) Street, b.br_city City, CONCAT(w.wrk_name, ' ', w.wrk_lname) Manager, "
                            + "t.tr_id Trip, COUNT(r.res_tr_id) Reservations, tr_cost*COUNT(r.res_tr_id) Profit FROM branch b LEFT JOIN manages m ON b.br_code = m.mng_br_code LEFT JOIN worker w ON m.mng_adm_AT = w.wrk_AT LEFT JOIN trip t ON t.tr_br_code = b.br_code LEFT JOIN reservation r ON t.tr_id = r.res_tr_id "
                            + "GROUP BY tr_id) AS T GROUP BY branch) AS T2", null);
            resultSet.next();
            int rowCount = resultSet.getInt(1);


            //https://stackoverflow.com/questions/6367737/resultset-exception-set-type-is-type-forward-only-why
            //resultSet.beforeFirst();

            resultSet = getTableData("Branch, Street, City, Manager, SUM(Reservations) as Reservations, SUM(Profit) as Profit",
                    "(SELECT b.br_code Branch, CONCAT(b.br_street, ' ', b.br_num) Street, b.br_city City, CONCAT(w.wrk_name, ' ', w.wrk_lname) Manager, t.tr_id Trip, COUNT(r.res_tr_id) Reservations, tr_cost*COUNT(r.res_tr_id) Profit FROM branch b LEFT JOIN manages m ON b.br_code = m.mng_br_code LEFT JOIN worker w ON m.mng_adm_AT = w.wrk_AT LEFT JOIN trip t ON t.tr_br_code = b.br_code LEFT JOIN reservation r ON t.tr_id = r.res_tr_id  GROUP BY tr_id) AS T GROUP BY branch", null);

            ResultSetMetaData rsmd = resultSet.getMetaData();
            columnCounterStatic = rsmd.getColumnCount();  //vriskoume posa columns exoume


            rowInfo = new String[rowCount][columnCounterStatic];

            //https://bugs.mysql.com/bug.php?id=40256
            //https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-connp-props-jdbc-compliance.html
            columnNames = new String[columnCounterStatic];
            for (int i=0; i<columnCounterStatic; i++) {     //Arxikopoioume thn columnNames
                columnNames[i] = rsmd.getColumnLabel(i+1);
            }

            for(int i=0;i<rowCount;i++) {
                resultSet.next();
                for(int j=0;j<columnCounterStatic;j++){
                    rowInfo[i][j]=resultSet.getString(columnNames[j]);       //Vazoume ta data sthn lista rowInfo pou tha xrhsimopoihsoume gia to JTable

                    // EDGE CASE
                    if ((columnNames[j].equals("Reservations") || columnNames[j].equals("Profit")))
                        if (rowInfo[i][j] == null)
                            rowInfo[i][j] = "0";

                }
            }

            //Ftiaxnoume to table kai to pane kai to vazoume sto frame, yo
            JTable jTable = new JTable(rowInfo,columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            jTable.setPreferredScrollableViewportSize(new Dimension(450,63));
            jTable.setFillsViewportHeight(true);
            jTable.setCellSelectionEnabled(false);
            jTable.setRowSelectionAllowed(false);

            scrollPane = new JScrollPane(jTable);
            scrollPane.setBounds(400, 250, 607, 300);
            SwingUtilities.updateComponentTreeUI(frame);    //Kanoume refresh gia na fugei oti exei ksemeinei
            frame.add(scrollPane);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error, please check your input.");
            e.printStackTrace();
        }
    }

    public static void branchWorkers(JFrame frame, String branch) {
        String[] columnNames;
        String[][] rowInfo;
        int columnCount;
        int totalSalaries = 0;


        SwingUtilities.updateComponentTreeUI(frame);

        try {
            ResultSet resultSet = Methods.getTableData("COUNT(*)", "worker", "wrk_br_code = " + branch);  //Vriskoume posous worker exoume
            resultSet.next();
            int rowCount = resultSet.getInt(1);

            //Pairnei ths plhrofories pou zhtountai apo ton pinaka worker
            resultSet = Methods.getTableData("CONCAT(wrk_name, ' ', wrk_lname) as Fullname, wrk_salary Salary", "worker", "wrk_br_code = " + branch);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            columnCount = rsmd.getColumnCount();
            rowInfo = new String[rowCount][columnCount];

            columnNames = new String[columnCount];
            for(int i=0;i<columnCount;i++){
                columnNames[i] = rsmd.getColumnLabel(i+1);     //Vazoume ta onomata twn columns ston pinaka columnName
            }

            for(int i=0;i<rowCount;i++) {
                resultSet.next();
                for(int j=0;j<columnCount;j++){
                    rowInfo[i][j]=resultSet.getString(columnNames[j]);       //Vazoume ta data sthn lista rowInfo pou tha xrhsimopoihsoume gia to JTable

                    if (columnNames[j].equals("Salary")) {
                        totalSalaries += resultSet.getInt(columnNames[j]);
                    }
                }
            }

            //Ftiaxnoume to table kai to pane kai to vazoume sto frame, yo
            JTable jTable = new JTable(rowInfo,columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            jTable.setPreferredScrollableViewportSize(new Dimension(450,63));
            jTable.setFillsViewportHeight(true);
            jTable.setCellSelectionEnabled(false);
            jTable.setRowSelectionAllowed(false);

            scrollPaneSalary = new JScrollPane(jTable);     //Ftiaxnoume to scrollPane gia ton teliko mistho
            scrollPaneSalary.setBounds(400, 565, 607, 300);
            SwingUtilities.updateComponentTreeUI(frame);    //Kanoume refresh gia na fugei oti exei ksemeinei
            frame.add(scrollPaneSalary);

            salaryLabel.setText("Total salary expenses: " + totalSalaries);     //Settin up salaryLabel
            salaryLabel.setBounds(1070,700,200,35);
            salaryLabel.setFont(new Font(null, Font.BOLD,12));
            salaryLabel.setForeground(Color.RED);
            frame.add(salaryLabel);




        } catch (SQLException eSQL) {
            System.out.println(eSQL.getMessage());
            System.out.println("Error, please check your input.");
            eSQL.printStackTrace();
        }
    }
    public static void tripInfoButton(JFrame frame) throws SQLException {
        submitDateButton.doClick();
        int i;
        frameStatic=frame;
        datetimeLabel = new JLabel("DAY         MONTH       YEAR");     //Ftiaxnoume ta labels
        questionLabel = new JLabel("Branch Code:");

        datetimeLabel.setFont(new Font(null, Font.BOLD, 22));
        datetimeLabel.setBounds(812,350,340,450);
        questionLabel.setFont(new Font(null, Font.BOLD, 18));
        questionLabel.setBounds(1100,50,340,450);
        frame.add(datetimeLabel);
        frame.add(questionLabel);
        String[] days = new String[31];
        String[] months = new String[12];
        String[] years = new String[5];

        for(i=0;i<31;i++){
            days[i]=Integer.toString(i+1);          //Meres
        }
        for(i=0;i<12;i++){
            months[i]=Integer.toString(i+1);        //Mhnes
        }
        for(i=0;i<5;i++){
            years[i]=Integer.toString(i+2020);      //Xronia
        }


        ResultSet resultSet = customQueryReturn("SELECT COUNT(*) FROM branch");        //Vriskw posa branch exw
        resultSet.next();
        int count = resultSet.getInt(1);
        String[] branchIDs = new String[count];             //Ftiaxnw pinaka gia ta branchIDs

        for(i=0;i<count;i++){
            branchIDs[i]=Integer.toString(i+1);
        }
        chooseDateBox[6] = new JComboBox<>(branchIDs);         //JComboBox me ta branch IDs
        chooseDateBox[6].setBounds(1100,300,50,50);
        frame.add(chooseDateBox[6]);

        for(i=0;i<2;i++){
            chooseDateBox[i] = new JComboBox<>(days);
            chooseDateBox[i].setBounds(800,600+i*100,100,50);     //Meres
            frame.add(chooseDateBox[i]);
            chooseDateBox[i+2] = new JComboBox<>(months);
            chooseDateBox[i+2].setBounds(920,600+i*100,100,50);     //Mhnes
            chooseDateBox[i+2].addActionListener(new Methods());
            frame.add(chooseDateBox[i+2]);
            chooseDateBox[i+4] = new JComboBox<>(years);
            chooseDateBox[i+4].setBounds(1040,600+i*100,100,50);        //Xronia
            frame.add(chooseDateBox[i+4]);
        }
        submitDateButton.setBounds(902,810,130,60);
        submitDateButton.addActionListener(new Methods());
        frame.add(submitDateButton);
        SwingUtilities.updateComponentTreeUI(frame);    //Kanoume refresh gia na fugei oti exei ksemeinei


        //ResultSet resultSet = customQueryReturn();
    }

    public static void clientOffersButton(JFrame frame){
        String[] lastNames = new String[0];
        frameStatic = frame;
        int rowCount = 0;
        int i;
        try {
            ResultSet resultSet = customQueryReturn("SELECT COUNT(DISTINCT rsof_lname) FROM reservation_offers");     //Vriskw posa rows exw apo onomata
            resultSet.next(); //move cursor to start
            rowCount = resultSet.getInt(1);
            resultSet = customQueryReturn("SELECT DISTINCT rsof_lname FROM reservation_offers");     //Vriskw ola ta last names pou uparxoun sto reservation offers
            lastNames = new String[rowCount];
            i = 0;
            while (resultSet.next()) {
                lastNames[i] = resultSet.getString("rsof_lname");          //Ftiaxnw pinaka me last names
                i++;
            }
        }
        catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            System.out.println("Error, please check your input.");
            throwables.printStackTrace();

        }



        chooselName = new JComboBox<String>(lastNames);                                 //Ftiaxnw drop down list me ola ta epwnuma

        if(first){             //Periptwsh pou kaleitai prwth fora to koumpi, thn xreiazomaste gia na arxikopoihsoume ton pinaka
            selectedlName = (String) chooselName.getSelectedItem();
            first=false;
        }

        chooselName.setSelectedItem(selectedlName);
        chooselName.addActionListener(new Methods());
        chooselName.setBounds(950,300,250,50);
        frame.add(chooselName);



        try {
            ResultSet resultSet = customQueryReturn("CALL findInfoByLastName('" + selectedlName + "')");        //Kanw call to stored procedure 3.1.3.4
            ResultSetMetaData rsmd = null;                       //MetaData gia na vrw posa columns exw kai ta onomata autwn
            rsmd = resultSet.getMetaData();
            columnCounterStatic = rsmd.getColumnCount();         //Vriskoume posa columns exoume


            String[] columnNames = new String[columnCounterStatic];
            for (i = 0; i < columnCounterStatic; i++) {
                columnNames[i] = rsmd.getColumnLabel(i + 1);     //Vazoume ta onomata twn columns ston pinaka columnNames
            }

            i=0;
            while (resultSet.next()){                   //Vriskoume posa rows exei to resultSet
                i++;
            }
            String[][] rowInfo = new String[i][columnCounterStatic];

            i=0;
            resultSet.beforeFirst();
            while (resultSet.next()) {                           //Vazoume ta data sthn lista rowInfo pou tha xrhsimopoihsoume gia to JTable
                for (int j = 0; j < columnCounterStatic; j++) {
                    rowInfo[i][j] = resultSet.getString(columnNames[j]);
                }
                i++;
            }
            JTable jTable = new JTable(rowInfo, columnNames);               //Ftiaxnw to table
            jTable.setPreferredScrollableViewportSize(new Dimension(50, 63));
            jTable.setFillsViewportHeight(true);

            scrollPane = new JScrollPane(jTable);                   //Ftiaxnw to pane gia na valw mesa to table
            scrollPane.setBounds(400, 210, 507, 300);
            jTable.setCellSelectionEnabled(false);
            jTable.setRowSelectionAllowed(true);
            frame.add(scrollPane);
        }
        catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            System.out.println("Error, please check your input.");
            throwables.printStackTrace();

        }

    }

    public static boolean contains(final int[] array, final int key) {        //Vriskei an uparxei ena key se ena array
        return Arrays.stream(array).anyMatch(i -> i == key);
    }


    @Override
//ACTION PERFORMED GIA TO SUBMIT BUTTON TOU INSERT DATA
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String query = "INSERT INTO " + tableStatic + " VALUES (";     //Ftiaxnw thn arxh tou query
            
            int k=0;
            for (int i = 1; i <=counterStatic; i++) {

                String columnText = fields.get(i).getText();
                

                if(contains(dropdownArray, i)){                 //Periptwsh pou exw dropdown array
                    query = query + "'" + chooseKey.get(k).getSelectedItem() + "'";
                    k++;
                }
                else if (fields.get(i).getText().equals("") || columnNameStatic.contains(columnText)) {      //Periptwsh pou den exw grapsei sto field
                    query = query + "null";
                }
                else
                    query = query + "'" + fields.get(i).getText().trim() + "'";

                if (i < counterStatic)
                    query = query + ",";
            }

            query = query + ");";
            try {
                customQuery(query);
            }
            catch (Exception e2){
                System.out.println("Error, please check your input.");
                e2.printStackTrace();
            }
        }

        if (e.getSource() == deleteButton) {
            String condition=" ";
            int j;
            for(j=0;j<columnCounterStatic;j++){
                if(valueStatic[j]!=null) {
                    condition = condition + columnNameStatic.get(j)/*columnNameStatic[j]*/ + "='" + valueStatic[j] + "'";
                    if (columnCounterStatic - 1 > j && valueStatic[j+1]!=null)
                        condition = condition + " AND ";
                }
            }
            //String condition = primaryName + " IN ( " + "SELECT " + primaryName + " FROM ( SELECT * FROM " + tableStatic + " ORDER BY " + primaryName + " ASC LIMIT " + selRowStatic + ",1)tmp);";
            String query="DELETE FROM " + tableStatic + " WHERE " + condition;
            customQuery(query);
        }



        if (e.getSource() == submitITButton) {

            // Insert into worker
            String query = "INSERT INTO worker VALUES (";   //Ftiaxnw thn arxh tou query
            query += "'" + fields.get(1).getText().trim() + "',";
            for (int i = 5; i <=counterStatic-1; i++) {
                String columnText = fields.get(i).getText();

                //TODO: at_IT doesnt get caught because real name is wrk_AT
                if (fields.get(i).getText().equals("") || columnNameStatic.contains(columnText)) {      //Periptwsh pou den exw grapsei sto field
                    query = query + "null";
                }
                else
                    query = query + "'" + fields.get(i).getText().trim() + "'";

                if (i < counterStatic)
                    query = query + ",";
            }
            //4th field (index = 8) is dropdown menu
            query = query + "'" + chooseKey.get(0).getSelectedItem() + "'";

            query = query + ");";
            try {
                customQuery(query);

            }
            catch (Exception e2){
                System.out.println("Error, please check your input.");
                e2.printStackTrace();
            }

            //Insert into IT
            query = "INSERT INTO it VALUES (";   //Ftiaxnw thn arxh tou query
            for (int i = 1; i <=counterStatic-4; i++) {

                if (i==3 && (fields.get(i).getText().equals("") || fields.get(i).getText().equals("it_date_start"))){     //Periptwsh null date start
                    fields.get(i).setText(String.valueOf(java.time.LocalDate.now()));
                }

                else if (i==4 && (fields.get(i).getText().equals("") || fields.get(i).getText().equals("it_date_end"))){     //Periptwsh null date end
                    query = query + "NULL";
                    continue; //TODO: continue doesn't enter a value for corresponding field?
                }

                //rest cases
                query = query + "'" + fields.get(i).getText().trim() + "'";
                if (i<counterStatic-4) {
                    query = query + ",";
                }
            }
            query = query + ");";
            try {
                customQuery(query);
            }
            catch (Exception e2){
                System.out.println("Error, please check your input.");
                e2.printStackTrace();
            }

        }


        //If month changes, change days
        else if (e.getSource() == chooseDateBox[2] || e.getSource() == chooseDateBox[4]) {
            chooseDateBox[0].removeAllItems();


            int selection = chooseDateBox[2].getSelectedIndex()+1;
            if (selection >= 1 && selection <= 7)
                for (int i=1; i<=30+(selection%2); i++)
                    chooseDateBox[0].addItem(i);

            else
                for (int i=1; i<=30+((selection-1)%2); i++)
                chooseDateBox[0].addItem(i);
    
                
            //FEBRUARY
            if (selection == 2) {
                int isLeapYear = 0;



                if ((chooseDateBox[4].getSelectedIndex()+2020)%4 == 0)
                    isLeapYear = 1;



                chooseDateBox[0].removeAllItems();
                for (int i=1; i<=28+isLeapYear; i++) {
                    chooseDateBox[0].addItem(i);
                }
            }
    
            }

            else if (e.getSource() == chooseDateBox[3] || e.getSource() == chooseDateBox[5]) {
                chooseDateBox[1].removeAllItems();
    

                int selection = chooseDateBox[3].getSelectedIndex()+1;
                if (selection >= 1 && selection <= 7)
                    for (int i=1; i<=30+(selection%2); i++)
                        chooseDateBox[1].addItem(i);
    
                else
                    for (int i=1; i<=30+((selection-1)%2); i++)
                    chooseDateBox[1].addItem(i);
        
                    
                if (selection == 2) {
                    int isLeapYear = 0;


                    if ((chooseDateBox[5].getSelectedIndex()+2020)%4 == 0)
                        isLeapYear = 1;



                    chooseDateBox[1].removeAllItems();
                    for (int i=1; i<=28+isLeapYear; i++) {
                        chooseDateBox[1].addItem(i);
                    }
                }
        
            }

        if(e.getSource()==submitDateButton) {
            frameStatic.remove(scrollPane);
            SwingUtilities.updateComponentTreeUI(frameStatic);    //Kanoume refresh gia na fugei oti exei ksemeinei
            String query;
            ResultSet resultSet;
            int i;
            int rowCount=0;
            String date1 = (chooseDateBox[4].getSelectedIndex() + 2020) + "-" + (chooseDateBox[2].getSelectedIndex() + 1) + "-" + (chooseDateBox[0].getSelectedIndex() + 1);
            String date2 = (chooseDateBox[5].getSelectedIndex() + 2020) + "-" + (chooseDateBox[3].getSelectedIndex() + 1) + "-" + (chooseDateBox[1].getSelectedIndex() + 1);
            query = "CALL viewTripsInRange(" + (chooseDateBox[6].getSelectedIndex() + 1) + ", '" + date1 + " 00:00:00', '" + date2 + " 00:00:00')";    //Kaloume thn stored procedure
            resultSet = customQueryReturn(query);
            ResultSetMetaData rsmd = null;
            try {
                while (resultSet.next()){           //Vriskw posa rows exoume
                    rowCount++;
                }

                rsmd = resultSet.getMetaData();
                columnCounterStatic = rsmd.getColumnCount();         //Vriskoume posa columns exoume

                String[][] rowInfo = new String[rowCount][columnCounterStatic];

                String[] columnNames = new String[columnCounterStatic];
                for (i = 0; i < columnCounterStatic; i++) {
                    columnNames[i] = rsmd.getColumnLabel(i + 1);     //Vazoume ta onomata twn columns ston pinaka columnNames
                    columnNameStatic.add(columnNames[i]);
                    //columnNameStatic[i] = columnNames[i];
                    columnTypeStatic[i] = rsmd.getColumnTypeName(i + 1);
                }
                i = -1;
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    i++;
                    for (int j = 0; j < columnCounterStatic; j++) {
                        rowInfo[i][j] = resultSet.getString(columnNames[j]);       //Vazoume ta data sthn lista rowInfo pou tha xrhsimopoihsoume gia to JTable
                    }
                }

                //Ftiaxnoume to table kai to pane kai to vazoume sto frame, yo
                JTable jTable = new JTable(rowInfo, columnNames);
                jTable.setPreferredScrollableViewportSize(new Dimension(50, 63));
                jTable.setFillsViewportHeight(true);

                scrollPane = new JScrollPane(jTable);
                scrollPane.setBounds(400, 210, 650, 300);
                SwingUtilities.updateComponentTreeUI(frameStatic);    //Kanoume refresh gia na fugei oti exei ksemeinei
                frameStatic.add(scrollPane);
            } catch (SQLException throwables) {
                System.out.println(throwables.getMessage());
                System.out.println("Error, please check your input.");
                throwables.printStackTrace();
            }
        }

        if(e.getSource()==chooselName){

            ItOptionsFrame.removeAll(ItOptionsFrame.counter,frameStatic);
            selectedlName = (String) chooselName.getSelectedItem();
            clientOffersButton(frameStatic);
        }


    }

    //Ta parakatw ulopoihhthhkan sthn UpdateTableButton
    @Override
    public void valueChanged(ListSelectionEvent e) {
    }
    @Override
    public void handleEvent(Event evt) {

    }
}