import java.util.Scanner;

public class CurveSpeed {

    private static class Curve {
        final int R;
        final double S;

        public Curve(String s) {
            final Scanner scanner = new Scanner(s);
            this.R = scanner.nextInt();
            this.S = scanner.nextDouble();
            scanner.close();
        }

        public long maximumSpeed() {
            return Math.round(Math.sqrt((this.R * (this.S + 0.16)) / 0.067));
        }

        @Override
        public String toString() {
            return "Curve{" +
                    "R=" + R +
                    ", S=" + S +
                    '}';
        }
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            final Curve curve = new Curve(scanner.nextLine());
            System.out.println(curve.maximumSpeed());
        }
        scanner.close();
    }

}
