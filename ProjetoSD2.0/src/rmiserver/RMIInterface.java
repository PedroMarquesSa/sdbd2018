package rmiserver;

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

    public boolean loginRMIServer(String username, String password) throws RemoteException;

    public boolean registarRMIServer(String username, String password) throws RemoteException;

    public String privilegiosEditorRMIServer(String username, String password, String usernameDest) throws RemoteException;

    public String introduzirArtistaRMIServer(String username, String artist_name, String description, String birth_date, String[] grupos) throws RemoteException;
    public String introduzirAlbumRMIServer(String username, String artist_name, String album_name, String description, String genero, String releaseDate, String recordlabel) throws RemoteException;
    public String introduzirMusicaRMIServer(String username, String artist_name, String album_name, String music_name) throws RemoteException;

    public String pesquisarAlbumPorArtistaRMIServer(String artist_name) throws RemoteException;
    public String pesquisarAlbumPorTituloRMIServer(String album_name) throws RemoteException;

    public String pesquisarArtistaRMIServer(String album_name) throws RemoteException;

    public String consultarDetalhesMusicaRMIServer(String artist_name, String album_name, String music_name) throws RemoteException;
    public String escreverCritiaAlbumRMIServer(String username, String artist_name, String album_name, String critic, int rating) throws RemoteException;

    public String enviarParaMulticast(String protocol) throws RemoteException;

    /*
	public String consultarDetalhesArtistaRMIServer(String username, String artist_name) throws RemoteException;
	public String consultarDetalhesAlbumRMIServer(String username, String album_name) throws RemoteException;

	public criarConcerto(String username, String[] artistas, ...) throws RemoteException;
	*/

    public Boolean ping() throws RemoteException;

}
