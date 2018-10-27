
import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Renato Matos
 */
public interface RMIInterface extends Remote{
	
    public String loginRMIServer(String text, String password) throws RemoteException;
    
    public String registarRMIServer(String text, String password) throws RemoteException;

    public Boolean ping() throws RemoteException;
}
