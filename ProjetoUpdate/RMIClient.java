import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Renato Matos
 */
public class RMIClient {

    private String name = null;

    public String getName(){
        return name;
    }

    static public String whatIsIt = "normal";

    public void setName(String givenName) {
        name = givenName;
    }

    public static void main(String args[]){

        Scanner sc = new Scanner(System.in);
        int regNumber=7000, opcao=0;
        Registry reg = null;
        RMIInterface rmiinterface = null;
        boolean trueOrFalse;

        String username=null, password=null;
        Boolean isEditor;

        try{
            reg = LocateRegistry.getRegistry("127.0.0.1", regNumber);
            rmiinterface = (RMIInterface) reg.lookup("rmiserver");
            System.out.println("Connected to RMIServer registry "+regNumber);
            //registarRMIClient(rmiinterface);
        }catch (Exception e) {
            //System.out.println(e);
            System.out.println("Sem conexao ao servidor!");
            System.exit(0);
            //e.printStackTrace();
        }


        trueOrFalse = false;
        while(trueOrFalse==false){
            System.out.println("\nO que deseja fazer?\n   1)Registar\n   2)Login");
            System.out.print(">>");
            opcao = sc.nextInt();

            while(opcao!=1 && opcao!=2){
                System.out.println("Opcao nao encontrada!");
                System.out.println("\nO que deseja fazer?\n   1)Registar\n   2)Login");
                System.out.print(">>");
                opcao = sc.nextInt();
            }

            if(opcao==1){
                try{
                    trueOrFalse = registarRMIClient(rmiinterface);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }
            else if(opcao==2){
                String lixo = sc.nextLine();
                System.out.println("\n-----LOGIN-----");
                System.out.print("Username:");
                username = sc.nextLine();
                System.out.print("Password:");
                password = sc.nextLine();

                try{
                    trueOrFalse = loginRMIClient(rmiinterface, username, password);
                }catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        System.out.println("Prima enter");

        trueOrFalse = true;
        while(trueOrFalse == true) {
            if(whatIsIt.equals("editor")) {
                trueOrFalse = menuEditor(rmiinterface, username, password);
            }
            else{
                trueOrFalse = menuUtilizador(rmiinterface, username, password);
            }
        }

    }

    public static boolean menuEditor(RMIInterface rmiinterface, String username, String password){
        int opcao;
        Scanner sc = new Scanner(System.in);
        System.out.println("\nQue pretende fazer?");
        System.out.println("   1)Adicionar dados");
        System.out.println("   2)Modificar dados");
        System.out.println("   3)Apagar dados");
        System.out.println("   4)Conceder privilegios de editor a um utilizador");
        System.out.println("   5)Pesquisar artista");
        System.out.println("   6)Pesquisar album");
        System.out.println("   7)Pesquisar musica");
        System.out.println("   8)Criar playlist");
        System.out.println("   9)Editar playlist");
        System.out.println("   10)Escrever critica a um album");
        System.out.println("   11)Upload de ficheiro para associar a uma musica");
        System.out.println("   12)Logout");
        System.out.print(">>");
        opcao = sc.nextInt();

        try{
            switch(opcao){
                case 1:
                    System.out.println("adicionar dados");
                    adicionarDados(rmiinterface, username);
                    break;
                case 2:
                    System.out.println("modificar dados");
                    //modificarDados(rmiinterface, username);
                    break;
                case 3:
                    System.out.println("apagar dados");
                    apagarArtista(rmiinterface, username);
                    //apagarDados(rmiinterface, username);
                    break;
                case 4:
                    System.out.println("conceder privilegios editor");
                    privilegiosEditorRMIClient(rmiinterface, username, password);
                    break;
                case 5:
                    System.out.println("pesquisar artista");
                    //pesquisarArtista(rmiinterface, username);
                    consultarDetalhesArtistaRMIClient(rmiinterface, username);
                    break;
                case 6:
                    System.out.println("pesquisar album");
                    pesquisarAlbumRMIClient(rmiinterface, username);
                    break;
                case 7:
                    System.out.println("pesquisar musica");
                    //pesquisarMusica(rmiinterface, username);
                    break;
                case 8:
                    System.out.println("criar playlist");
                    //criarPlaylist(rmiinterface, username);
                    break;
                case 9:
                    System.out.println("editar playlist");
                    editarPlaylist(rmiinterface, username);
                    break;
                case 10:
                    System.out.println("escrever critica");
                    escreverCriticaAlbumRMIClient(rmiinterface, username);
                    break;
                case 11:
                    System.out.println("upload ficheiro musica");
                    //uploadFicheiroMusica(rmiinterface, username);
                    break;
                case 12:
                    System.out.println("logout");
                    return false;
                default:
                    System.out.println("Opcao invalida!");
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean menuUtilizador(RMIInterface rmiinterface, String username, String password){
        int opcao;
        Scanner sc = new Scanner(System.in);
        System.out.println("\nQue pretende fazer?");
        System.out.println("   1)Pesquisar artista");
        System.out.println("   2)Pesquisar album");
        System.out.println("   3)Pesquisar musica");
        System.out.println("   4)Criar playlist");
        System.out.println("   5)Editar playlist");
        System.out.println("   6)Escrever critica a um album");
        System.out.println("   7)Upload de ficheiro para associar a uma musica");
        System.out.println("   8)Logout");
        System.out.print(">>");
        opcao = sc.nextInt();

        try{
            switch(opcao){
                case 1:
                    System.out.println("pesquisar artista");
                    //pesquisarArtista(rmiinterface, username);
                    break;
                case 2:
                    System.out.println("pesquisar album");
                    //pesquisarAlbum(rmiinterface, username);
                    break;
                case 3:
                    System.out.println("pesquisar musica");
                    //pesquisarMusica(rmiinterface, username);
                    break;
                case 4:
                    System.out.println("criar playlist");
                    //criarPlaylist(rmiinterface, username);
                    break;
                case 5:
                    System.out.println("editar playlist");
                    //editarPlaylistt(rmiinterface, username);
                    break;
                case 6:
                    System.out.println("escrever critica");
                    //escreverCriticaAlbum(rmiinterface, username);
                    break;
                case 7:
                    System.out.println("upload ficheiro musica");
                    //uploadFicheiroMusica(rmiinterface, username);
                    break;
                case 8:
                    System.out.println("logout");
                    return false;
                default:
                    System.out.println("Opcao invalida!");
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }


    public static boolean loginRMIClient(RMIInterface rmiinterface, String username, String password) throws RemoteException{
        String text, protocol;

        //text = rmiinterface.loginRMIServer(username, password);
        //System.out.println("Server: "+text);

        protocol = "type|login;username|"+username+";password|"+password;
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] partsWhat = parts[3].split("\\|");
        whatIsIt = partsWhat[1];
        System.out.println("whatIsIt="+whatIsIt);
        String[] parts2 = parts[5].split("\\|");
        String status = parts2[1];
        System.out.println("status="+status);


        if(status.equals("accepted")){
            System.out.println("Login efetuado com sucesso!");
            return true;
        }
        else if(status.equals("wrongusername")){
            System.out.println("Utilizador inexistente!");
            return false;
        }
        else if(status.equals("wrongpassword")){
            System.out.println("Password incorreta!");
            return false;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return false;
    }

    public static boolean registarRMIClient(RMIInterface rmiinterface) throws RemoteException{
        String text, username, password;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----REGISTAR NOVO UTILIZADOR-----");
        System.out.print("Username:");
        username = sc.nextLine();
        System.out.print("Password:");
        password = sc.nextLine();
        text = rmiinterface.registarRMIServer(username, password);
        //System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Utilizador registado com sucesso!");
            //return true;
            return false;
        }
        else if(text.equals("rejected")){
            System.out.println("Registo rejeitado. Tente registar-se com outro username.");
            return false;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return false;
    }

    public static void privilegiosEditorRMIClient(RMIInterface rmiinterface, String usernameReceived, String passwordReceived) throws RemoteException{
        String usernameDest, username = usernameReceived, password = passwordReceived, text;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----QUAL O USERNAME DO UTILIZADOR QUE PRETENDE PROMOVER A EDITOR-----");
        System.out.print("Username utilizador:");
        usernameDest = sc.nextLine();
        text = rmiinterface.privilegiosEditorRMIServer(username, password, usernameDest);
        //System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Utilizador "+usernameDest+" promovido com sucesso!");
            return;
        }
        else if(text.equals("wrongusername")){
            System.out.println("Os seus dados nao correspondem a um utilizador ja registado.");
            return;
        }
        else if(text.equals("wrongusernameDest")){
            System.out.println("O utilizador indicado nao corresponde a um utilizador ja registado.");
            return;
        }
        else if(text.equals("rejected")){
            System.out.println("Nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
    }

    public static void adicionarDados(RMIInterface rmiinterface, String username){
        int opcao;

        System.out.println("\n-----QUE TIPO DE DADOS PRETENDE ADICIONAR?-----");
        System.out.println("   1)Artista");
        System.out.println("   2)Grupo");
        System.out.println("   3)Album");
        System.out.println("   4)Musica");
        System.out.println("   5)Compositores");
        System.out.println("   6)Concerto");
        System.out.print(">>");
        Scanner sc = new Scanner(System.in);
        opcao = sc.nextInt();

        try{
            switch(opcao){
                case 1:
                    System.out.println("adicionar artista");
                    adicionarArtista(rmiinterface, username);
                    break;
                case 2:
                    System.out.println("adicionar grupo");
                    adicionarGrupo(rmiinterface, username);
                    break;
                case 3:
                    System.out.println("adicionar album");
                    adicionarAlbum(rmiinterface, username);
                    break;
                case 4:
                    System.out.println("adicionar musica");
                    adicionarMusica(rmiinterface, username);
                    break;
                case 5:
                    System.out.println("adicionar compositor");
                    adicionarCompositores(rmiinterface, username);
                    break;
                case 6:
                    System.out.println("adicionar concerto");
                    adicionarConcerto(rmiinterface, username);
                    break;
                default:
                    System.out.println("Opcao invalida!");
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return;
    }

    public static void adicionarArtista(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, description, birth_date, auxGrupo, grupos = null, protocol;
        Scanner sc = new Scanner(System.in);
        int nGrupos, i;

        System.out.println("\n-----ADICIONAR NOVO ARTISTA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Descricao:");
        description = sc.nextLine();
        System.out.print("Data nascimento (dd.mm.aaaa):");
        birth_date = sc.nextLine();

        /*
        System.out.print("A quantos grupos pertence:");
        nGrupos = sc.nextInt();
        if(nGrupos==0){
            grupos = " ";
        }
        else{
            for(i=0; i<nGrupos-1; i++){
                System.out.println("Grupo:");
                sc = new Scanner(System.in);
                auxGrupo = sc.nextLine();
                grupos += auxGrupo+",";
            }
            System.out.println("Grupo:");
            auxGrupo = sc.nextLine();
            grupos += auxGrupo;

        }
        */

        System.out.print("Grupos:");
        grupos = sc.nextLine();
        protocol = "type|new_artist;username|"+username+";artist_name|"+artist_name+";grupo|"+grupos+";description|"+description+";birth_date|"+birth_date;

        text = rmiinterface.enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[4].split("\\|");
        String status = parts2[1];
        //System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Artista adicionado com sucesso!");
            return;
        }
        else if(status.equals("rejected")){
            System.out.println("Artista ja existente.");
            return;
        }
        else if(status.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void adicionarGrupo(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, description, birth_date, protocol;
        Scanner sc = new Scanner(System.in);

        //type|new_artist;username|nome;artist_name|Brockhampton;grupo|Brockhampton;description|mensagem;birth_date|01.12.1998;id|123456789;serverId|1;status|null;

        System.out.println("\n-----ADICIONAR NOVO GRUPO-----");
        System.out.print("Nome do grupo:");
        artist_name = sc.nextLine();
        System.out.print("Descricao:");
        description = sc.nextLine();
        System.out.print("Data criacao (dd.mm.aaaa):");
        birth_date = sc.nextLine();

        protocol = "type|new_artist;username|"+username+";artist_name|"+artist_name+";grupo|"+artist_name+";description|"+description+";birth_date|"+birth_date;

        text = rmiinterface.enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[4].split("\\|");
        String status = parts2[1];
        //System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Grupo adicionado com sucesso!");
            return;
        }
        else if(status.equals("rejected")){
            System.out.println("Grupo ja existente.");
            return;
        }
        else if(status.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void adicionarAlbum(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, description, genero, dataLancamento, recordlabel, protocol;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----ADICIONAR NOVO ALBUM-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Descricao:");
        description = sc.nextLine();
        System.out.print("Genero:");
        genero = sc.nextLine();
        System.out.print("Data lancamento:");
        dataLancamento = sc.nextLine();
        System.out.print("Editora:");
        recordlabel = sc.nextLine();

        protocol = "type|new_album;username|"+username+";artist_name|"+artist_name+";album_name|"+album_name+";description|"+description+";genre|"+genero+";releaseDate|"+dataLancamento+";recordlabel|"+recordlabel;
        text = rmiinterface.enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[5].split("\\|");
        String status = parts2[1];
        //System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Album adicionado com sucesso!");
            return;
        }
        else if(status.equals("rejected")){
            System.out.println("Album ja existente.");
            return;
        }
        else if(status.equals("noartist")){
            System.out.println("O artista indicado nao existe");
            return;
        }
        else if(status.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void adicionarMusica(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, music_name, lyrics, protocol;
        Scanner sc = new Scanner(System.in);

        //type|new_music;username|nome;artist_name|Radiohead;album_name|OK Computer;music_name|Karma Police;lyrics|letraletraletra;id|123456789;serverId|1;status|null

        System.out.println("\n-----ADICIONAR NOVA MUSICA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Nome da musica:");
        music_name = sc.nextLine();
        System.out.print("Letra:");
        lyrics = sc.nextLine();

        protocol = "type|new_music;username|"+username+";artist_name|"+artist_name+";album_name|"+album_name+";music_name|"+music_name+";lyrics|"+lyrics;
        text = rmiinterface.enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[6].split("\\|");
        String status = parts2[1];
        System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Musica adicionada com sucesso!");
            return;
        }
        else if(status.equals("rejected")){
            System.out.println("Musica ja existente.");
            return;
        }
        else if(status.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        else if(status.equals("noartist")){
            System.out.println("O artista indicado nao existe");
            return;
        }
        else if(status.equals("noalbum")){
            System.out.println("O album indicado nao existe");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void adicionarCompositores(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, music_name, protocol, compositores=null, auxCompositor;
        int nCompositores, i;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----ADICIONAR NOVOS COMPOSITORES-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Nome da musica:");
        music_name = sc.nextLine();

        /*
        System.out.print("Quantos compositores:");
        nCompositores = sc.nextInt();

        if(nCompositores==0){
            compositores = " ";
        }
        else{
            for(i=0; i<nCompositores-1; i++){
                System.out.println("Grupo:");
                sc = new Scanner(System.in);
                auxCompositor = sc.nextLine();
                compositores += auxCompositor+",";
            }
            System.out.println("Grupo:");
            auxCompositor = sc.nextLine();
            compositores += auxCompositor;

        }
        */

        System.out.println("Compositores (no caso de serem mais que 1 separar por vírgula):");
        compositores = sc.nextLine();

        protocol = "type|add_compositor;username|"+username+";artist_name|"+artist_name+";album_name|"+album_name+";music_name|"+music_name+";compositor_name|"+compositores;
        text = rmiinterface.enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[6].split("\\|");
        String status = parts2[1];
        //System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Adicionado com sucesso!");
            return;
        }
        else if(status.equals("notfound")){
            System.out.println("Nao encontrado.");
            return;
        }
        else if(status.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void adicionarConcerto(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, music_name, protocol, artistas="", auxArtista, concert_name, data, local;
        int nArtistas, i;
        Scanner sc = new Scanner(System.in);

        //type|create_concert;username|PedroSa;artist_name|artista1,artista2,artista3;concert_name|nomeconcerto;date|20.06.2019;local|rua4;id|123456789;serverId|1;status|null;

        System.out.println("\n-----ADICIONAR NOVO CONCERTO-----");
        System.out.print("Nome do concerto:");
        concert_name = sc.nextLine();
        System.out.print("Data(dd.mm.aaaa):");
        data = sc.nextLine();
        System.out.print("Local:");
        local = sc.nextLine();

        System.out.print("Quantos artistas:");
        nArtistas = sc.nextInt();

        if(nArtistas==0){
            artistas = " ";
        }
        else{
            for(i=0; i<nArtistas-1; i++){
                System.out.print("Artista:");
                sc = new Scanner(System.in);
                auxArtista = sc.nextLine();
                artistas += auxArtista+",";
            }
            System.out.print("Artista:");
            sc = new Scanner(System.in);
            auxArtista = sc.nextLine();
            artistas += auxArtista;

        }

        //type|create_concert;username|PedroSa;artist_name|artista1,artista2,artista3;concert_name|nomeconcerto;date|20.06.2019;local|rua4;id|123456789;serverId|1;status|null;

        protocol = "type|add_concert;username|"+username+";artist_name|"+artistas+";concert_name|"+concert_name+";data|"+data+";local|"+local;
        System.out.println(protocol);
        text = rmiinterface.enviarParaMulticast(protocol);
        //System.out.println("Server: "+text);

        String[] parts = text.split(";");
        String[] parts2 = parts[7].split("\\|");
        String status = parts2[1];
        //System.out.println("status="+status);

        if(text.equals("accepted")){
            System.out.println("Adicionado com sucesso!");
            return;
        }
        else if(text.equals("rejected")){
            System.out.println("Ja existe (local e data ja preenchidos");
            return;
        }
        else if(text.equals("noartists")){
            System.out.println("Artistas nao existentes.");
            return;
        }
        else if(text.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void pesquisarAlbumRMIClient(RMIInterface rmiinterface, String username) throws RemoteException{
        int opcao;
        String text=null, artist_name, album_name, music_name, description;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----PESQUISAR-----");
        System.out.println("   1)Por nome do artista");
        System.out.println("   2)Por titulo do album");
        System.out.print(">>");
        opcao = sc.nextInt();

        switch(opcao){
            case 1:
                System.out.print("Nome do artista:");
                sc = new Scanner(System.in);
                artist_name = sc.nextLine();
                text = rmiinterface.pesquisarAlbumPorArtistaRMIServer(username, artist_name);
                System.out.println(text);
                break;
            case 2:
                System.out.print("Titulo do album:");
                album_name = sc.nextLine();
                text = rmiinterface.pesquisarAlbumPorTituloRMIServer(username, album_name);
                break;
            default:
                System.out.println("Opcao invalida!");
                break;
        }

        System.out.println("Server: "+text);
        String[] parts = text.split(";");
        String[] parts2 = parts[3].split("\\|");
        String status = parts2[1];
        System.out.println("status="+status);

        if(status.equals("accepted")){
            System.out.println("Album existente");
            return;
        }
        else if(status.equals("empty")){
            System.out.println("Artista sem albuns");
            return;
        }
        else if(status.equals("notfound")){
            System.out.println("Artista/album nao exitente");
            return;
        }

        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");

        return;
    }


    public static void consultarDetalhesMusica(RMIInterface rmiinterface, String username) throws RemoteException{
        String artist_name, album_name, music_name, protocol, text;
        Scanner sc = new Scanner(System.in);
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Nome da musica:");
        music_name = sc.nextLine();

        protocol = "type|music_details;artist_name|"+artist_name+";album_name|"+album_name+";music_name|"+music_name;

        text = rmiinterface.enviarParaMulticast(protocol);

        System.out.println(text);
    }

    public static void consultarDetalhesAlbumRMIClient(RMIInterface rmiinterface, String username) throws RemoteException {
        String artist_name, album_name, protocol, text, detalhes;
        int opcao;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----CONSULTAR DETALHES ALBUM-----");
        System.out.print("Artista:");
        artist_name = sc.nextLine();
        System.out.print("Titulo do album:");
        album_name = sc.nextLine();

        protocol = "type|album_details;artist_name|"+artist_name+";album_name|"+album_name;
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);

        System.out.println("\nPretende editar os detalhes?");
        System.out.println("   1)Sim");
        System.out.println("   2)Nao");
        System.out.print(">>");
        opcao = sc.nextInt();
        if(opcao == 1){
            System.out.println("Novos detalhes:");
            sc = new Scanner(System.in);
            detalhes = sc.nextLine();
            protocol = "type|new_album_details;album_name|"+artist_name+";details|"+detalhes;
            text = rmiinterface.enviarParaMulticast(protocol);
            System.out.println("Server: " + text);
        }
        return;
    }

    public static void consultarDetalhesArtistaRMIClient(RMIInterface rmiinterface, String username) throws RemoteException {
        String artist_name, protocol, text, detalhes;
        int opcao;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----CONSULTAR DETALHES ARTISTA-----");
        System.out.print("Artista:");
        artist_name = sc.nextLine();

        protocol = "type|artist_details;artist_name|"+artist_name;
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);

        return;
    }

    public static void escreverCriticaAlbumRMIClient(RMIInterface rmiinterface, String username) throws RemoteException {
        String text, artist_name, album_name, music_name, description, protocol, critic;
        int rating;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----ESCREVER CRITICA A UM ALBUM-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Critica:");
        critic = sc.nextLine();
        System.out.println("Rating:");
        rating = sc.nextInt();

        protocol = "type|album_critic;username|"+username+";artist_name|"+artist_name+";album_name|"+album_name+";critic|"+critic+";rating|"+rating;
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);
        return;
    }

    public static void uploadFicheiro(RMIInterface rmiinterface, String username) throws RemoteException {
        String text, artist_name, album_name, music_name, protocol, file_path;
        int rating;
        Scanner sc = new Scanner(System.in);

        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Nome da musica:");
        music_name = sc.nextLine();
        System.out.print("File Path:");
        file_path = sc.nextLine();

        protocol = "type|upload;music|"+music_name+";album|"+album_name+";artist|"+artist_name+";file|"+file_path;
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);
        return;
    }

    public static void adicionarPlaylist(RMIInterface rmiinterface, String username) throws RemoteException {
        /*
        String text, artist_name, album_name, music_name, protocol, file_path;
        int rating;
        Scanner sc = new Scanner(System.in);

        //falta inputs

        protocol = "type|new_playlist;username|"+username+";public|"+isPublic+";playlist_name|"+playlist_name
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);*/
        return;
    }

    public static void consultarConcerto(RMIInterface rmiinterface, String username) throws RemoteException {
        //por implementar
        return;
    }

    public static void editarArtista(RMIInterface rmiinterface, String username) throws RemoteException {
        //type|artist_edit;username|nome;artist_name|Quim Barreiros;id|123456789;serverId|1;status|null;
        //caso fique pending:
        //type|artist_edit;username|nome;artist_name|Quim Barreiros;bibliografia|;birth_date|11.11.2222;id|123456789;serverId|1;status|null; (caso nao altere algum campo, fica a branco)

        return;
    }

    public static void editarAlbum(RMIInterface rmiinterface, String username) throws RemoteException {
        //type|album_edit;username|nome;artist_name|Radiohead;album_name|OK Computer;id|123456789;serverId|1;status|null;
        //caso fique pending:
        //type|album_edit;username|nome;artist_name|Radiohead;album_name|OK Computer;genre|Rock;release_date|11.00.1111;description|descricao;id|123456789;serverId|1;status|null;

        return;
    }

    public static void editarMusica(RMIInterface rmiinterface, String username) throws RemoteException {
        //type|music_edit;username|nome;artist_name|Radiohead;album_name|OK Computer;music_name|nome;id|123456789;serverId|1;status|null;
        //caso fique pending:
        //type|music_edit;artist_name|Radiohead;album_name|OK Computer;music_name|nome;lyrics|;compositores|compositor1;id|123456789;status|null;

        return;
    }

    public static void editarPlaylist(RMIInterface rmiinterface, String username) throws RemoteException {
        //type|playlist_edit;username|nome;playlist_name|nome;id|123456789;serverId|1;status|null;
        //(caso fique pending): what -> delete (music -> musica(s) a apagar) // what -> add (music -> musica(s) a adicionar)
        //type|playlist_edit;username|nome;playlist_name|nome;what|delete;music|artista,album,musica+artista2,album2,musica2;id|123456789;serverId|1;status|null;
        return;
    }

    public static void editarConcerto(RMIInterface rmiinterface, String username) throws RemoteException {
        //por implementar
        return;
    }

    public static void apagarArtista(RMIInterface rmiinterface, String username) throws RemoteException {
        String artist_name, protocol, text;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----APAGAR ARTISTA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();

        protocol = "type|delete_artist;username|"+username+";artist_name|"+artist_name;

        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println(text);
        return;
    }

    public static void apagarAlbum(RMIInterface rmiinterface, String username) throws RemoteException {
        String artist_name, album_name, protocol, text;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----APAGAR ALBUM-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();

        protocol =  "type|delete_album;username|"+username+";artist_name|"+artist_name+";album|"+album_name;

        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println(text);
        return;
    }

    public static void apagarMusica(RMIInterface rmiinterface, String username) throws RemoteException {
        String artist_name, album_name, music_name, protocol, text;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----APAGAR MUSICA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Nome da musica:");
        music_name = sc.nextLine();

        protocol =  "type|delete_music;username|"+username+";artist_name|"+artist_name+";album|"+album_name+";music|"+music_name;

        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println(text);
        return;
    }

    public static void apagarPlaylist(RMIInterface rmiinterface, String username) throws RemoteException {

        String artist_name, album_name, music_name, protocol, text;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----APAGAR PLAYLIST-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();

        protocol = "type|delete_playlist;username|"+username+";name|"+artist_name;

        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println(text);

        return;
    }

    public static void apagarConcerto(RMIInterface rmiinterface, String username) throws RemoteException {
        //por implementar
        return;
    }

}
