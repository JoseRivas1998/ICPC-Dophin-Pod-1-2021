import java.util.*;
import java.util.stream.Collectors;

public class RecordMatching {

    private static class Record {

        final String email;
        final String firstName;
        final String lastName;


        Record(String inputLine) {
            final Scanner scanner = new Scanner(inputLine);
            this.firstName = scanner.next();
            this.lastName = scanner.next();
            this.email = scanner.next();
            scanner.close();
        }

        boolean matches(Record record) {
            return emailMatches(record) || nameMatches(record);
        }

        private boolean nameMatches(Record record) {
            return this.firstName.equalsIgnoreCase(record.firstName) && this.lastName.equalsIgnoreCase(record.lastName);
        }

        private boolean emailMatches(Record record) {
            return this.email.equalsIgnoreCase(record.email);
        }

        @Override
        public String toString() {
            return String.format("%s %s %s", this.email, this.lastName, this.firstName);
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = this == obj;
            if (!result) {
                if (obj == null || this.getClass() != obj.getClass()) {
                    result = false;
                } else {
                    final Record other = (Record) obj;
                    return this.matches(other);
                }
            }
            return result;
        }
    }

    private static class IntPair {
        final int i;
        final int j;

        private IntPair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    public static void main(String[] args) {
        final List<Record> internalRecords = new ArrayList<>();
        final List<Record> outsideRecords = new ArrayList<>();
        scanInput(internalRecords, outsideRecords);
        removePairs(internalRecords, outsideRecords);
        printResults(internalRecords, outsideRecords);
    }

    private static void removePairs(List<Record> internalRecords, List<Record> outsideRecords) {
        final List<Record> matchedRecords = internalRecords.stream()
                .filter(outsideRecords::contains)
                .collect(Collectors.toList());
        internalRecords.removeAll(matchedRecords);
        outsideRecords.removeAll(matchedRecords);
    }

    private static List<IntPair> findPairsToRemove(List<Record> internalRecords, List<Record> outsideRecords) {
        final List<IntPair> toRemove = new ArrayList<>();
        for (int i = 0; i < internalRecords.size(); i++) {
            Record internalRecord = internalRecords.get(i);
            final int indexInOutsideRecords = outsideRecords.indexOf(internalRecord);
            if (indexInOutsideRecords != -1) {
                toRemove.add(new IntPair(i, indexInOutsideRecords));
            }
        }
        return toRemove;
    }

    private static void printResults(List<Record> internalRecords, List<Record> outsideRecords) {
        if (internalRecords.isEmpty() && outsideRecords.isEmpty()) {
            System.out.println("No mismatches.");
        } else {
            for (Record internalRecord : internalRecords) {
                System.out.println("I " + internalRecord);
            }
            for (Record outsideRecord : outsideRecords) {
                System.out.println("O " + outsideRecord);
            }
        }
    }

    private static void scanInput(List<Record> internalRecords, List<Record> outsideRecords) {
        List<Record> currentlyInserting = internalRecords;
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.isBlank()) {
                currentlyInserting = outsideRecords;
            } else {
                currentlyInserting.add(new Record(line));
            }
        }
        scanner.close();
        internalRecords.sort(Comparator.comparing(record -> record.email.toLowerCase()));
        outsideRecords.sort(Comparator.comparing(record -> record.email.toLowerCase()));
    }

}
