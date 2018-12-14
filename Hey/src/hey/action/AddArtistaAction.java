/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import hey.model.DropMusicBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class AddArtistaAction extends ActionSupport implements SessionAware { //session do utilizador que esta realmente a interagir (guardamos a sessao par podermos partilhar entre acçoes)l
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String artist_name = null;
    private String description = null;
    private String birth_date = null;
    private String groups = null;

    @Override
    public String execute() {
        if(this.artist_name != null && !artist_name.equals("") &&
                this.description != null && !description.equals("") &&
                    this.birth_date != null && !birth_date.equals("") &&
                        this.groups != null && !groups.equals("")) {
            if (!session.get("what").equals("contributor")) {
                this.getDropMusicBean().setArtist_name(artist_name);
                this.getDropMusicBean().setBirth_date(birth_date);
                this.getDropMusicBean().setDescription(description);
                this.getDropMusicBean().setGroups(groups);
                try {
                    if(this.getDropMusicBean().addArtista().equals("accepted")){
                        //foi adicionado com sucesso
                        return SUCCESS;
                    }
                    else {
                        return LOGIN;
                    }
                } catch (RemoteException e) {
                    return LOGIN;
                }
            } else
                return LOGIN;
        }else
            return LOGIN;
    }


    public DropMusicBean getDropMusicBean() {
        if (!session.containsKey("dropMusicBean"))
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

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
}

