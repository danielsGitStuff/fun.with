package fun.with;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PrintingTest {
    private static ByteArrayOutputStream errContent;
    private static PrintStream originalErr;

    class Article {
        List<Integer> articleNumbers = new ArrayList<>();

        public Integer sum() {
            int sum = 0;
            for (Integer articleNumber : articleNumbers) {
                sum += articleNumber;
            }
            return sum;
        }
    }

    class Product {
        List<Article> articles = new ArrayList<>();

        public Integer sum() {
            int sum = 0;
            for (Article article : articles) {
                sum += article.sum();
            }
            return sum;
        }
    }

    private Product productOk, productFaulty;
    private Article articleOk, articleFaulty;

    @BeforeEach
    public void before() {
        articleOk = new Article();
        Stream.of(1, 2, 3).forEach(integer -> articleOk.articleNumbers.add(integer));
        productOk = new Product();
        productOk.articles.add(articleOk);

        articleFaulty = new Article();
        Stream.of(1, null, 3).forEach(integer -> articleFaulty.articleNumbers.add(integer));
        productFaulty = new Product();
        productFaulty.articles.add(articleFaulty);


    }

    @BeforeAll
    public static void beforeClass(){
        errContent = new ByteArrayOutputStream();
        originalErr = System.err;
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public static void afterClass(){
        System.setErr(originalErr);
    }

    @AfterEach
    public void after() {
        System.err.println(errContent.toString());
        errContent.reset();
    }

    @Test
    public void printNothing() {
        try {
            System.out.println(productOk.sum());
        } catch (Exception e) {
            Printing.printExceptionStub(productOk, e);
        }
        assertEquals(0, errContent.size());
    }

    @Test
    public void printNpe() {
        try {
            System.out.println(productFaulty.sum());
        } catch (Exception e) {
            Printing.printExceptionStub(productFaulty, e);
        }
        assertTrue(errContent.toString().contains(productFaulty.getClass().getName()));
    }

    @Test
    public void printNpeNoInstance() {
        try {
            System.out.println(productFaulty.sum());
        } catch (Exception e) {
            Printing.printExceptionStub(null, e);
        }
        assertTrue(errContent.toString().contains(productFaulty.getClass().getName()));
    }

    @Test
    public void printNpeClass() {
        try {
            System.out.println(productFaulty.sum());
        } catch (Exception e) {
            Printing.printExceptionStub(PrintingTest.class, e);
        }
        assertTrue(errContent.toString().contains(PrintingTest.class.getName() + "'"));
    }

}