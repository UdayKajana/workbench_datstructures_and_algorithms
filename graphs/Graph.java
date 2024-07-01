import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import printer.Printer;

public class Graph {
    boolean printInputFormat = true;
    Scanner scanner = new Scanner(System.in);
    LinkedList<Vertex> queue = new LinkedList<>();
    ArrayList<Vertex> startingVertices = new ArrayList<>();
    HashSet<Vertex> traversedVertices = new HashSet<>();
    HashMap<Integer, Vertex> allVertices = new HashMap<>();
    String userNeedsNextIteration;
    HashSet<String> AcceptableStrings = new HashSet<>();
    boolean changePathDescription = true, changeValueDescription = true;
    String buffer;

    private void insert() {
        Vertex node;
        int sep, data;
        String input;
        int neighbourData;
        String[] neighbours;
        while (true) {
            if (printInputFormat) {
                Printer.printHeader(" INSTRUCTIONS ", "-");
                Printer.printHeading("""
                        1. use [-] for seperating vertex and its neighbours.
                        2. use [,] for seperating multiple nodes.
                        3. use [;] for stopping the input.
                        {e.g: 1-2,3,4,5   <--- Vertex [1] and it's neighbours [2,3,4,5]
                            ;             <--- stopping input
                            """);
                Printer.printHeader(" Start building graph ", "-");
                printInputFormat = false;
            }
            input = scanner.next();
            if (input.equals(";"))
                break;
            sep = input.indexOf("-");
            data = Integer.parseInt(input.substring(0, sep));
            neighbours = input.substring(sep + 1).split(",");
            if (allVertices.containsKey(data))
                node = allVertices.get(data);
            else {
                node = new Vertex(data);
                startingVertices.add(node);
                allVertices.put(data, node);
            }
            for (String s : neighbours) {
                neighbourData = Integer.parseInt(s);
                if (!allVertices.containsKey(neighbourData))
                    allVertices.put(neighbourData, new Vertex(neighbourData));
                else if (startingVertices.contains(allVertices.get(neighbourData)))
                    startingVertices.remove(allVertices.get(neighbourData));
                node.vertices.add(allVertices.get(neighbourData));
            }
        }
    }

    private void traverseHandler() {
        do {
            Printer.printMenu("TRAVERSAL", "_", Arrays.asList(
                    "1. BREADTH FIRST",
                    "2. DEPTH FIRST",
                    "3. MANUAL"));
            System.out.print("CHOOSE OPTION:");
            int option = scanner.nextInt();
            traversedVertices.clear();
            queue.clear();
            switch (option) {
                case 1 -> {
                    for (Vertex startingVertex : startingVertices) {
                        System.out.println("starting with vertex[" + startingVertex.data + "]");
                        System.out.print(startingVertex.data + ", ");
                        breadthFirstTraversal(startingVertex);
                        System.out.println();
                    }
                }
                case 2 -> {
                    for (Vertex startingVertex : startingVertices) {
                        System.out.println("starting with vertex[" + startingVertex.data + "]");
                        depthFirstTraversal(startingVertex);
                        System.out.println();
                    }
                }
                case 3 -> interactiveTraversal();
                default -> {
                    System.out.print("invalid selection\nDo you want to repeat[y/n]:");
                    if (Character.toLowerCase(scanner.next().charAt(0)) == 'y')
                        traverseHandler();
                }
            }
            System.out.println("\nDo you want to continue [Yy/*] :");
        } while (Character.toLowerCase(scanner.next().charAt(0)) == 'y');
    }

    private void interactiveTraversal() {
        boolean isUpdated = false;
        int startingVertexToStart;
        if (startingVertices.isEmpty()) {
            System.out.println("Empty Graph.....");
            return;
        }
        System.out.println("initial node(s):");
        startingVertices.forEach(element -> System.out.print(element.data + " "));
        System.out.println("Choose starting Vertex to start with :");
        startingVertexToStart = scanner.nextInt();
        Vertex vertex = null;
        for (Vertex v : startingVertices)
            if (v.data == startingVertexToStart) {
                vertex = v;
                break;
            }
        if (vertex == null) {
            System.out.println("Invalid Starting Vertex....");
            interactiveTraversal();
            return;
        }
        String data;
        int intData;
        while (true) {
            System.out.print("choose next element [");
            for (Vertex v : vertex.vertices) {
                System.out.print(v.data + ", ");
            }
            System.out.print("NaN]: ");
            data = scanner.next();
            if (data.equalsIgnoreCase("end"))
                break;
            else if (data.equalsIgnoreCase("reset")) {
                interactiveTraversal();
                return;
            } else {
                intData = Integer.parseInt(data);
                for (Vertex v : vertex.vertices) {
                    if (v.data == intData) {
                        vertex = v;
                        isUpdated = true;
                        break;
                    }
                }
            }
            if (!isUpdated) {
                System.out.println("Wrong input... choose again: ");
            }
        }
    }

