package me.kallix.fakeserver.utils.chat;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

class ChatTypeAdapter extends TypeAdapter {

    final Map a;
    final ChatTypeAdapterFactory b;

    ChatTypeAdapter(ChatTypeAdapterFactory chattypeadapterfactory, Map map) {
        this.b = chattypeadapterfactory;
        this.a = map;
    }

    public void write(JsonWriter jsonwriter, Object object) throws IOException {
        if (object == null) {
            jsonwriter.nullValue();
        } else {
            jsonwriter.value(ChatTypeAdapterFactory.a(object));
        }
    }

    public Object read(JsonReader jsonreader) throws IOException {
        if (jsonreader.peek() == JsonToken.NULL) {
            jsonreader.nextNull();
            return null;
        } else {
            return this.a.get(jsonreader.nextString());
        }
    }
}