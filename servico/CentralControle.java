// CentralControle.java (Bug Fix Final)
package servico;

import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import modelo.coleta.*;
import observer.IObserver;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CentralControle {
    private List<Lixeira> lixeiras;
    private List<Caminhao> caminhoes;
    private IEstrategiaColeta estrategiaAtual;
    private List<IObserver> observers;
    private boolean sistemaAtivo;
    private Timer timerSimulacao;
    private int coletasRealizadas;
    private double eficienciaMedia;
    
    public CentralControle() {
        inicializarSistema();
    }
    
    private void inicializarSistema() {
        lixeiras = new ArrayList<>();
        caminhoes = new ArrayList<>();
        observers = new ArrayList<>();
        estrategiaAtual = new RotaRapida();
        sistemaAtivo = false;
        coletasRealizadas = 0;
        eficienciaMedia = 0.0;
        
        criarLixeirasIniciais();
        
        caminhoes.add(new Caminhao());
        caminhoes.add(new Caminhao());
        
        timerSimulacao = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sistemaAtivo) {
                    simularEnchimentoLixeiras();
                }
            }
        });
    }
    
    private void criarLixeirasIniciais() {
        int[][] posicoes = {
            {150, 120}, {350, 150}, {450, 180},
            {200, 320}, {400, 350}, {120, 380}
        };
        
        for (int[] pos : posicoes) {
            Lixeira lixeira = new Lixeira(pos[0], pos[1]);
            if (Math.random() < 0.5) {
                lixeira.setNivelAtual(75 + (int)(Math.random() * 20));
            }
            lixeiras.add(lixeira);
        }
        
        notificarObservers("LIXEIRAS_INICIALIZADAS", null);
    }
    
    public void ativarSistema() {
        sistemaAtivo = true;
        timerSimulacao.start();
        notificarObservers("SISTEMA_ATIVADO", null);
    }
    
    public void desativarSistema() {
        sistemaAtivo = false;
        timerSimulacao.stop();
        
        for (Caminhao caminhao : caminhoes) {
            caminhao.setEstado(modelo.caminhao.EstadoCaminhao.PARADO);
            for (Lixeira lixeira : caminhao.getRotaAtual()) {
                lixeira.setColetando(false);
            }
            caminhao.getRotaAtual().clear();
            caminhao.setLixeiraAtual(null);
        }
        
        notificarObservers("SISTEMA_DESATIVADO", null);
    }
    
    public void alterarEstrategia(IEstrategiaColeta novaEstrategia) {
        this.estrategiaAtual = novaEstrategia;
        
        for (Caminhao caminhao : caminhoes) {
            for (Lixeira lixeira : caminhao.getRotaAtual()) {
                lixeira.setColetando(false);
            }
            
            if (caminhao.getEstado() != modelo.caminhao.EstadoCaminhao.PARADO && 
                caminhao.getEstado() != modelo.caminhao.EstadoCaminhao.RETORNANDO) {
                List<Lixeira> novaRotaCaminhao = estrategiaAtual.calcularRota(lixeiras, caminhao);
                caminhao.definirRota(novaRotaCaminhao);
                
                if (!novaRotaCaminhao.isEmpty()) {
                    caminhao.setLixeiraAtual(novaRotaCaminhao.get(0));
                    novaRotaCaminhao.get(0).setColetando(true);
                    caminhao.setEstado(modelo.caminhao.EstadoCaminhao.INDO_PARA_COLETA);
                }
            }
        }
        
        notificarObservers("ESTRATEGIA_ALTERADA", novaEstrategia.getNome());
    }
    
    public void adicionarLixeira(int x, int y) {
        lixeiras.add(new Lixeira(x, y));
        notificarObservers("LIXEIRA_ADICIONADA", null);
    }
    
    public void simularEmergencia() {
        for (Lixeira lixeira : lixeiras) {
            lixeira.setNivelAtual(95 + new Random().nextInt(5));
        }
        notificarObservers("EMERGENCIA_SIMULADA", null);
    }
    
    public void iniciarSimulacaoLixo() {
        Timer timerColeta = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sistemaAtivo) {
                    processarColetas();
                }
            }
        });
        timerColeta.start();
    }
    
    private void simularEnchimentoLixeiras() {
        for (Lixeira lixeira : lixeiras) {
            if (!lixeira.isColetando() && new Random().nextDouble() < 0.4) {
                lixeira.simularEnchimento();
            }
        }
        notificarObservers("LIXEIRAS_ATUALIZADAS", null);
    }
    
    private void processarColetas() {
        for (Caminhao caminhao : caminhoes) {
            switch (caminhao.getEstado()) {
                case PARADO:
                    List<Lixeira> rotaCalculada = estrategiaAtual.calcularRota(lixeiras, caminhao);
                    
                    List<Lixeira> rotaValida = new ArrayList<>();
                    for (Lixeira lixeira : rotaCalculada) {
                        if (lixeira.precisaColeta() && !lixeira.isColetando()) {
                            rotaValida.add(lixeira);
                        }
                    }
                    
                    if (!rotaValida.isEmpty()) {
                        caminhao.definirRota(rotaValida);
                        caminhao.setEstado(modelo.caminhao.EstadoCaminhao.INDO_PARA_COLETA);
                        caminhao.setLixeiraAtual(rotaValida.get(0));
                        rotaValida.get(0).setColetando(true);
                        notificarObservers("COLETA_INICIADA", caminhao.getId());
                        System.out.println("🚛 " + caminhao.getId() + " iniciou rota com " + rotaValida.size() + " lixeiras");
                    }
                    break;
                    
                case INDO_PARA_COLETA:
                    if (caminhao.getLixeiraAtual() != null) {
                        Lixeira destino = caminhao.getLixeiraAtual();
                        
                        if (!destino.precisaColeta() || destino.getNivelAtual() == 0) {
                            System.out.println("⚠️ Lixeira " + destino.getId() + " não precisa mais de coleta, pulando...");
                            pularParaProximaLixeira(caminhao);
                            break;
                        }
                        
                        caminhao.moverPara(destino.getPosicaoX(), destino.getPosicaoY());
                        
                        if (caminhao.chegouNoDestino(destino.getPosicaoX(), destino.getPosicaoY())) {
                            caminhao.setEstado(modelo.caminhao.EstadoCaminhao.COLETANDO);
                            System.out.println("🎯 Caminhão " + caminhao.getId() + " chegou na lixeira " + 
                                             destino.getId() + " com " + destino.getNivelAtual() + "%");
                        }
                    }
                    break;
                    
                case COLETANDO:
                    if (caminhao.getLixeiraAtual() != null) {
                        Lixeira lixeiraAtual = caminhao.getLixeiraAtual();
                        
                        // CORREÇÃO CRÍTICA: Verificar se a lixeira ainda tem lixo
                        if (lixeiraAtual.getNivelAtual() == 0) {
                            System.out.println("⚠️ Lixeira " + lixeiraAtual.getId() + " já está vazia!");
                            pularParaProximaLixeira(caminhao);
                            break;
                        }
                        
                        System.out.println("🗑️ Coletando lixeira " + lixeiraAtual.getId() + 
                                         " - Nível antes: " + lixeiraAtual.getNivelAtual() + "%");
                        
                        // CORREÇÃO CRÍTICA: Verificar capacidade ANTES de coletar
                        int nivelLixeira = lixeiraAtual.getNivelAtual();
                        boolean podeColeter = caminhao.getCargaAtual() + nivelLixeira <= caminhao.getCapacidadeMaxima();
                        
                        if (!podeColeter) {
                            System.out.println("🚛💯 Caminhão " + caminhao.getId() + " não tem espaço para " + 
                                             nivelLixeira + "%, retornando à garagem");
                            // CORREÇÃO: Liberar a lixeira e ir para garagem
                            lixeiraAtual.setColetando(false);
                            limparRotaCaminhao(caminhao);
                            caminhao.setEstado(modelo.caminhao.EstadoCaminhao.RETORNANDO);
                            caminhao.setLixeiraAtual(null);
                            break;
                        }
                        
                        // Realizar a coleta (só se couber)
                        caminhao.coletarLixo(lixeiraAtual);
                        coletasRealizadas++;
                        
                        System.out.println("✅ Lixeira " + lixeiraAtual.getId() + 
                                         " coletada - Nível depois: " + lixeiraAtual.getNivelAtual() + "%");
                        
                        // CORREÇÃO: Verificar se está cheio APÓS coletar
                        if (caminhao.estaCheio()) {
                            System.out.println("🚛💯 Caminhão " + caminhao.getId() + " ficou cheio, retornando à garagem");
                            limparRotaCaminhao(caminhao);
                            caminhao.setEstado(modelo.caminhao.EstadoCaminhao.RETORNANDO);
                            caminhao.setLixeiraAtual(null);
                        } else {
                            // Ir para próxima lixeira
                            pularParaProximaLixeira(caminhao);
                        }
                        
                        notificarObservers("COLETA_REALIZADA", caminhao.getId());
                    }
                    break;
                    
                case RETORNANDO:
                    caminhao.moverPara(310, 255);
                    
                    if (caminhao.chegouNoDestino(310, 255)) {
                        caminhao.descarregar();
                        caminhao.setEstado(modelo.caminhao.EstadoCaminhao.PARADO);
                        System.out.println("🏠 Caminhão " + caminhao.getId() + " voltou à garagem e descarregou");
                        notificarObservers("COLETA_FINALIZADA", caminhao.getId());
                    }
                    break;
            }
        }
        
        calcularEficiencia();
    }
    
    // NOVO MÉTODO: Limpar rota e liberar lixeiras
    private void limparRotaCaminhao(Caminhao caminhao) {
        for (Lixeira lixeira : caminhao.getRotaAtual()) {
            lixeira.setColetando(false);
        }
        caminhao.getRotaAtual().clear();
    }
    
    private void pularParaProximaLixeira(Caminhao caminhao) {
        List<Lixeira> rotaAtualCaminhao = caminhao.getRotaAtual();
        
        if (!rotaAtualCaminhao.isEmpty()) {
            rotaAtualCaminhao.remove(0);
        }
        
        // CORREÇÃO: Verificar se caminhão está cheio ANTES de continuar
        if (caminhao.estaCheio()) {
            System.out.println("🚛💯 Caminhão " + caminhao.getId() + " está cheio, interrompendo rota");
            limparRotaCaminhao(caminhao);
            caminhao.setEstado(modelo.caminhao.EstadoCaminhao.RETORNANDO);
            caminhao.setLixeiraAtual(null);
            return;
        }
        
        if (!rotaAtualCaminhao.isEmpty()) {
            Lixeira proximaLixeira = null;
            Iterator<Lixeira> iterator = rotaAtualCaminhao.iterator();
            
            while (iterator.hasNext()) {
                Lixeira lixeira = iterator.next();
                if (lixeira.precisaColeta() && lixeira.getNivelAtual() > 0) {
                    proximaLixeira = lixeira;
                    break;
                } else {
                    iterator.remove();
                    lixeira.setColetando(false);
                }
            }
            
            if (proximaLixeira != null) {
                caminhao.setLixeiraAtual(proximaLixeira);
                proximaLixeira.setColetando(true);
                caminhao.setEstado(modelo.caminhao.EstadoCaminhao.INDO_PARA_COLETA);
                System.out.println("➡️ " + caminhao.getId() + " indo para próxima: " + proximaLixeira.getId());
            } else {
                caminhao.setEstado(modelo.caminhao.EstadoCaminhao.RETORNANDO);
                caminhao.setLixeiraAtual(null);
            }
        } else {
            caminhao.setEstado(modelo.caminhao.EstadoCaminhao.RETORNANDO);
            caminhao.setLixeiraAtual(null);
        }
    }
    
    private void calcularEficiencia() {
        if (lixeiras.isEmpty()) {
            eficienciaMedia = 0;
            return;
        }
        
        int totalNivel = 0;
        for (Lixeira lixeira : lixeiras) {
            totalNivel += lixeira.getNivelAtual();
        }
        
        double nivelMedio = (double) totalNivel / lixeiras.size();
        eficienciaMedia = Math.max(0, 100 - nivelMedio);
    }
    
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }
    
    private void notificarObservers(String evento, Object dados) {
        for (IObserver observer : observers) {
            observer.update(evento, dados);
        }
    }
    
    public List<Lixeira> getLixeiras() { return lixeiras; }
    public List<Caminhao> getCaminhoes() { return caminhoes; }
    public IEstrategiaColeta getEstrategiaAtual() { return estrategiaAtual; }
    public boolean isSistemaAtivo() { return sistemaAtivo; }
    public int getColetasRealizadas() { return coletasRealizadas; }
    public double getEficienciaMedia() { return eficienciaMedia; }
}