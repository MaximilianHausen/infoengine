package net.totodev.infoengine.util;

public class SerializationUtils {
    public static String serialize(float... values) {
        String[] serializedNumbers = new String[values.length];
        for (int i = 0; i < values.length; i++)
            serializedNumbers[i] = Float.toString(values[i]);

        return String.join("|", serializedNumbers);
    }

    public static float[] deserialize(String serializedValues) {
        String[] serializedNumbers = serializedValues.split("\\|");
        float[] numbers = new float[serializedNumbers.length];
        for (int i = 0; i < serializedNumbers.length; i++)
            numbers[i] = Float.parseFloat(serializedNumbers[i]);
        return numbers;
    }
}
