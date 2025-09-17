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

import cl.kanopus.common.change.ChangeAction;
import cl.kanopus.common.change.Comparator;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * This class allows you to merge or mix 2 objects, consolidating its
 * information and indicating for each data type AbstractChangeTO, which kind of
 * change has been made between the compared objects.
 *
 */
@SuppressWarnings("all")
public class ChangeUtils {

    private ChangeUtils() {
    }

    public static <T> boolean hasChanges(T source, T target) {
        return (checkChange(source, target) != ChangeAction.NONE);
    }

    public static <T> List<Comparator<T>> checkChangeOnList(List<T> sourceList, List<T> targetList) {

        HashMap<Integer, Integer> mapIds = new HashMap<>();
        HashMap<Integer, T> mapSource = new HashMap<>();
        if (sourceList != null) {
            for (T v : sourceList) {
                int id = v.hashCode();
                mapIds.put(id, id);
                mapSource.put(id, v);
            }
        }

        HashMap<Integer, T> mapTarget = new HashMap<>();
        if (targetList != null) {
            for (T v : targetList) {
                int id = v.hashCode();
                if (id == 0) {
                    //Asigno ID negativo temporal
                    id = Utils.generateRandomInt() * -1;
                }
                mapIds.put(id, id);
                mapTarget.put(id, v);
            }
        }

        List<Comparator<T>> newList = new ArrayList();
        for (Integer id : mapIds.keySet()) {
            T source = mapSource.get(id);
            T target = mapTarget.get(id);
            ChangeAction action = checkChange(source, target);

            if (action == ChangeAction.CREATE || action == ChangeAction.UPDATE) {
                newList.add(new Comparator<>(action, target));
            } else {
                newList.add(new Comparator<>(action, source));
            }
        }

        return newList;
    }

    public static <T> ChangeAction checkChange(T source, T target) {

        ChangeAction action = ChangeAction.NONE;
        try {
            if (source == null && target != null) {
                action = ChangeAction.CREATE;
            } else if (source != null && target == null) {
                action = ChangeAction.DELETE;
            } else if (source != null && target != null) {

                if (BeanUtils.isSimpleProperty(source.getClass())) {
                    action = source.equals(target) ? ChangeAction.NONE : ChangeAction.UPDATE;
                } else {
                    //Field[] fields = source.getClass().getDeclaredFields();
                    int level = 1;
                    List<Field> fields = getAllFields(source.getClass(), level);

                    iterationFields:
                    for (Field f : fields) {
                        f.setAccessible(true);
                        if (BeanUtils.isSimpleProperty(f.getType())) {
                            Object sourceValue = f.get(source);
                            Object targetValue = f.get(target);

                            if (!((sourceValue == null && targetValue == null) || (sourceValue != null && sourceValue.equals(targetValue)))) {
                                action = ChangeAction.UPDATE;
                                break;
                            }
                        } else {
                            Object sourceValue = f.get(source);
                            Object targetValue = f.get(target);

                            boolean isList = ((sourceValue instanceof List) || (targetValue instanceof List));

                            if (isList) {
                                List list1 = (List) sourceValue;
                                List list2 = (List) targetValue;

                                if (list1 != null && list2 != null && list1.size() != list2.size()) {
                                    action = ChangeAction.UPDATE;
                                    break;
                                } else {

                                    HashMap<Integer, Object> mapSource = new HashMap<>();
                                    if (sourceValue != null) {
                                        for (Object v : (List) sourceValue) {
                                            mapSource.put(v.hashCode(), v.hashCode());
                                        }
                                    }

                                    if (targetValue != null) {
                                        for (Object v : (List) targetValue) {
                                            if (!mapSource.containsKey(v.hashCode())) {
                                                action = ChangeAction.UPDATE;
                                                break iterationFields;
                                            }

                                        }
                                    }

                                }
                            } else {
                                if (checkChange(sourceValue, targetValue) != ChangeAction.NONE) {
                                    action = ChangeAction.UPDATE;
                                    break;
                                }
                            }

                        }
                    }
                }

            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            action = ChangeAction.NONE;
        }
        return action;
    }

    private static List<Field> getAllFields(Class clazz, int level) {
        if (clazz == null || clazz == Object.class || clazz.getName().equals("cl.kanopus.jdbc.entity.Mapping") || level == 3) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass(), level + 1));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }
}
