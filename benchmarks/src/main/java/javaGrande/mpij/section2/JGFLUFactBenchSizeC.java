package javaGrande.mpij.section2;
/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
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
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/

import javaGrande.mpij.jgfutil.JGFInstrumentor;
import javaGrande.mpij.section2.lufact.JGFLUFactBench;

import mpi.MPI;
import mpi.MPIException;

public class JGFLUFactBenchSizeC{ 

  public static int nprocess;
  public static int rank;

  public static void main(String argv[]) throws MPIException{

/* Initialise MPI */
     MPI.Init(argv);
     rank = MPI.COMM_WORLD.Rank();
     nprocess = MPI.COMM_WORLD.Size();

    if(rank==0) {
      JGFInstrumentor.printHeader(2,2,nprocess);
    }

    JGFLUFactBench lub = new JGFLUFactBench(nprocess,rank); 
    lub.JGFrun(2);
 
/* Finalise MPI */
     MPI.Finalize();

  }
}

 
