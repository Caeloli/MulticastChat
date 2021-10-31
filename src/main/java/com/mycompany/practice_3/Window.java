/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.practice_3;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import sun.util.logging.PlatformLogger;
/**
 *
 * @author caelo
 */
public class Window {
    public static String name;
    public int statusOp;
    public int exitFlag;
    private JFrame mainFrame;
    private JPanel panel, userPanel;
    private JTabbedPane chats;
    //private ArrayList<JTextArea> personChat;
    private ArrayList<JTextPane> personChat;
    private ArrayList<String> contactsChat;
    private ArrayList<String> contacts;
    private ArrayList<JTextField> txtSend;
    private ArrayList<JButton> btnSend;
    private ArrayList<JButton> btnUsers;
    
    public Window(){
        statusOp = 0;
        name = JOptionPane.showInputDialog("Ingrese el nombre que utilizará");
        while(name.isEmpty()){
            name = JOptionPane.showInputDialog("Ingrese el nombre que utilizará");
        }
        mainFrame = new JFrame();
        mainFrame.setSize(700,500);
        mainFrame.setTitle("Chat");
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(WindowEvent we){
                statusOp = 1;
                exitFlag = 1;
                System.out.println("Ventana cerrándose");
                try{
                    Thread.sleep(1000);
                } catch(InterruptedException ie){
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ie);
                }
            }
        });
        personChat = new ArrayList<>();
        contactsChat = new ArrayList<>();
        contacts = new ArrayList<>();
        txtSend = new ArrayList<>();
        btnSend = new ArrayList<>();
        btnUsers = new ArrayList<>();
        initComp();
        mainFrame.setVisible(true);
    }
    
    private void initComp(){ //Set foundations of the GUI
        panel = new JPanel();
        JLabel title = new JLabel("Tus chats: " + name);
        panel.setLayout(null);
        mainFrame.getContentPane().add(panel);
        title.setBounds(10,10,180,30);
        panel.add(title);
        setGeneral();
    }
     
    private void setGeneral(){ //Set general Components of the GUI
        chats = new JTabbedPane();
        chats.setBounds(10,50,480,400);
        newChat("General");
        panel.add(chats);
        
        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));        
        JScrollPane scroll = new JScrollPane(userPanel);
        scroll.setBounds(510, 50, 150, 400);
        panel.add(scroll); 
    }
    
    private void newChat(String nameChat){ //Generate a new tab with all the elements of Chat
        JPanel newPanel = new JPanel();
        newPanel.setLayout(null);
        chats.addTab(nameChat, newPanel);
        
        JTextPane chatScreen = new JTextPane();
        chatScreen.setEditable(false);
        chatScreen.setForeground(Color.BLACK);
        personChat.add(chatScreen); //Add a text area into an array
        JScrollPane scroll = new JScrollPane(personChat.get(personChat.size() - 1)); 
        //Creates ScrollPane that displays the chatScreen
        scroll.setBounds(10, 10, 455, 300);
        newPanel.add(scroll); //Add ScrollPane
        
        JTextField text = new JTextField();
        txtSend.add(text); //add TextField into txtSend array
        txtSend.get(txtSend.size()-1).setBounds(10, 320, 350, 39);
        newPanel.add(txtSend.get(txtSend.size()-1));
        
        JButton send = new JButton("Enviar");
        btnSend.add(send);
        btnSend.get(btnSend.size()-1).setBounds(370,320,95,38);
        btnSend.get(btnSend.size()-1).setText("Enviar");
        btnSend.get(btnSend.size()-1).addActionListener((ActionEvent e) -> {
            statusOp = 1;
        });
        String butName = "Enviar" + (btnSend.size()-1);
        System.out.println(butName);
        btnSend.get(btnSend.size()-1).setName(butName);
        newPanel.add(send);
       
        
        contactsChat.add(nameChat);
    }
    
    public void setContacts(String msg){ //Set and update the list of contacts that are connected in the chat
        System.out.println("Lista de contactos recibida");
        //System.out.println("Mensaje" + msg);
        String contact = "";
        contacts.clear();
        for(int i = 0; i<msg.length(); i++){
            if(Character.isLetter(msg.charAt(i)) || Character.isDigit(msg.charAt(i))){
                contact += msg.charAt(i);
            } else if(msg.charAt(i) == ',' || msg.charAt(i) == ']'){
                contacts.add(contact);
                contact = "";
            }
        }
        System.out.println("Lista de contactos: " + contacts);
        updateContacts();
    }
    
    public void updateContacts(){ //Update the list of buttons that represent the contacts in the chat
        userPanel.removeAll();
        
        for(int i = 0; i < contacts.size(); i++){
            if(contacts.get(i).equals(name))
                continue;
            JButton btn = new JButton(contacts.get(i));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getMinimumSize().height));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton btn = (JButton) e.getSource();
                    System.out.println("Name: " + btn.getText());
                    System.out.println("Contactschat contains: " + contactsChat.contains(btn.getText()));
                    if(!contactsChat.contains(btn.getText())){
                        newChat(btn.getText());
                    }
                    chats.setSelectedIndex(contactsChat.indexOf(btn.getText()));
                }
            });
            btnUsers.add(btn);
            userPanel.add(btn);
        }
        userPanel.revalidate();
        userPanel.repaint();
    }
    
    public void setMessage(String msg, Boolean isPriv){ //Send both private and general messages
        //Recompile sender
        String sender = "";
        String addressee = "";
        System.out.println("El mensaje es: " + msg);
        int i;
        for(i = 1; Character.isLetter(msg.charAt(i)) || Character.isDigit(msg.charAt(i)); i++)
            sender = sender + msg.charAt(i);
        System.out.println("El sender es: " + sender);
        if(isPriv){
            for(i+=2;Character.isLetter(msg.charAt(i)) || Character.isDigit(msg.charAt(i));i++)  
                addressee = addressee + msg.charAt(i);
            System.out.println("El receptor es: " + addressee);
            if(sender.equals(name)||addressee.equals(name)){ //Check if i'm the one sending o receiving the msg
                int selIndx;
                if(addressee.equals(name)){                 //Create a unilateral connection
                    if(!contactsChat.contains(sender)){
                        newChat(sender);
                        chats.setSelectedIndex(contactsChat.indexOf(sender));
                    }
                    selIndx = contactsChat.indexOf(sender);
                }else{
                    selIndx = contactsChat.indexOf(addressee);
                }
                personChat.get(selIndx).setText(personChat.get(selIndx).getText() + "\n" + sender + ":" + msg.substring(++i));
            }
        } else{
            int index = 0;
            personChat.get(index).setText(personChat.get(index).getText()+"\n"+msg.substring(1).replace(">", ": "));
        }        
    }
    
    public int getActiveTab(){
        return chats.getSelectedIndex();
    }
    
    public String getActiveMessage(){
        int tabIndex = chats.getSelectedIndex();
        String msg = txtSend.get(tabIndex).getText();
        txtSend.get(tabIndex).setText("");
        return msg;
    }
    
    public String getContactsChat(int in){
        return contactsChat.get(in);
    }
    
    public int getStatus(){
        return this.statusOp;
    }
    
    public int getExit(){
        return this.exitFlag;
    }
    
    public void setStatus(int newStatus){
        this.statusOp = newStatus;
    }
    
    public String getName(){
        return this.name;
    }
    
    public static void main(String[] args) {
        new Window();
    }
}
