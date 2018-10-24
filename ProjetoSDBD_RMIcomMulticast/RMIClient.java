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
        RMIClient client = new RMIClient();
        client.setName("RMIClient");
        /*
        Exception e = null;
        int regNumber = 7000;
        e = client.connectServer(regNumber);
        while(e != null){
            if(regNumber == 7000){
                regNumber = 7001;
                e = client.connectServer(regNumber);
            }
            if(regNumber == 7001){
                regNumber = 7000;
                e = client.connectServer(regNumber);
            }
        }*/
        client.connectServer(7000);
        //Registar ou login?
    }
    
    public static void loginRMIClient(RMIInterface rmiinterface) throws RemoteException{
        String text, username, password;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----LOGIN-----");
        System.out.print("Username:");
        username = sc.nextLine();
        System.out.print("Password:");
        password = sc.nextLine();
        text = rmiinterface.loginRMIServer(username, password);
        System.out.println("Server: "+text);
    }
     
    public static void registarRMIClient(RMIInterface rmiinterface) throws RemoteException{
        String text, username, password;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n-----REGISTAR NOVO UTILIZADOR-----");
        System.out.print("Username:");
        username = sc.nextLine();
        System.out.print("Password:");
        password = sc.nextLine();
        text = rmiinterface.registarRMIServer(username, password);
        System.out.println("Server: "+text);
    }
    
    private void connectServer(int regNumberReceived) {
        Scanner sc = new Scanner(System.in);
        String string, text;
        int opcao=0, regNumber = regNumberReceived;
        boolean trueOrFalse = true;
        
        try{
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", regNumber);
            RMIInterface rmiinterface = (RMIInterface) reg.lookup("rmiserver");
            System.out.println(getName()+" connected to RMIServer registry "+regNumber);
            
            System.out.println("\nO que deseja fazer?\n   1)Registar\n   2)Login");
            while(opcao!=1 && opcao!=2){
                System.out.print(">>");
                opcao = sc.nextInt();
                if(opcao==1)
                    registarRMIClient(rmiinterface);
                else if(opcao==2)
                    loginRMIClient(rmiinterface);
            }
            
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
                System.out.print(">>");
                string = sc.nextLine();
                if(string.equals("exit")){
                    trueOrFalse = false;
                }
                text = rmiinterface.getData(string);
                System.out.println("Server: "+text);
            }

        }catch (Exception e) {
            System.out.println(e);
            //e.printStackTrace();
            
            if(regNumber == 7000)
                connectServer(7001);
            else if(regNumber == 7001)
                connectServer(7000);
        }
    }
    
}
