import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/*Adicionado para o Multicast*/
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
/**/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Renato Matos
 */
public class RMIServer extends UnicastRemoteObject implements RMIInterface{
    
    /*Adicionado para o Multicast*/
    static private String MULTICAST_ADDRESS = "224.0.227.1";
    static private int PORT = 4321;
    /**/
    public RMIServer() throws RemoteException{
        super();
    }
    
    @Override
    public String getData(String text) throws RemoteException{
        //text = "Hi "+text;
        return text;
    }
    
    @Override
    public String loginRMIServer(String username, String password) throws RemoteException{
       
        int serverID = 1;
        String status = "null";
        System.out.println("\nLogin info received: username|"+username+", password|"+password);
        String protocol = "type|login;username|"+username+";password|"+password+";id|123456789;serverId|"+serverID+";status|"+status;
        System.out.println(protocol);
        //pedir confirmacao ao multicast
        //receber dados
        //if ....
        MulticastClient client = new MulticastClient();
        client.start();
        MulticastUser user = new MulticastUser(protocol);
        user.start();
        return "Login efectuado com sucesso!";
        //else ...
        //return "Dados nao correspondem a um user registado
    }
    
    @Override
    public String registarRMIServer(String username, String password) throws RemoteException{
       System.out.println("type|signup;username|"+username+";password|"+password);
       //pedir confirmacao ao multicast
       //receber dados
       //if ....
       return "Registo efectuado com sucesso!";
       //else ...
       //return "Dados nao correspondem a um user registado
    }
    
    public static void main(String args[]){
        try{
            //registry 7000 para server1, 7001 para server2
            System.out.print("Registry:");
            Scanner sc = new Scanner(System.in);
            int registry = sc.nextInt();
            Registry reg = LocateRegistry.createRegistry(registry);
            reg.rebind("rmiserver", new RMIServer());
            System.out.println("RMIServer started");
            /*Adicionado para o Multicast*/
            /*
            MulticastClient client = new MulticastClient();
            client.start();
            MulticastUser user = new MulticastUser();
            user.start();*/
            /**/
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    
    static class MulticastClient extends Thread{
        private String MULTICAST_ADDRESS = "224.0.227.1";
        private int PORT = 4321;

        public void run() {
            MulticastSocket socket = null;
            try {
                socket = new MulticastSocket(PORT);  // create socket and bind it
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                socket.joinGroup(group);
                while (true) {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }

    static class MulticastUser extends Thread {
        private String MULTICAST_ADDRESS = "224.0.227.1";
        private int PORT = 4321;
        String protocol;

        public MulticastUser(String protocol) {
            super("User " + (long) (Math.random() * 1000));
            this.protocol = protocol;
        }

        public void run() {
            MulticastSocket socket = null;
            System.out.println(this.getName() + " ready...");
            try {
                socket = new MulticastSocket();  // create socket without binding it (only for sending)
                byte[] buffer = protocol.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }

    
}
