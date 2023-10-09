import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ItOptionsFrame implements ActionListener {
    //Diafora variables pou xreiazomai
    String selectedTable;
    String[] tables = new String[16];
    String [] branches;
    public static int counter;

    //Images gia omorfo frame
    ImageIcon image = new ImageIcon("Main Files/Travel Agency/img.png"); //To icon tou application
    ImageIcon icon = new ImageIcon("Main Files/Travel Agency/companyicon.png"); //Icon ths etairias

    //Ftiaxnw frame,labels
    JFrame frame = new JFrame();
    JLabel welcomeLabel = new JLabel();
    JLabel nameLabel = new JLabel();
    JLabel actionsLabel = new JLabel();

    //Ftiaxnw buttons
    JButton insertDataButton = new JButton("Insert Data");
    JButton updateDataButton = new JButton("Update Data");
    JButton deleteDataButton = new JButton("Delete Data");
    JButton seeTripDataButton = new JButton("See Trip Information");
    JButton logButton = new JButton("Access logs");
    JButton branchInfoButton = new JButton("Store info");
    JButton addITButton = new JButton("Add IT");
    JButton clientOfferButton = new JButton("See client's offer");

    //Combo boxes
    static JComboBox<String> chooseTableBox;
    static JComboBox<String> chooseBranchBox;


    //Constructor
    ItOptionsFrame(String userID){

        String query = "SET SQL_SAFE_UPDATES = 0;";   //vgazoume safe mode gia na mporoume na svhsoume tous current users
        Methods.customQuery(query);
        query = "SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));";     //Wste na mporoume na kanoume nested select gia branch info button
        Methods.customQuery(query);
        query = "DELETE FROM currentUser;";
        Methods.customQuery(query);
        query = "INSERT INTO currentUser values ('"+userID+"');";     //Vazoume ws current user to epwnumo tou xrhsth
        Methods.customQuery(query);


        ImageIcon background = new ImageIcon("Main Files/Travel Agency/background.png"); //To background tou frame
        //ImageIcon background = new ImageIcon("background.png"); //To background tou frame
        frame.setContentPane(new JPanel(new BorderLayout()) {
            @Override public void paintComponent(Graphics g) {
                g.drawImage(background.getImage(), 0, 0, null);
            }
        });


        //Ftiaxnw combobox pou deixnei ola ta tables gia to insert button
        //INIT CHOOSETABLEBOX (FOR TABLES)
        int i=0;
        String tablen;
        try {
            ResultSet tableSet = Methods.getTableData("table_name", "information_schema.tables", "table_schema = 'travel_agency';");        //Vriskei ola ta onomata twn tables
            while (tableSet.next()) {
                tablen = tableSet.getString("table_name");
                if(!tablen.equals("currentuser") && !tablen.equals("log")) {
                    tables[i] = tablen;       //Ta eisagei ston pinaka tables
                    i += 1;
                }
            }
        }
        catch (Exception e){
            System.out.println("Error, please check your input");
            e.printStackTrace();
        }
        chooseTableBox = new JComboBox<String>(tables);
        chooseTableBox.addActionListener(this);
        chooseTableBox.setBounds(950,300,250,50);


        //INIT CHOOSESTOREBOX (FOR BRANCH)
        i=0;
        try {
            //Get row count
            ResultSet branchSet = Methods.getTableData("count(*)", "travel_agency.branch", null);        //Vriskei ola ta onomata twn tables
            branchSet.next();
            int rowCount = branchSet.getInt(1);
            branches = new String[rowCount]; //dynamically allocated

            branchSet = Methods.getTableData("br_code", "travel_agency.branch", null);        //Vriskei ola ta onomata twn tables

            while (branchSet.next()) {
                branches[i] = branchSet.getString("br_code");        //Ta eisagei ston pinaka tables
                i++;
            }
        }
        catch (Exception e){
            System.out.println("Error, please check your input");
            e.printStackTrace();
        }

        chooseBranchBox = new JComboBox<String>(branches);
        chooseBranchBox.addActionListener(this);
        chooseBranchBox.setBounds(1100,650,100,50);

        //Diafora labels gia pio omorfo frame
        welcomeLabel.setText("Welcome IT user");
        welcomeLabel.setBounds(650,70,210,35);
        welcomeLabel.setFont(new Font(null, Font.BOLD,25));
        welcomeLabel.setForeground(Color.WHITE);

        nameLabel.setText(userID);
        nameLabel.setBounds(855,70,200,35);
        nameLabel.setFont(new Font(null, Font.BOLD,25));
        nameLabel.setForeground(Color.RED);

        actionsLabel.setText("What do you wanna do?");
        actionsLabel.setBounds(32,150,400,25);
        actionsLabel.setFont(new Font(null, Font.BOLD,25));
        actionsLabel.setForeground(Color.WHITE);

        //Vazoume ta buttons ekei pou theloume, tous vazoume action listener gia na akoune energeies
        insertDataButton.setBounds(10, 200, 150, 50);
        insertDataButton.setFocusable(false);
        insertDataButton.addActionListener(this);

        updateDataButton.setBounds(180, 200, 150, 50);
        updateDataButton.setFocusable(false);
        updateDataButton.addActionListener(this);

        deleteDataButton.setBounds(10, 260, 150, 50);
        deleteDataButton.setFocusable(false);
        deleteDataButton.addActionListener(this);

        logButton.setBounds(10,320,150,50);
        logButton.setFocusable(false);
        logButton.addActionListener(this);

        seeTripDataButton.setBounds(180, 260, 150, 50);
        seeTripDataButton.setFocusable(false);
        seeTripDataButton.addActionListener(this);

        addITButton.setBounds(180, 320, 150, 50);
        addITButton.setFocusable(false);
        addITButton.addActionListener(this);

        branchInfoButton.setBounds(10, 380, 150, 50);
        branchInfoButton.setFocusable(false);
        branchInfoButton.addActionListener(this);

        clientOfferButton.setBounds(180, 380, 150, 50);
        clientOfferButton.setFocusable(false);
        clientOfferButton.addActionListener(this);


        //Vazoume ola ta panels,buttons ktlp sto frame
        frame.add(welcomeLabel);
        frame.add(nameLabel);
        frame.add(actionsLabel);
        frame.add(deleteDataButton);
        frame.add(seeTripDataButton);
        frame.add(updateDataButton);
        frame.add(insertDataButton);
        frame.add(logButton);
        frame.add(addITButton);
        frame.add(branchInfoButton);
        frame.add(clientOfferButton);

        //Ftiaxnoume to frame
        frame.setTitle("Travel Agency"); //set title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //kanoume main stop otan kanoume exit
        frame.setLayout(null);
        frame.setResizable(false); //den afhnoume resize
        frame.setSize(1280, 960);
        frame.setIconImage(image.getImage());


        new RedirectAppOutputStream().guiConsoleTest(frame);


        frame.setVisible(true);
    }


    public static void removeAll (int counter, JFrame frame) {

        // remove previous dropdown fields
        for (int i = 1; i < Methods.fields.size(); i++) {
            frame.remove(Methods.fields.get(i));
            // frame.remove(Methods.fields[i]);
        }

        for (int i=0; i < Methods.chooseKey.size(); i++) {
            frame.remove(Methods.chooseKey.get(i));
        }

        Methods.columnNameStatic.clear();
        frame.remove(Methods.submitButton);
        frame.remove(Methods.deleteButton);
        frame.remove(Methods.scrollPane);
        frame.remove(Methods.submitITButton);
        frame.remove(Methods.scrollPane);
        frame.remove(Methods.scrollPaneSalary);
        frame.remove(Methods.salaryLabel);
        frame.remove(Methods.columnLabel);

        frame.remove(chooseTableBox);
        frame.remove(chooseBranchBox);

        if (counter == 3) {
            for(int i=0;i<7;i++)
                frame.remove(Methods.chooseDateBox[i]);

            frame.remove(Methods.submitDateButton);
            frame.remove(Methods.datetimeLabel);
            frame.remove(Methods.questionLabel);
        }
        if(counter==8){
            frame.remove(Methods.chooselName);
        }

        SwingUtilities.updateComponentTreeUI(frame); //for good measure
    }


    @Override

    public void actionPerformed(ActionEvent e) {     //Koumpi Insert Data
        if(e.getSource()==insertDataButton) {
            removeAll(counter,frame);
            frame.add(chooseTableBox);
            frame.setVisible(true);
            SwingUtilities.updateComponentTreeUI(frame);
            counter=0;

            //Show fields selected at previous usage or index 0 (admin)
            int i = chooseTableBox.getSelectedIndex();
            selectedTable = tables[i];
            Methods.insertTableData(selectedTable,frame);

        }

        else if(e.getSource()==chooseTableBox) {    //Table twn insert data kai update data buttons
            removeAll(counter,frame);
            frame.add(chooseTableBox);

            int i = chooseTableBox.getSelectedIndex();
            selectedTable = tables[i];

            switch (counter) {
                case 0:
                    Methods.insertTableData(selectedTable,frame);
                    break;

                case 1:
                    Methods.updateTableButton(selectedTable,frame,false);
                    break;

                case 2:
                    Methods.updateTableButton(selectedTable,frame,true);
                    break;
            }
        }



        else if(e.getSource()==updateDataButton) {   //Koumpi Update Data
            removeAll(counter,frame);
            frame.add(chooseTableBox);
            frame.setVisible(true);
            SwingUtilities.updateComponentTreeUI(frame);
            counter=1;

            //Show table selected at previous usage or index 0 (admin)
            int i = chooseTableBox.getSelectedIndex();
            selectedTable = tables[i];
            Methods.updateTableButton(selectedTable,frame, false);
        }

        else if(e.getSource()==deleteDataButton) {   //Koumpi Update Data
            removeAll(counter,frame);
            frame.add(chooseTableBox);
            frame.setVisible(true);
            SwingUtilities.updateComponentTreeUI(frame);
            counter=2;

            //Show table selected at previous usage or index 0 (admin)
            int i = chooseTableBox.getSelectedIndex();
            selectedTable = tables[i];
            Methods.updateTableButton(selectedTable,frame, true);
        }

        else if(e.getSource()==seeTripDataButton){
            removeAll(counter,frame);


            try {
                Methods.tripInfoButton(frame);
            } catch (SQLException throwables) {
                System.out.println("Error, please check your input");
                throwables.printStackTrace();
            };
            SwingUtilities.updateComponentTreeUI(frame);
            counter=3;
        }

        else if(e.getSource()==logButton) { //Koumpi logs
            removeAll(counter,frame);
            Methods.logTableButton(frame);
            SwingUtilities.updateComponentTreeUI(frame);
            counter=4;
        }

        else if(e.getSource()==addITButton){
            removeAll(counter,frame);
            Methods.addITButton(frame);
            SwingUtilities.updateComponentTreeUI(frame);
            counter=5;
        }

        else if (e.getSource()==branchInfoButton) {
            removeAll(counter,frame);
            frame.add(chooseBranchBox);
            Methods.branchInfoButton(frame);
            SwingUtilities.updateComponentTreeUI(frame);
            counter=6;

            // pre render the latest selected branch for salary overview when clicking on branch info button
            // defaults to index 0 which is the first branch
            frame.remove(Methods.scrollPaneSalary);
            int i = chooseBranchBox.getSelectedIndex(); //Returns the item that was selected
            selectedTable = branches[i];
            Methods.branchWorkers(frame, selectedTable);
            counter=7;
        }

        else if(e.getSource()==chooseBranchBox) {    //Table twn insert data kai update data buttons
            //Right after branchInfo, so all previous panes are cleared by branchInfo.
            frame.remove(Methods.scrollPaneSalary);

            int i = chooseBranchBox.getSelectedIndex(); //Returns the item that was selected
            selectedTable = branches[i];

            Methods.branchWorkers(frame, selectedTable);
            counter=7;
        }

        else if(e.getSource()==clientOfferButton){
            removeAll(counter,frame);
            Methods.clientOffersButton(frame);

            counter=8;

        }
    }
}
