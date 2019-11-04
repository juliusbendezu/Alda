package t7graphs;

import java.util.*;

public class MyUndirectedGraph<T> implements UndirectedGraph<T> {


    //Datat för noden ger själva noden.
    //Den har i sin tur koll på sina kopplingar och kostnaden för dessa.
    Map<T, Node<T>> nodes = new HashMap<>();

    private static class Node<T> {

        HashMap<Node<T>, Integer> connections = new HashMap<>();

        T data;

        Node(T data) {
            this.data = data;
        }

        void addConnection(Node<T> connection, int cost) {
            connections.put(connection, cost);
        }

        void updateCost(Node<T> connection, int cost) {
            addConnection(connection, cost);
        }

        int getCostTo(Node<T> connection) {
            return connections.get(connection);
        }

        boolean isConnectedWith(Node<T> node) {
            return connections.containsKey(node);
        }

        int getNumberOfConnections() {
            return connections.size();
        }

        // Används för DFS, returnerar nästa outforskade nod i parametern
        // eller null om alla är utforskade
        Node<T> getNextNodeNotIn(Set<Node<T>> visited) {
            for (Node<T> node : connections.keySet())
                if (!visited.contains(node))
                    return node;


            return null;
        }

        Set<Node<T>> getConnections() {
            return connections.keySet();
        }
    }

    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public int getNumberOfEdges() {
        int edges = 0;
        for (Node<T> node : nodes.values())
            edges += node.getNumberOfConnections();

        return edges / 2; //Eftersom connections går åt båda hållen.
    }

    @Override
    public boolean add(T newNode) {
        if (nodes.containsKey(newNode))
            return false;
        nodes.put(newNode, new Node<>(newNode));
        return true;
    }

    @Override
    public boolean connect(T node1, T node2, int cost) {

        if (!nodes.containsKey(node1) || !nodes.containsKey(node2))
            return false;
        if (cost <= 0)
            return false;


        Node<T> firstNode = nodes.get(node1);
        Node<T> secondNode = nodes.get(node2);

        //De båda gör i princip samma sak, detta ökar endast läsbarheten
        if (isConnected(node1, node2)) {
            firstNode.updateCost(secondNode, cost);
            secondNode.updateCost(firstNode, cost);
        } else {
            firstNode.addConnection(secondNode, cost);
            secondNode.addConnection(firstNode, cost);
        }

        return true;
    }