    private void breadthFirstTraversal(Vertex thisVertex) {
        if (thisVertex == null)
            return;
        if (!traversedVertices.contains(thisVertex)) {
            traversedVertices.add(thisVertex);
            for (Vertex vertex : thisVertex.vertices) {
                queue.add(vertex);
                System.out.print(vertex.data + ", ");
            }
            if (queue.size() > 0)
                breadthFirstTraversal(queue.pop());
        }
    }

    private void depthFirstTraversal(Vertex thisVertex) {
        if (thisVertex == null)
            return;
        System.out.print(thisVertex.data + ", ");
        traversedVertices.add(thisVertex);
        for (Vertex v : thisVertex.vertices) {
            if (!traversedVertices.contains(v))
                depthFirstTraversal(v);
        }
    }

    private void modifyHandler() {
        boolean updateEdgeDescriprion = true;
        String[] scripts;
        int pre, suc;
        do {
            Printer.printMenu("MODIFY", "_", Arrays.asList(
                    "1. UPDATE VERTEX",
                    "3. DELETE VERTEX",
                    "2. ALTER EDGE"));
            System.out.print("CHOOSE OPTION:");
            int option = scanner.nextInt();
            traversedVertices.clear();
            queue.clear();
            switch (option) {
                case 1 -> {
                    if (changePathDescription) {
                        Printer.printHeading("""
                                scenario 1: 0-1,2/3,4   <--- Vertex 0's neighbours [1,2] are updated with [3,4]
                                scenario 2: root = 1    <--- updating root value
                                   ;                    <--- stopping input
                                   """);
                        changePathDescription = false;
                    }
                    System.out.print("Enter the update script :");
                    updateValueOfanExistingVertex(scanner.next());
                }
                case 2 -> {
                    if (updateEdgeDescriprion) {
                        Printer.printHeading("""
                                use 'x' for deleting edge.
                                use '+' for creating edge.
                                e.g : 1x2, 2+3    <--- edge between 1,2 will be delated and 2,3 will be created
                                   ;              <--- stopping input
                                   """);
                        updateEdgeDescriprion = false;
                    }
                    buffer = scanner.next().toLowerCase();
                    if (buffer.equals(";"))
                        break;
                    scripts = buffer.split(",");
                    for (String chunk : scripts) {
                        pre = Integer.parseInt(chunk.substring(0, chunk.indexOf('x')));
                        suc = Integer.parseInt(chunk.substring(chunk.indexOf('x') + 1));
                        char action = chunk.contains("x") ? 'x' : (chunk.contains("+") ? '+' : '$');
                        if (action == '$'){
                            Printer.printWarningMessage("Invalid action or data");
                        break;
                    }
                        if (allVertices.containsKey(pre) && allVertices.get(pre).vertices.contains(suc)) {
                            if (action == 'x')
                                allVertices.get(pre).vertices.remove(suc);
                            else
                                allVertices.get(pre).vertices.add(allVertices.get(suc));
                            break;
                        } else if (!allVertices.containsKey(pre))
                            Printer.printWarningMessage("No " + pre + " present int the graph");
                        else
                            Printer.printWarningMessage("No " + suc + " present int the graph");
                    }
                }
                case 3 -> deleteEdge();
                case 4 -> deleteVertex();
                default -> System.out.print("invalid selection");
            }
        } while (needIterationForThis(" modify "));
    }

    private void updateExistingEdge() {

    }

    private void updateValueOfanExistingVertex(String next) {
    }

    private void deleteEdge() {

    }

    private void deleteVertex() {

    }

