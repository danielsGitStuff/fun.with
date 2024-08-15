package fun.with.unstable.dataframe;

import fun.with.Lists;

import java.util.function.Function;

public abstract class ColumnCast implements Function<Object, Object> {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public static class BooleanCast extends ColumnCast {
        private static final Function<String, Boolean> cStr2Bool = s -> {
            if ("true".equals(s) || "false".equals(s) || s.isEmpty())
                return Boolean.parseBoolean(s);
            throw new RuntimeException("cannot cast to boolean");
        };

        @Override
        public Object apply(Object o) {
            return o == null ? null : (o instanceof Boolean ? o : cStr2Bool.apply(o.toString().toLowerCase()));
        }
    }

    public static class IntCast extends ColumnCast {
        @Override
        public Object apply(Object o) {
            return o == null ? null : (o instanceof Integer ? o : Integer.parseInt(o.toString()));
        }
    }

    public static class DoubleCast extends ColumnCast {

        @Override
        public Object apply(Object o) {
            return o == null ? null : (o instanceof Double ? o : Double.parseDouble(o.toString()));
        }
    }

    public static class StringCast extends ColumnCast {

        @Override
        public Object apply(Object o) {
            if (o == null) return null;
            if (o instanceof String) return o;
            throw new RuntimeException("cannot cast to string");
        }
    }

    public static class ObjectCast extends ColumnCast {

        @Override
        public Object apply(Object o) {
            return o;
        }
    }

    public static Lists<ColumnCast> CASTS = Lists.of(new BooleanCast(), new IntCast(), new DoubleCast(), new StringCast(), new ObjectCast());
}
