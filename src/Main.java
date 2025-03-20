import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class Parametros {
    public static final int TEMPO_MORTE_COORDENADOR = 60;
    public static final int TEMPO_MINIMO_PROCESSAMENTO = 10;
    public static final int TEMPO_MAXIMO_PROCESSAMENTO = 15;
    public static final int TEMPO_TENTATIVA_CONSUMO_RECURSO = 10;
    public static final int TEMPO_CRIACAO_NOVO_PROCESSO = 10;
    public static final int NUMERO_RECURSOS = 1;
}

public class Main {
    private static CoordenadorThread coordenadorThread;
    static Map<Integer, ProcessoThread> threads = new HashMap<>();
    static HashMap<Integer, Recurso> recursos = new HashMap<>();

    private static int createNewId() {
        int novoId = ThreadLocalRandom.current().nextInt(1, 1000);

        while (threads.containsKey(novoId)) {
            novoId = ThreadLocalRandom.current().nextInt(1, 1000);
        }

        return novoId;
    }

    private static void PopulaRecursos() {
        for (int i = 1; i <= Parametros.NUMERO_RECURSOS; i++) {
            Recurso recurso = new Recurso(i);
            recursos.put(i, recurso);
        }
    }

    private static ProcessoThread createThread() {
        int novoId = createNewId();
        ProcessoThread novoProcessoThread = new ProcessoThread(novoId, coordenadorThread);
        return novoProcessoThread;
    }

    private static void DecideNovoCoordenador() {
        Random rand = new Random();
        Integer idNovoCoordenador = (Integer) threads.keySet().toArray()[rand.nextInt(threads.size())];
        coordenadorThread = new CoordenadorThread(idNovoCoordenador, recursos);
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors
                .newScheduledThreadPool(Runtime.getRuntime().availableProcessors()); //

        PopulaRecursos();

        // Criação da primeira thread coordenadora
        ProcessoThread primeiroProcesso = createThread();
        coordenadorThread = new CoordenadorThread(primeiroProcesso.getId(), recursos);
        threads.put(primeiroProcesso.getId(), primeiroProcesso);

        // Scheduler usado para criar threads
        scheduler.scheduleAtFixedRate(() -> {
            ProcessoThread processo = createThread();
            threads.put(processo.getId(), processo);
        }, Parametros.TEMPO_CRIACAO_NOVO_PROCESSO, Parametros.TEMPO_CRIACAO_NOVO_PROCESSO, TimeUnit.SECONDS);

        // scheduler usado para derrubar e definir novo coordenador
        scheduler.scheduleAtFixedRate(() -> {
            if (!threads.isEmpty()) {

                System.out.println("Derrubando coordenador: " + coordenadorThread.getId());
                threads.remove(coordenadorThread.getId());
                // Verifica se ainda há threads e seleciona uma nova thread aleatória como
                // coordenadora

                if (!threads.isEmpty()) {
                    DecideNovoCoordenador();
                }
            }
            ;
        }, Parametros.TEMPO_MORTE_COORDENADOR, Parametros.TEMPO_MORTE_COORDENADOR, TimeUnit.SECONDS);

        // Scheduler usado para definir o tempo de execução do programa
        scheduler.schedule(() -> {
            threads.clear();
            scheduler.shutdown();
            System.out.println("Execução finalizada após 3 minutos.");
        }, 3, TimeUnit.MINUTES);
    }
}
