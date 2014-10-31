package me.pauzen.jlib.reflection;

import me.pauzen.jlib.unsafe.UnsafeProvider;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class Reflect {

    private static Unsafe unsafe = UnsafeProvider.getUnsafe();
    private static Map<Class, Set<Field>> HIERARCHIC_CACHED_CLASS_FIELDS = new HashMap<>();
    private static Map<Class, Set<Field>> CACHED_CLASS_FIELDS            = new HashMap<>();
    private Reflect() {
    }

    public static void removeFinal(Field field) {
        try {
            Field modifier = Field.class.getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.set(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Class[] toClassArray(Object[] objects) {
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++)
            classes[i] = objects[i].getClass();
        return classes;
    }

    /**
     * Finds the field in the class hierarchy.
     *
     * @param clazz The class where to start the search.
     * @param name  The name of the field to find.
     * @return Either the found field, or a null value if one is not found.
     */
    public static Field getField(Class clazz, String name) {
        Class currentClass = clazz;
        for (; currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            for (Field field : getFieldsHierarchic(clazz))
                if (field.getName().equals(name)) return field;
        }
        return null;
    }

    /**
     * Returns all fields in the class and all its super classes.
     *
     * @param clazz The class to return all found fields from.
     * @return A Set of all found fields in the class.
     */
    public static Set<Field> getFieldsHierarchic(Class clazz) {
        if (HIERARCHIC_CACHED_CLASS_FIELDS.containsKey(clazz)) return HIERARCHIC_CACHED_CLASS_FIELDS.get(clazz);
        Set<Field> fields = new HashSet<>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Set<Field> fields1 = getFields(clazz);
            Collections.addAll(fields, fields1.toArray(new Field[fields1.size()]));
        }
        HIERARCHIC_CACHED_CLASS_FIELDS.put(clazz, fields);
        return fields;
    }

    public static Set<Field> getFields(Class clazz) {
        if (CACHED_CLASS_FIELDS.containsKey(clazz)) return CACHED_CLASS_FIELDS.get(clazz);
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        CACHED_CLASS_FIELDS.put(clazz, fields);
        return fields;
    }

    /**
     * Returns all static fields within a class and all its super classes.
     *
     * @param clazz The class to search for static fields.
     * @return A Set of the static fields in the class.
     */
    public static Set<Field> getStaticFieldsHierarchic(Class clazz) {
        Set<Field> fields = new HashSet<>();
        for (Field field : getFieldsHierarchic(clazz))
            if (Modifier.isStatic(field.getModifiers())) fields.add(field);
        return fields;
    }

    public static Set<Field> getStaticFields(Class clazz) {
        Set<Field> fields = new HashSet<>();
        for (Field field : getFields(clazz))
            if (Modifier.isStatic(field.getModifiers())) fields.add(field);
        return fields;
    }

    /**
     * Gets the shallow size of the Class.
     *
     * @param clazz Class to get the shallow size of.
     * @return The size of the Object.
     */
    public static long getShallowSize(Class clazz) {
        Set<Field> fields = new HashSet<>();
        for (Field field : Reflect.getFieldsHierarchic(clazz))
            if (!Modifier.isStatic(field.getModifiers())) fields.add(field);

        long size = 0;
        for (Field field : fields) {
            long offset = unsafe.objectFieldOffset(field);
            size = Math.max(size, offset);
        }

        return ((size >> 2) + 1) << 2; // ADDS PADDING
    }

    public static long getShallowSize(Object object) {
        return getShallowSize(object.getClass());
    }

}