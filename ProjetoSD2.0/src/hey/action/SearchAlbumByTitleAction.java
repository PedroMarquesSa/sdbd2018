/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import hey.model.DropMusicBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class SearchAlbumByTitleAction extends ActionSupport implements SessionAware { //session do utilizador que esta realmente a interagir (guardamos a sessao par podermos partilhar entre acçoes
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String album_name = null, resposta=null;

	@Override
	public String execute() {
		System.out.println("Titulo:"+album_name);
		if (this.album_name != null && !album_name.equals("")) {
			this.getDropMusicBean().setAlbum_name(album_name);
			try {
				System.out.println("Passou:");
				resposta = this.getDropMusicBean().pesquisarAlbumPorTitulo(album_name);
				System.out.println("Hey");
				System.out.println(resposta);

			} catch (RemoteException e) {
				return LOGIN;
			}

			return SUCCESS;
		}
		return LOGIN;
	}
	
	public void setAlbum_name(String album_name) {
		this.album_name = album_name;
	}

	public String getAlbum_name() {
		return this.album_name;
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
