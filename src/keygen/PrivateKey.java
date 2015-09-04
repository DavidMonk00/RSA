package keygen;

import java.math.BigInteger;

public class PrivateKey {
	public BigInteger d;
	public BigInteger n;
	public PrivateKey(BigInteger n, BigInteger d){
		this.n = n;
		this.d = d;
	}
}
