// EstadoCaminhao.java
package modelo.caminhao;

public enum EstadoCaminhao {
    PARADO("Parado"),
    INDO_PARA_COLETA("Indo para Coleta"),
    COLETANDO("Coletando"),
    RETORNANDO("Retornando");
    
    private final String descricao;
    
    EstadoCaminhao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() { return descricao; }
}