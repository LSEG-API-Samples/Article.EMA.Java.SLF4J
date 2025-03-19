///*|-----------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  --
// *|           Copyright LSEG 2025. All rights reserved.                       --
///*|-----------------------------------------------------------------------------

package com.refinitiv.ema.provider;

import com.refinitiv.ema.access.EmaFactory;
import com.refinitiv.ema.access.FieldList;
import com.refinitiv.ema.access.GenericMsg;
import com.refinitiv.ema.access.Msg;
import com.refinitiv.ema.access.OmmException;
import com.refinitiv.ema.access.OmmIProviderConfig;
import com.refinitiv.ema.access.OmmProvider;
import com.refinitiv.ema.access.OmmProviderClient;
import com.refinitiv.ema.access.OmmProviderEvent;
import com.refinitiv.ema.access.OmmReal;
import com.refinitiv.ema.access.OmmState;
import com.refinitiv.ema.access.PostMsg;
import com.refinitiv.ema.access.RefreshMsg;
import com.refinitiv.ema.access.ReqMsg;
import com.refinitiv.ema.access.StatusMsg;
import com.refinitiv.ema.access.UpdateMsg;
import com.refinitiv.ema.rdm.EmaRdm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.log4j.BasicConfigurator;

class AppClient implements OmmProviderClient {

	private static final Logger logger = LoggerFactory.getLogger(AppClient.class);

	public long itemHandle = 0;

	public void onReqMsg(ReqMsg reqMsg, OmmProviderEvent providerEvent) {
		switch (reqMsg.domainType()) {
		case EmaRdm.MMT_LOGIN:
			processLoginRequest(reqMsg, providerEvent);
			break;
		case EmaRdm.MMT_MARKET_PRICE:
			processMarketPriceRequest(reqMsg, providerEvent);
			break;
		default:
			processInvalidItemRequest(reqMsg, providerEvent);
			break;
		}
	}

	public void onRefreshMsg(RefreshMsg refreshMsg, OmmProviderEvent providerEvent) {
	}

	public void onStatusMsg(StatusMsg statusMsg, OmmProviderEvent providerEvent) {
	}

	public void onGenericMsg(GenericMsg genericMsg, OmmProviderEvent providerEvent) {
	}

	public void onPostMsg(PostMsg postMsg, OmmProviderEvent providerEvent) {
	}

	public void onReissue(ReqMsg reqMsg, OmmProviderEvent providerEvent) {
	}

	public void onClose(ReqMsg reqMsg, OmmProviderEvent providerEvent) {
	}

	public void onAllMsg(Msg msg, OmmProviderEvent providerEvent) {
	}

	void processLoginRequest(ReqMsg reqMsg, OmmProviderEvent event) {
		event.provider()
				.submit(EmaFactory.createRefreshMsg().domainType(EmaRdm.MMT_LOGIN).name(reqMsg.name())
						.nameType(EmaRdm.USER_NAME).complete(true).solicited(true).state(OmmState.StreamState.OPEN,
								OmmState.DataState.OK, OmmState.StatusCode.NONE, "Login accepted")
						.attrib(EmaFactory.createElementList()), event.handle());
	}

	void processMarketPriceRequest(ReqMsg reqMsg, OmmProviderEvent event) {
		if (itemHandle != 0) {
			processInvalidItemRequest(reqMsg, event);
			return;
		}

		FieldList fieldList = EmaFactory.createFieldList();

		fieldList.add(EmaFactory.createFieldEntry().ascii(3, reqMsg.name()));
		fieldList.add(EmaFactory.createFieldEntry().enumValue(15, 840));
		fieldList.add(EmaFactory.createFieldEntry().real(21, 3900, OmmReal.MagnitudeType.EXPONENT_NEG_2));
		fieldList.add(EmaFactory.createFieldEntry().real(22, 3990, OmmReal.MagnitudeType.EXPONENT_NEG_2));
		fieldList.add(EmaFactory.createFieldEntry().real(25, 3994, OmmReal.MagnitudeType.EXPONENT_NEG_2));
		fieldList.add(EmaFactory.createFieldEntry().real(30, 9, OmmReal.MagnitudeType.EXPONENT_0));
		fieldList.add(EmaFactory.createFieldEntry().real(31, 19, OmmReal.MagnitudeType.EXPONENT_0));

		event.provider().submit(EmaFactory.createRefreshMsg().serviceName(reqMsg.serviceName()).name(reqMsg.name())
				.state(OmmState.StreamState.OPEN, OmmState.DataState.OK, OmmState.StatusCode.NONE, "Refresh Completed")
				.solicited(true).payload(fieldList).complete(true), event.handle());
		// logger.info("IProvider_App: Send Market Price Refresh messages");
		logger.info("IProvider_App.AppClient: Sent Market Price Refresh messages");
		itemHandle = event.handle();
	}

	void processInvalidItemRequest(ReqMsg reqMsg, OmmProviderEvent event) {
		event.provider()
				.submit(EmaFactory.createStatusMsg().name(reqMsg.name()).serviceName(reqMsg.serviceName())
						.domainType(reqMsg.domainType()).state(OmmState.StreamState.CLOSED, OmmState.DataState.SUSPECT,
								OmmState.StatusCode.NOT_FOUND, "Item not found"),
						event.handle());
	}

}

public class IProvider_App {

	private static final Logger logger = LoggerFactory.getLogger(IProvider_App.class);

	public static void main(String[] args) {
		//BasicConfigurator.configure();
		OmmProvider provider = null;
		try {

			logger.info("Starting IProvider_App application, waiting for a consumer application");

			AppClient appClient = new AppClient();
			FieldList fieldList = EmaFactory.createFieldList();
			UpdateMsg updateMsg = EmaFactory.createUpdateMsg();

			provider = EmaFactory.createOmmProvider(EmaFactory.createOmmIProviderConfig()
					.operationModel(OmmIProviderConfig.OperationModel.USER_DISPATCH), appClient);

			while (appClient.itemHandle == 0) {
				provider.dispatch(1000);
				Thread.sleep(1000);
			}

			for (int i = 0; i < 60; i++) {
				provider.dispatch(1000);

				fieldList.clear();
				fieldList.add(EmaFactory.createFieldEntry().real(22, 3991 + i, OmmReal.MagnitudeType.EXPONENT_NEG_2));
				fieldList.add(EmaFactory.createFieldEntry().real(25, 3994 + i, OmmReal.MagnitudeType.EXPONENT_NEG_2));
				fieldList.add(EmaFactory.createFieldEntry().real(30, 10 + i, OmmReal.MagnitudeType.EXPONENT_0));
				fieldList.add(EmaFactory.createFieldEntry().real(31, 19 + i, OmmReal.MagnitudeType.EXPONENT_0));

				provider.submit(updateMsg.clear().payload(fieldList), appClient.itemHandle);
				logger.info("IProvider_App: Sent Market Price Update message");

				Thread.sleep(1000);
			}
		} catch (OmmException | InterruptedException excp) {
			logger.error(excp.getMessage());
		} finally {
			if (provider != null)
				provider.uninitialize();
		}
	}
}
