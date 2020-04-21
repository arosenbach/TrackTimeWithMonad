package logmonad;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class Truc {

    private String name;
    private Stopwatch stopwatch;

    private Truc(String name, Stopwatch stopwatch) {
        this.name = name;
        this.stopwatch = stopwatch;
    }

    public static Truc of(final String name, final Stopwatch stopwatch) {
        return new Truc(name, stopwatch);
    }

    public Truc append(Truc other){
        return this; // TODO
    }

    public long elapsed(final TimeUnit timeUnit) {
        return stopwatch.elapsed(timeUnit);
    }

    public String getName() {
        return name;
    }
}
