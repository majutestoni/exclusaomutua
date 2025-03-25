import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class Parametros {
    public static final int TEMPO_MORTE_COORDENADOR = 60;
    public static final int TEMPO_MINIMO_PROCESSAMENTO = 5;
    public static final int TEMPO_MAXIMO_PROCESSAMENTO = 15;
    public static final int TEMPO_TENTATIVA_CONSUMO_RECURSO = 10;
    public static final int TEMPO_CRIACAO_NOVO_PROCESSO = 10;
    public static final int NUMERO_RECURSOS = 4;
    public static final int TEMPO_EXECUSAO = 300;
}   

public class Main {
    private static CoordenadorThread coordenadorThread;
    static Map<Integer, ProcessoThread> processos = new HashMap<>();
    static HashMap<Integer, Recurso> recursos = new HashMap<>();
    private static ScheduledExecutorService scheduler;

    private static int CriaNovoProcessoId() {
        int novoId = ThreadLocalRandom.current().nextInt(1, 1000);

        while (processos.containsKey(novoId)) {
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
        processos.put(novoProcessoThread.getId(), novoProcessoThread);
        return novoProcessoThread;
    }

    private static ProcessoThread GetProcessoAleatorio(){
        Random rand = new Random();
        Integer idNovoCoordenador = (Integer) processos.keySet().toArray()[rand.nextInt(processos.size())];
        ProcessoThread processo = processos.get(idNovoCoordenador);

        return processo;
    }

    private synchronized static void SetNovoCoordenadorAleatorio() {
        if (processos.isEmpty())
            return;
 
        coordenadorThread = new CoordenadorThread(GetProcessoAleatorio(), recursos);

        for (ProcessoThread processo : processos.values()) {
            processo.setCoordenador(coordenadorThread);
        }
    }

    private static void EncerraPrograma() {
        for (ProcessoThread processo : processos.values()) {
            processo.EncerraProcesso();
        }
        processos.clear();
        scheduler.shutdown();
        System.out.println("Execução finalizada após 3 minutos.");
    }

    private synchronized static void TrocaCoordenador() {
        if (processos.isEmpty())
            return;

        System.out.println("Derrubando coordenador: " + coordenadorThread.getProcesso().getId());
        ProcessoThread processoCoordenador = coordenadorThread.getProcesso();

        processoCoordenador.EncerraProcesso();
        processos.remove(processoCoordenador.getId());
        SetNovoCoordenadorAleatorio();
    }

    public static void main(String[] args) {
        scheduler = Executors
                .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        PopulaRecursos();

        // Criação da primeira thread coordenadora
        ProcessoThread primeiroProcesso = CriaThread();
        coordenadorThread = new CoordenadorThread(primeiroProcesso, recursos);
        primeiroProcesso.setCoordenador(coordenadorThread);

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
