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
        String input, token;
        int neighbourData;
        String[] neighbours;
        Vertex neighbourVertex = null;
        boolean isCreatedVertex = false;
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
            token = input.substring(0, sep);
            if (!token.matches("\\d+")) {
                Printer.printWarningMessage(" Token /'" + token + "' is invalid!... [SKIPPING]");
                continue;
            }
            data = Integer.parseInt(token);
            neighbours = input.substring(sep + 1).split(",");
            if (allVertices.containsKey(data))
                node = allVertices.get(data);
            else {
                node = new Vertex(data);
                System.out.println("'" + data + " is now created!");
                startingVertices.add(node);
                allVertices.put(data, node);
            }
            for (String s : neighbours) {
                if (!s.matches("\\d+")) {
                    Printer.printWarningMessage(" Token /'" + token + "' is invalid!... [SKIPPING]");
                    continue;
                }
                neighbourData = Integer.parseInt(s);
                isCreatedVertex = allVertices.containsKey(neighbourData);
                if (isCreatedVertex) {
                    neighbourVertex = allVertices.get(neighbourData);
                    if (node != neighbourVertex)
                        startingVertices.remove(neighbourVertex);
                } else {
                    neighbourVertex = new Vertex(neighbourData);
                    allVertices.put(neighbourData, neighbourVertex);
                }
                if (!node.vertices.contains(neighbourVertex)) {
                    node.vertices.add(allVertices.get(neighbourData));
                    System.out.println("'" + neighbourData + " is now " + (isCreatedVertex ? "created as " : "")
                            + "neighbour of '" + data + "'");
                } else
                    Printer.printWarningMessage("'" + neighbourData + " already a neighbour of '" + data + "'");
            }
        }
    }

    private void traverseHandler() {
        do {
            Printer.printMenu("TRAVERSAL", "_", Arrays.asList(
                    "1. BREADTH FIRST",
                    "2. DEPTH FIRST",
                    "3. INTERACTIVE"));
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
                default -> System.out.println("invalid selection");
            }
        } while (getUserAgreement("Traverse the graph again?:"));
    }

    private void interactiveTraversal() {
        traversedVertices.clear();
        queue.clear();
        boolean isUpdated = false;
        int startingVertexToStart;
        if (startingVertices.isEmpty()) {
            Printer.printWarningMessage("empty graph.....");
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
        Printer.printMenu("MODIFY", "_", Arrays.asList(
                "1. UPDATE VERTEX",
                "2. DELETE VERTEX",
                "3. ALTER EDGE"));
        System.out.print("CHOOSE OPTION:");
        int option = scanner.nextInt();
        traversedVertices.clear();
        queue.clear();
        switch (option) {
            case 1 -> {
                if (changePathDescription) {
                    Printer.printHeading("""
                            Unlike INSERTION, MODIFICATION can't create new Vertices, it can re-structure the GRAPH.
                              >>-:-:-> 1/3,2/4   <--- Vertices [1,2] are updated with [3,4]
                               ;                 <--- stopping input
                               """);
                    changePathDescription = false;
                }
                System.out.print("Enter the update script :");
                List<String> linesOfInput = new ArrayList<>();
                String input = scanner.next();
                while (!input.equals(";")) {
                    linesOfInput.add(input);
                    input = scanner.next();
                }
                for (String line : linesOfInput)
                    updateVertexValue(line);
            }
            case 2 -> {
                if (updateEdgeDescriprion) {
                    Printer.printHeading("""
                            use 'x' for deleting edge.
                            use '+' for creating edge.
                            [e.g : 1x2, 2+3    <--- edge between 1,2 will be delated and between 2,3 will be created]
                               ;              <--- stopping input
                               """);
                    updateEdgeDescriprion = false;
                }
                List<String> linesOfInput = new ArrayList<>();
                String input = scanner.next().toLowerCase();
                while (!input.equals(";")) {
                    linesOfInput.add(input);
                    input = scanner.next();
                }
                for (String line : linesOfInput)
                    alterEdge(line);
            }
            case 3 -> {
                Printer.printHeading("""
                            use ',' for seperating multiple vertices
                            ; <--- stopping input
                            [e.g : 1,2,3    <--- vertices 1, 2, 3 will be delated]
                        """);
                List<String> linesOfInput = new ArrayList<>();
                String input = scanner.next().toLowerCase();
                while (!input.equals(";")) {
                    linesOfInput.add(input);
                    input = scanner.next();
                }
                for (String line : linesOfInput)
                    deleteVertex(line);
            }
            default -> System.out.print("invalid selection");
        }
    }

    private void updateVertexValue(String input) {
        String[] neighbours = input.split(",");
        int sep, present, newData;
        for (String prompt : neighbours) {
            sep = prompt.indexOf("/");
            present = prompt.substring(0, sep).matches("\\d+")
                    ? Integer.parseInt(prompt.substring(0, sep))
                    : Integer.MIN_VALUE;
            newData = prompt.substring(sep + 1).matches("\\d+")
                    ? Integer.parseInt(prompt.substring(sep + 1))
                    : Integer.MIN_VALUE;
            if ((present == Integer.MIN_VALUE || newData == Integer.MIN_VALUE)) {
                Printer.printWarningMessage("'" + prompt + "' is invalid!");
                return;
            }
            if (!allVertices.containsKey(present)) {
                Printer.printWarningMessage("'" + present + "' is not in the Graph!");
                return;
            }
            allVertices.get(present).data = newData;
        }
    }

    private void alterEdge(String script) {
        int pre, suc;
        String[] prompts = script.split(",");
        for (String chunk : prompts) {
            char action = chunk.contains("x") ? 'x' : (chunk.contains("+") ? '+' : '$');
            if (action == '$') {
                Printer.printWarningMessage("Invalid action or data");
                continue;
            }
            pre = chunk.substring(0, chunk.indexOf(action)).matches("\\d+")
                    ? Integer.parseInt(chunk.substring(0, chunk.indexOf(action)))
                    : Integer.MIN_VALUE;
            suc = chunk.substring(chunk.indexOf(action) + 1).matches("\\d+")
                    ? Integer.parseInt(chunk.substring(chunk.indexOf(action) + 1))
                    : Integer.MIN_VALUE;
            if ((pre == Integer.MIN_VALUE || suc == Integer.MIN_VALUE)) {
                Printer.printWarningMessage("'" + chunk + "' is invalid!");
                return;
            }
            if (allVertices.containsKey(pre) && allVertices.containsKey(suc)) {
                if (action == 'x') {
                    if (allVertices.get(pre).vertices.contains(allVertices.get(suc))) {
                        if (isSingleNeighbourVertex(allVertices.get(suc))) {
                            startingVertices.add(allVertices.get(suc));
                            Printer.printSuccessMessage("'" + suc + "' is became a starting vertex.");
                        }
                        allVertices.get(pre).vertices.remove(allVertices.get(suc));
                    } else
                        Printer.printWarningMessage("'" + suc + "' is not a neighbour of '" + pre + "'");
                } else
                    allVertices.get(pre).vertices.add(allVertices.get(suc));
            } else {
                Printer.printWarningMessage(
                        "'" + (allVertices.containsKey(pre) ? suc : pre) + "' ntot present in the graph");
            }

        }
    }

    private void deleteVertex(String input) {
        ArrayList<Vertex> parents = new ArrayList<>();
        int data;
        String[] prompts = input.split(",");
        for (String prompt : prompts) {
            if (prompt.matches("\\d+")) {
                data = Integer.parseInt(prompt);
                parents = getParentsOf(data);
                if (parents.size() > 0) {
                    for (Vertex parent : parents) {
                        for (Vertex neighbour : allVertices.get(data).vertices) {
                            if (isSingleNeighbourVertex(neighbour))
                                startingVertices.add(neighbour);
                        }
                        parent.vertices.remove(allVertices.get(data));
                    }
                    allVertices.remove(data);
                }
            } else
                Printer.printWarningMessage("'" + prompt + "' is a invalid prompt!");
        }
    }

    private boolean isSingleNeighbourVertex(Vertex neighbour) {
        int count = 0;
        for (int data : allVertices.keySet()) {
            allVertices.get(data).vertices.contains(neighbour);
            count++;
            if (count > 1)
                return false;
        }
        return false;
    }

    private ArrayList<Vertex> getParentsOf(int prompt) {
        Vertex searchingVertex = allVertices.get(prompt);
        ArrayList<Vertex> neighbourVertices = new ArrayList<>();
        for (int currentData : allVertices.keySet()) {
            if (allVertices.get(currentData).vertices.contains(searchingVertex)) {
                neighbourVertices.add(allVertices.get(currentData));
            }
        }
        return neighbourVertices;
    }

    private void searchHandler() {
        queue.clear();
        Printer.printMenu("SEARCH", "_", Arrays.asList(
                "1. BREADTH FIRST",
                "2. DEPTH FIRST"));
        System.out.print("CHOOSE OPTION:");
        int option = scanner.nextInt();
        System.out.print("Enter data to search :");
        int i = 0;
        switch (option) {
            case 1 -> {
                int searchingData = scanner.nextInt();
                while (i < startingVertices.size()) {
                    breadthFirstSearch(startingVertices.get(i++), searchingData);
                }
            }
            case 2 -> {
                depthFirstSearch(scanner.nextInt());
            }
            default -> {
                if (getUserAgreement("invalid selection\nDo you want to repeat[y/n]:"))
                    searchHandler();
            }
        }
    }

    private void depthFirstSearch(int searchingData) {
        Stack<Vertex> stack = new Stack<>();
        LinkedList<Vertex> path = new LinkedList<>();
        Vertex top;
        boolean isVertexFound = false, isVertexFoundInEntireGraph = false;
        for (Vertex sv : startingVertices) {
            stack.add(sv);
            isVertexFound = false;
            while (stack.size() > 0) {
                top = stack.pop();
                while (top != sv && !path.getLast().vertices.contains(top))
                    path.removeLast();
                if (top.data == searchingData) {
                    path.forEach(element -> System.out.print(element.data + ", "));
                    System.out.println(top.data);
                    isVertexFound = true;
                }
                if (top.vertices.size() > 0) {
                    path.add(top);
                    stack.addAll(top.vertices);
                }
            }
            stack.clear();
            isVertexFoundInEntireGraph = isVertexFoundInEntireGraph || isVertexFound;
        }
        System.out.println(searchingData + " is not found in the graph!");
    }

    private void breadthFirstSearch(Vertex startingVertex, int searchingData) { 
        Stack<Vertex> stack = new Stack<>();
        Stack<Vertex> path = new Stack<>();
        Vertex stackPoppedVertex, queuePoppedVertex;
        if (startingVertex != null && startingVertex.data == searchingData) {
            Printer.printSuccessMessage(startingVertex + " is a Starting Vertex!");
            return;
        }
        queue.add(startingVertex);
        while (queue.size() > 0) {
            queuePoppedVertex = queue.pop();
            stack.add(queuePoppedVertex);
            for (Vertex v : queuePoppedVertex.vertices) {
                if (!traversedVertices.contains(v)) {
                    traversedVertices.add(v);
                    queue.add(v);
                }
                if (v.data == searchingData) {
                    Printer.printSuccessMessage(
                            searchingData + " is found in the Graph as neighbour of" + queuePoppedVertex);
                    path.add(queuePoppedVertex);
                    while (stack.size() > 0) {
                        stackPoppedVertex = stack.pop();
                        if (stackPoppedVertex.vertices.contains(path.peek()))
                            path.add(stackPoppedVertex);
                    }
                    Printer.printSuccessMessage("PATH:");
                    for (Vertex p : path) {
                        System.out.print(p.data + "->");
                    }
                    System.out.print("NaN");
                }
            }
        }
        Printer.printFailureMessage("No such elements found (:");
    }

    private boolean getUserAgreement(String context) {
        System.out.println(context);
        userNeedsNextIteration = scanner.next();
        return userNeedsNextIteration.equalsIgnoreCase("yes") || userNeedsNextIteration.equalsIgnoreCase("y");
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