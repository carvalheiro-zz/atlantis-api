package br.com.srcsoftware.api.atlantis.utilidades;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import br.com.srcsoftware.api.atlantis.entity.abstracts.AbstractPO;
import br.com.srcsoftware.api.atlantis.exceptions.ModelFactoryException;
import br.com.srcsoftware.api.atlantis.utilidades.Utilidades;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

/**
 * Fábrica de conversão entre DTOs e Entities (PO) via serialização JSON contendo
 * suporte a adaptação de tipos heterogêneos (Datas e BigDecimal).
 */
@Component
@RequiredArgsConstructor
public final class ModelFactory<PO, DTO> {
	
	private final Validator validator;

    private static final Locale LOCALE_PT_BR = Locale.of("pt", "BR");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy", LOCALE_PT_BR)
            .withResolverStyle(ResolverStyle.SMART);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss", LOCALE_PT_BR)
            .withResolverStyle(ResolverStyle.SMART);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm:ss", LOCALE_PT_BR)
            .withResolverStyle(ResolverStyle.SMART);

    private static final Gson GSON_TO_PO = new GsonBuilder()
            .registerTypeAdapter(String.class, new StringAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
            .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapterToPO())
            .serializeNulls()
            .create();

    private static final Gson GSON_TO_DTO = new GsonBuilder()
            .registerTypeAdapter(String.class, new StringAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
            .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapterToDTO())
            .serializeNulls()
            .create();

    public PO getPO(Class<PO> tipoDestinoPO, DTO objetoOrigemDTO) throws ModelFactoryException {
        if (objetoOrigemDTO == null) {
            return null;
        }
        try {
            String dtoJSON = GSON_TO_PO.toJson(objetoOrigemDTO);
            return GSON_TO_PO.fromJson(dtoJSON, tipoDestinoPO);
        } catch (Exception e) {
            throw handleJsonParseException(new JsonParseException(e));
        }
    }

    public DTO getDTO(Class<DTO> tipoDestinoDTO, PO objetoOrigemPO) throws ModelFactoryException {
        if (objetoOrigemPO == null) {
            return null;
        }
        try {
            Object objetoLimpo = unproxy(objetoOrigemPO);
            String poJSON = GSON_TO_DTO.toJson(objetoLimpo);
            return GSON_TO_DTO.fromJson(poJSON, tipoDestinoDTO);
        } catch (Exception e) {
        	throw handleJsonParseException(new JsonParseException(e));
        }
    }

    public String toJSON(AbstractPO objetoOrigemPO) throws ModelFactoryException {
        if (objetoOrigemPO == null) {
            return null;
        }
        try {
            Object objetoLimpo = unproxy(objetoOrigemPO);
            return GSON_TO_DTO.toJson(objetoLimpo);
        } catch (Exception e) {
        	throw handleJsonParseException(new JsonParseException(e));
        }
    }

