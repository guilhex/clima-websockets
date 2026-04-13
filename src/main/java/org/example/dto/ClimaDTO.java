package org.example.dto;

/**
 * Payload enviado ao tópico /topic/clima a cada broadcast.
 */
public class ClimaDTO {

    private String cidade;
    private double temperatura;
    private String descricao;
    private String horario;

    public ClimaDTO() {}

    public ClimaDTO(String cidade, double temperatura, String descricao, String horario) {
        this.cidade = cidade;
        this.temperatura = temperatura;
        this.descricao = descricao;
        this.horario = horario;
    }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}

