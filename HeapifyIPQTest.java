package priorityqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeapifyIPQTest {
  private HeapifyIPQ ipq;

  @BeforeEach
  void init() {
    ipq = new HeapifyIPQ();
  }

  @Test
  void heapifyGraph1(){
    var result = ipq.compute("GraphInput1.txt");

    var expectedResult = "[(3, 2), (0, 2), (0, 1), (2, 1), (3, 1), (0, 3), (2, 0), (1, 0), (2, 3), (3, 0), (1, 2), (1, 3)]";
    assertEquals(expectedResult, result);
  }

  @Test
  void heapifyGraph2(){
    var result = ipq.compute("GraphInput2.txt");

    var expectedResult = "[(5, 3), (3, 5), (2, 3), (0, 2), (3, 4), (4, 2), (1, 0), (2, 4), (2, 6), (3, 1), (0, 5), (1, 6), (2, 0), (4, 5), (0, 3), (0, 4), (2, 5), (5, 6), (3, 0), (6, 3), (3, 2), (0, 1), (1, 5), (3, 6), (4, 0), (4, 1), (0, 6), (4, 3), (2, 1), (4, 6), (5, 0), (5, 1), (5, 2), (1, 2), (5, 4), (1, 3), (6, 0), (6, 1), (6, 2), (1, 4), (6, 4), (6, 5)]";
    assertEquals(expectedResult, result);
  }

  @Test
  void heapifyGraph3(){
    var result = ipq.compute("GraphInput3.txt");

    var expectedResult = "[(3, 2), (3, 0), (2, 0), (2, 1), (0, 2), (0, 3), (0, 1), (1, 0), (2, 3), (1, 2), (3, 1), (1, 3)]";
    assertEquals(expectedResult, result);
  }

}
