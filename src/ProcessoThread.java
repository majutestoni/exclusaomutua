import java.util.concurrent.ThreadLocalRandom;

public class ProcessoThread extends Thread {
    private Long id;

    public ProcessoThread(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public void run() {

    }

    public void usaRecurso(Integer idRecurso, CoordenadorThread coordenadorThread) {
       // try {
            int tempoUso = ThreadLocalRandom.current().nextInt(5000, 15000);
            System.out.println("Processo " + id + " está usando o recurso " + idRecurso + " por " + tempoUso + " ms.");

            //Thread.sleep(tempoUso);  // Simula o uso do recurso com tempo aleatório
        for (int i = 0; i < 100000; i++) {

        }

            System.out.println("Processo " + id + " finalizou o uso do recurso " + idRecurso);

            coordenadorThread.removerProcessoDoRecurso(idRecurso, id);

    //    } catch (InterruptedException e) {
            // Caso o processo seja interrompido, ele deve parar o uso do recurso
       //     System.out.println("Processo " + id + " foi interrompido enquanto usava o recurso.");
     //   }
    }
}
