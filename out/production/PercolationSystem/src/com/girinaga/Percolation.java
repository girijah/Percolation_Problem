package com.girinaga;

import java.io.ObjectInputStream.GetField;

import javax.swing.JOptionPane;

import edu.princeton.cs.algs4.StdArrayIO;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/*************************************************************************
 *  Author: Dr E Kapetanios
 *  Last update: 17-02-2016
 *
 *************************************************************************/

/*************************************************************************
 *  Add here your answers in regards with the performance analysis
 *  ---------------------------------------------------------------  
 *  This implementation uses weighted quick union by size (without path compression).
 *  percolatesDirect operation with N sites takes constant time.
 *  Afterwards, the union, and connected
 *  operations  take logarithmic time (in the worst case) and the
 *  random,flow operations take quadratic time. connectWithSurroundingOpenSites, 
 *  isValid, xyTo1D, printPercolationStatus and isOpen operations 
 *  take constant time. If percolates method's parameter list 
 *  is empty and make modifications, the performance will take constant time.
 *************************************************************************/

public class Percolation {
	
	private static WeightedQuickUnionUF grid;	
	private static WeightedQuickUnionUF fullness;
	private static int N;						// number of nodes in grid = (NxN)
	private static boolean[] openBool;				// true == open, false == blocked
	private static int virtualTop;
	private static int virtualBottom;
	

    // given an N-by-N matrix of open sites, return an N-by-N matrix
    // of sites reachable from the top
    /**
     * @param open
     * @return
     */
    public static boolean[][] flow(boolean[][] open) {
        
    	boolean[][] full = open;
    	
    	openBool = new boolean[N*N];
    	  	 
    	 for (int i = 0; i <open.length; i++){
             for (int j = 0; j < open.length; j++){           	
                 if (open[i][j]==true) {                 	
                	 openBool[xyTo1D(i,j)]=true;                	                   
                  connectWithSurroundingOpenSites(i,j);               
        	   }
            }             
    	 } 
    	 for (int i = 0; i <open.length; i++){
             for (int j = 0; j < open.length; j++){  
            	 if(isFull(i,j)){
                 	full[i][j]=true;   
                    }else{
                 	   full[i][j]=false;
                    } 
             }
         }
    	 
    	 return full;
    }
    

    // does the system percolate?
    /**
     * @param open
     * @return
     */
    public static boolean percolates(boolean[][] open) {
    	
    	openBool = new boolean[N*N];
    	 for (int i = 0; i <open.length; i++){
             for (int j = 0; j < open.length; j++){   
            	 if (open[i][j]==true) {                 	
                	 openBool[xyTo1D(i,j)]=true;                	                   
                  connectWithSurroundingOpenSites(i,j);                
        	   }
             }
         }
    	if(grid.connected(virtualTop, virtualBottom)){
    		return true;
    	}else
        return false;
    }
    
    
    public static boolean percolatesI() {
    	if(grid.connected(virtualTop, virtualBottom)){
    		return true;
    	}else
        return false;	
    }
    
 // does the system percolate vertically in a direct way?
    /**
     * @param open
     * @return
     */
    public static boolean percolatesDirect(boolean[][] open) {    	
    	
    	 int i = 0; 
    	 boolean isVerticallyPercolates=false;
    	 
             for (int j = 0; j < open.length; j++){            	 
            	 if(open[i][j]==true){
            	 boolean isVertical=false;
            	 
            	 for(int v=0;v<open.length-1;v++){            	             	 
            	 if(isOpen(v+1,j)){
            		 isVertical=true;          			
          		 }else{
          			 isVertical=false;  
          			break;
          		 }
            	 }
            	 
            	 if(isVertical){
            		 isVerticallyPercolates=true; 
            	 }
            	 }
             }         
        return isVerticallyPercolates;
    }

