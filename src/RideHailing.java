import java.util.*;

public class RideHailing {

    private static class Graph {

        private final Map<Integer, Set<AdjacencyNode>> adjacencyList;
        private final Map<Integer, Map<Integer, Integer>> shortestPaths;

        private Graph() {
            adjacencyList = new HashMap<>();
            shortestPaths = new HashMap<>();
        }

        private static class AdjacencyNode implements Comparable<AdjacencyNode> {
            final int destination;
            final int weight;

            private AdjacencyNode(int destination, int weight) {
                this.destination = destination;
                this.weight = weight;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                AdjacencyNode that = (AdjacencyNode) o;
                return destination == that.destination;
            }

            @Override
            public int hashCode() {
                return Objects.hash(destination);
            }

            @Override
            public int compareTo(AdjacencyNode o) {
                return Integer.compare(this.destination, o.destination);
            }
        }

        public void displayGraph() {
            this.adjacencyList.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(integerSetEntry -> {
                        final int source = integerSetEntry.getKey();
                        integerSetEntry.getValue().forEach(adjacencyNode -> {
                            System.out.printf("%d -%d-> %d \n", source, adjacencyNode.weight, adjacencyNode.destination);
                        });
                    });
        }

        public void add(int node) {
            if (!this.adjacencyList.containsKey(node)) {
                this.adjacencyList.put(node, new TreeSet<>());
            }
        }

        public void addEdge(int source, int destination, int weight) {
            if (this.adjacencyList.containsKey(source)) {
                if(this.adjacencyList.get(source).add(new AdjacencyNode(destination, weight))) {
                    this.shortestPaths.clear();
                }
            }
        }

        public int shortestPathLength(int source, int destination) {
            if (!this.shortestPaths.containsKey(source) || !this.shortestPaths.get(source).containsKey(destination)) {
                this.doDijkstras(source);
            }
            return this.shortestPaths.get(source).get(destination);
        }

        private void doDijkstras(int source) {
            final TreeSet<Integer> sptSet = new TreeSet<>();
            final Map<Integer, Integer> distancesFromSource = new HashMap<>();
            for (int destination : this.adjacencyList.keySet()) {
                distancesFromSource.put(destination, destination == source ? 0 : Integer.MAX_VALUE);
            }
            while (sptSet.size() != this.adjacencyList.size()) {
                int u = getClosestNotInSptSet(sptSet, distancesFromSource);
                sptSet.add(u);
                final Set<AdjacencyNode> adjacentNodes = this.adjacencyList.get(u);
                for (AdjacencyNode adjacentNode : adjacentNodes) {
                    final int v = adjacentNode.destination;
                    int possibleDistance = distancesFromSource.get(u) != Integer.MAX_VALUE ? distancesFromSource.get(u) + adjacentNode.weight : Integer.MAX_VALUE;
                    if (possibleDistance < distancesFromSource.get(v)) {
                        distancesFromSource.put(v, possibleDistance);
                    }
                }
            }
            this.shortestPaths.put(source, distancesFromSource);
        }

        private int getClosestNotInSptSet(TreeSet<Integer> sptSet, Map<Integer, Integer> distancesFromSource) {
            int closestNotInSptSet = -1;
            int closestDistance = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Integer> distanceFromSource : distancesFromSource.entrySet()) {
                int destination = distanceFromSource.getKey();
                int distance = distanceFromSource.getValue();
                if (distance <= closestDistance && !sptSet.contains(destination)) {
                    closestNotInSptSet = destination;
                    closestDistance = distance;
                }
            }
            return closestNotInSptSet;
        }

    }

    private static class Trip {

        final int source;
        final int destination;
        final int time;

        public Trip(String tripLine) {
            final Scanner scanner = new Scanner(tripLine);
            source = scanner.nextInt();
            destination = scanner.nextInt();
            time = scanner.nextInt();
            scanner.close();
        }

        @Override
        public String toString() {
            return String.format("t = %d, %d -> %d", this.time, this.source, this.destination);
        }
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final Scanner firstLineScanner = new Scanner(scanner.nextLine());
        final int numDestinations = firstLineScanner.nextInt();
        final int numRoads = firstLineScanner.nextInt();
        final int numTrips = firstLineScanner.nextInt();
        firstLineScanner.close();

        final Graph graph = readGraph(scanner, numDestinations, numRoads);
        final List<Trip> trips = readTrips(scanner, numTrips);

        int numDrivers = 0;

        while(!trips.isEmpty()) {
            numDrivers++;
            Trip mostRecentTrip = trips.remove(trips.size() - 1);
            do {
                int currentTime = mostRecentTrip.time + graph.shortestPathLength(mostRecentTrip.source, mostRecentTrip.destination);
                int currentPosition = mostRecentTrip.destination;
                int nextPossibleTripIndex = -1;
                for (int i = trips.size() - 1; i >= 0 && nextPossibleTripIndex == -1; i--) {
                    final Trip trip = trips.get(i);
                    final int timeAtArrival = currentTime + graph.shortestPathLength(currentPosition, trip.source);
                    if (timeAtArrival == trip.time) {
                        nextPossibleTripIndex = i;
                    }
                }
                mostRecentTrip = nextPossibleTripIndex == -1 ? null : trips.remove(nextPossibleTripIndex);
            } while(mostRecentTrip != null);
        }

        System.out.println(numDrivers);
        scanner.close();
    }

    private static List<Trip> readTrips(Scanner scanner, int numTrips) {
        final List<Trip> trips = new ArrayList<>();
        for (int i = 0; i < numTrips; i++) {
            final Trip trip = new Trip(scanner.nextLine());
            trips.add(trip);
        }
        trips.sort((o1, o2) -> Integer.compare(o2.time, o1.time));
        return trips;
    }

    private static Graph readGraph(Scanner scanner, int numDestinations, int numRoads) {
        final Graph graph = new Graph();
        for (int i = 0; i < numDestinations; i++) {
            graph.add(i + 1);
        }
        for (int i = 0; i < numRoads; i++) {
            final Scanner roadLineScanner = new Scanner(scanner.nextLine());
            final int source = roadLineScanner.nextInt();
            final int destination = roadLineScanner.nextInt();
            final int weight = roadLineScanner.nextInt();
            graph.addEdge(source, destination, weight);
            roadLineScanner.close();
        }
        return graph;
    }


}
