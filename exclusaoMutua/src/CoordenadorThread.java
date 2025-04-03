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

    public synchronized Boolean VerificaRecurso(Recurso recurso, ProcessoThread processo) {
        int idRecurso = recurso.getId();

        if (recurso.hasThread()) {

            if (processo.hasRecurso())
                return false;

            boolean hasRecurso = recursosSolicitados.containsKey(idRecurso);

            if(!hasRecurso){
                List<ProcessoThread> solicitantes = new ArrayList<ProcessoThread>();
                solicitantes.add(processo);
                System.out.println(processo + " entrou na fila para o " + recurso
                + ". Fila " + recurso + " => "
                + solicitantes);
                recursosSolicitados.put(idRecurso, solicitantes);
            }else {
                List<ProcessoThread> solicitantes = recursosSolicitados.get(idRecurso);

                if (!solicitantes.contains(processo)) {
                    solicitantes.add(processo);
                    System.out.println(processo + " entrou na fila para o " + recurso
                            + ". Fila " + recurso + " => "
                            + solicitantes);
                    recursosSolicitados.put(idRecurso, solicitantes);
                }
            }
            
            return false;
        } else {
            recurso.setThread(processo);
            processo.TentaUsarRecurso();
            return true;
        }
    }

    public synchronized void RemoverProcessoDoRecurso(ProcessoThread thread) {
        Recurso recurso = thread.getRecurso();
        int idRecurso = recurso.getId();

        recurso.setThread(null);

        List<ProcessoThread> solicitantes = recursosSolicitados.get(idRecurso);

        if (solicitantes != null && !solicitantes.isEmpty()) {
            ProcessoThread proximoProcesso = solicitantes.remove(0);

            recursosSolicitados.put(idRecurso, solicitantes);

            System.out
                    .println(recurso + " foi liberado para o processo " + proximoProcesso + ". Fila " + recurso + " => "
                            + solicitantes);

            recurso.setThread(proximoProcesso);
            proximoProcesso.usaRecurso(recurso);
        }
    }

    public synchronized Recurso GetRecursoAleatorio() {
        if (recursos.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        Integer idRecurso = (Integer) recursos.keySet().toArray()[rand.nextInt(recursos.size())];
        return recursos.get(idRecurso);
    }

    public synchronized HashMap<Integer, Recurso> getRecursos() {
        return recursos;
    }

    public synchronized ProcessoThread getProcesso() {
        return processo;
    }

    public synchronized void setProcesso(ProcessoThread processo) {
        this.processo = processo;
    }
}
