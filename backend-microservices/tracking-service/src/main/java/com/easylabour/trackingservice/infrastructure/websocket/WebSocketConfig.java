package com.easylabour.trackingservice.infrastructure.websocket;

import com.easylabour.trackingservice.infrastructure.kafka.BookingEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final BookingEventConsumer bookingEventConsumer;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-tracking").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/topic/tracking/")) {
                        String bookingId = destination.substring("/topic/tracking/".length());
                        
                        // Check booking active status
                        if (!bookingEventConsumer.isBookingActiveForTracking(bookingId)) {
                            log.warn("WebSocket subscription rejected: booking {} is not in an active tracking state", bookingId);
                            throw new AccessDeniedException("Booking is not eligible for tracking");
                        }

                        // Verify customer ownership and authentication
                        if (accessor.getUser() == null) {
                            log.warn("WebSocket subscription rejected: unauthenticated user");
                            throw new AccessDeniedException("Authentication required");
                        }
                        
                        String customerId = accessor.getUser().getName();
                        if (!bookingEventConsumer.verifyCustomerOwnership(bookingId, customerId)) {
                            log.warn("WebSocket subscription rejected: user {} does not own booking {}", customerId, bookingId);
                            throw new AccessDeniedException("Unauthorized booking access");
                        }
                    }
                }
                return message;
            }
        });
    }
}
