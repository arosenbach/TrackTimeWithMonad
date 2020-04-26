package logmonad.other;

public interface Monoid<A> {

    A zero();
    Monoid<A> append(Monoid<A> monoid);
    String getDelimiter();

}