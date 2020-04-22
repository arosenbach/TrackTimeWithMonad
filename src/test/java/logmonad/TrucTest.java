package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Truc along with Truc::append is a Monoid")
class TrucTest {

    @Test
    @DisplayName("equality by value")
    public void equals() throws Exception {
        final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
        final Truc truc2 = Truc.of("Acme", Stopwatch.createStarted());
        assertEquals(truc1, truc2);
    }

    @Test
    @DisplayName("right identity: x <> mempty = x)")
    public void rightIdentity()  {
        final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
        final Truc truc2 = truc1.append(Truc.EMPTY);
        assertEquals(truc1, truc2);
    }

    @Test
    @DisplayName("left identity: mempty <> x = x)")
    public void lefttIdentity() {
        final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
        final Truc truc2 = Truc.EMPTY.append(truc1);
        assertEquals(truc1, truc2);
    }

    @Test
    @DisplayName("associativity")
    public void associativity() {
        final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
        final Truc truc2 = Truc.of("Acme", Stopwatch.createStarted());
        final Truc truc3 = Truc.of("Acme", Stopwatch.createStarted());
        assertEquals((truc1.append(truc2)).append(truc3), truc1.append((truc2).append(truc3)));
    }

}