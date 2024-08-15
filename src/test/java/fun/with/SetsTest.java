package fun.with;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetsTest {
    Sets<String> a = Sets.of("a","aa","aaa");
    Sets<String> a2 = Sets.of("a","aa2","aaa");
    Sets<String> b = Sets.of("b","bb","bbb");
    @BeforeEach
    public void before(){}

    @Test
    void intersection() {
        assertEquals(3, a.intersection(a).size());
        assertEquals(2, a.intersection(a2).size());
        assertEquals(0, a.intersection(b).size());
    }

    @Test
    void testIntersection() {
    }
}