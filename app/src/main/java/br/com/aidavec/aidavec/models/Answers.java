package br.com.aidavec.aidavec.models;

/**
 * Created by leonardo.saganski on 30/12/16.
 */

public class Answers {
    public int not_id;
    public int usr_id;
    public String res_resposta;

    public int getNot_id() {
        return not_id;
    }

    public void setNot_id(int not_id) {
        this.not_id = not_id;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public String getRes_resposta() {
        return res_resposta;
    }

    public void setRes_resposta(String res_resposta) {
        this.res_resposta = res_resposta;
    }

}
