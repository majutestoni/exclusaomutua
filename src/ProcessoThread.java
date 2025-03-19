import java.util.concurrent.ThreadLocalRandom;

public class ProcessoThread implements Runnable {
    private int id;

    public ProcessoThread(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        synchronized (ProcessoThread.class) {
            System.out.println("Nova thread: " + id);
        }
    }

    public void usaRecurso(Integer idRecurso, CoordenadorThread coordenadorThread) {
        try {
            // Tempo de uso do recurso é aleatório entre 5 a 15 segundos
            int tempoUso = ThreadLocalRandom.current().nextInt(5000, 15000);
            System.out.println("Processo " + id + " está usando o recurso " + idRecurso + " por " + tempoUso + " ms.");

            // Simula o uso do recurso
            Thread.sleep(tempoUso);  // Simula o uso do recurso com tempo aleatório

            // Após o uso do recurso, o processo notifica o coordenador
            System.out.println("Processo " + id + " finalizou o uso do recurso " + idRecurso);

            // Chama o coordenador para remover o processo do recurso e liberar o recurso para o próximo
            coordenadorThread.removerProcessoDoRecurso(idRecurso, id);

        } catch (InterruptedException e) {
            // Caso o processo seja interrompido, ele deve parar o uso do recurso
            System.out.println("Processo " + id + " foi interrompido enquanto usava o recurso.");
        }
    }
}
