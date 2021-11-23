
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.*;

//import ...

/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

public static void main(String[] args) {
		// String key;

		if (args.length < 2) {
			System.out.println("Usage: setup <logfile pathname>");
			System.exit(1);
		}

		/* add your code here */
		String option = args[0];
		String filename = args[1];

		if (option.equals("-N")) {

			if (!file_test(filename)) {
				try {
					Gradebook gradebook = new Gradebook(filename);
					KeyPair key_pair = Helper.GenerateKeyPair();
					Helper.SaveGradeBook(gradebook, filename, key_pair);
					System.out.println(Helper.SerializePrivateKey(key_pair.getPrivate()));
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (option.equals("-RecoverTest") && args.length > 2 && TestRecoverGradebook(filename, args[2])) {
			// args[2] is the key
			return;
		}

		System.out.println("invalid");
		System.exit(255);
	}

private static boolean file_test(String filename) {
		try {
			File f = new File(filename);
			return f.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Boolean TestRecoverGradebook(String filename, String key) {
		try {
			System.out.println("Recovering gradebook..");
			Gradebook gradebook = Helper.OpenGradeBook(filename, key);
			System.out.println("Success!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
