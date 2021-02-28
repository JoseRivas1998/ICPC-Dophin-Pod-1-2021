import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogDB {

    private Map<String, Node<Fact>> facts;

    public LogDB() {
        this.facts = new HashMap<>();
    }

    public void addFacts(String inputString) {
        final Pattern pattern = Pattern.compile("([a-zA-Z0-9][a-zA-Z0-9_]*)\\s*\\((.*?)\\)");
        final Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            final String name = matcher.group(1).trim();
            final String argList = matcher.group(2).trim();
            final Node<Fact> newNode = new Node<>();
            newNode.data = new Fact(name, argList);
            newNode.next = this.facts.get(name);
            this.facts.put(name, newNode);
        }
    }

    private static class Fact {
        final String name;
        final String[] args;

        public Fact(String name, String argList) {
            this.name = name;
            this.args = argList.split(",");
            for (int i = 0; i < this.args.length; i++) {
                 this.args[i] = this.args[i].trim();
            }
        }

        boolean matches(Query q) {
            if (!this.name.equals(q.name)) return false;
            if (this.args.length != q.args.length) return false;
            final Map<String, String> variableValues = new HashMap<>();
            boolean matches = true;
            for (int i = 0; i < this.args.length && matches; i++) {
                // ignore query arguments that are just "_"
                if (!q.args[i].equals("_")) {
                    if (q.args[i].startsWith("_")) {
                        if (variableValues.containsKey(q.args[i])) {
                            matches = this.args[i].equals(variableValues.get(q.args[i]));
                        } else {
                            variableValues.put(q.args[i], this.args[i]);
                        }
                    } else {
                        matches = q.args[i].equals(this.args[i]);
                    }
                }
            }
            return matches;
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", this.name, String.join(", ", this.args));
        }
    }

    private static class Query {
        final String name;
        final String[] args;

        public Query(String name, String argList) {
            this.name = name;
            this.args = argList.split(",");
            for (int i = 0; i < this.args.length; i++) {
                this.args[i] = this.args[i].trim();
            }
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", this.name, String.join(", ", this.args));
        }
    }

    private static class Node<T> {
        Node<T> next;
        T data;

        @Override
        public String toString() {
            return this.data.toString() + (this.next != null ? " -> " + this.next.toString() : "");
        }
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final String factsInput = getFactsInputString(scanner);
        final String queriesInputString = getQueriesInputStream(scanner);
        scanner.close();

        final LogDB logDB = new LogDB();
        logDB.addFacts(factsInput);
//        logDB.facts.values().forEach(System.out::println);

        final Pattern pattern = Pattern.compile("([a-zA-Z0-9][a-zA-Z0-9_]*)\\s*\\((.*?)\\)");
        final Matcher matcher = pattern.matcher(queriesInputString);
        while (matcher.find()) {
            final String name = matcher.group(1).trim();
            final String argList = matcher.group(2).trim();
            final Query query = new Query(name, argList);
            int numMatches = 0;
            for (Node<Fact> fact = logDB.facts.get(query.name); fact != null; fact = fact.next) {
                if (fact.data.matches(query)) numMatches++;
            }
            System.out.println(numMatches);
        }

    }

    private static String getQueriesInputStream(Scanner scanner) {
        final StringBuilder queriesInputStringBuilder = new StringBuilder();
        while(scanner.hasNextLine()) {
            queriesInputStringBuilder.append(scanner.nextLine());
            queriesInputStringBuilder.append(" ");
        }
        return queriesInputStringBuilder.toString();
    }

    private static String getFactsInputString(Scanner scanner) {
        final StringBuilder factsInputBuilder = new StringBuilder();
        String currentLine;
        while (!(currentLine = scanner.nextLine()).isBlank()) {
            factsInputBuilder.append(currentLine);
            factsInputBuilder.append(" ");
        }
        return factsInputBuilder.toString();
    }

}
