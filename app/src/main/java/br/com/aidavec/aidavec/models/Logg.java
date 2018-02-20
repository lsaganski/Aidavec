package br.com.aidavec.aidavec.models;

import br.com.aidavec.aidavec.core.Globals;

/**
 * Created by leonardo.saganski on 30/12/16.
 */

public class Logg {
    public int log_id;
    public int usr_id;
    public String log_data;
    public String log_tipo;
    public String log_info;

    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public String getLog_data() {
        return log_data;
    }

    public void setLog_data(String log_data) {
        this.log_data = log_data;
    }

    public String getLog_tipo() {
        return log_tipo;
    }

    public void setLog_tipo(String log_tipo) {
        this.log_tipo = log_tipo;
    }

    public String getLog_info() {
        return log_info;
    }

    public void setLog_info(String log_info) {
        this.log_info = log_info;
    }

    public Logg(String tipo) {
        setUsr_id(Globals.getInstance().loggedUser != null ? Globals.getInstance().loggedUser.getUsr_id() : 0);
        setLog_tipo(tipo);
    }
}
