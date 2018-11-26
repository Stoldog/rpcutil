package com.eduhzy.rpcutil;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;

/**
 * @author zhongHG
 * @link https://blog.csdn.net/sunrainamazing/article/details/80952680
 * @date 2018-11-23
 */
public class TypeAdapters {
    public static final String EMPTY = "";


    /**
     * 对于String 类型 的 策略
     */
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        //进行反序列化
        @Override
        public String read(JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }
                //要进行属性值的判断 若为 空字符串 则返回null 否则返回 本身的值
                String result = reader.nextString();
                return result.length() > 0 ? result : null;
            } catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }

        // 进行序列化
        @Override
        public void write(JsonWriter writer, String value) {
            try {
                if (value == null) {
                    writer.value(EMPTY);
                    return;
                }
                writer.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            if (TypeAdapters.read(in)) {
                return null;
            }
            return in.nextInt();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.value(0);
            } else {
                out.value(value);
            }
        }
    };


    private static final boolean read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return true;
        }
        if (in.peek() == JsonToken.STRING) {
            in.nextString();
            return true;
        }
        return false;
    }


    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            if (TypeAdapters.read(in)) {
                return null;
            }
            return in.nextLong();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.value(0);
            } else {
                out.value(value);
            }
        }
    };


    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            if (TypeAdapters.read(in)) {
                return null;
            }
            return in.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.value(0);
            } else {
                out.value(value);
            }
        }
    };


}
