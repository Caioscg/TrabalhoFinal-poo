// IEstrategiaColeta.java
package modelo.coleta;

import modelo.lixeira.Lixeira;
import modelo.caminhao.Caminhao;
import java.util.List;

public interface IEstrategiaColeta {
    List<Lixeira> calcularRota(List<Lixeira> lixeiras, Caminhao caminhao);
    String getNome();
    String getCor();
    String getDescricao();
}