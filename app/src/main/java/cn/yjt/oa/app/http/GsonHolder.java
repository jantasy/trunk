package cn.yjt.oa.app.http;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonHolder {
	private static GsonHolder instance = new GsonHolder();
	private Gson gson;
	private GsonHolder(){
		gson = createGson();
	}
	public static GsonHolder getInstance(){
		return instance;
	}
	public Gson getGson(){
		return gson;
	}
	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		// Register an adapter to manage the date types as long values
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			@Override
			public Date deserialize(JsonElement arg0, Type arg1,
					JsonDeserializationContext arg2) throws JsonParseException {
				return null;
			}
		});
		return builder.create();
	}

}
