package fun.with.misc;

import fun.with.interfaces.CollectionLike;

public class Ints {
    private Integer i;
    private String format;

    public Ints(String s) {
        this.i = Integer.parseInt(s);
    }

    public Ints(int i) {
        this.i = i;
    }

    public static Integer max(CollectionLike<Integer, ?> ints) {
        Integer max = null;
        for (Integer i : ints.getCollection()) {
            if (max == null)
                max = i;
            else {
                max = max > i ? max : i;
            }
        }
        return max;
    }

    public static Ints of(String s) {
        return new Ints(s);
    }

    public static Ints of(int i) {
        return new Ints(i);
    }

    public Ints inc() {
        this.i++;
        return this;
    }

    public Ints add(Integer i) {
        this.i += i;
        return this;
    }

    public Ints subtract(Integer i) {
        this.i -= i;
        return this;
    }

    public Ints multiply(Integer i) {
        this.i *= i;
        return this;
    }

    public Ints divide(Integer i) {
        this.i /= i;
        return this;
    }

    @Override
    public String toString() {
        if (this.format == null)
            return this.i.toString();
        return String.format(this.format, i);
    }

    public Ints format(String format) {
        this.format = format;
        return this;
    }
}
