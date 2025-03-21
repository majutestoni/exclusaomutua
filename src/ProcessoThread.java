import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ProcessoThread {
    private int id;
    private Recurso recurso;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()); //

    public ProcessoThread(int id, CoordenadorThread coordenadorThread) {
        this.id = id;
        TentaUsarRecurso(coordenadorThread);
        System.out.println("Nova thread: " + this.toString());
    }

    public int getId() {
        return id;
    }

    private int getTempoUso() {
        return ThreadLocalRandom.current().nextInt(Parametros.TEMPO_MINIMO_PROCESSAMENTO * 1000,
                Parametros.TEMPO_MAXIMO_PROCESSAMENTO * 1000);
    }

    protected void TentaUsarRecurso(CoordenadorThread coordenadorThread) {
        scheduler.scheduleAtFixedRate(() -> {
            Recurso recursoASerSolicitado = coordenadorThread.getRandomRecurso();

            // Verifica se o recurso está disponível
            Boolean retorno = coordenadorThread.verificaRecurso(recursoASerSolicitado, this);

            // Se o recurso estiver disponível, usa-o
            if (retorno) {
                usaRecurso(recursoASerSolicitado, coordenadorThread);
            }
        }, 1, Parametros.TEMPO_TENTATIVA_CONSUMO_RECURSO, TimeUnit.SECONDS);
    }

    public void usaRecurso(Recurso recurso, CoordenadorThread coordenadorThread) {
        try {
            // Tempo de uso do recurso é aleatório entre 5 a 15 segundos
            this.setRecurso(recurso);

            int tempoUso = getTempoUso();

            System.out.println("Processo " + id + " está usando o recurso " + recurso + " por "
                    + tempoUso + " ms.");

            // Simula o uso do recurso
            Thread.sleep(tempoUso); // Simula o uso do recurso com tempo aleatório

            // Após o uso do recurso, o processo notifica o coordenador
            System.out.println("Processo " + id + " finalizou o uso do recurso " + recurso);

            // Chama o coordenador para remover o processo do recurso e liberar o recurso
            // para o próximo
        } catch (Exception e) {
            System.out.println("Processo " + id + " foi interrompido enquanto usava o recurso." + recurso.getId() + "." + e.getMessage());
        } finally {
            coordenadorThread.removerProcessoDoRecurso(this);
            this.setRecurso(null);
        }
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        System.out.println("Processo " + this.getId() + " obteve o recurso " + recurso.getId());
        this.recurso = recurso;
    }

    public boolean PossuiRecurso() {
        return recurso != null;
    }

    @Override
    public String toString() {
        return "ProcessoThread " + id;
    }

}
