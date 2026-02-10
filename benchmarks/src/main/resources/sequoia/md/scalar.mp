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

instance md md_L1(level 1) inner
{
	entrypoint run[0];
	
	data()
	{
		array pos() { elements = 8192; }
		array vel() { elements = 8192; }
		array mass() { elements = 8192; }
		array accelCur() { elements = 8192; }
		array accelOld() { elements = 8192; }
	}
	
	control(level 1)
	{
		loop i() { }

		callsite accel() { target accel_L1() {} }
		callsite arrayInit() { target arrayInit_L1() {} }
		callsite mainLoop() { target mainLoop_L1() {} }
	}
}

instance mainLoop mainLoop_L1(level 1) inner
{
	control(level 1)
	{
		callsite posUpdate() { target posUpdate_L1() {} }
		callsite copyKernel() { target copyKernel_L1() {} }
		callsite arrayInit() { target arrayInit_L1() {} }
		callsite accel() { target accel_L1() {} }
		callsite velUpdate() { target velUpdate_L1() {} }
	}
}

instance accel accel_L1(level 1) inner
{
	tunable r = 8192;
	control (level 0)
	{
		loop i() { spmd { fullrange = 0,1; ways = 1; iterblk = 1; } }
		loop j() { }	
		callsite arrayInit(level 1) { target arrayInit_L1() {} }
		callsite accel() { target accel_L0() {} }
	}	
}

instance accel accel_L0(level 0) leaf {}

instance copyKernel copyKernel_L1(level 1) inner 
{
	tunable r = 8192;
	control (level 0)
	{
		loop i() { spmd { fullrange = 0,1; ways = 1; iterblk = 1; } }
		
		callsite copyKernel() { target copyKernel_L0() {} }
	}
}

instance copyKernel copyKernel_L0(level 0) leaf {}

instance arrayInit arrayInit_L1(level 1) inner 
{
	tunable r = 8192;
	control (level 0)
	{
		loop i() { spmd { fullrange = 0,1; ways = 1; iterblk = 1; } }
		
		callsite arrayInit() { target arrayInit_L0() {} }
	}
}

instance arrayInit arrayInit_L0(level 0) leaf {}

instance posUpdate posUpdate_L1(level 1) inner 
{
	tunable r = 8192;
	control (level 0)
	{
		loop i() { spmd { fullrange = 0,1; ways = 1; iterblk = 1; } }
		
		callsite posUpdate() { target posUpdate_L0() {} }
	}
}

instance posUpdate posUpdate_L0(level 0) leaf {}

instance velUpdate velUpdate_L1(level 1) inner 
{
	tunable r = 8192;
	control (level 0)
	{
		loop i() { spmd { fullrange = 0,1; ways = 1; iterblk = 1; } }
		
		callsite velUpdate() { target velUpdate_L0() {} }
	}
}

instance velUpdate velUpdate_L0(level 0) leaf {}


