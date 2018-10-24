import java.net.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.227.1";
    private int PORT = 4321; //porto
    private static int id;

    //contador para saber o numero de threads ativas
    private int counter = 0;
    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public static void main(String[] args) {
        //Ficheiros temp = new Ficheiros();
        //temp.criaFicheiroObejto("Contas");
        //System.out.println("criei o ficheiro");
        MulticastServer server = new MulticastServer();
        id = Integer.parseInt(args[0]);
        server.start();
    }

    public void run() {
        MulticastSocket socket = null;
        HashMap<String,String> map;
        try {
            socket = new MulticastSocket(PORT);  //create socket binding it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                //Rececao da mensagem
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                map = packet2Hashmap(packet);
                //verificar se o server é o indicado para responder
                if(verifyId(map)){
                    //criacao de thread que responde a cada pedido
                    counter++;
                    MulticastServerThread thread = new MulticastServerThread(socket, packet, counter);
                    thread.start();
                    counter--; //aumentar numero de threads
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    //funcao para trasformar pacote em hashmap
    public HashMap<String, String> packet2Hashmap(DatagramPacket packet) {
        HashMap<String, String> aux = new HashMap<>();
        String message = new String(packet.getData(), 0, packet.getLength());
        String[] temp = message.split(";");
        String[] each;

        for (int i=0; i<temp.length; i++) {
            each = temp[i].split("\\|");
            aux.put(each[0], each[1]);
        }
        return aux;
    }

    //verificar se o id do server que queremos que responda é de facto o que vai responder
    public boolean verifyId(HashMap<String, String> map) {
        //System.out.println("O servidor que responde: " + map.get("serverId"));
        if(map.get("serverId") == null) return false;
        return map.get("serverId").equals(Integer.toString(id));
    }
}
//======================================================================
//======================================================================

//THREAD WORKER
class MulticastServerThread extends Thread {
    private String MULTICAST_ADDRESS = "224.0.227.1";
    private int PORT = 4321; //porto de rececao
    private InetAddress group;

    {
        try {
            group = InetAddress.getByName(MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    //socket + id (vêm da main) + packet
    private MulticastSocket socket = null;
    private int id;
    private HashMap<String, String> map = new HashMap<>();
    //criar class(es) para aceder na thread
    private Ficheiros fich = new Ficheiros();

    MulticastServerThread(MulticastSocket socket, DatagramPacket packet, int counter){
        this.socket = socket;
        this.id = counter;
        this.map = fich.packet2Hashmap(packet);
    }

    //metodo de acao de thread
    public void run() {
        String temp = null;
        DatagramPacket resposta = null;
        System.out.println("A thread "+this.id+ " esta a correr");
        //caso seja um registo
        if(this.map.get("type").equals("register")){
            System.out.println("Registo!!!");
            temp = fich.registerUser(this.map);
            System.out.println(temp);
            resposta = string2packet(temp);
        }
        if(this.map.get("type").equals("login")){
            System.out.println("Login!!!");
            temp = fich.loginUser(this.map);
            System.out.println(temp);
            resposta = string2packet(temp);
        }
        try {
            socket.send(resposta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DatagramPacket string2packet(String temp) {
        byte[] buffer = temp.getBytes();
        return new DatagramPacket(buffer, buffer.length, group, PORT);
    }
}

//======================================================================
//======================================================================

//Classes auxiliares
class User implements Serializable {
    public String username;
    public String password;
    public String status;
    public String what; //admin, normal, editor

    public User(String username, String password, String status) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.what = "normal";
    }
}

//======================================================================
//======================================================================

//FICHEIROS DE OBJETO
class Ficheiros implements Serializable {
    public void criaFicheiroObjeto(String aux) {
            try {
            FileOutputStream f = new FileOutputStream(new File(aux + ".obj")); //guardar contas (usernames, password, status)
            //ObjectOutputStream ocontas = new ObjectOutputStream(fcontas);
            //ocontas.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
                e.printStackTrace();
            }
    }

    //Abre e carrega users para ArrayList para consultar
    public ArrayList<User> buscaUsers() {
        ObjectInputStream iS = null;
        try {
            iS = new ObjectInputStream(new FileInputStream("Contas.obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //array de users
        ArrayList<User> users = new ArrayList<>();
        try {

            if (iS != null) {
                users = (ArrayList<User>) iS.readObject();
            } else {
                System.out.println("Users esta vazio");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //imprime users
        System.out.println("<=====================><=====================><=====================>");
        for(User aux : users) {
            System.out.println(aux.username+"->"+aux.password+"->"+aux.status);
        }
        System.out.println("<=====================><=====================><=====================>");
        return users;
    }

    //atualiza User - lista de utilizadores
    public void updateUserList(ArrayList<User> aux) {
        ObjectOutputStream oS = null;
        try {
            oS = new ObjectOutputStream(new FileOutputStream("Contas.obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (oS != null) {
                oS.writeObject(aux);
                System.out.println("inscrevi user");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUser(ArrayList<User> aux, String username) {
        for (User anAux : aux) {
            if (anAux.username.equals(username))
                return true; //ja existe alguem com este username
        }
        return false; //livre para utilizar
    }

    public int checkPassword(ArrayList<User> aux, User user) {
        int resposta = 0;
        for (User anAux : aux) {
            if (anAux.username.equals(user.username)) {
                resposta = 1;
                if (anAux.password.equals(user.password)) {
                    return 2; //certo
                }
            }
        }
        return resposta;
    }

    //Registo -> devolve a string a enviar pelo server
    public String registerUser(HashMap<String, String> map) {
        String tempResposta;
        User user = new User(map.get("username"), map.get("password"), "null"); //status-> null (ato de registar)
        ArrayList<User> aux;
        aux = buscaUsers();
        //SINCRONIZACAO ??????????
        if(!checkUser(aux, user.username)){ //caso possa utilizar esse nome
            aux.add(user);
            updateUserList(aux);
            tempResposta = "type|register;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted";
        } else { //username ja usado (DAR UPDATE DEPOIS)
            System.out.println("username ja usado");
            tempResposta = "type|register;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|rejected";
        }
        return tempResposta;
    }

    //Login -> devolve a string a enviar pelo server
    public String loginUser(HashMap<String, String> map) {
        String tempResposta = null;
        User user = new User(map.get("username"), map.get("password"), "accepted"); //status -> accepted
        ArrayList<User> aux;
        aux = buscaUsers();
        int temp = checkPassword(aux, user);
        if(temp == 2){ //existe essa conta e a pass esta certa
            aux.add(user);
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted";
        } else if(temp == 1) { //username ja usado, pass errada
            System.out.println("Username ja usado");
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongpassword";
        } else if(temp == 0){
            System.out.println("Username nao existe");
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongusername";
        }
        return tempResposta;
    }

    //converte
    private HashMap<String, String> string2Hashmap(String message) {
        HashMap<String, String> aux = new HashMap<>();
        String[] temp = message.split(";");
        String[] each;

        for (int i=0; i<temp.length; i++) {
            each = temp[i].split("\\|");
            aux.put(each[0], each[1]);
        }
        return aux;
    }

    public HashMap<String, String> packet2Hashmap(DatagramPacket packet) {
        HashMap<String, String> aux = new HashMap<>();
        String message = new String(packet.getData(), 0, packet.getLength());
        String[] temp = message.split(";");
        String[] each;

        for (int i=0; i<temp.length; i++) {
            each = temp[i].split("\\|");
            aux.put(each[0], each[1]);
        }
        return aux;
    }

}

