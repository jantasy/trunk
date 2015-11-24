package cn.yjt.oa.app.cardpack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HttpRequest {
    public static String append(final CharSequence url, final Map<?, ?> params) {
        final String baseUrl = url.toString();
        if (params == null || params.isEmpty())
            return baseUrl;

        final StringBuilder result = new StringBuilder(baseUrl);

        addPathSeparator(baseUrl, result);
        addParamPrefix(baseUrl, result);

        Map.Entry<?, ?> entry;
        Iterator<?> iterator = params.entrySet().iterator();
        entry = (Map.Entry<?, ?>) iterator.next();
        addParam(entry.getKey().toString(), entry.getValue(), result);

        while (iterator.hasNext()) {
            result.append('&');
            entry = (Map.Entry<?, ?>) iterator.next();
            addParam(entry.getKey().toString(), entry.getValue(), result);
        }

        return result.toString();
    }

    private static StringBuilder addPathSeparator(final String baseUrl,
                                                  final StringBuilder result) {
        // Add trailing slash if the base URL doesn't have any path segments.
        //
        // The following test is checking for the last slash not being part of
        // the protocol to host separator: '://'.
        if (baseUrl.indexOf(':') + 2 == baseUrl.lastIndexOf('/'))
            result.append('/');
        return result;
    }

    private static StringBuilder addParamPrefix(final String baseUrl,
                                                final StringBuilder result) {
        // Add '?' if missing and add '&' if params already exist in base url
        final int queryStart = baseUrl.indexOf('?');
        final int lastChar = result.length() - 1;
        if (queryStart == -1)
            result.append('?');
        else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&')
            result.append('&');
        return result;
    }
    private static StringBuilder addParam(final Object key, Object value,
                                          final StringBuilder result) {
        if (value != null && value.getClass().isArray())
            value = arrayToList(value);

        if (value instanceof Iterable<?>) {
            Iterator<?> iterator = ((Iterable<?>) value).iterator();
            while (iterator.hasNext()) {
                result.append(key);
                result.append("[]=");
                Object element = iterator.next();
                if (element != null)
                    result.append(element);
                if (iterator.hasNext())
                    result.append("&");
            }
        } else {
            result.append(key);
            result.append("=");
            if (value != null)
                result.append(value);
        }

        return result;
    }

    private static List<Object> arrayToList(final Object array) {
        if (array instanceof Object[])
            return Arrays.asList((Object[]) array);

        List<Object> result = new ArrayList<Object>();
        // Arrays of the primitive types can't be cast to array of Object, so this:
        if (array instanceof int[])
            for (int value : (int[]) array) result.add(value);
        else if (array instanceof boolean[])
            for (boolean value : (boolean[]) array) result.add(value);
        else if (array instanceof long[])
            for (long value : (long[]) array) result.add(value);
        else if (array instanceof float[])
            for (float value : (float[]) array) result.add(value);
        else if (array instanceof double[])
            for (double value : (double[]) array) result.add(value);
        else if (array instanceof short[])
            for (short value : (short[]) array) result.add(value);
        else if (array instanceof byte[])
            for (byte value : (byte[]) array) result.add(value);
        else if (array instanceof char[])
            for (char value : (char[]) array) result.add(value);
        return result;
    }

}
