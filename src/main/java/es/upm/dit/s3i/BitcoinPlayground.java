package es.upm.dit.s3i;

import java.io.File;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;

@Component
public class BitcoinPlayground {
	
	private NetworkParameters netParams = TestNet3Params.get();
	private String walletPrefix = "S3iShellwarp";

	public BitcoinPlayground() {
		
	}
	
	public void run() {
		createWallet(netParams, walletPrefix);
	}
	
	private void createAddress(NetworkParameters netParams) {
		ECKey key = new ECKey();
		
		System.out.println("key:");
		System.out.println("  pub: " + key.getPublicKeyAsHex());
		System.out.println("  sec: " + key.getPrivateKeyAsHex());
		System.out.println("  address: " + key.toAddress(netParams));
	}
	
	private void createWallet(NetworkParameters netParams, String prefix) {
		File directory = new File("bitcoin");
		WalletAppKit kit = new WalletAppKit(netParams, directory, prefix);
		
		kit.startAsync();
		kit.awaitRunning();
		
		Wallet wallet = kit.wallet();
		ECKey key0 = wallet.currentReceiveKey();
		System.out.println("wallet: " + wallet);
		
		DeterministicSeed seed = wallet.getKeyChainSeed();
		System.out.println("seed: " + seed.toHexString());
		System.out.println("      " + Utils.join(seed.getMnemonicCode()));
		
		System.out.println("receiving address: " + key0.toAddress(netParams));
		System.out.println("impored keys: ");
		for (ECKey key: wallet.getImportedKeys()) {
			System.out.println("  key: " + key.toAddress(netParams));
		}
		System.out.println("issued keys: ");
		for (ECKey key: wallet.getIssuedReceiveKeys()) {
			System.out.println("  key: " + key.toAddress(netParams));
		}
		System.out.println("balance: " + wallet.getBalance().toFriendlyString());
	}
}
