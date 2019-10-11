///*|-----------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  --
// *|           Copyright Refinitiv 2019. All rights reserved.                  --
///*|-----------------------------------------------------------------------------

package com.refinitiv.ema.consumer;

import com.thomsonreuters.ema.access.Msg;
import com.thomsonreuters.ema.access.AckMsg;
import com.thomsonreuters.ema.access.GenericMsg;
import com.thomsonreuters.ema.access.RefreshMsg;
import com.thomsonreuters.ema.access.StatusMsg;
import com.thomsonreuters.ema.access.UpdateMsg;
import com.thomsonreuters.ema.access.Data;
import com.thomsonreuters.ema.access.DataType;
import com.thomsonreuters.ema.access.DataType.DataTypes;
import com.thomsonreuters.ema.access.EmaFactory;
import com.thomsonreuters.ema.access.FieldEntry;
import com.thomsonreuters.ema.access.FieldList;
import com.thomsonreuters.ema.access.OmmConsumer;
import com.thomsonreuters.ema.access.OmmConsumerClient;
import com.thomsonreuters.ema.access.OmmConsumerEvent;
import com.thomsonreuters.ema.access.OmmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppClient implements OmmConsumerClient {

	private static final Logger logger = LoggerFactory.getLogger(AppClient.class);

	public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent event) {
		logger.info("Consumer_App.AppClient: Receives Market Price Refresh message");
		logger.info("Item Name: " + (refreshMsg.hasName() ? refreshMsg.name() : "<not set>"));
		logger.info("Service Name: " + (refreshMsg.hasServiceName() ? refreshMsg.serviceName() : "<not set>"));

		logger.info("Item State: " + refreshMsg.state());

		

		if (DataType.DataTypes.FIELD_LIST == refreshMsg.payload().dataType())
			decode(refreshMsg.payload().fieldList());

		logger.info("\n");
	}

	public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent event) {

		logger.info("Consumer_App.AppClient: Receives Market Price Update message");

		logger.info("Item Name: " + (updateMsg.hasName() ? updateMsg.name() : "<not set>"));
		logger.info("Service Name: " + (updateMsg.hasServiceName() ? updateMsg.serviceName() : "<not set>"));

		if (DataType.DataTypes.FIELD_LIST == updateMsg.payload().dataType())
			decode(updateMsg.payload().fieldList());

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

	void decode(FieldList fieldList) {
		for (FieldEntry fieldEntry : fieldList) {
			//logger.info("Fid: " + fieldEntry.fieldId() + " Name = " + fieldEntry.name() + " DataType: "+ DataType.asString(fieldEntry.load().dataType()) + " Value: ");
			
			String msg = "Fid: " + fieldEntry.fieldId() + " Name = " + fieldEntry.name() + " DataType: "
					+ DataType.asString(fieldEntry.load().dataType()) + " Value: ";

			if (Data.DataCode.BLANK == fieldEntry.code())
				logger.info(String.format("%s%s",msg," blank"));
			else
				switch (fieldEntry.loadType()) {
				case DataTypes.REAL:
					logger.info(String.format("%s%s",msg,fieldEntry.real().asDouble()));
					break;
				case DataTypes.DATE:
					logger.info(String.format("%s%s",msg,fieldEntry.date().day() + " / " + fieldEntry.date().month() + " / "
							+ fieldEntry.date().year()));
					break;
				case DataTypes.TIME:
					logger.info(String.format("%s%s",msg,fieldEntry.time().hour() + ":" + fieldEntry.time().minute() + ":"
							+ fieldEntry.time().second() + ":" + fieldEntry.time().millisecond()));
					break;
				case DataTypes.DATETIME:
					logger.info(String.format("%s%s",msg,fieldEntry.dateTime().day() + " / " + fieldEntry.dateTime().month() + " / "
							+ fieldEntry.dateTime().year() + "." + fieldEntry.dateTime().hour() + ":"
							+ fieldEntry.dateTime().minute() + ":" + fieldEntry.dateTime().second() + ":"
							+ fieldEntry.dateTime().millisecond() + ":" + fieldEntry.dateTime().microsecond() + ":"
							+ fieldEntry.dateTime().nanosecond()));
					break;
				case DataTypes.INT:
					logger.info(String.format("%s%s",msg,fieldEntry.intValue()));
					break;
				case DataTypes.UINT:
					logger.info(String.format("%s%s",msg,fieldEntry.uintValue()));
					break;
				case DataTypes.ASCII:
					logger.info(String.format("%s%s",msg,fieldEntry.ascii()));
					break;
				case DataTypes.ENUM:
					logger.info(String.format("%s%s",msg,fieldEntry.hasEnumDisplay() ? fieldEntry.enumDisplay() : fieldEntry.enumValue()));
					break;
				case DataTypes.RMTES:
					logger.info(String.format("%s%s",msg,fieldEntry.rmtes()));
					break;
				case DataTypes.ERROR:
					logger.error(String.format("%s%s",msg,"(" + fieldEntry.error().errorCodeAsString() + ")"));
					break;
				default:
					//logger.info("\n");
					break;
				}
		}
	}
}

public class Consumer_App {

	private static final Logger logger = LoggerFactory.getLogger(Consumer_App.class);

	public static void main(String[] args) {
		OmmConsumer consumer = null;
		try {

			logger.info("Starting Consumer_App application");
			AppClient appClient = new AppClient();

			consumer = EmaFactory.createOmmConsumer(
					EmaFactory.createOmmConsumerConfig().host("localhost:14022").username("emajava"));

			logger.info("Consumer_App: Send item request message");
			consumer.registerClient(EmaFactory.createReqMsg().serviceName("DIRECT_FEED").name("/EUR="), appClient);

			Thread.sleep(60000); // API calls onRefreshMsg(), onUpdateMsg() and onStatusMsg()
		} catch (InterruptedException | OmmException excp) {
			logger.error(excp.getMessage());
		} finally {
			if (consumer != null)
				consumer.uninitialize();
		}
	}
}
