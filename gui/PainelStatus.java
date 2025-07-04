// PainelStatus.java
package gui;

import servico.CentralControle;
import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import observer.IObserver;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PainelStatus extends JPanel implements IObserver {
    private CentralControle central;
    private JLabel lblTotalLixeiras;
    private JLabel lblLixeirasVazias;
    private JLabel lblLixeirasParciais;
    private JLabel lblLixeirasLotadas;
    private JLabel lblCaminhoesAtivos;
    private JLabel lblCaminhoesParados;
    private JLabel lblEstrategiaAtual;
    private JLabel lblColetasRealizadas;
    private JLabel lblEficienciaMedia;
    private JTextArea txtLogEventos;
    private JScrollPane scrollLog;
    
    public PainelStatus(CentralControle central) {
        this.central = central;
        inicializarComponentes();
        configurarLayout();
        atualizarDados();
    }
    
    private void inicializarComponentes() {
        setBorder(BorderFactory.createTitledBorder("📊 Status do Sistema"));
        
        lblTotalLixeiras = new JLabel("0");
        lblLixeirasVazias = new JLabel("0");
        lblLixeirasParciais = new JLabel("0");
        lblLixeirasLotadas = new JLabel("0");
        lblCaminhoesAtivos = new JLabel("0");
        lblCaminhoesParados = new JLabel("0");
        lblEstrategiaAtual = new JLabel("Rota Rápida");
        lblColetasRealizadas = new JLabel("0");
        lblEficienciaMedia = new JLabel("0%");
        
        txtLogEventos = new JTextArea(8, 20);
        txtLogEventos.setEditable(false);
        txtLogEventos.setFont(new Font("Monospaced", Font.PLAIN, 10));
        scrollLog = new JScrollPane(txtLogEventos);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        adicionarLog("Sistema iniciado");
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal com dados
        JPanel painelDados = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Seção Lixeiras
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        painelDados.add(criarTitulo("🗑️ Lixeiras"), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        painelDados.add(new JLabel("Total:"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblTotalLixeiras, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        painelDados.add(new JLabel("🟢 Vazias (0-30%):"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblLixeirasVazias, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        painelDados.add(new JLabel("🟡 Parciais (30-70%):"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblLixeirasParciais, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        painelDados.add(new JLabel("🔴 Lotadas (70-100%):"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblLixeirasLotadas, gbc);
        
        // Seção Caminhões
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        painelDados.add(criarTitulo("🚛 Caminhões"), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 6;
        painelDados.add(new JLabel("🟢 Ativos:"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblCaminhoesAtivos, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        painelDados.add(new JLabel("⚪ Parados:"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblCaminhoesParados, gbc);
        
        // Seção Estratégia
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        painelDados.add(criarTitulo("⚙️ Estratégia"), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 9;
        painelDados.add(new JLabel("Atual:"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblEstrategiaAtual, gbc);
        
        // Seção Estatísticas
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        painelDados.add(criarTitulo("📈 Estatísticas"), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 11;
        painelDados.add(new JLabel("Coletas realizadas:"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblColetasRealizadas, gbc);
        
        gbc.gridx = 0; gbc.gridy = 12;
        painelDados.add(new JLabel("Eficiência média:"), gbc);
        gbc.gridx = 1;
        painelDados.add(lblEficienciaMedia, gbc);
        
        add(painelDados, BorderLayout.NORTH);
        
        // Log de eventos
        JPanel painelLog = new JPanel(new BorderLayout());
        painelLog.setBorder(BorderFactory.createTitledBorder("📋 Log de Eventos"));
        painelLog.add(scrollLog, BorderLayout.CENTER);
        
        add(painelLog, BorderLayout.CENTER);
    }
    
    private JLabel criarTitulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(new Font("Arial", Font.BOLD, 12));
        titulo.setForeground(new Color(0, 100, 0));
        return titulo;
    }
    
    public void atualizarDados() {
        // Contadores de lixeiras
        List<Lixeira> lixeiras = central.getLixeiras();
        int totalLixeiras = lixeiras.size();
        int vazias = 0, parciais = 0, lotadas = 0;
        
        for (Lixeira lixeira : lixeiras) {
            int nivel = lixeira.getNivelAtual();
            if (nivel <= 30) vazias++;
            else if (nivel <= 70) parciais++;
            else lotadas++;
        }
        
        lblTotalLixeiras.setText(String.valueOf(totalLixeiras));
        lblLixeirasVazias.setText(String.valueOf(vazias));
        lblLixeirasParciais.setText(String.valueOf(parciais));
        lblLixeirasLotadas.setText(String.valueOf(lotadas));
        
        // Contadores de caminhões
        List<Caminhao> caminhoes = central.getCaminhoes();
        int ativos = 0, parados = 0;
        
        for (Caminhao caminhao : caminhoes) {
            if (caminhao.getEstado().getDescricao().equals("Parado")) {
                parados++;
            } else {
                ativos++;
            }
        }
        
        lblCaminhoesAtivos.setText(String.valueOf(ativos));
        lblCaminhoesParados.setText(String.valueOf(parados));
        
        // Estratégia atual
        lblEstrategiaAtual.setText(central.getEstrategiaAtual().getNome());
        
        // Estatísticas
        lblColetasRealizadas.setText(String.valueOf(central.getColetasRealizadas()));
        lblEficienciaMedia.setText(String.format("%.1f%%", central.getEficienciaMedia()));
    }
    
    public void adicionarLog(String evento) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
            txtLogEventos.append(String.format("[%s] %s\n", timestamp, evento));
            txtLogEventos.setCaretPosition(txtLogEventos.getDocument().getLength());
        });
    }
    
    @Override
    public void update(String evento, Object dados) {
        SwingUtilities.invokeLater(() -> {
            atualizarDados();
            
            // Adicionar ao log baseado no evento
            switch (evento) {
                case "LIXEIRA_ADICIONADA":
                    adicionarLog("Nova lixeira adicionada");
                    break;
                case "COLETA_INICIADA":
                    adicionarLog("Coleta iniciada: " + dados);
                    break;
                case "COLETA_FINALIZADA":
                    adicionarLog("Coleta finalizada: " + dados);
                    break;
                case "ESTRATEGIA_ALTERADA":
                    adicionarLog("Estratégia alterada para: " + dados);
                    break;
                case "EMERGENCIA_SIMULADA":
                    adicionarLog("🚨 EMERGÊNCIA SIMULADA!");
                    break;
                case "SISTEMA_ATIVADO":
                    adicionarLog("Sistema ativado");
                    break;
                case "SISTEMA_DESATIVADO":
                    adicionarLog("Sistema desativado");
                    break;
            }
        });
    }
}