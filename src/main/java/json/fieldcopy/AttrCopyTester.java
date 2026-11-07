package json.fieldcopy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.xml.XmlMapper;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AttrCopyTester {


    static String s1 ="{\n" +
            "                    \"envelope\": {\n" +
            "                    },\n" +
            "                    \"payload\" : {\n" +
            "            \"source\": \"WEB\",\n" +
            "            \"application\": {\n" +
            "            \"type\": \"AMEX\"\n" +
            "              }\n" +
            "            }\n" +
            "            }\n";





    static String s5 = "{\n" +
            "  \"rules\": {\n" +
            "      \"Rule1\": {\n" +
            "        \"attr1\": \"payload,source\",\n" +
            "        \"attr2\": \"payload,application,type\"\n" +
            "    },\n" +
            "      \"Rule2\": {\n" +
            "        \"attr1\": \"payload,person,salary\",\n" +
            "        \"attr2\": \"payload,score\"\n" +
            "      }\n" +
            "  }\n" +
            "}";

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      //  Example test = mapper.readValue(s5,Example.class);

        Rules test = mapper.readValue(s5,Rules.class);

        Request test1 = mapper.readValue(s1,  new com.fasterxml.jackson.core.type.TypeReference<Request>(){});

        System.out.println(mapper.writeValueAsString(test));

        System.out.println(mapper.writeValueAsString(test1));

        process(test1,test,"Rule1");

        System.out.println(mapper.writeValueAsString(test1));

        XmlMapper xmlMapper = new XmlMapper();
        System.out.println(xmlMapper.writeValueAsString(test1));



        com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(s1);




    }

    static void process(Request request, Rules rules, String ruleToUse) throws Exception
    {

        Envelope envelope = request.getEnvelope();

        Map<String,String> conditions = rules.getRules().get(ruleToUse);

        System.out.println(conditions);

        conditions.forEach((source,destinations) -> {

            try {

                System.out.println(source);
                System.out.println(destinations);

                String[] destination = destinations.split(",");

                Object object = request;
                for (String s : destination)
                {
                    Field privateField = object.getClass().getDeclaredField(s);
                    privateField.setAccessible(true);
                    object = privateField.get(object);

                }
                System.out.println("fieldValue = " + object);
                {
                    Field privateField = envelope.getClass().getDeclaredField(source);
                    privateField.setAccessible(true);
                    privateField.set(envelope,object);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } );



                //Request.class.getDeclaredField("payload");


    }



}
