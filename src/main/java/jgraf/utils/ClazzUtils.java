package jgraf.utils;

import jgraf.neo4j.factory.AuxNodeLabels;
import org.neo4j.graphdb.Node;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClazzUtils {
    private static final Set<Class<?>> PRINTABLE_CLASSES = Set.of(
            boolean.class, Boolean.class,
            char.class, Character.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            ZonedDateTime.class,
            Date.class,
            String.class
            // TODO Enum?
    );

    public static boolean isPrintable(Class<?> cl) {
        return PRINTABLE_CLASSES.contains(cl);
    }

    @SuppressWarnings("unchecked")
    public static Object toPrintableObject(Class cl, String value) {
        if (cl.equals(boolean.class) || cl.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        if (cl.equals(char.class) || cl.equals(Character.class)) {
            return Character.valueOf(value.charAt(0));
        }
        if (cl.equals(byte.class) || cl.equals(Byte.class)) {
            return Byte.parseByte(value);
        }
        if (cl.equals(short.class) || cl.equals(Short.class)) {
            return Short.parseShort(value);
        }
        if (cl.equals(int.class) || cl.equals(Integer.class)) {
            return Integer.parseInt(value);
        }
        if (cl.equals(long.class) || cl.equals(Long.class)) {
            return Long.parseLong(value);
        }
        if (cl.equals(float.class) || cl.equals(Float.class)) {
            return Float.parseFloat(value);
        }
        if (cl.equals(double.class) || cl.equals(Double.class)) {
            return Double.parseDouble(value);
        }
        if (cl.equals(String.class)) {
            return value;
        }
        if (cl.equals(ZonedDateTime.class)) {
            return ZonedDateTime.parse(value);
        }
        if (cl.equals(Date.class)) {
            try {
                return DateFormat.getDateInstance().parse(value);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (cl.equals(Enum.class)) {
            return Enum.valueOf(cl, value);
        }
        if (cl.equals(Object.class)) {
            return toPrintableObject(value);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object toPrintableObject(String value) {
        if (value == null || value.isBlank()) return null;
        if (value.trim().toLowerCase().matches("true|false")) {
            return Boolean.parseBoolean(value);
        }
        if (value.length() == 1) {
            return Character.valueOf(value.charAt(0));
        }
        if (value.matches("[0-9]+")) {
            Object object = null;
            try {
                object = Byte.parseByte(value);
            } catch (NumberFormatException eB) {
                try {
                    object = Short.parseShort(value);
                } catch (NumberFormatException eS) {
                    try {
                        object = Integer.parseInt(value);
                    } catch (NumberFormatException eI) {
                        try {
                            object = Long.parseLong(value);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            if (object != null) return object;
        }
        if (value.matches("[0-9]+[.,][0-9]+")) {
            Object object = null;
            try {
                object = Float.parseFloat(value);
            } catch (NumberFormatException eF) {
                try {
                    object = Double.parseDouble(value);
                } catch (NumberFormatException ignored) {
                }
            }
            if (object != null) return object;
        }

        Object object = null;
        try {
            object = ZonedDateTime.parse(value);
        } catch (DateTimeParseException eZD) {
            try {
                object = DateFormat.getDateInstance().parse(value);
            } catch (ParseException ignored) {
            }
        }

        // TODO Enum?

        return value;
    }

    public static Object toPrintableObjectSimplified(String value) {
        if (value == null || value.isBlank()) return null;
        if (value.matches("[0-9]+")) {
            return Integer.valueOf(value);
        }
        if (value.matches("[0-9]+[.,][0-9]+")) {
            return Double.valueOf(value);
        }

        Object object = null;
        try {
            object = ZonedDateTime.parse(value);
        } catch (DateTimeParseException eZD) {
            try {
                object = DateFormat.getDateInstance().parse(value);
            } catch (ParseException ignored) {
            }
        }

        return value;
    }

    public static Object castPrintableObject(Class<?> targetType, String value) {
        if (targetType == null || value == null) return null;
        if (targetType.equals(byte.class) || targetType.equals(Byte.class))
            return Byte.valueOf(value);
        if (targetType.equals(short.class) || targetType.equals(Short.class))
            return Short.valueOf(value);
        if (targetType.equals(int.class) || targetType.equals(Integer.class))
            return Integer.valueOf(value);
        if (targetType.equals(long.class) || targetType.equals(Long.class))
            return Long.valueOf(value);
        if (targetType.equals(float.class) || targetType.equals(Float.class))
            return Float.valueOf(value);
        if (targetType.equals(double.class) || targetType.equals(Double.class))
            return Double.valueOf(value);
        if (targetType.equals(ZonedDateTime.class))
            return ZonedDateTime.parse(value);
        if (targetType.equals(DateFormat.class)) {
            try {
                return DateFormat.getDateInstance().parse(value);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return targetType.cast(value);
    }

    public static boolean isSubclass(Class<?> cl, Collection<Class<?>> classes) {
        return classes.stream().anyMatch(clazz -> clazz.isAssignableFrom(cl));
    }

    public static String getSimpleClassName(Node node) {
        return StreamSupport.stream(node.getLabels().spliterator(), false)
                .filter(label -> !AuxNodeLabels.isIn(label))
                .map(label -> {
                    try {
                        return Class.forName(label.name()).getSimpleName();
                    } catch (ClassNotFoundException e) {
                        // Not a class name
                        return label.name();
                    }
                }).collect(Collectors.joining(", "));
    }

    public static boolean isSubclass(Class<?> subclass, Class<?> superclass, Class<?> genericType) {
        Type genericSuperclass = subclass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?> typeArgument) {
                return parameterizedType.getRawType().equals(superclass) && typeArgument.equals(genericType);
            }
        }
        return false;
    }

    public static boolean isInstanceOf(Node node, Class<?> superclass) {
        return StreamSupport.stream(node.getLabels().spliterator(), false)
                .anyMatch(label -> {
                    try {
                        return superclass.isAssignableFrom(Class.forName(label.name()));
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                });
    }
}
