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
                System.out.println("   1)Introduzir novo artista (necessita permissões de editor)");
                System.out.println("   2)Introduzir novo album (necessita permissões de editor)");
                System.out.println("   3)Introduzir nova musica (necessita permissões de editor)");
                System.out.println("   4)Pesquisar album (FALTA IMPLEMENTAR PARTE DO MULTICAST SERVER)");
                System.out.println("   5)Consultar detalhes de album (incluindo musicas e criticas)");
                //System.out.println("   6)Editar detalhes de album (incluindo musicas) (POR IMPLEMENTAR)");
                //System.out.println("   7)Escrever critica sobre um album (com pontuacao) (POR IMPLEMENTAR)");
                //System.out.println("   8)Consultar detalhes de artista (e.g., discografia, biografia) (POR IMPLEMENTAR)");
                System.out.println("   9)Dar privilegios de editor a um utilizador");
                //System.out.println("   10)Upload de ficheiro para associar a uma musica existente (POR IMPLEMENTAR)");
                //System.out.println("   11)Partilhar um ficheiro musical e permitir o respetivo download (POR IMPLEMENTAR)");
                System.out.println("   12)Logout");
                System.out.print(">>");
                opcao = sc.nextInt();

                try{
                    switch(opcao){ 
                        case 1:
                            introduzirArtistaRMIClient(rmiinterface, username);
                            break;
                        case 2:
                            introduzirAlbumRMIClient(rmiinterface, username);
                            break;
                        case 3:
                            introduzirMusicaRMIClient(rmiinterface, username);
                            break;
                        case 4:
                            pesquisarAlbumRMIClient(rmiinterface, username);
                            break;
                        //case 5:
                        //    consultarDetalhesAlbumRMIClient(rmiinterface, username);
                        //    break;
                        case 9:
                            privilegiosEditorRMIClient(rmiinterface, username, password);
                            break;
                        case 12:
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
        System.out.println("Server: "+text);
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
        System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Utilizador registado com sucesso!");
            //return true;
            return false;
        }
        else if(text.equals("rejected")){
            System.out.println("Registo rejeitado. Tente novamente.");
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
        System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Utilizador "+usernameDest+" promovido com sucesso!");
            return;
        }
        else if(text.equals("wrongusername")){
            System.out.println("Os seus dados nao correspondem a um utilizador ja registado.");
            return;
        }
        else if(text.equals("wrongusernameDest")){
            System.out.println("O utilizador que esta a tentar promover nao corresponde a um utilizador ja registado.");
            return;
        }
        else if(text.equals("rejected")){
            System.out.println("Nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
    }

    public static void introduzirArtistaRMIClient(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, description, birth_date;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----INTRODUZIR NOVO ARTISTA-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Descricao:");
        description = sc.nextLine();
        System.out.print("Data nascimento:");
        birth_date = sc.nextLine();

        text = rmiinterface.introduzirArtistaRMIServer(username, artist_name, description, birth_date);
        System.out.println("Server: "+text);
        if(text.equals("accepted")){
            System.out.println("Artista adicionado com sucesso!");
            return;            
        }
        else if(text.equals("rejected")){
            System.out.println("Artista ja existente.");
            return;
        }
        else if(text.equals("nopermission")){
            System.out.println("O utilizador nao tem permissao para esta acao.");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        return;
    }

    public static void introduzirAlbumRMIClient(RMIInterface rmiinterface, String username) throws RemoteException{
        String text, artist_name, album_name, description;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-----INTRODUZIR NOVO ALBUM-----");
        System.out.print("Nome do artista:");
        artist_name = sc.nextLine();
        System.out.print("Nome do album:");
        album_name = sc.nextLine();
        System.out.print("Descricao:");
        description = sc.nextLine();

        text = rmiinterface.introduzirAlbumRMIServer(username, artist_name, album_name, description);
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
                artist_name = sc.nextLine();
                text = rmiinterface.pesquisarAlbumPorArtistaRMIServer(username, artist_name);
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
        if(text.equals("accepted")){
            System.out.println("Album existente");
            return;            
        }
        else if(text.equals("empty")){
            System.out.println("Artista sem albuns");
            return;
        }
        else if(text.equals("notfound")){
            System.out.println("Album nao existente");
            return;
        }
        System.out.println("Ocorreu alguma falha no sistema. Tente novamente.");
        System.out.println("POR IMPLEMENTAR!");
        return;
    }
    
}
