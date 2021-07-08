# StyleLogic Sutter Mill Prediction Engine v2
Original prediction engine for Review Movies from StyleLogic of 
Solana Beach (San Diego) developed originally by John and James 
Skrinska in 2001 partially cleaned up to a v2 by introducing 
idiomatic Java, updating Java from 1.1 to 11, backing it with 
the H2 in-memory database, and moving its structure to a more 
clean layout. The direction is to re-thread it to distribute 
the heaviest loads to improve scalability optimizing it for 
single-machine processing wrapping it as an RA service for 
decentralized applications. The original SQL was not with the 
application so had to be determined. 
