// RotaRapida.java
package modelo.coleta;

import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import java.util.*;

public class RotaRapida implements IEstrategiaColeta {
    
    @Override
    public List<Lixeira> calcularRota(List<Lixeira> lixeiras, Caminhao caminhao) {
        List<Lixeira> lixeirasParaColeta = new ArrayList<>();
        
        // Filtrar apenas lixeiras que precisam de coleta
        for (Lixeira lixeira : lixeiras) {
            if (lixeira.precisaColeta() && !lixeira.isColetando()) {
                lixeirasParaColeta.add(lixeira);
            }
        }
        
        // Ordenar por distância mais próxima
        lixeirasParaColeta.sort((l1, l2) -> {
            double dist1 = calcularDistancia(caminhao.getPosicaoX(), caminhao.getPosicaoY(), 
                                           l1.getPosicaoX(), l1.getPosicaoY());
            double dist2 = calcularDistancia(caminhao.getPosicaoX(), caminhao.getPosicaoY(), 
                                           l2.getPosicaoX(), l2.getPosicaoY());
            return Double.compare(dist1, dist2);
        });
        
        return lixeirasParaColeta;
    }
    
    private double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    @Override
    public String getNome() { return "Rota Rápida"; }
    
    @Override
    public String getCor() { return "VERMELHO"; }
    
    @Override
    public String getDescricao() { return "Coleta primeiro as lixeiras mais próximas"; }
}