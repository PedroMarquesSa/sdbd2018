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
        while(trueOrFalse == true){
            sc.nextLine();
            System.out.println("\nQue pretende fazer?");
            System.out.println("   1)Introduzir novos dados (necessita permissões de editor)");
            System.out.println("   2)Pesquisar album");
            System.out.println("   3)Consultar detalhes de album (c/ opcao para editar)");
            System.out.println("   4)Escrever critica sobre um album");
            System.out.println("   5)Consultar detalhes de artista (e.g., discografia, biografia)");
            System.out.println("   6)Dar privilegios de editor a um utilizador");
            //System.out.println("   6)Criar concerto");
            //System.out.println("   7)Upload de ficheiro para associar a uma musica existente)");
            //System.out.println("   8)Partilhar um ficheiro musical e permitir o respetivo download");
            System.out.println("   9)Logout");
            System.out.print(">>");
            opcao = sc.nextInt();

            try{
                switch(opcao){
                    case 1:
                        introduzirDados(rmiinterface, username);
                        break;
                    case 2:
                        pesquisarAlbumRMIClient(rmiinterface, username);
                        break;
                    case 3:
                        consultarDetalhesAlbumRMIClient(rmiinterface, username);
                        break;
                    case 4:
                        escreverCriticaAlbumRMIClient(rmiinterface, username);
                        break;
                    case 5:
                        consultarDetalhesArtistaRMIClient(rmiinterface, username);
                        break;
                    case 6:
                        privilegiosEditorRMIClient(rmiinterface, username, password);
                        break;
                    case 7:
                        //escreverCriticaAlbumRMIClient(rmiinterface, username);
                        break;
                    case 8:
                        //criarConcertoRMIClient(rmiinterface, username);
                        break;
                    case 9:
                        trueOrFalse = false;
                        break;
                    default:
                        System.out.println("Opcao invalida!");
                        break;
                }
            }
            catch(Exception e){
                System.out.println(e);
            }

        }

    }

    public static boolean loginRMIClient(RMIInterface rmiinterface, String username, String password) throws RemoteException{
        String text;
        text = rmiinterface.loginRMIServer(username, password);
        //System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Login efetuado com sucesso!");
            return true;
        }
        else if(text.equals("wrongusername")){
            System.out.println("Utilizador inexistente!");
            return false;
        }
        else if(text.equals("wrongpassword")){
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

    public static void introduzirDados(RMIInterface rmiinterface, String username){
        int opcao;

        System.out.println("\n-----QUE TIPO DE DADOS PRETENDE INTRODUZIR?-----");
        System.out.println("   1)Artista");
        System.out.println("   2)Grupo");
        System.out.println("   3)Album");
        System.out.println("   4)Musica");
        System.out.print(">>");
        Scanner sc = new Scanner(System.in);
        opcao = sc.nextInt();

        try{
            switch(opcao){
                case 1:
                    introduzirArtistaRMIClient(rmiinterface, username, false);
                    break;
                case 2:
                    introduzirArtistaRMIClient(rmiinterface, username, true);
                    break;
                case 3:
                    introduzirAlbumRMIClient(rmiinterface, username);
                    break;
                case 4:
                    introduzirMusicaRMIClient(rmiinterface, username);
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

    public static void introduzirArtistaRMIClient(RMIInterface rmiinterface, String username, Boolean grupo) throws RemoteException{
        String text, artist_name, description, birth_date, auxGrupo = null;
        Scanner sc = new Scanner(System.in);
        int nGrupos, i=0;
        String[] grupos;

        System.out.println("\n-----INTRODUZIR NOVO ARTISTA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Descricao:");
        description = sc.nextLine();
        System.out.print("Data nascimento (dd.mm.aaaa):");
        birth_date = sc.nextLine();

        if(grupo==true){
            grupos = new String[1];
            grupos[0] = artist_name;
        }
        else{
            System.out.print("Quantos grupos:");
            nGrupos = sc.nextInt();
            if(nGrupos == 0) {
                grupos = new String[1];
                grupos[0] = " ";
            }
            else {
                grupos = new String[nGrupos];
                for(i=0; i<nGrupos; i++){
                    System.out.println("Grupo " + (i+1) + ":");
                    //while( auxGrupo.equals(null))
                    sc = new Scanner(System.in);
                    auxGrupo = sc.nextLine();
                    grupos[i] = auxGrupo;
                    //auxGrupo = null;
                }
            }
        }

        text = rmiinterface.introduzirArtistaRMIServer(username, artist_name, description, birth_date, grupos);
        System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Adicionado com sucesso!");
            return;
        }
        else if(text.equals("rejected")){
            System.out.println("Ja existente.");
            return;
        }
        else if(text.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void introduzirGrupo(RMIInterface rmiinterface, String username) throws RemoteException{
        return;
    }

    public static void introduzirAlbumRMIClient(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, description, genero, dataLancamento, recordlabel;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----INTRODUZIR NOVO ALBUM-----");
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

        text = rmiinterface.introduzirAlbumRMIServer(username, artist_name, album_name, description, genero, dataLancamento, recordlabel);
        System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Album adicionado com sucesso!");
            return;
        }
        else if(text.equals("rejected")){
            System.out.println("Album ja existente.");
            return;
        }
        else if(text.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void introduzirMusicaRMIClient(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, music_name, description;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----INTRODUZIR NOVA MUSICA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Nome da musica:");
        music_name = sc.nextLine();

        text = rmiinterface.introduzirMusicaRMIServer(username, artist_name, album_name, music_name);
        System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Musica adicionada com sucesso!");
            return;
        }
        else if(text.equals("rejected")){
            System.out.println("Musica ja existente.");
            return;
        }
        else if(text.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        else if(text.equals("noartist")){
            System.out.println("O artista indicado nao existe");
            return;
        }
        else if(text.equals("noalbum")){
            System.out.println("O album indicado nao existe");
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

        protocol = "type|album_details;artist_name|"+artist_name;
        text = rmiinterface.enviarParaMulticast(protocol);
        System.out.println("Server: "+text);

        return;
    }

    public static void escreverCriticaAlbumRMIClient(RMIInterface rmiinterface, String username) throws RemoteException {
        String text, artist_name, album_name, music_name, description, protocol, critic;
        int rating;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----ESCREVER NOVA MUSICA-----");
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

    /*
    public static void criarConcertoRMIClient(RMIInterface rmiinterface, String username) throws RemoteException {
        String protocol, artistas, concert_name;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----CRIAR CONCERTO-----");
        System.out.print("Nome do concerto:");
        concert_name = sc.nextLine();


        protocol = "type|create_concert;username|"+username+";artist_name|artista1,artista2,artista3;|"+concert_name+";date|20.06.2019;local|rua4;id|123456789;serverId|1;status|null";
        return;
    }*/

}
