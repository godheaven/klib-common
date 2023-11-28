package cl.kanopus.common.util;

import cl.kanopus.common.change.Change;
import cl.kanopus.common.change.ChangeAction;
import cl.kanopus.common.change.IgnoreMerge;
import cl.kanopus.common.util.KanopusBeanUtils.BeanProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 *
 * This class allows you to merge or mix 2 objects, consolidating its
 * information and indicating for each data type AbstractChangeTO, which kind of
 * change has been made between the compared objects.
 *
 */
@SuppressWarnings("all")
public class ChangeUtils {

    private ChangeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T extends Change> T mergeChanges(T source, T target) {
        T merge = null;

        if (source == null && target == null) {
            return null;
        }

        if (source == null && target != null) {
            merge = (T) BeanUtils.instantiateClass(target.getClass());
            //copy all information and setting parent and all child as CREATE
            KanopusBeanUtils.copyProperties(target, merge, getChangeProperty(ChangeAction.CREATE));
        } else if (source != null && target == null) {
            merge = (T) BeanUtils.instantiateClass(source.getClass());
            //copy all information and setting parent and all child as DELETE
            KanopusBeanUtils.copyProperties(source, merge, getChangeProperty(ChangeAction.DELETE));
        } else if (source != null && target != null) {
            merge = (T) BeanUtils.instantiateClass(target.getClass());
            BeanUtils.copyProperties(target, merge); //Spring copy properties is not recursive
            try {
                boolean hasSameSimpleProperties = hasSameSimpleProperties(source, merge);
                merge.setAction(hasSameSimpleProperties ? ChangeAction.NONE : ChangeAction.UPDATE);

                Field[] fields = source.getClass().getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    boolean isSimpleType = BeanUtils.isSimpleProperty(f.getType());

                    boolean hasAnnotationIgnore = f.getAnnotation(IgnoreMerge.class) != null;
                    if (hasAnnotationIgnore) {
                        continue;
                    }

                    if (!isSimpleType) {
                        Object sourceValue = f.get(source);
                        Object targetValue = f.get(target);
                        boolean inherentAbstractChange = ((sourceValue instanceof Change) || (targetValue instanceof Change));
                        boolean isList = ((sourceValue instanceof List) || (targetValue instanceof List));

                        if (isList) {
                            HashMap<Long, Long> mapIds = new HashMap<>();
                            HashMap<Long, Change> mapSource = new HashMap<>();
                            if (sourceValue != null) {
                                for (Object v : (List) sourceValue) {
                                    if (v instanceof Change) {
                                        Long id = ((Change) v).getId();
                                        mapIds.put(id, id);
                                        mapSource.put(id, (Change) v);
                                    }
                                }
                            }

                            HashMap<Long, Change> mapTarget = new HashMap<>();
                            if (targetValue != null) {
                                for (Object v : (List) targetValue) {
                                    if (v instanceof Change) {
                                        Long id = ((Change) v).getId();
                                        if (id == null || id == 0) {
                                            //Asigno ID negativo temporal
                                            id = Utils.generateRandomLong() * -1;
                                        }
                                        mapIds.put(id, id);
                                        mapTarget.put(id, (Change) v);
                                    }
                                }
                            }

                            List newList = new ArrayList();
                            for (Iterator it = mapIds.keySet().iterator(); it.hasNext();) {
                                Long id = (Long) it.next();
                                newList.add(mergeChanges(mapSource.get(id), mapTarget.get(id)));
                            }
                            if (!newList.isEmpty()) {
                                f.set(merge, newList);
                            }
                        }

                        if (inherentAbstractChange) {
                            Object mergeValue = f.get(merge);
                            f.set(merge, mergeChanges((Change) sourceValue, (Change) mergeValue));
                        }

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error trying merge objects: " + e.getMessage(), e);
            }

        }

        return merge;
    }

    private static BeanProperty getChangeProperty(ChangeAction action) {
        BeanProperty changeProperty = new BeanProperty();
        changeProperty.setName("action");
        changeProperty.setType(ChangeAction.class);
        changeProperty.setValue(action);
        return changeProperty;
    }

    private static <T extends Change> boolean hasSameSimpleProperties(T source, T target) throws IllegalArgumentException, IllegalAccessException {
        boolean isEquals = true;
        if (source != null && target != null && Objects.equals(source.getId(), target.getId())) {
            Field[] fields = source.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                if (BeanUtils.isSimpleProperty(f.getType())) {
                    Object sourceValue = f.get(source);
                    Object targetValue = f.get(target);

                    if (!((sourceValue == null && targetValue == null) || (sourceValue != null && sourceValue.equals(targetValue)))) {
                        isEquals = false;
                        break;
                    }
                }
            }
        } else {
            isEquals = false;
        }
        return isEquals;
    }

    public static boolean hasChanges(List<? extends Change> items) {
        boolean hasChanges = false;
        if (items != null) {
            for (Change c : items) {
                if (c.getAction() != null && c.getAction() != ChangeAction.NONE) {
                    hasChanges = true;
                    break;
                }
            }
        }
        return hasChanges;
    }

    public static boolean hasChangesRecursive(List<? extends Change> items) throws IllegalArgumentException, IllegalAccessException {
        boolean hasChanges = false;
        boolean elementsHaveLists = false;
        if (items != null && !items.isEmpty()) {
            elementsHaveLists = itemHasListField(items.get(0));
            for (Change c : items) {

                if (!elementsHaveLists) {
                    hasChanges = evalChange(c);

                    if (hasChanges) {
                        return true;
                    }

                } else {

                    Field[] fields = c.getClass().getDeclaredFields();

                    for (Field f : fields) {

                        f.setAccessible(true);
                        boolean isSimpleType = BeanUtils.isSimpleProperty(f.getType());

                        if (!isSimpleType) {
                            Object sourceValue = f.get(c);
                            boolean isList = ((sourceValue instanceof List));

                            if (isList) {

                                hasChanges = hasChangesRecursive((List<? extends Change>) sourceValue);

                                if (hasChanges) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return hasChanges;
    }

    private static boolean evalChange(Change c) {
        return (c.getAction() != null && (c.getAction() == ChangeAction.CREATE || c.getAction() == ChangeAction.DELETE));
    }

    private static boolean itemHasListField(Change c) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = c.getClass().getDeclaredFields();

        for (Field f : fields) {
            f.setAccessible(true);
            boolean isSimpleType = BeanUtils.isSimpleProperty(f.getType());

            if (!isSimpleType) {

                Object sourceValue = f.get(c);
                boolean isList = ((sourceValue instanceof List));

                if (isList) {
                    return true;
                }
            }
        }
        return false;
    }
}
