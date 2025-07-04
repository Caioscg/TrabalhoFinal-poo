// SimuladorMovimento.java
package servico;

import modelo.caminhao.Caminhao;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimuladorMovimento {
    private List<Caminhao> caminhoes;
    private Timer timerMovimento;
    private boolean ativo;
    
    public SimuladorMovimento(List<Caminhao> caminhoes) {
        this.caminhoes = caminhoes;
        this.ativo = false;
        
        // Timer para movimento suave dos caminhões
        timerMovimento = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ativo) {
                    atualizarMovimentos();
                }
            }
        });
    }
    
    public void iniciar() {
        ativo = true;
        timerMovimento.start();
    }
    
    public void parar() {
        ativo = false;
        timerMovimento.stop();
    }
    
    private void atualizarMovimentos() {
        // O movimento já é gerenciado pelo CentralControle
        // Este timer serve apenas para garantir fluidez visual
    }
    
    public boolean isAtivo() {
        return ativo;
    }
}