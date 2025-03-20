import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoordenadorThread extends ProcessoThread {
    private HashMap<Long, Long> recursosEmUso = new HashMap<>();
    private HashMap<Long, List<Long>> recursosSolicitados = new HashMap<>();

    public CoordenadorThread(long id, HashMap<Long, Long> recursosEmUso) {
        super(id);
        this.recursosEmUso = recursosEmUso;
        populaRecurso();
    }

    public HashMap<Long, Long> getRecursosEmUso() {
        return recursosEmUso;
    }

    public synchronized String verificaRecurso(long idRecurso, long idThread) {
        if (recursosEmUso.containsKey(idRecurso)) {
            List<Long> solicitantes = recursosSolicitados.get(idRecurso);
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

        List<Long> solicitantes = recursosSolicitados.get(idRecurso);
        if (solicitantes != null && !solicitantes.isEmpty()) {
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
