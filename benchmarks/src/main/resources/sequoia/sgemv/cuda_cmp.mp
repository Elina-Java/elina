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

instance sgemv i1(level 3) inner
{
	entrypoint main[0];

	//tunable blockSize = 64;
	//tunable Mblk = 2;
	//tunable Nblk = 32;
	//tunable Mblk = 2097152;
	//tunable Nblk = 256;
	tunable Mblk = 4096;
	tunable Nblk = 256;

	data()
	{
		//array a() { elements = 64, 4; }	
		//array x() { elements = 4; }
		//array y() { elements = 64; }
		//array a() { elements = 2, 32; }
		//array x() { elements = 32; }
		//array y() { elements = 2; }
		//array a() { elements = 4194304, 256; }
		//array x() { elements = 256; }
		//array y() { elements = 4194304; }
		array a() { elements = 8192, 256; }
		array x() { elements = 256; }
		array y() { elements = 8192; }
	}

	control(level 2)
	{
		loop i() { spmd { fullrange = 0,2; ways = 2; iterblk = 1; } }
		loop j() { spmd { ways = 1; iterblk = 1; } }
		callsite sgemv() { target i2() { } }
	}
}

instance sgemv i2(level 2) inner
{

	//tunable blockSize = 32;
	//tunable Mblk = 1;
	//tunable Nblk = 32;
	tunable Mblk = 1;
	tunable Nblk = 256;

	control(level 1)
	{
		//loop i() { spmd { fullrange = 0,2097152; ways = 2097152; iterblk = 1; } }
		loop i() { spmd { fullrange = 0, 4096; ways = 4096; iterblk = 1; } }
		loop j() { spmd { ways = 1; iterblk = 1; } }
		callsite sgemv() { target i3() { } }
	}
}

instance sgemv i3(level 1) inner
{
	//tunable blockSize = 1;
	tunable Mblk = 1;
	tunable Nblk = 1;

	control(level 0)
	{
		loop i() { spmd { fullrange = 0,256; ways = 1; iterblk = 1; } }
		loop j() { spmd { ways = 256; iterblk = 1; } }
		callsite sgemv() { target i4() { } }
	}
}

instance sgemv i4(level 0) leaf
{ 
	//fileName("ext_saxpy.cu");
}
