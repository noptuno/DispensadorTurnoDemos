package com.gpp.devoluciondeenvases.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterConstants.Command;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.Table;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@SuppressLint("DefaultLocale")
public class XTUtils {

	private static final String TAG = "XUtils";



	private static int char2Int(char data) {
		if (data >= 48 && data <= 57)// 0~9
			data -= 48;
		else if (data >= 65 && data <= 70)// A~F
			data -= 55;
		else if (data >= 97 && data <= 102)// a~f
			data -= 87;
		return Integer.valueOf(data);
	}


	public static byte[] string2bytes2(String content) {

		Log.i(TAG, "" + content);
		try {
			content = new String(content.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		char[] charArray = content.toCharArray();
		byte[] tempByte = new byte[512];
		tempByte[0] = 0x34;
		int count = 0;
		for (int i = 0; i < charArray.length; i = i + 2) {
			tempByte[count++] = (byte) (char2Int(charArray[i]) * 16 + char2Int(charArray[i + 1]));
		}
		Log.i(TAG, "---------------");
		byte[] retByte = new byte[count];
		System.arraycopy(tempByte, 0, retByte, 0, count);
		for (int i = 0; i < retByte.length; i++) {
			Log.i(TAG, retByte[i] + "");
		}
		return tempByte;
	}

	public static void printNote(PrinterInstance mPrinter) {
		mPrinter.initPrinter();
		mPrinter.setFont(0, 0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.printText("Notas");
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
		StringBuffer sb = new StringBuffer();
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setFont(0, 1, 1, 0, 0);
		mPrinter.printText("nombre compania" + "\n");
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		// 字号使用默认
		mPrinter.setFont(0, 0, 0, 0, 0);
		sb.append("numeros" + "574001\n");

		mPrinter.printText(sb.toString()); // 打印

		sb = new StringBuffer();

			sb.append("numero" + "                                6.00\n");
			sb.append("precio" + "                                35.00\n");
			sb.append("metodo pago" + "                                100.00\n");
			sb.append("cambio" + "                                65.00\n");

			sb.append("==============================================\n");


		mPrinter.printText(sb.toString());

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setFont(0, 0, 1, 0, 0);
		mPrinter.printText("gracias" + "\n");
		mPrinter.printText("version demo" + "\n\n\n");

		mPrinter.setFont(0, 0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

	}

}
