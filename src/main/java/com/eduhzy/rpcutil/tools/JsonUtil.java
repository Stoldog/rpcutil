package com.eduhzy.rpcutil.tools;

import com.eduhzy.rpcutil.annotations.RpcField;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhongHG
 * @date 2018-12-03
 */
public class JsonUtil {

    private static final String ARRAY_START = "[";

    private static final String NEW_LINE = "\r\n";

    private static final int DEFAULT_WIDTH = 30;

    /**
     * 添加 json 字段注释
     *
     * @param fields       filed
     * @param sample       json sample
     * @param needRequired 是否需要必填注释
     * @return json with putLineComment
     * @throws IOException exception
     */
    public static String putJsonComment(Field[] fields, String sample, boolean needRequired) throws IOException {
        return putJsonComment(fields, sample, DEFAULT_WIDTH, needRequired);
    }

    /**
     * 添加 json 字段注释
     *
     * @param fields       filed
     * @param sample       json sample
     * @param width        注释与 json 的宽度
     * @param needRequired 是否需要必填注释
     * @return json with putLineComment
     * @throws IOException exception
     */
    @SuppressWarnings("all")
    public static String putJsonComment(Field[] fields, String sample, int width, boolean needRequired) throws IOException {
        List<String> lines = jsonSampleToLines(sample);

        StringBuilder sb = new StringBuilder();

        String root = "";
        for (String line : lines) {
            String trim = line.trim();

            if (Objects.equals(ARRAY_START, trim)) {
                // 数组 [ 开始
                root += ARRAY_START;
                sb.append(line).append(NEW_LINE);
            } else if (Objects.equals("{", trim)) {
                // 对象 { 开始
                root += "{";
                sb.append(line).append(NEW_LINE);
            } else if (trim.endsWith(ARRAY_START)) {
                // 子节点数组 [ 开始
                root += trim.substring(1, trim.lastIndexOf("\"") + 1);
                root += ARRAY_START;
                root = root.replace("\"", "");
                sb.append(line).append(NEW_LINE);
            } else if (trim.endsWith("{")) {
                // 子节点对象 { 开始
                root += trim.substring(1, trim.lastIndexOf("\"") + 1);
                root += "{";
                root = root.replace("\"", "");
                sb.append(line).append(NEW_LINE);
            } else if (Objects.equals("},", trim)) {
                // 子节点对象 }, 结束,该节点后还有元素(兄弟节点)
                root = removeEnd(root);
                sb.append(line).append(NEW_LINE);
            } else if (Objects.equals("],", trim)) {
                // 子节点数组 ], 结束,该节点后还有元素(兄弟节点)
                root = removeEnd(root);
                sb.append(line).append(NEW_LINE);
            } else if (Objects.equals("}", trim)) {
                // 子节点对象 } 结束
                if (root.endsWith("{")) {
                    root = removeEnd(root);
                }
                sb.append(line).append(NEW_LINE);
            } else if (Objects.equals("]", trim)) {
                // 子节点数组 ] 结束
                if (root.endsWith(ARRAY_START)) {
                    root = removeEnd(root);
                }
                sb.append(line).append(NEW_LINE);
            } else if (trim.contains(":")) {
                // 普通json节点,需要添加注释的节点
                String[] split = trim.split(":");
                String key = split[0].replace("\"", "");
                String parentsKey = findParentKey(root);

                // 该节点的所有父节点
                String[] parents = Arrays.stream(parentsKey.split("-")).filter(StringUtils::isNoneEmpty).toArray(String[]::new);
                putLineComment(parents, fields, line, key, sb, width, needRequired);
                sb.append(NEW_LINE);
            } else {
                // 普通节点,类似 true  false
                sb.append(line).append(NEW_LINE);
            }
        }

        return sb.toString();
    }

    /**
     * 添加 每一行的注释
     *
     * @param parents      parents
     * @param fields       fields
     * @param line         line
     * @param key          key
     * @param sb           stringBuilder
     * @param width        width
     * @param needRequired 是否需要必填注释
     */
    private static void putLineComment(String[] parents, Field[] fields, String line, String key, StringBuilder sb, int width, boolean needRequired) {
        if (parents.length == 0) {
            // 最顶层
            for (Field field : fields) {
                if (Objects.equals(field.getName(), key)) {
                    RpcField annotation = field.getAnnotation(RpcField.class);
                    String msg = "// ";
                    if (annotation != null) {

                        // 当参数时,添加是否时必填信息
                        if (needRequired) {
                            String required = annotation.required() ? "  必填项 " : "非必填项";
                            msg = msg + required;
                        }
                        msg = StringUtil.fillBlank(msg, 10) + annotation.description();
                    }
                    sb.append(StringUtil.fillBlank(line, width)).append(msg);
                    return;
                }
            }
        } else {
            for (String parent : parents) {
                for (Field field : fields) {
                    if (Objects.equals(parent, field.getName())) {
                        RpcField annotation = field.getAnnotation(RpcField.class);
                        if (annotation != null) {
                            Class cls = annotation.paramClass();
                            putLineComment(Arrays.copyOfRange(parents, 1, parents.length), ClassUtil.getDeclaredFields(cls), line, key, sb, width, needRequired);
                        } else {
                            Class<?> type = field.getType().getComponentType();
                            putLineComment(Arrays.copyOfRange(parents, 1, parents.length), ClassUtil.getDeclaredFields(type), line, key, sb, width, needRequired);
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * 查找所有的父节点
     *
     * @param root root
     * @return parentsKey
     */
    private static String findParentKey(String root) {
        return root.replace("{", "-")
                .replace("[", "-")
                .replace("--", "-");
    }

    /**
     * 去除 无用的结束标签及key
     *
     * @param root root
     * @return new root
     */
    private static String removeEnd(String root) {
        root = root.substring(0, root.length() - 1);
        if (StringUtils.isEmpty(root)) {
            return root;
        }
        while (true) {

            if (root.length() >= 1) {
                char c = root.charAt(root.length() - 1);
                if (c != 123 && c != 91) {
                    root = root.substring(0, root.length() - 1);
                    continue;
                }
                break;
            }
        }
        return root;
    }

    /**
     * json sample to lines
     *
     * @param sample json sample
     * @return json lines
     * @throws IOException exception
     */
    private static List<String> jsonSampleToLines(String sample) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sample.getBytes())));
        String line;
        List<String> lines = new LinkedList<>();
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
