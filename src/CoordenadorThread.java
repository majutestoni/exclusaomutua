import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CoordenadorThread {
    private ProcessoThread processo;
    private HashMap<Integer, Recurso> recursos = new HashMap<>();
    private HashMap<Integer, List<ProcessoThread>> recursosSolicitados = new HashMap<>();

    public CoordenadorThread(ProcessoThread pProcesso, HashMap<Integer, Recurso> recursosEmUso) {
        this.recursos = recursosEmUso;
        this.processo = pProcesso;
        System.out.println("Novo coordenador: " + processo);
    }

    public Boolean VerificaRecurso(Recurso recurso, ProcessoThread thread) {
        int idRecurso = recurso.getId();

        if (recurso.hasThread()) {

            if (thread.hasRecurso())
                return false;

            // O recurso está ocupado, então o processo entra na fila
            List<ProcessoThread> solicitantes = recursosSolicitados.get(idRecurso);

            if (solicitantes == null) {
                solicitantes = new ArrayList<ProcessoThread>();
            }

            if (!solicitantes.contains(thread)) {
                solicitantes.add(thread); // Adiciona à fila de solicitantes

                System.out.println("Processo " + thread + " entrou na fila para o recurso " + idRecurso
                        + ". Fila " + recurso + " => "
                        + solicitantes);

                recursosSolicitados.put(idRecurso, solicitantes);
            }

            return false;
        } else {
            recurso.setThread(thread);
            processo.TentaUsarRecurso(this);
            return true;
        }
    }

    public void RemoverProcessoDoRecurso(ProcessoThread thread) {
        Recurso recurso = thread.getRecurso();
        int idRecurso = recurso.getId();

        recurso.setThread(null);

        List<ProcessoThread> solicitantes = recursosSolicitados.get(idRecurso);

        if (solicitantes != null && !solicitantes.isEmpty()) {
            // Libera o recurso para o próximo processo da fila
            ProcessoThread proximoProcesso = solicitantes.remove(0); // Pega o próximo processo na fila

            recursosSolicitados.put(idRecurso, solicitantes);

            // Atribui o recurso ao próximo processo
            System.out
                    .println(recurso + " foi liberado para o processo " + proximoProcesso + ". Fila " + recurso + " => "
                            + solicitantes);

            recurso.setThread(proximoProcesso);
            proximoProcesso.usaRecurso(recurso, this);
        }
    }

    public Recurso GetRecusoAleatorio() {
        if (recursos.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        Integer idRecurso = (Integer) recursos.keySet().toArray()[rand.nextInt(recursos.size())];
        return recursos.get(idRecurso);
    }

    public HashMap<Integer, Recurso> getRecursos() {
        return recursos;
    }

    public ProcessoThread getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoThread processo) {
        this.processo = processo;
    }
}
