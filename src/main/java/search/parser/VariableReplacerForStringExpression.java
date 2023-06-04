package search.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariableReplacerForStringExpression extends Replacer<String, String, String[]>{

    private boolean isContains = false;
    private final Map<Integer, Integer> cacheIndexes =  new HashMap<>();
    @Override
    public String replace(String data, String pattern, String[] values) {
        if (data.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        data = data.replaceAll("'", "\"");
        String[] expression = data.split("((?=>=|\\(|&|<>|<=|=|>|<|\\|\\|)|(?<=>=|\\(|&|<>|<=|=|>|<|\\|\\|))");
        if (!cacheIndexes.isEmpty()) {
            for (Map.Entry<Integer,Integer> cacheIndex : cacheIndexes.entrySet()) {
                expression[cacheIndex.getKey()] = values[cacheIndex.getValue()-1];
            }
            for (String element: expression) {
                result.append(element);
            }
            return result.toString();
        }
        int index = 1;
        for (String value : values) {
            int arrayIndex = 0;
            for (String exprValue: expression) {
                if (exprValue.contains(pattern + "[" + index + "]")) {
                    if (value.startsWith("\\")) {
                        cacheIndexes.put(arrayIndex, index);
                        expression[arrayIndex] = "\"" + value + "\"";
                        isContains = true;
                        break;
                    }
                    cacheIndexes.put(arrayIndex, index);
                    expression[arrayIndex] = value;
                    isContains = true;
                    break;
                }
                arrayIndex++;
            }
        index++;
        }
        if (!isContains) {
            throw new RuntimeException("Выражение не содержит шаблона для подстановки: "
                    + pattern + "[" + "table_column" + "]");
        }

        for (String element: expression) {
            result.append(element);
        }
        return result.toString();
    }
}
