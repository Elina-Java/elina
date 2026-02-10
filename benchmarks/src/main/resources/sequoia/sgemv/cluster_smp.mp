// *****************************************************************************
// Copyright (c) 2010,
//   The Board of Trustees of The Leland Stanford Junior University
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation
// and/or other materials provided with the distribution.
// 
// 3. The names of the contributors may not be used to endorse or promote
// products derived from this software without specific prior written
// permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
// *****************************************************************************

instance sgemv i2(level 2) inner
{
	entrypoint main[0];

	tunable Mblk = 2048;
	tunable Nblk = 4096;

	data()
	{
		array a() 
		{ 
			elements = 8192, 4096; 
			blockcyclic { grid = 4,1; blocksize = 2048, 4096; }
		}
		array x() 
		{ 
			elements = 4096; 
		}
		array y() 
		{ 
			elements = 8192; 
			blockcyclic { grid = 4; blocksize = 2048; }
		}
	}

	control(level 1)
	{
		loop i() { spmd { fullrange = 0,4; ways = 4; iterblk = 1; } }
		loop j() { spmd { ways = 1; iterblk = 1; } }
		callsite sgemv() { target i1() {} } 
	}
}

instance sgemv i1(level 1) inner
{
	tunable Mblk = 1024;
	tunable Nblk = 4096;
	
	control(level 0)
	{
		loop i() { spmd { fullrange = 0,2; ways = 2; iterblk = 1; } }
		loop j() { spmd { ways = 1; iterblk = 1; } }
		callsite sgemv() { target l() { } }
	}
}

instance sgemv l(level 0) leaf { }
