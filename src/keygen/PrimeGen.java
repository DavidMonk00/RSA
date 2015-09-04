package keygen;

import java.math.BigInteger;

public class PrimeGen {
	public static BigInteger PrimeGen(BigInteger start) {
		if (start.mod(BigInteger.valueOf(2)) == BigInteger.ZERO){
			start = start.add(BigInteger.ONE);
		}
		boolean isPrime = false;
		BigInteger prime = BigInteger.ZERO;
		while (isPrime == false) {
			isPrime = PrimeCheck.PrimeCheck(start);
			if (isPrime){
				prime = start;
			}
			start = start.add(BigInteger.valueOf(2));
		}
		return prime;
	}
}
