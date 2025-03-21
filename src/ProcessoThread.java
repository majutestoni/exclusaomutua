import java.util.concurrent.ThreadLocalRandom;

public class ProcessoThread extends Thread {
    private final Long id;

    public ProcessoThread(Long id) {
        this.id = id;
    }

    public long getProcessoId() {
        return id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10000, 25001));
            }
        } catch (InterruptedException e) {
            System.out.println("Processo " + id + " foi interrompido.");
        }
    }

    public void usaRecurso(long idRecurso, CoordenadorThread coordenadorThread) {
        try {
            int tempoUso = ThreadLocalRandom.current().nextInt(5000, 15000);
            System.out.println("Processo " + id + " está usando o recurso " + idRecurso + " por " + tempoUso + " ms.");

            Thread.sleep(tempoUso); // Simula o uso do recurso com tempo aleatório

            System.out.println("Processo " + id + " finalizou o uso do recurso " + idRecurso);
            coordenadorThread.removerProcessoDoRecurso(idRecurso, id);

        } catch (InterruptedException e) {
            System.out.println("Processo " + id + " foi interrompido enquanto usava o recurso.");
        }
    }
}
