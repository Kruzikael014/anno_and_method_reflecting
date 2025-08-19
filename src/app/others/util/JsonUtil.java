package app.others.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.stream.Collectors;

public class JsonUtil {

	public static String toJson(Object obj) {
		if (obj == null)
			return "null";

		if (obj instanceof String) {
			return "\"" + escape((String) obj) + "\"";
		}

		if (obj instanceof Number || obj instanceof Boolean) {
			return obj.toString();
		}

		if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).stream().map(JsonUtil::toJson).collect(Collectors.joining(",", "[", "]"));
		}

		// generic object -> serialize fields
		StringBuilder sb = new StringBuilder("{");
		Field[] fields = obj.getClass().getDeclaredFields();
		boolean first = true;
		for (Field f : fields) {
			f.setAccessible(true);
			try {
				Object value = f.get(obj);
				if (!first)
					sb.append(",");
				sb.append("\"").append(f.getName()).append("\":").append(toJson(value));
				first = false;
			} catch (Exception ignored) {
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private static String escape(String s) {
		return s.replace("\"", "\\\"");
	}
}
