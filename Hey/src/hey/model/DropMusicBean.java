/**
 * Raul Barbosa 2014-11-07
 */
package hey.model;


import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmiserver.RMIInterface;

public class DropMusicBean {
	private RMIInterface server;
	private String username; // username and password supplied by the user
	private String password;
	//Artista
	private String artist_name;
	private String description;
	private String birth_date;
	private String groups;

	public DropMusicBean() {
		try {
			Registry reg = LocateRegistry.getRegistry("127.0.0.1", 7000);
			server = (RMIInterface) reg.lookup("rmiserver");
			System.out.println("Connected to rmiserver.RMIServer registry "+7000);
		}
		catch(NotBoundException|RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

//	public boolean addArtista() throws RemoteException {
//		return server.introduzirArtistaRMIServer(this.); //ALTERAR AQUI
//	}

	public boolean registarUser() throws RemoteException {
		return server.registarRMIServer(this.username, this.password);
	}

	public String loginUser() throws RemoteException {
		return server.loginRMIServer(this.username, this.password);
	}

	public String addArtista() throws RemoteException {
		return server.introduzirArtistaRMIServer(this.username, this.artist_name, this.description, this.birth_date, this.groups);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}

	public void setGroups(String grupos) {
		this.groups = grupos;
	}
}
