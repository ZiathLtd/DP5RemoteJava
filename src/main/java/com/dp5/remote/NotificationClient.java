package com.dp5.remote;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonObject;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import jakarta.websocket.ClientEndpoint;

@ClientEndpoint
public class NotificationClient {
	private StompSession stompSession;
	private StompHeaders header = new StompHeaders();
	private final static String[] NOTIFICATION_TYPES = { "DEVICE_LEGACY", "DEVICE_CONNECTED", "DEVICE_DISCONNECTED",
			"LINEAR_CONNECTED", "LINEAR_DISCONNECTED", "LINEAR_NEW_BARCODE", "LINEAR_PLUGGED_IN", "LINEAR_UNPLUGGED",
			"SCAN_MILESTONE", "ACTIVATOR_EVENT" };

	public NotificationClient(String notificationUrl) {
		try {

			ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
			taskScheduler.initialize();
			WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
			stompClient.setMessageConverter(new StringMessageConverter());
			stompClient.setTaskScheduler(taskScheduler); 
			this.header.setDestination("/topic/events");
			this.stompSession = stompClient.connect(notificationUrl, new MyStompSessionHandler()).get();
			this.stompSession.setAutoReceipt(true);

			this.stompSession.subscribe(this.header, new StompFrameHandler() {
				@Override
				public Type getPayloadType(StompHeaders headers) {
					return String.class;
				}

				@Override
				public void handleFrame(StompHeaders headers, Object payload) {
					try {
						JsonObject jsonObject = Json.createReader(new StringReader(payload.toString())).readObject();
						if (jsonObject.containsKey("notificationType")) {
							String notificationType = jsonObject.getString("notificationType");
							if (Arrays.asList(NOTIFICATION_TYPES).contains(notificationType)) {
								System.out.println("Notification Recieved:");
								System.out.println(headers);
								System.out.println(payload);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private class MyStompSessionHandler extends StompSessionHandlerAdapter {

		public MyStompSessionHandler() {
		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			System.out.println("connection open:" + session.getSessionId());

		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			System.out.println("Handle Frame: ");
			System.out.println(Integer.parseInt(headers.get("code").get(0)) + headers.get("message").get(0));
		}

		@Override
		public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
				Throwable exception) {
			System.out.println("Handle Exception");
			System.out.println("connection exception: " + session.getSessionId() + " Command: " + command);
			System.err.println(exception.getMessage());
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			System.out.println("connection error : " + session.getSessionId());
			System.err.println(ex.getMessage());
		}
	}
}
