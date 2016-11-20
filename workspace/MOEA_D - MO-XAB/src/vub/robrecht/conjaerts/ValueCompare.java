package vub.robrecht.conjaerts;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ValueCompare implements Comparator<Integer> {

    Map<Integer, Double> base;
    public ValueCompare(HashMap<Integer, Double> hashMap) {
        this.base = hashMap;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Integer a, Integer b) {
        if (base.get(a) <= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}