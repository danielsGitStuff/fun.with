package fun.with.dataframe;

import fun.with.Lists;

import java.util.function.Function;

public abstract class ColumnCast implements Function<Object, Object> {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public static class BooleanCast extends ColumnCast {
        private static final Function<String, Boolean> cStr2Bool = s -> {
            if (s.equals("true") || s.equals("false"))
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
            return o == null ? null : (o instanceof String ? o : o.toString());
        }
    }

    public static Lists<ColumnCast> CASTS = Lists.of(new BooleanCast(), new IntCast(), new DoubleCast(), new StringCast());
}
