package fun.with.unstable.dataframe;

import fun.with.Lists;
import fun.with.Sets;

import java.util.function.Function;

public abstract class ColumnCast implements Function<Object, Object> {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract String getPrintableName(Lists<DFValue> values);

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

        @Override
        public String getPrintableName(Lists<DFValue> values) {
            return "Boolean";
        }
    }

    public static class IntCast extends ColumnCast {
        @Override
        public Object apply(Object o) {
            return o == null ? null : (o instanceof Integer ? o : Integer.parseInt(o.toString()));
        }

        @Override
        public String getPrintableName(Lists<DFValue> values) {
            return "Int";
        }
    }

    public static class LongCast extends ColumnCast {
        @Override
        public Object apply(Object o) {
            return o == null ? null : (o instanceof Long ? o : Long.parseLong(o.toString()));
        }

        @Override
        public String getPrintableName(Lists<DFValue> values) {
            return "Long";
        }
    }

    public static class DoubleCast extends ColumnCast {

        @Override
        public Object apply(Object o) {
            return o == null ? null : (o instanceof Double ? o : Double.parseDouble(o.toString()));
        }

        @Override
        public String getPrintableName(Lists<DFValue> values) {
            return "Double";
        }
    }

    public static class StringCast extends ColumnCast {

        @Override
        public Object apply(Object o) {
            if (o == null) return null;
            if (o instanceof String) return o;
            throw new RuntimeException("cannot cast to string");
        }

        @Override
        public String getPrintableName(Lists<DFValue> values) {
            return "String";
        }
    }

    public static class ObjectCast extends ColumnCast {

        @Override
        public Object apply(Object o) {
            return o;
        }

        @Override
        public String getPrintableName(Lists<DFValue> values) {
            // simple check here, no inheritance!
            if (values != null) {
                Sets<? extends Class<?>> existingClasses = values.filter(dfValue -> !dfValue.isNull()).map(dfValue -> dfValue.getObject().getClass()).sets();
                if (existingClasses.size() == 1) {
                    return "Obj(" + existingClasses.iterator().next().getSimpleName() + ")";
                }
            }
            return "Object";
        }
    }

    public static Lists<ColumnCast> CASTS = Lists.of(new BooleanCast(), new IntCast(), new LongCast(), new DoubleCast(), new StringCast(), new ObjectCast());
}
