/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import hey.model.DropMusicBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class GoToAlbumCriticAction extends ActionSupport implements SessionAware { //session do utilizador que esta realmente a interagir (guardamos a sessao par podermos partilhar entre acçoes
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;

	@Override
	public String execute() {
		return SUCCESS;
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
