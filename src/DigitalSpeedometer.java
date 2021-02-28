import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DigitalSpeedometer implements Iterable<Integer> {

    final double tf;
    final double tr;
    final List<Double> speeds;

    public DigitalSpeedometer(Scanner scanner) {
        this.tf = scanner.nextDouble();
        this.tr = scanner.nextDouble();
        List<Double> speeds = new ArrayList<>();
        while (scanner.hasNextDouble()) {
            speeds.add(scanner.nextDouble());
        }
        this.speeds = speeds.stream().collect(Collectors.toUnmodifiableList());
    }

    private class SpeedIterator implements Iterator<Integer> {

        int currentIndex;

        public SpeedIterator() {
            this.currentIndex = 0;
        }

        private double mostRecentSOutsideRange(double i) {
            double s = -1;
            boolean found = false;
            final Interval interval = new Interval(i + tf, i + tr, true, true);
            for (int index = this.currentIndex - 1; index >= 0 && !found; index--) {
                if (!interval.test(speeds.get(index))) {
                    found = true;
                    s = speeds.get(index);
                }
            }
            return s;
        }

        @Override
        public boolean hasNext() {
            return this.currentIndex < speeds.size();
        }

        @Override
        public Integer next() {
            final double s = speeds.get(this.currentIndex);
            final double i = Math.floor(s);
            final double j = i + 1;
            int displaySpeed = 0;
            final double iPlusTf = i + tf;
            final double iPlusTr = i + tr;
            if (new Interval(0, 1, false, false).test(s)) {
                displaySpeed = 1;
            } else if (new Interval(i, iPlusTf, true, false).test(s)) {
                displaySpeed = (int) i;
            } else if (new Interval(iPlusTr, j, false, true).test(s)) {
                displaySpeed = (int) j;
            } else if (new Interval(iPlusTf, iPlusTr, true, true).test(s)) {
                final double mostRecentS = mostRecentSOutsideRange(i);
                displaySpeed = (int) (Double.compare(mostRecentS, iPlusTr) < 0 ? i : j);
            }
            ++this.currentIndex;
            return displaySpeed;
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new SpeedIterator();
    }

    private static class Interval implements Predicate<Double> {
        final double min;
        final double max;
        final boolean lowerInclusive;
        final boolean upperInclusive;

        private Interval(double min, double max, boolean lowerInclusive, boolean upperInclusive) {
            this.min = min;
            this.max = max;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max, lowerInclusive, upperInclusive);
        }

        @Override
        public boolean test(Double aDouble) {
            return isAboveLowerBound(aDouble) && isBelowUpperBound(aDouble);

        }

        private boolean isAboveLowerBound(Double aDouble) {
            final int compareToMin = Double.compare(aDouble, min);
            return this.lowerInclusive ? compareToMin >= 0 : compareToMin > 0;
        }

        private boolean isBelowUpperBound(Double aDouble) {
            final int compareToMax = Double.compare(aDouble, max);
            return this.upperInclusive ? compareToMax <= 0 : compareToMax < 0;
        }
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final DigitalSpeedometer digitalSpeedometer = new DigitalSpeedometer(scanner);
        scanner.close();
        for (Integer speed : digitalSpeedometer) {
            System.out.println(speed);
        }
    }
}
