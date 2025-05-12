import java.util.*;

public class DijkstraSimple {

    static class Edge {
        int to, weight;
        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public static int dijkstra(Map<Integer, List<Edge>> graph, int n, int start, int end) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int d = current[1];

            if (d > dist[u]) continue;

            for (Edge edge : graph.getOrDefault(u, new ArrayList<>())) {
                int v = edge.to;
                int newDist = dist[u] + edge.weight;
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{v, newDist});
                }
            }
        }

        return dist[end] == Integer.MAX_VALUE ? -1 : dist[end];
    }

    public static void main(String[] args) {
        int n = 4;
        int start = 0;
        int end = 3;

        int[][] edges = {
                {0, 1, 4},
                {0, 2, 1},
                {1, 3, 1},
                {2, 1, 2},
                {2, 3, 5}
        };

        Map<Integer, List<Edge>> graph = new HashMap<>();

        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            graph.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, w));
        }

        int result = dijkstra(graph, n, start, end);

        if (result == -1) {
            System.out.println("Сокровище недостижимо");
        } else {
            System.out.println("Длина кратчайшего пути: " + result);
        }
    }
}
