package json;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.sun.xml.internal.ws.util.QNameMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * Created by mkhanwalkar on 12/2/15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONTraverser {


    static String s2 = "{\n" +
            "    \"glossary\": {\n" +
            "        \"title\": \"example glossary\",\n" +
            "\t\t\"GlossDiv\": {\n" +
            "            \"title\": \"S\",\n" +
            "\t\t\t\"GlossList\": {\n" +
            "                \"GlossEntry\": {\n" +
            "                    \"ID\": \"SGML\",\n" +
            "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
            "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
            "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
            "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
            "\t\t\t\t\t\"GlossDef\": {\n" +
            "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
            "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
            "                    },\n" +
            "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";


    static String s4 = "{\n" +
            "  \"name\": \"David\",\n" +
            "  \"role\": \"Manager\",\n" +
            "  \"city\": \"Los Angeles\"\n" +
            "}";

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);




        com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(s2);

         print(rootNode.fields());



    }



    static void print(Iterator<Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> elements )
    {


        if (!elements.hasNext()) {
            //do nothing
        }
        else
        {

            elements.forEachRemaining(e -> {

                System.out.println(e.getKey() + "  " + e.getValue().textValue());

                print(e.getValue().fields());


            });


        }


    }


}
