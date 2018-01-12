package nl.uu.cs.treewidth;

import java.io.Reader;
import java.io.StringReader;

import nl.uu.cs.treewidth.algorithm.*;
import nl.uu.cs.treewidth.input.*;
import nl.uu.cs.treewidth.output.DotWriter;
import nl.uu.cs.treewidth.input.GraphInput.InputData;
import nl.uu.cs.treewidth.ngraph.*;
import nl.uu.cs.treewidth.timing.JavaNanoTime;
import nl.uu.cs.treewidth.timing.Stopwatch;

public class TDPrint {
  
  /**
   * @param args
   */
  public static String nativeMain(String str_graph) {
    NGraph<InputData> g;
    Reader sr = new StringReader( str_graph );
    GraphInput in = new DgfReader( sr );
    
    
    try {
      g = in.get(); //Read the graph
    } catch (InputException e) {
      return "JAVA_ERROR: Exception reading the graph" ;
    }
    
    NVertexOrder<InputData> permutation = null;
    
   QuickBB<InputData> qbbAlgo = new QuickBB<InputData>();
   qbbAlgo.setInput( g );
   qbbAlgo.run();
   permutation = qbbAlgo.getPermutation();
// System.out.println(permutation.toString());     

    PermutationToTreeDecomposition<InputData> convertor = new PermutationToTreeDecomposition<InputData>( permutation );
    convertor.setInput( g );
    convertor.run();

    NGraph<NTDBag<InputData>> decomposition = convertor.getDecomposition();
    DotWriter dw = new DotWriter();
    return dw.formatTD(decomposition);
  }
  
  public static void main(String[] args) {
    
    NGraph<InputData> g;
    // args: ... algo graph
    String graph = args[args.length-1];
    String algorithm = args[args.length-2];
    GraphInput in = new DgfReader( graph );
    
    
    try {
      g = in.get(); //Read the graph
    } catch (InputException e) {
      System.out.println( "There was an error opening this file." );
      return;
    }
    
    
    MaximumMinimumDegreePlusLeastC<InputData> lbAlgo = new MaximumMinimumDegreePlusLeastC<InputData>();
    lbAlgo.setInput( g );
    lbAlgo.run();
    int lowerbound = lbAlgo.getLowerBound();
    
    GreedyFillIn<InputData> ubAlgo = new GreedyFillIn<InputData>();
    ubAlgo.setInput( g );
    ubAlgo.run();
    int upperbound = ubAlgo.getUpperBound();
    
    NVertexOrder<InputData> permutation = null;
    
    if( lowerbound == upperbound ) {
      permutation = ubAlgo.getPermutation();
    } else if( algorithm.equals( "upperbound" ) ) {
      permutation = ubAlgo.getPermutation();
    } else {
      QuickBB<InputData> qbbAlgo = new QuickBB<InputData>();
      qbbAlgo.setInput( g );
      qbbAlgo.run();
      permutation = qbbAlgo.getPermutation();
    }
    
    PermutationToTreeDecomposition<InputData> convertor = new PermutationToTreeDecomposition<InputData>( permutation );
    convertor.setInput( g );
    try {
      convertor.run();
    } catch (IndexOutOfBoundsException e) {
      System.out.println( "There was an error converting a permutation to a tree decomposition." );
      return;
    }
    NGraph<NTDBag<InputData>> decomposition = convertor.getDecomposition();
    DotWriter dw = new DotWriter();
    System.out.println(dw.formatTD(decomposition));
    
  }
  
}
