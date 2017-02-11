package es.upm.dit.s3i;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class BitcoinPlayground {

	public BitcoinPlayground() {
		NetworkParameters netParams = TestNet3Params.get();
		createAddress(netParams);
		createAddress(netParams);
		createAddress(netParams);
	}
	
	ECKey createAddress(NetworkParameters netParams) {
		ECKey key = new ECKey();
		
		System.out.println("key:");
		System.out.println("  pub: " + key.getPublicKeyAsHex());
		System.out.println("  sec: " + key.getPrivateKeyAsHex());
		System.out.println("  address: " + key.toAddress(netParams));
		return key;
	}
}
