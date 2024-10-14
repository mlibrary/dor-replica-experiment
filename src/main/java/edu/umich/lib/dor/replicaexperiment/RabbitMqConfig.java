package edu.umich.lib.dor.replicaexperiment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

import edu.umich.lib.dor.replicaexperiment.exception.BusinessException;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.DepositMessage;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.PurgeMessage;
import edu.umich.lib.dor.replicaexperiment.messaging.messages.UpdateMessage;

@Configuration
public class RabbitMqConfig {

    public class CustomFatalExceptionStrategy
        extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

        @Override
        public boolean isFatal(Throwable t) {
            return (t.getCause() instanceof BusinessException);
        }
    }

    @Bean
    FatalExceptionStrategy customExceptionStrategy() {
        return new CustomFatalExceptionStrategy();
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(customExceptionStrategy());
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        SimpleRabbitListenerContainerFactoryConfigurer configurer
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setErrorHandler(errorHandler());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(4);
        return factory;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("depositMessage", DepositMessage.class);
        idClassMapping.put("updateMessage", UpdateMessage.class);
        idClassMapping.put("purgeMessage", PurgeMessage.class);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public Queue testQueue() {
        return new Queue("testQueue", true);
    }

    @Bean
    public Queue depositQueue() {
        return new Queue("depositQueue", true);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("updateQueue", true);
    }

    @Bean
    public Queue purgeQueue() {
        return new Queue("purgeQueue", true);
    }
}
