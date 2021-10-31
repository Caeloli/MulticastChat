/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.practice_3;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 *
 * @author caelo
 */
public class MServer extends Thread{
    //Class D 224.0.0.0 - 239.255.255.255
    public static final String MCAST_ADDR = "224.0.0.2"; //Class D
    public static final int MCAST_PORT = 4000;
    public static final int DGRAM_BUF_LEN = 2048;
    private ArrayList<String> contacts;
    
    public void run(){
        contacts = new ArrayList();
        String msg = "";
        InetAddress group = null;
        try{
            group = InetAddress.getByName(MCAST_ADDR); //Determine IP address of a host
                                                       //given's the host name
        } catch(Exception e){
            e.printStackTrace();
        }
        for(;;){
            try{
                MulticastSocket ms = new MulticastSocket(MCAST_PORT);
                ms.joinGroup(group);
                
                //Waiting Datagram of clients
                
                byte[] buf = new byte[DGRAM_BUF_LEN];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                ms.receive(recv);
                byte [] data = recv.getData();
                msg = new String(data);
                System.out.println("Datos recibidos: "+ msg);
                
                if(msg.contains("<init>")){
                    msg = msg.substring(6);
                    System.out.println(msg);
                    String name = "";
                    int i = 0;
                    while(Character.isLetter(msg.charAt(i))){
                        name = name + msg.charAt(i);
                        i++;
                    }
                    contacts.add(name);
                    String cont = "<contacts>" + contacts.toString();
                    //Sending contacts
                    System.out.println("Enviando " + cont); 
                    DatagramPacket sendPacket = new DatagramPacket(cont.getBytes(), cont.length(), group, MCAST_PORT);
                    ms.send(sendPacket);
                    ms.close();
                }else if(msg.contains("C<msg>")){
                    msg = "S" + msg.substring(1);                    
                    DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.length(), group, MCAST_PORT);
                    System.out.println("Enviando: " + msg + " con un TTL = " + ms.getTimeToLive());
                    ms.send(sendPacket);
                    ms.close();
                } else if(msg.contains("<exit>")){
                    String exitName = "";
                    for(int i = 6;Character.isLetter(msg.charAt(i));i++)
                        exitName = exitName + msg.charAt(i);
                    contacts.remove(exitName);
                    String cont = "<contacts>" + contacts.toString();
                    //Sending contacts
                    System.out.println("Enviando " + cont); 
                    DatagramPacket sendPacket = new DatagramPacket(cont.getBytes(), cont.length(), group, MCAST_PORT);
                    ms.send(sendPacket);
                    ms.close();
                }
                
            } catch(Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    public static void main(String[] args) {
        try{
            MServer m = new MServer();
            m.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
