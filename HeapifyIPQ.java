package priorityqueue;

import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class HeapifyIPQ
{
    private record Edge(int startVertexID, int endVertexID) {
        public String toString() {
            return String.format("(%d, %d)", startVertexID, endVertexID);
        }
    }
    private record State(Edge sinkingHeapNode, int index, boolean isSinkFeasible) {}

    private int verticesCount;
    private Map<Edge, Integer> graph;
    private List<Edge> minHeapIPQ;

    private void computeAndPrint(final String inputFile) {
        readGraphVerticesCount(inputFile);
        buildGraphFromInputFile(inputFile);
        createIPQ();
        System.out.println();
        System.out.println("Heap loaded with graph edges, before heapify :");
        System.out.println(printHeap());
        heapify();
        System.out.println("Heap after heapify :");
        System.out.println(printHeap());
    }

    public String compute(final String inputFile) {
        readGraphVerticesCount(inputFile);
        buildGraphFromInputFile(inputFile);
        createIPQ();
        heapify();
        return printHeap();
    }

    private void readGraphVerticesCount(final String inputFile) {
        try {
            verticesCount = new Scanner(new File(inputFile)).nextInt();
        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }

    private void buildGraphFromInputFile(final String inputFile) {
        try {
            graph = Files.lines(new File(inputFile).toPath())
                .skip(1)
                .map(edgeData -> Arrays.stream(edgeData.split(" "))
                    .map(String::strip)
                    .map(Integer::parseInt)
                    .toList())
                .collect(Collectors.toUnmodifiableMap(
                    list -> new Edge(list.get(0), list.get(1)),
                    list -> list.get(2)));
        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }

    private void createIPQ() {
        minHeapIPQ = graph.keySet().stream()
            .sorted(Comparator.comparing(Edge::startVertexID).thenComparing(Edge::endVertexID))
            .collect(Collectors.toList());
    }

    private void heapify() {
        final int finalParentHeapIndex = ((minHeapIPQ.size() - 1) - 1) / 2;

        Stream.iterate(finalParentHeapIndex, index -> index >= 0, index -> index - 1)
            .forEach(this::sink);
    }

    private void sink(final int index) {
        final Edge sinkingHeapNode = minHeapIPQ.get(index);

        final int terminalIndex = Stream.iterate(new State(sinkingHeapNode, index, true), this::sinkOneLevel)
            .filter(state -> (!state.isSinkFeasible) || (2 * state.index() + 1 >= minHeapIPQ.size()))
            .findFirst()
            .orElseThrow()
            .index();

        minHeapIPQ.set(terminalIndex, sinkingHeapNode);
    }

    private State sinkOneLevel(final State state) {
        final int index = state.index();
        final int leftChildIndex = 2 * index + 1;
        final int rightChildIndex = 2 * index + 2;

        final Edge parentHeapNode = state.sinkingHeapNode();
        final Edge leftChildHeapNode = minHeapIPQ.get(leftChildIndex);
        Edge rightChildHeapNode = null;

        final int parentEdgeCost = graph.get(parentHeapNode);
        final int leftChildEdgeCost = graph.get(leftChildHeapNode);

        int childEdgeCost = -1;
        boolean isLeft = false;

        if (rightChildIndex >= minHeapIPQ.size()) {
            childEdgeCost = leftChildEdgeCost;
            isLeft = true;
        } else {
            rightChildHeapNode = minHeapIPQ.get(rightChildIndex);
            final int rightChildEdgeCost = graph.get(rightChildHeapNode);

            if (leftChildEdgeCost < rightChildEdgeCost) {
                childEdgeCost = leftChildEdgeCost;
                isLeft = true;
            } else {
                childEdgeCost = rightChildEdgeCost;
                isLeft = false;
            }
        }

        if (parentEdgeCost > childEdgeCost) {
            minHeapIPQ.set(index, isLeft ? leftChildHeapNode : rightChildHeapNode);

            final int newIndex = isLeft ? leftChildIndex : rightChildIndex;

            return new State(state.sinkingHeapNode(), newIndex, true);
        } else {
            return new State(state.sinkingHeapNode(), index, false);
        }
    }

    private String printHeap() {
        return String.format("%s", minHeapIPQ);
    }

    public static void main(String[] args) {
        final HeapifyIPQ indexedPriorityQueue = new HeapifyIPQ();

        indexedPriorityQueue.computeAndPrint("GraphInput1.txt");
        indexedPriorityQueue.computeAndPrint("GraphInput2.txt");
        indexedPriorityQueue.computeAndPrint("GraphInput3.txt");
    }
}
