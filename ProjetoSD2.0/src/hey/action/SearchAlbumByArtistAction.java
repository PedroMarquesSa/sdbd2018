/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import hey.model.DropMusicBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class SearchAlbumByArtistAction extends ActionSupport implements SessionAware { //session do utilizador que esta realmente a interagir (guardamos a sessao par podermos partilhar entre acçoes
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String artist_name = null, resposta=null;

	@Override
	public String execute() {
		System.out.println("A procurar album de artista "+artist_name);
		if (this.artist_name != null && !artist_name.equals("")) {
			this.getDropMusicBean().setArtist_name(artist_name);
			try {
				resposta = this.getDropMusicBean().pesquisarAlbumPorArtista(artist_name);
				System.out.println(resposta);

			} catch (RemoteException e) {
				return LOGIN;
			}

			return SUCCESS;
		}
		return LOGIN;
	}
	
	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}

	public String getArtist_name() {
		return this.artist_name;
	}

	public String getResposta() {
		return this.resposta;
	}

	
	public DropMusicBean getDropMusicBean() {
		if(!session.containsKey("dropMusicBean"))
			this.setDropMusicBean(new DropMusicBean());
		return (DropMusicBean) session.get("dropMusicBean");
	}

	public void setDropMusicBean(DropMusicBean dropMusicBean) {
		this.session.put("dropMusicBean", dropMusicBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
