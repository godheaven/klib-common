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
package cl.kanopus.common;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;

public class ThreadContext {

    private final Map<String, Map> map;
    private static final ThreadContext INSTANCE = new ThreadContext();
    private static final String KEY_THREAD_UNIQUE_ID = "threadUniqueId";
    private static final String KEY_THREAD_TIME_START = "threadTimeStart";
    private static final String KEY_THREAD_APP_KEY = "threadAppKey";
    private static final String KEY_THREAD_USER_EMAIL = "threadUserEmail";
    private static final String KEY_THREAD_USER_ID = "threadUserId";
    private static final String KEY_THREAD_USER_NAME = "threadUserName";
    private static final String KEY_THREAD_COMPANY_ID = "threadCompanyId";
    private static final String KEY_THREAD_TOKEN = "threadToken";

    private ThreadContext() {
        this.map = Collections.synchronizedMap(new HashMap<>());
    }

    public static final ThreadContext getInstance() {
        return INSTANCE;
    }

    public static String getThreadId() {
        return "" + Thread.currentThread().getId();
    }

    /**
     * Returns the unique threadId for the current thread. This value is unique
     * because it includes the thread start timestamp concatenated with the
     * thread name.
     *
     * @return a string representing the thread's unique identifier.
     */
    public static String getThreadUniqueId() {
        String threadUniqueId = (String) getObject(KEY_THREAD_UNIQUE_ID);
        if (threadUniqueId == null) {
            // In exceptional cases where the context wasn't initialized,
            // initialize it here.
            initContext();
            threadUniqueId = (String) getObject(KEY_THREAD_UNIQUE_ID);
        }
        return threadUniqueId;
    }

    public static void setAppKey(String appKey) {
        addObject(KEY_THREAD_APP_KEY, appKey);
        MDC.put(KEY_THREAD_APP_KEY, appKey);
    }

    public static String getAppKey() {
        return (String) getObject(KEY_THREAD_APP_KEY);
    }

    public static String getToken() {
        return (String) getObject(KEY_THREAD_TOKEN);
    }

    public static void setToken(String token) {
        addObject(KEY_THREAD_TOKEN, token);
    }

    public static String getUserEmail() {
        return (String) getObject(KEY_THREAD_USER_EMAIL);
    }

    public static void setUserEmail(String user) {
        addObject(KEY_THREAD_USER_EMAIL, user);
    }

    public static Long getUserId() {
        return (Long) getObject(KEY_THREAD_USER_ID);
    }

    public static void setUserId(Long userId) {
        addObject(KEY_THREAD_USER_ID, userId);
    }

    public static String getUserName() {
        return (String) getObject(KEY_THREAD_USER_NAME);
    }

    public static void setUserName(String userName) {
        addObject(KEY_THREAD_USER_NAME, userName);
    }

    public static String getCompanyId() {
        return (String) getObject(KEY_THREAD_COMPANY_ID);
    }

    public static void setCompanyId(String companyId) {
        addObject(KEY_THREAD_COMPANY_ID, companyId);
    }

    /**
     * <p>
     * Retorna el mapa de contexto para el current Thread
     *
     * @return
     */
    public static Map getContextMap() {
        //revisamos si existia el mapa viendo si al traerlo retorna null.
        //Esto es mas eficiente que preguntar primero, antes de traer efectivamente el dato.
        Map contextMap = getInstance().map.get(getThreadId());
        if (contextMap == null) {
            contextMap = Collections.synchronizedMap(new HashMap<>());
        }
        return contextMap;
    }

    /**
     * <p>
     * Agrega un objeto al mapa de contexto del current Thread dada una llave
     * especifica.
     *
     * @param objectKey
     * @param objectToInsert
     */
    public static void addObject(String objectKey, Object objectToInsert) {
        String threadId = getThreadId();
        Map contextMap = getContextMap();
        contextMap.put(objectKey, objectToInsert);
        getInstance().map.put(threadId, contextMap);
    }

    /**
     * <p>
     * Retorna el objeto almacenado en el mapa del contexto asociado al current
     * Thread y que tenga como llave <code>objectName</code>
     *
     * @param objectName nombre del objeto guardado. Usualmente sera el nombre
     * de la clase del objeto con la primera letra en minuscula.
     * @return Objeto almacenado en el ThreadContext del thread actual
     */
    public static Object getObject(String objectName) {
        Map contextMap = getContextMap();		//
        return contextMap.get(objectName);
    }

    public static long getTotalTimeMillis() {
        long totalTimeMillis = 0;
        Object timeStart = getObject(KEY_THREAD_TIME_START);
        if (timeStart != null) {
            Date timeEnd = new Date();
            totalTimeMillis = timeEnd.getTime() - ((Date) timeStart).getTime();
        }
        return totalTimeMillis;
    }

    public static void initContext() {
        String threadUniqueId = (new Date()).getTime() + "_" + Thread.currentThread().getName();
        addObject(KEY_THREAD_UNIQUE_ID, threadUniqueId);
        addObject(KEY_THREAD_TIME_START, new Date());
        MDC.put(KEY_THREAD_UNIQUE_ID, threadUniqueId);
    }

    /**
     * <p>
     * Remueve el mapa con el contexto del thread actual.
     */
    public static void destroyContext() {
        if (getInstance().map.containsKey(getThreadId())) {
            getInstance().map.remove(getThreadId());
            MDC.remove(KEY_THREAD_UNIQUE_ID);
            MDC.remove(KEY_THREAD_APP_KEY);
        }
    }

}
