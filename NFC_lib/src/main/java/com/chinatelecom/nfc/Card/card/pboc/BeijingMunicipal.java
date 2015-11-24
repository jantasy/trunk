package com.chinatelecom.nfc.Card.card.pboc;

import java.util.ArrayList;

import android.content.res.Resources;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.Card.Util_;
import com.chinatelecom.nfc.Card.tech.Iso7816;

final class BeijingMunicipal extends PbocCard {
	private final static int SFI_EXTRA_LOG = 4;
	private final static int SFI_EXTRA_CNT = 5;

	private BeijingMunicipal(Iso7816.Tag tag, Resources res) {
		super(tag);
		name = res.getString(R.string.nfc_name_bj);
	}

	@SuppressWarnings("unchecked")
	final static BeijingMunicipal load(Iso7816.Tag tag, Resources res) {

		/*--------------------------------------------------------------*/
		// select PSF (1PAY.SYS.DDF01)
		/*--------------------------------------------------------------*/
		if (tag.selectByName(DFN_PSE).isOkey()) {

			Iso7816.Response INFO, CNT, CASH;

			/*--------------------------------------------------------------*/
			// read card info file, binary (4)
			/*--------------------------------------------------------------*/
			INFO = tag.readBinary(SFI_EXTRA_LOG);
			if (INFO.isOkey()) {

				/*--------------------------------------------------------------*/
				// read card operation file, binary (5)
				/*--------------------------------------------------------------*/
				CNT = tag.readBinary(SFI_EXTRA_CNT);

				/*--------------------------------------------------------------*/
				// select Main Application
				/*--------------------------------------------------------------*/
				if (tag.selectByID(DFI_EP).isOkey()) {

					/*--------------------------------------------------------------*/
					// read balance
					/*--------------------------------------------------------------*/
					CASH = tag.getBalance(true);

					/*--------------------------------------------------------------*/
					// read log file, record (24)
					/*--------------------------------------------------------------*/
					ArrayList<byte[]> LOG = readLog(tag, SFI_LOG);

					/*--------------------------------------------------------------*/
					// build result string
					/*--------------------------------------------------------------*/
					final BeijingMunicipal ret = new BeijingMunicipal(tag, res);
					ret.parseBalance(CASH);
					ret.parseInfo(INFO, CNT);
					ret.parseLog(LOG);

					return ret;
				}
			}
		}

		return null;
	}

	private void parseInfo(Iso7816.Response info, Iso7816.Response cnt) {
		if (!info.isOkey() || info.size() < 32) {
			serl = version = date = count = null;
			return;
		}

		final byte[] d = info.getBytes();
		serl = Util_.toHexString(d, 0, 8);
		version = String.format("%02X.%02X%02X", d[8], d[9], d[10]);
		date = String.format("%02X%02X.%02X.%02X - %02X%02X.%02X.%02X", d[24],
				d[25], d[26], d[27], d[28], d[29], d[30], d[31]);
		count = null;

		if (cnt != null && cnt.isOkey() && cnt.size() > 4) {
			byte[] e = cnt.getBytes();
			final int n = Util_.toInt(e, 1, 4);
			if (e[0] == 0)
				count = String.format("%d ", n);
			else
				count = String.format("%d* ", n);
		}
	}
}
