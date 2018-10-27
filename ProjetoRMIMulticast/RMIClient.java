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
                try{
                    trueOrFalse = loginRMIClient(rmiinterface);
                }catch (Exception e) {
                    System.out.println(e);
                } 
            }
        }
        


        trueOrFalse = true;
        while(trueOrFalse == true){
                sc.nextLine();
                System.out.println("\nQue pretende fazer?");
                System.out.println("   1)Introduzir artistas, albuns e musicas");
                System.out.println("   2)Pesquisar albuns por artista e por titulo de album");
                System.out.println("   3)Consultar detalhes de album (incluindo musicas e criticas)");
                System.out.println("   4)Editar detalhes de album (incluindo musicas)");
                System.out.println("   5)Escrever critica sobre um album (com pontuacao)");
                System.out.println("   6)Consultar detalhes de artista (e.g., discografia, biografia)");
                System.out.println("   7)Dar privilegios de editor a um utilizador");
                System.out.println("   8)Upload de ficheiro para associar a uma musica existente");
                System.out.println("   9)Partilhar um ficheiro musical e permitir o respetivo download");
                System.out.println("   10)Logout");
                System.out.print(">>");
                opcao = sc.nextInt();

                if(opcao==10){
                    trueOrFalse = false;
                }
                
            }
            
    }
    
    public static boolean loginRMIClient(RMIInterface rmiinterface) throws RemoteException{
        String text, username, password;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----LOGIN-----");
        System.out.print("Username:");
        username = sc.nextLine();
        System.out.print("Password:");
        password = sc.nextLine();
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
            System.out.println("Utilizador inexistente!");
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
    
}
