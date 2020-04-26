package logmonad.example;

public class Record {
    private final String id;

    public Record(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id='" + id + '\'' +
                '}';
    }
}
