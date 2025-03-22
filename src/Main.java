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
    public static final int TEMPO_EXECUSAO = 180;
}   

public class Main {
    private static CoordenadorThread coordenadorThread;
    static Map<Integer, ProcessoThread> threads = new HashMap<>();
    static HashMap<Integer, Recurso> recursos = new HashMap<>();
    private static ScheduledExecutorService scheduler;

    private static int CriaNovoProcessoId() {
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

    private static ProcessoThread CriaThread() {
        int novoId = CriaNovoProcessoId();
        ProcessoThread novoProcessoThread = new ProcessoThread(novoId, coordenadorThread);
        threads.put(novoProcessoThread.getId(), novoProcessoThread);
        return novoProcessoThread;
    }

    private static ProcessoThread GetProcessoAleatorio(){
        Random rand = new Random();
        Integer idNovoCoordenador = (Integer) threads.keySet().toArray()[rand.nextInt(threads.size())];
        ProcessoThread processo = threads.get(idNovoCoordenador);

        return processo;
    }

    private static void SetNovoCoordenador() {
        if (threads.isEmpty())
            return;

        ProcessoThread processo = GetProcessoAleatorio();
 
        coordenadorThread = new CoordenadorThread(processo, recursos);
    }

    private static void EncerraPrograma() {
        threads.clear();
        scheduler.shutdown();
        System.out.println("Execução finalizada após 3 minutos.");
    }

    private static void TrocaCoordenador() {
        if (threads.isEmpty())
            return;

        System.out.println("Derrubando coordenador: " + coordenadorThread.getProcesso().getId());
        threads.remove(coordenadorThread.getProcesso().getId());

        SetNovoCoordenador();
    }

    public static void main(String[] args) {
        scheduler = Executors
                .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        PopulaRecursos();

        // Criação da primeira thread coordenadora
        ProcessoThread primeiroProcesso = CriaThread();
        coordenadorThread = new CoordenadorThread(primeiroProcesso, recursos);

        scheduler.scheduleAtFixedRate(() -> {
            CriaThread();
        }, Parametros.TEMPO_CRIACAO_NOVO_PROCESSO, Parametros.TEMPO_CRIACAO_NOVO_PROCESSO, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            TrocaCoordenador();
        }, Parametros.TEMPO_MORTE_COORDENADOR, Parametros.TEMPO_MORTE_COORDENADOR, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            EncerraPrograma();
        }, Parametros.TEMPO_EXECUSAO, TimeUnit.SECONDS);
    }
}
