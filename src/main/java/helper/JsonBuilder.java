package helper;

import org.json.JSONObject;

public class JsonBuilder {

    public JsonBuilder() {}


    /**
     * generate a Response
     *
     * @return a custom json
     */

    public String generateResponse(String typ, String message, String content) {

        JSONObject json = new JSONObject();

        json.put("typ", typ)
            .put("message", message)
            .put("content", content);

        System.out.println(json.toString());

        return json.toString();
    }
}
