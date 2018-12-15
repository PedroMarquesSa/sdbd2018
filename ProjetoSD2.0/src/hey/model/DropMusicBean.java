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
	private String artist_name, album_name;

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

	public boolean registarUser() throws RemoteException {
		return server.registarRMIServer(this.username, this.password);
	}

	public boolean loginUser() throws RemoteException {
		return server.loginRMIServer(this.username, this.password);
	}

	public String pesquisarArtista(String artista) throws RemoteException {
		System.out.println("Artista:"+artista);
		return server.pesquisarArtistaRMIServer(artista);
	}

	public String pesquisarAlbumPorArtista(String artista) throws RemoteException {
		System.out.println("Artista:"+artista);
		return server. pesquisarAlbumPorArtistaRMIServer(artista);
	}

	public String pesquisarAlbumPorTitulo(String album_name) throws RemoteException {
		return server.pesquisarAlbumPorTituloRMIServer(album_name);
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

	public void setAlbum_name(String album_name) {
		this.album_name = album_name;
	}
}