    @Override
    public boolean isConnected(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2))
            return false;

        Node<T> firstNode = nodes.get(node1);
        Node<T> secondNode = nodes.get(node2);

        return firstNode.isConnectedWith(secondNode);
    }

    @Override
    public int getCost(T node1, T node2) {
        if (!isConnected(node1, node2))
            return -1;

        Node<T> firstNode = nodes.get(node1);
        Node<T> secondNode = nodes.get(node2);

        return firstNode.getCostTo(secondNode);
    }

    @Override
    public List<T> depthFirstSearch(T start, T end) {
        List<T> path = new ArrayList<>();
        Deque<Node<T>> stack = new ArrayDeque<>();
        Set<Node<T>> visited = new HashSet<>();

        if (start.equals(end)) {
            path.add(start);
            return path;
        }

        Node<T> currentNode = nodes.get(start);
        Node<T> endNode = nodes.get(end);

        stack.push(currentNode);
        visited.add(currentNode);

        while (!stack.isEmpty()) {
            currentNode = currentNode.getNextNodeNotIn(visited);

            if (currentNode == null) {
                stack.pop();
                currentNode = stack.peek();
            } else {
                stack.push(currentNode);
                visited.add(currentNode);

                if (currentNode.equals(endNode)) {
                    for (Node<T> node : stack)
                        path.add(node.data);

                    Collections.reverse(path);
                    return path;
                }
            }
        }

        return path;
    }

    @Override
    public List<T> breadthFirstSearch(T start, T end) {
        List<T> path = new ArrayList<>();
        Deque<Node<T>> queue = new ArrayDeque<>();
        Set<Node<T>> visited = new HashSet<>();
        Map<Node<T>, Node<T>> childParentPairs = new HashMap<>();

        if (start.equals(end)) {
            path.add(start);
            return path;
        }

        Node<T> currentNode = nodes.get(start);
        Node<T> endNode = nodes.get(end);

        queue.add(currentNode);
        visited.add(currentNode);

        while (!queue.isEmpty()) {
            currentNode = queue.poll();
            visited.add(currentNode);

            if (currentNode.equals(endNode)) {

                while (currentNode != null) {
                    path.add(currentNode.data);
                    currentNode = childParentPairs.get(currentNode); //Via föräldrarna följa vägen tillbaka till ursprunget
                }

                Collections.reverse(path);
                return path;
            }

            for (Node<T> connection : currentNode.getConnections()) {
                if (!visited.contains(connection) && !childParentPairs.containsKey(connection)) {
                    queue.add(connection);
                    childParentPairs.put(connection, currentNode); //Varje gång sökningen tar sig vidare sparas info om vart man kom ifrån
                }
            }
        }

        return path;
    }

    @Override
    public UndirectedGraph<T> minimumSpanningTree() {
        MyUndirectedGraph<T> mst = new MyUndirectedGraph<>();
        PriorityQueue<Edge<T>> heap = getConnectionsAsEdges();

        //Skapar en samling för varje nod som finns i grafen
        List<Set<Node<T>>> sets = makeSets();

        while (!heap.isEmpty()) {
            Edge<T> cheapestEdge = heap.poll();
            Node<T> firstNode = cheapestEdge.node1;
            Node<T> secondNode = cheapestEdge.node2;

            Set<Node<T>> firstSet = null;
            Set<Node<T>> secondSet = null;

            for (Set<Node<T>> set : sets) {
                if (set.contains(firstNode))
                    firstSet = set;
                if (set.contains(secondNode))
                    secondSet = set;
            }

            //Kollar så att de två setten inte är samma, detta skulle leda till cyklisk edge
            if (!firstSet.equals(secondSet)) {
                mst.add(firstNode.data);
                mst.add(secondNode.data);
                mst.connect(firstNode.data, secondNode.data, cheapestEdge.cost);

                //Merga setten och ta bort det överblivna.
                //Detta görs tills det bara finns ett set kvar som då innehåller alla noder
                //och då är vårt mst klart.
                firstSet.addAll(secondSet);
                sets.remove(secondSet);
            }
        }

        return mst;
    }


    private List<Set<Node<T>>> makeSets() {
        List<Set<Node<T>>> sets = new ArrayList<>();

        for (Node<T> node : nodes.values()) {
            Set<Node<T>> set = new HashSet<>();
            set.add(node);
            sets.add(set);
        }
        return sets;
    }

    private PriorityQueue<Edge<T>> getConnectionsAsEdges() {
        PriorityQueue<Edge<T>> edges = new PriorityQueue<>();

        for (Node<T> node : nodes.values()) {
            for (Map.Entry<Node<T>, Integer> connection : node.connections.entrySet()) {
                Edge<T> edge = new Edge<>(node, connection.getKey(), connection.getValue());
                if (!edges.contains(edge))
                    edges.add(edge);
            }
        }

        return edges;
    }

    private static class Edge<T> implements Comparable<Edge<T>> {
        Node<T> node1;
        Node<T> node2;
        int cost;

        Edge(Node<T> node1, Node<T> node2, int cost) {
            this.node1 = node1;
            this.node2 = node2;
            this.cost = cost;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Edge))
                return false;
            Edge otherEdge = (Edge) other;
            return (node1.equals(otherEdge.node1) && node2.equals(otherEdge.node2)) ||
                    (node1.equals(otherEdge.node2) && node2.equals(otherEdge.node1));
        }

        @Override
        public int compareTo(Edge<T> otherEdge) {
            return Integer.compare(cost, otherEdge.cost);
        }
    }
}
