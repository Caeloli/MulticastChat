/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.practice_3;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import sun.applet.Main;

/**
 *
 * @author caelo
 */
public class MClient extends Thread{
    
    public static final String MCAST_ADDR = "224.0.0.2";
    public static final int MCAST_PORT = 4000;
    public static final int DGRAM_BUF_LEN = 2048;
    public static Window w;
    
    public void run(){
        InetAddress group = null;
        try{
            group = InetAddress.getByName(MCAST_ADDR);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            MulticastSocket ms = new MulticastSocket(MCAST_PORT);
            ms.joinGroup(group);
            DatagramPacket contact = new DatagramPacket(("<init>" + w.getName()).getBytes(), ("<init>" + w.getName()).length(), group, MCAST_PORT);
            ms.send(contact);
            while(true){
                if(w.getStatus() == 0){
                    try{
                        ms.setSoTimeout(100);
                        byte[] buf = new byte[DGRAM_BUF_LEN];
                        DatagramPacket recv = new DatagramPacket(buf, buf.length);
                        ms.receive(recv);
                        byte[] data = recv.getData();
                        String msg = new String(data);
                        //System.out.println("Datos recibidos: " +msg );
                        if(msg.contains("<contacts>")){
                            w.setContacts(msg.substring(11).replaceAll("\\s+", ""));
                        }   
                        else if(msg.contains("S<msg><priv>"))
                            w.setMessage(msg.substring(12), true);
                        else if(msg.contains("S<msg>"))
                            w.setMessage(msg.substring(6), false);
                        
                    } catch(SocketTimeoutException ste){
                        continue;
                    } catch(Exception e){
                        e.printStackTrace();
                        System.exit(1);
                    }
                } else if(w.getStatus() == 1){
                    System.out.println("Hora de enviar un mensajito");
                    String msgToSend = "";
                    if(w.getExit() == 1){
                        msgToSend = "<exit>" + w.getName();
                    }else{
                        if(w.getActiveTab() == 0){
                            msgToSend = "C<msg><"+w.getName()+">"+w.getActiveMessage();
                        }
                        else if(w.getActiveTab() != 0){
                            msgToSend = "C<msg><priv><"+w.getName()+"><"+w.getContactsChat(w.getActiveTab())+">"+w.getActiveMessage();
                        }
                    }
                    DatagramPacket packet = new DatagramPacket(msgToSend.getBytes(), msgToSend.length(), group, MCAST_PORT);
                    System.out.println("Enviando: " + msgToSend + "Con un TTL: " + ms.getTimeToLive() );
                    ms.send(packet);
                    w.setStatus(0);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
 
    public static void main(String[] args) {
        try{
            w = new Window();
            MClient client = new MClient();
            client.start();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
