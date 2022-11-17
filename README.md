# Broadcast

Neste exemplo, criamos uma arquitetura de microsserviço simplista em que os serviços se comunicam 
por difusão de mensagens. Todos os serviços são editores e assinantes em simultâneo, 
portanto qualquer mensagem publicada por um serviço deve ser recebida por todos os outros serviços, 
mas não deve ser enviada ao editor (o editor não está interessado na sua própria mensagem).

    # Primeiro vamos criar nosso o TOPIC
`
gcloud pubsub topics create topic_broadcast
`

    # Logo em seguida vamos criar nossas subscriptions
`
gcloud pubsub subscriptions create broadcast_subscription_one --topic=topic_broadcast
`
`
gcloud pubsub subscriptions create broadcast_subscription_two --topic=topic_broadcast
`

    # Vamos publicar nossa function que vai publicar no TOPIC (Publisher).
`
gcloud functions deploy broadcast_publisher \
--entry-point br.com.silascarneiro.BroadcastExamplePublish \
--runtime java11 \
--memory 256MB \
--trigger-http
`

    # Vamos criar os consumidores dos subscribes \n não esqueça de mudar a subscription e o nome do projeto dentro da function
    
`    
gcloud functions deploy broadcast_subscribe_one \
--entry-point br.com.silascarneiro.BroadcastExampleConsumer \
--trigger-topic topic_broadcast  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

`    
gcloud functions deploy broadcast_subscribe_two \
--entry-point br.com.silascarneiro.BroadcastExampleConsumer \
--trigger-topic topic_broadcast  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

# EventBus

Nesse cenário, queremos que todos os nossos consumidores recebam todas as mensagens do editor.

    # Primeiro vamos criar o TOPIC
`
gcloud pubsub topics create topic_eventbus
`

    # Logo em seguida vamos criar nossas subscriptions
`
gcloud pubsub subscriptions create client_subscription --topic=topic_eventbus --message-filter='attributes.sender="client"'
`

`    
gcloud pubsub subscriptions create payment_subscription --topic=topic_eventbus --message-filter='attributes.sender="payment"'
`

    # Vamos publicar nossa function que vai publicar no TOPIC (Publisher)
`
gcloud functions deploy eventbus_publisher \
--entry-point br.com.silascarneiro.EventBusExamplePublisher \
--runtime java11 \
--memory 256MB \
--trigger-http
`

    # Vamos criar os consumidores dos subscribes \n não esqueça de mudar a subscription e o nome do projeto dentro da function

`    
gcloud functions deploy eventbus_subscribe_filter_payment \
--entry-point br.com.silascarneiro.EventbusConsumerExample \
--trigger-topic topic_eventbus  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

`    
gcloud functions deploy eventbus_subscribe_filter_client \
--entry-point br.com.silascarneiro.EventbusConsumerExample \
--trigger-topic topic_eventbus  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

# Worker

Neste cenário, precisamos processar todas as mensagens que chegam de um único produtor o mais rápido possível. 
As mensagens não precisam ser processadas numa sequência estrita, o que significa que podemos acelerar o 
processamento empregando vários threads de trabalho (consumidores).

    # Primeiro vamos criar o TOPIC
`
gcloud pubsub topics create topic_worker
`

    # Logo em seguida vamos criar nosso subscription
`
gcloud pubsub subscriptions create worker_subscription --topic=topic_worker
`

    # Vamos publicar nossa function que vai publicar no TOPIC (Publisher)
`
gcloud functions deploy worker_publisher \
--entry-point br.com.silascarneiro.WorkerExamplePublish \
--runtime java11 \
--memory 256MB \
--trigger-http
`

    # Vamos criar os workers

`    
gcloud functions deploy worker_subscribe_one \
--entry-point br.com.silascarneiro.WorkerConsumerExample \
--trigger-topic topic_worker  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

`    
gcloud functions deploy worker_subscribe_two \
--entry-point br.com.silascarneiro.WorkerConsumerExample \
--trigger-topic topic_worker  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`