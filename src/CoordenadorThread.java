import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoordenadorThread extends ProcessoThread {
    private HashMap<Integer, Recurso> recursosEmUso = new HashMap<>();
    private HashMap<Integer, List<ProcessoThread>> recursosSolicitados = new HashMap<>();

    public CoordenadorThread(int id, HashMap<Integer, Recurso> recursosEmUso) {
        super(id,null);
        this.recursosEmUso = recursosEmUso;
        populaRecurso();
    }

    public HashMap<Integer, Recurso> getRecursosEmUso() {
        return recursosEmUso;
    }

    public synchronized String verificaRecurso(Recurso recurso, ProcessoThread thread) {
        int idRecurso = recurso.getId();

        if (recursosEmUso.containsKey(idRecurso)) {

            // O recurso está ocupado, então o processo entra na fila
            List<ProcessoThread> solicitantes = recursosSolicitados.get(idRecurso);

            if (!solicitantes.contains(thread)) {
                solicitantes.add(thread); // Adiciona à fila de solicitantes
            }

            recursosSolicitados.put(idRecurso, solicitantes);
            System.out.println("Processo " + thread.getId() + " entrou na fila para o recurso " + idRecurso);
            return null;
        } else {
            recurso.setThread(thread);

            recursosEmUso.put(idRecurso, recurso);
            System.out.println("Processo " + thread.getId() + " obteve o recurso " + idRecurso);
            return "Recurso liberado";
        }
    }

    // Após o processo liberar o recurso, o coordenador verifica se há algum processo esperando
    public synchronized void removerProcessoDoRecurso(Recurso recurso, int id) {
        int idRecurso = recurso.getId();

        recursosEmUso.remove(idRecurso); // Remove o recurso da lista de recursos em uso

        List<ProcessoThread> solicitantes = recursosSolicitados.get(idRecurso);
        
        if (solicitantes != null && !solicitantes.isEmpty()) {
            // Libera o recurso para o próximo processo da fila
            ProcessoThread proximoProcesso = solicitantes.remove(0); // Pega o próximo processo na fila

            recursosSolicitados.put(idRecurso, solicitantes);

            // Atribui o recurso ao próximo processo
            recursosEmUso.put(idRecurso, recurso);

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
