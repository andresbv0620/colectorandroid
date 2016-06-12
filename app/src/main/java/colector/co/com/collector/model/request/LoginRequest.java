package colector.co.com.collector.model.request;

/**
 * Created by dherrera on 11/10/15.
 */
public class LoginRequest {

    private String username;
    private String password;
    private String tablet_id;

    public LoginRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTabletId() {
        return tablet_id;
    }

    public void setTabletId(String tablet_id) {
        this.tablet_id = tablet_id;
    }
}

