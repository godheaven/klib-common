package cl.kanopus.common.util;

import cl.kanopus.common.data.ImageBase64;
import cl.kanopus.common.enums.EnumIdentifiable;
import cl.kanopus.common.data.Paginator;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
@SuppressWarnings("UseSpecificCatch")
public class KanopusBeanUtils {

    private static final String PROPERTY_PATH_SEPARATOR_REGEXP = "\\.";
    private static final String PROPERTY_PATH_SEPARATOR = ".";

    private static Map<String, Map> propTranslateMaps = new HashMap<>();

    private KanopusBeanUtils() {
    }

    public static <T> List<T> mergeList(List<T> source, List<T> target) {
        List<T> merged = source;
        if (merged == null) {
             merged = new ArrayList<>();
        }
        
        if (target != null) {
            for (T t: target) {
                if (!merged.contains(t)){
                    merged.add(t);
                }
            }
        }

        return merged;
    }

    public static <T> T copyProperties(Object source, Class<T> targetClassType) {
        try {
            T target = targetClassType.newInstance();
            copyPropertiesRecursive(source, target, null, null);
            return target;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
            return;//ninguno de los objetos debe ser null.
        }

        if (target instanceof List && !(source instanceof List)) {
            return;//no se pueden copiar los elementos si uno es lista y el otro no. (aun no implementado)
        }

        //Copy StringWritter
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

        //Copy ImageBase64
        if (source.getClass() == byte[].class && target.getClass() == ImageBase64.class) {
            ImageBase64 ta = (ImageBase64) target;
            ta.setData(Base64.getEncoder().encodeToString((byte[]) source));
            return;
        }

        //si el mapa de transformacion de nombres de properties es nulo entonces buscamos si
        //tiene un default seteado anteriormente en el mapa de mapas.
        if (propertiesTranslationMap == null) {
            propertiesTranslationMap = KanopusBeanUtils.getPropertiesTranslationMap(source.getClass(), target.getClass());
        }

        //Esto solo funciona para los primeros elementos que entran, no para los atributos,
        //pues cuando se examinan los atributos se ejecuta otra parte del codigo.
        if (target instanceof List && source instanceof List) {
            copyPropertiesRecursive((List<?>) source, (List<?>) target, null, propertiesTranslationMap, propertiesWithNewValuesMap);
            return;
        }

        //si el source es una lista y el target es un arreglo.
        if (target.getClass().isArray() && !source.getClass().isArray()) {
            //no se puede copiar elementos de un objeto cualquiera a un array (aun no soportado)
            if (source instanceof List) {
                Class targetElemType = target.getClass().getComponentType();
                Object[] outArr = copyListToArray((List) source, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
                int len = outArr.length > ((Object[]) target).length ? ((Object[]) target).length : outArr.length;
                for (int i = 0; i < len; i++) {
                    ((Object[]) target)[i] = outArr[i];
                }
            }
            return;
        }

        //Esto solo funciona para los primeros elementos que entran, no para los atributos,
        //pues cuando se examinan los atributos se ejecuta otra parte del codigo.
        if (target.getClass().isArray() && source.getClass().isArray()) {
            Class tac = target.getClass().getComponentType();

            Object[] sourceArr = (Object[]) source;
            Object[] targetArr = (Object[]) target;

            //como el Arrays.asList() no copia bien los elementos, lo hacemos a mano
            List sourceList = new ArrayList();
            for (int i = 0; i < sourceArr.length; i++) {
                sourceList.add(sourceArr[i]);
            }
            List targetList = new ArrayList();
            for (int i = 0; i < targetArr.length; i++) {
                if (targetArr[i] != null) {
                    targetList.add(targetArr[i]);
                }
            }

            //utilizamos el copiado de lista
            copyPropertiesRecursive(sourceList, targetList, tac, propertiesTranslationMap, propertiesWithNewValuesMap);

            //como la lista de origen puede ser mayor que la lista de destino y por lo tanto el resultado
            //de la copia puede ser que haya aumentado el tamaÃ±o del arreglo destino, creamos uno nuevo
            //para copiar los elementos.
            //Se respeta el tamaÃ±o del arreglo original para poder mantener el arreglo fuera de este metodo.
            for (int i = 0; i < ((Object[]) target).length; i++) {
                ((Object[]) target)[i] = targetList.get(i);
            }
            return;
        }

        BeanWrapper sourceBw = new BeanWrapperImpl(source);
        BeanWrapper targetBw = new BeanWrapperImpl(target);

        /*
         * Para poder mapear tipos complejos a tipos simples y vice-versa
         * se fusionaran los nombres de los properties de la clase source
         * y los nombres de los properties llaves del mapa de traduccion
         * de properties.
         * Los nombres se guardaran en un mapa junto con el tipo del property
         * si es que se encuentra dicho property.
         */
        Map<String, BeanProperty> sourcePropertiesMap = new HashMap<>();
        //para poder ordenar los nombres de los properties declarados primero y despues
        //los del mapa.
        List<String> propNames = new ArrayList<>();
        //obtenemos los nombres de los properties de la clase source y sus tipos.
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
                continue;
            }
            BeanProperty bp = new BeanProperty(propName, pds[j].getPropertyType(), propValue);
            sourcePropertiesMap.put(pds[j].getName(), bp);
            propNames.add(pds[j].getName());

        }
        //obtenemos los nombres y los tipos de los properties propuestos desde
        //el mapa de transformacion.
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
                    //no se pudo accesar al property por lo que no se agrega al mapa
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
                    //Se asigna nuevo valor automaticamente
                    sourcePropValue = newValue.getValue();
                }
            }

            //si el valor del atributo de origen es null, entonces no se copia nada.
            if (sourcePropValue == null) {
                continue;
            }

            String targetPropName = sourcePropName;
            //si viene un mapa de traduccion de nombres de atributos (propertiesTranslationMap)
            //entonces lo uso para transformar el nombre del property.
            if (propertiesTranslationMap != null) {
                if (propertiesTranslationMap.containsKey(sourcePropName)) {
                    targetPropName = propertiesTranslationMap.get(sourcePropName);
                } //si existe un atributo en el target que sea igual al nombre del atributo del
                //source, pero en el mapa existe una relacion con otro sourceAttribute, entonces
                //si el sourceAtt del mapa es diferente del sourceAtt actualmente revisado, se
                //salta esta copia de valores.
                else if (propertiesTranslationMap.containsValue(targetPropName)) {
                    continue;
                }
            }

            PropertyDescriptor targetDesc;
            try {
                //separa el propertyName en un arreglo de elementos suponiendo el separador "."
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
                            //Null value
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

            //si no puedo escribir en el atributo del target paso al siguiente.
            if (targetDesc.getWriteMethod() == null) {
                continue;
            }

            Class targetPropType = targetDesc.getPropertyType();
            Object obj = sourcePropValue;
            if (!org.springframework.beans.BeanUtils.isSimpleProperty(targetPropType)) {

                try {
                    obj = org.springframework.beans.BeanUtils.instantiateClass(targetPropType);
                    copyPropertiesRecursive(sourcePropValue, obj, null, propertiesWithNewValuesMap); //@TODO revisar porque no esta translationMap
                } catch (Exception e1) {
                    //Si no puedo inicializar el target entonces sigo adelante, porque es un
                    //objeto complejo pero que se puede copiar nomas, como un Date o BigInteger
                    //Si no puedo copiarlo recursivamente despues se copiara simplemente o si
                    //es lista entrara a copiarse como tal.
                }

            }

            if (java.util.List.class.isAssignableFrom(targetPropType) && java.util.List.class.isAssignableFrom(sourcePropType)) {
                //si el target es una lista y el source tambien...
                Class targetElemType = KanopusBeanUtils.getGenericDeclaredType(targetBw.getWrappedClass(), targetPropName);
                obj = new ArrayList();
                copyPropertiesRecursive((List) sourcePropValue, (List) obj, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
            } else if (java.util.List.class.isAssignableFrom(sourcePropType) && targetPropType.isArray()) {
                //si el source es una lista y el target es un array de algun objeto...
                Class targetElemType = targetPropType.getComponentType();
                obj = copyListToArray((List) sourcePropValue, targetElemType, propertiesTranslationMap, propertiesWithNewValuesMap);
            } else if (sourcePropType.isEnum() && !targetPropType.isEnum() && (sourcePropValue instanceof EnumIdentifiable)) {
                //si el source es implementa EnumIdentifiable
                obj = ((EnumIdentifiable) sourcePropValue).getId();
            } else if (targetPropType.isEnum() && !sourcePropType.isEnum() && EnumIdentifiable.class.isAssignableFrom(targetPropType)) {
                //si el target implementa EnumIdentifiable
                obj = Utils.parseEnum(targetPropType, sourcePropValue);
            } else if (targetPropType.isEnum() && sourcePropType.isEnum() && sourcePropValue.getClass() != targetPropType.getClass() && EnumIdentifiable.class.isAssignableFrom(sourcePropValue.getClass())) {
                //si el target implementa EnumIdentifiable
                obj = Utils.parseEnum(targetPropType, ((EnumIdentifiable) sourcePropValue).getId());
            } else if (targetPropType == java.util.Date.class && sourcePropValue.getClass() == java.time.LocalDateTime.class) {
                obj = java.sql.Timestamp.valueOf((LocalDateTime) sourcePropValue);
            } else if (sourcePropType == java.util.Date.class && targetPropType == java.time.LocalDateTime.class) {
                obj = Instant.ofEpochMilli(((Date) sourcePropValue).getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (sourcePropValue.getClass() == ImageBase64.class && targetPropType == byte[].class) {
                ImageBase64 so = (ImageBase64) sourcePropValue;
                obj = Base64.getDecoder().decode(so.getData());
            } else if (targetPropType == java.util.Date.class && (sourcePropType != java.util.Date.class && sourcePropValue.getClass() != java.util.Date.class && sourcePropValue.getClass() != java.sql.Timestamp.class)) {
                continue;
            }

            try {
                //TODO aqui se puede completar para transformar tipos si es necesario.
                //finalmente se copia el valor del atributo.
                targetBw.setPropertyValue(targetPropName, obj);
            } catch (Exception ex) {
                //Null value
            }
        }
    }

    private static <T, S> T[] copyListToArray(List<S> sourceList, Class<T> targetClassType, Map<String, String> propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        Object obj;
        try {
            obj = Array.newInstance(targetClassType, sourceList.size());
        } catch (Exception e) {
            return null;//no se pudo crear el arreglo.
        }
        ArrayList<T> objList = new ArrayList<>();
        copyPropertiesRecursive(sourceList, objList, targetClassType, propertiesTranslationMap, propertiesWithNewValuesMap);

        //como no tengo el tipo del arreglo apriori, no puedo hacer toArray();
        //asi es que recorremos la lista llenando el arreglo.
        T[] resultArray = (T[]) obj;
        for (int j = 0; j < objList.size(); j++) {
            resultArray[j] = objList.get(j);
        }
        return resultArray;
    }

    private static <T, S> void copyPropertiesRecursive(List<S> source, List<T> target, Class<T> targetClass, Map propertiesTranslationMap, Map<String, BeanProperty> propertiesWithNewValuesMap) {
        for (int i = 0; i < source.size(); i++) {
            S sourceElem = source.get(i);

            //si la clase de destino es null, se asume que es del mismo tipo que la clase de los
            //objetos de origen.
            Class<?> tClass;
            if (targetClass == null) {
                tClass = sourceElem.getClass();
            } else {
                tClass = targetClass;
            }

            T targetElem;
            if (tClass == Object.class || tClass == String.class) {
                targetElem = (T) sourceElem;
            } else {
                try {
                    targetElem = (T) org.springframework.beans.BeanUtils.instantiateClass(tClass);
                    copyPropertiesRecursive(sourceElem, targetElem, propertiesTranslationMap, propertiesWithNewValuesMap);
                } catch (Exception e2) {
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
        Field field;
        try {
            field = parentClass.getDeclaredField(propertyName);

            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                for (Type t : pt.getActualTypeArguments()) {
                    return (Class) t;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return Object.class;
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
         * @param name the name to set
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
         * @param type the type to set
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
         * @param value the value to set
         */
        public void setValue(Object value) {
            this.value = value;
        }
    }

}
