package go_game;

import java.util.stream.Stream;

public class StreamFilterStrategy implements Strategy {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int determineMove(Board b, Mark m) {
		// TODO Auto-generated method stub
		return 0;
	}

//	private int[] ints;
	// Via lambdas
	// Stream<int> of intstream
//	Stream<Integer> betterOptions = Stream.of(ints).filter(i -> i % 2 ==0); // BOOLEAN EXPRESSIE DIE VERGELIJKT MET EEN BASIS WAARDE
//	System.out.println("Better options: " + betterOptions.collect(Collectors.toList()));
}
