package search;

import search.data.EvaluationValue;
import search.exceptions.EvaluationException;
import search.parser.ParseException;
import search.parser.VariableReplacerForStringExpression;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvValueFilter {

    private final String file;
    private String filter;
    private boolean withoutFiltering;
    private CacheService cacheService;
    private List<String> filteredValues;
    private VariableReplacerForStringExpression replacer;
    public CsvValueFilter(String file) {
        this.file = file;
    }
    public CsvValueFilter(String file,
                          VariableReplacerForStringExpression replacer,
                          CacheService cache){
        this(file);
        this.replacer = replacer;
        this.cacheService = cache;
    }
    public void setFilter(String filter) {
        if (filter.isEmpty()) {
            withoutFiltering = true;
        }
        this.filter = filter;
    }

    private boolean isColumnValueStartWith(String name, String str) {
        String[] lineValues = str.split(",", 3);
        String airportName = lineValues[1];
        return airportName.startsWith("\"" + name);
    }

    public List<String> outputFilteredDataOnColumnName(String startName) {
        if (startName.isEmpty()) {
            throw new RuntimeException("Пустой шаблон поиска");
        }
        if (cacheService.getData() != null && cacheService.getKey() != null) {
            if (cacheService.getKey() == startName.charAt(0)) {
                filteredValues = new ArrayList<>();
                for (String value: cacheService.getData()) {
                    if (isColumnValueStartWith(startName, value)) {
                        filteredValues.add(value);
                    }
                }
                if (!withoutFiltering) {
                    return getFilteredValues();
                } else {
                    return filteredValues;
                }
            }
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            cacheService.recreateCache();
            while ((line = bufferedReader.readLine()) != null) {
                if (isColumnValueStartWith(startName, line)) {
                    cacheService.setKey(startName.charAt(0));
                    cacheService.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!withoutFiltering) {
            return getFilteredValues();
        } else {
            return cacheService.getData();
        }
    }

    private List<String> getFilteredValues() {
        filteredValues = new ArrayList<>();
        for (String value: cacheService.getData()) {
            try {
                String[] values = value.split(",(?!\\s)");
                String replacedStr = replacer.replace(filter, "column", values);
                Expression expression = new Expression(replacedStr);
                EvaluationValue evaluationValue = expression.evaluate();
                if (evaluationValue.getBooleanValue()) {
                    filteredValues.add(value);
                }
            } catch (EvaluationException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return filteredValues;
    }


}

