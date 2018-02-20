package br.com.aidavec.aidavec.models;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class User {
    public int usr_id;
    public String usr_nome;
    public String usr_sobrenome;
    public String usr_email;
    public String usr_telefone;
    public String usr_uf;
    public String usr_cidade;
    public String usr_senha;
    public int usr_status;
    public String usr_device;
    public String usr_dt_cadastro;

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public String getUsr_nome() {
        return usr_nome;
    }

    public void setUsr_nome(String usr_nome) {
        this.usr_nome = usr_nome;
    }

    public String getUsr_sobrenome() {
        return usr_sobrenome;
    }

    public void setUsr_sobrenome(String usr_sobrenome) {
        this.usr_sobrenome = usr_sobrenome;
    }

    public String getUsr_email() {
        return usr_email;
    }

    public void setUsr_email(String usr_email) {
        this.usr_email = usr_email;
    }

    public String getUsr_telefone() {
        return usr_telefone;
    }

    public void setUsr_telefone(String usr_telefone) {
        this.usr_telefone = usr_telefone;
    }

    public String getUsr_uf() {
        return usr_uf;
    }

    public void setUsr_uf(String usr_uf) {
        this.usr_uf = usr_uf;
    }

    public String getUsr_cidade() {
        return usr_cidade;
    }

    public void setUsr_cidade(String usr_cidade) {
        this.usr_cidade = usr_cidade;
    }

    public String getUsr_senha() {
        return usr_senha;
    }

    public void setUsr_senha(String usr_senha) {
        this.usr_senha = usr_senha;
    }

    public int getUsr_status() {
        return usr_status;
    }

    public void setUsr_status(int usr_status) {
        this.usr_status = usr_status;
    }

    public String getUsr_device() {
        return usr_device;
    }

    public void setUsr_device(String usr_device) {
        this.usr_device = usr_device;
    }

    public String getUsr_dt_cadastro() {
        return usr_dt_cadastro;
    }

    public void setUsr_dt_cadastro(String usr_dt_cadastro) {
        this.usr_dt_cadastro = usr_dt_cadastro;
    }
}
