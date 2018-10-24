import java.lang.reflect.Array;
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
        id = Integer.parseInt(args[0]);


        // QUANDO CORRER A PRIMEIRA VEZ, SEM FICHEIROS
        /*<====================================================><====================================================><====================================================>
        <====================================================><====================================================><====================================================>*/
        /*Ficheiros temp = new Ficheiros(id);
        temp.criaFicheiroObjeto("Contas", id);
        System.out.println("criei o ficheiro");
       */
        /*<====================================================><====================================================><====================================================>
        <====================================================><====================================================><====================================================>*/


        MulticastServer server = new MulticastServer();
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
                if (verifyId(map) == 2) { //o servidor atende e responde
                    //criacao de thread que responde a cada pedido
                    counter++;
                    MulticastServerThread thread = new MulticastServerThread(socket, packet, counter, id);
                    thread.start();
                    counter--; //aumentar numero de threads
                } else if (verifyId(map) == 1){ //apenas processa informacao
                    counter++;
                    MulticastServerThreadWorkerOnly workerOnly = new MulticastServerThreadWorkerOnly(map, counter, id);
                    workerOnly.start();
                    counter--;
                } else { //ignorar porque é uma mensagem do proprio servidor
                    continue;
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

    //Retorna:
    // 2 -> é um pedido e é o servidor a responder
    // 1 -> é um pedido e NAO é o servior a responder
    // 0 -> nao é um pedido, ignorar
    public int verifyId(HashMap<String, String> map) {
        //System.out.println("O servidor que responde: " + map.get("serverId"));
        if (map.get("serverId") == null)
            return 0;
        if (map.get("serverId").equals(Integer.toString(id)))
            return 2;
        else
            return 1;
    }

}
//======================================================================
//======================================================================

//THREAD WORKER -> apenas trabalhar
class MulticastServerThreadWorkerOnly extends Thread {
    private int id;
    private HashMap<String, String> map;
    //criar class(es) para aceder na thread
    private Ficheiros fich;

    MulticastServerThreadWorkerOnly(HashMap<String, String> map, int counter, int idServer) {
        fich = new Ficheiros(idServer);
        this.id = counter;
        this.map = map;
    }

    //metodo de acao de thread
    public void run() {
        System.out.println("sou o escravo");
        String temp = null;
        System.out.println("A thread " + this.id + " esta a correr");
        //caso seja um registo
        if (this.map.get("type").equals("register")) {
            System.out.println("Registo!!!");
            temp = fich.registerUser(this.map);
            System.out.println(temp);
        }
        if (this.map.get("type").equals("login")) {
            System.out.println("Login!!!");
            temp = fich.loginUser(this.map);
            System.out.println(temp);
        }
        if (this.map.get("type").equals("promote")){
            System.out.println("Promocao a editor!!!");
            temp = fich.tornarEditor(this.map);
            System.out.println(temp);
        }
    }
}

//THREAD WORKER + SENDER
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
    private int idServer;
    private HashMap<String, String> map = new HashMap<>();
    //criar class(es) para aceder na thread
    private Ficheiros fich;

    MulticastServerThread(MulticastSocket socket, DatagramPacket packet, int counter, int idServer){
        fich = new Ficheiros(idServer);
        this.socket = socket;
        this.id = counter;
        this.map = fich.packet2Hashmap(packet);
        this.idServer = idServer;
    }

    //verificar se o id do server que queremos que responda é de facto o que vai responder
    public boolean verifyId(HashMap<String, String> map) {
        //System.out.println("O servidor que responde: " + map.get("serverId"));
        if(map.get("serverId") == null) return false;
        return map.get("serverId").equals(Integer.toString(idServer));
    }

    //metodo de acao de thread
    public void run() {
        System.out.println("sou o outro");
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
        if (this.map.get("type").equals("promote")){
            System.out.println("Promocao a editor!!!");
            temp = fich.tornarEditor(this.map);
            System.out.println(temp);
            resposta = string2packet(temp);
        }
        try {
            System.out.println("enviei a resposta");
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
    public String status; //online,
    public String what; //admin, normal, editor

    public User(String username, String password, String status, String what) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.what = what;
    }
}

//======================================================================
//======================================================================

//FICHEIROS DE OBJETO
class Ficheiros implements Serializable {

    private int id;
    private int firstOne;
    public Ficheiros(int id) {
        this.id = id;
        //this.firstOne = 0; //ATENCAO ERRO PODE VIR DAQUI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    public void criaFicheiroObjeto(String aux, int id) {
            try {
            FileOutputStream f = new FileOutputStream(new File(aux + Integer.toString(id) + ".obj")); //guardar contas (usernames, password, status)
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
            iS = new ObjectInputStream(new FileInputStream("Contas"+Integer.toString(id)+".obj"));
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
            System.out.println(aux.username+"->"+aux.password+"->"+aux.what+"->"+aux.status);
        }
        System.out.println("<=====================><=====================><=====================>");
        return users;
    }

