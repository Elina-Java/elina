package javaGrande.seq.section3;

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

import javaGrande.seq.jgfutil.JGFInstrumentor;
import javaGrande.seq.section3.euler.JGFEulerBench;
import javaGrande.seq.section3.moldyn.JGFMolDynBench;
import javaGrande.seq.section3.montecarlo.JGFMonteCarloBench;
import javaGrande.seq.section3.raytracer.JGFRayTracerBench;
import javaGrande.seq.section3.search.JGFSearchBench;

public class JGFAllSizeA {

	public static void main(String argv[]) {

		int size = 0;

		JGFInstrumentor.printHeader(3, size);

		JGFEulerBench eb = new JGFEulerBench();
		eb.JGFrun(size);

		JGFMolDynBench mdb = new JGFMolDynBench();
		mdb.JGFrun(size);

		JGFMonteCarloBench mcb = new JGFMonteCarloBench();
		mcb.JGFrun(size);

		JGFRayTracerBench rtb = new JGFRayTracerBench();
		rtb.JGFrun(size);

		JGFSearchBench sb = new JGFSearchBench();
		sb.JGFrun(size);

	}
}
