import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {
    private static CoordenadorThread coordenadorThread;
    static Map<Integer, Thread> threads = new HashMap<>();
    static HashMap<Integer, Integer> recursosEmUso = new HashMap<>();

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Criação da primeira thread coordenadora
        int primeiroId = ThreadLocalRandom.current().nextInt(1, 1000);
        ProcessoThread primeiraProcessoThread = new ProcessoThread(primeiroId);
        coordenadorThread = new CoordenadorThread(primeiroId, recursosEmUso);

        Thread thread = new Thread(() -> { 
            primeiraProcessoThread.IniciaThread(coordenadorThread,scheduler);
        });

        thread.start();
        threads.put(primeiroId, thread);

        // Scheduler usado para criar threads
        scheduler.scheduleAtFixedRate(() -> {
            int novoId = ThreadLocalRandom.current().nextInt(1, 1000);
            
            while (threads.containsKey(novoId)) {
                novoId = ThreadLocalRandom.current().nextInt(1, 1000);
            }

            Thread t = new Thread(() -> { 
                primeiraProcessoThread.IniciaThread(coordenadorThread,scheduler);
            });

            threads.put(novoId, t);

            t.start();
            System.out.println("Nova thread: " + novoId);
        }, 1, 5, TimeUnit.SECONDS);

        // scheduler usado para derrubar e definir novo coordenador
        scheduler.scheduleAtFixedRate(() -> {
            if (!threads.isEmpty()) {
                recursosEmUso = coordenadorThread.getRecursosEmUso();
                System.out.println("Derrubando coordenador: " + coordenadorThread.getId());
                Thread threadToInterrupt = threads.get(coordenadorThread.getId());
                threadToInterrupt.interrupt();
                threads.remove(coordenadorThread.getId());

                // Verifica se ainda há threads e seleciona uma nova thread aleatória como coordenadora
                if (!threads.isEmpty()) {
                    Random rand = new Random();
                    Integer idNovoCoordenador = (Integer) threads.keySet().toArray()[rand.nextInt(threads.size())];
                    coordenadorThread = new CoordenadorThread(idNovoCoordenador, recursosEmUso);

                    System.out.println("Novo coordenador: " + idNovoCoordenador);


                }
            }
        }, 1, 20, TimeUnit.SECONDS);


        // Scheduler usado para definir o tempo de execução do programa
        scheduler.schedule(() -> {
            scheduler.shutdown();
            System.out.println("Execução finalizada após 3 minutos.");
        }, 3, TimeUnit.MINUTES);
    }

}
