// TipoLixeira.java
package modelo.lixeira;

public enum TipoLixeira {
    COMUM("🗑️", "Lixo Comum"),
    RECICLAVEL("♻️", "Reciclável"),
    ORGANICO("🍃", "Orgânico"),
    VIDRO("🥃", "Vidro"),
    PAPEL("📄", "Papel");
    
    private final String emoji;
    private final String descricao;
    
    TipoLixeira(String emoji, String descricao) {
        this.emoji = emoji;
        this.descricao = descricao;
    }
    
    public String getEmoji() { return emoji; }
    public String getDescricao() { return descricao; }
}