package fun.with;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by xor on 30.10.2015.
 */
public enum NumberCasting {
    // IDEs might tell you to remove boxing (like: "return Long.valueOf(n.longValue())").
    // don't do that: it happened that the return values had wrong types (eg: Long instead of int)
    P_DOUBLE(double.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : (double) Double.valueOf(n.doubleValue());
        }
    },
    P_FLOAT(float.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : (float) Float.valueOf(n.floatValue());
        }
    },
    P_LONG(long.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : (long) Long.valueOf(n.longValue());
        }
    },
    P_BYTE(byte.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : (byte) Byte.valueOf(n.byteValue());
        }
    },
    P_SHORT(short.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : (short) Short.valueOf(n.shortValue());
        }
    },
    P_INT(int.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : (int) Integer.valueOf(n.intValue());
        }
    },
    BYTE(Byte.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : n.byteValue();
        }
    },
    SHORT(Short.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : n.shortValue();
        }
    },
    INT(Integer.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : n.intValue();
        }
    },
    LONG(Long.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : n.longValue();
        }
    },
    NULL(null) {
        @Override
        public Number cast(Number n) {
            return null;
        }
    },
    DOUBLE(Double.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : n.doubleValue();
        }
    },
    FLOAT(Float.class) {
        @Override
        public Number cast(Number n) {
            return n == null ? null : n.floatValue();
        }
    };

    private final static Map<Class<? extends Number>, NumberCasting> TRANSFORMER_MAP = new HashMap<>();

    static {
        for (NumberCasting tranformer : values()) {
            TRANSFORMER_MAP.put(tranformer.type, tranformer);
        }
    }

    private final Class<? extends Number> type;

    NumberCasting(Class<? extends Number> type) {
        this.type = type;
    }

    public abstract Number cast(Number n);

    public static NumberCasting forType(Class<? extends Number> type) {
        final NumberCasting t = TRANSFORMER_MAP.get(type);
        return t == null ? NULL : t;
    }

    /**
     * returns number but with type In
     * @param in
     * @param number
     * @return
     * @param <In>
     * @param <N>
     */
    public static <In extends Number,N extends Number> In castLike(In in, N number ){
        In casted = (In) NumberCasting.forType(in.getClass()).cast(number);
        return casted;
    }
}
