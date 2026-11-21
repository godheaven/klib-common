/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.common.util;

import cl.kanopus.common.data.ImageBase64;
import cl.kanopus.common.data.Paginator;
import cl.kanopus.common.enums.EnumIdentifiable;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;

@SuppressWarnings("UseSpecificCatch")
public class KanopusBeanUtils {

    private static final String PROPERTY_PATH_SEPARATOR_REGEXP = "\\.";
    private static final String PROPERTY_PATH_SEPARATOR = ".";

    private static final Logger LOGGER = LoggerFactory.getLogger(KanopusBeanUtils.class);

    private static final Map<String, Map> propTranslateMaps = new ConcurrentHashMap<>();

    private KanopusBeanUtils() {
    }

    public static <T> List<T> mergeList(List<T> source, List<T> target) {
        List<T> merged = source;
        if (merged == null) {
            merged = new ArrayList<>();
        }

        if (target != null) {
            for (T t : target) {
                if (!merged.contains(t)) {
                    merged.add(t);
                }
            }
        }

        return merged;
    }

    public static <T> T copyProperties(Object source, Class<T> targetClassType) {
        try {
            T target = targetClassType.getDeclaredConstructor().newInstance();
            copyPropertiesRecursive(source, target, null, null);
            return target;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot instantiate " + targetClassType.getName() + " (a public no-args constructor is required)", ex);
        }
    }

    public static void copyProperties(Object source, Object target) {
        copyPropertiesRecursive(source, target, null, null);
    }

    public static void copyProperties(Object source, Object target, BeanProperty newValue) {
        Map<String, BeanProperty> propertiesWithNewValuesMap = new HashMap<>();
        propertiesWithNewValuesMap.put(newValue.getName(), newValue);
        copyPropertiesRecursive(source, target, null, propertiesWithNewValuesMap);
    }

    public static <T, S> Paginator<T> copyPaginator(Paginator<S> sourceList, Class<T> targetClassType) {
        Paginator<T> records = new Paginator<>();
        for (S source : sourceList.getRecords()) {
            T target = KanopusBeanUtils.copyProperties(source, targetClassType);
            records.getRecords().add(target);
        }
        records.setTotalRecords(sourceList.getTotalRecords());
        return records;
    }

    public static <T, S> List<T> copyList(List<S> sourceList, Class<T> targetClassType) {
        return copyList(sourceList, targetClassType, null, null);
    }

    public static <T, S> List<T> copyList(List<S> sourceList, Class<T> targetClassType, Map<String, String> propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        ArrayList<T> objList = new ArrayList<>();
        copyPropertiesRecursive(sourceList, objList, targetClassType, propertiesTranslationMap, propertiesWithNewValuesMap);
        return objList;
    }

