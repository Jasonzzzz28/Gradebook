
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.Cipher;

import javax.crypto.KeyGenerator;

public class Helper {


	public static Gradebook OpenGradeBook(String fileName, String serializedPrivateKey) throws Exception {


		try {

			KeyPair keyPair = DeserializePrivateKey(serializedPrivateKey);
			File file = new File(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String[] lines = new String[3];// line 1 should be the signature and line 2 is the encrypted AES key line 3 is
											// the encrypted grade book JSON
			int count = 0;
			while (count < lines.length && (lines[count] = reader.readLine()) != null) {
				count++;
			}
			reader.close();


			if (Verify(lines[1], lines[0], keyPair.getPublic())) {

				String encrypted_JSON = lines[2];
				String aesKeyEncoded = Decrypt(lines[1], keyPair.getPrivate());

				byte[] bytes = Base64.getDecoder().decode(aesKeyEncoded);

				SecretKey aesKey = new SecretKeySpec(bytes, 0, bytes.length, "AES");

				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, aesKey);
				byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encrypted_JSON));


				String decrypted_gradebook_JSON = new String(plainText);

				Gradebook gradebook = new Gradebook(decrypted_gradebook_JSON, true);

				if (gradebook.getName().equals(fileName)) { // <-- Implement this

					return gradebook;
				} else {
					System.out.println("The gradebook file name was changed!");
				}
			}
			throw new Exception("Failed to open gradebook file!");

		} catch(Exception e) {
			System.out.println(e.toString());
			throw e; 
		}


		
	}





	public static void SaveGradebook(Gradebook gradebook, String filename, String serializedPrivateKey)
			throws Exception {
		
		Matcher matcher = Pattern.compile("^[a-zA-Z0-9._]+$").matcher(filename);
		if (!matcher.find()) {
			new Exception("Invalid file name");
		}

		KeyPair key_pair = DeserializePrivateKey(serializedPrivateKey);
		SaveGradeBook(gradebook, filename, key_pair);
	}





	public static void SaveGradeBook(Gradebook gradebook, String filename, KeyPair keyPair) throws Exception {

		try {

			String gradebook_json = gradebook.toString();

			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, new SecureRandom());
			SecretKey aesKey = keyGenerator.generateKey();

			Cipher cipher = Cipher.getInstance("AES");

			cipher.init(Cipher.ENCRYPT_MODE, aesKey);

			byte[] cipherBytes = cipher.doFinal(gradebook_json.getBytes());

			String encrypted_gradebook_JSON = Base64.getEncoder().encodeToString(cipherBytes);

			String encryptedAESKey = Encrypt(Base64.getEncoder().encodeToString(aesKey.getEncoded()), keyPair.getPublic());

			String signature = Sign(encryptedAESKey, keyPair.getPrivate());

			BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
			writer.write(signature);// Line 1 in the grade book file will be the signature
			writer.newLine();
			writer.write(encryptedAESKey);// Line 2 in the grade book file will be the encrypted AES key
			writer.newLine();
			writer.write(encrypted_gradebook_JSON);// line 3 in the grade book file will be the encrypted grade book JSON
													// text
			writer.close();

		} catch(Exception e) {
			System.out.println(e.toString());
			throw e; 
		}


	}


	public static KeyPair GenerateKeyPair() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		return generator.generateKeyPair();
	}

	public static String SerializePrivateKey(PrivateKey privateKey) throws Exception {
		String p = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		return toHex(p.getBytes());
	}

	private static KeyPair DeserializePrivateKey(String serializedPrivateKey) throws Exception {
		serializedPrivateKey = fromHex(serializedPrivateKey);

		byte[] bytes = Base64.getDecoder().decode(serializedPrivateKey);// split[0]);

		PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));

		RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;

		RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(),
				privk.getPublicExponent());
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);

		return new KeyPair(publicKey, privateKey);
	}

	private static String Encrypt(String plainText, String serializedPrivateKey) throws Exception {
		KeyPair keyPair = DeserializePrivateKey(serializedPrivateKey);
		return Encrypt(plainText, keyPair.getPublic());
	}

	private static String Encrypt(String plainText, PublicKey publicKey) throws Exception {
		Cipher RSA_cipher = Cipher.getInstance("RSA");

		RSA_cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] cipher_bytes = RSA_cipher.doFinal(plainText.getBytes("UTF8"));

		return Base64.getEncoder().encodeToString(cipher_bytes);
	}

	private static String Decrypt(String encryptedText, String serializedPrivateKey) throws Exception {
		KeyPair keyPair = DeserializePrivateKey(serializedPrivateKey);
		return Decrypt(encryptedText, keyPair.getPrivate());
	}

	private static String Decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(encryptedText);

		Cipher RSA_cipher = Cipher.getInstance("RSA");

		RSA_cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return new String(RSA_cipher.doFinal(bytes), "UTF8");
	}

	private static String Sign(String plainText, String serializedPrivateKey) throws Exception {
		KeyPair keyPair = DeserializePrivateKey(serializedPrivateKey);
		return Sign(plainText, keyPair.getPrivate());
	}

	private static String Sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature private_signature = Signature.getInstance("SHA256withRSA");

		private_signature.initSign(privateKey);

		private_signature.update(plainText.getBytes("UTF8"));

		byte[] signature = private_signature.sign();

		return Base64.getEncoder().encodeToString(signature);
	}

	private static boolean Verify(String plainText, String signature, String serializedPrivateKey) throws Exception {
		KeyPair keyPair = DeserializePrivateKey(serializedPrivateKey);
		return Verify(plainText, signature, keyPair.getPublic());
	}

	private static boolean Verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature public_signature = Signature.getInstance("SHA256withRSA");

		public_signature.initVerify(publicKey);

		public_signature.update(plainText.getBytes("UTF8"));

		byte[] signature_bytes = Base64.getDecoder().decode(signature);

		return public_signature.verify(signature_bytes);
	}

	private static String toHex(byte[] data) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.length; i++)
			result.append(String.format("%x", data[i]));
		return result.toString().toUpperCase();
	}

	private static String fromHex(String hexData) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < hexData.length(); i += 2) {
			result.append((char) Integer.parseInt(hexData.substring(i, i + 2), 16));
		}
		return result.toString();
	}
}
