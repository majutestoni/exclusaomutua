import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {
    private static CoordenadorThread coordenadorThread;
    static Map<Long, ProcessoThread> threads = new HashMap<>();
    static HashMap<Long, Long> recursosEmUso = new HashMap<>();

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
            long novoId = ThreadLocalRandom.current().nextInt(1, 1000);
            while (threads.containsKey(novoId)) {
                novoId = ThreadLocalRandom.current().nextInt(1, 1000);
            }

            ProcessoThread novoProcessoThread = new ProcessoThread(novoId);
            threads.put(novoId, novoProcessoThread);
            Thread thread = new Thread(novoProcessoThread);
            thread.start();
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
                    long idNovoCoordenador = (long) threads.keySet().toArray()[rand.nextInt(threads.size())];
                    coordenadorThread = new CoordenadorThread(idNovoCoordenador, recursosEmUso);

                    System.out.println("Novo coordenador: " + idNovoCoordenador);


                }
            }
        }, 1, 20, TimeUnit.SECONDS);


        // Para recurso ser solicitado
        scheduler.scheduleAtFixedRate(() -> {
            if (!threads.isEmpty()) {
                // Escolher um processo aleatório
                Random rand = new Random();
                long idProcesso = (long) threads.keySet().toArray()[rand.nextInt(threads.size())];
                ProcessoThread processoThread = threads.get(idProcesso);

                long recursoASerSolicitado = ThreadLocalRandom.current().nextLong(1, 3);

                // Verifica se o recurso está disponível
                String retorno = coordenadorThread.verificaRecurso(recursoASerSolicitado, idProcesso);

                if (retorno != null) {
                    // Se o recurso estiver disponível, usa-o
                    processoThread.usaRecurso(recursoASerSolicitado, coordenadorThread);
                } else {
                    // Se o recurso está ocupado, o processo será colocado na fila e tentará novamente
                    System.out.println("Recurso " + recursoASerSolicitado + " está ocupado. Processo " + idProcesso + " tentará novamente.");
                    // Reagendar a tentativa para o processo, sem bloquear o sistema
                }
            }
        }, 1, 6, TimeUnit.SECONDS);


        // Scheduler usado para definir o tempo de execução do programa
        scheduler.schedule(() -> {
            scheduler.shutdown();
            System.out.println("Execução finalizada após 3 minutos.");
        }, 3, TimeUnit.MINUTES);
    }

}
