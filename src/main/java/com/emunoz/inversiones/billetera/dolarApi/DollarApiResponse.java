package com.emunoz.inversiones.billetera.dolarApi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DollarApiResponse {

    @JsonProperty("serie")
    private List<Serie> serie;

    public List<Serie> getSerie() {
        return serie;
    }

    public void setSerie(List<Serie> serie) {
        this.serie = serie;
    }

    public static class Serie {
        @JsonProperty("valor")
        private Float valor;

        public Float getValor() {
            return valor;
        }

        public void setValor(Float valor) {
            this.valor = valor;
        }
    }
}
