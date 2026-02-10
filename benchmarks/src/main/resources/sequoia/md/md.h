/*******************************************************************************
 * Copyright (c) 2010,
 *   The Board of Trustees of The Leland Stanford Junior University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

#ifndef MD_H
#define MD_H

#include "types.h"

// Simple functions for implementing the md physics simulation.
// (Whatever that is.)

// Top level function for running md physics simulation
void run(Float3* pos, Float3* vel, float* mass, int nSteps);

// Initializes elements in an array to 0.0
task<inner> void arrayInit(inout Float3 array[N]);
task<leaf> void arrayInit(inout Float3 array[N]);

// Compute acceleration by computing forces among bodies
task<inner> void accel(in Float3 pos1[N], in Float3 pos2[N], in float mass1[MASS_REP*N], in float mass2[MASS_REP*N],
		in unsigned int blockIndex1, in unsigned int blockIndex2, in unsigned int blockCount, inout Float3 Accel[N]);
task<leaf> void accel(in Float3 pos1[N], in Float3 pos2[N], in float mass1[MASS_REP*N], in float mass2[MASS_REP*N],
		in unsigned int blockIndex1, in unsigned int blockIndex2, in unsigned int blockCount, inout Float3 Accel[N]);

// VVerlet Position Update: updates position using velocity and acceleration
task<inner> void posUpdate(inout Float3 pos[N], in Float3 vel[N], in Float3 accel[N]);
task<leaf> void posUpdate(inout Float3 pos[N], in Float3 vel[N], in Float3 accel[N]);

// VVerlet Velocity Update: updates velocity using acceleration
task<inner> void velUpdate(inout Float3 vel[N], in Float3 accel[N], in Float3 accelOld[N]);
task<leaf> void velUpdate(inout Float3 vel[N], in Float3 accel[N], in Float3 accelOld[N]);

// Copy Kernel: copies data from accelNew to accelOld
task<inner> void copyKernel(out Float3 accelOld[N], in Float3 accelNew[N]);
task<leaf> void copyKernel(out Float3 accelOld[N], in Float3 accelNew[N]);

// Main Loop: executes one iteration of md
task<inner> void mainLoop(inout Float3 pos[N], inout Float3 vel[N], in float mass[MASS_REP*N], inout Float3 accelOld[N], inout Float3 accelCur[N]);

// Entrypoint task:
task<inner> void md(inout Float3 pos[N], inout Float3 vel[N], in float mass[MASS_REP*N], inout Float3 accelOld[N], inout Float3 accelCur[N], in int nSteps);

#endif
