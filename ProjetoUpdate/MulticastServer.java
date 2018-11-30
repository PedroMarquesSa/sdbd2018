import java.lang.reflect.Array;
import java.net.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.227.1";
    private int PORT = 4321; //porto
    private static int id;
    private static Connection connection = null;
    private static FuncoesBD funcoesBD = null;
    //contador para saber o numero de threads ativas
    private int counter = 0;
    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public static void main(String[] args) {
        //enquanto teste
        id = 1;
        //id = Integer.parseInt(args[0]);




        // QUANDO CORRER A PRIMEIRA VEZ, SEM FICHEIROS
        /*<====================================================><====================================================><====================================================>
        <====================================================><====================================================><====================================================>*/
        /*
        Ficheiros temp = new Ficheiros(id);
        temp.criaFicheiroObjeto("Contas", id);
        temp.criaFicheiroObjeto("Biblioteca", id);
        System.out.println("criei o ficheiro");
        */
        /*<====================================================><====================================================><====================================================>
        <====================================================><====================================================><====================================================>*/


        DatabaseConnection newConnection = new DatabaseConnection();
        connection = newConnection.createConnection();

        funcoesBD = new FuncoesBD(connection);

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
                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                //System.out.println("\n"+buffer.toString()+"\n");
                map = packet2Hashmap(packet);
                if (verifyId(map) == 2) { //o servidor atende e responde
                    //criacao de thread que responde a cada pedido
                    counter++;
                    MulticastServerThread thread = new MulticastServerThread(socket, packet, counter, id, true, connection, funcoesBD);
                    thread.start();
                    counter--; //aumentar numero de threads
                } else if (verifyId(map) == 1){ //apenas processa informacao
                    counter++;
                    MulticastServerThread thread = new MulticastServerThread(socket, packet, counter, id, false, connection, funcoesBD);
                    thread.start();
                    counter--; //aumentar numero de threads
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
            try {
                aux.put((each[0]!=null && !each[0].isEmpty())?each[0]:"", (each[1]!=null && !each[1].isEmpty())?each[1]:"");
            }
            catch(ArrayIndexOutOfBoundsException exception) {
                continue;
            }

        }
        return aux;
    }

    //Retorna:
    // 2 -> é um pedido e é o servidor a responder
    // 1 -> é um pedido e NAO é o servidor a responder
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
    //private Ficheiros fich;
    //para ver se sou EU a responder ou nao
    private boolean myTurn = false;
    //conexao com base de dados
    private Connection connection = null;
    private FuncoesBD funcoesBD = null;

    MulticastServerThread(MulticastSocket socket, DatagramPacket packet, int counter, int idServer, boolean myTurn, Connection connection, FuncoesBD funcoesBD){
        //fich = new Ficheiros(idServer);
        this.socket = socket;
        this.id = counter;
        this.map = packet2Hashmap(packet);
        this.idServer = idServer;
        this.myTurn = myTurn;
        this.connection = connection;
        this.funcoesBD = funcoesBD;
    }



    //metodo de acao de thread
    public void run() {
        String temp = null;
        DatagramPacket resposta = null;
        System.out.println("A thread "+this.id+ " esta a correr");
        //caso seja um registo
        switch (this.map.get("type")) {
            case "register":
                System.out.println("Registo!!!");
                try {
                    temp = funcoesBD.registaUser(map);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("nao conseguiu chegar a funcoesBD.registaUser()");
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "login":
                System.out.println("Login!!!");
                try {
                    temp = funcoesBD.loginUser(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "promote":
                System.out.println("Promocao a editor!!!");
                try {
                    temp = funcoesBD.tornarEditor(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "new_artist":
                System.out.println("Adiciona artista!!!");
                try {
                    temp = funcoesBD.addArtista(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "new_album":
                System.out.println("Adiciona album!!!");
                try {
                    temp = funcoesBD.addAlbum(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "new_music":
                System.out.println("Adiciona musica!!!");
                try {
                    temp = funcoesBD.addMusica(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "search_album_by_artist":
                System.out.println("procura album pelo nome do artista!!!");
                try {
                    temp = funcoesBD.searchAlbumArtista(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "search_album_by_title":
                System.out.println("procura album pelo nome do album!!!");
                try {
                    temp = funcoesBD.searchAlbumTitle(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "album_details":
                System.out.println("detalhes album!!!");
                try {
                    temp = funcoesBD.detalhesAlbum(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "artist_details":
                System.out.println("detalhes artista!!!");
                try {
                    temp = funcoesBD.detalhesArtista(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "music_details":
                System.out.println("detalhes musica!!!");
                try {
                    temp = funcoesBD.detalhesMusica(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "album_critic":
                System.out.println("critica album!!!");
                try {
                    temp = funcoesBD.writeReview(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "create_concert":
                System.out.println("cria concerto!!!");
                try {
                    temp = funcoesBD.addConcert(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "add_compositor":
                System.out.println("adiciona compositores!!!");
                try {
                    temp = funcoesBD.addCompositores(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "new_playlist":
                System.out.println("cria playlist!!!");
                try {
                    temp = funcoesBD.addPlaylist(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "artist_edit":
                System.out.println("edita artista!!!");
                try {
                    temp = funcoesBD.editArtista(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "album_edit":
                System.out.println("edita album!!!");
                try {
                    temp = funcoesBD.editAlbum(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "music_edit":
                System.out.println("edita musica!!!");
                try {
                    temp = funcoesBD.editMusica(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "delete_artist":
                System.out.println("apaga artista!!!");
                try {
                    temp = funcoesBD.deleteArtista(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "delete_album":
                System.out.println("apaga album!!!");
                try {
                    temp = funcoesBD.deleteAlbum(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            case "delete_music":
                System.out.println("apaga musica!!!");
                try {
                    temp = funcoesBD.deleteMusica(this.map);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(temp);
                resposta = string2packet(temp);
                break;
            default:
                resposta = string2packet("type|unknown"+map.toString());
        }
        try {
            if(myTurn) {
                System.out.println("enviei a resposta");
                socket.send(resposta);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nao consegui enviar a resposta.");
        }

    }

    private DatagramPacket string2packet(String temp) {
        byte[] buffer = temp.getBytes();
        return new DatagramPacket(buffer, buffer.length, group, PORT);
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



/*<====================================================><====================================================><====================================================>
<====================================================><====================================================><====================================================>*/


//Classes Musica (EM ARVORE, hierarquico) !!!!!!!!!


class Artista implements Serializable {
    public String nome;
    public String descricao;
    public String dataNascimento;

    public Artista(String nome, String descricao, String dataNascimento) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataNascimento = dataNascimento;
    }
}

class Album implements Serializable {
    public String Artista;
    public String descricao;
    public String nome;
    public float ratingAVG;
    public String genero;
    public String dataLancamento;
    public String editora;

    public Album(String artista, String descricao, String nome, float ratingAVG, String genero,String dataLancamento, String editora) {
        Artista = artista;
        this.descricao = descricao;
        this.nome = nome;
        this.ratingAVG = ratingAVG;
        this.genero = genero;
        this.dataLancamento = dataLancamento;
        this.editora = editora;
    }
}

class Musica implements Serializable {
    public String nome;
    public String Artista;
    public String Album;
    public String letra;

    public Musica(String nome, String artista, String album, String letra) {
        this.nome = nome;
        this.Artista = artista;
        this.Album = album;
        this.letra = letra;
    }
}

class Review implements Serializable {
    public String Artista;
    public String Album;
    public String descricao;
    public float rating;
    public String editor;

    public Review(String artista, String album, String descricao, float rating, String nome) {
        Artista = artista;
        Album = album;
        this.descricao = descricao;
        this.rating = rating;
        this.editor = nome;
    }
}