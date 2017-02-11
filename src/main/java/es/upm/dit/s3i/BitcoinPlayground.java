package es.upm.dit.s3i;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.stereotype.Component;

@Component
public class BitcoinPlayground {
	
	private NetworkParameters netParams = TestNet3Params.get();

	public BitcoinPlayground() {
		
	}
	
	public void run() {
		createAddress(netParams);
		createAddress(netParams);
		createAddress(netParams);
	}
	
	private ECKey createAddress(NetworkParameters netParams) {
		ECKey key = new ECKey();
		
		System.out.println("key:");
		System.out.println("  pub: " + key.getPublicKeyAsHex());
		System.out.println("  sec: " + key.getPrivateKeyAsHex());
		System.out.println("  address: " + key.toAddress(netParams));
		return key;
	}
}
