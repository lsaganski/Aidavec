package br.com.aidavec.aidavec.models;

/**
 * Created by leonardo.saganski on 30/12/16.
 */

public class ReportData {
    public double totalPontos;
    public double totalPontosCampanha;
    public double kmDia;
    public double kmSemana;
    public double kmMes;

    public double getTotalPontos() {
        return totalPontos;
    }

    public void setTotalPontos(double totalPontos) {
        this.totalPontos = totalPontos;
    }

    public double getTotalPontosCampanha() {
        return totalPontosCampanha;
    }

    public void setTotalPontosCampanha(double totalPontosCampanha) {
        this.totalPontosCampanha = totalPontosCampanha;
    }

    public double getKmDia() {
        return kmDia;
    }

    public void setKmDia(double kmDia) {
        this.kmDia = kmDia;
    }

    public double getKmSemana() {
        return kmSemana;
    }

    public void setKmSemana(double kmSemana) {
        this.kmSemana = kmSemana;
    }

    public double getKmMes() {
        return kmMes;
    }

    public void setKmMes(double kmMes) {
        this.kmMes = kmMes;
    }
}
