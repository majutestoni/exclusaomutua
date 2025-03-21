import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CoordenadorThread extends ProcessoThread {
    private HashMap<Integer, Recurso> recursos = new HashMap<>();
    private HashMap<Integer, List<ProcessoThread>> recursosSolicitados = new HashMap<>();

    public CoordenadorThread(int id, HashMap<Integer, Recurso> recursosEmUso) {
        super(id, null);
        this.recursos = recursosEmUso;
        System.out.println("Novo coordenador: " + this);
    }

    public HashMap<Integer, Recurso> getRecursos() {
        return recursos;
    }

    public Boolean verificaRecurso(Recurso recurso, ProcessoThread thread) {
        int idRecurso = recurso.getId();

        if (recurso.possuiThread()) {

            if (thread.PossuiRecurso())
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
            this.TentaUsarRecurso(this);
            return true;
        }
    }

    // Após o processo liberar o recurso, o coordenador verifica se há algum
    // processo esperando
    public void removerProcessoDoRecurso(ProcessoThread thread) {
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

    public Recurso getRandomRecurso() {
        if (recursos.isEmpty()) {
            return null; // Retorna null se não houver recursos
        }

        List<Recurso> valores = new ArrayList<>(recursos.values());
        Random generator = new Random();
        int index = generator.nextInt(valores.size());
        return valores.get(index); // Pega um índice aleatório seguro
    }
}
