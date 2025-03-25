import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ProcessoThread {
    private int id;
    private Recurso recurso;
    private CoordenadorThread coordenador;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()); //

    public ProcessoThread(int id, CoordenadorThread coordenadorThread) {
        this.id = id;
        coordenador = coordenadorThread;
        TentaUsarRecurso();
        System.out.println("Novo Processo: " + this);
    }

    public int getId() {
        return id;
    }

    private int getTempoUso() {
        return ThreadLocalRandom.current().nextInt(Parametros.TEMPO_MINIMO_PROCESSAMENTO * 1000,
                Parametros.TEMPO_MAXIMO_PROCESSAMENTO * 1000);
    }

    protected void TentaUsarRecurso() {
        scheduler.scheduleAtFixedRate(() -> {
            Recurso recursoASerSolicitado = coordenador.GetRecursoAleatorio();

            Boolean retorno = coordenador.VerificaRecurso(recursoASerSolicitado, this);

            if (retorno) {
                usaRecurso(recursoASerSolicitado);
            }
        }, 1, Parametros.TEMPO_TENTATIVA_CONSUMO_RECURSO, TimeUnit.SECONDS);
    }

    public void usaRecurso(Recurso recurso) {
        try {
            System.out.println(this + " obteve o " + recurso);

            this.setRecurso(recurso);

            int tempoUso = getTempoUso();

            System.out.println(this + " está usando o " + recurso + " por "
                    + tempoUso + " ms.");

            // Simula o uso do recurso
            Thread.sleep(tempoUso); // Simula o uso do recurso com tempo aleatório

            System.out.println(this + " finalizou o uso do " + recurso);

        } catch (Exception e) {
            System.out.println(this + " foi interrompido enquanto usava o " + recurso + "." + e.getMessage());
        } finally {
            coordenador.RemoverProcessoDoRecurso(this);
            this.setRecurso(null);
        }
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public boolean hasRecurso() {
        return recurso != null;
    }

    @Override
    public String toString() {
        return "ProcessoThread " + id;
    }

    public void EncerraProcesso() {
        scheduler.shutdownNow();
    }

    public CoordenadorThread getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(CoordenadorThread coordenador) {
        this.coordenador = coordenador;
    }
}
