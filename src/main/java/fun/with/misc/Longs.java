package fun.with.misc;

import fun.with.interfaces.CollectionLike;

public class Longs {
    private Long i;
    private String format;

    public Longs(String s) {
        this.i = Long.parseLong(s);
    }

    public Longs(Long i) {
        this.i = i;
    }

    public static Long max(CollectionLike<Long, ?> longs) {
        Long max = null;
        for (Long i : longs.getCollection()) {
            if (max == null)
                max = i;
            else {
                max = max > i ? max : i;
            }
        }
        return max;
    }

    public static Longs of(String s) {
        return new Longs(s);
    }

    public static Longs of(Long i) {
        return new Longs(i);
    }

    public Longs inc() {
        this.i++;
        return this;
    }

    public Longs add(Long i) {
        this.i += i;
        return this;
    }

    public Longs subtract(Long i) {
        this.i -= i;
        return this;
    }

    public Longs multiply(Long i) {
        this.i *= i;
        return this;
    }

    public Longs divide(Long i) {
        this.i /= i;
        return this;
    }

    @Override
    public String toString() {
        if (this.format == null)
            return this.i.toString();
        return String.format(this.format, i);
    }

    public Longs format(String format) {
        this.format = format;
        return this;
    }
}