    // draw the N-by-N boolean matrix to standard draw
    /**
     * @param a
     * @param which
     */
    public static void show(boolean[][] a, boolean which) {
    	int N = a.length;
    StdDraw.setXscale(-1, N);
    StdDraw.setYscale(-1, N);
    for (int i = 0; i < N; i++){
        for (int j = 0; j < N; j++){            
            if (isFull(i, j)) {
                StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); 
                StdDraw.filledSquare(j, N-i-1, .5);
            }else if (a[i][j]==which) {            	
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledSquare(j, N-i-1, .5);
            }
           
         }
      }
   
    }
    
    
    // return a random N-by-N boolean matrix, where each entry is
    // true with probability p
    /*if bernoulli() returns false that is white box, black box for true*/ 
    /**
     * @param N
     * @param p
     * @return
     */
    public static boolean[][] random(int N, double p) {
    	if(N<=0) throw new IllegalArgumentException("N cannot be less than or equal to 0.");
		Percolation.N = N;
		grid = new WeightedQuickUnionUF(N*N+2);
		fullness = new WeightedQuickUnionUF(N*N+1);
				
		virtualTop = Percolation.N*Percolation.N;
		virtualBottom = Percolation.N*Percolation.N  + 1;
		
        boolean[][] a = new boolean[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = StdRandom.bernoulli(p);
        return a;
    }
    
    
    /**
     * @param N
     * @param p
     * @param M
     * @return
     */
    public static double eval(int N, double p, int M) {
    	double counter=0;
    	for(int i=0; i<M; i++){
    		boolean[][] r=random(N,p);    		
    		flow(r);
    		if(percolates(r)){
    			counter++;    		
    		}    		
    	}
    	System.out.println("Number N: "+N); 
    	System.out.println("Probability: "+p);    
    	System.out.println("percolates happened/times: "+counter+"/ "+M);    	
        return counter/M;
    }
    
   	
	
    // test client
    /**
     * @param args
     */
    public static void main(String[] args) {
    	

    	// A matrix 5-by-5 will be randomly generated
    	// The dimensions should be changed, while you are testing and during the viva, in order to analyse the performance of the algorithm
    	
    	boolean[][] randomlyGenMatrix = random(5, 0.6); // For instance, a matrix 5-by-5 will be randomly generated
    	
		StdArrayIO.print(randomlyGenMatrix);
    	show(randomlyGenMatrix, true);// All open sites are displayed in black... 
        
        Stopwatch timerFlow = new Stopwatch();
        boolean[][] percolatingMatrix = flow(randomlyGenMatrix);
        StdOut.println("Elapsed time for flowing = " + timerFlow.elapsedTime());        
        
        StdArrayIO.print(percolatingMatrix);
        show(randomlyGenMatrix, true); //If flowing happened,open sites which are full/filled from top are displayed in blue...
        printPercolationStatusI();
        
        /*StdOut.print("Does the system percolates? ");
        Stopwatch timerPercI = new Stopwatch();
        StdOut.println(percolatesI());
        StdOut.println("Elapsed time for percolation I= " + timerPercI.elapsedTime());*/
        
        StdOut.print("Does the system percolates? ");
        Stopwatch timerPerc = new Stopwatch();
        StdOut.println(percolates(randomlyGenMatrix));
        StdOut.println("Elapsed time for percolation = " + timerPerc.elapsedTime());
        
        StdOut.print("Flows vertically in a direct way? ");
        Stopwatch timerPercDir = new Stopwatch();
        StdOut.println(percolatesDirect(randomlyGenMatrix));
        StdOut.println("Elapsed time for vertically direct percolation = " + timerPercDir.elapsedTime());
        
        Stopwatch timerEval = new Stopwatch();
        double percprob = eval(10, 0.593, 1000);
        StdOut.println("Elapsed time for percolation probability = " + timerEval.elapsedTime());
        StdOut.println("Probability that the system percolates: " + percprob);
    }
    
    
   
    public static void printPercolationStatus(boolean[][] boolArr) {
    	 //StdDraw.setPenColor(StdDraw.BLACK);
    	    if(percolates(boolArr)){
    	    //StdDraw.text(N*0.5, -0.7," system percolates!");	
    	    	JOptionPane.showMessageDialog(null, "Percolates!");
    	    }else if(percolates(boolArr)==false){
    	    //StdDraw.text(N*0.42, -0.7,"system doesn't percolate");
    	    	JOptionPane.showMessageDialog(JOptionPane.getRootFrame().getParent(), "Doesn't Percolate!");
    	    }
		
	}
    
    public static void printPercolationStatusI() {
   	 //StdDraw.setPenColor(StdDraw.BLACK);
   	    if(percolatesI()){
   	    //StdDraw.text(N*0.5, -0.7," system percolates!");	
   	    	JOptionPane.showMessageDialog(null, "Percolates!");
   	    }else if(percolatesI()==false){
   	    //StdDraw.text(N*0.42, -0.7,"system doesn't percolate");
   	    	JOptionPane.showMessageDialog(JOptionPane.getRootFrame().getParent(), "Doesn't Percolate!");
   	    }
		
	}


	/**
     * @param i
     * @param j
     * @return
     */
    private static int xyTo1D(int i, int j)
	{
		return (int) (N*i+j);  
	}
    
	
	/**
	 * @param i
	 * @param j
	 * @return
	 */
	private static boolean isOpen(int i, int j)
	{			
		return openBool[xyTo1D(i,j)] == true;
	}	
	
	/**
	 * Checks if a given node is full, i.e. connected to the top row.
	 * This is checked against the fullness WeightedQuickUnionUF object, 
	 * which is NOT connected to the virtual bottom.  
	 * <p>
	 * This prevents backwash - false fullness in nodes that are connected
	 * to the bottom row but not connected to the top row.
	 * @param i the row of the node being checked
	 * @param j the column of the node being checked
	 * @return true if the node is full
	 */
	/**
	 * @param i
	 * @param j
	 * @return
	 */
	private static boolean isFull(int i, int j)
	{	
		return fullness.connected(xyTo1D(i,j), virtualTop);	
	}
	
	
	
	/**
	 * @param i
	 * @param j
	 * @return
	 */
	private static boolean isValid(int i, int j) {
		if(i>=0 && i<N && j>=0 && j<N){
		return true;	
		}else
		return false;
	}	
	
	//	Connects node at given row/column with valid surrounding open sites (left/right/top/bottom).
	//	If the node is in the first row it is connected to the virtual top node,
	//	if the node is in the last row it is connected to the virtual bottom node.
	//	@param i the row of the node to connect
	/**
	 * @param i
	 * @param j
	 */
	private static void connectWithSurroundingOpenSites(int i, int j)
	{
		int index = xyTo1D(i,j);
	
		
		if(i == 0)
		{
			// connecting node to virtualTop if it is in first row
			// in grid & fullness UF objects
			grid.union(virtualTop, index);
			fullness.union(virtualTop, index);
			//System.out.println("virtualTop: "+virtualTop);
		}
		if(i == N-1)
		{
			// connecting index node to virtualBottom if it is in last row
			// ONLY for grid UF object
			// Not connecting for fullness UF object to prevent backwash
			grid.union(virtualBottom, index);
			//System.out.println("virtualBottom: "+virtualBottom);
		}
		if( isValid(i,j-1)&&isOpen(i,j-1))
		{
			// connecting index node to it's left node if open (for grid & fullness)
			grid.union(xyTo1D(i,j-1), index);
			fullness.union(xyTo1D(i,j-1), index);
		}
		if(  isValid(i,j+1)&&isOpen(i,j+1))
		{
			// connecting index node to right node if open (for grid & fullness)
			grid.union(xyTo1D(i,j+1), index);
			fullness.union(xyTo1D(i,j+1), index);
		}
		if( isValid(i-1,j)&& isOpen(i-1,j))
		{
			// connecting index node to top node if open (for grid & fullness)
			grid.union(xyTo1D(i-1,j), index);
			fullness.union(xyTo1D(i-1,j), index);
		}
		if( isValid(i+1,j)&&isOpen(i+1,j))
		{
			// connecting index node to bottom node if open (for grid & fullness)
			grid.union(xyTo1D(i+1,j), index);
			fullness.union(xyTo1D(i+1,j), index);
		}
	}
	

}



