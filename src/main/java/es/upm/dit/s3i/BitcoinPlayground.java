package es.upm.dit.s3i;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ListenableFuture;

@Component
public class BitcoinPlayground {
	
	private NetworkParameters netParams = TestNet3Params.get();
	private String walletPrefix = "S3iShellwarp";
	private File walletDirectory = new File("bitcoin");

	public BitcoinPlayground() {
		
	}
	
	public void run() {
		//createWallet(netParams, walletPrefix);
		
		// Single transaction
		Address address = Address.fromBase58(netParams, "mssCFZ4vM1Qzo2t3JUStA2VmLC5j3KHvmh"); // Josi
		//Address address = Address.fromBase58(netParams, "mjzn7TW98zhWoguqS4nDmnDoJpvqEEdnwm"); // Jorge
		Coin coin = Coin.parseCoin("0.001");
		try {
			send(netParams, coin, address);
		} catch (InsufficientMoneyException | ExecutionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		// Multisignature NM tx
		Address address = Address.fromBase58(netParams, "mjzn7TW98zhWoguqS4nDmnDoJpvqEEdnwm"); // Jorge
		Coin coin = Coin.parseCoin("0.001");
		
		List<String> pubKeyList = new ArrayList<>();
		// Josi
		// Jorge
		// Marta
		
		List<ECKey> keyList = new ArrayList<>();
		for (String pubKeyHex : pubKeyList) {
			byte[] pubKeyBytes = Utils.parseAsHexOrBase58(pubKeyHex);
			ECKey key = ECKey.fromPublicOnly(pubKeyBytes);
			keyList.add(key);
		}
		
		try {
			Transaction T1 = sendNM(netParams, coin, 2, keyList);
			String T1Hash = T1.getHashAsString();
			System.out.println("T1: " + T1Hash);
			
			File file = new File(walletDirectory.getPath(), T1Hash + ".tx");
			OutputStream stream = new FileOutputStream(file);
			stream.write(T1.bitcoinSerialize());
			stream.close();
			
		} catch (InsufficientMoneyException | ExecutionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	private void createAddress(NetworkParameters netParams) {
		ECKey key = new ECKey();
		
		System.out.println("key:");
		System.out.println("  pub: " + key.getPublicKeyAsHex());
		System.out.println("  sec: " + key.getPrivateKeyAsHex());
		System.out.println("  address: " + key.toAddress(netParams));
	}
	
	private void createWallet(NetworkParameters netParams, String prefix) {
		WalletAppKit kit = new WalletAppKit(netParams, walletDirectory, prefix);
		
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
	
	private void send(NetworkParameters netParams, Coin coin, Address address) throws InsufficientMoneyException, ExecutionException, InterruptedException {
		WalletAppKit kit = new WalletAppKit(netParams, walletDirectory, walletPrefix);
		kit.startAsync();
		kit.awaitRunning();
		
		Wallet wallet = kit.wallet();
		Wallet.SendResult result = wallet.sendCoins(kit.peerGroup(), address, coin);
		result.broadcastComplete.get();
	}
	
	private Transaction sendNM(NetworkParameters netParams, Coin coin, int N, List<ECKey> keyList) throws InsufficientMoneyException, ExecutionException, InterruptedException {
		WalletAppKit kit = new WalletAppKit(netParams, walletDirectory, walletPrefix);
		kit.startAsync();
		kit.awaitRunning();
		
		Transaction T1 = new Transaction(netParams);
		Script script = ScriptBuilder.createMultiSigOutputScript(N, keyList);
		T1.addOutput(coin, script);
		SendRequest request = SendRequest.forTx(T1);
		
		Wallet wallet = kit.wallet();
		wallet.completeTx(request);
		wallet.commitTx(request.tx);
		
		PeerGroup peerGroup = kit.peerGroup();
		ListenableFuture<Transaction> future = peerGroup.broadcastTransaction(request.tx).future();
		future.get();
		return T1;
	}
}
