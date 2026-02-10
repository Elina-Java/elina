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

#ifndef CONV_2D_H
#define CONV_2D_H


// Simple functions for implementing 2D Convolution.
//
// See the 1D convolution case for a description of the basic approach
// implemented here.  This program simply extends the 1D formulation to 2D.
//
// Note that this comment is out of sync.
// There currently is no 1D formulation.
//
// Inputs: N+U-1xM+V-1 matrix, a, UxV matrix, h
// Outputs: NxM matrix, c


task<inner> void conv2d(in float a[N+U-1][M+V-1], in float h[U][V], out float c[N][M]);
task<leaf> void conv2d(in float a[N+U-1][M+V-1], in float h[U][V], out float c[N][M]);

void reference(float** a, float** h, float** c, unsigned int M, unsigned int N, unsigned int U, unsigned int V);

#endif
