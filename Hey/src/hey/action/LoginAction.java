/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import hey.model.DropMusicBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware { //session do utilizador que esta realmente a interagir (guardamos a sessao par podermos partilhar entre acçoes
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() {
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !username.equals("") &&
			this.password != null && !username.equals("")) {

				this.getHeyBean().setUsername(this.username);
				this.getHeyBean().setPassword(this.password);

			try {
				String temp = this.getHeyBean().loginUser();
				if(!temp.contains("wrong")){
					session.put("username", username);
					session.put("loggedin", true); // this marks the user as logged in
					session.put("what", temp);
					return SUCCESS;
				}
				else {
					return LOGIN;
				}
			} catch (RemoteException e) {
				return LOGIN;
			}
		}
		else
			return LOGIN;
	}
	
	public void setUsername(String username) {
		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public DropMusicBean getHeyBean() {
		if(!session.containsKey("dropMusicBean"))
			this.setHeyBean(new DropMusicBean());
		return (DropMusicBean) session.get("dropMusicBean");
	}

	public void setHeyBean(DropMusicBean dropMusicBean) {
		this.session.put("dropMusicBean", dropMusicBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
