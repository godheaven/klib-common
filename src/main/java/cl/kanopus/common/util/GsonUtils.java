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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class GsonUtils {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private GsonUtils() {
    }

    public static final Gson custom = new GsonBuilder()
            .setDateFormat(DATETIME_FORMAT)
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private static final ThreadLocal<SimpleDateFormat> DATETIME_FORMATTER = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATETIME_FORMAT));
        private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));

        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();
            try {
                return date.length() >= 19 ? DATETIME_FORMATTER.get().parse(date) : DATE_FORMATTER.get().parse(date);
            } catch (ParseException ex) {
                throw new JsonParseException(ex);
            }
        }

        @Override
        public JsonElement serialize(Date src, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(DATETIME_FORMATTER.get().format(src));
        }
    }

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.getDecoder().decode(json.getAsString());
        }

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }
    }

    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime datetime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(datetime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // "yyyy-MM-dd HH:mm"
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public static <T> List<T> getList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return (List<T>) (json != null ? GsonUtils.custom.fromJson(json, type) : new ArrayList<T>());
    }

}
