package me.kallix.fakeserver.utils.chat;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Locale;

public class ChatTypeAdapterFactory implements TypeAdapterFactory {

    public ChatTypeAdapterFactory() {
    }

    public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {

        Class<?> var3 = var2.getRawType();

        if (!var3.isEnum()) {
            return null;
        } else {

            HashMap<Object, Object> var4 = new HashMap<>();
            Object[] var5 = var3.getEnumConstants();
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Object var8 = var5[var7];
                var4.put(a(var8), var8);
            }

            return new ChatTypeAdapter(this, var4);
        }
    }

    public static String a(Object var1) {
        return var1 instanceof Enum ? ((Enum<?>)var1).name().toLowerCase(Locale.US) : var1.toString().toLowerCase(Locale.US);
    }
}
