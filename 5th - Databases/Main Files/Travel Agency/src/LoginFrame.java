import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;

public class LoginFrame extends JFrame implements ActionListener {
    JButton loginButton = new JButton("Login");
    JButton exitButton = new JButton("Exit");
    JTextField userIDField = new JTextField();
    JPasswordField userPasswordField = new JPasswordField();
    JLabel userIDLabel = new JLabel("User ID:");
    JLabel userPasswordLabel = new JLabel("Password:");
    JLabel messageLabel = new JLabel();
    ImageIcon image = new ImageIcon("Main Files/Travel Agency/img.png"); //To icon tou application
    ImageIcon icon = new ImageIcon("Main Files/Travel Agency/companyicon.png"); //Icon tou login screen
    JLabel label1 = new JLabel("<html>Welcome to <br/> Viljot Halbar<html>");
    JPanel panel1 = new JPanel();
    HashMap<String,String> loginInfo = new HashMap<String,String>(); //Ftiaxnoume to hashmap loginInfo wste na einai globally available gia na xrhsimopoihthei apo methodous

    LoginFrame(HashMap<String,String> loginInfoOriginal) {
        loginInfo = loginInfoOriginal;


        userIDLabel.setBounds(410,430,80,25);
        userIDLabel.setFont(new Font(null,Font.BOLD,15));
        userPasswordLabel.setBounds(410,475,80,25);
        userPasswordLabel.setFont(new Font(null,Font.BOLD,15));

        messageLabel.setBounds(450,350,500,35);
        messageLabel.setFont(new Font(null,Font.BOLD,21));

        userIDField.setBounds(490,430,200,25);
        userPasswordField.setBounds(490,475,200,25);


        //Ftiaxnoume omorfa to window
        this.setTitle("Travel Agency"); //set title
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //kanoume main stop otan kanoume exit
        this.setLayout(null);
        this.setResizable(false); //den afhnoume resize
        this.setSize(1280, 960);
        this.setIconImage(image.getImage());
        this.getContentPane().setBackground(Color.cyan);  //alazoume xrwma sto background

        panel1.setBackground(Color.cyan);
        panel1.setBounds(0, 0, 620, 300);


        label1.setHorizontalTextPosition(JLabel.RIGHT);
        label1.setVerticalTextPosition(JLabel.CENTER);
        label1.setForeground(Color.BLUE);
        label1.setFont(new Font("Mv Boli", Font.BOLD, 38));
        label1.setIcon(icon);
        panel1.add(label1);




        loginButton.setBounds(430, 515, 100, 50);
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);
        exitButton.setBounds(580,515,100,50);
        exitButton.setFocusable(false);
        exitButton.addActionListener(this);




        this.add(loginButton);
        this.add(userIDLabel);
        this.add(userIDField);
        this.add(userPasswordField);
        this.add(messageLabel);
        this.add(userPasswordLabel);
        this.add(exitButton);
        this.add(panel1);

        this.getRootPane().setDefaultButton(loginButton); // enter presses login button

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==exitButton) {
            System.exit(0);
        }

        if(e.getSource()==loginButton){
            String userID = userIDField.getText().trim();
            String password = String.valueOf(userPasswordField.getPassword());

            if(loginInfo.containsKey(userID)) {
                if (loginInfo.get(userID).equals(password)) {
                    messageLabel.setForeground(Color.magenta);
                    messageLabel.setText("Login Successful");
                    this.dispose();        //diwxnoume to palio window
                    
                    
                    new ItOptionsFrame(userID);
                    
                } else {
                    messageLabel.setForeground(Color.red);
                    messageLabel.setText("You entered the wrong password.");
                }
            }
            else{
                messageLabel.setForeground(Color.red);
                messageLabel.setText("User ID does not exist in database.");
            }
        }
    }
}
