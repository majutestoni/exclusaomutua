import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {
    private static CoordenadorThread coordenadorThread;
    private static final Map<Long, ProcessoThread> threads = new ConcurrentHashMap<>();
    private static HashMap<Long, Long> recursosEmUso = new HashMap<>();

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        // Criação da primeira thread coordenadora
        long primeiroId = ThreadLocalRandom.current().nextInt(1, 1000);
        System.out.println("Nova thread: " + primeiroId);
        ProcessoThread primeiraProcessoThread = new ProcessoThread(primeiroId);
        coordenadorThread = new CoordenadorThread(primeiroId, recursosEmUso);
        System.out.println("Novo coordenador: " + primeiroId);
        threads.put(primeiroId, primeiraProcessoThread);
        Thread primeiraThread = new Thread(primeiraProcessoThread);
        primeiraThread.start();

        // Scheduler usado para criar threads
        scheduler.scheduleAtFixedRate(() -> {
            long novoId;
            do {
                novoId = ThreadLocalRandom.current().nextInt(1, 1000);
            } while (threads.containsKey(novoId));

            ProcessoThread novoProcessoThread = new ProcessoThread(novoId);
            threads.put(novoId, novoProcessoThread);
            Thread thread = new Thread(novoProcessoThread);
            thread.start();
            System.out.println("Nova thread: " + novoId);
        }, 0, 40, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            if (!threads.isEmpty()) {
                recursosEmUso = coordenadorThread.getRecursosEmUso();
                System.out.println("Derrubando coordenador: " + coordenadorThread.getProcessoId());
                ProcessoThread threadToInterrupt = threads.remove(coordenadorThread.getProcessoId());

                if (threadToInterrupt != null) {
                    threadToInterrupt.interrupt();
                }

                // Verifica se ainda há threads e seleciona uma nova thread aleatória como coordenadora
                if (!threads.isEmpty()) {
                    synchronized (threads) {
                        List<Long> ids = new ArrayList<>(threads.keySet());
                        long idNovoCoordenador = ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
                        coordenadorThread = new CoordenadorThread(idNovoCoordenador, recursosEmUso);
                        System.out.println("Novo coordenador: " + idNovoCoordenador);
                    }
                }
            }
        }, 1, 60, TimeUnit.SECONDS);

        // Para recurso ser solicitado
        scheduler.scheduleAtFixedRate(() -> {
            if (!threads.isEmpty()) {
                List<Long> ids = new ArrayList<>(threads.keySet());
                long idProcesso = ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
                ProcessoThread processoThread = threads.get(idProcesso);
                long recursoASerSolicitado = ThreadLocalRandom.current().nextLong(1, 6);

                String retorno = coordenadorThread.verificaRecurso(recursoASerSolicitado, idProcesso);
                if (retorno != null) {
                    processoThread.usaRecurso(recursoASerSolicitado, coordenadorThread);
                } else {
                    System.out.println("Recurso " + recursoASerSolicitado + " está ocupado. Processo " + idProcesso + " tentará novamente.");
                }
            }
        }, 0, ThreadLocalRandom.current().nextInt(10, 26), TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            System.out.println("Execução finalizada após 3 minutos.");
        }, 3, TimeUnit.MINUTES);
    }

}
