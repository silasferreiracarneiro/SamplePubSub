# Broadcast

    Criando as subscriptions

    gcloud pubsub subscriptions create cliente-subscription \ 
    --topic=topic-1 \ 
    --message-filter='attributes.sender!="client"'

    gcloud pubsub subscriptions create payment-subscription \ 
    --topic=topic-1 \ 
    --message-filter='attributes.sender!="payment"'


    # Publicando as publisher

    gcloud functions deploy eventbus-publisher \
        --entry-point br.com.silascarneiro.EventBusExamplePublisher \
        --runtime java11 \
        --memory 256MB \
        --trigger-http

    #Publicando o consumer

    gcloud functions deploy eventbus-subscribe-filter-one \
        --entry-point br.com.silascarneiro.EventbusConsumerExample \                         
        --runtime java11 \
        --memory 256MB \
        --trigger-topic br.com.silascarneiro.exemplo.eventbus  \
        --allow-unauthenticated

# EventBus

# Worker


