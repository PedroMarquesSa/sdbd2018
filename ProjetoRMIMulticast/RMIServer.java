import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.net.*;
import java.io.*;
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
    
    public RMIServer() throws RemoteException{
        super();
    }
    
    @Override
    public String loginRMIServer(String username, String password) throws RemoteException{
       
        int serverID = 1;
        String status = "null";
        System.out.println("\nLogin info received: username|"+username+", password|"+password);
        String protocol = "type|login;username|"+username+";password|"+password+";id|123456789;serverId|"+serverID+";status|"+status;
        System.out.println(protocol+"\n");
        
        MulticastClient client = new MulticastClient();
        client.start();
        MulticastUser user = new MulticastUser(protocol);
        user.start();
        
        return "Login efectuado com sucesso!";
    }
    
    @Override
    public String registarRMIServer(String username, String password) throws RemoteException{
       System.out.println("type|signup;username|"+username+";password|"+password);
       return "Registo efectuado com sucesso!";
    }
    
    @Override
    public Boolean ping(){
        return true;
    }

    public static void main(String args[]) throws RemoteException{
        connect();   
    }

    public static void connect() throws RemoteException{
        Registry reg = null;
        RMIInterface rmiinterface = null;
        try{
            reg = LocateRegistry.createRegistry(7000);
            rmiinterface = (RMIInterface) reg.lookup("rmiserver");
            reg.rebind("rmiserver", new RMIServer());
        //Nao ha nada ligado ao registry 7000. Este servidor torna-se no servidor primario   
        }catch (java.rmi.NotBoundException e) {
            System.out.println(e);
            try{
                reg.rebind("rmiserver", new RMIServer());
                System.out.println("Deu bind no registry 7000!");
                System.out.println("Servidor primario");
            }catch (Exception e2) {
                //System.out.println(e);
            }
        //Ja ha algo ligado ao registry 7000. Este servidor torna-se no servidor secundario
        }catch(java.rmi.server.ExportException e){
            //System.out.println(e);
            System.out.println("Ja existe um servidor ligado ao registry 7000!");
            System.out.println("Servidor secundario");
            try{
                Registry reg2 = LocateRegistry.getRegistry("127.0.0.1", 7000);
                RMIInterface rmiinterface2 = (RMIInterface) reg2.lookup("rmiserver");
                int pingsNaoCorrespondidos = 0;
                boolean trueOrFalse = true;
                while(trueOrFalse){
                    try{
                    //Intervalo de tempo entre cada ping
                    Thread.sleep(5000); //deveria ser com currentTimeMillis()?
                    if(rmiinterface2.ping() == true){
                        System.out.println("Ping!");
                    }
                    }catch(Exception e3){
                        //System.out.println(e3);
                        pingsNaoCorrespondidos++;
                        System.out.println("Pings nao correspondidos:"+pingsNaoCorrespondidos);
                    }
                    if(pingsNaoCorrespondidos>=5){
                        trueOrFalse = false;
                        /*caso o primario retorne antes de passarem as 5 tentativas, o secundario
                        (que entretanto transformou-se em primário) só volta a ser secundário passadas
                        essa 5 tentativas*/
                    }
                }
                connect();
            }catch(Exception notbound){}
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


    /*
    static void rmiComoTCPClient(){
        //TCP entre RMI server. server1 como TCPClient e server2 como TCPServer
        int tcpServerPort = 6000;
        try{
            System.out.println("A Escuta no Porto 6000");
            ServerSocket listenSocket = new ServerSocket(tcpServerPort);
            System.out.println("LISTEN SOCKET="+listenSocket);               
        }catch(IOException e){
            System.out.println("Listen:" + e.getMessage());
        }
        Socket s = null;
        //String tcpServerAddress = "192.168.1.198";
        String tcpServerAddress = "10.16.0.71";
        
        try {
            // 1o passo
            s = new Socket(tcpServerAddress, tcpServerPort);
            System.out.println("SOCKET=" + s);
            // 2o passo
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String texto = "";
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);
            System.out.println("Introduza texto:");
            // 3o passo
            int num = 0;
            while (true) {
                long time = System.currentTimeMillis();
                //Teste de 30 em 30 seg
                if(time%5000 == 0){
                    num++;
                    texto = "Teste "+num;
                    out.writeUTF(texto);
                    String data = in.readUTF();
                    System.out.println("server2: " + data);
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
        }
    }


    static void rmiComoTCPServer(){
        try{
            int tcpServerPort = 6000;
            System.out.println("A Escuta no Porto 6000");
            ServerSocket listenSocket = new ServerSocket(tcpServerPort);
            System.out.println("LISTEN SOCKET="+listenSocket);

            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                DataInputStream in;
                DataOutputStream out;
                        
                try{                            
                    in = new DataInputStream(clientSocket.getInputStream());
                    out = new DataOutputStream(clientSocket.getOutputStream());
                    String resposta;
                    try{
                        while(true){
                            //an echo server
                            String data = in.readUTF();
                            System.out.println("server1: "+data+" enviado");
                            out.writeUTF(data+" recebido");
                        }
                    }
                    catch(EOFException e){System.out.println("EOF:" + e);
                    }catch(IOException e){System.out.println("IO:" + e);
                }

                }catch(IOException e){
                    System.out.println("Connection:" + e.getMessage());
                }
            }
        
        }catch(IOException e){
            System.out.println("Listen:" + e.getMessage());
        }
    }*/

}
