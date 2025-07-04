// Caminhao.java (Versão Otimizada)
package modelo.caminhao;

import modelo.lixeira.Lixeira;
import java.util.ArrayList;
import java.util.List;

public class Caminhao {
    private String id;
    private int posicaoX;
    private int posicaoY;
    private int capacidadeMaxima;
    private int cargaAtual;
    private EstadoCaminhao estado;
    private List<Lixeira> rotaAtual;
    private Lixeira lixeiraAtual;
    private static int contador = 1;
    
    public Caminhao() {
        this.id = "C" + String.format("%02d", contador++);
        this.posicaoX = 310; // Posição da garagem
        this.posicaoY = 255;
        this.capacidadeMaxima = 100;
        this.cargaAtual = 0;
        this.estado = EstadoCaminhao.PARADO;
        this.rotaAtual = new ArrayList<>();
    }
    
    public void definirRota(List<Lixeira> rota) {
        this.rotaAtual = new ArrayList<>(rota);
    }
    
    public void moverPara(int x, int y) {
        // OTIMIZAÇÃO: Movimento mais rápido - velocidade aumentada de 2 para 5
        int velocidade = 5;
        int dx = x - this.posicaoX;
        int dy = y - this.posicaoY;
        
        if (Math.abs(dx) > velocidade) {
            this.posicaoX += dx > 0 ? velocidade : -velocidade;
        } else {
            this.posicaoX = x;
        }
        
        if (Math.abs(dy) > velocidade) {
            this.posicaoY += dy > 0 ? velocidade : -velocidade;
        } else {
            this.posicaoY = y;
        }
    }
    
    public boolean chegouNoDestino(int x, int y) {
        // OTIMIZAÇÃO: Área de chegada maior para não ficar "grudado" nas coordenadas exatas
        return Math.abs(posicaoX - x) <= 8 && Math.abs(posicaoY - y) <= 8;
    }
    
    public void coletarLixo(Lixeira lixeira) {
        // CORREÇÃO: Verificar se a lixeira realmente tem lixo para coletar
        int quantidade = lixeira.getNivelAtual();
        
        if (quantidade > 0 && cargaAtual + quantidade <= capacidadeMaxima) {
            cargaAtual += quantidade;
            // Debug: Log da coleta
            System.out.println("Caminhão " + id + " coletou " + quantidade + 
                             "% da lixeira " + lixeira.getId());
            lixeira.esvaziar(); // Isso deve zerar o nível
        } else if (quantidade <= 0) {
            System.out.println("Lixeira " + lixeira.getId() + " já estava vazia!");
        } else {
            System.out.println("Caminhão " + id + " está cheio demais para coletar!");
        }
    }
    
    public void descarregar() {
        this.cargaAtual = 0;
        System.out.println("Caminhão " + id + " descarregou todo o lixo na garagem");
    }
    
    public boolean estaCheio() {
        return cargaAtual >= capacidadeMaxima * 0.8;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public int getPosicaoX() { return posicaoX; }
    public int getPosicaoY() { return posicaoY; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public int getCargaAtual() { return cargaAtual; }
    public EstadoCaminhao getEstado() { return estado; }
    public void setEstado(EstadoCaminhao estado) { this.estado = estado; }
    public List<Lixeira> getRotaAtual() { return rotaAtual; }
    public Lixeira getLixeiraAtual() { return lixeiraAtual; }
    public void setLixeiraAtual(Lixeira lixeira) { this.lixeiraAtual = lixeira; }
}