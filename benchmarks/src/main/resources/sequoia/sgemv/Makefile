################################################################################
# Copyright (c) 2010,
#   The Board of Trustees of The Leland Stanford Junior University
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# 1. Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
# 
# 2. Redistributions in binary form must reproduce the above copyright notice,
# this list of conditions and the following disclaimer in the documentation
# and/or other materials provided with the distribution.
# 
# 3. The names of the contributors may not be used to endorse or promote
# products derived from this software without specific prior written
# permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
################################################################################

SRC=sgemv.sq main.sq
SQ_FLAG=-s6 -O -d -lsq_cstdio -lsq_ctime
SQ_NO_OPT=-s6 -d -lsq_cstdio -lsq_ctime

scalar:
	sq++ $(SRC) scalar.m scalar.mp $(SQ_FLAG)

smp2:
	sq++ $(SRC) smp2.m smp2.mp $(SQ_FLAG)

cpu:
	sq++ $(SRC) cpu.m cpu.mp $(SQ_FLAG)

smp3:
	sq++ $(SRC) smp3.m smp3.mp $(SQ_FLAG)

cluster2:
	sq++ $(SRC) cluster2.m cluster2.mp $(SQ_FLAG)

cluster_smp:
	sq++ $(SRC) cluster_smp.m cluster_smp.mp $(SQ_FLAG)

cuda:
	sq++ $(SRC) cuda.m cuda.mp $(SQ_NO_OPT)

cuda_cmp:
	sq++ $(SRC) cuda_cmp.m cuda_cmp.mp $(SQ_NO_OPT)
	
clean:
	rm -rf out debug
