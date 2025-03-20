import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ProcessoThread{
    private int id;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()); //

    public ProcessoThread(int id, CoordenadorThread coordenadorThread) {
        this.id = id;

        usaRecurso(coordenadorThread);
    }

    public int getId() {
        return id;
    }

    private int getTempoUso(){
        return ThreadLocalRandom.current().nextInt(5000, 15000);
    }

    private void usaRecurso(CoordenadorThread coordenadorThread) {
        scheduler.scheduleAtFixedRate(() -> {
            try {

                Recurso recursoASerSolicitado = new Recurso(ThreadLocalRandom.current().nextInt(1, 3));

                // Verifica se o recurso está disponível
                String retorno = coordenadorThread.verificaRecurso(recursoASerSolicitado, this);

                // Se o recurso estiver disponível, usa-o
                if (retorno != null) {
                    // Tempo de uso do recurso é aleatório entre 5 a 15 segundos
                    int tempoUso = getTempoUso();

                    System.out.println("Processo " + id + " está usando o recurso " + recursoASerSolicitado + " por "
                            + tempoUso + " ms.");

                    // Simula o uso do recurso
                    Thread.sleep(tempoUso); // Simula o uso do recurso com tempo aleatório

                    // Após o uso do recurso, o processo notifica o coordenador
                    System.out.println("Processo " + id + " finalizou o uso do recurso " + recursoASerSolicitado);

                    // Chama o coordenador para remover o processo do recurso e liberar o recurso
                    // para o próximo
                    coordenadorThread.removerProcessoDoRecurso(recursoASerSolicitado, id);
                } else {
                    // Se o recurso está ocupado, o processo será colocado na fila e tentará
                    // novamente
                    System.out.println("Recurso " + recursoASerSolicitado + " está ocupado. Processo " + this.id
                            + " tentará novamente.");
                    // Reagendar a tentativa para o processo, sem bloquear o sistema
                }
            } catch (InterruptedException e) {
                // Caso o processo seja interrompido, ele deve parar o uso do recurso
                // System.out.println("Processo " + id + " foi interrompido enquanto usava o
                // recurso.");
            }
        }, 1, 6, TimeUnit.SECONDS);
    }
}
