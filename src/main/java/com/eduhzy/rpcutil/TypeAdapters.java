package com.eduhzy.rpcutil;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

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


    /**
     * 对于int 和 Integer 类型
     * 由于 int 类型 有默认值 0，
     * -- 通常我们无法确定 0 是否具备实际意义
     * 但是 Integer 的类型 null , 我们可以确定的是 -- 无意义的
     * <p>
     * 因此在设计属性的类型是 通常采用 Integer  而不是 int 类型
     * <p>
     * 故 由于 int 的 0  具备 实际意义  -- 不进行转换
     * 而是转换 Integer 类型的 null 值
     */
    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            System.out.println(in.peek() + " ----->");
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (in.peek() == JsonToken.STRING) {
                in.nextString();
                return null;
            }
            return in.nextInt();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.value(EMPTY);
            } else {
                out.value(value);
            }
        }
    };

    /**
     * 对于double类型的转换
     */
    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (in.peek() == JsonToken.STRING) {
                in.nextString();
                return null;
            }
            return in.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.value(EMPTY);
            } else {
                out.value(value);
            }
        }
    };


}
