import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoordenadorThread extends ProcessoThread {
    private final HashMap<Long, Long> recursosEmUso;
    private final HashMap<Long, List<Long>> recursosSolicitados = new HashMap<>();

    public CoordenadorThread(long id, HashMap<Long, Long> recursosEmUso) {
        super(id);
        this.recursosEmUso = new HashMap<>(recursosEmUso);
        populaRecurso();
    }

    public HashMap<Long, Long> getRecursosEmUso() {
        return new HashMap<>(recursosEmUso);
    }

    public synchronized String verificaRecurso(long idRecurso, long idThread) {
        List<Long> solicitantes = recursosSolicitados.getOrDefault(idRecurso, new ArrayList<>());

        if (recursosEmUso.containsKey(idRecurso)) {
            if (!solicitantes.contains(idThread)) {
                solicitantes.add(idThread); // Adiciona à fila de solicitantes
            }
            recursosSolicitados.put(idRecurso, solicitantes);
            System.out.println("Processo " + idThread + " entrou na fila para o recurso " + idRecurso);
            return null;
        } else {
            recursosEmUso.put(idRecurso, idThread);
            System.out.println("Processo " + idThread + " obteve o recurso " + idRecurso);
            return "Recurso liberado";
        }
    }

    // Após o processo liberar o recurso, o coordenador verifica se há algum processo esperando
    public synchronized void removerProcessoDoRecurso(long idRecurso, long id) {
        recursosEmUso.remove(idRecurso); // Remove o recurso da lista de recursos em uso

        List<Long> solicitantes = recursosSolicitados.getOrDefault(idRecurso, new ArrayList<>());
        if (!solicitantes.isEmpty()) {
            long proximoProcesso = solicitantes.remove(0);
            recursosSolicitados.put(idRecurso, solicitantes);

            recursosEmUso.put(idRecurso, proximoProcesso);
            System.out.println("Recurso " + idRecurso + " foi liberado para o processo " + proximoProcesso);
        }
    }

    // Inicializa os recursos solicitados (para 5 recursos, por exemplo)
    private void populaRecurso() {
        for (long i = 1; i < 6; i++) {
            recursosSolicitados.put(i, new ArrayList<>());
        }
    }
}
