package org.hypergraphdb.viewer.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hypergraphdb.type.BonesOfBeans;

public class RefUtils
{
    private static Map<String, ClassInspector> inspectors = new HashMap<String, ClassInspector>();

    synchronized static ClassInspector getInspector(Class<?> type)
    {
        if (type == null) return null;
        String name = type.getName();
        ClassInspector conv = inspectors.get(name);
        if (conv == null)
        {
            conv = new ClassInspector(type);
            inspectors.put(name, conv);
        }
        return conv;
    }
    
    public static synchronized Set<Field> getFieldsForType(Class<?> type)
    {
        return getInspector(type).getSlots();
    }
    
    private static final Field[] EMPTY = new Field[0];
    public static synchronized Field[] getFieldsForTypeArray(Class<?> type)
    {
        if(BonesOfBeans.primitiveEquivalentOf(type) != null ||
                String.class.equals(type))
            return EMPTY;
        Set<Field> set  = getFieldsForType(type);
        return set.toArray(new Field[set.size()]);
    }
    
    private static class ClassInspector 
    {
        protected Class<?> type;
        protected Set<Field> cachedSlots;
        
        public ClassInspector(Class<?> type)
        {
            this.type = type;
        }

        public Set<Field> getSlots()
        {
            if (cachedSlots != null)
                return cachedSlots;
            cachedSlots = new HashSet<Field>();
            Field[] fs = RefUtils.getAllFields(type);
            for (Field f : fs)
                if (!Modifier.isStatic(f.getModifiers())) 
                    cachedSlots.add(f);
            return cachedSlots;
        }
   }
    
    public static Method getGetMethod(final Class<?> clazz, final String name)
    {
        final String fieldName = name.substring(0, 1).toUpperCase()
                + name.substring(1);
        Method getMethod = null;
        try
        {
            getMethod = clazz.getMethod("get" + fieldName, new Class<?>[] {});
        }
        catch (final Exception e)
        {
        }
        if (getMethod == null) try
        {
            getMethod = clazz.getMethod("is" + fieldName, new Class<?>[] {});
        }
        catch (final Exception e)
        {
        }
        return getMethod;
    }

    public static Method getSetMethod(final Class<?> clazz, final String name)
    {
        final String fieldName = setterName(name);
        Method setMethod = null;
        try
        {
            Method m = getGetMethod(clazz, name);
            if (m != null)
                setMethod = clazz.getMethod(fieldName, new Class[] { m
                        .getReturnType() });
        }
        catch (final Exception e)
        {
        }
        return setMethod;
    }

    public static String setterName(String name)
    {
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static Field getField(Class<?> cls, String name)
    {
        Field f = getPublicField(cls, name);
        if(f == null)
            f = getPrivateField(cls, name);
        return f;
    }
    
    public static Field getPublicField(Class<?> cls, String name)
    {
        try
        {
            Field f = cls.getField(name);
            if (f != null && !Modifier.isStatic(f.getModifiers()) &&
                    Modifier.isPublic(f.getModifiers()))
                return f;
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public static Field getPrivateField(Class<?> cls, String name)
    {
        try
        {
            Field f = cls.getDeclaredField(name);
            if (f != null &&
                    !Modifier.isStatic(f.getModifiers())) 
                return f;
        }
        catch (Exception e)
        {
        }
        return (cls.getSuperclass() == null) ? null : getPrivateField(cls
                .getSuperclass(), name);
    }

    public static Object getPrivateFieldValue(Object instance, Class<?> cls,
            String name)
    {
        int dot = name.indexOf(".");
        if (dot > 0) return getValue(instance, cls, name);
        try
        {
            Field f = getPrivateField(cls, name);
            if (f != null)
            {
                f.setAccessible(true);
                return f.get(instance);
            }
        }
        catch (Exception e)
        {
            System.err.println("Unable to get field: " +
                    name + " in class: " + cls.getName());
        }
        return null;
    }
    
    public static void setPrivateFieldValue(Object instance, Class<?> cls,
            String name, Object value)
    {
        int dot = name.indexOf(".");
        if (dot > 0) {
            return; //getValue(instance, cls, name);
        }
        try
        {
            Field f = getPrivateField(cls, name);
            if (f != null)
            {
                f.setAccessible(true);
                f.set(instance, value);
            }
        }
        catch (Exception e)
        {
            System.err.println("Unable to set: " + value + "for " +
                    name + " in class: " + cls.getName());
        }
    }

    public static Class<?> getType(Class<?> cls, String name)
    {
        int dot = name.indexOf(".");
        if (dot > 0)
        {
            Class<?> c = getType(cls, name.substring(0, dot));
            return getType(c, name.substring(dot + 1));
        }
        Field f = getPublicField(cls, name);
        if (f != null) return f.getType();
        Method m = getGetMethod(cls, name);
        if (m != null) return m.getReturnType();
        f = getPrivateField(cls, name);
        return (f != null) ? f.getType() : null;
    }

    public static Object getValue(Object instance, Class<?> cls, String name)
    {
        int dot = name.indexOf(".");
        if (dot > 0)
        {
            Object o = getValue(instance, cls, name.substring(0, dot));
            return getValue(o, o.getClass(), name.substring(dot + 1));
        }
        try
        {
            Field f = getPublicField(cls, name);
            if (f != null)
                return f.get(instance);
            f = getPrivateField(cls, name);
            if (f != null)
            {
                f.setAccessible(true);
                return f.get(instance);
            }
            Method m = getGetMethod(cls, name);
            if (m != null) return m.invoke(instance);
        }
        catch (Exception e)
        {
            System.err.println("Unable to retrieve field: " + name + " for "
                    + instance + "in " + cls + " Reason: " + e);
            //e.printStackTrace();
        }
        return null;
    }

    private RefUtils()
    {
    }

    public static BeanInfo getBeanInfo(Class<?> type)
    {
        BeanInfo info = null;
        try
        {
            info = Introspector.getBeanInfo(type);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return info;
    }
    
    
    /**
     * Return a list of all fields (whatever access status, and on whatever
     * superclass they were defined) that can be found on this class.
     * <p>This is like a union of {@link Class#getDeclaredFields()} which
     * ignores and super-classes, and {@link Class#getFields()} which ignored
     * non-public fields
     * @param clazz The class to introspect
     * @return The complete list of fields
     */
    public static Field[] getAllFields(Class<?> clazz)
    {
        List<Class<?>> classes = getAllSuperclasses(clazz);
        classes.add(clazz);
        return getAllFields(classes);
    }
    /**
     * As {@link #getAllFields(Class)} but acts on a list of {@link Class}s and
     * uses only {@link Class#getDeclaredFields()}.
     * @param classes The list of classes to reflect on
     * @return The complete list of fields
     */
    private static Field[] getAllFields(List<Class<?>> classes)
    {
        Set<Field> fields = new HashSet<Field>();
        for (Class<?> clazz : classes)
        {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        return fields.toArray(new Field[fields.size()]);
    }
    /**
     * Return a List of super-classes for the given class.
     * @param clazz the class to look up
     * @return the List of super-classes in order going up from this one
     */
    public static List<Class<?>> getAllSuperclasses(Class<?> clazz)
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null)
        {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }

        return classes;
    }

    
}

