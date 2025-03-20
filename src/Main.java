import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {
    private static CoordenadorThread coordenadorThread;
    static Map<Integer, ProcessoThread> threads = new HashMap<>();
    static HashMap<Integer, Recurso> recursosEmUso = new HashMap<>();


    private static int createNewId(){
        int novoId = ThreadLocalRandom.current().nextInt(1, 1000);
            
        while (threads.containsKey(novoId)) {
            novoId = ThreadLocalRandom.current().nextInt(1, 1000);
        }

        return novoId;
    }

    private static ProcessoThread createThread(){
        int novoId = createNewId();
        ProcessoThread novoProcessoThread = new ProcessoThread(novoId,coordenadorThread);
        return novoProcessoThread;
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()); // 

        // Criação da primeira thread coordenadora
        ProcessoThread primeiroProcesso = createThread();
        coordenadorThread = new CoordenadorThread(primeiroProcesso.getId(), recursosEmUso);
        threads.put(primeiroProcesso.getId(), primeiroProcesso);

        // Scheduler usado para criar threads
        scheduler.scheduleAtFixedRate(() -> {
            ProcessoThread processo = createThread();
            threads.put(processo.getId(), processo);

            System.out.println("Nova thread: " + processo.getId());
        }, 1, 5, TimeUnit.SECONDS);

        // // scheduler usado para derrubar e definir novo coordenador
        // scheduler.scheduleAtFixedRate(() -> {
        //     if (!threads.isEmpty()) {
        //         recursosEmUso = coordenadorThread.getRecursosEmUso();
        //         System.out.println("Derrubando coordenador: " + coordenadorThread.getId());
        //         ProcessoThread threadToInterrupt = threads.get(coordenadorThread.getId());
        //         //Thread.interrupt();
        //         threads.remove(coordenadorThread.getId());

        //         // Verifica se ainda há threads e seleciona uma nova thread aleatória como coordenadora
        //         if (!threads.isEmpty()) {
        //             Random rand = new Random();
        //             Integer idNovoCoordenador = (Integer) threads.keySet().toArray()[rand.nextInt(threads.size())];
        //             coordenadorThread = new CoordenadorThread(idNovoCoordenador, recursosEmUso);

        //             System.out.println("Novo coordenador: " + idNovoCoordenador);


        //         }
        //     }
        // }, 1, 20, TimeUnit.SECONDS);


        // Scheduler usado para definir o tempo de execução do programa
        scheduler.schedule(() -> {
            threads.clear();
            recursosEmUso.clear();
            scheduler.shutdown();
            System.out.println("Execução finalizada após 3 minutos.");
        }, 3, TimeUnit.MINUTES);
    }
}
