package monpackageclient;

import monpackage.MyServer;
import monpackage.javacard.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;


import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;

public class MyClient {
	static Apdu apdu ;
	static CadT1Client cad;
	
	public Apdu Msg(byte ins, byte lc, byte[] data,byte le) throws IOException, CadTransportException{	
		apdu = new Apdu();
		apdu.command[Apdu.CLA] = MyServer.CLA_MONAPPLET;
		apdu.command[Apdu.P1] = 0x00;
		apdu.command[Apdu.P2] = 0x00;
		apdu.command[Apdu.INS] = ins;
		//apdu.setLe(0x7f);
		apdu.setLe(le);
		if (data!=null)
			apdu.setDataIn(data);
		cad.exchangeApdu(apdu);
		return apdu;
	}

	public void Connect(){
		Socket sckCarte;
		
		try {
			sckCarte = new Socket("localhost", 9025);
			sckCarte.setTcpNoDelay(true);
			BufferedInputStream input = new BufferedInputStream(sckCarte.getInputStream());
			BufferedOutputStream output = new BufferedOutputStream(sckCarte.getOutputStream());
			cad = new CadT1Client(input, output);
		} catch (Exception e) {
			System.out.println("Erreur : impossible de se connecter a la Javacard");
			return;
		}
		/* Mise sous tension de la carte */
		try {
			cad.powerUp();
		} catch (Exception e) {
			System.out.println("Erreur lors de l'envoi de la commande Powerup a la Javacard");
			return;
		}
	}
	
	public void Select() throws IOException, CadTransportException{

		/* Sélection de l'applet :création du commande SELECT APDU */
		apdu = new Apdu();
		apdu.command[Apdu.CLA] = (byte) 0x00;
		apdu.command[Apdu.INS] = (byte) 0xA4;
		apdu.command[Apdu.P1] = 0x04;
		apdu.command[Apdu.P2] = 0x00;
		byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
		apdu.setDataIn(appletAID);
		cad.exchangeApdu(apdu);
		if (apdu.getStatus() != 0x9000) {
			System.out.println("Erreur lors de la sélection de l'applet");
			System.exit(1);
		}
		
	
	}
	
	public void Deselect(){
		/* Mise hors tension de la carte */
		try {
			cad.powerDown();
		} catch (Exception e) {
			System.out.println("Erreur lors de l'envoi de la commande Powerdown a la Javacard");
			return;
		}
	}
}