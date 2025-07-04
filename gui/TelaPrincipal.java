// TelaPrincipal.java (Versão Corrigida)
package gui;

import servico.CentralControle;
import servico.SimuladorMovimento;
import modelo.coleta.*;
import observer.IObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame implements IObserver {
    private CentralControle central;
    private SimuladorMovimento simulador;
    private PainelMapa painelMapa;
    private PainelStatus painelStatus;
    private JButton btnIniciar;
    private JButton btnParar;
    private JButton btnRotaRapida;
    private JButton btnRotaEconomica;
    private JButton btnRotaCompleta;
    private JButton btnEmergencia;
    private JButton btnAdicionarLixeira;
    private JLabel lblStatus;
    
    public TelaPrincipal() {
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void inicializarComponentes() {
        setTitle("🗑️ Sistema de Coleta Inteligente - Cidade Inteligente");
        
        // Inicializar sistema
        central = new CentralControle();
        simulador = new SimuladorMovimento(central.getCaminhoes());
        central.addObserver(this);
        
        // Componentes da GUI
        painelMapa = new PainelMapa(central);
        painelStatus = new PainelStatus(central);
        
        btnIniciar = new JButton("▶️ Ativar Sistema");
        btnParar = new JButton("⏸️ Desativar Sistema");
        btnParar.setEnabled(false);
        
        btnRotaRapida = new JButton("🔴 Rota Rápida");
        btnRotaEconomica = new JButton("🔵 Rota Econômica");
        btnRotaCompleta = new JButton("🟡 Rota Completa");
        
        btnEmergencia = new JButton("🚨 Emergência");
        btnAdicionarLixeira = new JButton("➕ Add Lixeira");
        
        lblStatus = new JLabel("Status: Sistema Desativado");
        lblStatus.setForeground(Color.RED);
        
        // Registrar observadores
        central.addObserver(painelMapa);
        central.addObserver(painelStatus);
        
        // Estratégia inicial
        btnRotaRapida.setBackground(Color.LIGHT_GRAY);
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(painelMapa);
        splitPane.setRightComponent(painelStatus);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.75);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Painel de controles
        JPanel painelControles = new JPanel(new BorderLayout());
        
        // Linha superior: controles principais
        JPanel painelPrincipal = new JPanel(new FlowLayout());
        painelPrincipal.add(btnIniciar);
        painelPrincipal.add(btnParar);
        painelPrincipal.add(new JLabel(" | "));
        painelPrincipal.add(new JLabel("Estratégia:"));
        painelPrincipal.add(btnRotaRapida);
        painelPrincipal.add(btnRotaEconomica);
        painelPrincipal.add(btnRotaCompleta);
        painelPrincipal.add(new JLabel(" | "));
        painelPrincipal.add(btnEmergencia);
        painelPrincipal.add(btnAdicionarLixeira);
        painelPrincipal.add(new JLabel(" | "));
        painelPrincipal.add(lblStatus);
        
        painelControles.add(painelPrincipal, BorderLayout.NORTH);
        
        add(painelControles, BorderLayout.SOUTH);
    }
    
    private void configurarEventos() {
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                central.ativarSistema();
                central.iniciarSimulacaoLixo();
                simulador.iniciar();
                
                btnIniciar.setEnabled(false);
                btnParar.setEnabled(true);
                lblStatus.setText("Status: Sistema Ativo");
                lblStatus.setForeground(Color.GREEN);
            }
        });
        
        btnParar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                central.desativarSistema();
                simulador.parar();
                
                btnIniciar.setEnabled(true);
                btnParar.setEnabled(false);
                lblStatus.setText("Status: Sistema Desativado");
                lblStatus.setForeground(Color.RED);
            }
        });
        
        // Estratégias
        btnRotaRapida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                central.alterarEstrategia(new RotaRapida());
                atualizarBotoesEstrategia(btnRotaRapida);
            }
        });
        
        btnRotaEconomica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                central.alterarEstrategia(new RotaEconomica());
                atualizarBotoesEstrategia(btnRotaEconomica);
            }
        });
        
        btnRotaCompleta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                central.alterarEstrategia(new RotaCompleta());
                atualizarBotoesEstrategia(btnRotaCompleta);
            }
        });
        
        // Ações especiais
        btnEmergencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                central.simularEmergencia();
                JOptionPane.showMessageDialog(TelaPrincipal.this, 
                    "🚨 Emergência simulada!\nTodas as lixeiras foram enchidas!", 
                    "Emergência", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnAdicionarLixeira.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Adiciona lixeira em posição aleatória
                int x = 50 + (int)(Math.random() * 500);
                int y = 50 + (int)(Math.random() * 400);
                central.adicionarLixeira(x, y);
            }
        });
        
        // Timer para atualização da interface
        Timer timerAtualizacao = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painelMapa.repaint();
                painelStatus.atualizarDados();
            }
        });
        timerAtualizacao.start();
    }
    
    private void atualizarBotoesEstrategia(JButton botaoSelecionado) {
        // Reset todos
        btnRotaRapida.setBackground(null);
        btnRotaEconomica.setBackground(null);
        btnRotaCompleta.setBackground(null);
        
        // Destacar selecionado
        botaoSelecionado.setBackground(Color.LIGHT_GRAY);
    }
    
    @Override
    public void update(String evento, Object dados) {
        SwingUtilities.invokeLater(() -> {
            switch (evento) {
                case "ESTRATEGIA_ALTERADA":
                    lblStatus.setText("Status: Estratégia alterada");
                    break;
                case "PROCESSO_INICIADO":
                    lblStatus.setText("Status: Coleta em andamento");
                    break;
                case "EMERGENCIA_SIMULADA":
                    lblStatus.setText("Status: EMERGÊNCIA!");
                    lblStatus.setForeground(Color.RED);
                    break;
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // CORREÇÃO: Usar getSystemLookAndFeel() corretamente
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Usar look and feel padrão se houver erro
                System.out.println("Usando Look and Feel padrão");
            }
            new TelaPrincipal().setVisible(true);
        });
    }
}