package com.sty.ne.appperformance.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:18 PM
 */
public class JsonUtil {

    public static class PostProcessingEnabler implements TypeAdapterFactory {
        public interface PostProcessable {
            void postProcess();
        }

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

            return new TypeAdapter<T>() {
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                public T read(JsonReader in) throws IOException {
                    T obj = delegate.read(in);
                    if (obj instanceof PostProcessable) {
                        ((PostProcessable) obj).postProcess();
                    }
                    return obj;
                }
            };
        }
    }

    private static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new PostProcessingEnabler()).create();

    public static <T> List<T> fromJson(JsonArray jsonArray, TypeToken<List<T>> typeToken) {
        return gson.fromJson(jsonArray, typeToken.getType());
    }
}
