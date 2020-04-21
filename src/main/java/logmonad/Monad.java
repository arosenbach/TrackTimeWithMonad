package logmonad;

import java.util.function.Function;

public interface Monad<T> {

    // monads have 3 fundamental operations :
    // return : T1 -> M<T1>
    // unbox : M<T> -> T
    // bind : M<T> -> (T -> M<U>) -> M<U>

    // Monad unbox : M<T> -> T
    public T get();
    // monad bind : M<T> -> (T -> M<U>) -> M<U>
    <U> Monad<U> bind(Function<T, Monad<U>> mapping);
    // a monad is a functor so it can also fmap
    <U> Monad<U> fmap(Function<T, U> fn);
}