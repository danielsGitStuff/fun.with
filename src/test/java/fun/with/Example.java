package fun.with;

import fun.with.misc.Pair;

import java.util.Objects;

public class Example {
    public class House {
        public int doors;
        public int windows;

        public House(int doors, int windows) {
            this.doors = doors;
            this.windows = windows;
        }

        @Override
        public String toString() {
            return "House{" +
                    "doors=" + doors +
                    ", windows=" + windows +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            House house = (House) o;
            return doors == house.doors && windows == house.windows;
        }

        @Override
        public int hashCode() {
            return Objects.hash(doors, windows);
        }
    }

    public void generateReadmeMarkdown() {
        caption("fun.with", 0);
        text("Bring a bit of Kotlin like goodness to Java to make Lists etc more fun.\n" +
                "Just the basic stuff.");
        br();
        caption("Examples", 1);

        p1("public class House {\n" +
                "    public int doors;\n" +
                "    public int windows;\n" +
                "}", "Have a `House` class:\n");

        Lists<House> houses = Lists.of(new House(2, 4), new House(3, 5), new House(4, 6));
        Lists<House> houses2 = Lists.of(new House(2, 4), new House(3, 5));

        p1("Lists<House> houses = Lists.of(new House(2, 4), new House(3, 5), new House(4, 6));\n" +
                "Lists<House> houses2 = Lists.of(new House(2, 4), new House(3, 5));\n" +
                "// => " + houses +
                "// => " + houses2, null);

        Lists<House> additionalHouses = houses.copy().add(new House(10, 11)).add(new House(12, 13));
        p2("Lists<House> additionalHouses = houses.copy().add(new House(10, 11)).add(new House(12, 13));", "add elements as you go (modifies the underlying list).", additionalHouses);

        Maps<House, Integer> house2windows = houses.associateWith(house -> house.windows + 1);
        this.p2("Maps<House, Integer> house2windows = houses.associateWith(house -> house.windows + 1);", "create a Map with all houses and their according amount of windows", house2windows);

        Maps<Integer, House> windows2house = houses.associateBy(house -> house.windows);
        p2("Maps<Integer, House> windows2house = houses.associateBy(house -> house.windows);", "create a Map with all houses and their according amount of windows", windows2house);

        Lists<House> mapped = houses.mapIndexed((index, house) -> new House(index + 1, house.windows));
        p2("Lists<House> mapped = houses.mapIndexed((index, house) -> new House(index + 1, house.windows));", "have an indexed map method", mapped);

        Maps<Integer, Lists<House>> doors2house = houses.copy().add(new House(2, 9)).groupBy(house -> house.doors);
        p2("Maps<Integer, Lists<House>> doors2house = houses.copy().add(new House(2, 9)).groupBy(house -> house.doors);", "group by", doors2house);

        Maps<Integer, Lists<Integer>> doors2windows = houses.copy().add(new House(2, 9)).groupBy(h -> h.doors, house -> house.windows);
        p2("Maps<Integer, Lists<Integer>> doors2windows = houses.copy().add(new House(2, 9)).groupBy(h -> h.doors, house -> house.windows);", "group by with value selector", doors2windows);

        Maps<Integer, Integer> door2firstWindows = doors2house.mapValues((doors, hs) -> hs.first().windows);
        p2("Maps<Integer, Integer> door2firstWindows = doors2house.mapValues((integer, hs) -> hs.first().windows);", "map function for Map values only", door2firstWindows);

        Maps<String, House> mappedMap = doors2house.map((doors, hs) -> Pair.of("S" + doors, hs.first()));
        p2("Maps<String, House> mappedMap = doors2house.map((doors, hs) -> Pair.of(\"S\" + doors, hs.first()));", "map the whole Map", mappedMap);

        Lists<House> intersection = houses.intersection(houses2);
        p2("Lists<House> intersection = houses.intersection(houses2);", "intersect collections", intersection);

        Lists<House> subtraction = houses.subtract(houses2);
        p2("Lists<House> subtraction = houses.subtract(houses2);", "subtract collections", subtraction);

        p1("  Sets.of(1, 2, 3);\n" +
                "  Lists.of(1, 2, 3).sets();\n" +
                "  Sets.wrap(Arrays.asList(1, 2, 3))", "create Sets");


    }

    public static void br() {
        System.out.println();
    }

    public static void text(String text) {
        if (text.contains("\n"))
            text(Lists.of(text.split("\n")));
        else
            System.out.println(Lists.of(text));
    }

    public static void text(Lists<String> text) {
        text.forEach(System.out::println);
    }


    public static void caption(String text, int indent) {
        Lists<String> indents = Lists.of("#").repeat(indent + 1);
        System.out.println(indents.join("") + " " + text);
        System.out.println();
    }

    public static void p1(Lists<String> codeLines, String caption) {
        if (caption != null)
            System.out.println(caption);
        System.out.println("```java");
        codeLines.forEach(System.out::println);
        System.out.println("```");
        System.out.println();
    }

    public static void p1(String codeLines, String caption) {
        if (codeLines.contains("\n"))
            p1(Lists.of(codeLines.split("\n")), caption);
        else
            p1(Lists.of(codeLines), caption);
    }

    public static void p2(Lists<String> codeLines, String caption, Object result) {
        System.out.println("- " + caption);
        System.out.println("```java");
        codeLines.forEach(System.out::println);
        System.out.println("// => " + result.toString());
        System.out.println("```");
        System.out.println();
    }

    public void p2(String codeLines, String caption, Object result) {
        if (codeLines.contains("\n"))
            p2(Lists.of(codeLines.split("\n")), caption, result);
        else
            p2(Lists.of(codeLines.trim()), caption, result);
    }

    public static void main(String[] args) {
        new Example().generateReadmeMarkdown();
    }
}
