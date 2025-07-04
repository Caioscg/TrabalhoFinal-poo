// PainelMapa.java
package gui;

import servico.CentralControle;
import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import observer.IObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PainelMapa extends JPanel implements IObserver {
    private CentralControle central;
    
    public PainelMapa(CentralControle central) {
        this.central = central;
        setPreferredSize(new Dimension(600, 500));
        setBackground(new Color(240, 248, 255));
        setBorder(BorderFactory.createTitledBorder("🗺️ Mapa da Cidade - " + central.getEstrategiaAtual().getNome()));
        
        // Clique para adicionar lixeira
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    central.adicionarLixeira(e.getX(), e.getY());
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        desenharGaragem(g2d);
        desenharLixeiras(g2d);
        desenharCaminhoes(g2d);
        desenharRotas(g2d);
        desenharLegenda(g2d);
        
        setBorder(BorderFactory.createTitledBorder("🗺️ Mapa da Cidade - " + central.getEstrategiaAtual().getNome()));
    }
    
    private void desenharGaragem(Graphics2D g2d) {
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(290, 240, 40, 30);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(290, 240, 40, 30);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("🏢 GARAGEM", 280, 235);
    }
    
    private void desenharLixeiras(Graphics2D g2d) {
        for (Lixeira lixeira : central.getLixeiras()) {
            int x = lixeira.getPosicaoX();
            int y = lixeira.getPosicaoY();
            
            g2d.setColor(lixeira.getCorVisual());
            
            if (lixeira.isColetando()) {
                if (System.currentTimeMillis() % 1000 < 500) {
                    g2d.fillOval(x - 12, y - 12, 24, 24);
                }
            } else {
                g2d.fillOval(x - 12, y - 12, 24, 24);
            }
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - 12, y - 12, 24, 24);
            g2d.setStroke(new BasicStroke(1));
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(lixeira.getEmoji(), x - 6, y + 4);
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 8));
            g2d.drawString(lixeira.getId(), x - 15, y - 15);
            g2d.drawString(lixeira.getNivelAtual() + "%", x - 8, y + 20);
            
            if (lixeira.precisaColeta() && !lixeira.isColetando()) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("⚠️", x + 15, y - 10);
            }
        }
    }
    
    private void desenharCaminhoes(Graphics2D g2d) {
        for (Caminhao caminhao : central.getCaminhoes()) {
            int x = caminhao.getPosicaoX();
            int y = caminhao.getPosicaoY();
            
            String estado = caminhao.getEstado().getDescricao();
            switch (estado) {
                case "Parado":
                    g2d.setColor(Color.GRAY);
                    break;
                case "Coletando":
                    g2d.setColor(Color.GREEN);
                    break;
                case "Retornando":
                    g2d.setColor(Color.ORANGE);
                    break;
                default:
                    g2d.setColor(Color.BLUE);
            }
            
            g2d.fillRect(x - 15, y - 10, 30, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x - 15, y - 10, 30, 20);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.drawString("🚛", x - 8, y + 5);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 8));
            g2d.drawString(caminhao.getId(), x - 12, y - 12);
            g2d.drawString(estado, x - 20, y + 25);
        }
    }
    
    private void desenharRotas(Graphics2D g2d) {
        for (Caminhao caminhao : central.getCaminhoes()) {
            if (!caminhao.getRotaAtual().isEmpty()) {
                String corEstrategia = central.getEstrategiaAtual().getCor();
                switch (corEstrategia) {
                    case "VERMELHO":
                        g2d.setColor(new Color(255, 0, 0, 100));
                        break;
                    case "AZUL":
                        g2d.setColor(new Color(0, 0, 255, 100));
                        break;
                    case "AMARELO":
                        g2d.setColor(new Color(255, 255, 0, 150));
                        break;
                }
                
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
                
                int x = caminhao.getPosicaoX();
                int y = caminhao.getPosicaoY();
                
                for (Lixeira lixeira : caminhao.getRotaAtual()) {
                    g2d.drawLine(x, y, lixeira.getPosicaoX(), lixeira.getPosicaoY());
                    x = lixeira.getPosicaoX();
                    y = lixeira.getPosicaoY();
                }
                
                g2d.setStroke(new BasicStroke(1));
            }
        }
    }
    
    private void desenharLegenda(Graphics2D g2d) {
        int x = 10;
        int y = getHeight() - 150;
        
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(x, y, 180, 140, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, 180, 140, 8, 8);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("📋 Legenda:", x + 5, y + 15);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        // Lixeiras
        g2d.setColor(Color.GREEN);
        g2d.fillOval(x + 5, y + 25, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Lixeira Vazia (0-30%)", x + 20, y + 33);
        
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(x + 5, y + 40, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Lixeira Parcial (30-70%)", x + 20, y + 48);
        
        g2d.setColor(Color.RED);
        g2d.fillOval(x + 5, y + 55, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Lixeira Cheia (70-100%)", x + 20, y + 63);
        
        // Caminhões
        g2d.setColor(Color.GRAY);
        g2d.fillRect(x + 5, y + 70, 12, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Caminhão Parado", x + 20, y + 78);
        
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x + 5, y + 85, 12, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Caminhão Coletando", x + 20, y + 93);
        
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(x + 5, y + 100, 12, 8);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Caminhão Retornando", x + 20, y + 108);
        
        // Dicas
        g2d.setFont(new Font("Arial", Font.ITALIC, 8));
        g2d.drawString("💡 Clique direito para adicionar lixeira", x + 5, y + 125);
        g2d.drawString("⚠️ Símbolo indica coleta necessária", x + 5, y + 135);
    }
    
    @Override
    public void update(String evento, Object dados) {
        SwingUtilities.invokeLater(() -> {
            repaint();
        });
    }
}