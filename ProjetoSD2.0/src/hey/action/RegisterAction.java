package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import hey.model.DropMusicBean;
import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.Map;


public class RegisterAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    public String execute() {
        // any username is accepted without confirmation (should check using RMI)
        if(this.username != null && !username.equals("") &&
                this.password != null && !username.equals("")) {

            this.getDropMusicBean().setUsername(this.username);
            this.getDropMusicBean().setPassword(this.password);

            try {
                if(this.getDropMusicBean().registarUser()){
                    session.put("username", username);
                    session.put("loggedin", true); // this marks the user as logged in
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

    public DropMusicBean getDropMusicBean() {
        if(!session.containsKey("dropMusicBean"))
            this.setDropMusicBean(new DropMusicBean());
        return (DropMusicBean) session.get("dropMusicBean");
    }

    public void setDropMusicBean(DropMusicBean dropMusicBean) {
        this.session.put("dropMusicBean", dropMusicBean);
    }


    public void setSession(Map<String, Object> session) {
        this.session = session;
    }


}
