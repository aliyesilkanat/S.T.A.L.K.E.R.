package stalker.data.transaction.relational;

import java.util.HashMap;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aliyesilkanat.stalker.data.transaction.relational.DataSet;
import com.aliyesilkanat.stalker.data.transaction.relational.JsonArrayAdapter;

public class JsonArrayAdapterTest {
	JsonArray array;
	HashMap<String, String> map;
	@Before
	public void setUp(){
		array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.put("uname", "aliyesilkanat");
		obj.put("name", "Ali");
		obj.put("uri", "http://instagram.com/aliyesilkanat");
		array.add(obj);
		
		obj = new JsonObject();
		obj.put("uname", "isikerhan");
		obj.put("name", "Isik");
		obj.put("uri", "http://instagram.com/isikerhan");
		array.add(obj);
		
		obj = new JsonObject();
		obj.put("uname", "anils");
		obj.put("name", "Anil");
		obj.put("uri", "http://instagram.com/anils");
		array.add(obj);
		
		map = new HashMap<String, String>();
		map.put("UserName", "uname");
		map.put("RealName", "name");
		map.put("UserURI", "uri");
	}
	@Test
	public void testAdapter() throws Exception {
		DataSet dataSet = new JsonArrayAdapter(array, map, "user");
		Object result = dataSet.get(1).getAttribute("RealName");
		Assert.assertEquals("Isik", result);
	}
}