    private void searchHandler() {
        boolean elementNotFoundInSubTree = true;
        queue.clear();
        Printer.printMenu("SEARCH", "_", Arrays.asList(
                "1. BREADTH FIRST",
                "2. DEPTH FIRST"));
        System.out.print("CHOOSE OPTION:");
        int option = scanner.nextInt();
        System.out.print("Enter data to search :");
        switch (option) {
            case 1 -> {
                for (int i = 0; i < startingVertices.size() && elementNotFoundInSubTree; i++)
                    elementNotFoundInSubTree = breadthFirstSearch(startingVertices.get(i), scanner.nextInt());
            }
            case 2 -> {
                for (int i = 0; i < startingVertices.size() && elementNotFoundInSubTree; i++)
                    elementNotFoundInSubTree = depthFirstSearch(startingVertices.get(i), scanner.nextInt());
            }
            default -> {
                System.out.print("invalid selection\nDo you want to repeat[y/n]:");
                char cont = scanner.next().charAt(0);
                if (cont == 'y' || cont == 'Y')
                    searchHandler();
            }
        }
    }

    private boolean depthFirstSearch(Vertex startingVertex, int searchingData) {
        Stack<Vertex> stack = new Stack<>();
        LinkedList<Vertex> path = new LinkedList<>();
        Vertex top;
        if (startingVertex != null) {
            stack.push(startingVertex);
        }
        while (stack.size() > 0) {
            top = stack.pop();
            while (top != startingVertex && !path.getLast().vertices.contains(top))
                path.removeLast();
            if (top.data == searchingData) {
                path.forEach(element -> System.out.print(element.data + ", "));
                System.out.println(top.data);
                return true;
            }
            if (top.vertices.size() > 0) {
                path.add(top);
                stack.addAll(top.vertices);
            }
        }
        System.out.println(searchingData + " is not found in the graph!");
        return false;
    }

    private boolean breadthFirstSearch(Vertex startingVertex, int searchingData) {
        Stack<Vertex> stack = new Stack<>();
        Stack<Vertex> path = new Stack<>();
        Vertex vv;
        Vertex vertex;
        if (startingVertex != null)
            queue.add(startingVertex);
        while (queue.size() > 0) {
            vertex = queue.pop();
            stack.add(vertex);
            if (vertex.data == searchingData) {
                System.out.println(searchingData + " found :)");
                path.add(vertex);
                while (stack.size() > 0) {
                    vv = stack.pop();
                    if (vv.vertices.contains(path.peek()))
                        ;
                    path.add(vv);
                }
                while (path.size() > 0) {
                    System.out.print(path.pop() + "-> ");
                }
                return true;
            }
            for (Vertex v : vertex.vertices) {
                if (!traversedVertices.contains(v)) {
                    traversedVertices.add(v);
                    queue.add(v);
                }
                if (v.data == searchingData) {
                    System.out.println(searchingData + " found :)");
                    path.add(vertex);
                    while (stack.size() > 0) {
                        vv = stack.pop();
                        if (vv.vertices.contains(path.peek()))
                            ;
                        path.add(vv);
                    }
                    while (path.size() > 0) {
                        System.out.print(path.pop() + "-> ");
                    }
                    return true;
                }
            }
        }
        Printer.printFailureMessage("No such elements found (:");
        return false;
    }

    private boolean needIterationForThis(String context) {
        System.out.print("Do you want to repeate " + context + " again [Yes/y] :");
        return userNeedsNextIteration.equals("yes") || userNeedsNextIteration.equals("y") ? true : false;
    }

    public static void main(String argvs[]) {
        Graph graph = new Graph();
        List<String> menu = Arrays.asList(
                "1. INSERT",
                "2. MODIFY",
                "3. SEARCH",
                "4. TRAVERSE",
                "5. EXIT");
        do {
            try {
                Printer.printMenu("GRAPH", "-", menu);
                System.out.print("CHOOSE OPTION:");
                int option = graph.scanner.nextInt();
                switch (option) {
                    case 1 -> graph.insert();
                    case 2 -> graph.modifyHandler();
                    case 3 -> graph.searchHandler();
                    case 4 -> graph.traverseHandler();
                    case 5 -> {
                        graph.scanner.close();
                        System.exit(0);
                    }
                    default -> Printer.printErrorMessage("---Invalid option selected---");
                }
            } catch (Exception e) {
                Printer.printErrorMessage("[ERROR]" + e.getMessage());
                e.printStackTrace();
                graph.scanner.next();
            }
        } while (true);
    }

}