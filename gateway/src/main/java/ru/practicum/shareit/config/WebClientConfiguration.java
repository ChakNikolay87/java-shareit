package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfiguration {
    @Value("${shareit-server.url}")
    String serverUrl;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        return new BookingClient(getRestTemplate("/bookings", builder));
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        return new ItemClient(getRestTemplate("/items", builder));
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder builder) {
        return new ItemRequestClient(getRestTemplate("/requests", builder));
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        return new UserClient(getRestTemplate("/users", builder));
    }

    private RestTemplate getRestTemplate(String API_PREFIX, RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }
}