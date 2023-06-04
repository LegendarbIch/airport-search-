package search.parser;

public abstract class Replacer<T,P,V> {
    public abstract T replace(T data, P pattern, V values);
}