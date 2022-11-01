package br.com.silascarneiro;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EventBusExamplePublisher implements HttpFunction {

  private static String PROJECT_ID = "aplicacao-gke";
  private static String TOPIC_ID = "br.com.silascarneiro.exemplo.eventbus";

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    Gson gson = new Gson();
    JsonObject object = gson.fromJson(request.getReader(), JsonObject.class);
    BufferedWriter writer = response.getWriter();
    publishWithErrorHandlerExample(object.toString(), writer);
  }

  public static void publishWithErrorHandlerExample(String message, BufferedWriter writer)
      throws Exception {
    TopicName topicName = TopicName.of(PROJECT_ID, TOPIC_ID);
    Publisher publisher = null;

    try {
      publisher = Publisher.newBuilder(topicName).build();

      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      ApiFuture<String> future = publisher.publish(pubsubMessage);

      ApiFutures.addCallback(
          future,
          new ApiFutureCallback<String>() {

            @Override
            public void onFailure(Throwable throwable) {
              try {
                if (throwable instanceof ApiException) {
                  ApiException apiException = ((ApiException) throwable);
                  System.out.println(apiException.getStatusCode().getCode());
                  System.out.println(apiException.isRetryable());
                }
                System.out.println("Error publishing message : " + message);
                writer.write("Error publishing message : " + message);
              } catch (IOException e) {
                e.printStackTrace();
              }
            }

            @Override
            public void onSuccess(String messageId) {
              try {
                System.out.println("Published message ID: " + messageId);
                writer.write("Published message ID: " + messageId);
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          },
          MoreExecutors.directExecutor());
    } finally {
      if (publisher != null) {
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
      }
    }
  }
}
