package javaGrande.seq.section3;

import javaGrande.seq.jgfutil.JGFInstrumentor;
import javaGrande.seq.section3.raytracer.JGFRayTracerBench;

/**************************************************************************
 * * Java Grande Forum Benchmark Suite - Version 2.0 * * produced by * * Java
 * Grande Benchmarking Project * * at * * Edinburgh Parallel Computing Centre *
 * * email: epcc-javagrande@epcc.ed.ac.uk * * * This version copyright (c) The
 * University of Edinburgh, 1999. * All rights reserved. * *
 **************************************************************************/

public class JGFRayTracerBenchSizeA {

	public static void main(String argv[]) {

		JGFInstrumentor.printHeader(3, 0);

		JGFRayTracerBench rtb = new JGFRayTracerBench();
		rtb.JGFrun(0);

	}
}
