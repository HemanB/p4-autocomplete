import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("null argument/s");
        }
        initialize(terms, weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (!myMap.containsKey(prefix)) {
            return new ArrayList<>();
        }
        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> matches = myMap.get(prefix);
        int min = Math.min(k, matches.size());
        return myMap.get(prefix).subList(0, min);
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();

        for (int i = 0; i < terms.length; i++) {
            String word = terms[i];
            double weight = weights[i];
            Term term = new Term(word, weight);
            int greatestIndex = Math.min(word.length(), MAX_PREFIX);

            for (int j = 0; j < greatestIndex; j++) {
                String sub = word.substring(0, j);
                myMap.putIfAbsent(sub, new ArrayList<>());
                myMap.get(sub).add(term);
            }
        }

        for (String s : myMap.keySet()) {
            Collections.sort(myMap.get(s), (Comparator.comparing(Term::getWeight).reversed()));
        }
    }

    @Override
    public int sizeInBytes() {
        int mySize = 0;
        HashSet<Term> terms = new HashSet<>();

        for (String s : myMap.keySet()) {
            mySize += s.length() * BYTES_PER_CHAR;
            terms.addAll(myMap.get(s));
        }

        for (Term t : terms) {
            mySize += (t.getWord().length() * BYTES_PER_CHAR) + BYTES_PER_DOUBLE;
        }
        return mySize;
    }
}

