public class Recurso {
    private int id;

    private ProcessoThread thread;

    public Recurso(int id) {
        setId(id);
    }

    public ProcessoThread getThread() {
        return thread;
    }

    public void setThread(ProcessoThread thread) {
        this.thread = thread;
    }

    public boolean hasThread() {
        return thread != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Recurso " + id;
    }

}
