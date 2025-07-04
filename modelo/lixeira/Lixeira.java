// Lixeira.java (Versão Corrigida)
package modelo.lixeira;

import java.awt.Color;
import java.util.Random;

public class Lixeira {
    private String id;
    private int posicaoX;
    private int posicaoY;
    private int nivelAtual;
    private TipoLixeira tipo;
    private boolean coletando;
    private static int contador = 1;
    
    public Lixeira(int x, int y) {
        this.id = "L" + String.format("%03d", contador++);
        this.posicaoX = x;
        this.posicaoY = y;
        // OTIMIZAÇÃO: Começar com níveis mais variados para demonstração
        this.nivelAtual = 30 + new Random().nextInt(60); // 30-90%
        this.tipo = TipoLixeira.values()[new Random().nextInt(TipoLixeira.values().length)];
        this.coletando = false;
    }
    
    public void simularEnchimento() {
        if (nivelAtual < 95) {
            // OTIMIZAÇÃO: Enchimento mais rápido para demonstração
            nivelAtual += new Random().nextInt(8) + 3; // 3-10% por vez
            if (nivelAtual > 100) nivelAtual = 100;
        }
    }
    
    public void esvaziar() {
        // CORREÇÃO: Garantir que a lixeira seja esvaziada corretamente
        System.out.println("Esvaziando lixeira " + id + " (era " + nivelAtual + "%)");
        this.nivelAtual = 0;
        this.coletando = false;
        System.out.println("Lixeira " + id + " agora está com " + nivelAtual + "%");
    }
    
    public boolean precisaColeta() {
        return nivelAtual >= 70;
    }
    
    public Color getCorVisual() {
        if (nivelAtual <= 30) return Color.GREEN;
        else if (nivelAtual <= 70) return Color.YELLOW;
        else return Color.RED;
    }
    
    public String getEmoji() {
        return tipo.getEmoji();
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public int getPosicaoX() { return posicaoX; }
    public int getPosicaoY() { return posicaoY; }
    public int getNivelAtual() { return nivelAtual; }
    public TipoLixeira getTipo() { return tipo; }
    public boolean isColetando() { return coletando; }
    public void setColetando(boolean coletando) { 
        this.coletando = coletando; 
        if (coletando) {
            System.out.println("Lixeira " + id + " marcada como sendo coletada");
        }
    }
    public void setNivelAtual(int nivel) { 
        this.nivelAtual = Math.max(0, Math.min(100, nivel)); // Garantir entre 0-100
    }
}