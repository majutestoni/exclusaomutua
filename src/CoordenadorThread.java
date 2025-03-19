import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoordenadorThread extends ProcessoThread {
    private HashMap<Integer, Integer> recursosEmUso = new HashMap<>();
    private HashMap<Integer, List<Integer>> recursosSolicitados = new HashMap<>();

    public CoordenadorThread(int id, HashMap<Integer, Integer> recursosEmUso) {
        super(id);
        this.recursosEmUso = recursosEmUso;
        populaRecurso();
    }

    public HashMap<Integer, Integer> getRecursosEmUso() {
        return recursosEmUso;
    }

    public synchronized String verificaRecurso(Integer idRecurso, Integer idThread) {
        if (recursosEmUso.containsKey(idRecurso)) {
            // O recurso está ocupado, então o processo entra na fila
            List<Integer> solicitantes = recursosSolicitados.get(idRecurso);
            if (!solicitantes.contains(idThread)) {
                solicitantes.add(idThread); // Adiciona à fila de solicitantes
            }
            recursosSolicitados.put(idRecurso, solicitantes);
            System.out.println("Processo " + idThread + " entrou na fila para o recurso " + idRecurso);
            return null; // Não pode usar o recurso ainda
        } else {
            // O recurso está disponível
            recursosEmUso.put(idRecurso, idThread);
            System.out.println("Processo " + idThread + " obteve o recurso " + idRecurso);
            return "Recurso liberado";
        }
    }


    // Após o processo liberar o recurso, o coordenador verifica se há algum processo esperando
    public synchronized void removerProcessoDoRecurso(Integer idRecurso, int id) {
        recursosEmUso.remove(idRecurso); // Remove o recurso da lista de recursos em uso

        List<Integer> solicitantes = recursosSolicitados.get(idRecurso);
        if (solicitantes != null && !solicitantes.isEmpty()) {
            // Libera o recurso para o próximo processo da fila
            int proximoProcesso = solicitantes.remove(0); // Pega o próximo processo na fila
            recursosSolicitados.put(idRecurso, solicitantes);

            // Atribui o recurso ao próximo processo
            recursosEmUso.put(idRecurso, proximoProcesso);
            System.out.println("Recurso " + idRecurso + " foi liberado para o processo " + proximoProcesso);
        }
    }



    // Inicializa os recursos solicitados (para 5 recursos, por exemplo)
    private void populaRecurso() {
        for (int i = 1; i < 6; i++) {
            recursosSolicitados.put(i, new ArrayList<>());
        }
    }
}
