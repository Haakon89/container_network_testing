package network.simulation.test.UtilityClasses;

import com.google.gson.*;
import com.google.gson.internal.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        if (typeFieldName == null || baseType == null) throw new NullPointerException();
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType) {
        return new RuntimeTypeAdapterFactory<>(baseType, "type");
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        if (type == null || label == null) throw new NullPointerException();
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw new IllegalArgumentException("Duplicate type or label");
        }
        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    @Override
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (!baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }

        final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();
        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                if (delegate == null) {
                    throw new JsonParseException("Cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                }
                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                jsonObject.addProperty(typeFieldName, label);
                Streams.write(jsonObject, out);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement jsonElement = Streams.parse(in);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement labelJsonElement = jsonObject.remove(typeFieldName);
                if (labelJsonElement == null) {
                    throw new JsonParseException("Cannot deserialize; missing type field: " + typeFieldName);
                }
                String label = labelJsonElement.getAsString();
                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                if (delegate == null) {
                    throw new JsonParseException("Cannot deserialize subtype with label: " + label);
                }
                return delegate.fromJsonTree(jsonObject);
            }
        };
    }
}
