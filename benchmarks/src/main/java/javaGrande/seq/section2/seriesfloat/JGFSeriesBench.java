/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/


package javaGrande.seq.section2.seriesfloat;

import javaGrande.seq.jgfutil.JGFInstrumentor;
import javaGrande.seq.jgfutil.JGFSection2;

public class JGFSeriesBench extends SeriesTest implements JGFSection2{ 

  private int size; 
  private int datasizes[]={10000,100000,1000000};

  public void JGFsetsize(int size){
    this.size = size;
  }

  public void JGFinitialise(){
    array_rows = datasizes[size];
    buildTestData();
  }
 
  public void JGFkernel(){
    Do(); 
  }

  public void JGFvalidate(){
	  float ref[][] = { { (float) 2.8729012, 0 },
				{ (float) 1.1159486, (float) -1.8819966 },
				{ (float) 0.34412986, (float) -1.164581 },
				{ (float) 0.15222682, (float) -0.8143533 } };
/* 
// for 200 points 
    float ref[][] = {{2.8377707562588803, 0.0},
		       {1.0457844730995536, -1.8791032618587762},
		       {0.27410022422635033, -1.158835123403027},
		       {0.08241482176581083, -0.8057591902785817}};
*/ 
  
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 2; j++){
	float error = Math.abs(TestArray[j][i] - ref[i][j]);
	if (error > 1.0e-12 ){
	  System.out.println("Validation failed for coefficient " + j + "," + i);
	  System.out.println("Computed value = " + TestArray[j][i]);
	  System.out.println("Reference value = " + ref[i][j]);
	}
      }
    }
  }

  public void JGFtidyup(){
    freeTestData(); 
  }  



  public void JGFrun(int size){


    JGFInstrumentor.addTimer("Section2:Series:Kernel", "coefficients",size);
    JGFsetsize(size); 
    JGFinitialise(); 
    JGFkernel(); 
    JGFvalidate(); 
    JGFtidyup(); 

     
    JGFInstrumentor.addOpsToTimer("Section2:Series:Kernel", (float) (array_rows * 2 - 1));
 
    JGFInstrumentor.printTimer("Section2:Series:Kernel"); 
  }
}