    public <T extends AbstractPO> T fromJSON(Class<T> tipoDestinoPO, String json) throws ModelFactoryException {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return GSON_TO_PO.fromJson(json, tipoDestinoPO);
        } catch (Exception e) {
        	throw handleJsonParseException(new JsonParseException(e));
        }
    }

    /**
     * Remove proxies do Hibernate recursivamente em toda a árvore de objetos.
     */
    @SuppressWarnings("unchecked")
    private <T> T unproxy(T entity) {
        return deepUnproxy(entity, new IdentityHashMap<>());
    }

    @SuppressWarnings("unchecked")
    private <T> T deepUnproxy(T entity, Map<Object, Object> visited) {
        if (entity == null) {
            return null;
        }

        Object unproxied = Hibernate.unproxy(entity);
        
        if (visited.containsKey(unproxied)) {
            return (T) visited.get(unproxied);
        }

        visited.put(unproxied, unproxied);
        Hibernate.initialize(unproxied);

        Class<?> clazz = unproxied.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(unproxied);
                    if (fieldValue != null) {
                        Object cleanValue = deepUnproxy(fieldValue, visited);
                        if (cleanValue != fieldValue) {
                            field.set(unproxied, cleanValue);
                        }
                    }
                } catch (Exception e) {
                    // Ignora campos restritos
                }
            }
            clazz = clazz.getSuperclass();
        }

        return (T) unproxied;
    }

    private ModelFactoryException handleJsonParseException(JsonParseException e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof java.text.ParseException || cause instanceof DateTimeParseException || cause.getMessage().contains("date")) {
                String msg = e.getMessage().replace("\"", " ").replace("Unparseable date", "Data inválida");
                return new ModelFactoryException(msg, e);
            }
            if (cause instanceof NumberFormatException) {
                String msg = e.getMessage().replace("\"", " ").replace("For input string", "Número inválido");
                return new ModelFactoryException(msg, e);
            }
        }
        return new ModelFactoryException(e.getMessage(), e);
    }

    // =========================================================================
    // Adaptadores do Gson
    // =========================================================================

    private static class StringAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            JsonToken token = reader.peek();
            if (token == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            if (token == JsonToken.BOOLEAN) {
                return String.valueOf(reader.nextBoolean());
            }
            if (token == JsonToken.NUMBER) {
                return reader.nextString();
            }
            return Utilidades.removerEspacosExcedentes(reader.nextString());
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null || value.isBlank()) {
                writer.nullValue();
                return;
            }
            writer.value(Utilidades.removerEspacosExcedentes(value));
        }
    }

    private static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate localDate, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localDate.format(DATE_FORMATTER));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonPrimitive()) {
                throw new JsonParseException("O valor da data deve ser uma String");
            }
            return LocalDate.parse(json.getAsString(), DATE_FORMATTER);
        }
    }

    private static class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonPrimitive()) {
                throw new JsonParseException("O valor da data/hora deve ser uma String");
            }
            return LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);
        }
    }

    private static class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        @Override
        public JsonElement serialize(LocalTime localTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localTime.format(TIME_FORMATTER));
        }

        @Override
        public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonPrimitive()) {
                throw new JsonParseException("O valor da hora deve ser uma String");
            }
            String horaDefinida = json.getAsString();
            if (horaDefinida.length() == 5) {
                horaDefinida = horaDefinida.concat(":00");
            }
            return LocalTime.parse(horaDefinida, TIME_FORMATTER);
        }
    }

    private static class BigDecimalTypeAdapterToPO implements JsonSerializer<String>, JsonDeserializer<BigDecimal> {
        @Override
        public JsonElement serialize(String valor, Type typeOfSrc, JsonSerializationContext context) {
            BigDecimal valorBigDecimal = Utilidades.parseBigDecimal(valor);
            return new JsonPrimitive(valorBigDecimal);
        }

        @Override
        public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonPrimitive()) {
                throw new JsonParseException("O valor numérico deve ser uma String");
            }
            return Utilidades.parseBigDecimal(json.getAsString());
        }
    }

    private static class BigDecimalTypeAdapterToDTO implements JsonSerializer<BigDecimal>, JsonDeserializer<String> {
        @Override
        public JsonElement serialize(BigDecimal valor, Type typeOfSrc, JsonSerializationContext context) {
            String valorString = Utilidades.parseBigDecimal(valor);
            return new JsonPrimitive(valorString);
        }

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonPrimitive()) {
                throw new JsonParseException("O valor do texto deve ser um JsonPrimitive");
            }
            return json.getAsString();
        }
    }
    
    public <T> void validarNotNull(T objeto) throws ModelFactoryException {
        if (objeto == null) {
            throw new ModelFactoryException("O objeto a ser validado não pode ser nulo.");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(objeto);
        if (!violations.isEmpty()) {
            String mensagemErros = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + " | " + m2)
                    .orElse("Erro de validação desconhecido.");
            
            throw new ModelFactoryException("Erro de validação: " + mensagemErros);
        }
    }
}