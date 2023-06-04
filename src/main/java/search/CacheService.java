package search;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CacheService {
    @Getter
    @Setter
    private Character key;
    private List<String> values;

    public void add(String str) {
        values.add(str);
    }
    public void recreateCache() {
        values = new ArrayList<>();
    }
    public List<String> getData() {
        return values;
    }
}
