import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.*;
import java.nio.file.*;
public class TravelingSalesmanProblemFunctional
{
    private record Edge(int startNodeID, int endNodeID) {}
    private record Path(int pathPrefix, int suffixNodeID) {}
    private int nodesCount;
    private Map<Edge, Integer> graph;
    private int S;
    private int COMPLETE_PATH;
    private void compute(final String inputFileString) {
        readGraphSize(inputFileString);
        buildGraphWithEdgeWeightsFromFile(inputFileString);
        solveTravelingSalesmanProblem();
    }
    private void compute(final int nodesCount) {
        this.nodesCount = nodesCount;
        buildGraphWithRandomEdgeWeights();
        solveTravelingSalesmanProblem();
    }
    private void readGraphSize(final String inputFileString) {
        try {
            final Scanner readFile = new Scanner(new File(inputFileString));
            nodesCount = readFile.nextInt();
            readFile.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    private void buildGraphWithEdgeWeightsFromFile(final String inputFileString) {
        try {
            graph = Files.lines(new File(inputFileString).toPath())
                .skip(1)
                .map(edge -> Arrays.stream(edge.split(" "))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList()))
                .collect(Collectors.toMap(this::createGraphEdge, this::getEdgeWeight));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    private Edge createGraphEdge(final List<Integer> edge) {
        final int startNodeID = edge.get(0);
        final int endNodeID = edge.get(1);
        return new Edge(startNodeID, endNodeID);
    }
    private int getEdgeWeight(final List<Integer> edge) {
        return edge.get(2);
    }
    private void buildGraphWithRandomEdgeWeights() {
        graph = IntStream.range(0, nodesCount)
            .boxed()
            .flatMap(startNodeID -> IntStream.range(0, nodesCount)
                .filter(endNodeID -> startNodeID != endNodeID)
                .mapToObj(endNodeID -> new Edge(startNodeID, endNodeID)))
            .collect(Collectors.toMap(edge -> edge, this::getRandomEdgeWeight));
    }
    private int getRandomEdgeWeight(final Edge ignorableEdge) {
        return (int) Math.floor((Math.random() * 100) + 1);
    }
    private void solveTravelingSalesmanProblem() {
        final Map<Integer, Map<Path, Integer>> memo = createDSes();
        initDSes(memo);
        buildMemoTable(memo);
        final Map.Entry<Path, Integer> optimalPathData = computeOptimalCost(memo);
        final Deque<Integer> shortestPath = findShortestPath(memo, optimalPathData.getKey());
        printResult(optimalPathData.getValue(), shortestPath);
    }
    private Map<Integer, Map<Path, Integer>> createDSes() {
        S = nodesCount - 1;
        COMPLETE_PATH = (1 << nodesCount) - 1;
        return new HashMap<>();
    }
    private void initDSes(final Map<Integer, Map<Path, Integer>> memo) {
        final Map<Path, Integer> twoNodePaths = IntStream.range(0, S)
            .boxed()
            .collect(Collectors.toMap(this::createTwoNodePath, this::setPathCostAsEdgeWeight));
        memo.put(2, twoNodePaths);
    }
    private Path createTwoNodePath(final int N) {
        return new Path(1 << S, N);
    }
    private int setPathCostAsEdgeWeight(final int N) {
        return graph.get(new Edge(S, N));
    }
    private void buildMemoTable(final Map<Integer, Map<Path, Integer>> memo) {
        generateNextSetOfPaths(2, memo).trigger();
    }
    private interface Tailcall<T>
    {
        public abstract Tailcall<T> applyAndThen();
        public default boolean isEnd()  { return false; }
        public default T data() { return null; }
        public default T trigger() {
            return Stream.iterate(this, Tailcall::applyAndThen)
                .filter(Tailcall::isEnd)
                .map(Tailcall::data)
                .findFirst()
                .get();
        }
    }
    private Tailcall<Integer> generateNextSetOfPaths(final int nodesCountInLatestPath, final Map<Integer, Map<Path, Integer>> memo) {
        if (nodesCountInLatestPath == nodesCount)
            return () -> endTailcalling(nodesCountInLatestPath);
        else
            return () -> continueTailcalling(nodesCountInLatestPath, memo);
    }
    private <T> Tailcall<T> endTailcalling(final T t) {
        return new Tailcall<T>() {
            public Tailcall<T> applyAndThen() { return null; }
            public boolean isEnd() { return true; }
            public T data() { return t; }
        };
    }
    private Tailcall<Integer> continueTailcalling(final int nodesCountInLatestPath, final Map<Integer, Map<Path, Integer>> memo) {
        final Map<Path, Integer> latestPaths = memo.get(nodesCountInLatestPath);
        final Map<Path, Integer> newPaths = latestPaths.entrySet().stream()
            .flatMap(latestPathData -> IntStream.range(0, S)
                .filter(N -> isNextNodeAbsentInLatestPathPrefix(N, latestPathData))
                .filter(N -> isNextNodeDifferentFromLatestPathSuffixNode(N, latestPathData))
                .mapToObj(N -> createNewPathAndComputeCost(N, latestPathData)))
            .collect(Collectors.groupingBy(Map.Entry<Path, Integer>::getKey,
                Collectors.reducing(Integer.MAX_VALUE, Map.Entry<Path, Integer>::getValue,
                    BinaryOperator.minBy(Comparator.comparingInt(newPathCost -> newPathCost)))));
        memo.put(nodesCountInLatestPath + 1, newPaths);
        return generateNextSetOfPaths(nodesCountInLatestPath + 1, memo);
    }
    private boolean isNextNodeAbsentInLatestPathPrefix(final int N, final Map.Entry<Path, Integer> latestPathData) {
        final int SM = latestPathData.getKey().pathPrefix();
        return (SM & (1 << N)) == 0;
    }
    private boolean isNextNodeDifferentFromLatestPathSuffixNode(final int N, final Map.Entry<Path, Integer> latestPathData) {
        final int C = latestPathData.getKey().suffixNodeID();
        return C != N;
    }
    private Map.Entry<Path, Integer> createNewPathAndComputeCost(final int N, final Map.Entry<Path, Integer> latestPathData) {
        final int SM = latestPathData.getKey().pathPrefix();
        final int C = latestPathData.getKey().suffixNodeID();
        final int SMC = SM | (1 << C);
        final Path newPath = new Path(SMC, N);
        final int latestPathCost = latestPathData.getValue();
        final Edge newEdge = new Edge(C, N);
        final int newEdgeCost = graph.get(newEdge);
        final int newPathCost = latestPathCost + newEdgeCost;
        return new AbstractMap.SimpleEntry<>(newPath, newPathCost);
    }
    private Map.Entry<Path, Integer> computeOptimalCost(final Map<Integer, Map<Path, Integer>> memo) {
        return IntStream.range(0, S)
            .mapToObj(C -> getHamiltonianPathDataSMC(C, memo))
            .map(this::computeHamiltonianCycleSMCSCost)
            .collect(Collectors.reducing(new AbstractMap.SimpleEntry<>(null, Integer.MAX_VALUE),
                BinaryOperator.minBy(Comparator.comparing(Map.Entry<Path, Integer>::getValue))));
    }
    private Map.Entry<Path, Integer> getHamiltonianPathDataSMC(final int C, final Map<Integer, Map<Path, Integer>> memo) {
        final int SM = COMPLETE_PATH ^ (1 << C);
        final Path hamiltonianPath = new Path(SM, C);
        final Map<Path, Integer> hamiltonianPathsData = memo.get(nodesCount);
        final int hamiltonianPathCost = hamiltonianPathsData.get(hamiltonianPath);
        return new AbstractMap.SimpleEntry<>(hamiltonianPath, hamiltonianPathCost);
    }
    private Map.Entry<Path, Integer> computeHamiltonianCycleSMCSCost(final Map.Entry<Path, Integer> hamiltonianPathData) {
        final int hamiltonianPathSMCCost = hamiltonianPathData.getValue();
        final Path hamiltonianPathSMC = hamiltonianPathData.getKey();
        final int C = hamiltonianPathSMC.suffixNodeID();
        final Edge reverseEdgeTowardsSourceNodeCS = new Edge(C, S);
        final int reverseEdgeCSWeight = graph.get(reverseEdgeTowardsSourceNodeCS);
        final int hamiltonianCycleSMCSCost = hamiltonianPathSMCCost + reverseEdgeCSWeight;
        return new AbstractMap.SimpleEntry<>(hamiltonianPathSMC, hamiltonianCycleSMCSCost);
    }
    private Deque<Integer> findShortestPath(final Map<Integer, Map<Path, Integer>> memo, final Path optimalPath) {
        final Deque<Integer> shortestPath = new ArrayDeque<>();
        shortestPath.add(S);
        shortestPath.addFirst(optimalPath.suffixNodeID());
        return findPreviousNodeAlongShortestPath(shortestPath, optimalPath, nodesCount - 1, memo).trigger();
    }
    private Tailcall<Deque<Integer>> findPreviousNodeAlongShortestPath(final Deque<Integer> shortestPath, final Path optimalPathSMCN,
            final int nodesCountInLatestPathSMC, final Map<Integer, Map<Path, Integer>> memo) {
        if (shortestPath.size() == nodesCount) {
            shortestPath.addFirst(S);
            return () -> endTailcalling(shortestPath);
        } else {
            return () -> continueTailcalling(shortestPath, optimalPathSMCN, nodesCountInLatestPathSMC, memo);
        }
    }
    private Tailcall<Deque<Integer>> continueTailcalling(final Deque<Integer> shortestPath, final Path optimalPathSMCN,
            final int nodesCountInLatestPathSMC, final Map<Integer, Map<Path, Integer>> memo) {
        final Path previousOptimalPathSMC = IntStream.range(0, S)
            .filter(C -> isThisSuffixNodePresentInLatestOptimalPathSMC(C, optimalPathSMCN))
            .mapToObj(C -> findPreviousPathSMCAndComputeCost(C, optimalPathSMCN, nodesCountInLatestPathSMC, memo))
            .collect(Collectors.collectingAndThen(Collectors.minBy(Comparator.comparingInt(Map.Entry<Path, Integer>::getValue)),
                optionalPathData -> optionalPathData.map(Map.Entry<Path, Integer>::getKey).orElse(null)));
        shortestPath.addFirst(previousOptimalPathSMC.suffixNodeID());
        return findPreviousNodeAlongShortestPath(shortestPath, previousOptimalPathSMC, nodesCountInLatestPathSMC - 1, memo);
    }
    private boolean isThisSuffixNodePresentInLatestOptimalPathSMC(final int C, final Path optimalPathSMCN) {
        final int SMC = optimalPathSMCN.pathPrefix();
        return (SMC & (1 << C)) != 0;
    }
    private Map.Entry<Path, Integer> findPreviousPathSMCAndComputeCost(final int C, final Path optimalPathSMCN,
            final int nodesCountInLatestPathSMC, final Map<Integer, Map<Path, Integer>> memo) {
        final Map<Path, Integer> previousPathsSMCData = memo.get(nodesCountInLatestPathSMC);
        final int SMC = optimalPathSMCN.pathPrefix();
        final int N = optimalPathSMCN.suffixNodeID();
        final int SM = SMC ^ (1 << C);
        final Path previousPathSMC = new Path(SM, C);
        final int previousPathSMCCost = previousPathsSMCData.get(previousPathSMC);
        final Edge forwardEdgeCN = new Edge(C, N);
        final int forwardEdgeCNWeight = graph.get(forwardEdgeCN);
        final int pathSMCNTotalCost = previousPathSMCCost + forwardEdgeCNWeight;
        return new AbstractMap.SimpleEntry<>(previousPathSMC, pathSMCNTotalCost);
    }
    private void printResult(final int optimalCost, final Deque<Integer> shortestPath) {
        System.out.println();
        System.out.printf("# of graph nodes = %d\n", nodesCount);
        System.out.println("The graph :");
        printGraph();
        System.out.printf("Min cost of hamiltonian cycle = %,d\n", optimalCost);
        System.out.println("shortest path :");
        printShortestPath(shortestPath);
    }
    private void printGraph() {
        printGraphHeader();
        printGraphEdgeWeights();
    }
    private void printGraphHeader() {
        printHeaderOfEndNodeIDs();
        printHeaderOfEndNodeLiterals();
        printHeaderOfDashes();
    }
    private void printHeaderOfEndNodeIDs() {
        final String strOfEndNodeIDs = IntStream.range(0, nodesCount)
            .mapToObj(endNodeID -> String.format("%3d ", endNodeID))
            .reduce("", String::concat);
        final String prefixFillerString = String.format("%6s | ", " ");
        System.out.println(prefixFillerString + strOfEndNodeIDs);
    }
    private void printHeaderOfEndNodeLiterals() {
        final String strOfEndNodeLiterals = IntStream.range(0, nodesCount)
            .mapToObj(endNodeID -> String.format("%3c ", endNodeID + 'A'))
            .reduce("", String::concat);
        final String prefixFillerString = String.format("%6s | ", " ");
        System.out.println(prefixFillerString + strOfEndNodeLiterals);
    }
    private void printHeaderOfDashes() {
        final String strOfDashesCoveringEdgeWeights = IntStream.range(0, nodesCount)
            .mapToObj(endNodeID -> String.format("%s", "----"))
            .reduce("", String::concat);
        final String prefixFillerString = String.format("%s-|-", "------");
        System.out.println(prefixFillerString + strOfDashesCoveringEdgeWeights);
    }
    private void printGraphEdgeWeights() {
        IntStream.range(0, nodesCount)
            .mapToObj(this::constructPrintableStringForStartNode)
            .forEach(System.out::println);
    }
    private String constructPrintableStringForStartNode(final int startNodeID) {
        final String startNodePrefixString = constructStartNodePrefixString(startNodeID);
        final String printableStringOfEdgeWeights = IntStream.range(0, nodesCount)
            .mapToObj(endNodeID -> new Edge(startNodeID, endNodeID))
            .map(this::constructPrintableStringForThisEdge)
            .reduce("", String::concat);
        return startNodePrefixString + printableStringOfEdgeWeights;
    }
    private String constructStartNodePrefixString(final int startNodeID) {
        return String.format("%2d : %c | ", startNodeID, startNodeID + 'A');
    }
    private String constructPrintableStringForThisEdge(final Edge edge) {
        return String.format("%3d ",
            edge.startNodeID() == edge.endNodeID() ? 0 : graph.get(edge));
    }
    private void printShortestPath(final Deque<Integer> shortestPath) {
        System.out.println("node IDs :");
        printShortestPathNodeIDs(shortestPath);
        System.out.println("node ID literals :");
        printShortestPathNodeLiterals(shortestPath);
        System.out.println("edge weights :");
        printShortestPathEdgeWeights(shortestPath);
        System.out.println("cumulative edge weights :");
        printShortestPathCumulativeEdgeWeights(shortestPath);
    }
    private void printShortestPathNodeIDs(final Deque<Integer> shortestPath) {
        final String printableString = shortestPath.stream()
            .map(nodeID -> String.format("%3d ", nodeID))
            .reduce("", String::concat);
        System.out.println(printableString);
    }
    private void printShortestPathNodeLiterals(final Deque<Integer> shortestPath) {
        final String printableString = shortestPath.stream()
            .map(nodeID -> String.format("%3c ", nodeID + 'A'))
            .reduce("", String::concat);
        System.out.println(printableString);
    }
    private void printShortestPathEdgeWeights(final Deque<Integer> shortestPath) {
        System.out.printf("%3d ", 0);
        final Iterator<Integer> startNodeIterator = shortestPath.iterator();
        final String printableString = shortestPath.stream()
            .skip(1)
            .map(endNodeID -> new Edge(startNodeIterator.next(), endNodeID))
            .map(edge -> String.format("%3d ", graph.get(edge)))
            .reduce("", String::concat);
        System.out.println(printableString);
    }
    private class Context
    {
        private int data;
        public int getData() { return data; }
        public void setData(int data) { this.data = data; }
    }
    private void printShortestPathCumulativeEdgeWeights(final Deque<Integer> shortestPath) {
        final Iterator<Integer> startNodeIterator = shortestPath.iterator();
        final Deque<Integer> edgeWeights = shortestPath.stream()
            .skip(1)
            .map(endNodeID -> new Edge(startNodeIterator.next(), endNodeID))
            .map(graph::get)
            .collect(Collectors.toCollection(ArrayDeque::new));
        edgeWeights.addFirst(0);
        final Context ctxt = new Context();
        final List<Integer> cumulativeEdgeWeights = edgeWeights.stream()
            .map(edgeWeight -> computeCumulativeEdgeWeight(edgeWeight, ctxt))
            .collect(Collectors.toList());
        final String printableString = cumulativeEdgeWeights.stream()
            .map(cumulativeEdgeWeight -> String.format("%3d ", cumulativeEdgeWeight))
            .reduce("", String::concat);
        System.out.println(printableString);
    }
    private int computeCumulativeEdgeWeight(final int edgeWeight, final Context ctxt) {
        final int cumulativeEdgeWeight = ctxt.getData() + edgeWeight;
        ctxt.setData(cumulativeEdgeWeight);
        return cumulativeEdgeWeight;
    }
    public static void main(String[] args) {
        TravelingSalesmanProblemFunctional tsp = new TravelingSalesmanProblemFunctional();
        tsp.compute("TSPInput1.txt");
        tsp.compute("TSPInput2.txt");
        tsp.compute(4);
        tsp.compute(5);
        tsp.compute(7);
        tsp.compute(10);
        tsp.compute(15);
        tsp.compute(18);
        tsp.compute(20);
        tsp.compute(23);
        tsp.compute(25);
    }
}