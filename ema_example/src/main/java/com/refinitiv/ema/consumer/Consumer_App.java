///*|-----------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  --
// *|           Copyright LSEG 2025. All rights reserved.                       --
///*|-----------------------------------------------------------------------------

package com.refinitiv.ema.consumer;

import com.refinitiv.ema.access.Msg;
import com.refinitiv.ema.access.AckMsg;
import com.refinitiv.ema.access.GenericMsg;
import com.refinitiv.ema.access.RefreshMsg;
import com.refinitiv.ema.access.StatusMsg;
import com.refinitiv.ema.access.UpdateMsg;
import com.refinitiv.ema.access.Data;
import com.refinitiv.ema.access.DataType;
import com.refinitiv.ema.access.DataType.DataTypes;
import com.refinitiv.ema.access.EmaFactory;
import com.refinitiv.ema.access.FieldEntry;
import com.refinitiv.ema.access.FieldList;
import com.refinitiv.ema.access.OmmConsumer;
import com.refinitiv.ema.access.OmmConsumerClient;
import com.refinitiv.ema.access.OmmConsumerEvent;
import com.refinitiv.ema.access.OmmException;
import com.refinitiv.ema.rdm.EmaRdm;
import com.refinitiv.ema.access.ReqMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppClient implements OmmConsumerClient {

	private static final Logger logger = LoggerFactory.getLogger(AppClient.class);

	public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent event) {
		logger.info("Consumer_App.AppClient: Receives Market Price Refresh message");
		logger.info("Item Name: " + (refreshMsg.hasName() ? refreshMsg.name() : "<not set>"));
		logger.info("Service Name: " + (refreshMsg.hasServiceName() ? refreshMsg.serviceName() : "<not set>"));

		logger.info("Item State: " + refreshMsg.state());

		logger.info(String.format("%s",refreshMsg));

		logger.info("\n");
	}

	public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent event) {

		logger.info("Consumer_App.AppClient: Receives Market Price Update message");

		logger.info("Item Name: " + (updateMsg.hasName() ? updateMsg.name() : "<not set>"));
		logger.info("Service Name: " + (updateMsg.hasServiceName() ? updateMsg.serviceName() : "<not set>"));

		logger.info(String.format("%s",updateMsg));

		logger.info("\n");
	}

	public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent event) {
		logger.info("Item Name: " + (statusMsg.hasName() ? statusMsg.name() : "<not set>"));
		logger.info("Service Name: " + (statusMsg.hasServiceName() ? statusMsg.serviceName() : "<not set>"));

		if (statusMsg.hasState())
			logger.info("Item State: " + statusMsg.state());

		logger.info("\n");
	}

	public void onGenericMsg(GenericMsg genericMsg, OmmConsumerEvent consumerEvent) {
	}

	public void onAckMsg(AckMsg ackMsg, OmmConsumerEvent consumerEvent) {
	}

	public void onAllMsg(Msg msg, OmmConsumerEvent consumerEvent) {
	}

}

public class Consumer_App {

	private static final Logger logger = LoggerFactory.getLogger(Consumer_App.class);

	public static void main(String[] args) {
		//BasicConfigurator.configure();
		OmmConsumer consumer = null;
		String service_name = "ELEKTRON_DD";
		try {

			logger.info("Starting Consumer_App application");
			AppClient appClient = new AppClient();

			//consumer = EmaFactory.createOmmConsumer(EmaFactory.createOmmConsumerConfig().host("localhost:14022").username("emajava"));
			
			consumer = EmaFactory.createOmmConsumer(EmaFactory.createOmmConsumerConfig().consumerName("Consumer_1"));

			ReqMsg reqMsg = EmaFactory.createReqMsg();

			logger.info("Consumer_App: Register Login stream");
			consumer.registerClient(reqMsg.domainType(EmaRdm.MMT_LOGIN), appClient);

			logger.info("Consumer_App: Register Directory stream");
			consumer.registerClient(reqMsg.domainType(EmaRdm.MMT_DIRECTORY).serviceName(service_name), appClient);

			logger.info("Consumer_App: Send item request message");
			consumer.registerClient(reqMsg.clear().serviceName(service_name).name("/EUR="), appClient);

			Thread.sleep(60000); // API calls onRefreshMsg(), onUpdateMsg() and onStatusMsg()
		} catch (InterruptedException | OmmException excp) {
			logger.error(excp.getMessage());
		} finally {
			if (consumer != null)
				consumer.uninitialize();
		}
	}
}
