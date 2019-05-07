package helper;

import org.json.JSONObject;

public class JsonBuilder {

    public JsonBuilder() {

    }

    /**
     * generate Error
     *
     * @param error
     * @return a custom json
     */

    public String generateError(String error) {

        JSONObject json = new JSONObject();

        json.put("error", error);


        return json.toString();
    }

    /**
     * generate a Payload
     *
     * @return a custom json
     */

    public String generatePayload(String typ, String message, String content) {

        JSONObject json = new JSONObject();

        json.put("typ", typ)
            .put("message", message)
            .put("content", content);

        return json.toString();
    }
}
