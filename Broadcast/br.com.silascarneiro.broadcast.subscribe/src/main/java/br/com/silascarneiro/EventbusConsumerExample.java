package br.com.silascarneiro;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class EventbusConsumerExample implements BackgroundFunction<PubSubMessage> {

  private static final Logger logger = Logger.getLogger(EventbusConsumerExample.class.getName());
  private static final String PROJECT_ID = "aplicacao-gke";
  private static final String SUBSCRIPTION_ID = "br.com.silascarneiro.exemplo.broadcast";

  public static void main(String... args) throws Exception {
    createPullSubscriptionExample();
  }

  @Override
  public void accept(PubSubMessage message, Context context) throws Exception {
    createPullSubscriptionExample();
  }

  public static void createPullSubscriptionExample() {
    ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID);

    MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
      logger.info("Data Subscription: " + message.getData().toStringUtf8());
      System.out.println("Id: " + message.getMessageId());
      System.out.println("Data: " + message.getData().toStringUtf8());
      consumer.ack();
    };

    Subscriber subscriber = null;
    try {
      subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
      subscriber.startAsync().awaitRunning();
      System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
      subscriber.awaitTerminated(30, TimeUnit.SECONDS);
    } catch (TimeoutException timeoutException) {
      subscriber.stopAsync();
    }
  }
}
