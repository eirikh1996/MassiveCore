package com.massivecraft.massivecore.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AdapterPolymorphic<T> implements JsonDeserializer<T>, JsonSerializer<T>
{
	public static final String TYPE = "type";
	public static final String VALUE = "value";
	
	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context)
	{
		if (src == null)
		{
			return JsonNull.INSTANCE;
		}
		
		JsonObject ret = new JsonObject();
		
		String type = src.getClass().getCanonicalName();
		ret.addProperty(TYPE, type);
		
		JsonElement value = context.serialize(src); 
		ret.add(VALUE, value);
		
		return ret;
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if (!json.isJsonObject())
		{
			throw new JsonParseException("A polymorph must be an object.");
		}
		
		JsonObject jsonObject = json.getAsJsonObject();
		
		if (!jsonObject.has(TYPE))
		{
			throw new JsonParseException("A polymorph must be have a \""+TYPE+"\" field.");
		}
		
		if (!jsonObject.has(VALUE))
		{
			throw new JsonParseException("A polymorph must be have a \"+VALUE+\" field.");
		}
		
		String type = jsonObject.get(TYPE).getAsString();
		
		Class<?> typeClass = null;
		try
		{
			typeClass = Class.forName(type);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new JsonParseException(e.getMessage());
		}
		return context.deserialize(jsonObject.get(VALUE), typeClass);
	}
	
}