    //encontra o User especifico no array
    public User encontraUser(ArrayList<User> aux, User user) {
        User temp = null;
        for(User i : aux) {
            if(i.username.equals(user.username)) {
                temp = i;
                return temp;
            }
        }
        return temp;
    }


    public void makeAdmin(User user, ArrayList<User> users){
        int i = 0;
        for(User aux:users) {
            if(user.username.equals(aux.username)){
                users.get(i).what = "admin";
                System.out.println("Ficou admin");
                return ;
            }
            i++;
        }
    }

    public boolean checkEmpty(ArrayList<User> users) {
        return users.isEmpty();
    }
    //atualiza User - lista de utilizadores
    public void updateUserList(ArrayList<User> aux) {
        ObjectOutputStream oS = null;
        try {
            oS = new ObjectOutputStream(new FileOutputStream("Contas"+Integer.toString(id)+".obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (oS != null) {
                oS.writeObject(aux);
                System.out.println("atualizei lista");
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

    //user fica online
    public void turnOnline(ArrayList<User> aux, User user) {
        int i=0;
        for (User anAux : aux) {
            if (anAux.username.equals(user.username)) {
                aux.get(i).status = "online";
                return;
            }
            i++;
        }
    }

    //Registo -> devolve a string a enviar pelo server
    public String registerUser(HashMap<String, String> map) {
        String tempResposta;
        boolean first = false;
        User user = new User(map.get("username"), map.get("password"), "null", "normal"); //status-> null (ato de registar)
        ArrayList<User> aux;
        aux = buscaUsers();
        if(checkEmpty(aux))
            first = true;
        //SINCRONIZACAO ??????????
        if(!checkUser(aux, user.username)){ //caso possa utilizar esse nome
            aux.add(user);
            if(first){
                makeAdmin(user, aux); //caso seja o primeiro
            }
            updateUserList(aux); //carrega para o ficheiro
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
        User user = new User(map.get("username"), map.get("password"), "accepted", "normal"); //status -> accepted
        ArrayList<User> aux;
        aux = buscaUsers();
        int temp = checkPassword(aux, user);
        if(temp == 2){ //existe essa conta e a pass esta certa
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|accepted";
            turnOnline(aux, user);
            updateUserList(aux);
        } else if(temp == 1) { //username ja usado, pass errada
            System.out.println("Username ja usado");
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongpassword";
        } else if(temp == 0){
            System.out.println("Username nao existe");
            tempResposta = "type|login;username|"+user.username+";password|"+user.password+";id|"+map.get("id")+";status|wrongusername";
        }
        return tempResposta;
    }

    //DAR PERMISSOES DE EDITOR
    public String tornarEditor(HashMap<String, String> map) {
        String tempResposta = null;
        User user = new User(map.get("username"), map.get("password"), "accepted", map.get("what")); //status -> accepted
        User userDest = new User(map.get("usernameDest"), "null", "null", map.get("null")); //user que quero promover
        int i=0; //iterador para modificar
        ArrayList<User> aux;
        aux = buscaUsers();
        user = encontraUser(aux, user);
        userDest = encontraUser(aux, userDest);
        if(user!=null){ // caso exista o user a tentar promover
            if(userDest!=null){ //caso exista o user a ser promovido
                if (user.what.equals("admin") || user.what.equals("editor")) { //caso o user a querer promover seja admin ou editor
                    for(User temp : aux) {
                        if (temp.username.equals(userDest.username)){
                            aux.get(i).what = "editor";
                            updateUserList(aux);
                            tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|accepted;";
                            return tempResposta;
                        }
                        i++;
                    }
                }
                //caso o user nao tenha permissao
                tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|rejected;";
                return tempResposta;
            }
            //caso nao exista o user a ser promovido
            tempResposta = "type|promote;username|"+user.username+";password|"+user.password+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|wrongusernameDest;";
            return tempResposta;
        }
        //caso nao exista o user a ser promovido
        tempResposta = "type|promote;username|"+map.get("username")+";password|"+map.get("password")+";usernameDest|"+map.get("usernameDest")+";id|"+map.get("id")+";status|wrongusername;";
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


/*<====================================================><====================================================><====================================================>
<====================================================><====================================================><====================================================>*/


//Classes Musica

class Artista implements Serializable {
    public String nome;
    public String descricao;
    public String dataNascimento;
    public ArrayList<Album> albums;
}

class Album implements Serializable {
    public String Artista;
    public String descricao;
    public String nome;
    public float ratingAVG;
    public ArrayList<Review> reviews;
    public ArrayList<Musica> musicas;
}

class Musica implements Serializable {
    public String nome;
    public String Artista;
    public String Album;
}

class Review implements Serializable {

}