    private static void copyPropertiesRecursive(Object source, Object target, Map<String, String> propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        if (target == null || source == null) {
            return; // none of the objects should be null.
        }

        if (target instanceof List && !(source instanceof List)) {
            return; // Cannot copy elements when one side is a List and the other is not (not
            // implemented yet)
        }

        // Copy StringWritter
        if (source.getClass() == StringWriter.class && target.getClass() == StringWriter.class) {
            StringWriter so = (StringWriter) source;
            StringWriter ta = (StringWriter) target;
            ta.write(so.toString());
            return;
        }

        if (source.getClass() == String.class && target.getClass() == StringWriter.class) {
            StringWriter ta = (StringWriter) target;
            ta.write((String) source);
            return;
        }

        // Copy ImageBase64
        if (source.getClass() == byte[].class && target.getClass() == ImageBase64.class) {
            ImageBase64 ta = (ImageBase64) target;
            ta.setData(Base64.getEncoder().encodeToString((byte[]) source));
            return;
        }

        // If the property name translation map is null, try to find a default one
        // previously set in the map of maps.
        if (propertiesTranslationMap == null) {
            propertiesTranslationMap = KanopusBeanUtils.getPropertiesTranslationMap(source.getClass(), target.getClass());
        }

        // This only works for the top-level elements; attributes are handled elsewhere
        // when their properties are examined.
        if (target instanceof List && source instanceof List) {
            copyPropertiesRecursive((List<?>) source, (List<?>) target, null, propertiesTranslationMap, propertiesWithNewValuesMap);
            return;
        }

        // If the source is a list and the target is an array.
        if (target.getClass().isArray() && !source.getClass().isArray()) {
            // cannot copy arbitrary objects to an array (not supported yet)
            if (source instanceof List<?> sourceList) {
                Class<?> targetElemType = target.getClass().getComponentType();
                Object outArr = copyListToArray(sourceList, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
                fillArray(outArr, target);
            }
            return;
        }

        // This only works for the top-level elements; attributes are handled elsewhere
        // when their properties are examined.
        if (target.getClass().isArray() && source.getClass().isArray()) {
            copyArray(source, target, propertiesTranslationMap, propertiesWithNewValuesMap);
            return;
        }

        BeanWrapper sourceBw = new BeanWrapperImpl(source);
        BeanWrapper targetBw = new BeanWrapperImpl(target);

        /*
         * To map complex types to simple types and vice-versa, merge the property names from the source class with the keys from the property translation map. Names are stored in a map together with the property type when found.
         */
        Map<String, BeanProperty> sourcePropertiesMap = new HashMap<>();
        // To order property names: declared properties first, followed by those from the
        // translation map.
        List<String> propNames = new ArrayList<>();
        // Obtain the property names of the source class and their types.
        PropertyDescriptor[] pds = sourceBw.getPropertyDescriptors();
        for (int j = 0; j < pds.length; j++) {
            String propName = pds[j].getName();
            if (propName.equals("class") || propNames.contains(propName)) {
                continue;
            }
            Object propValue;
            try {
                propValue = sourceBw.getPropertyValue(propName);
            } catch (Exception e) {
                LOGGER.debug("Cannot read property '{}' of {}: {}", propName, source.getClass().getName(), e.getMessage());
                continue;
            }
            BeanProperty bp = new BeanProperty(propName, pds[j].getPropertyType(), propValue);
            sourcePropertiesMap.put(pds[j].getName(), bp);
            propNames.add(pds[j].getName());
        }
        // obtain the property names and types proposed by the translation map.
        if (propertiesTranslationMap != null) {
            for (Iterator iterator = propertiesTranslationMap.keySet().iterator(); iterator.hasNext();) {
                String propName = (String) iterator.next();
                try {
                    Object value = sourceBw.getPropertyValue(propName);
                    Class propType = sourceBw.getPropertyType(propName);
                    BeanProperty bp = new BeanProperty(propName, propType, value);
                    sourcePropertiesMap.put(propName, bp);
                    if (propNames.contains(propName)) {
                        propNames.remove(propName);
                    }
                    propNames.add(propName);

                } catch (Exception e) {
                    // could not access the property, so it is not added to the map
                    LOGGER.debug("Cannot read translated property '{}' of {}: {}", propName, source.getClass().getName(), e.getMessage());
                }
            }
        }

        for (int i = 0; i < propNames.size(); i++) {
            String sourcePropName = propNames.get(i);
            BeanProperty bp = sourcePropertiesMap.get(sourcePropName);
            Class sourcePropType = bp.getType();
            Object sourcePropValue = bp.getValue();

            if (propertiesWithNewValuesMap != null && propertiesWithNewValuesMap.containsKey(sourcePropName)) {
                BeanProperty newValue = propertiesWithNewValuesMap.get(sourcePropName);
                if (sourcePropType == newValue.getType()) {
                    // Assign new value automatically
                    sourcePropValue = newValue.getValue();
                }
            }

            // If the source attribute value is null, nothing is copied.
            if (sourcePropValue == null) {
                continue;
            }

            String targetPropName = sourcePropName;
            // If a translation map for attribute names is provided, use it to
            // transform the source property name.
            if (propertiesTranslationMap != null) {
                if (propertiesTranslationMap.containsKey(sourcePropName)) {
                    targetPropName = propertiesTranslationMap.get(sourcePropName);
                } // If the target has a property with the same name as the source but the
                  // translation map links that source attribute to a different target attribute,
                  // skip copying this source property.
                else if (propertiesTranslationMap.containsValue(targetPropName)) {
                    continue;
                }
            }

            PropertyDescriptor targetDesc;
            try {
                // Split the propertyName into an array of elements using the "." separator
                String[] props = targetPropName.split(PROPERTY_PATH_SEPARATOR_REGEXP);
                String prop = "";
                if (props.length > 0) {
                    for (int j = 0; j < props.length - 1; j++) {
                        prop = prop + PROPERTY_PATH_SEPARATOR + props[j];
                        if (j == 0) {
                            prop = props[j];
                        }
                        Class type;
                        try {
                            type = targetBw.getPropertyType(prop);
                        } catch (Exception e) {
                            continue;
                        }
                        Object oldValue = null;
                        try {
                            oldValue = targetBw.getPropertyValue(prop);
                        } catch (InvalidPropertyException e) {
                            // Null value
                        }
                        if (oldValue == null) {
                            Object obj = org.springframework.beans.BeanUtils.instantiateClass(type);
                            targetBw.setPropertyValue(prop, obj);
                        }
                    }
                }

                targetDesc = targetBw.getPropertyDescriptor(targetPropName);
            } catch (InvalidPropertyException e) {
                continue;
            }

            // If the target attribute is not writable, move to the next one.
            if (targetDesc.getWriteMethod() == null) {
                continue;
            }

            Class targetPropType = targetDesc.getPropertyType();
            Object obj = sourcePropValue;
            if (!org.springframework.beans.BeanUtils.isSimpleProperty(targetPropType)) {

                try {
                    obj = org.springframework.beans.BeanUtils.instantiateClass(targetPropType);
                    // TODO: review why translationMap is not passed here
                    copyPropertiesRecursive(sourcePropValue, obj, null, propertiesWithNewValuesMap);
                } catch (Exception e1) {
                    // If target initialization fails, continue. The object may be
                    // a complex but copyable type (e.g. Date or BigInteger).
                    // If recursive copy fails, the value will be copied directly,
                    // or if it is a list it will be copied as a list.
                    obj = sourcePropValue;
                    LOGGER.trace("Cannot instantiate {} for property '{}': {}", targetPropType.getName(), targetPropName, e1.getMessage());
                }
            }

            if (java.util.List.class.isAssignableFrom(targetPropType) && java.util.List.class.isAssignableFrom(sourcePropType)) {
                // If target is a list and source is also a list...
                Class targetElemType = KanopusBeanUtils.getGenericDeclaredType(targetBw.getWrappedClass(), targetPropName);
                obj = new ArrayList<>();
                copyPropertiesRecursive((List) sourcePropValue, (List) obj, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
            } else if (java.util.List.class.isAssignableFrom(sourcePropType) && targetPropType.isArray()) {
                // If source is a list and target is an object array...
                Class targetElemType = targetPropType.getComponentType();
                obj = copyListToArray((List) sourcePropValue, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
            } else if (sourcePropType.isArray() && targetPropType.isArray() && !targetPropType.isInstance(sourcePropValue)) {
                // Array of a different type: copy element by element into a new array
                obj = Array.newInstance(targetPropType.getComponentType(), Array.getLength(sourcePropValue));
                copyArray(sourcePropValue, obj, propertiesTranslationMap, propertiesWithNewValuesMap);
            } else if (sourcePropType.isEnum() && !targetPropType.isEnum() && (sourcePropValue instanceof EnumIdentifiable)) {
                // If source implements EnumIdentifiable
                obj = ((EnumIdentifiable) sourcePropValue).getId();
            } else if (targetPropType.isEnum() && !sourcePropType.isEnum() && EnumIdentifiable.class.isAssignableFrom(targetPropType)) {
                // If target implements EnumIdentifiable
                obj = Utils.parseEnum(targetPropType, sourcePropValue);
            } else if (targetPropType.isEnum() && sourcePropType.isEnum() && !targetPropType.isInstance(sourcePropValue) && EnumIdentifiable.class.isAssignableFrom(sourcePropValue.getClass())) {
                // If the target implements EnumIdentifiable
                obj = Utils.parseEnum(targetPropType, ((EnumIdentifiable) sourcePropValue).getId());
            } else if (targetPropType == java.util.Date.class && sourcePropValue.getClass() == java.time.LocalDateTime.class) {
                obj = java.sql.Timestamp.valueOf((LocalDateTime) sourcePropValue);
            } else if (sourcePropType == java.util.Date.class && targetPropType == java.time.LocalDateTime.class) {
                obj = Instant.ofEpochMilli(((Date) sourcePropValue).getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (sourcePropValue.getClass() == ImageBase64.class && targetPropType == byte[].class) {
                ImageBase64 so = (ImageBase64) sourcePropValue;
                obj = Base64.getDecoder().decode(so.getData());
            } else if (targetPropType == java.util.Date.class
                    && (sourcePropType != java.util.Date.class && sourcePropValue.getClass() != java.util.Date.class && sourcePropValue.getClass() != java.sql.Timestamp.class)) {
                continue;
            }

            try {
                // Finally, copy the attribute value.
                targetBw.setPropertyValue(targetPropName, obj);
            } catch (Exception ex) {
                LOGGER.warn("Cannot copy property '{}' into {}: {}", targetPropName, target.getClass().getName(), ex.getMessage());
            }
        }
    }

    /**
     * Copies the elements of {@code source} array into {@code target} array (both may be primitive arrays). Simple values are assigned directly, beans are copied recursively. Only {@code min(source.length, target.length)} elements are copied.
     */
    private static void copyArray(Object source, Object target, Map<String, String> propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        Class<?> targetElemType = target.getClass().getComponentType();
        int length = Array.getLength(source);
        List<Object> sourceList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            sourceList.add(Array.get(source, i));
        }
        Object copied = copyListToArray(sourceList, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
        fillArray(copied, target);
    }

    private static void fillArray(Object source, Object target) {
        int len = Math.min(Array.getLength(source), Array.getLength(target));
        for (int i = 0; i < len; i++) {
            Object value = Array.get(source, i);
            if (value == null && target.getClass().getComponentType().isPrimitive()) {
                continue;
            }
            try {
                Array.set(target, i, value);
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Cannot copy array element {} into {}: {}", i, target.getClass().getComponentType().getName(), ex.getMessage());
            }
        }
    }

    /**
     * Copies a list into a new array of {@code targetClassType} (which may be a primitive type).
     *
     * @return the new array, never null
     */
    private static Object copyListToArray(List<?> sourceList, Class<?> targetClassType, Map<String, String> propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        Object resultArray = Array.newInstance(targetClassType, sourceList.size());
        List<Object> objList = new ArrayList<>();
        copyPropertiesRecursive((List<Object>) sourceList, objList, (Class<Object>) wrap(targetClassType), propertiesTranslationMap, propertiesWithNewValuesMap);
        fillArray(objList.toArray(), resultArray);
        return resultArray;
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        return Array.get(Array.newInstance(type, 1), 0).getClass();
    }

    private static <T, S> void copyPropertiesRecursive(List<S> source, List<T> target, Class<T> targetClass, Map propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        for (int i = 0; i < source.size(); i++) {
            S sourceElem = source.get(i);
            if (sourceElem == null) {
                target.add(null);
                continue;
            }

            // If the target class is null, assume it is the same type as the source objects.
            Class<?> tClass;
            if (targetClass == null) {
                tClass = sourceElem.getClass();
            } else {
                tClass = targetClass;
            }

            T targetElem;
            if (tClass == Object.class || tClass == String.class || org.springframework.beans.BeanUtils.isSimpleValueType(tClass)) {
                targetElem = (T) sourceElem;
            } else {
                try {
                    targetElem = (T) org.springframework.beans.BeanUtils.instantiateClass(tClass);
                    copyPropertiesRecursive(sourceElem, targetElem, propertiesTranslationMap, propertiesWithNewValuesMap);
                } catch (Exception e2) {
                    LOGGER.debug("Cannot instantiate {} while copying a list, the source element is used: {}", tClass.getName(), e2.getMessage());
                    targetElem = (T) sourceElem;
                }
            }
            target.add(targetElem);
        }
    }

    private static Class getGenericDeclaredType(Class parentClass, String propertyName) {
        if (parentClass == null || propertyName == null || "".equals(propertyName)) {
            return null;
        }
        Field field = findField(parentClass, propertyName);
        if (field == null) {
            return null;
        }
        if (field.getGenericType() instanceof ParameterizedType pt) {
            Type[] args = pt.getActualTypeArguments();
            if (args.length > 0) {
                Type t = args[0];
                if (t instanceof Class<?> c) {
                    return c;
                } else if (t instanceof ParameterizedType ptArg && ptArg.getRawType() instanceof Class<?> raw) {
                    return raw;
                }
                // wildcard or type variable: let the copy use the source element type
                return null;
            }
        }
        return Object.class;
    }

    /**
     * Finds a declared field walking up the class hierarchy.
     */
    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                // try with the superclass
            }
        }
        return null;
    }

    private static Map<String, String> getPropertiesTranslationMap(Class sourceClass, Class targetClass) {
        String key = sourceClass.getSimpleName() + "_" + targetClass.getSimpleName();
        return propTranslateMaps.get(key);
    }

    public static class BeanProperty {

        private String name;
        private Class type;
        private Object value;

        public BeanProperty() {
        }

        public BeanProperty(String name, Class type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the type
         */
        public Class getType() {
            return type;
        }

        /**
         * @param type
         *            the type to set
         */
        public void setType(Class type) {
            this.type = type;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value
         *            the value to set
         */
        public void setValue(Object value) {
            this.value = value;
        }
    }
}
