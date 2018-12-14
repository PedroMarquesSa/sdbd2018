package rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.io.*;
import java.util.Random;
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
public class RMIServer extends UnicastRemoteObject implements RMIInterface {

    public RMIServer() throws RemoteException{
        super();
    }

    @Override
    public String loginRMIServer(String username, String password) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        System.out.println("\nLogin info received: username|"+username+", password|"+password);
        String protocol = "type|login;username|"+username+";password|"+password+";id|"+id+";serverId|"+serverID+";status|null";
        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo brodcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}



        String[] parts = respostaClient.split(";");
        //System.out.println("parts[4]= "+parts[4]);
        String[] partsWhat = parts[3].split("\\|");
        String whatIsIt = partsWhat[1];
        String[] parts2 = parts[5].split("\\|");
        System.out.println("parts2[1]= "+parts2[1]);
        if(parts2[1].equals("accepted")){
            System.out.println("Login efetuado com sucesso!");
            return whatIsIt;
        }
        else if(parts2[1].equals("wrongusername")){
            System.out.println("Utilizador inexistente!");
            return "wrongusername";
        }
        else if(parts2[1].equals("wrongpassword")){
            System.out.println("Password incorreta!");
            return "wrongpassword";
        }
        else return "wrongusername";
    }

    @Override
    public boolean registarRMIServer(String username, String password) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        String protocol = "type|register;username|"+username+";password|"+password+";id|"+id+";serverId|"+serverID+";status|null";

        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo brodcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}
        String[] parts = respostaClient.split(";");
        //System.out.println("parts[4]= "+parts[4]);
        String[] parts2 = parts[4].split("\\|");
        String status = parts2[1];
        System.out.println("parts2[1]= "+parts2[1]);
        //System.out.println("Server: "+text);
        if(parts2[1].equals("accepted")){
            System.out.println("Utilizador registado com sucesso!");
            return true;
            //return false;
        }
        else {
            System.out.println("Registo rejeitado. Tente registar-se com outro username.");
            return false;
        }
    }

    @Override
    public String privilegiosEditorRMIServer(String username, String password, String usernameDest) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        String protocol = "type|promote;username|"+username+";password|"+password+";usernameDest|"+usernameDest+";id|"+id+";serverId|"+serverID+";status|null";

        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo brodcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}
        String[] parts = respostaClient.split(";");
        //System.out.println("parts[4]= "+parts[4]);
        String[] parts2 = parts[5].split("\\|");
        String status = parts2[1];
        System.out.println("parts2[1]= "+parts2[1]);

        return parts2[1];
    }

    public String introduzirArtistaRMIServer(String username,  String artist_name, String description, String birth_date, String grupos) throws RemoteException{
        String protocol = "type|new_artist;username|"+username+";artist_name|"+artist_name+";grupo|"+grupos+";description|"+description+";birth_date|"+birth_date;
        String text = enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[4].split("\\|");
        String status = parts2[1];
        //System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Artista adicionado com sucesso!");
            return "0";
        }
        else if(status.equals("rejected")){
            System.out.println("Artista ja existente.");
            return "rejected";
        }
        else if(status.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return "nopermission";
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return "dunno";
    }

    @Override
    public String introduzirAlbumRMIServer(String username,  String artist_name, String album_name, String description, String genero, String releaseDate, String recordlabel) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        String protocol = "type|new_album;username|"+username+";artist_name|"+artist_name+";album_name|"+album_name+";description|"+description+";genre|"+genero+"releaseDate|"+releaseDate+"recordlabel|"+recordlabel+";id|"+id+";serverId|"+serverID+";status|null";

        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo broadcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}
        String[] parts = respostaClient.split(";");
        String[] parts2 = parts[5].split("\\|");
        String status = parts2[1];
        System.out.println("parts2[1]= "+parts2[1]);

        return parts2[1];
    }

    @Override
    public String introduzirMusicaRMIServer(String username,  String artist_name, String album_name, String music_name) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        String protocol = "type|new_music;username|"+username+";artist_name|"+artist_name+";album_name|"+album_name+";music_name|"+music_name+";id|"+id+";serverId|"+serverID+";status|null";
        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo broadcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}
        String[] parts = respostaClient.split(";");
        String[] parts2 = parts[6].split("\\|");
        String status = parts2[1];
        System.out.println("parts2[1]= "+parts2[1]);

        return parts2[1];
    }

    @Override
    public String pesquisarAlbumPorArtistaRMIServer(String username, String artist_name) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        String protocol = "type|search_album_by_artist;artist_name|"+artist_name+";id|"+id+";serverId|"+serverID+";status|null";
        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo broadcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}

        return respostaClient;
    }

    @Override
    public String pesquisarAlbumPorTituloRMIServer(String username, String album_name) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        String protocol = "type|search_album_by_title;album_name|"+album_name+";id|"+id+";serverId|"+serverID+";status|null";
        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo broadcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}

        return respostaClient;
    }

    @Override
    public String enviarParaMulticast(String protocol) throws RemoteException{
        int serverID = 1;

        Random rand = new Random();
        int id = rand.nextInt(999999999) + 1;

        System.out.println(protocol+"\n");

        MulticastClient client = new MulticastClient();
        client.start();

        protocol += ";id|"+id+";serverId|"+serverID+";status|null";

        MulticastUser user = new MulticastUser(protocol);
        user.start();

        //Para que nao receba a mensagem enviada por ele proprio pelo broadcast
        String respostaClient = null;
        try{
            Thread.sleep(500);
            respostaClient = client.getResposta();
            System.out.println("RESPOSTA CLIENT: "+respostaClient);
        }catch(Exception e){}

        return respostaClient;
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

        private String resposta;

        public void setResposta(String message) {
            resposta = message;
        }

        public String getResposta(){
            return resposta;
        }

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
                    System.out.println("De novo "+message);
                    setResposta(message);
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
        /*
        private String resposta;

        public void setResposta(String message) {
            resposta = message;
        }

        public String getResposta(){
            return resposta;
        }*/

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

                //setResposta(packet);
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }
}
