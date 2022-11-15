package org.totodev.engine.util;

public class SerializationUtils {
    public static String serialize(float... values) {
        String[] serializedNumbers = new String[values.length];
        for (int i = 0; i < values.length; i++)
            serializedNumbers[i] = Float.toString(values[i]);

        return String.join("|", serializedNumbers);
    }

    public static float[] deserialize(String serializedValues) {
        return deserialize(serializedValues.split("\\|"));
    }

    public static float[] deserialize(String... serializedValues) {
        float[] numbers = new float[serializedValues.length];
        for (int i = 0; i < serializedValues.length; i++)
            numbers[i] = Float.parseFloat(serializedValues[i]);
        return numbers;
    }
}
