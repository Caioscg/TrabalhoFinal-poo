// RotaEconomica.java
package modelo.coleta;

import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import java.util.*;

public class RotaEconomica implements IEstrategiaColeta {
    
    @Override
    public List<Lixeira> calcularRota(List<Lixeira> lixeiras, Caminhao caminhao) {
        List<Lixeira> lixeirasParaColeta = new ArrayList<>();
        
        // Filtrar apenas lixeiras que precisam de coleta
        for (Lixeira lixeira : lixeiras) {
            if (lixeira.precisaColeta() && !lixeira.isColetando()) {
                lixeirasParaColeta.add(lixeira);
            }
        }
        
        // Ordenar por nível de enchimento (mais cheias primeiro)
        lixeirasParaColeta.sort((l1, l2) -> Integer.compare(l2.getNivelAtual(), l1.getNivelAtual()));
        
        return lixeirasParaColeta;
    }
    
    @Override
    public String getNome() { return "Rota Econômica"; }
    
    @Override
    public String getCor() { return "AZUL"; }
    
    @Override
    public String getDescricao() { return "Prioriza lixeiras mais cheias para otimizar viagens"; }
}