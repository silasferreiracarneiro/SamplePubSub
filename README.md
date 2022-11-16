# Broadcast

Neste exemplo, criamos uma arquitetura de microsserviço simplista em que os serviços se comunicam 
por difusão de mensagens. Todos os serviços são editores e assinantes em simultâneo, 
portanto qualquer mensagem publicada por um serviço deve ser recebida por todos os outros serviços, 
mas não deve ser enviada ao editor (o editor não está interessado na sua própria mensagem).

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

Nesse cenário, queremos que todos os nossos consumidores recebam todas as mensagens do editor.

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

Neste cenário, precisamos processar todas as mensagens que chegam de um único produtor o mais rápido possível. 
As mensagens não precisam ser processadas numa sequência estrita, o que significa que podemos acelerar o 
processamento empregando vários threads de trabalho (consumidores).

    # Primeiro vamos criar o TOPIC
`
gcloud pubsub topics create topic_worker
`

    # Logo em seguida vamos criar nosso subscription
`
gcloud pubsub subscriptions create worker-subscription --topic=topic_worker
`

    # Vamos publicar nossa function que vai publicar no TOPIC (Publisher)
`
gcloud functions deploy worker-publisher \
--entry-point br.com.silascarneiro.WorkerExamplePublish \
--runtime java11 \
--memory 256MB \
--trigger-http
`

    # Vamos criar os workers

`    
gcloud functions deploy worker-subscribe-one \
--entry-point br.com.silascarneiro.WorkerConsumerExample \
--trigger-topic topic_worker  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`

`    
gcloud functions deploy worker-subscribe-two \
--entry-point br.com.silascarneiro.WorkerConsumerExample \
--trigger-topic topic_worker  \
--runtime java11 \
--memory 256MB \
--allow-unauthenticated
`