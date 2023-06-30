package com.efficiencyScape.Server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import okhttp3.HttpUrl;

public class HttpUrlAdapter extends TypeAdapter<HttpUrl>
{
	@Override
	public void write(JsonWriter out, HttpUrl value) throws IOException
	{
		out.beginObject();
		out.name("url");
		out.value(value.url().toString());
		out.endObject();
	}

	@Override
	public HttpUrl read(JsonReader in) throws IOException
	{
		HttpUrl url = null;
		in.beginObject();
		String fieldName = null;

		while (in.hasNext())
		{
			JsonToken token = in.peek();

			if(token.equals(JsonToken.NAME))
			{
				fieldName = in.nextName();
			}

			if (fieldName != null && fieldName.equals("url"))
			{
				url = HttpUrl.parse(in.nextString());
			}
		}
		in.endObject();
		return url;
	}
}
