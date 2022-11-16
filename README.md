# Broadcast

    # Primeiro vamos criar o TOPIC
`
gcloud pubsub topics create topic_broadcast
`

    # Logo em seguida vamos criar nossas subscriptions
`
gcloud pubsub subscriptions create broadcast-subscription-one --topic=topic_broadcast
`
`
gcloud pubsub subscriptions create broadcast-subscription-two --topic=topic_broadcast
`

        # Vamos publicar nossa function que vai publicar no TOPIC (Publisher).
`
gcloud functions deploy broadcast-publisher \
--entry-point br.com.silascarneiro.BroadcastExamplePublish \
--runtime java11 \
--memory 256MB \
--trigger-http
`

        # Vamos criar os consumidores dos subscribes
`    
gcloud functions deploy broadcast-subscribe-one \
--entry-point br.com.silascarneiro.BroadcastExampleConsumer \
--trigger-topic topic_broadcast  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

`    
gcloud functions deploy broadcast-subscribe-two \
--entry-point br.com.silascarneiro.BroadcastExampleConsumer \
--trigger-topic topic_broadcast  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

# EventBus

    # Primeiro vamos criar o TOPIC
`
gcloud pubsub topics create topic_eventbus
`

    # Logo em seguida vamos criar nossas subscriptions
`
gcloud pubsub subscriptions create client-subscription --topic=topic_eventbus --message-filter='attributes.sender="client"'
`

`    
gcloud pubsub subscriptions create payment-subscription --topic=topic_eventbus --message-filter='attributes.sender="payment"'
`

        # Vamos publicar nossa function que vai publicar no TOPIC (Publisher)
`
gcloud functions deploy eventbus-publisher \
--entry-point br.com.silascarneiro.EventBusExamplePublisher \
--runtime java11 \
--memory 256MB \
--trigger-http
`

        # Vamos criar os consumidores dos subscribes

`    
gcloud functions deploy eventbus-subscribe-filter-payment \
--entry-point br.com.silascarneiro.EventbusConsumerExample \
--trigger-topic topic_eventbus  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

`    
gcloud functions deploy eventbus-subscribe-filter-client \
--entry-point br.com.silascarneiro.EventbusConsumerExample \
--trigger-topic topic_eventbus  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

# Worker


