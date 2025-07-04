// RotaCompleta.java
package modelo.coleta;

import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import java.util.*;

public class RotaCompleta implements IEstrategiaColeta {
    
    @Override
    public List<Lixeira> calcularRota(List<Lixeira> lixeiras, Caminhao caminhao) {
        List<Lixeira> lixeirasParaColeta = new ArrayList<>();
        
        // Incluir TODAS as lixeiras (mesmo as que não precisam urgentemente)
        for (Lixeira lixeira : lixeiras) {
            if (lixeira.getNivelAtual() > 20 && !lixeira.isColetando()) {
                lixeirasParaColeta.add(lixeira);
            }
        }
        
        // Algoritmo do vizinho mais próximo otimizado
        if (lixeirasParaColeta.isEmpty()) return lixeirasParaColeta;
        
        List<Lixeira> rotaOtimizada = new ArrayList<>();
        List<Lixeira> naoVisitadas = new ArrayList<>(lixeirasParaColeta);
        
        // Começar pela lixeira mais próxima
        Lixeira atual = encontrarMaisProxima(caminhao.getPosicaoX(), caminhao.getPosicaoY(), naoVisitadas);
        rotaOtimizada.add(atual);
        naoVisitadas.remove(atual);
        
        // Continuar pelo vizinho mais próximo
        while (!naoVisitadas.isEmpty()) {
            Lixeira proxima = encontrarMaisProxima(atual.getPosicaoX(), atual.getPosicaoY(), naoVisitadas);
            rotaOtimizada.add(proxima);
            naoVisitadas.remove(proxima);
            atual = proxima;
        }
        
        return rotaOtimizada;
    }
    
    private Lixeira encontrarMaisProxima(int x, int y, List<Lixeira> lixeiras) {
        Lixeira maisProxima = lixeiras.get(0);
        double menorDistancia = calcularDistancia(x, y, maisProxima.getPosicaoX(), maisProxima.getPosicaoY());
        
        for (Lixeira lixeira : lixeiras) {
            double distancia = calcularDistancia(x, y, lixeira.getPosicaoX(), lixeira.getPosicaoY());
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProxima = lixeira;
            }
        }
        
        return maisProxima;
    }
    
    private double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    @Override
    public String getNome() { return "Rota Completa"; }
    
    @Override
    public String getCor() { return "AMARELO"; }
    
    @Override
    public String getDescricao() { return "Rota otimizada visitando todas as lixeiras necessárias"; }
}