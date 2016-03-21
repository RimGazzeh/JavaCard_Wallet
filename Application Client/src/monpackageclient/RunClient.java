package monpackageclient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadTransportException;
import monpackage.MyServer;

public class RunClient {

	public static void main(String[] args) throws IOException, CadTransportException {
		// TODO Auto-generated method stub
		MyClient client = new MyClient();
		client.Connect();
		client.Select();
		
		// System.out.println("okk");
		
			System.out.println();
			System.out.println("Application cliente Javacard");
			System.out.println("----------------------------");
			System.out.println();
			System.out.println("Donner le code PIN (max 4 caractere):");
			//byte[] pin_ok = { 1, 3, 9, 3 };
			byte[] pin_ok ;
			Scanner sc = new Scanner(System.in);	
			pin_ok= new byte[] { (byte)sc.nextInt(),(byte)sc.nextInt(),(byte)sc.nextInt(),(byte)sc.nextInt() };
			int fin;
			Apdu apdu = client.Msg(MyServer.INS_TEST_CODE_PIN, (byte) 0x04, pin_ok, (byte) 0x7f);
			if (apdu.getStatus() == 0x6300) {
				{System.out.println("noo");
				fin = 0;}
			} else
				{System.out.println("okk");
				fin = 1;}
			while (fin==1) {
			System.out.println("--------------------------------------------");
			System.out.println("1 - Voir budget");
			System.out.println("2 - Ajouter Argent");
			System.out.println("3 - Extraire de l'argent");
			System.out.println("4 - Quitter");
			System.out.println();
			System.out.println("Votre choix ?");
			int choix = System.in.read();
				while (!(choix >= '1' && choix <= '4')) {
					choix = System.in.read();
				}
			
			switch (choix) {
			case '1': {
				apdu = client.Msg(MyServer.INS_INTERROGER_COMPTE, (byte) 0x00, null, (byte) 0x7f);
				if (apdu.getStatus() != 0x9000) {
					System.out.println("Erreur : status word different de 0x9000");
				} else {
					System.out.println("Valeur du compteur : " + apdu.dataOut[1]);
				}
				break;
			}
			case '2': {
				System.out.println("Montant a ajouter:");
				byte[] montant = new byte[] {sc.nextByte() };
				apdu = client.Msg(MyServer.INS_INCREMENTER_COMPTE, (byte) 0x01, montant, (byte) 0x7f);
				if (apdu.getStatus() != 0x9000) {
					System.out.println("Erreur : status word different de 0x9000");
				} else {
					System.out.println("OK");
				}
				break;	
			}
			case '3': {
				System.out.println("Montant a extraire:");
				byte[] montant = new byte[] {sc.nextByte() };
				apdu = client.Msg(MyServer.INS_DECREMENTER_COMPTE, (byte) 0x01, montant, (byte) 0x7f);
				sc.reset();
				if (apdu.getStatus() == 0x6A85) {
					System.out.println("Erreur : status word different de 0x9000");
				} else {
					System.out.println("OK");
				}
				break;	
			}
			case '4':
				fin = 0;
				break;

			}
			System.out.println();
			System.out.println("Continuer [ 1:oui / 0:non ]:");	
			fin=sc.nextInt();

		}
			client.Deselect();
	}
}