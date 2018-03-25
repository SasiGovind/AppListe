package upec.projetandroid2017_2018;

/**
 * Created by Sasig on 25/03/2018.
 */

public class HistoData {

    private String time;
    private String list;
    private String element;
    private String username;
    private String action;
    private String descrition;

    public HistoData(String time, String list, String element, String username, String action, String descrition) {
        this.time = time;
        this.list = list;
        this.element = element;
        this.username = username;
        this.action = action;
        this.descrition = descrition;
    }

    public String getTime() {
        return time;
    }

    public String getList() {
        return list;
    }

    public String getElement() {
        return element;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getDescrition() {
        return descrition;
    }
}
